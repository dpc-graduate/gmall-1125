package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.vo.SkuSaleVo;
import com.atguigu.gmall.pms.feign.fallback.GmallSmsFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: dpc
 * @data: 2020/5/21,10:46
 */
@FeignClient(value = "sms-service", fallback = GmallSmsFallBack.class)
@Component
public interface GmallSmsClient {
    @PostMapping("sms/smsskubounds/sku/sales")
    public ResponseVo<Object> saveSale(@RequestBody SkuSaleVo skuSaleVo);
}
