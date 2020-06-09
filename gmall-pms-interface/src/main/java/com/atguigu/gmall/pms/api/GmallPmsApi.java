package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: dpc
 * @data: 2020/5/27,11:35
 */
public interface GmallPmsApi {
    /**
     * 分页查询spu
     *
     * @param paramVo
     * @return
     */
    @PostMapping("pms/spu/page")
    public ResponseVo<List<SpuEntity>> querySpuPage(@RequestBody PageParamVo paramVo);

    /**
     * 根据spuId查询sku
     *
     * @param spuId
     * @return
     */
    @GetMapping("pms/sku/spu/{spuId}")
    public ResponseVo<List<SkuEntity>> querySkusBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据品牌id查询品牌
     *
     * @param id
     * @return
     */
    @GetMapping("pms/brand/{id}")
    public ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    /**
     * 根据分类id查询分类
     *
     * @param id
     * @return
     */
    @GetMapping("pms/category/{id}")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    /**
     * 根据skuId查询搜索类型的销售属性及值
     *
     * @param skuId
     * @return
     */
    @GetMapping("pms/skuattrvalue/sku/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySkuAttrValuesBySkuId(@PathVariable("skuId") Long skuId);

    /**
     * 根据spuId查询搜索类型的基本属性及值
     *
     * @param spuId
     * @return
     */
    @GetMapping("pms/spuattrvalue/spu/{spuId}")
    public ResponseVo<List<SpuAttrValueEntity>> querySpuAttrValuesBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据spuid查询spu
     *
     * @param id
     * @return
     */
    @GetMapping("pms/spu/{id}")
    ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    /**
     * 根据父id查询子分类
     *
     * @param id
     * @return
     */
    @GetMapping("pms/category/parent/{id}")
    public ResponseVo<List<CategoryEntity>> queryCategoriesByPid(@PathVariable("id") Long id);

    /**
     * 根据一级分类id 查询二级分类和三级分类
     *
     * @param pid
     * @return
     */
    @GetMapping("pms/category/parent/with/subs/{pid}")
    ResponseVo<List<CategoryEntity>> queryCategoriesSubByPid(@PathVariable Long pid);

    /**
     * 根据skuId查询sku
     */
    @GetMapping("pms/sku/{id}")
    public ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);

    /**
     * 根据三级分类id 查询1 2 3级分类
     */
    @GetMapping("pms/category/all/{cid3}")
    public ResponseVo<List<CategoryEntity>> queryCategoriesByCid3(@PathVariable Long cid3);

    /**
     * 根据skuid 查询sku的图片列表
     *
     * @param skuId
     * @return
     */
    @GetMapping("pms/skuimages/sku/{skuId}")
    public ResponseVo<List<SkuImagesEntity>> querySkuImages(@PathVariable Long skuId);

    /**
     * 根据spuid查询对应所有sku的销售属性
     *
     * @param spuId
     * @return
     */
    @GetMapping("pms/skuattrvalue/spu/{spuId}")
    public ResponseVo<List<SaleAttrValueVo>> querySaleAttrBySpuId(@PathVariable Long spuId);

    /**
     * 根据一组销售属性来确定sku的id
     */
    @GetMapping("pms/skuattrvalue/sku/spu/{spuId}")
    public ResponseVo<String> querySkuJsonBySpuId(@PathVariable Long spuId);

    /**
     * 根据spuId查询spu的描述信息
     * @param spuId
     * @return
     */
    @GetMapping("pms/spudesc/{spuId}")
    @ApiOperation("详情查询")
    public ResponseVo<SpuDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);

    /**
     *  查询sku的销售属性和基本属性
     * @return
     */
    @GetMapping("pms/attrgroup/item/group")
    public ResponseVo<List<ItemGroupVo>> queryItemGroupByCiuAndSpuIdAndSkuId(
            @RequestParam("cid") Long cid,
            @RequestParam("spuId") Long spuId,
            @RequestParam("skuId") Long skuId);

    /**
     * 查询所有的销售属性
     * @param skuId
     * @return
     */
    @GetMapping("pms/skuattrvalue/sku/sale/{skuId}")
    public ResponseVo<List<SkuAttrValueEntity>> querySaleAttrValueBySpuId(@PathVariable Long skuId);
}
