package com.atguigu.gmall.wms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.entity.vo.SkuLockVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/5/27,11:43
 */
public interface GmallWmsApi {
    /**
     * 根据库存skuid查询库存信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("wms/waresku/sku/{skuId}")
    public ResponseVo<List<WareSkuEntity>> queryWareSkusBySkuId(@PathVariable("skuId") Long skuId);

    /**
     * 验证库存和锁库存，有锁定失败的情况下解锁所有被锁定的库存
     * @param lockVos
     * @param orderToken
     * @return
     */
    @PostMapping("wms/waresku/check/lock/{orderToken}")
    public ResponseVo<List<SkuLockVo>> checkAndLock(@RequestBody List<SkuLockVo> lockVos, @PathVariable("orderToken") String orderToken);
}
