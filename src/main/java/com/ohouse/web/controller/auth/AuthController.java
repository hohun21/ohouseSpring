package com.ohouse.web.controller.auth;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ohouse.web.domain.member.MemberVO;
import com.ohouse.web.service.auth.MemberLoginService;
import com.ohouse.web.service.auth.MemberSignupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final MemberSignupService memberService;
	private final MemberLoginService memberLoginService;
	
	@GetMapping("/signup.htm")
	public String signupForm() {
	    return "member/signup";
	}
	
	@PostMapping("/signup.htm")
	public String signup(MemberVO memberVO, RedirectAttributes rttr) {
		memberService.register(memberVO);

		rttr.addFlashAttribute("result", 1);
		return "redirect:../main.htm";
	}

	@GetMapping("/login.htm")
	public String login(String error, String logout, Model model) throws Exception{
		System.out.println("🤩 MemberLoginController.login()...");

		log.info("> error : " + error);
		log.info("> logout : " + logout);

		if (error != null) {
			model.addAttribute("error", "로그인 실패로 ...");
			//
			//
		}
		if (logout != null) {
			model.addAttribute("logout", "로그아웃 실패로 ...");
		}

		return "member/login";
	}

	@GetMapping("/statusCheck.ajax")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> statusCheck( @RequestParam(required = false) String id) {

		Map<String, Object> result = new HashMap<>();

		if (id == null || id.trim().isBlank()) {
			result.put("success", false);
			result.put("status", null);
			result.put("code", "INVALID_ID");

			return ResponseEntity.badRequest().body(result);
		}

		try {
			Integer status = memberLoginService.statusCheck(id.trim());

			if (status == null) {
				result.put("success", false);
				result.put("status", null);
				result.put("code", "NOT_FOUND");

			} else if (status == 0) {
				result.put("success", true);
				result.put("status", 0);
				result.put("code", "WITHDRAWN");

			} else if (status == -1) {
				result.put("success", true);
				result.put("status", -1);
				result.put("code", "STOP");

			} else {
				result.put("success", true);
				result.put("status", status);
				result.put("code", "ACTIVE");
			}

			return ResponseEntity.ok(result);

		} catch (SQLException | NamingException e) {
			e.printStackTrace();

			result.put("success", false);
			result.put("status", null);
			result.put("code", "SERVER_ERROR");

			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(result);
		}
	}

}
