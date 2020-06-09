package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: dpc
 * @data: 2020/6/3,21:13
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {

}
