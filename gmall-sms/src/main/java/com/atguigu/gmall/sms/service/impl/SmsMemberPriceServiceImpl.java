package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsMemberPriceMapper;
import com.atguigu.gmall.sms.entity.SmsMemberPriceEntity;
import com.atguigu.gmall.sms.service.SmsMemberPriceService;


@Service("smsMemberPriceService")
public class SmsMemberPriceServiceImpl extends ServiceImpl<SmsMemberPriceMapper, SmsMemberPriceEntity> implements SmsMemberPriceService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsMemberPriceEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsMemberPriceEntity>()
        );

        return new PageResultVo(page);
    }

}