package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsHomeSubjectSpuMapper;
import com.atguigu.gmall.sms.entity.SmsHomeSubjectSpuEntity;
import com.atguigu.gmall.sms.service.SmsHomeSubjectSpuService;


@Service("smsHomeSubjectSpuService")
public class SmsHomeSubjectSpuServiceImpl extends ServiceImpl<SmsHomeSubjectSpuMapper, SmsHomeSubjectSpuEntity> implements SmsHomeSubjectSpuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsHomeSubjectSpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsHomeSubjectSpuEntity>()
        );

        return new PageResultVo(page);
    }

}