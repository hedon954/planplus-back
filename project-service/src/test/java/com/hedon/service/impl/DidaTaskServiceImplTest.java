package com.hedon.service.impl;

import com.hedon.ProjectApplication;
import common.vo.request.DidaTaskRequestVo;
import common.vo.response.DidaTaskResponseVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
     * 测试createTask
     *
     * @author yang jie
     * @create 2020.20.26 00:00
     */
    @Test
    public void testCreateTask() {
        DidaTaskRequestVo requestVo = new DidaTaskRequestVo();
        requestVo.setTaskContent("胡吃海喝");
        requestVo.setTaskPlace("二食堂");
        requestVo.setTaskRate(0);
        requestVo.setTaskStartTime(LocalDateTime.now().minusHours(3).plusDays(1));
        requestVo.setTaskPredictedFinishTime(LocalDateTime.now().plusHours(1).plusDays(2));
        Integer taskId = didaTaskService.createTask(1, requestVo);
        System.out.println("==============");
        System.out.println(taskId);
        System.out.println("===============");
    }

    /**
     * 测试getTasksByDate
     *
     * @author yang jie
     * @create 2020-10-26 23:10
     */
    @Test
    public void testSelectTasksByDate() {
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = didaTaskService.getTasksByDate(2, LocalDate.now().minusDays(1).plusDays(1));
        System.out.println("==================================");
        for (DidaTaskResponseVo taskResponseVo : didaTaskResponseVos) {
            System.out.println(taskResponseVo);
        }
        System.out.println("==================================");
    }

    /**
     * 测试startTask
     *
     * @author yang jie
     * @create 2020-10-29 12:10
     */
    @Test
    public void testStartTask() {
        didaTaskService.startTask(17, 1);
    }

    /**
     * 测试delayTask
     *
     * @author yang jie
     * @create 2020-10-29 17:15
     */
    @Test
    public void testDelayTask() {
        didaTaskService.delayTask(17, 2, 30);
    }

    /**
     * 测试finishTask
     *
     * @author yang jie
     * @create 2020-10-29 18:40
     */
    @Test
    public void testFinishTask() {
        didaTaskService.finishTask(17, 1);
    }

    /**
     * 测试modifyTask
     *
     * @author yang jie
     * @create 2020-10-29 19:40
     */
    @Test
    public void testModifyTask() {
        DidaTaskRequestVo requestVo = new DidaTaskRequestVo();
        requestVo.setTaskContent("给长城贴瓷砖");
        requestVo.setTaskPlace("山海关");
        requestVo.setTaskRate(0);
        requestVo.setTaskStartTime(LocalDateTime.now());
        requestVo.setTaskPredictedFinishTime(LocalDateTime.now().plusHours(2));
        requestVo.setTaskAdvanceRemindTime(30);
        didaTaskService.modifyTask(17, 1, requestVo);
    }

    /**
     * 测试getTasksByStatus
     *
     * @author yang jie
     * @create 2020-10-29 20:40
     */
    @Test
    public void testGetTasksByStatus() {
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = didaTaskService.getTasksByStatus(1, 1);
        for (DidaTaskResponseVo didaTaskResponseVo : didaTaskResponseVos) {
            System.out.println("========================");
            System.out.println(didaTaskResponseVo);
        }
    }

    /**
     * 测试getAllTasks
     *
     * @author yang jie
     * @create 2020-10-29 21:00
     */
    @Test
    public void testGetAllTasks() {
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = didaTaskService.getAllTasks(2);
        for (DidaTaskResponseVo didaTaskResponseVo : didaTaskResponseVos) {
            System.out.println("========================");
            System.out.println(didaTaskResponseVo);
        }
    }

    /**
     * 测试deleteTask
     *
     * @author yang jie
     * @create 2020-10-29 21:10
     */
    @Test
    public void testDeleteTask() {
        didaTaskService.deleteTask(1, 2);
    }

    /**
     * 测试getTaskById
     *
     * @author yang jie
     * @create 2020-11-05 12:05
     */
    @Test
    public void testGetTaskById() {
        System.out.println(didaTaskService.getTaskById(26, 1));
    }

    /**
     * 测试draftTask
     *
     * @author yang jie
     * @create 2020-11-06 22:55
     */
    @Test
    public void testDraftTask() {
        didaTaskService.draftTask(17, 1);
    }
}
