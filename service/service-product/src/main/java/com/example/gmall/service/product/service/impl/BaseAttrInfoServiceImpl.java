package com.example.gmall.service.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.service.product.entity.BaseAttrInfo;
import com.example.gmall.service.product.mapper.BaseAttrInfoMapper;
import com.example.gmall.service.product.service.BaseAttrInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2024-02-06 23:58:52
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Override
    public List<BaseAttrInfo> getAttrInfoAndValue(Long category1Id,
                                                      Long category2Id,
                                                      Long category3Id) {
        List<BaseAttrInfo> attrInfos = baseMapper.getAttrInfoAndValue(category1Id, category2Id, category3Id);
        return attrInfos;
    }
}




