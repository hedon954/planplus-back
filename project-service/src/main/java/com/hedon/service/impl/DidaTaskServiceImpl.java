package com.hedon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.service.IDidaTaskService;
import common.code.ResultCode;
import common.entity.DidaTask;
import common.entity.DidaUserTask;
import common.exception.ServiceException;
import common.mapper.DidaTaskMapper;
import common.mapper.DidaUserTaskMapper;
import common.vo.request.DidaTaskRequestVo;
import common.vo.response.DidaTaskResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
@Service
public class DidaTaskServiceImpl extends ServiceImpl<DidaTaskMapper, DidaTask> implements IDidaTaskService {

    @Autowired
    DidaTaskMapper didaTaskMapper;

    @Autowired
    DidaUserTaskMapper didaUserTaskMapper;

    /**
     * 创建新任务
     *
     * @author yang jie
     * @create 2020-10-25 23:50
     * @param userId
     * @param taskInfo
     */
    @Override
    @Transactional()
    public void createTask(Integer userId, DidaTaskRequestVo taskInfo) {

        //修改任务表
        DidaTask didaTask = DidaTaskRequestVo.toDidaTask(taskInfo);
        didaTaskMapper.insert(didaTask);

        //获取新建任务的taskId
        Integer taskId = didaTask.getTaskId();
        //修改用户任务表
        DidaUserTask didaUserTask = new DidaUserTask();
        didaUserTask.setDidaTaskId(taskId);
        didaUserTask.setDidaUserId(userId);
        didaUserTaskMapper.insert(didaUserTask);
    }

    /**
     * 按日期查询待办任务 多表查询
     *
     * @author yang jie
     * @create 2020-10-26 22:45
     * @param userId
     * @param date
     * @return
     */
    @Override
    public ArrayList<DidaTaskResponseVo> getTasksByDate(Integer userId, LocalDate date) {
        ArrayList<DidaTask> didaTasks = didaTaskMapper.selectByDate(userId, date.toString() + "%");
        return tasksToVos(didaTasks);
    }


    /**
     * 开始任务，修改任务状态
     *
     * @author yang jie
     * @create 2020-10-29 11:30
     * @param taskId
     * @param userId
     */
    @Override
    public void startTask(Integer taskId, Integer userId) {

        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        DidaTask task = didaTaskMapper.selectById(taskId);

        //判断任务是否存在
        if(task == null) {
            throw new ServiceException(ResultCode.TASK_NOT_EXIST);
        }

        task.setTaskStatus(1);
        didaTaskMapper.updateById(task);
    }


    /**
     * 推迟任务，修改任务开始时间、预计结束时间
     *
     * @author yang jie
     * @create 2020-10-29 16:50
     * @param taskId
     * @param userId
     * @param delayTime
     */
    @Override
    public void delayTask(Integer taskId, Integer userId, Integer delayTime) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        //获取任务的原开始时间、原预计结束时间
        DidaTask task = didaTaskMapper.selectById(taskId);

        //判断任务是否存在
        if(task == null) {
            throw new ServiceException(ResultCode.TASK_NOT_EXIST);
        }

        LocalDateTime startTime = task.getTaskStartTime();
        LocalDateTime predictedFinishTime = task.getTaskPredictedFinishTime();

        //修改任务开始时间、原预计结束时间
        startTime = startTime.plusMinutes(delayTime);
        predictedFinishTime = predictedFinishTime.plusMinutes(delayTime);

