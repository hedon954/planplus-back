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
        requestVo.setTaskContent("上工地搬砖");
        requestVo.setTaskPlace("黄土高坡");
        requestVo.setTaskRate(0);
        requestVo.setTaskStartTime(LocalDateTime.now());
        requestVo.setTaskPredictedFinishTime(LocalDateTime.now());
        didaTaskService.createTask(1, requestVo);
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
}
