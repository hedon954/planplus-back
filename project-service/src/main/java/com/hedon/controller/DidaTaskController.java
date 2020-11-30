package com.hedon.controller;

import com.hedon.feign.NotificationFeignService;
import com.hedon.service.IDidaTaskService;
import com.hedon.service.IDidaUserService;
import common.code.ResultCode;
import common.entity.DidaTask;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
import common.vo.request.DidaTaskRequestVo;
import common.vo.request.DidaTaskSentenceRequestVo;
import common.vo.response.DidaTaskResponseVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    IDidaUserService didaUserService;

    @Autowired
    NotificationFeignService notificationFeignService;


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
    public ResponseBean createTask(@AuthenticationPrincipal(expression = "#this.userId") Integer userId, @RequestBody @Validated DidaTaskRequestVo taskInfo) {
        //判断userId是否为空
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //判断taskInfo是否为空
        if(taskInfo == null) {
            return ResponseBean.fail(ResultCode.PARAMETER_ERROR);
        }
        Map<String,Object> map = new HashMap<>();
        Integer taskId = 0;
        //创建新任务
        try {
            taskId = didaTaskService.createTask(userId, taskInfo);
            System.out.println("task Id = " + taskId);
            map.put("taskId",taskId);
            map.put("subScribeId", UUID.randomUUID().toString().substring(0,20));
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(map);
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
    @PutMapping("/delay/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean delayTask(@PathVariable("taskId") Integer taskId,
                                  @AuthenticationPrincipal(expression = "#this.userId") Integer userId,
                                  @RequestParam("delayTime") Integer delayTime,
                                  @RequestParam("formId") String formId) {

        //判断userId是否为空
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //判断taskInfo是否为空
        if(taskId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_TASK_ID);
        }

        Map<String,Object> map = new HashMap<>();
        //推迟任务
        try {
            didaTaskService.delayTask(taskId, userId, delayTime,formId);
            //返回一个随机生成的订阅ID，便于前端发起订阅
            map.put("subScribeId",UUID.randomUUID().toString().substring(0,30));
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(map);
    }


    /**
     * 补充接口 将任务保存至草稿箱
     *
     * @author yang jie
     * @create 2020-11-06 22:50
     * @param taskId
     * @param userId
     * @return
     */
    @ApiOperation(value = "补充接口 将任务保存至草稿箱", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "Integer", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    })
    @PutMapping("/draft/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean draftTask(@PathVariable("taskId") Integer taskId, @AuthenticationPrincipal(expression = "#this.userId") Integer userId) {
        //判断userId是否为空
        if(userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //判断taskInfo是否为空
        if(taskId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_TASK_ID);
        }

        //将任务保存至草稿箱
        try {
            didaTaskService.draftTask(taskId, userId);
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
     * @param formId
     * @return
     */
    @ApiOperation(value = "接口2.1.2.4 结束任务", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "Integer", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "formId", value = "表单ID", dataType = "String", paramType = "query", required = true)
    })
    @PutMapping("/finish/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean finishTask(@PathVariable("taskId") Integer taskId,
                                   @AuthenticationPrincipal(expression = "#this.userId") Integer userId,
                                   @RequestParam("fromId") String formId) {
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
            didaTaskService.finishTask(taskId, userId, formId);
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
    public ResponseBean modifyTask(@PathVariable("taskId") Integer taskId,
                                   @AuthenticationPrincipal(expression = "#this.userId") Integer userId,
                                   @RequestBody @Validated DidaTaskRequestVo taskInfo) {
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

        Map<String,Object> map = new HashMap<>();
        //修改任务内容
        try {
            didaTaskService.modifyTask(taskId, userId, taskInfo);
            map.put("subScribeId",UUID.randomUUID().toString().substring(0,30));
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(map);
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


    /**
     * 接口2.1.5 通过一句话创建任务
     *
     * @author Jiahan Wang
     * @create 2020.11.24
     * @param userId
     * @param taskInfo
     * @return
     */
    @ApiOperation(value = "接口2.1.5 通过一句话创建任务",httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true),
            @ApiImplicitParam(name = "taskInfo",value = "任务信息",dataType = "DidaTaskSentenceRequestVo",paramType = "body",required = true)
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/createBySentence")
    public ResponseBean createTaskBySentence(@AuthenticationPrincipal(expression = "#this.userId")Integer userId,
                                             @RequestBody DidaTaskSentenceRequestVo taskInfo){
        //检查 userId 是否为空
        if (userId == null){
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //检查 taskInfo 是否为空
        if (StringUtils.isBlank(taskInfo.getTaskInfo())){
            return ResponseBean.fail(ResultCode.TIMED_TASK_CREATE_FAILED,"任务创建失败，请输入任务信息!");
        }
        //创建任务
        Map<String,Object> map = new HashMap<>();
        DidaTaskResponseVo vo = null;
        try{
            DidaTask didaTask = didaTaskService.createTaskBySentence(userId,taskInfo);
            vo = new DidaTaskResponseVo(didaTask);
            map.put("didaTask",vo);
            map.put("subScribeId", UUID.randomUUID().toString().substring(0,20));
        }catch (ServiceException e){
            return e.getFailResponse();
        }catch (URISyntaxException e){
            return ResponseBean.fail(ResultCode.TIMED_TASK_CREATE_FAILED);
        }
        return ResponseBean.success(map);
    }

    /**
     * 接口2.1.3.6 查询草稿任务
     *
     * @author Jiahan Wang
     * @create 2020-11-30
     * @param userId
     * @return
     */
    @ApiOperation(value = "接口2.1.3.6 查询草稿任务", httpMethod = "GET")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Integer", paramType = "query", required = true)
    @GetMapping("/draft")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean getDraftTasks(@AuthenticationPrincipal(expression = "#this.userId") Integer userId) {
        if (userId == null) {
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }

        //查询所有已办任务
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = new ArrayList<>();
        try {
            didaTaskResponseVos = didaTaskService.getTasksByStatus(userId, 3);
        } catch (ServiceException e) {
            return e.getFailResponse();
        }
        return ResponseBean.success(didaTaskResponseVos);
    }
}
