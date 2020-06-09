package com.atguigu.gmall.pms.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/6/3,21:00
 */
@Data
public class ItemGroupVo {
    /**
     * 规格参数组名称
     */
    private String groupName;
    /**
     * 分组下的规格参数和值
     */
    private List<AttrValueVo> attrValues;
}
