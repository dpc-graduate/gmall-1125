package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.entity.Cart;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/6/12,11:31
 */
public interface GmallCartApi {
    /**
     * 查询选中状态的购物车
     * @param userId
     * @return
     */
    @GetMapping("query/{userId}")
     ResponseVo<List<Cart>> queryCheckedCartsByUserId(@PathVariable("userId") Long userId);
}
