package com.hedon.controller;


import com.hedon.feign.UserFeignService;
import com.hedon.service.ITestService;
import common.code.ResultCode;
import common.entity.User;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
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
 * @since 2020-10-16
 */
@RestController
@RequestMapping("/project/test")
public class TestController {

    @Autowired
    private ITestService testService;


    @Autowired
    private UserFeignService userFeignService;

    /**
     * 获取所有用户 => 调用 auth-center 的接口
     *
     * @return 所有用户信息
     */
    @ApiOperation(value = "接口 xxx： 查询所有用户信息",httpMethod = "GET",notes = "")
    @GetMapping("/user/users")
    public ResponseBean getUsers(){
        return userFeignService.getUsers();
    }


    /**
     * 接口 xxx： 根据 id 查询用户
     * @param id            id
     * @return
     */
    @ApiOperation(value = "接口 xxx： 根据 id 查询用户",httpMethod = "GET",notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "用户ID",paramType = "path",dataType = "Integer",required = true)
    })
    @GetMapping("/user/{id}")
//    public ResponseBean<User> getUserById(@PathVariable("id") Integer id, ServerWebExchange exchange){
    public ResponseBean<User> getUserById(@PathVariable("id") Integer id){
        //获取请求体中的用户信息
//        User user = (User) exchange.getAttributes().get("user");
//        System.out.println("user is : " + user);

        //检查用户 id 是否为空
        if (id == null){
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }

        //查询用户信息
        User selectedUser;
        try{
            selectedUser = testService.getUserById(id);
        }catch (ServiceException e){
            return e.getFailResponse();
        }

        //返回数据给前端
        return ResponseBean.success(selectedUser);
    }

    /**
     * 接口 xxx： 更新用户信息
     * @param user
     * @return
     */
    @ApiOperation(value = "接口 xxx： 更新用户信息",httpMethod = "PUT",notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user",value = "用户",paramType = "query",dataType = "common.entity.User",required = true)
    })
    @PutMapping("/user/update")
    public ResponseBean updateUser(@RequestBody User user){

        //更新
        int i = testService.updateUser(user);
        //409是熔断降级
        if ( i == 409 ){
            return ResponseBean.fail(ResultCode.FALLBACK);
        }
        //只有i=1的时候才正常
        if ( i != 1 ){
            return ResponseBean.fail(ResultCode.DATABASE_ERROR);
        }
        return ResponseBean.success();
    }

}
