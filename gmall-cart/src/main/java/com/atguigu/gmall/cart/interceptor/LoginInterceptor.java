package com.atguigu.gmall.cart.interceptor;

import com.atguigu.gmall.cart.config.JwtProperties;
import com.atguigu.gmall.cart.entity.UserInfo;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

/**
 * @author: dpc
 * @data: 2020/6/10,11:28
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor implements HandlerInterceptor {
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();
    @Autowired
    private JwtProperties properties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        THREAD_LOCAL.set(new UserInfo(1L, UUID.randomUUID().toString()));
//        System.out.println("方法前执行");
        //获取用户登录信息
        String token = CookieUtils.getCookieValue(request, properties.getCookieName());
        String userKey = CookieUtils.getCookieValue(request, properties.getUserKey());
        if (StringUtils.isBlank(userKey)) {
            userKey = UUID.randomUUID().toString();
            CookieUtils.setCookie(request, response, properties.getUserKey(), userKey, 15552000);
        }
        if (StringUtils.isBlank(token)) {
            THREAD_LOCAL.set(new UserInfo(null,userKey));
            return true;
        }
        try {
            Map<String, Object> map = JwtUtils.getInfoFromToken(token, properties.getPublicKey());
            THREAD_LOCAL.set(new UserInfo(Long.valueOf(map.get("userId").toString()),userKey));
        } catch (Exception e) {
            THREAD_LOCAL.set(new UserInfo(null,userKey));
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    public static UserInfo getUserInfo() {
        return THREAD_LOCAL.get();
    }

    /**
     * 释放资源
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        THREAD_LOCAL.remove();
    }
}
