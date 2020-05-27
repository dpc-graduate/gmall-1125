package com.atguigu.gmall.pms.feign.fallback;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.vo.SkuSaleVo;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import org.springframework.stereotype.Component;

/**
 * @author: dpc
 * @data: 2020/5/21,10:48
 */
@Component
public class GmallSmsFallBack implements GmallSmsClient {
    @Override
    public ResponseVo<Object> saveSale(SkuSaleVo skuSaleVo) {
        return ResponseVo.fail("保存营销信息失败");
    }
}
