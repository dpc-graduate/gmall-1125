package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.entity.Cart;
import com.atguigu.gmall.cart.mapper.CartMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/6/11,20:31
 */
@Service
public class CartAsyncService {
    @Autowired
    private CartMapper cartMapper;

    @Async
    public void updateCardByUserIdAndSkuId(Cart cart, String userId) {
        this.cartMapper.update(cart, new QueryWrapper<Cart>().eq("user_id", userId).eq("sku_id", cart.getSkuId()));
    }

    @Async
    public void saveCart(Cart cart) {
        this.cartMapper.insert(cart);
    }

    public void deleteCart(String userKey) {
        this.cartMapper.delete(new QueryWrapper<Cart>().eq("user_id", userKey));
    }

    @Async
    public void deleteCartByUserIdAndSkuId(String userId, Long skuId) {
        this.cartMapper.delete(new QueryWrapper<Cart>().eq("sku_id", skuId).eq("user_id", userId));
    }
    @Async
    public void batchDeleteCart(String userId, List<Long> skuIds) {
        this.cartMapper.delete(new QueryWrapper<Cart>().eq("user_id",userId).in("sku_id",skuIds));
    }
}
