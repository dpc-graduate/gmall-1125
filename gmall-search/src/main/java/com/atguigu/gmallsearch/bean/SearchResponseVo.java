package com.atguigu.gmallsearch.bean;

import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/5/29,14:54
 */
@Data
public class SearchResponseVo {
    /**
     * 品牌集合
     */
    private List<BrandEntity> brands;
    /**
     * 分类
     */

    private List<CategoryEntity> categories;
    /**
     * 规格参数
     */
    private List<SearchResponseAttrVo> filters;
    /**
     * 分页数据
     */
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    /**
     * 当前页数据
     */
    private List<Goods> goodsList;

}
