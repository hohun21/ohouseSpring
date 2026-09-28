package com.ohouse.web.controller.member;

import com.ohouse.web.domain.member.MyOrderDTO;
import com.ohouse.web.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/myPage.htm")
    public String myPage(Model model, HttpSession session) throws SQLException, ClassNotFoundException {
        Map<String,Object> authUser = new HashMap<>();
        authUser.put("name","테스트유저1");
        session.setAttribute("authUser",authUser);
        return "member/mypage";
    }

    @GetMapping("/myShopping.htm")
    public String myShopping(Model model) throws SQLException, ClassNotFoundException {
        int member_id = 4;
        List<MyOrderDTO> orderdto = this.memberService.selectorder(member_id);

        model.addAttribute("orderdto",orderdto);
        return "member/myShopping";
    }
}
