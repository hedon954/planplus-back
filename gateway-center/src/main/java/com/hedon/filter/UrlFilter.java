package com.hedon.filter;

import org.apache.commons.lang.StringUtils;

/**
 * 对 Url 的一些处理
 *
 * @author Hedon Wang
 * @create 2020-10-16 22:08
 */
public class UrlFilter {

    /**
     * 不需要认证的 URL
     *
     *  oauth：这是 oauth2 开头的接口，本身就是来认证的，所以不需要认证
     *  /project/user/login：项目自定义的登录接口
     *  /actuator：服务监控接口，不需要认证
     *  /login：spring security 自带的登录接口，过滤掉
     *
     */
    public static String[] notRequiredAuthUrl = new String[]{"/oauth","/project/user/login","/actuator","/login"};



    /**
     * 判断传进来的请求路径是否需要进行认证
     *
     * @param path      路径
     * @return          是否需要认证
     */
    public static boolean isNeedAuthentication(String path) {

        for (String url: notRequiredAuthUrl){
            //如果属于不需要认证的 URL 的，直接放行
            if (StringUtils.startsWith(path,url)){
                System.out.println(path +"不需要认证");
                return false;
            }
        }
        //其他的请求需要认证
        return true;
    }
}
