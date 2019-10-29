package com.pipichao.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
@Controller
@RequestMapping("/demo")
public class TestController {

    @RequestMapping("/test")
    public String test(){
        return "index";
    }


    @RequestMapping(value = "/login/{userId}")
    public String test(@PathVariable("userId") String userId, Model model) throws Exception {
        //把登录名称传给jsp页面
        model.addAttribute("userId", userId);
        return "index";
    }
}
