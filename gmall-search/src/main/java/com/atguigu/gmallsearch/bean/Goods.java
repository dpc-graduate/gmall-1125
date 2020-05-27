package com.atguigu.gmallsearch.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 一个goods对应一个sku
 *
 * @author: dpc
 * @data: 2020/5/27,9:37
 */
@Data
public class Goods {
    private Long skuId;
    private String title;
    private String subTitle;
    private String defaultImg;
    private BigDecimal price;
    /**
     * 品牌
     */
    private Long brandId;
    private String brandName;
    private String logo;
    /**
     * 分类
     */
    private Long categoryId;
    private String categoryName;
    /**
     * 规格属性
     */
    private List<SearchAttrVo> searchAttrs;

}
