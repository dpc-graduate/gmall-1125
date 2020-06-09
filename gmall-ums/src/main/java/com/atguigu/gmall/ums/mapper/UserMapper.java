package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表
 * 
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-06-09 15:17:20
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
	
}
