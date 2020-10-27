package com.hedon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.service.IDidaTaskService;
import common.entity.DidaTask;
import common.entity.DidaUserTask;
import common.mapper.DidaTaskMapper;
import common.mapper.DidaUserTaskMapper;
import common.vo.request.DidaTaskRequestVo;
import common.vo.response.DidaTaskResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

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
        didaTaskMapper.insertTask(didaTask);

        //获取新建任务的taskId
        Integer taskId = didaTask.getTaskId();
        //修改用户任务表
        DidaUserTask didaUserTask = new DidaUserTask();
        didaUserTask.setDidaTaskId(taskId);
        didaUserTask.setDidaUserId(userId);
        didaUserTaskMapper.insert(didaUserTask);
    }

    /**
     * 按日期查询待办任务
     *
     * @author yang jie
     * @create 2020-10-26 22:45
     * @param userId
     * @param date
     * @return
     */
    @Override
    public ArrayList<DidaTaskResponseVo> getTasksByDate(Integer userId, LocalDate date) {
        //按日期查询待办任务
        ArrayList<DidaTask> didaTasks = didaTaskMapper.selectByDate(userId, date.toString() + "%");
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = new ArrayList<>();
        for (DidaTask didaTask : didaTasks) {
            //将任务信息中需要的字段重新封装
            didaTaskResponseVos.add(new DidaTaskResponseVo(didaTask));
        }
        return didaTaskResponseVos;
    }
}
