package com.example.gmall.weball.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/5/2024 - 4:50 am
 * @Description
 */
@Controller
public class LoginController {

    //先访问weball，然后会根据gateway设置的route转发到相应微服务上
    @GetMapping("/login.html")
    public String login(@RequestParam(value = "originUrl", required = false) String originUrl, Model model) {
        model.addAttribute("originUrl", originUrl);
        return "login";
    }

}
