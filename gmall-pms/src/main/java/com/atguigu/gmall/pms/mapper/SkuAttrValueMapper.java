package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 * 
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 10:57:54
 */
@Mapper
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValueEntity> {

    List<SkuAttrValueEntity> querySkuAttrValueEntity(long skuId);

    List<SkuAttrValueEntity> querySaleAttrBySpuId(Long spuId);

    List<Map<String, Object>> querySkuJsonBySpuId(Long spuId);

    List<SkuAttrValueEntity> querySpuAttrValueBySKuIdAndGId(@Param("skuId") Long skuId, @Param("groupId") Long id);
}
