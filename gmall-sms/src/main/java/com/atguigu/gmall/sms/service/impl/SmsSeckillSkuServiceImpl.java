package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsSeckillSkuMapper;
import com.atguigu.gmall.sms.entity.SmsSeckillSkuEntity;
import com.atguigu.gmall.sms.service.SmsSeckillSkuService;


@Service("smsSeckillSkuService")
public class SmsSeckillSkuServiceImpl extends ServiceImpl<SmsSeckillSkuMapper, SmsSeckillSkuEntity> implements SmsSeckillSkuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsSeckillSkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsSeckillSkuEntity>()
        );

        return new PageResultVo(page);
    }

}