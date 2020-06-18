package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: dpc
 * @data: 2020/6/12,11:09
 */
@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("confirm")
    public String confirmOrder(Model model) {
        OrderConfirmVo confirmVo = this.orderService.confirm();
        model.addAttribute("confirmVo", confirmVo);
        return "trade";
    }
    @PostMapping("submit")
    @ResponseBody
    public ResponseVo<Object> submit(@RequestBody OrderSubmitVo submitVo){
        OrderEntity orderEntity = this.orderService.submit(submitVo);
        if (orderEntity!=null){
            return ResponseVo.ok(orderEntity.getOrderSn());
        }
      return ResponseVo.fail("服务器错误，订单创建失败");
    }
}
