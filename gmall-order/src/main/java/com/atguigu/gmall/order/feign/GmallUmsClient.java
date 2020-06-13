package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: dpc
 * @data: 2020/6/12,11:35
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {
}
