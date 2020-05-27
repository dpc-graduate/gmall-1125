package com.atguigu.gmall.pms.entity.vo;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import lombok.Data;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/5/20,11:36
 */
@Data
public class SpuVo extends SpuEntity {
    private List<String> spuImages;
    private List<SpuAttrValueVo> baseAttrs;
    private List<SkuVo> skus;
}
