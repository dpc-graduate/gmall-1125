package com.atguigu.gmall.oms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: dpc
 * @data: 2020/6/13,10:50
 */
public interface GmallOmsApi {
    /**
     * 新增订单和订单详情
     * @param submitVo
     * @return
     */
    @PostMapping("oms/order/save/order")
    public ResponseVo<OrderEntity> saveOrder(@RequestBody OrderSubmitVo submitVo);
}
