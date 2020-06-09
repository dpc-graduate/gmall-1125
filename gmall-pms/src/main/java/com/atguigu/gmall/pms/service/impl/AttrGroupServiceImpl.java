package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.vo.GroupVo;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.mapper.SpuAttrValueMapper;
import com.atguigu.gmall.pms.vo.AttrValueVo;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    private AttrMapper attrMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SpuAttrValueMapper spuAttrValueMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrGroupEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<AttrGroupEntity> queryGroupById(Long id) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", id);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<GroupVo> queryForGroup(Long cid) {
        //先查询组
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("category_id", cid));
        if (CollectionUtils.isEmpty(groupEntities)) {
            return null;
        }

        //遍历组，查询每个组下面的属性
        return groupEntities.stream().map(groupEntity -> {
            GroupVo groupVo = new GroupVo();
            BeanUtils.copyProperties(groupEntity, groupVo);
            List<AttrEntity> entities = this.attrMapper.selectList(new QueryWrapper<AttrEntity>().eq("group_id", groupEntity.getId()).eq("type", 1));
            groupVo.setAttrEntities(entities);
            return groupVo;
        }).collect(Collectors.toList());
    }


    @Override
    public List<ItemGroupVo> queryItemGroupByCiuAndSpuIdAndSkuId(Long cid, Long spuId, Long skuId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("category_id", cid));
        if (CollectionUtils.isEmpty(attrGroupEntities)) {
            return null;
        }
        return attrGroupEntities.stream().map(attrGroupEntity -> {
            ItemGroupVo groupVo = new ItemGroupVo();
            groupVo.setGroupName(attrGroupEntity.getName());
            ArrayList<AttrValueVo> list = new ArrayList<>();
            //先查询当前组下的基本属性和值
            List<SpuAttrValueEntity> spuAttrValueEntityList=this.spuAttrValueMapper.querySpuAttrValueBySpuIdAndGId(spuId,attrGroupEntity.getId());
            if (!CollectionUtils.isEmpty(spuAttrValueEntityList)){
                list.addAll(spuAttrValueEntityList.stream().map(spuAttrValueEntity -> {
                    AttrValueVo valueVo = new AttrValueVo();
                    BeanUtils.copyProperties(spuAttrValueEntity,valueVo);
                    return valueVo;
                }).collect(Collectors.toList()));
            }
            //再查询销售属性的属性和值
            List<SkuAttrValueEntity> skuAttrValueEntities=this.skuAttrValueMapper.querySpuAttrValueBySKuIdAndGId(skuId,attrGroupEntity.getId());
            if (!CollectionUtils.isEmpty(skuAttrValueEntities)){
                list.addAll(skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                    AttrValueVo valueVo = new AttrValueVo();
                    BeanUtils.copyProperties(skuAttrValueEntity,valueVo);
                    return valueVo;
                }).collect(Collectors.toList()));
            }
            groupVo.setAttrValues(list);
            return groupVo;
        }).collect(Collectors.toList());
    }
}