package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.DidaUserTask;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
public interface IDidaUserTaskService extends IService<DidaUserTask> {

    //插入用户——任务
    void insertUserTask(DidaUserTask userTask);
}
