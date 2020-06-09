package com.atguigu.gmall.item.service;

import com.atguigu.gmall.item.config.ThreadPoolConfig;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.ItemSaleVo;
import com.atguigu.gmall.pms.vo.ItemVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author: dpc
 * @data: 2020/6/8,21:52
 */
@Service
public class ItemService {
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    public ItemVo queryItemBySkuId(Long skuId) {
        ItemVo itemVo = new ItemVo();
        //sku的基本信息
        CompletableFuture<SkuEntity> skuEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuEntity skuEntity = pmsClient.querySkuById(skuId).getData();
            if (skuEntity == null) {
                return null;
            }
            itemVo.setSkuId(skuEntity.getId());
            itemVo.setTitle(skuEntity.getTitle());
            itemVo.setSubTitle(skuEntity.getSubtitle());
            itemVo.setPrice(skuEntity.getPrice());
            itemVo.setWeight(skuEntity.getWeight());
            itemVo.setDefaultImage(skuEntity.getDefaultImage());
            return skuEntity;
        }, threadPoolExecutor);
        //三级分类
        CompletableFuture<Void> categoryFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {

            List<CategoryEntity> categoryEntities = this.pmsClient.queryCategoriesByCid3(skuEntity.getCatagoryId()).getData();
            itemVo.setCategories(categoryEntities);
        }, threadPoolExecutor);
        //品牌信息
        CompletableFuture<Void> brandFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {

            BrandEntity brandEntity = this.pmsClient.queryBrandById(skuEntity.getBrandId()).getData();
            if (brandEntity != null) {
                itemVo.setBrandId(brandEntity.getId());
                itemVo.setBrandName(brandEntity.getName());
            }
        }, threadPoolExecutor);
        //spu信息
        CompletableFuture<Void> spuFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            SpuEntity spuEntity = pmsClient.querySpuById(skuEntity.getSpuId()).getData();

            if (spuEntity != null) {
                itemVo.setSpuId(spuEntity.getId());
                itemVo.setSpuName(spuEntity.getName());
            }
        }, threadPoolExecutor);

        //sku 图片信息
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImagesEntities = this.pmsClient.querySkuImages(skuId).getData();
            itemVo.setImages(skuImagesEntities);
        }, threadPoolExecutor);

        //促销信息
        CompletableFuture<Void> salesCompletableFuture = CompletableFuture.runAsync(() -> {

            List<ItemSaleVo> itemSaleVos = this.smsClient.querySaleVosBySkuId(skuId).getData();
            if (!CollectionUtils.isEmpty(itemSaleVos)) {
                itemVo.setSales(itemSaleVos);
            }
        }, threadPoolExecutor);
        //库存信息
        CompletableFuture<Void> storeFuture = CompletableFuture.runAsync(() -> {
            List<WareSkuEntity> wareSkuEntities = this.wmsClient.queryWareSkusBySkuId(skuId).getData();
            if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                itemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
            }
        }, threadPoolExecutor);
        //销售属性
        CompletableFuture<Void> saleAttrValue = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {

            List<SaleAttrValueVo> saleAttrValueVos = this.pmsClient.querySaleAttrBySpuId(skuEntity.getSpuId()).getData();
            itemVo.setSaleAttrs(saleAttrValueVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> SkuSalesFuture = CompletableFuture.runAsync(() -> {
            //当前sku的销售属性
            List<SkuAttrValueEntity> skuAttrValueEntities = this.pmsClient.querySaleAttrValueBySpuId(skuId).getData();
            if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                Map<Long, String> collect = skuAttrValueEntities.stream().collect(Collectors.toMap(SkuAttrValueEntity::getAttrId, SkuAttrValueEntity::getAttrValue));
                itemVo.setSaleAttr(collect);
            }
        }, threadPoolExecutor);


        CompletableFuture<Void> skuJsonCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            //spu所有销售属性和对应关系
            String data = this.pmsClient.querySkuJsonBySpuId(skuEntity.getSpuId()).getData();
            itemVo.setSkuJsons(data);
        }, threadPoolExecutor);

        CompletableFuture<Void> descCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            //spu的海报信息
            SpuDescEntity entity = this.pmsClient.querySpuDescById(skuEntity.getSpuId()).getData();
            if (entity != null) {
                String decript = entity.getDecript();
                itemVo.setSpuImages(Arrays.asList(StringUtils.split(decript, ",")));
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> groupCompletableFuture = skuEntityCompletableFuture.thenAcceptAsync(skuEntity -> {
            List<ItemGroupVo> groupVos = this.pmsClient.queryItemGroupByCiuAndSpuIdAndSkuId(skuEntity.getCatagoryId(), skuEntity.getSpuId(), skuId).getData();
            itemVo.setGroups(groupVos);
        }, threadPoolExecutor);
        CompletableFuture.allOf(categoryFuture,brandFuture,spuFuture,salesCompletableFuture,storeFuture,
                saleAttrValue,SkuSalesFuture,skuJsonCompletableFuture,descCompletableFuture,groupCompletableFuture).join();
        return itemVo;
    }
}
