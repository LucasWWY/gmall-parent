package com.example.gmall.weball.controller;

import com.example.gmall.feign.search.SearchFeignClient;
import com.example.gmall.search.vo.SearchParamVO;
import com.example.gmall.search.vo.SearchRespVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 3:49 am
 * @Description
 */
@Controller
public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;

    @GetMapping("/list.html")
    public String search(SearchParamVO param, Model model) {

        //远程调用检索服务 （controller -> common/feign client -> rpc/service-search -> service/service-search）
        SearchRespVO searchData = searchFeignClient.search(param).getData();

        //这么多参数要返回给前端页面，没有一个原生的对象能够含有这么多不同数据，所以需要将这些参数放到VO中

        //1、检索参数
        model.addAttribute("searchParam",searchData.getSearchParam());

        //2、品牌面包屑 字符串 [x 苹果][x 16gb] 选完平台属性后，品牌面包屑出现作为一个提示
        model.addAttribute("trademarkParam",searchData.getTrademarkParam());

        //3、平台属性面包屑 集合 [{attrName、attrValue、attrId}]
        model.addAttribute("propsParamList",searchData.getPropsParamList());


        //4、品牌列表 集合 [{tmId、tmName、tmLogoUrl}]
        model.addAttribute("trademarkList",searchData.getTrademarkList());

        //5、平台属性列表（在sku图片上方供用户选择） 集合 [{attrName、attrValueList(字符串集合)、attrId}]
        model.addAttribute("attrsList",searchData.getAttrsList());

        //6、url参数
        model.addAttribute("urlParam",searchData.getUrlParam());

        //7、排序信息（type、sort）
        model.addAttribute("orderMap",searchData.getOrderMap());

        //8、商品列表 集合[{每个商品信息}]
        model.addAttribute("goodsList",searchData.getGoodsList());

        //9、页码
        model.addAttribute("pageNo",searchData.getPageNo());

        //10、总页码
        model.addAttribute("totalPages",searchData.getTotalPages());

        return "list/index"; //检索结果展示页
    }
}
