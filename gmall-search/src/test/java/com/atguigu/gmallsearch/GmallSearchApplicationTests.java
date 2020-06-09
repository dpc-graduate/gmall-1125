package com.atguigu.gmallsearch;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmallsearch.bean.Goods;
import com.atguigu.gmallsearch.bean.SearchAttrVo;
import com.atguigu.gmallsearch.feign.GmallPmsClient;
import com.atguigu.gmallsearch.feign.GmallWmsClient;
import com.atguigu.gmallsearch.respository.GoodsRepository;
import io.swagger.models.auth.In;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {
    @Autowired
    private ElasticsearchRestTemplate restTemplate;
    @Autowired
    private GoodsRepository repository;
    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Test
    void contextLoads() {
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
    }

    @Test
    public void test() {
        //定义分页参数
        Integer pageNum = 1;
        Integer PageSize = 100;
        do {//分批获取spu
            ResponseVo<List<SpuEntity>> spuResponseVo = gmallPmsClient.querySpuPage(new PageParamVo(pageNum, PageSize, null));
            List<SpuEntity> spuEntities = spuResponseVo.getData();
            //如果最后一页是100条，会再查询一次
            if (CollectionUtils.isEmpty(spuEntities)) {
                return;
            }
            //导入到索引库
            for (SpuEntity spuEntity : spuEntities) {//根据spuid查询所有sku信息
                ResponseVo<List<SkuEntity>> skuResponseVo = gmallPmsClient.querySkusBySpuId(spuEntity.getId());
                //sku信息
                List<SkuEntity> skuEntities = skuResponseVo.getData();
                if (!CollectionUtils.isEmpty(skuEntities)) {
                    List<Goods> goodsList = skuEntities.stream().map(skuEntity -> {
                        //把skuEntities转换成goodsList
                        Goods goods = new Goods();
                        //设置 good基本信息 从sku直接赋值
                        goods.setSkuId(skuEntity.getId());
                        goods.setTitle(skuEntity.getTitle());
                        goods.setSubTitle(skuEntity.getSubtitle());
                        goods.setDefaultImage(skuEntity.getDefaultImage());
                        goods.setPrice(skuEntity.getPrice());
                        goods.setCreateTime(spuEntity.getCreateTime());
                        //品牌
                        ResponseVo<BrandEntity> brandEntityResponseVo = this.gmallPmsClient.queryBrandById(skuEntity.getBrandId());
                        BrandEntity brandEntity = brandEntityResponseVo.getData();
                        if (brandEntity != null) {
                            goods.setBrandId(skuEntity.getBrandId());
                            goods.setBrandName(brandEntity.getName());
                            goods.setLogo(brandEntity.getLogo());
                        }
                        //分类
                        CategoryEntity categoryEntity = this.gmallPmsClient.queryCategoryById(skuEntity.getCatagoryId()).getData();
                        if (categoryEntity != null) {
                            goods.setCategoryId(skuEntity.getCatagoryId());
                            goods.setCategoryName(categoryEntity.getName());
                        }
                        List<WareSkuEntity> wareSkuEntities = this.gmallWmsClient.queryWareSkusBySkuId(skuEntity.getId()).getData();
                        //只要有一个仓库的库存大于0就说明有货
                        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                            goods.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
                            goods.setSales(wareSkuEntities.stream().map(wareSkuEntity -> wareSkuEntity.getSales()).reduce((a, b) -> a + b).get());
                        }
                        List<SearchAttrVo> searchAttrs = new ArrayList<>();
                        //通用属性 检索参数
                        List<SpuAttrValueEntity> spuAttrValueEntities = gmallPmsClient.querySpuAttrValuesBySpuId(spuEntity.getId()).getData();

                        //销售属性 检索参数
                        List<SkuAttrValueEntity> skuAttrValueEntities = gmallPmsClient.querySkuAttrValuesBySkuId(skuEntity.getId()).getData();
                        if (!CollectionUtils.isEmpty(spuAttrValueEntities)) {
                            List<SearchAttrVo> searchAttrVoList = spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                                SearchAttrVo searchAttrVo = new SearchAttrVo();
                                BeanUtils.copyProperties(spuAttrValueEntity, searchAttrVo);
                                return searchAttrVo;
                            }).collect(Collectors.toList());
                            searchAttrs.addAll(searchAttrVoList);
                        }
                        if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                            List<SearchAttrVo> searchAttrVoList = skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                                        SearchAttrVo searchAttrVo = new SearchAttrVo();
                                        BeanUtils.copyProperties(skuAttrValueEntity, searchAttrVo);
                                        return searchAttrVo;
                                    }
                            ).collect(Collectors.toList());
                            searchAttrs.addAll(searchAttrVoList);
                        }
                        goods.setSearchAttrs(searchAttrs);
                        return goods;
                    }).collect(Collectors.toList());
                    //批量插入
                    this.repository.saveAll(goodsList);
                }
            }
            pageNum++;
            //获取当前页记录数
            PageSize = spuEntities.size();
        }
        while (PageSize == 100);
    }
}
