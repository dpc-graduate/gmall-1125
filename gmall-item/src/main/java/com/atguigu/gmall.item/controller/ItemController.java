package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.pms.vo.ItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: dpc
 * @data: 2020/6/8,21:51
 */
@Controller
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping("{skuId}.html")
    public String item(@PathVariable Long skuId, Model model) {
        ItemVo itemVo = this.itemService.queryItemBySkuId(skuId);
        if (itemVo != null) {
            model.addAttribute("itemVo", itemVo);
            return "item";
        }
        else {
            model.addAttribute("msg","没有改商品");
            return "sorry";

        }
    }
}
