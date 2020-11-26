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
import common.vo.common.UserBaiduInfo;
import common.vo.request.DidaUserRequestVo;
import common.vo.response.DidaUserResponseVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
     * 注入 OAuth2RestTemplate 实例
     * 在主启动类定义
     * 可以用来发送请求调用远程服务
     * 且在发送请求的时候会自动帮我们带上上一个请求的请求头，即token
     */
    @Autowired
    private OAuth2RestTemplate oAuth2RestTemplate;

    /**
     * 注入环境变量实例，可以通过它来读取配置文件中的值
     */
    @Autowired
    private Environment environment;


    /**
     * [已废弃]
     *
     * 登录
     * @param phoneNumber
     * @param password
     * @return
     */
    @Deprecated
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
     * @param userId 用户id
     * @param didaUserRequestVo 前端发送的修改信息
     * @return
     * @throws JsonProcessingException
     */
    @PutMapping("/info")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean updateUserInfo(@AuthenticationPrincipal(expression = "#this.userId")Integer userId,@RequestBody DidaUserRequestVo didaUserRequestVo) throws JsonProcessingException {
        DidaUser didaUser = new DidaUser();
        //从前端传输的对象中复制属性
        BeanUtils.copyProperties(didaUserRequestVo,didaUser);
        //设置用户id
        didaUser.setUserId(userId);
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


    /**
     * 百度小程序的 swan.login 成功后会传一个 code 过来
     * 这个 code 可以获取用户百度账号下的 openId 和 sessionKey
     * 需要将他们存储到数据库中，后面发送通知的时候需要用到 openId
     * sessionKey 也可以用来解析用户数据，后面根据具体情况可以考虑
     *
     * @author Jiahan Wang
     * @create 2020.11.3
     * @param code
     * @return
     */
    @PostMapping("/getUserOpenIdAndSessionKey")
    @PreAuthorize(("hasAuthority('ROLE_ADMIN')"))
    public ResponseBean getUserOpenIdAndSessionKey(@AuthenticationPrincipal(expression = "#this.userId") Integer userId,
                                                   @RequestParam("code")String code){
        //检查 code 是否为空
        if (StringUtils.isNotBlank(code)){
            //尝试发送请求获取 openId 和 sessionKey
            try{
                //获取 access_token
                OAuth2AccessToken accessToken = oAuth2RestTemplate.getAccessToken();
                String accessTokenValue = accessToken.getValue();
                //请求链接
                String jscode2sessionkeyUrl = "https://spapi.baidu.com/oauth/jscode2sessionkey";
                //请求头
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("Authorization","bearer "+accessTokenValue);
                //请求参数
                MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
                params.add("code",code);
                params.add("client_id",environment.getProperty("baidu.planplus.client-id"));
                params.add("sk",environment.getProperty("baidu.planplus.sk"));
                //请求体
                HttpEntity<MultiValueMap<String,String >> entity = new HttpEntity<>(params,httpHeaders);
                ResponseEntity<UserBaiduInfo> responseEntity = oAuth2RestTemplate.exchange(jscode2sessionkeyUrl, HttpMethod.POST, entity,UserBaiduInfo.class);
                //成功拿到的话，就存到数据库
                UserBaiduInfo userBaiduInfo = responseEntity.getBody();
                try{
                    didaUserService.saveUserBaiduInfo(userId,userBaiduInfo);
                    return ResponseBean.success(userBaiduInfo);
                }catch (ServiceException e){
                    return e.getFailResponse();
                }
            }catch (Exception e){
                //如果捕获到异常，那就重新登录
                e.printStackTrace();
                return ResponseBean.fail(ResultCode.GET_OPENID_FAILED);
            }
        }

        return ResponseBean.fail(ResultCode.NO_AUTHENTICATION_CODE);
    }

    /**
     * 用户上传头像接口
     * @param userId 用户id
     * @param avatar 头像文件
     * @return
     * @throws IOException
     */
    @PostMapping("/avatar")
    public ResponseBean uploadAvatar(@AuthenticationPrincipal(expression = "#this.userId")Integer userId,
                                     @RequestParam("file") MultipartFile avatar) throws IOException {
        try{
            didaUserService.uploadAvatar(userId,avatar);
            return ResponseBean.success();
        }catch (ServiceException e) {
            e.printStackTrace();
            return e.getFailResponse();
        }
    }

    @GetMapping("/avatar/download")
    public ResponseEntity downloadAvatar(@AuthenticationPrincipal(expression = "#this.userId")Integer userId){
        Resource resource = didaUserService.loadAvatar(userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""+resource.getFilename()+"\"")
                .body(resource);
    }

}
