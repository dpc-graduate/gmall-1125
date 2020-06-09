package com.atguigu.gmallsearch.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author: dpc
 * @data: 2020/5/27,14:12
 */
@FeignClient("wms-service")
@Component
public interface GmallWmsClient extends GmallWmsApi {
}
