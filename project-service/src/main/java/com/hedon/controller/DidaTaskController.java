package com.hedon.controller;


import com.hedon.service.IDidaTaskService;
import com.hedon.service.IDidaUserTaskService;
import common.code.ResultCode;
import common.entity.DidaTask;
import common.entity.DidaUserTask;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
import common.vo.request.DidaTaskRequestVo;
import common.vo.response.DidaTaskResponseVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
@RestController
@RequestMapping("/project/task")
public class DidaTaskController {

    @Autowired
    IDidaTaskService didaTaskService;


    /**
     * 接口2.1.1 创建新任务
     *
     * @author yang jie
     * @create 2020-10-25 23:00
     * @param userId
     * @param taskInfo 封装的任务信息
     * @return
     */
    @ApiOperation(value = "接口2.1.1 新建任务", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "taskInfo", value = "任务信息", dataType = "Object", paramType = "body", required = true)
    })
    @PostMapping("/create")
    public ResponseBean createTask(@RequestParam("userId") Integer userId, @RequestBody DidaTaskRequestVo taskInfo) {
        //判断userId是否为空
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //判断taskInfo是否为空
        if(taskInfo == null) {
            return ResponseBean.fail(ResultCode.INVALID_PARAMETER);
        }

        //创建新任务
        try {
            didaTaskService.createTask(userId, taskInfo);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success();
    }

    /**
     * 查询今日待办任务
     *
     * @author yang jie
     * @create 2020-10-26 22:30
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.3.1 查询今日任务", httpMethod = "GET")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("/today")
    public ResponseBean getTodayTasks(@RequestParam("userId") Integer userId) {
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }

        //查询今日待办
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = null;
        try {
            didaTaskResponseVos = didaTaskService.getTasksByDate(userId, LocalDate.now());
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(didaTaskResponseVos);
    }

    /**
     * 查询明日待办任务
     *
     * @author yang jie
     * @create 2020-10-27 01:00
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.3.2 查询明日任务", httpMethod = "GET")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("/tomorrow")
    public ResponseBean getTomorrowTasks(@RequestParam("userId") Integer userId) {
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }

        //查询明日待办
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = null;
        try {
            didaTaskResponseVos = didaTaskService.getTasksByDate(userId, LocalDate.now().plusDays(1));
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(didaTaskResponseVos);
    }

}
