package com.atguigu.gmall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import org.springframework.util.CollectionUtils;
import springfox.documentation.spring.web.json.Json;


@Service("skuAttrValueService")
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValueEntity> implements SkuAttrValueService {
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<SkuAttrValueEntity> querySkuAttrValueEntity(long skuId) {
        return skuAttrValueMapper.querySkuAttrValueEntity(skuId);
    }

    @Override
    public List<SaleAttrValueVo> querySaleAttrBySpuId(Long spuId) {
        List<SkuAttrValueEntity> skuAttrValueEntities = this.skuAttrValueMapper.querySaleAttrBySpuId(spuId);
        List<SaleAttrValueVo> saleAttrValueVos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
//            Map<Long, List<SkuAttrValueEntity>> collect = skuAttrValueEntities.stream().collect(Collectors.groupingBy(skuAttrValueEntity -> skuAttrValueEntity.getAttrId()));
            //对集合以attrId进行分组，是一个map  key是attrId
            Map<Long, List<SkuAttrValueEntity>> collect = skuAttrValueEntities.stream().collect(Collectors.groupingBy(SkuAttrValueEntity::getAttrId));
            collect.forEach((attrId, skuAttrValueEntities1) -> {
                if (!CollectionUtils.isEmpty(skuAttrValueEntities1)) {
                    SaleAttrValueVo valueVo = new SaleAttrValueVo();
                    valueVo.setAttrId(attrId);
                    valueVo.setAttrName(skuAttrValueEntities1.get(0).getAttrName());
                    valueVo.setAttrValues(skuAttrValueEntities1.stream().map(SkuAttrValueEntity::getAttrValue).collect(Collectors.toSet()));
                    saleAttrValueVos.add(valueVo);
                }
            });
        }

        return saleAttrValueVos;
    }

    @Override
    public String querySkuJsonBySpuId(Long spuId) {
        List<Map<String, Object>> skuJson = skuAttrValueMapper.querySkuJsonBySpuId(spuId);
        Map<String, Long> attrValueMap = skuJson.stream().collect(Collectors.toMap(map->map.get("attr_values").toString(),map->(Long)map.get("sku_id")));
        return JSON.toJSONString(attrValueMap);
    }

}