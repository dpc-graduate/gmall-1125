package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsCouponSpuMapper;
import com.atguigu.gmall.sms.entity.SmsCouponSpuEntity;
import com.atguigu.gmall.sms.service.SmsCouponSpuService;


@Service("smsCouponSpuService")
public class SmsCouponSpuServiceImpl extends ServiceImpl<SmsCouponSpuMapper, SmsCouponSpuEntity> implements SmsCouponSpuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsCouponSpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsCouponSpuEntity>()
        );

        return new PageResultVo(page);
    }

}