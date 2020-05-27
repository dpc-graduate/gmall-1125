package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsSeckillPromotionMapper;
import com.atguigu.gmall.sms.entity.SmsSeckillPromotionEntity;
import com.atguigu.gmall.sms.service.SmsSeckillPromotionService;


@Service("smsSeckillPromotionService")
public class SmsSeckillPromotionServiceImpl extends ServiceImpl<SmsSeckillPromotionMapper, SmsSeckillPromotionEntity> implements SmsSeckillPromotionService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsSeckillPromotionEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsSeckillPromotionEntity>()
        );

        return new PageResultVo(page);
    }

}