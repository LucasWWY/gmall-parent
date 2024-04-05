package com.example.gmall.weball.controller;

import com.example.gmall.common.result.Result;
import com.example.gmall.feign.product.CategoryFeignClient;
import com.example.gmall.service.product.vo.CategoryTreeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 14/2/2024 - 11:09 pm
 * @Description
 */
@Controller //这里的数据要返给web-all的thymeleaf，而不是返回json数据，所以用@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    CategoryFeignClient categoryFeignClient;

    //@ResponseBody
    //@GetMapping("/hello")
    //public String hello(){
    //    return "hello";
    //}

    @GetMapping({"/","/index.html"})
    public String index(Model model, HttpServletRequest request){
        //远程调用 service-product 获取系统所有的三级分类数据
        Result<List<CategoryTreeVO>> categoryTreeVOs = categoryFeignClient.getCategoryTree();
        List<CategoryTreeVO> data = categoryTreeVOs.getData();
        model.addAttribute("list",data);
        return "index/index";
    }

}
