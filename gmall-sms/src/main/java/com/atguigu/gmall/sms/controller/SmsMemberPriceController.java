package com.atguigu.gmall.sms.controller;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.sms.entity.SmsMemberPriceEntity;
import com.atguigu.gmall.sms.service.SmsMemberPriceService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 商品会员价格
 *
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 11:27:59
 */
@Api(tags = "商品会员价格 管理")
@RestController
@RequestMapping("sms/smsmemberprice")
public class SmsMemberPriceController {

    @Autowired
    private SmsMemberPriceService smsMemberPriceService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySmsMemberPriceByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = smsMemberPriceService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SmsMemberPriceEntity> querySmsMemberPriceById(@PathVariable("id") Long id){
		SmsMemberPriceEntity smsMemberPrice = smsMemberPriceService.getById(id);

        return ResponseVo.ok(smsMemberPrice);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SmsMemberPriceEntity smsMemberPrice){
		smsMemberPriceService.save(smsMemberPrice);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SmsMemberPriceEntity smsMemberPrice){
		smsMemberPriceService.updateById(smsMemberPrice);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		smsMemberPriceService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
