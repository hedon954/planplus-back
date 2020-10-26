package com.hedon.controller;


import com.hedon.service.IDidaTaskService;
import com.hedon.service.IDidaUserTaskService;
import common.code.ResultCode;
import common.entity.DidaTask;
import common.entity.DidaUserTask;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
import common.vo.request.DidaTaskRequestVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    IDidaUserTaskService didaUserTaskService;

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
    public ResponseBean insertTask(@RequestParam("userId") Integer userId, @RequestBody DidaTaskRequestVo taskInfo) {
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
            //修改任务表
            DidaTask didaTask = DidaTaskRequestVo.toDidaTask(taskInfo);
            didaTaskService.insertTask(didaTask);

            //获取新建任务的taskId
            Integer taskId = didaTask.getTaskId();
            //修改用户任务表
            DidaUserTask didaUserTask = new DidaUserTask();
            didaUserTask.setDidaTaskId(taskId);
            didaUserTask.setDidaUserId(userId);
            didaUserTaskService.insertUserTask(didaUserTask);

        } catch (ServiceException e) {
            return e.getFailResponse();
        }

        return ResponseBean.success();
    }

}
