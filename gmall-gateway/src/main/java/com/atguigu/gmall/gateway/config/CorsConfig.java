package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

/**
 * @author: dpc
 * @data: 2020/5/19,9:58
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        //设置可以访问的域名，不推荐使用* 因为使用* 不能携带cookie，而且不安全
        configuration.addAllowedOrigin("*");

        //允许所有类型的方法来跨域访问
        configuration.addAllowedMethod("*");
        //允许携带cookie
        configuration.setAllowCredentials(true);
        //允许携带所有的头信息来跨域访问
        configuration.addAllowedHeader("*");
        source.registerCorsConfiguration("/**",configuration);
        return new CorsWebFilter(source);
    }
}
