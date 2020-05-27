package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.sms.entity.SmsCouponSpuEntity;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 11:27:59
 */
public interface SmsCouponSpuService extends IService<SmsCouponSpuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

