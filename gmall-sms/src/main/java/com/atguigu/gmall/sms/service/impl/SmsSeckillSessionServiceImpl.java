package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsSeckillSessionMapper;
import com.atguigu.gmall.sms.entity.SmsSeckillSessionEntity;
import com.atguigu.gmall.sms.service.SmsSeckillSessionService;


@Service("smsSeckillSessionService")
public class SmsSeckillSessionServiceImpl extends ServiceImpl<SmsSeckillSessionMapper, SmsSeckillSessionEntity> implements SmsSeckillSessionService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsSeckillSessionEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsSeckillSessionEntity>()
        );

        return new PageResultVo(page);
    }

}