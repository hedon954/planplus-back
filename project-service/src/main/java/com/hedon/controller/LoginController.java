package com.hedon.controller;

import com.hedon.sercurity.TokenInfo;
import com.hedon.service.IBaiduInfoService;
import com.hedon.service.IDidaUserService;
import com.hedon.service.IShortMessageService;
import common.code.ResultCode;
import common.entity.BaiduInfo;
import common.entity.DidaUser;
import common.exception.ServiceException;
import common.mapper.BaiduInfoMapper;
import common.vo.common.BaiduTokenInfo;
import common.vo.common.ResponseBean;
import common.vo.common.UserBaiduInfo;
import common.vo.request.ChangePwdRequestVo;
import common.vo.request.LoginRequestVo;
import common.vo.request.RegisterRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录、鉴权相关的控制器
 *
 * @author Hedon Wang
 * @create 2020-11-02 09:08
 */
@RestController
@RequestMapping("/project/login")
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)   //支持全局方法安全控制
public class LoginController {

    @Autowired
    IDidaUserService didaUserService;

    @Autowired
    IShortMessageService shortMessageService;

    /**
     * 注入环境变量实例，可以通过它来读取配置文件中的值
     */
    @Autowired
    private Environment environment;

    @Autowired
    private BaiduInfoMapper baiduInfoMapper;

    /**
     * 百度信息 Service
     */
    @Autowired
    private IBaiduInfoService baiduInfoService;

    /**
     * 发送 http 请求工具
     */
    private RestTemplate restTemplate = new RestTemplate();


    /**
     * 检查是否已经登录
     *
     * @author Jihan Wang
     * @create 2020.11.2
     * @return
     */
    @PostMapping("/checkLogin")
    @PreAuthorize(("hasAuthority('ROLE_ADMIN')"))
    public ResponseBean checkLogin(@AuthenticationPrincipal(expression = "#this.userId") Integer userId){
        DidaUser didaUser = didaUserService.getUserById(userId);
        Map<String,Object> map = new HashMap<>();
        map.put("new",didaUser.getIsNewUser());
        return ResponseBean.success(map);
    }


    /**
     * 登录接口
     *
     * @author Jihan Wang
     * @create 2020.11.2
     * @param loginVo
     * @return
     */
    @PostMapping("/login")
    public ResponseBean login(@RequestBody LoginRequestVo loginVo, HttpServletRequest request){

        if (loginVo!=null){
            /**
             * 去获取 token
             */
            try{
                //请求路径
                String oauthServiceUrl = "http://localhost:10040/oauth/token";
                //请求头
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                httpHeaders.setBasicAuth("planplus","123456");
                //请求参数
                MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
                params.add("username",loginVo.getUsername());
                params.add("password",loginVo.getPassword());
                params.add("grant_type","password");
                params.add("scope","write read");
                //封装请求体
                HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(params,httpHeaders);
                //发送请求
                ResponseEntity<TokenInfo> response = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST, entity, TokenInfo.class);
                //放到请求域中，放进去之前要进行初始化，设置过期时间
                request.getSession().setAttribute("token",response.getBody().init());
                //登录成功
                return ResponseBean.success(response.getBody().init());
            }catch (Exception e){
                return ResponseBean.fail(ResultCode.ERROR_PASSWORD);
            }
        }

