package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.sms.entity.SmsMemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 11:27:59
 */
public interface SmsMemberPriceService extends IService<SmsMemberPriceEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

