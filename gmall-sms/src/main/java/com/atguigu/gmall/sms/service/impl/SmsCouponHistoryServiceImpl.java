package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsCouponHistoryMapper;
import com.atguigu.gmall.sms.entity.SmsCouponHistoryEntity;
import com.atguigu.gmall.sms.service.SmsCouponHistoryService;


@Service("smsCouponHistoryService")
public class SmsCouponHistoryServiceImpl extends ServiceImpl<SmsCouponHistoryMapper, SmsCouponHistoryEntity> implements SmsCouponHistoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsCouponHistoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsCouponHistoryEntity>()
        );

        return new PageResultVo(page);
    }

}