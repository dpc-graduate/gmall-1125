package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: dpc
 * @data: 2020/6/9,19:48
 */
@FeignClient("ums-service")
public interface GmallUmsCilent extends GmallUmsApi {

}
