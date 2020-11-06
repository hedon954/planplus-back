package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.DidaTask;
import common.vo.request.DidaTaskRequestVo;
import common.vo.response.DidaTaskResponseVo;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
public interface IDidaTaskService extends IService<DidaTask> {

    /**
     * 创建新任务
     *
     * @author yang jie
     * @create 2020-10-25 23:50
     * @param userId
     * @param taskInfo
     */
    Integer createTask(Integer userId, DidaTaskRequestVo taskInfo);

    /**
     * 按日期查询待办任务
     *
     * @author yang jie
     * @create 2020-10-26 22:45
     * @param userId
     * @param date
     * @return
     */
    ArrayList<DidaTaskResponseVo> getTasksByDate(Integer userId, LocalDate date);

    /**
     * 开始任务，修改任务状态
     *
     * @author yang jie
     * @create 2020-10-29 11:30
     * @param taskId
     * @param userId
     */
    void startTask(Integer taskId, Integer userId);

    /**
     * 推迟任务，修改任务开始时间、预计结束时间
     *
     * @author yang jie
     * @create 2020-10-29 16:50
     * @param taskId
     * @param userId
     * @param delayTime
     */
    void delayTask(Integer taskId, Integer userId, Integer delayTime);

    /**
     * 结束任务，改任务状态，写任务实际结束时间、花费时间
     *
     * @author yang jie
     * @create 2020-10-29 15:35
     * @param taskId
     * @param userId
     */
    void finishTask(Integer taskId, Integer userId);

    /**
     * 修改任务内容
     *
     * @author yang jie
     * @create 2020-10-29 19:35
     * @param taskId
     * @param userId
     * @param taskInfo
     */
    void modifyTask(Integer taskId, Integer userId, DidaTaskRequestVo taskInfo);

    /**
     * 按状态查询任务
     *
     * @author yang jie
     * @create 2020-10-29 20:20
     * @param userId
     * @param taskStatus
     */
    ArrayList<DidaTaskResponseVo> getTasksByStatus(Integer userId, Integer taskStatus);

    /**
     * 查询所有任务
     *
     * @author yang jie
     * @create 2020-10-29 20:50
     * @param userId
     */
    ArrayList<DidaTaskResponseVo> getAllTasks(Integer userId);

    /**
     * 删除任务
     *
     * @author yang jie
     * @create 2020-10-29 21:10
     * @param taskId
     * @param userId
     */
    void deleteTask(Integer taskId, Integer userId);

    /**
     * 查询单个任务
     *
     * @author yang jie
     * @create 2020-11-05 11:50
     * @param taskId
     * @param userId
     */
    DidaTaskResponseVo getTaskById(Integer taskId, Integer userId);
}
