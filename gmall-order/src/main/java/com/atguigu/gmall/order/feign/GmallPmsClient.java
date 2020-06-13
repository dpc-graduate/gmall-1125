package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: dpc
 * @data: 2020/6/12,11:12
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
