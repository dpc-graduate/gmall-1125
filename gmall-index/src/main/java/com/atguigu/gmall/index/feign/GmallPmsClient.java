package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import lombok.Synchronized;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author: dpc
 * @data: 2020/6/2,13:51
 */

@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {

}
