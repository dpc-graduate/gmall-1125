package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsCouponMapper;
import com.atguigu.gmall.sms.entity.SmsCouponEntity;
import com.atguigu.gmall.sms.service.SmsCouponService;


@Service("smsCouponService")
public class SmsCouponServiceImpl extends ServiceImpl<SmsCouponMapper, SmsCouponEntity> implements SmsCouponService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsCouponEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsCouponEntity>()
        );

        return new PageResultVo(page);
    }

}