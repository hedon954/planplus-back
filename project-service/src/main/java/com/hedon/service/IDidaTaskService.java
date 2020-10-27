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
     * @param didaTask
     */
    void createTask(Integer userId, DidaTaskRequestVo taskInfo);

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
}
