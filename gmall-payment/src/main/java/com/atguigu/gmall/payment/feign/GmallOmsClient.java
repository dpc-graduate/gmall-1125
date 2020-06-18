package com.atguigu.gmall.payment.feign;

import com.atguigu.gmall.oms.api.GmallOmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: dpc
 * @data: 2020/6/15,10:44
 */
@FeignClient("oms-service")
public interface GmallOmsClient extends GmallOmsApi {
}
