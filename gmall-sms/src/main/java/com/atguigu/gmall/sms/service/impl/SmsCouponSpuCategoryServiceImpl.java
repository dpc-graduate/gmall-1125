package com.atguigu.gmall.sms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsCouponSpuCategoryMapper;
import com.atguigu.gmall.sms.entity.SmsCouponSpuCategoryEntity;
import com.atguigu.gmall.sms.service.SmsCouponSpuCategoryService;


@Service("smsCouponSpuCategoryService")
public class SmsCouponSpuCategoryServiceImpl extends ServiceImpl<SmsCouponSpuCategoryMapper, SmsCouponSpuCategoryEntity> implements SmsCouponSpuCategoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsCouponSpuCategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsCouponSpuCategoryEntity>()
        );

        return new PageResultVo(page);
    }

}