package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.DidaTask;

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
     * @create 2020-20-25 23:50
     * @param didaTask
     */
    void insertTask(DidaTask didaTask);
}
