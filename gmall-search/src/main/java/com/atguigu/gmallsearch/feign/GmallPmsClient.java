package com.atguigu.gmallsearch.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author: dpc
 * @data: 2020/5/27,14:11
 */
@FeignClient("pms-service")
@Component
public interface GmallPmsClient extends GmallPmsApi {
}
