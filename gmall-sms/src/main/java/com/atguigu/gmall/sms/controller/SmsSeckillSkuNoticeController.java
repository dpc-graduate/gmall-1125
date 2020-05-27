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

import com.atguigu.gmall.sms.entity.SmsSeckillSkuNoticeEntity;
import com.atguigu.gmall.sms.service.SmsSeckillSkuNoticeService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 秒杀商品通知订阅
 *
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 11:27:59
 */
@Api(tags = "秒杀商品通知订阅 管理")
@RestController
@RequestMapping("sms/smsseckillskunotice")
public class SmsSeckillSkuNoticeController {

    @Autowired
    private SmsSeckillSkuNoticeService smsSeckillSkuNoticeService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySmsSeckillSkuNoticeByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = smsSeckillSkuNoticeService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SmsSeckillSkuNoticeEntity> querySmsSeckillSkuNoticeById(@PathVariable("id") Long id){
		SmsSeckillSkuNoticeEntity smsSeckillSkuNotice = smsSeckillSkuNoticeService.getById(id);

        return ResponseVo.ok(smsSeckillSkuNotice);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SmsSeckillSkuNoticeEntity smsSeckillSkuNotice){
		smsSeckillSkuNoticeService.save(smsSeckillSkuNotice);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SmsSeckillSkuNoticeEntity smsSeckillSkuNotice){
		smsSeckillSkuNoticeService.updateById(smsSeckillSkuNotice);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		smsSeckillSkuNoticeService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
