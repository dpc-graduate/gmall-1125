package com.atguigu.gmall.sms.mapper;

import com.atguigu.gmall.sms.entity.SmsCouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 11:27:59
 */
@Mapper
public interface SmsCouponHistoryMapper extends BaseMapper<SmsCouponHistoryEntity> {
	
}
