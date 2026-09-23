package com.ohouse.web.controller.auth;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ohouse.web.domain.member.MemberVO;
import com.ohouse.web.service.auth.MemberSignupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequiredArgsConstructor
public class MemberSignupController {

	private final MemberSignupService memberService;

	@PostMapping("/signup.htm")
	public String signup(MemberVO memberVO, RedirectAttributes rttr) throws ClassNotFoundException, SQLException {
		memberService.register(memberVO);

		rttr.addFlashAttribute("result", 1);
		return "redirect:../index.htm";
	}

}
