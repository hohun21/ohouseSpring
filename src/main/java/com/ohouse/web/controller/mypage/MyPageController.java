package com.ohouse.web.controller.mypage;

import com.ohouse.web.domain.member.MyOrderDTO;
import com.ohouse.web.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MyPageController {
    private final MemberService memberService;
    @GetMapping("/myPage.htm")
    public String myPage(@AuthenticationPrincipal Object principal, Model model) {
        
        if (principal == null) {
            return "redirect:/auth/login.htm";
        }

        return "member/mypage"; 
    }
    @GetMapping("/myShopping.htm")
    public String myShopping(Model model,
                             @AuthenticationPrincipal(expression = "member_vo.memberId") Integer member_id) throws SQLException, ClassNotFoundException {

        List<MyOrderDTO> orderdto = this.memberService.selectorder(member_id);

        model.addAttribute("orderdto",orderdto);
        return "member/myShopping";
    }
}