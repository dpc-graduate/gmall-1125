package com.atguigu.gmall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.wms.entity.WareEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author dpc
 * @email 1789333527@qq.com
 * @date 2020-05-20 09:42:52
 */
public interface WareService extends IService<WareEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

