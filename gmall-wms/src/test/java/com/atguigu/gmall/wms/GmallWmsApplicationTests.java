package com.atguigu.gmall.wms;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.mapper.WareSkuMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GmallWmsApplicationTests {
@Autowired
private WareSkuMapper wareSkuMapper;
    @Test
    void contextLoads() {
        List<WareSkuEntity> check = wareSkuMapper.check(1L, 30);
        check.forEach(wareSkuEntity -> System.out.println(wareSkuEntity));
    }

}
