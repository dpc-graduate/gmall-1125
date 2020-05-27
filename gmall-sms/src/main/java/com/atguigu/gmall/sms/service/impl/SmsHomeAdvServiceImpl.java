package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsHomeAdvMapper;
import com.atguigu.gmall.sms.entity.SmsHomeAdvEntity;
import com.atguigu.gmall.sms.service.SmsHomeAdvService;


@Service("smsHomeAdvService")
public class SmsHomeAdvServiceImpl extends ServiceImpl<SmsHomeAdvMapper, SmsHomeAdvEntity> implements SmsHomeAdvService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsHomeAdvEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsHomeAdvEntity>()
        );

        return new PageResultVo(page);
    }

}