        //更新数据库
        task.setTaskStartTime(startTime);
        task.setTaskPredictedFinishTime(predictedFinishTime);
        didaTaskMapper.updateById(task);
    }


    /**
     * 结束任务，改任务状态，写任务实际结束时间、花费时间
     *
     * @author yang jie
     * @create 2020-10-29 15:35
     * @param taskId
     * @param userId
     */
    @Override
    public void finishTask(Integer taskId, Integer userId) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        //获取任务开始时间和当前时间
        DidaTask didaTask = didaTaskMapper.selectById(taskId);

        //判断任务是否存在
        if(didaTask == null) {
            throw new ServiceException(ResultCode.TASK_NOT_EXIST);
        }

        LocalDateTime startTime = didaTask.getTaskStartTime();
        LocalDateTime finishTime = LocalDateTime.now();

        //计算花费时间
        Duration duration = Duration.between(startTime, finishTime);

        int day = (int) duration.toDays();
        int hour = (int) duration.toHours() % (day == 0? 1: 24);
        int minutes = (int) duration.toMinutes() % (hour == 0? 1: 60);
        String consumedTime = (day==0? "": day + "D ") + (hour==0? "": hour + "h ") + minutes + "m";

        //更新数据库
        didaTask.setTaskRealFinishTime(finishTime);
        didaTask.setTaskConsumedTime(consumedTime);
        didaTask.setTaskStatus(2);
        didaTaskMapper.updateById(didaTask);
    }


    /**
     * 修改任务内容
     *
     * @author yang jie
     * @create 2020-10-29 19:35
     * @param taskId
     * @param userId
     * @param taskInfo
     */
    @Override
    public void modifyTask(Integer taskId, Integer userId, DidaTaskRequestVo taskInfo) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        DidaTask task = didaTaskMapper.selectById(taskId);

        //判断任务是否存在
        if(task == null) {
            throw new ServiceException(ResultCode.TASK_NOT_EXIST);
        }

        task.setTaskContent(taskInfo.getTaskContent());
        task.setTaskPlace(taskInfo.getTaskPlace());
        task.setTaskRate(taskInfo.getTaskRate());
        task.setTaskStartTime(taskInfo.getTaskStartTime());
        task.setTaskPredictedFinishTime(taskInfo.getTaskPredictedFinishTime());
        task.setTaskAdvanceRemindTime(taskInfo.getTaskAdvanceRemindTime());

        //更新数据库
        didaTaskMapper.updateById(task);
    }


    /**
     * 按状态查询任务 多表查询
     *
     * @author yang jie
     * @create 2020-10-29 20:20
     * @param userId
     * @param taskStatus
     */
    @Override
    public ArrayList<DidaTaskResponseVo> getTasksByStatus(Integer userId, Integer taskStatus) {
        ArrayList<DidaTask> didaTasks = didaTaskMapper.selectByStatus(userId, taskStatus);
        return tasksToVos(didaTasks);
    }


    /**
     * 查询所有任务
     *
     * @author yang jie
     * @create 2020-10-29 20:50
     * @param userId
     */
    @Override
    public ArrayList<DidaTaskResponseVo> getAllTasks(Integer userId) {
        ArrayList<DidaTask> didaTasks = didaTaskMapper.selectAll(userId);
        return tasksToVos(didaTasks);
    }


    /**
     * 删除任务
     *
     * @author yang jie
     * @create 2020-10-29 21:10
     * @param taskId
     * @param userId
     */
    @Override
    @Transactional()
    public void deleteTask(Integer taskId, Integer userId) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        //修改任务表
        didaTaskMapper.deleteById(taskId);

        //修改用户任务表
        QueryWrapper<DidaUserTask> queryWrapper = new QueryWrapper();
        queryWrapper.eq("dida_task_id", taskId);
        didaUserTaskMapper.delete(queryWrapper);
    }


    /**
     * 判断用户和人物之间是否存在对应关系
     *
     * @author yang jie
     * @create 2020-10-29 16:30
     * @param taskId
     * @param userId
     * @return
     */
    private void judgeUserTaskMatch(Integer taskId, Integer userId) {
        QueryWrapper<DidaUserTask> queryWrapper = new QueryWrapper<>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("dida_user_id", userId);
        map.put("dida_task_id", taskId);
        queryWrapper.allEq(map);

        /**
         * 如果查询结果为空，
         * 说明用户任务表中不存在该条数据，即任务和用户不匹配，或者任务不存在
         * 此时抛出异常
         */
        if(didaUserTaskMapper.selectList(queryWrapper) == null || didaUserTaskMapper.selectList(queryWrapper).size() == 0) {
            throw new ServiceException(ResultCode.USER_TASK_MISMATCHING);
        }
    }


    /**
     * 抽取出的公共方法，将查询到的任务列表转换成responseVo列表
     *
     * @author yang jie
     * @create 2020-10-29 20:35
     * @param didaTasks
     * @return
     */
    private ArrayList<DidaTaskResponseVo> tasksToVos(ArrayList<DidaTask> didaTasks) {
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = new ArrayList<>();
        for (DidaTask didaTask : didaTasks) {
            //将任务信息中需要的字段重新封装
            didaTaskResponseVos.add(new DidaTaskResponseVo(didaTask));
        }
        return didaTaskResponseVos;
    }

}
