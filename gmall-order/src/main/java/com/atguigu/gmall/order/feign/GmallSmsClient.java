package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: dpc
 * @data: 2020/6/12,11:12
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
