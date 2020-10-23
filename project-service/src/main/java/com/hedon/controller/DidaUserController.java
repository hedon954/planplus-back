package com.hedon.controller;


import com.hedon.service.IDidaUserService;
import common.code.ResultCode;
import common.entity.DidaUser;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
import common.vo.response.DidaUserResponseVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
@RestController
@RequestMapping("/project/user")
public class DidaUserController {

    @Autowired
    IDidaUserService didaUserService;

    /**
     * 接口1.5 获取用户信息
     *
     * @author Hedon
     * @create 2020.10.23
     * @param userId 用户ID
     * @return 包含用户信息的 ResponseBean
     */
    @ApiOperation(value = "接口1.5 获取用户信息",httpMethod = "GET")
    @ApiImplicitParam(name = "userId",value = "用户ID",dataType = "Integer",paramType = "path",required = true)
    @GetMapping("/{userId}")
    public ResponseBean getUserById(@PathVariable("userId")Integer userId){

        //检查id是否为空
        if (userId == null){
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //查询用户信息
        DidaUserResponseVo didaUserResponseVo;
        try{
            DidaUser didaUser = didaUserService.getUserById(userId);
            didaUserResponseVo = new DidaUserResponseVo(didaUser);
        }catch (ServiceException e){
            //从抛出的异常信息中封装出一个 ResponseBean
            return e.getFailResponse();
        }
        //封装信息，返回给前端
        return ResponseBean.success(didaUserResponseVo);
    }
}
