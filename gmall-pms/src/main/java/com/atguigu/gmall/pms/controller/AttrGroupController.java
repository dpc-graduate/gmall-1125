package com.atguigu.gmall.pms.controller;

import java.util.List;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.vo.GroupVo;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * 属性分组
 *
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-18 10:57:54
 */
@Api(tags = "属性分组 管理")
@RestController
@RequestMapping("pms/attrgroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryAttrGroupByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = attrGroupService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<AttrGroupEntity> queryAttrGroupById(@PathVariable("id") Long id) {
        AttrGroupEntity attrGroup = attrGroupService.getById(id);

        return ResponseVo.ok(attrGroup);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        attrGroupService.removeByIds(ids);

        return ResponseVo.ok();
    }

    @GetMapping("category/{id}")
    @ApiOperation("查询商品分组属性")
    public ResponseVo<List<AttrGroupEntity>> queryGroupById(@PathVariable("id") Long id) {
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.queryGroupById(id);
        return ResponseVo.ok(attrGroupEntities);
    }

    @GetMapping("withattrs/{cid}")
    public ResponseVo<List<GroupVo>> queryForGroup(@PathVariable("cid") Long cid) {
        List<GroupVo> groupVos=  this.attrGroupService.queryForGroup(cid);
        return ResponseVo.ok(groupVos);
    }
    @GetMapping("item/group")
    public ResponseVo<List<ItemGroupVo>> queryItemGroupByCiuAndSpuIdAndSkuId(
            @RequestParam("cid") Long cid,
            @RequestParam("spuId") Long spuId,
            @RequestParam("skuId") Long skuId){
        List<ItemGroupVo> itemGroupVos=this.attrGroupService.queryItemGroupByCiuAndSpuIdAndSkuId(cid,spuId,skuId);
        return ResponseVo.ok(itemGroupVos);
    }
}
