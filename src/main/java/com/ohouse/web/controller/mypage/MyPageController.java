package com.ohouse.web.controller.mypage;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MyPageController {

    @GetMapping("/myPage.htm")
    public String myPage(@AuthenticationPrincipal Object principal, Model model) {
        
        if (principal == null) {
            return "redirect:/auth/login.htm";
        }

        return "member/mypage"; 
    }
}