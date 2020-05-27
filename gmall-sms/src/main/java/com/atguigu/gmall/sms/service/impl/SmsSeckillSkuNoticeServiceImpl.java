package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsSeckillSkuNoticeMapper;
import com.atguigu.gmall.sms.entity.SmsSeckillSkuNoticeEntity;
import com.atguigu.gmall.sms.service.SmsSeckillSkuNoticeService;


@Service("smsSeckillSkuNoticeService")
public class SmsSeckillSkuNoticeServiceImpl extends ServiceImpl<SmsSeckillSkuNoticeMapper, SmsSeckillSkuNoticeEntity> implements SmsSeckillSkuNoticeService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsSeckillSkuNoticeEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsSeckillSkuNoticeEntity>()
        );

        return new PageResultVo(page);
    }

}