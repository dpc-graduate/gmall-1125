package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsHomeSubjectMapper;
import com.atguigu.gmall.sms.entity.SmsHomeSubjectEntity;
import com.atguigu.gmall.sms.service.SmsHomeSubjectService;


@Service("smsHomeSubjectService")
public class SmsHomeSubjectServiceImpl extends ServiceImpl<SmsHomeSubjectMapper, SmsHomeSubjectEntity> implements SmsHomeSubjectService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsHomeSubjectEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsHomeSubjectEntity>()
        );

        return new PageResultVo(page);
    }

}