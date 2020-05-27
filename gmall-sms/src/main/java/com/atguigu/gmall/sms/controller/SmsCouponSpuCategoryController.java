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

import com.atguigu.gmall.sms.entity.SmsCouponSpuCategoryEntity;
import com.atguigu.gmall.sms.service.SmsCouponSpuCategoryService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 优惠券分类关联
 *
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 11:27:59
 */
@Api(tags = "优惠券分类关联 管理")
@RestController
@RequestMapping("sms/smscouponspucategory")
public class SmsCouponSpuCategoryController {

    @Autowired
    private SmsCouponSpuCategoryService smsCouponSpuCategoryService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySmsCouponSpuCategoryByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = smsCouponSpuCategoryService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SmsCouponSpuCategoryEntity> querySmsCouponSpuCategoryById(@PathVariable("id") Long id){
		SmsCouponSpuCategoryEntity smsCouponSpuCategory = smsCouponSpuCategoryService.getById(id);

        return ResponseVo.ok(smsCouponSpuCategory);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SmsCouponSpuCategoryEntity smsCouponSpuCategory){
		smsCouponSpuCategoryService.save(smsCouponSpuCategory);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SmsCouponSpuCategoryEntity smsCouponSpuCategory){
		smsCouponSpuCategoryService.updateById(smsCouponSpuCategory);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		smsCouponSpuCategoryService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
