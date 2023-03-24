package com.pro.snap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
public class DemoController {

    // test page jump
    @RequestMapping("/hello")
    public String hello(Model model){
        model.addAttribute("name", "  zzzzzz@lhr");
        return "hello";
    }
}
