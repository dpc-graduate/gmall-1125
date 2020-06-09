package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author: dpc
 * @data: 2020/6/3,20:11
 */
@Data
public class ItemVo {
    /**
     * 面包屑的三级分类
     */
    private List<CategoryEntity> categories;
    /**
     * 品牌信息
     */
    private Long brandId;

    private String brandName;
    private Long spuId;
    private String spuName;
    /**
     * 中间 sku信息
     */
    private Long skuId;
    private String title;
    private String subTitle;
    private BigDecimal price;
    private Integer weight;
    private String defaultImage;
    /**
     * sku 图片列表
     */
    private List<SkuImagesEntity> images;
    /**
     * sku 的促销
     */
    private List<ItemSaleVo> sales;
    /**
     * 库存
     */
    private Boolean store=false;
    /**
     * spu销售属性组合
     */
    private List<SaleAttrValueVo> saleAttrs;
    /**
     * 当前sku的销售属性
     */
    private Map<Long,String> saleAttr;
    /**
     *  销售属性组合和skuId的对应关系
     */
    private String skuJsons;
    /**
     * 商品详情 spu的图片列表
     */
    private List<String> spuImages;
    /**
     * 规格参数组及 规格参数和值
     */
    private List<ItemGroupVo> groups;
}
