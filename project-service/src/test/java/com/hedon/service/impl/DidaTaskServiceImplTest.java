package com.hedon.service.impl;

import com.hedon.ProjectApplication;
import common.entity.DidaTask;
import common.entity.DidaUserTask;
import common.vo.request.DidaTaskRequestVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

/**
 * @author yang jie
 * @create 2020-10-26 00:00
 */
@SpringBootTest(classes = {ProjectApplication.class})
public class DidaTaskServiceImplTest {

    @Autowired
    DidaTaskServiceImpl didaTaskService;

    @Autowired
    DidaUserTaskServiceImpl didaUserTaskService;

    /**
     * 测试insertTask
     *
     * @author yang jie
     * @create 2020.20.26 00:00
     */
    @Test
    public void testInsertTask() {
        DidaTaskRequestVo requestVo = new DidaTaskRequestVo();
        requestVo.setTaskContent("吃饭");
        requestVo.setTaskPlace("银泰");
        requestVo.setTaskRate(0);
        requestVo.setTaskStartTime(LocalDateTime.now());
        requestVo.setTaskPredictedFinishTime(LocalDateTime.now());
        DidaTask didaTask = DidaTaskRequestVo.toDidaTask(requestVo);
        didaTaskService.insertTask(didaTask);
        Integer taskId = didaTask.getTaskId();
        System.out.println("============================");
        System.out.println(taskId);
        System.out.println("============================");

        DidaUserTask didaUserTask = new DidaUserTask();
        didaUserTask.setDidaTaskId(taskId);
        didaUserTask.setDidaUserId(1);
        didaUserTaskService.insertUserTask(didaUserTask);
    }
}
