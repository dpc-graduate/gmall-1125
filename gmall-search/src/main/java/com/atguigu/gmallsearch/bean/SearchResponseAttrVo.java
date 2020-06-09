package com.atguigu.gmallsearch.bean;

import lombok.Data;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/5/29,14:57
 */
@Data
public class SearchResponseAttrVo {
    private  Long attrId;
    private String attrName;
    private List<String> attrValues;
}
