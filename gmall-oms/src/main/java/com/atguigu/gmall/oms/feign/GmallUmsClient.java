package com.atguigu.gmall.oms.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: dpc
 * @data: 2020/6/13,10:17
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {
}
