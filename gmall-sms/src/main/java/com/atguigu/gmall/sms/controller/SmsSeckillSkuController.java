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

import com.atguigu.gmall.sms.entity.SmsSeckillSkuEntity;
import com.atguigu.gmall.sms.service.SmsSeckillSkuService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 秒杀活动商品关联
 *
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 11:27:59
 */
@Api(tags = "秒杀活动商品关联 管理")
@RestController
@RequestMapping("sms/smsseckillsku")
public class SmsSeckillSkuController {

    @Autowired
    private SmsSeckillSkuService smsSeckillSkuService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySmsSeckillSkuByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = smsSeckillSkuService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SmsSeckillSkuEntity> querySmsSeckillSkuById(@PathVariable("id") Long id){
		SmsSeckillSkuEntity smsSeckillSku = smsSeckillSkuService.getById(id);

        return ResponseVo.ok(smsSeckillSku);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SmsSeckillSkuEntity smsSeckillSku){
		smsSeckillSkuService.save(smsSeckillSku);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SmsSeckillSkuEntity smsSeckillSku){
		smsSeckillSkuService.updateById(smsSeckillSku);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		smsSeckillSkuService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
