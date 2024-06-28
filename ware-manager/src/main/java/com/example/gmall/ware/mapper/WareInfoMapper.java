package com.example.gmall.ware.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.gmall.ware.bean.WareInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @param
 * @return
 */
@Repository
public interface WareInfoMapper extends BaseMapper<WareInfo> {


    public List<WareInfo> selectWareInfoBySkuid(String skuid);



}
