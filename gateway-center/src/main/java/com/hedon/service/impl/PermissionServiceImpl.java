package com.hedon.service.impl;

import com.hedon.service.PermissionService;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Hedon Wang
 * @create 2020-10-31 11:35
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    /**
     * 判断用户是否有权限
     *
     * @param request           当前请求
     * @param authentication    用户权限信息
     * @return
     */
    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        //TODO:根据实际业务需求，从参数中获取信息，然后去查询数据库或者缓存，进行对比，然后再判断是否放行
        System.out.println("当前的请求是：" + request.getRequestURI());
//        System.out.println("当前用户的权限信息是：" + ReflectionToStringBuilder.toString(authentication));

        //判断是否已登录（携带 token）
        if (authentication instanceof AnonymousAuthenticationToken){
            //如果是匿名用户，说明没带 token，抛出异常
            throw new AccessTokenRequiredException(null);
        }

        /*
        //获取当前请求路径
        String requestURI = request.getRequestURI();
        //获取用户拥有的权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority: authorities){
            String authorityStr = authority.getAuthority();
            //判断用户是否有该请求的权限
            if (StringUtils.containsIgnoreCase(requestURI,StringUtils.substringAfter(authorityStr,"ROLE_"))) {
                return true;
            }
        }
        return false;
         */

//        return RandomUtils.nextInt() % 2 == 0;  //50% 放行
        return true;
    }
}
