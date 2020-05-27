package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsSkuLadderMapper;
import com.atguigu.gmall.sms.entity.SmsSkuLadderEntity;
import com.atguigu.gmall.sms.service.SmsSkuLadderService;


@Service("smsSkuLadderService")
public class SmsSkuLadderServiceImpl extends ServiceImpl<SmsSkuLadderMapper, SmsSkuLadderEntity> implements SmsSkuLadderService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsSkuLadderEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsSkuLadderEntity>()
        );

        return new PageResultVo(page);
    }

}