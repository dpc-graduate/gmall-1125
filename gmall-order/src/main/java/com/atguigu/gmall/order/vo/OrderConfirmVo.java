package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.oms.vo.OrderItemVo;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import lombok.Data;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/6/12,10:54
 */
@Data
public class OrderConfirmVo {
    private List<UserAddressEntity> addresses;
    private List<OrderItemVo> orderItems;
    private Integer bounds;
    /**
     * 防止订单重复提交，确保下单的幂等性
     */
    private String orderToken;
}
