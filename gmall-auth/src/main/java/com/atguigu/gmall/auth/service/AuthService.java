package com.atguigu.gmall.auth.service;

import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.feign.GmallUmsCilent;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.IpUtil;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.ums.config.UserException;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author: dpc
 * @data: 2020/6/9,19:46
 */
@Service
public class AuthService {
    @Autowired
    private GmallUmsCilent umsCilent;
    @Autowired
    private JwtProperties jwtProperties;
    public void accredit(String loginName, String password, HttpServletRequest request, HttpServletResponse response) {
        UserEntity entity = umsCilent.queryUser(loginName, password).getData();
        if (entity==null){
            throw new UserException("用户名或密码错误");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",entity.getId());
        map.put("userName",entity.getUsername());
        map.put("ip", IpUtil.getIpAddressAtService(request));
        try {
           String token=JwtUtils.generateToken(map,this.jwtProperties.getPrivateKey(),this.jwtProperties.getExpire());
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60,"utf-8");
            CookieUtils.setCookie(request,response,this.jwtProperties.getUnick(),entity.getNickname(),this.jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
            throw new UserException("服务器错误");
        }
    }
}
