package com.atguigu.gmall.sms.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: dpc
 * @data: 2020/5/21,10:24
 */
@Data
public class SkuSaleVo {
    private Long skuId;
    /**
     * 下面三个参数是积分优惠信息
     */
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;
    /**
     * 下面三个是sku的打折优惠信息
     */
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;
    /**
     * 下面三个是sku的满减优惠信息
     */
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;
}