        //没传参
        return ResponseBean.fail(ResultCode.ERROR);
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
    @PostMapping("/getUserOpenIdAndSessionKeyAndUnionId")
    public ResponseBean getUserOpenIdAndSessionKeyAndUnionId(@RequestParam("code")String code){
        //检查 code 是否为空
        if (StringUtils.isNotBlank(code)){
            //尝试发送请求获取 openId 和 sessionKey
            try{
                //请求链接
                String jscode2sessionkeyUrl = "https://spapi.baidu.com/oauth/jscode2sessionkey";
                //请求头
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set("Content-Type","Application/x-www-form-urlencoded");
                //请求参数
                MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
                params.add("code",code);
                params.add("client_id",environment.getProperty("baidu.planplus.client-id"));
                params.add("sk",environment.getProperty("baidu.planplus.sk"));
                //请求体
                HttpEntity<MultiValueMap<String,String >> entity = new HttpEntity<>(params,httpHeaders);
                ResponseEntity<UserBaiduInfo> responseEntity = restTemplate.exchange(jscode2sessionkeyUrl, HttpMethod.POST, entity,UserBaiduInfo.class);
                //成功拿到的话，就存到数据库
                UserBaiduInfo userBaiduInfo = responseEntity.getBody();
                try{
                    //拿到 openId 后，继续发送请求获取用户的 unionId
                    String userUnionId = getUserUnionId(userBaiduInfo.getOpenid());
                    //查看用户是否存在
                    DidaUser didaUser = didaUserService.selectUserByUnionId(userBaiduInfo.getOpenid(),userBaiduInfo.getSession_key(),userUnionId);
                    return ResponseBean.success(didaUser);
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
     * 根据用户的 openId 来获取用户的 unionId
     *
     * @author Jiahan Wang
     * @create 2020.12.6
     * @param openId
     * @return
     */
    public String getUserUnionId(String openId){
        //补充百度信息 access_token 和 token_time
        BaiduInfo planPlusInfo = baiduInfoService.getPlanPlusInfo();
        //检查 token 是否过期
        if (planPlusInfo.tokenIsExpired()){
            //如果过期，则重新获取 token
            try{
                getBaiduToken(planPlusInfo);
            }catch (Exception e){
                log.error("更新百度 token 失败，原因为为：({})",e.getMessage());
                throw new ServiceException(ResultCode.UPDATE_BAIDU_INFO_FAILED);
            }
        }
        //获取用户 unionId 的百度链接
        String getUserUnionIdUrl = "https://openapi.baidu.com/rest/2.0/smartapp/getunionid?access_token="+ planPlusInfo.getAccessToken();
        try {
            //请求头
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type","Application/x-www-form-urlencoded");
            //请求参数
            MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
            params.add("openid",openId);
            //请求体
            HttpEntity<MultiValueMap<String,String >> entity = new HttpEntity<>(params,httpHeaders);
            ResponseEntity<UserBaiduInfo> responseEntity = restTemplate.exchange(getUserUnionIdUrl, HttpMethod.POST, entity,UserBaiduInfo.class);
            UserBaiduInfo userBaiduInfo = responseEntity.getBody();
            //拿到 unionId
            if (userBaiduInfo.getErrno() == 0){
                return userBaiduInfo.getData().get("unionid");
            }else{
                throw new ServiceException(ResultCode.GET_UNIONID_FAILED);
            }
        }catch (Exception e){
            //如果捕获到异常，那就重新登录
            e.printStackTrace();
            throw new ServiceException(ResultCode.GET_UNIONID_FAILED);
        }


    }

    /**
     * 获取百度 token
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param planPlusInfo
     */
    private void getBaiduToken(BaiduInfo planPlusInfo) throws Exception{
        //请求链接
        String getBaiduTokenUrl = "https://openapi.baidu.com/oauth/2.0/token";
        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //请求参数
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","client_credentials");
        params.add("client_id",environment.getProperty("baidu.planplus.client-id"));
        params.add("client_secret",environment.getProperty("baidu.planplus.sk"));
        params.add("scope","smartapp_snsapi_base");
        //请求体
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(params,httpHeaders);
        //发送请求
        ResponseEntity<BaiduTokenInfo> response = restTemplate.exchange(getBaiduTokenUrl, HttpMethod.POST, entity, BaiduTokenInfo.class);
        //更新 token
        BaiduTokenInfo tokenInfo = response.getBody();
        planPlusInfo.setAccessToken(tokenInfo.getAccess_token());
        planPlusInfo.setTokenTime(LocalDateTime.now());
        baiduInfoMapper.updateById(planPlusInfo);
    }



    /**
     * 注册
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param vo
     * @return
     */
    @PostMapping("/register")
    public ResponseBean register(@RequestBody RegisterRequestVo vo){

        if (vo == null){
            return ResponseBean.fail(ResultCode.REGISTER_FAILED);
        }
        //注册
        try {
            didaUserService.register(vo.getUsername(),vo.getPassword(),vo.getCode());
        }catch (ServiceException e){
            return e.getFailResponse();
        }

        return ResponseBean.success();
    }


    /**
     * 找回密码
     *
     * @param vo
     * @return
     */
    @PostMapping("/getPasswordBack")
    public ResponseBean getPasswordBack(@RequestBody ChangePwdRequestVo vo){
        if (vo == null){
            return ResponseBean.fail(ResultCode.ERROR);
        }
        //找回密码
        try {
            didaUserService.getPasswordBack(vo.getUsername(),vo.getPassword(),vo.getCode());
        }catch (ServiceException e){
            return e.getFailResponse();
        }

        return ResponseBean.success();

    }

    /**
     * 退出登录
     *
     * @author Jihan Wang
     * @create 2020.11.2
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public ResponseBean logout(HttpServletRequest request){
        //将 session 失效
        request.getSession().invalidate();
        return ResponseBean.success();
    }


    /**
     * 测试发送验证码
     * @param userId
     * @param phoneNumber
     * @return
     */
    @PostMapping("/code")
    @PreAuthorize(("hasAuthority('ROLE_ADMIN')"))
    public ResponseBean sendCode(@AuthenticationPrincipal(expression = "#this.userId") Integer userId,
                                 @RequestParam("phoneNumber")String phoneNumber){
        System.out.println(userId);
        return shortMessageService.sendCode(phoneNumber);
    }

}
