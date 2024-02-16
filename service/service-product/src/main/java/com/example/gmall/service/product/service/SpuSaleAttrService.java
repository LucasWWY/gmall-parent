package com.example.gmall.service.product.service;

import com.example.gmall.service.product.entity.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service
* @createDate 2024-02-06 23:58:53
*/
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);

    /**
     * 为sku详情页服务，获取销售属性和值，并按固定顺序排序，并且标记出哪个sku的属性值被选中了
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListOrder(Long spuId, Long skuId);

    /**
     * 根据spuId得到所有sku的销售属性
     * @param spuId
     * @return
     */
    String getValuesSkuJson(Long spuId);
}
