package com.atguigu.gmall.wms.mapper;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-20 09:42:52
 */
@Mapper
public interface WareSkuMapper extends BaseMapper<WareSkuEntity> {
    /**
     * 查询仓库列表
     * @param skuId
     * @param count
     * @return
     */
    List<WareSkuEntity> check(@Param("skuId") Long skuId,@Param("count") Integer count);

    /**
     * 更新仓库
     * @param id
     * @param count
     * @return
     */
    int lock(@Param("id")Long id,@Param("count")Integer count);

    /**
     * 解锁
     * @param id
     * @param count
     * @return
     */
    int unlock(@Param("id")Long id,@Param("count")Integer count);

	
}
