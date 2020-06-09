package com.atguigu.gmallsearch.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmallsearch.bean.Goods;
import com.atguigu.gmallsearch.bean.SearchParamVo;
import com.atguigu.gmallsearch.bean.SearchResponseAttrVo;
import com.atguigu.gmallsearch.bean.SearchResponseVo;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.recycler.Recycler;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: dpc
 * @data: 2020/5/29,13:37
 */
@Service
public class SearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public SearchResponseVo search(SearchParamVo searchParamVo) {
        SearchResponseVo responseVo = null;
        try {
            SearchResponse searchResponse = restHighLevelClient.search(new SearchRequest(new String[]{"goods"}, sourceBuilder(searchParamVo)), RequestOptions.DEFAULT);
            responseVo = parseResult(searchResponse);
            responseVo.setPageNum(searchParamVo.getPageNum());
            responseVo.setPageSize(searchParamVo.getPageSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseVo;
    }

    private SearchSourceBuilder sourceBuilder(SearchParamVo searchParamVo) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //构件查询条件
        //1.1 匹配查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        String keyword = searchParamVo.getKeyword();
        if (StringUtils.isBlank(keyword)) {
            return sourceBuilder;
        }
        boolQuery.must(QueryBuilders.matchQuery("title", keyword).operator(Operator.AND));
        sourceBuilder.query(boolQuery);
        //1.2 过滤
        //1.2.1 品牌过滤
        List<Long> brandIds = searchParamVo.getBrandId();
        if (!CollectionUtils.isEmpty(brandIds)) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", brandIds));
        }
        //1.2.2 分类过滤
        List<Long> cid = searchParamVo.getCid();
        if (!CollectionUtils.isEmpty(cid)) {
            boolQuery.filter(QueryBuilders.termsQuery("categoryId", cid));
        }
        //1.2.3 规格参数过滤
        List<String> props = searchParamVo.getProps();
        if (!CollectionUtils.isEmpty(props)) {
            props.forEach(prop -> {
                String[] attr = StringUtils.split(prop, ":");
                if (attr != null && attr.length == 2) {
                    String attrId = attr[0];
                    String[] attrValues = StringUtils.split(attr[1], "-");
                    BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();
                    boolQuery1.must(QueryBuilders.termQuery("searchAttrs.attrId", attrId));
                    boolQuery1.must(QueryBuilders.termsQuery("searchAttrs.attrValue", attrValues));
                    boolQuery.filter(QueryBuilders.nestedQuery("searchAttrs", boolQuery1, ScoreMode.None));
                }
            });
        }
        //1.2.4 价格区间过滤
        Integer from = searchParamVo.getPriceFrom();
        Integer to = searchParamVo.getPriceTo();
        if (from != null || to != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
            if (from != null) {
                rangeQuery.gte(from);
            }
            if (to != null) {
                rangeQuery.lte(to);
            }
            boolQuery.filter(rangeQuery);
        }
        //1.2.5 是否有货过滤
        Boolean store = searchParamVo.getStore();
        if (store != null) {
            boolQuery.filter(QueryBuilders.termQuery("store", store));
        }
        //构件排序条件
        Integer sort = searchParamVo.getSort();
        if (sort != null) {
            SortOrder sortOrder = SortOrder.DESC;
            String field = "_score";

            switch (sort) {
                case 1:
                    field = "price";
                    break;
                case 2:
                    field = "price";
                    sortOrder = SortOrder.ASC;
                    break;
                case 3:
                    field = "sales";
                    break;
                case 4:
                    field = "createTime";
                    break;
            }
            sourceBuilder.sort(field, sortOrder);
        }
        //构件分页条件
        Integer pageNum = searchParamVo.getPageNum();
        Integer pageSize = searchParamVo.getPageSize();
        sourceBuilder.from((pageNum - 1) * pageSize);
        sourceBuilder.size(pageSize);
        //构建高亮条件
        sourceBuilder.highlighter(new HighlightBuilder().
                field("title").
                preTags("<font style='color:red'>").
                postTags("</font>"));
        //构件聚合条件
        //品牌聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                .subAggregation(AggregationBuilders.terms("logoAgg").field("logo"))
        );
        //分类聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAgg").field("categoryId").
                subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName"))
        );
        //规格参数聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("attrAgg", "searchAttrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("searchAttrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("searchAttrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("searchAttrs.attrValue"))
                )
        );
        //结果集过滤
        sourceBuilder.fetchSource(new String[]{"skuId", "title", "subTitle", "defaultImage", "price"}, null);
        System.out.println(sourceBuilder.toString());
        return sourceBuilder;
    }

    /**
     * 解析搜索的响应结果
     *
     * @param response
     * @return
     */
    private SearchResponseVo parseResult(SearchResponse response) {
        SearchResponseVo responseVo = new SearchResponseVo();
        //1分页数据
        SearchHits hits = response.getHits();
        responseVo.setTotal(hits.getTotalHits());
        //2当前页数据 hits
        SearchHit[] hitsHits = hits.getHits();
        List<Goods> goodsList = Stream.of(hitsHits).map(entity -> {
            String source = entity.getSourceAsString();
            Goods goods = JSON.parseObject(source, Goods.class);
            Text[] titles = entity.getHighlightFields().get("title").getFragments();
            goods.setTitle(titles[0].toString());
            return goods;
        }).collect(Collectors.toList());
        responseVo.setGoodsList(goodsList);
        //3聚合数据
        Map<String, Aggregation> aggregationMap = response.getAggregations().asMap();
        //品牌聚合
        ParsedLongTerms brandIdAgg = (ParsedLongTerms) aggregationMap.get("brandIdAgg");
        List<? extends Terms.Bucket> brandIdAggBuckets = brandIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(brandIdAggBuckets)) {
            List<BrandEntity> brandEntityList = brandIdAggBuckets.stream().map(bucket -> {
                BrandEntity brandEntity = new BrandEntity();
                // 从桶中获取key，key就是brandId
                Long brandId = ((Terms.Bucket) bucket).getKeyAsNumber().longValue();
                brandEntity.setId(brandId);

                // 获取桶中子聚合
                Map<String, Aggregation> brandAggregationMap = ((Terms.Bucket) bucket).getAggregations().asMap();

                // 获取品牌名称的子聚合
                ParsedStringTerms brandNameAgg = (ParsedStringTerms) brandAggregationMap.get("brandNameAgg");
                // 一个品牌id对应的品牌名称肯定有且仅有一个
                brandEntity.setName(brandNameAgg.getBuckets().get(0).getKeyAsString());

                // 获取品牌logo的子聚合
                ParsedStringTerms logoAgg = (ParsedStringTerms) brandAggregationMap.get("logoAgg");
                // 获取logo子聚合中的桶
                List<? extends Terms.Bucket> logoAggBuckets = logoAgg.getBuckets();
                // 判断logo桶集合是否为空，不为空，获取第一个
                if (!CollectionUtils.isEmpty(logoAggBuckets)) {
                    brandEntity.setLogo(logoAggBuckets.get(0).getKeyAsString());
                }
                return brandEntity;
            }).collect(Collectors.toList());
            responseVo.setBrands(brandEntityList);
        }

        //分类聚合
        ParsedLongTerms categoryIdAgg = (ParsedLongTerms) aggregationMap.get("categoryIdAgg");
        List<? extends Terms.Bucket> catbuckets = categoryIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(catbuckets)) {
            List<CategoryEntity> categoryEntities = catbuckets.stream().map(bucket -> {
                CategoryEntity categoryEntity = new CategoryEntity();
                //最外层的就是id
                long longValue = bucket.getKeyAsNumber().longValue();
                categoryEntity.setId(longValue);
                //获取桶中的子聚合
                ParsedStringTerms categoryNameAgg = (ParsedStringTerms) ((Terms.Bucket) bucket).getAggregations().get("categoryNameAgg");
                categoryEntity.setName(categoryNameAgg.getBuckets().get(0).getKeyAsString());
                return categoryEntity;
            }).collect(Collectors.toList());
            responseVo.setCategories(categoryEntities);
        }

        //属性聚合
        ParsedNested attrAgg = (ParsedNested) aggregationMap.get("attrAgg");
        ParsedLongTerms attrIdAgg = (ParsedLongTerms) attrAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> attrBuckets = attrIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(attrBuckets)) {
            List<SearchResponseAttrVo> attrVos = attrBuckets.stream().map(bucket -> {
                SearchResponseAttrVo attrVo = new SearchResponseAttrVo();
                attrVo.setAttrId(((Terms.Bucket) bucket).getKeyAsNumber().longValue());
                Map<String, Aggregation> asMap = ((Terms.Bucket) bucket).getAggregations().asMap();

                ParsedStringTerms agg = (ParsedStringTerms) asMap.get("attrNameAgg");
                attrVo.setAttrName(agg.getBuckets().get(0).getKeyAsString());
                ParsedStringTerms attrValueAgg = (ParsedStringTerms) asMap.get("attrValueAgg");
                List<? extends Terms.Bucket> buckets = attrValueAgg.getBuckets();
                if (!CollectionUtils.isEmpty(buckets)) {
                    attrVo.setAttrValues(buckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList()));
                }


                return attrVo;
            }).collect(Collectors.toList());
            responseVo.setFilters(attrVos);
        }

        return responseVo;
    }
}
