package com.hedon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * ② 审计过滤器
 *
 * @author Hedon Wang
 * @create 2020-10-16 20:14
 */
@Component
@Slf4j
public class AuditLogFilter implements GlobalFilter, Ordered {

    /**
     * 审计逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //TODO: 这里应该做一些审计逻辑，这里先不做，简单做输入
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        log.info(path + ":"+ method +"请求进来了，审计开始 ============>>>>>>>>>>>>>");
        System.out.println("有请求进来了，审计开始 ============>>>>>>>>>>>>>");
        return chain.filter(exchange);
    }

    /**
     * 过滤器的顺序：限流(用Hystrix)->认证(1)->审计(2)->授权(3)
     * @return
     */
    @Override
    public int getOrder() {
        return 2;
    }

}
