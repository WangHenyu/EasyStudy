package com.why.gateway.filter;


import com.google.gson.JsonObject;
import com.why.commonutils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局权限认证过滤器
 */
@Order(0)
@Component
public class AuthGlobalFilter implements GlobalFilter {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //校验用户必须登录(可以继续配置)
        if(antPathMatcher.match("/eduorder/save/**", path) ||
           antPathMatcher.match("/user/**/auth/**", path) ||
           antPathMatcher.match("/eduvod/*/auth/**", path) ||
           antPathMatcher.match("/eduservice/**/auth/**", path)) {

            System.out.println("-------进入权限过滤器---------");
            List<String> tokenList = request.getHeaders().get("token");
            if(null == tokenList) {
                ServerHttpResponse response = exchange.getResponse();
                return out(response);
            }
            boolean hasAuth = JwtUtils.checkToken(tokenList.get(0));
            if (!hasAuth){
                ServerHttpResponse response = exchange.getResponse();
                return out(response);
            }
        }
        //内部服务接口不允许外部访问
        if(antPathMatcher.match("/**/inner/**", path)) {
            ServerHttpResponse response = exchange.getResponse();
            return out(response);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> out(ServerHttpResponse response){
        JsonObject message = new JsonObject();
        message.addProperty("success", false);
        message.addProperty("code", 28004);
        message.addProperty("data", "权限认证失败");
        byte[] bits = message.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //指定编码否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF8");
        return response.writeWith(Mono.just(buffer));
    }

}
