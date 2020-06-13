package com.atguigu.gmall.cart.controller;


import com.atguigu.gmall.cart.entity.Cart;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.bean.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author: dpc
 * @data: 2020/6/10,11:31
 */
@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("test")
    @ResponseBody
    public String test() throws ExecutionException, InterruptedException {
//        UserInfo userInfo = LoginInterceptor.getUserInfo();
//        System.err.println(userInfo);
//        System.out.println("目标方法正在执行");
        long currentTimeMillis = System.currentTimeMillis();
//        ListenableFuture<String> stringFuture = cartService.execute1();
//        ListenableFuture<String> stringFuture1 = cartService.execute2();
//        System.out.println(stringFuture.get()+stringFuture1.get());
//        stringFuture.addCallback(s -> System.out.println("方法去1执行成功："+s),ex-> System.out.println("方法1执行失败："+ex));
//        stringFuture1.addCallback(s -> System.out.println("方法去2执行成功："+s),ex-> System.out.println("方法2执行失败："+ex));
        ListenableFuture<String> s = cartService.execute1();
        s.addCallback(a -> System.out.println(a), e -> System.out.println(e.getMessage()));
        String s1 = cartService.execute2();
        System.err.println(s + s1);
        System.out.println("执行两个方法花费了：" + (System.currentTimeMillis() - currentTimeMillis) + "毫秒");
        return "hello";
    }

    @GetMapping
    public String addCart(Cart cart) {
        cartService.addCart(cart);
        return "redirect:http://cart.gmall.com/addCart.html?skuId=" + cart.getSkuId();
    }

    @GetMapping("addCart.html")
    public String addCart(@RequestParam("skuId") Long skuId, Model model) {
        Cart cart = cartService.queryCartBySkuId(skuId);
        model.addAttribute("cart", cart);
        return "addCart";
    }

    @GetMapping("cart.html")
    public String queryCarts(Model model) {
        List<Cart> cartList = this.cartService.queryCarts();
        model.addAttribute("carts", cartList);
        return "cart";
    }

    @PostMapping("updateNum")
    @ResponseBody
    public ResponseVo<Object> updateNum(@RequestBody Cart cart) {
        this.cartService.updateNum(cart);
        return ResponseVo.ok();
    }

    @PostMapping("deleteCart")
    @ResponseBody
    public ResponseVo<Object> deleteCart(@RequestParam("skuId") Long skuId) {
        this.cartService.deleteCart(skuId);
        return ResponseVo.ok();

    }

    @GetMapping("query/{userId}")
    @ResponseBody
    public ResponseVo<List<Cart>> queryCheckedCartsByUserId(@PathVariable("userId") Long userId) {
        List<Cart> carts = this.cartService.queryCheckedCartsByUserId(userId);
        return ResponseVo.ok(carts);
    }
}
