package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.utils.IpUtil;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.gateway.config.JwtProperties;
import com.google.common.net.HttpHeaders;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: dpc
 * @data: 2020/6/10,9:19
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.PathConfig> {
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 通过构造方法 来给配置类赋值
     */
    public AuthGatewayFilterFactory() {
        super(PathConfig.class);
    }

    @Override
    public GatewayFilter apply(PathConfig config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                System.out.println("局部过滤器" + config);
                //1 判断路径是否要拦截 是否在config中，不在直接放行
                ServerHttpResponse response = exchange.getResponse();
                ServerHttpRequest request = exchange.getRequest();
                //获取当前请求路径
                String path = request.getURI().getPath();
                List<String> authPaths = config.getAuthPaths();
                //不在拦截名单中就放行
                if (authPaths.stream().allMatch(authPath -> path.indexOf(authPath) == -1)) {
                    return chain.filter(exchange);
                }
                //2 拦截 获取token信息，同步请求token在cookie中，异步在头信息中
                String token = request.getHeaders().getFirst("token");
                //先尝试从头信息获取
                if (StringUtils.isBlank(token)) {
                    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                    if (!CollectionUtils.isEmpty(cookies) && cookies.containsKey(jwtProperties.getCookieName())) {
                        token = cookies.getFirst(jwtProperties.getCookieName()).getValue();
                    }
                }
                //3 判断token是否为空，空就拦截重定向到登录
                if (StringUtils.isBlank(token)) {
                    //303 重定向到path地址
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnURL" + request.getURI().toString());
                    return response.setComplete();
                }
                //4 解析token，异常就拦截，重定向到登录
                try {
                    Map<String, Object> map = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
                    String ip = (String) map.get("ip");
                    String currentIp = IpUtil.getIpAddressAtGateway(request);
                    //5 根据token中的ip和当前请求的ip是否一样，不一样就拦截
                    if (!StringUtils.equals(ip, currentIp)) {
                        response.setStatusCode(HttpStatus.SEE_OTHER);
                        response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnURL" + request.getURI().toString());
                        return response.setComplete();
                    }
                    //6 把登录信息传递给后续的微服务
                    request.mutate().header("userId", (String) map.get("userId")).build();
                    exchange.mutate().request(request).build();
                    //7 放行
                    return chain.filter(exchange);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://sso.gmall.com/toLogin.html?returnURL" + request.getURI().toString());
                    return response.setComplete();
                }
            }
        };
    }

    /**
     * 指定字段模型接受顺序
     *
     * @return
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("authPaths");
    }

    /**
     * 指定接受配置参数的字段类型 为list
     *
     * @return
     */
    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    /**
     * 定义接受过滤器参数的数据模型
     */
    @Data
    public static class PathConfig {
        private List<String> authPaths;

    }
}

