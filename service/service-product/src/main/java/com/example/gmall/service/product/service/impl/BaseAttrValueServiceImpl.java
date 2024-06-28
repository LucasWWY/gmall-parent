package com.example.gmall.service.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.model.product.entity.BaseAttrValue;
import com.example.gmall.service.product.mapper.BaseAttrValueMapper;
import com.example.gmall.service.product.service.BaseAttrValueService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【base_attr_value(属性值表)】的数据库操作Service实现
* @createDate 2024-02-06 23:58:52
*/
@Service
public class BaseAttrValueServiceImpl extends ServiceImpl<BaseAttrValueMapper, BaseAttrValue>
    implements BaseAttrValueService{

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        List<BaseAttrValue> baseAttrValueList = list(new LambdaQueryWrapper<BaseAttrValue>()
                .eq(BaseAttrValue::getAttrId, attrId));
        return baseAttrValueList;
    }
}




