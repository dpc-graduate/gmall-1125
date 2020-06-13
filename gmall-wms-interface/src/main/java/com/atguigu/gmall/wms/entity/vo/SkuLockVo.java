package com.atguigu.gmall.wms.entity.vo;

import lombok.Data;

/**
 * @author: dpc
 * @data: 2020/6/13,8:36
 */
@Data
public class SkuLockVo {
    //锁定的仓库id
    private Long wareSkuId;
    private Long skuId;
    private Integer count;
    private Boolean lock;
}
