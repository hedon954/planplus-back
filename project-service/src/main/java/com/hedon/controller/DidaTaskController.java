package com.hedon.controller;


import com.hedon.service.IDidaTaskService;
import common.code.ResultCode;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
import common.vo.request.DidaTaskRequestVo;
import common.vo.response.DidaTaskResponseVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

//import com.sun.org.apache.regexp.internal.RE;

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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean createTask(@AuthenticationPrincipal(expression = "#this.userId") Integer userId, @RequestBody DidaTaskRequestVo taskInfo) {
        //判断userId是否为空
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //判断taskInfo是否为空
        if(taskInfo == null) {
            return ResponseBean.fail(ResultCode.PARAMETER_ERROR);
        }

        //新建任务的id
        Integer taskId;
        //创建新任务
        try {
            taskId = didaTaskService.createTask(userId, taskInfo);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(taskId);
    }


    /**
     * 接口2.1.2.2 开始任务
     *
     * @author yang jie
     * @create 2020-10-29 11:20
     * @param taskId
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.2.2 开始任务", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "Integer", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    })
    @PutMapping("/start/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean startTask(@PathVariable("taskId") Integer taskId, @AuthenticationPrincipal(expression = "#this.userId") Integer userId) {
        //判断userId是否为空
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //判断taskInfo是否为空
        if(taskId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_TASK_ID);
        }

        //开始任务
        try {
            didaTaskService.startTask(taskId, userId);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success();
    }


    @ApiOperation(value = "接口2.1.2.3 推迟任务", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务Id", dataType = "Integer", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "delayTime", value = "推迟时间", dataType = "Integer", paramType = "query", required = true)
    })
    @PutMapping("delay/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean delayTask(@PathVariable("taskId") Integer taskId, @AuthenticationPrincipal(expression = "#this.userId") Integer userId, @RequestParam("delayTime") Integer delayTime) {

        //判断userId是否为空
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //判断taskInfo是否为空
        if(taskId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_TASK_ID);
        }

        //推迟任务
        try {
            didaTaskService.delayTask(taskId, userId, delayTime);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success();
    }


    /**
     * 接口2.1.2.4 结束任务
     *
     * @author yang jie
     * @create 2020-10-29 17:30
     * @param taskId
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.2.4 结束任务", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "Integer", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    })
    @PutMapping("/finish/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean finishTask(@PathVariable("taskId") Integer taskId, @AuthenticationPrincipal(expression = "#this.userId") Integer userId) {
        //判断userId是否为空
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //判断taskInfo是否为空
        if(taskId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_TASK_ID);
        }

        //结束任务
        try {
            didaTaskService.finishTask(taskId, userId);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success();
    }


    /**
     * 接口2.1.2.5 修改任务内容
     *
     * @author yang jie
     * @create 2020-10-29 19:30
     * @param taskId
     * @param userId
     * @param taskInfo
     * @return
     */
    @ApiOperation(value = "接口2.1.2.4 结束任务", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "Integer", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "taskInfo", value = "任务内容", dataType = "Object", paramType = "body", required = true)
    })
    @PutMapping("/modify/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean modifyTask(@PathVariable("taskId") Integer taskId, @AuthenticationPrincipal(expression = "#this.userId") Integer userId, @RequestBody DidaTaskRequestVo taskInfo) {
        //判断userId是否为空
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //判断taskId是否为空
        if(taskId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_TASK_ID);
        }
        //判断taskInfo是否为空
        if(taskInfo == null) {
            return ResponseBean.fail(ResultCode.PARAMETER_ERROR);
        }

        //修改任务内容
        try {
            didaTaskService.modifyTask(taskId, userId, taskInfo);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success();
    }


    /**
     * 接口2.1.3.1 查询今日待办任务
     *
     * @author yang jie
     * @create 2020-10-26 22:30
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.3.1 查询今日任务", httpMethod = "GET")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("/today")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean getTodayTasks(@AuthenticationPrincipal(expression = "#this.userId") Integer userId) {
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
     * 接口2.1.3.2 查询明日待办任务
     *
     * @author yang jie
     * @create 2020-10-27 01:00
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.3.2 查询明日任务", httpMethod = "GET")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("/tomorrow")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean getTomorrowTasks(@AuthenticationPrincipal(expression = "#this.userId") Integer userId) {
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }

        //查询明日待办
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = new ArrayList<>();
        try {
            didaTaskResponseVos = didaTaskService.getTasksByDate(userId, LocalDate.now().plusDays(1));
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(didaTaskResponseVos);
    }


    /**
     * 接口2.1.3.3 查询所有待办
     *
     * @author yang jie
     * @create 2020-10-29 20:15
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.3.3 查询所有待办", httpMethod = "GET")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("/todo")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean getTodoTasks(@AuthenticationPrincipal(expression = "#this.userId") Integer userId) {
        if (userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }

        //查询所有待办任务
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = new ArrayList<>();
        try {
            didaTaskResponseVos = didaTaskService.getTasksByStatus(userId, 0);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(didaTaskResponseVos);
    }


    /**
     * 接口2.1.3.4 查询所有已办
     *
     * @author yang jie
     * @create 2020-10-29 20:50
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.3.4 查询所有已办", httpMethod = "GET")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("/done")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean getDoneTasks(@AuthenticationPrincipal(expression = "#this.userId") Integer userId) {
        if (userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }

        //查询所有已办任务
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = new ArrayList<>();
        try {
            didaTaskResponseVos = didaTaskService.getTasksByStatus(userId, 2);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(didaTaskResponseVos);
    }


    /**
     * 接口2.1.3.5 查询所有任务
     *
     * @author yang jie
     * @create 2020-10-29 20:15
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.3.5 查询所有任务", httpMethod = "GET")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean getAllTasks(@AuthenticationPrincipal(expression = "#this.userId") Integer userId) {
        if (userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }

        //查询所有任务
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = new ArrayList<>();
        try {
            didaTaskResponseVos = didaTaskService.getAllTasks(userId);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(didaTaskResponseVos);
    }


    /**
     * 接口2.1.4 删除任务
     *
     * @author yang jie
     * @create 2020-10-29 21:05
     * @param taskId
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.4 删除任务", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "Integer", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    })
    @DeleteMapping("/delete/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean deleteTask(@PathVariable("taskId") Integer taskId, @AuthenticationPrincipal(expression = "#this.userId") Integer userId) {
        //判断userId是否为空
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //判断taskInfo是否为空
        if(taskId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_TASK_ID);
        }

        //删除任务
        try {
            didaTaskService.deleteTask(taskId, userId);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success();
    }


    /**
     * 接口2.1.3.6 查询单个任务
     *
     * @author yang jie
     * @create 2020-11-05 11:50
     * @param userId
     * @param taskId
     * @return
     */
    @ApiOperation(value = "接口2.1.3.6 查询单个任务", httpMethod = "GET")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("/single/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean getTask(@AuthenticationPrincipal(expression = "#this.userId") Integer userId, @PathVariable("taskId") Integer taskId) {
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        if(taskId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_TASK_ID);
        }

        //根据taskId查询单个任务
        DidaTaskResponseVo didaTaskResponseVo;
        try {
            didaTaskResponseVo = didaTaskService.getTaskById(taskId, userId);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(didaTaskResponseVo);
    }
}
