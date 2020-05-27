package com.atguigu.gmall.sms.mapper;

import com.atguigu.gmall.sms.entity.SmsCouponSpuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 11:27:59
 */
@Mapper
public interface SmsCouponSpuMapper extends BaseMapper<SmsCouponSpuEntity> {
	
}
