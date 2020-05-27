package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsSkuFullReductionMapper;
import com.atguigu.gmall.sms.entity.SmsSkuFullReductionEntity;
import com.atguigu.gmall.sms.service.SmsSkuFullReductionService;


@Service("smsSkuFullReductionService")
public class SmsSkuFullReductionServiceImpl extends ServiceImpl<SmsSkuFullReductionMapper, SmsSkuFullReductionEntity> implements SmsSkuFullReductionService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsSkuFullReductionEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsSkuFullReductionEntity>()
        );

        return new PageResultVo(page);
    }

}