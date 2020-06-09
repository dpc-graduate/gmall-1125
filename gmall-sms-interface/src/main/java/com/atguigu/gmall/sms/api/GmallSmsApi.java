package com.atguigu.gmall.sms.api;

import com.atguigu.gmall.common.bean.ResponseVo;

import com.atguigu.gmall.pms.entity.vo.SkuSaleVo;
import com.atguigu.gmall.pms.vo.ItemSaleVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/5/21,14:15
 */

public interface GmallSmsApi {
    @PostMapping("sms/smsskubounds/sku/sales")
    public ResponseVo<Object> saveSkuSales(@RequestBody SkuSaleVo skuSaleVo);

    @GetMapping("sms/smsskubounds/sku/{skuId}")
    public ResponseVo<List<ItemSaleVo>> querySaleVosBySkuId(@PathVariable("skuId")Long skuId);
}
