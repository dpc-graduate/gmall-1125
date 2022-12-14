package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.entity.vo.SkuSaleVo;
import com.atguigu.gmall.pms.entity.vo.SkuVo;
import com.atguigu.gmall.pms.entity.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.entity.vo.SpuVo;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.SpuService;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {
    @Autowired
    private SpuDescMapper spuDescMapper;
    @Autowired
    private SpuAttrValueService spuAttrValueService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo queryPage(long categoryId, PageParamVo pageParamVo) {
        QueryWrapper<SpuEntity> wrapper = new QueryWrapper<>();
        if (categoryId != 0) {
            wrapper.eq("category_id", categoryId);
        }
        String key = pageParamVo.getKey();
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(t -> t.eq("id", key).or().like("name", key));
        }

        IPage<SpuEntity> page = this.page(
                pageParamVo.getPage(), wrapper);
        return new PageResultVo(page);
    }

    @Override
    @GlobalTransactional
    public void bigSave(SpuVo spuVo) {
        //1 ??????spu??????
        //??????spu????????? pms_spu
        spuVo.setCreateTime(new Date());
        spuVo.setUpdateTime(spuVo.getCreateTime());
        this.save(spuVo);
        Long spuVoId = spuVo.getId();
        //?????????????????? spu_desc
        List<String> list = spuVo.getSpuImages();
        if (!CollectionUtils.isEmpty(list)) {
            String join = StringUtils.join(list, ",");
            SpuDescEntity entity = new SpuDescEntity();
            entity.setSpuId(spuVoId);
            entity.setDecript(join);
            spuDescMapper.insert(entity);
        }
        //??????spu??????????????? spu_attr_value
        List<SpuAttrValueVo> baseAttrs = spuVo.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<SpuAttrValueEntity> spuAttrValueEntities = baseAttrs.stream().map(attr -> {
                SpuAttrValueEntity entity = new SpuAttrValueEntity();
                BeanUtils.copyProperties(attr, entity);
                entity.setSpuId(spuVoId);
                return entity;
            }).collect(Collectors.toList());
            this.spuAttrValueService.saveBatch(spuAttrValueEntities);
        }

        //2 ??????sku??????
        //sku?????? sku
        List<SkuVo> skuVos = spuVo.getSkus();
        if (CollectionUtils.isEmpty(skuVos)) {
            return;
        }
        //????????????
        skuVos.forEach(skuVo -> {
            //?????????????????????????????????
            skuVo.setSpuId(spuVoId);
            skuVo.setBrandId(spuVo.getBrandId());
            skuVo.setCatagoryId(spuVo.getCategoryId());
            List<String> images = spuVo.getSpuImages();
            if (!CollectionUtils.isEmpty(images)) {
                //?????????????????????????????????????????????????????????spu?????????????????????????????????
                skuVo.setDefaultImage(StringUtils.isNotBlank(skuVo.getDefaultImage()) ? skuVo.getDefaultImage() : images.get(0));
            }
            this.skuMapper.insert(skuVo);
            Long skuId = skuVo.getId();
            //??????sku???????????? sku_images
            if (!CollectionUtils.isEmpty(images)) {
                List<SkuImagesEntity> skuImagesEntities = images.stream().map(image -> {
                    SkuImagesEntity entity = new SkuImagesEntity();
                    entity.setSkuId(skuId);
                    entity.setUrl(image);
                    entity.setDefaultStatus(StringUtils.equals(image, skuVo.getDefaultImage()) ? 1 : 0);
                    return entity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);
            }
            //??????sku??????????????? sku_attr_value
            List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)) {
                saleAttrs.forEach(saleAttr -> {
                    saleAttr.setSkuId(skuId);
                });
                this.skuAttrValueService.saveBatch(saleAttrs);
            }
            //??????sku???????????????

            //??????????????????
            //??????????????????
            //??????????????????
            SkuSaleVo saleVo = new SkuSaleVo();
            saleVo.setSkuId(skuId);
            BeanUtils.copyProperties(skuVo, saleVo);
            this.gmallSmsClient.saveSale(saleVo);
            this.rabbitTemplate.convertAndSend("GMALL_ITEM_EXCHANGE","item.insert",spuVoId);
            System.out.println("???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");

        });

    }

}