package com.example.gmall.service.product.service;

import com.example.gmall.service.product.entity.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.gmall.service.product.vo.SkuSaveInfoVO;

/**
* @author wangweiyedemacbook
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2024-02-06 23:58:53
*/
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfoData(SkuSaveInfoVO skuSaveInfoVO);

    void upGoods(Long skuId);

    void downGoods(Long skuId);
}
