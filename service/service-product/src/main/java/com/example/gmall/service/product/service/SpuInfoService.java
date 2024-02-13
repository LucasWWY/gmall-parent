package com.example.gmall.service.product.service;

import com.example.gmall.service.product.entity.SpuInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.gmall.service.product.vo.SpuSaveInfoVO;

/**
* @author wangweiyedemacbook
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2024-02-06 23:58:53
*/
public interface SpuInfoService extends IService<SpuInfo> {

    void saveSpuInfoData(SpuSaveInfoVO spuSaveInfoVO);
}
