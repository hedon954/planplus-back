package com.hedon.handler;

import com.hedon.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

/**
 * 权限表达式控制器
 *
 * @author Hedon Wang
 * @create 2020-10-31 11:51
 */
@Component
public class ZuulWebSecurityExpressionHandler extends OAuth2WebSecurityExpressionHandler {

    /**
     * 注入我们手写的权限服务
     */
    @Autowired
    private PermissionService permissionService;

    /**
     *  创建评估上下文，告诉它 "#permissionService.hasPermission(request,authentication) 这个字符串该怎么解析
     *
     * @param authentication
     * @param invocation
     * @return
     */
    @Override
    protected StandardEvaluationContext createEvaluationContextInternal(Authentication authentication, FilterInvocation invocation) {
        //执行父类的，创建出标准的评估上下文（避免覆盖掉了原来的）
        StandardEvaluationContext sec = super.createEvaluationContextInternal(authentication, invocation);
        //再添加我们自定义的
        sec.setVariable("permissionService",permissionService);
        return sec;
    }

}
