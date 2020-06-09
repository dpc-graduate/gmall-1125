package com.atguigu.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: dpc
 * @data: 2020/6/3,19:59
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class GmallItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmallItemApplication.class,args);
    }
}
