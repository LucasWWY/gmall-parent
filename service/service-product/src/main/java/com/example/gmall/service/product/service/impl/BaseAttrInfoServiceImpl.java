package com.example.gmall.service.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.model.product.entity.BaseAttrInfo;
import com.example.gmall.model.product.entity.BaseAttrValue;
import com.example.gmall.service.product.mapper.BaseAttrInfoMapper;
import com.example.gmall.service.product.service.BaseAttrInfoService;
import com.example.gmall.service.product.service.BaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author wangweiyedemacbook
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2024-02-06 23:58:52
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Autowired
    BaseAttrValueService baseAttrValueService;

    @Override
    public List<BaseAttrInfo> getAttrInfoAndValue(Long category1Id,
                                                      Long category2Id,
                                                      Long category3Id) {
        List<BaseAttrInfo> attrInfos = baseMapper.getAttrInfoAndValue(category1Id, category2Id, category3Id);
        //BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo>
        //BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
        //public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {
        //    protected Log log = LogFactory.getLog(this.getClass());
        //    @Autowired
        //    protected M baseMapper;
        return attrInfos;
    }

    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        int i = baseMapper.insert(baseAttrInfo);
        baseAttrInfo.getAttrValueList().forEach(baseAttrValue -> {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueService.save(baseAttrValue);
        });
    }

    public void updateAttrInfo(BaseAttrInfo baseAttrInfo) {
        //1. 修改属性名
        baseMapper.updateById(baseAttrInfo);

        List<Long> ids = new ArrayList<>(); //前端提交的所有属性值的id
        for (BaseAttrValue baseAttrValue : baseAttrInfo.getAttrValueList()) {
            ids.add(baseAttrValue.getId());
        }

        //2. 删除属性值，前端没提交的就是要删除。
        //数据库原来：59,60,61,62,63
        //前端提交的：59,60,63
        //差集：把 61,62 删除
        // delete from base_attr_value where attr_id = 11 and id not in(59,60,63)

        if(ids.size() > 0){
            baseAttrValueService
                    .lambdaUpdate() //快速得到queryWrapper
                    .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId())
                    .notIn(BaseAttrValue::getId, ids)
                    .remove();
        }else {
            baseAttrValueService
                    .lambdaUpdate() //快速得到queryWrapper
                    .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId())
                    .remove();
        }


        //3. 修改属性值
        for (BaseAttrValue attrValue : baseAttrInfo.getAttrValueList()) {
            if (attrValue.getId() == null) {
                //这些是新增，属性值没提交id的就是新增
                //回填attrId
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueService.save(attrValue);
            }else {
                //这些是修改，属性值提交id就是修改
                baseAttrValueService.updateById(attrValue);
            }
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        return baseAttrValueService.getAttrValueList(attrId);
    }

}




