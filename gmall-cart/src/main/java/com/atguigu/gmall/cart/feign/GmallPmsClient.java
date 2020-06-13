package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: dpc
 * @data: 2020/6/10,14:54
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
