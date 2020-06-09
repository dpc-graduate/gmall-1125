package com.atguigu.gmall.sms.service;


import com.atguigu.gmall.pms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.entity.vo.SkuSaleVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.sms.entity.SmsSkuBoundsEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 11:27:59
 */
public interface SmsSkuBoundsService extends IService<SmsSkuBoundsEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    void saveSkuSales(SkuSaleVo skuSaleVo);

    List<ItemSaleVo> querySaleVoBySkuId(Long skuId);
}

