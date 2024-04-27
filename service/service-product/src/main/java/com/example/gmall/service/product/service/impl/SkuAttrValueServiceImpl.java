package com.example.gmall.service.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.search.SearchAttr;
import com.example.gmall.service.product.entity.SkuAttrValue;
import com.example.gmall.service.product.service.SkuAttrValueService;
import com.example.gmall.service.product.mapper.SkuAttrValueMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service实现
* @createDate 2024-02-06 23:58:53
*/
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue>
    implements SkuAttrValueService{

    @Override
    public List<SearchAttr> getSkuAttrsAndValue(Long skuId) {
        return baseMapper.getSkuAttrsAndValue(skuId);
    }
}




