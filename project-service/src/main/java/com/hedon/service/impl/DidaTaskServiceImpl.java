package com.hedon.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.feign.NotificationFeignService;
import com.hedon.service.IDidaTaskService;
import common.code.ResultCode;
import common.dto.TaskNotificationDto;
import common.entity.DidaTask;
import common.entity.DidaUser;
import common.entity.DidaUserTask;
import common.exception.ServiceException;
import common.mapper.DidaTaskMapper;
import common.mapper.DidaUserMapper;
import common.mapper.DidaUserTaskMapper;
import common.vo.common.ResponseBean;
import common.vo.request.DidaTaskRequestVo;
import common.vo.response.DidaTaskResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
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

    @Autowired
    NotificationFeignService notificationFeignService;

    @Autowired
    DidaUserMapper didaUserMapper;

    /**
     * 创建新任务
     *
     * @author yang jie
     * @create 2020-10-25 23:50
     * @param userId
     * @param taskInfo
     */
    @Override
    @Transactional
    public Integer createTask(Integer userId, DidaTaskRequestVo taskInfo) {

        //修改任务表
        DidaTask didaTask = DidaTaskRequestVo.toDidaTask(taskInfo);
        didaTaskMapper.insert(didaTask);

        //获取新建任务的taskId
        Integer taskId = didaTask.getTaskId();

        //发送通知
        ResponseBean responseBean = sendNotification(userId,didaTask,taskInfo.getTaskFormId());
        if (responseBean.getCode() != 1000L){
            //如果发送消息不成功，那就要回滚 => 这里先手动回滚删除前面插入的数据
            didaTaskMapper.deleteById(taskId);
            throw new ServiceException(ResultCode.TIMED_TASK_CREATE_FAILED);
        }

        //修改用户任务表
        DidaUserTask didaUserTask = new DidaUserTask();
        didaUserTask.setDidaTaskId(taskId);
        didaUserTask.setDidaUserId(userId);
        didaUserTaskMapper.insert(didaUserTask);

        return taskId;
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
    public void delayTask(Integer taskId, Integer userId, Integer delayTime,String formId) {
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

        //发送通知
        ResponseBean responseBean = sendNotification(userId, task, formId);
        if (responseBean.getCode() != 1000L){
            //如果发送消息不成功，抛出异常
            throw new ServiceException(ResultCode.TASK_DELAY_FAILED);
        }

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

        //检查是否修改了任务的开始时间和提前提醒时间
        Long oldTaskStartTime = task.getTaskStartTime().toEpochSecond(ZoneOffset.UTC);
        Long newTaskStartTime = taskInfo.getTaskStartTime().toEpochSecond(ZoneOffset.UTC);
        Integer oldTaskAdvanceRemindTime = task.getTaskAdvanceRemindTime();
        Integer newTaskAdvanceRemindTime = taskInfo.getTaskAdvanceRemindTime();
        Boolean needNotify = false;
        if (!oldTaskStartTime.equals(newTaskStartTime) ||
            !oldTaskAdvanceRemindTime.equals(newTaskAdvanceRemindTime)){
            needNotify = true;
        }

        task.setTaskContent(taskInfo.getTaskContent());
        task.setTaskPlace(taskInfo.getTaskPlace());
        task.setTaskRate(taskInfo.getTaskRate());
        task.setTaskStartTime(taskInfo.getTaskStartTime());
        task.setTaskPredictedFinishTime(taskInfo.getTaskPredictedFinishTime());
        task.setTaskAdvanceRemindTime(taskInfo.getTaskAdvanceRemindTime());

        //发送通知
        if (needNotify){
            ResponseBean responseBean = sendNotification(userId, task, taskInfo.getTaskFormId());
            if (responseBean.getCode() != 1000L){
                //如果发送消息不成功，抛出异常
                throw new ServiceException(ResultCode.TASK_DELAY_FAILED);
            }
        }


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
     * 查询单个任务
     *
     * @author yang jie
     * @create 2020-11-05 11:50
     * @param taskId
     * @param userId
     */
    @Override
    public DidaTaskResponseVo getTaskById(Integer taskId, Integer userId) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        DidaTaskResponseVo didaTaskResponseVo = new DidaTaskResponseVo(didaTaskMapper.selectById(taskId));

        return didaTaskResponseVo;
    }


    /**
     * 将任务保存至草稿箱
     *
     * @author yang jie
     * @create 2020-11-06 23:50
     * @param taskId
     * @param userId
     */
    @Override
    public void draftTask(Integer taskId, Integer userId) {
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

        task.setTaskStatus(3);
        didaTaskMapper.updateById(task);
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

    /**
     * 调用通知模块发送通知
     *
     * @author Jiahan Wang
     * @create Jiahan Wang
     * @param userId    用户ID
     * @param didaTask  任务体
     * @param formId    表单ID
     * @return
     */
    public ResponseBean sendNotification(Integer userId, DidaTask didaTask,String formId){
        DidaUser didaUser = didaUserMapper.selectById(userId);
        TaskNotificationDto dto = new TaskNotificationDto();
        //设置任务ID
        dto.setTaskId(didaTask.getTaskId());
        //设置延迟时间
        long nowEpochSecond = LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond();
        long startEpochSecond = didaTask.getTaskStartTime().toInstant(ZoneOffset.UTC).getEpochSecond();
        Long expiration = startEpochSecond - nowEpochSecond;
        Integer taskAdvanceRemindTime = didaTask.getTaskAdvanceRemindTime();
        Long advance = 60L * taskAdvanceRemindTime;
        //检查设置的提前提醒时间是否合理
        if (expiration > (advance + 10L)){
            expiration -= (advance + 10);
        }else if (expiration >= 0){
            //比如设置15分钟，但是现在离开始任务只有10分钟，那就立即通知
            expiration = 1L;
        }else{
            //如果当前时间早于任务开始时间，那么发送通知失败
            expiration = -1L;
        }
        dto.setExpiration(expiration);
        dto.setSceneId(formId);
        dto.setTouserOpenId(didaUser.getUserOpenId());
        dto.setPage("/pages/modification/modification?taskId="+didaTask.getTaskId());

        System.out.println("project-service dto : " + dto);
        return notificationFeignService.sendNotificationMsg(dto);
    }

}
