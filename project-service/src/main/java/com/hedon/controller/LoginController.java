package com.hedon.controller;

import com.hedon.sercurity.TokenInfo;
import com.hedon.sercurity.UserDetailsEnhance;
import common.code.ResultCode;
import common.vo.common.ResponseBean;
import common.vo.request.LoginRequestVo;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;

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
        System.out.println("user is :" + userId);
        return ResponseBean.success(userId);
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
                String oauthServiceUrl = "http://localhost:9527/oauth/token";
                //请求头
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                httpHeaders.setBasicAuth("planplus","123456");
                //请求参数
                MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
                params.add("username",loginVo.getPhoneNumber());
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

}
