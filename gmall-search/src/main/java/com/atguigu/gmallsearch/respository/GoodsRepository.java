package com.atguigu.gmallsearch.respository;

import com.atguigu.gmallsearch.bean.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author: dpc
 * @data: 2020/5/27,14:07
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {

}
