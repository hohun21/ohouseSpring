package com.ohouse.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/main.htm")
    public String home(Model model) {
        model.addAttribute("data", "Hello, Spring from IntelliJ!");
        return "main/main";
    }
}