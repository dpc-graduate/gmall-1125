package com.atguigu.gmall.pms.entity.vo;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: dpc
 * @data: 2020/5/20,13:49
 */
@Data
public class SkuVo extends SkuEntity {
    /**
     * 接受sku的图片列表
     */
    private List<String> images;
    private List<SkuAttrValueEntity> saleAttrs;
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
