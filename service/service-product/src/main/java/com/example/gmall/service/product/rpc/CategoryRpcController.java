package com.example.gmall.service.product.rpc;

import com.example.gmall.common.result.Result;
import com.example.gmall.service.product.service.BaseCategory1Service;
import com.example.gmall.model.product.vo.CategoryTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 14/2/2024 - 11:27 pm
 * @Description
 */
@RequestMapping("/api/inner/rpc/product")
@RestController
public class CategoryRpcController {

    @Autowired
    BaseCategory1Service baseCategory1Service;


    /**
     * 获取分类的全部数据并组织成树形结构
     * @return
     */
    @GetMapping("/category/tree")
    public Result<List<CategoryTreeVO>> getCategoryTree(){

        List<CategoryTreeVO> vos = baseCategory1Service.getCategoryTree();
        return Result.ok(vos);
    }


    /**
     * 给sku详情(web-all -> service-item -> service-product)使用的，每个sku必有三级分类，根据三级分类id，得到整个分类的完整路径
     * @param c3Id
     * @return
     */
    @GetMapping("/category/view/{c3Id}")
    public Result<CategoryTreeVO> getCategoryTreeWithC3Id(@PathVariable("c3Id") Long c3Id){
        CategoryTreeVO vo = baseCategory1Service.getCategoryTreeWithC3Id(c3Id);

        return Result.ok(vo);
    }

}
