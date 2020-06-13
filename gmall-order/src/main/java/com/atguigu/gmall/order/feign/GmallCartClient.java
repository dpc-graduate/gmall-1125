package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.cart.api.GmallCartApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: dpc
 * @data: 2020/6/12,11:34
 */
@FeignClient("cart-service")
public interface GmallCartClient extends GmallCartApi {
}
