package com.atguigu.gmallsearch.bean;

import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/5/29,13:18
 */
@Data
public class SearchParamVo {
    /**
     * 搜索框
     */
    private String keyword;
    /**
     * 品牌过滤条件
     */
    private List<Long> brandId;
    /**
     * 分类过滤条件
     */
    private List<Long> cid;
    /**
     * 规格参数的过滤条件
     * ["2:128G-256G","3:8G-16G"]
     */
    private List<String> props;
    /**
     * 排列条件
     * 默认得分排序，1 价格降序、2价格升序、3销量排序、4新品降序
     */
    private Integer sort;
    /**
     * 价格区间
     */
    private Integer priceFrom;
    private Integer priceTo;
    /**
     * 接受分页参数
     */
    private Integer pageNum = 1;
    private final Integer pageSize = 20;
    /**
     * 是否有货
     */
    private Boolean store;

}
