package com.ohouse.web.controller.shopping;

import com.ohouse.web.service.shopping.FeaturedProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FeaturedProductController {
    private final FeaturedProductService service;

    @GetMapping("/best.htm")
    public String best(Model model) {
        model.addAttribute("bestProducts", service.best());
        model.addAttribute("activeMenu", "best");
        return "shopping/best/best";
    }
    @GetMapping("/only.htm")
    public String only(Model model) {
        model.addAttribute("products", service.only());
        model.addAttribute("activeMenu", "only");
        return "shopping/only/only";
    }
}
