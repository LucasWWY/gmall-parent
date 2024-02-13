package com.example.gmall.service.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.service.product.entity.SpuSaleAttr;
import com.example.gmall.service.product.service.SpuSaleAttrService;
import com.example.gmall.service.product.mapper.SpuSaleAttrMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2024-02-06 23:58:53
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
    implements SpuSaleAttrService{

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        /**
         * select ssa.*,ssav.id vid, ssav.sale_attr_value_name
         * from spu_sale_attr ssa
         * Left join spu_sale_attr_value ssav
         * on ssa.spu_id=ssav.spu_id
         * and ssa.base_sale_attr_id = ssav.base_sale_attr_id
         * where ssa.spu_id=24
         */

        List<SpuSaleAttr> spuSaleAttrs = baseMapper.getSpuSaleAttrList(spuId); //MybatisPlus自带方法不够用，自定义sql
        return spuSaleAttrs;
    }
}




