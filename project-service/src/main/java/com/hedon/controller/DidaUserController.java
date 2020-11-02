package com.hedon.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hedon.service.IDidaUserService;
import common.code.ResultCode;
import common.entity.DidaUser;
import common.exception.ServiceException;
import common.util.PhoneFormatCheckUtils;
import common.vo.common.ResponseBean;
import common.vo.request.DidaUserRequestVo;
import common.vo.response.DidaUserResponseVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/project/user")
public class DidaUserController {

    @Autowired
    IDidaUserService didaUserService;


    /**
     * 登录
     * @param phoneNumber
     * @param password
     * @return
     */
    @PostMapping("/login")
    public ResponseBean login(@RequestParam("phoneNumber")String phoneNumber, @RequestParam("password")String password){
        //判断手机格式是否正确
        if (!PhoneFormatCheckUtils.isPhoneLegal(phoneNumber)){
            return ResponseBean.fail(ResultCode.PHONE_FORMAT_ERROR);
        }
        //判断密码是否为空
        if (StringUtils.isBlank(password)){
            return ResponseBean.fail(ResultCode.EMPTY_PASSWORD);
        }
        //登录操作
        try {
            DidaUserResponseVo didaUser = didaUserService.login(phoneNumber,password);
            return ResponseBean.success(didaUser);
        }catch (ServiceException e){
            return e.getFailResponse();
        }
    }

    /**
     * 接口1.5 获取用户信息
     *
     * @author Hedon
     * @create 2020.10.23
     * @param userId 用户ID
     * @return 包含用户信息的 ResponseBean
     */
    @ApiOperation(value = "接口1.5 获取用户信息",httpMethod = "GET")
    @ApiImplicitParam(name = "userId",value = "用户ID",dataType = "Integer",paramType = "header",required = true)
    @GetMapping("/info")
    @PreAuthorize(("hasAuthority('ROLE_ADMIN')"))
    public ResponseBean getUserById(@AuthenticationPrincipal(expression = "#this.userId")Integer userId){

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
            //从抛出的异常信息中封装出一个 ResponseBean6
            return e.getFailResponse();
        }
        //封装信息，返回给前端
        return ResponseBean.success(didaUserResponseVo);
    }

    /**
     * @author Ruolin
     * @create 2020.10.25
     * @param userId
     * @param json
     * @return
     */
    @PutMapping("/{userId}")
    public ResponseBean updateUserInfo(@PathVariable("userId")Integer userId,@RequestBody String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        DidaUserRequestVo didaUserRequestVo = objectMapper.readValue(json,DidaUserRequestVo.class);
        DidaUser didaUser = new DidaUser();
        BeanUtils.copyProperties(didaUserRequestVo,didaUser);
        try{
            didaUserService.updateUserInfoById(didaUser);
            return ResponseBean.success();
        }catch (ServiceException e) {
            e.printStackTrace();
            return e.getFailResponse();
        }
    }


    /**
     * @author Ruolin
     * @create 2020.11.2
     * @param userId
     * @param json
     * @return
     * @throws JsonProcessingException
     */
    @PutMapping("/pwd/{userId}")
    public ResponseBean updatePassword(@PathVariable("userId")Integer userId,@RequestBody String json)throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        //获取新旧密码
        String oldPwd = objectMapper.readValue(json, ObjectNode.class).get("oldPwd").asText();
        String newPwd = objectMapper.readValue(json, ObjectNode.class).get("newPwd").asText();
        try{
            didaUserService.updatePassword(userId,oldPwd,newPwd);
            return ResponseBean.success();
        }catch (ServiceException e) {
            e.printStackTrace();
            return e.getFailResponse();
        }
    }



}
