package com.ohouse.web.controller.auth;

import java.util.HashMap;
import java.util.Map;
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

/** 인증 요청의 웹 계층. 실제 비밀번호 인증은 Spring Security가 처리한다. */
@Controller
@Log4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final MemberSignupService memberSignupService;
    private final MemberLoginService memberLoginService;

    @GetMapping("/signup.htm")
    public String signupForm() { return "member/signup"; }

    @PostMapping("/signup.htm")
    public String signup(MemberVO memberVO, @RequestParam String passwordConfirm,
                         Model model, RedirectAttributes redirectAttributes) {
        String id = memberVO.getId() == null ? "" : memberVO.getId().trim();
        String name = memberVO.getName() == null ? "" : memberVO.getName().trim();
        String password = memberVO.getPassword() == null ? "" : memberVO.getPassword();
        if (!id.matches("[A-Za-z0-9_-]{4,20}")) {
            model.addAttribute("signupError", "아이디는 영문, 숫자, 하이픈, 밑줄 4~20자로 입력해주세요.");
            return "member/signup";
        }
        if (name.length() < 2 || name.length() > 20) {
            model.addAttribute("signupError", "이름은 2~20자로 입력해주세요.");
            return "member/signup";
        }
        if (!password.matches("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9!@#$%^&*()_+=?.-]{8,}$")
                || !password.equals(passwordConfirm)) {
            model.addAttribute("signupError", "비밀번호 형식 또는 확인값을 확인해주세요.");
            return "member/signup";
        }
        memberVO.setId(id);
        memberVO.setName(name);
        try {
            memberSignupService.register(memberVO);
        } catch (IllegalArgumentException e) {
            model.addAttribute("signupError", e.getMessage());
            return "member/signup";
        }
        redirectAttributes.addFlashAttribute("result", 1);
        return "redirect:/main.htm";
    }

    @GetMapping("/login.htm")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout, Model model) {
        if (error != null) model.addAttribute("error", "로그인에 실패했습니다.");
        if (logout != null) model.addAttribute("logout", "로그아웃했습니다.");
        return "member/login";
    }

    @GetMapping("/idcheck.ajax")
    @ResponseBody
    public Map<String, Object> idCheck(@RequestParam(required = false) String id) {
        Map<String, Object> result = new HashMap<>();
        String value = id == null ? "" : id.trim();
        if (!value.matches("[A-Za-z0-9_-]{4,20}")) {
            result.put("count", 1); result.put("code", "INVALID_ID"); return result;
        }
        result.put("count", memberLoginService.idExists(value) ? 1 : 0);
        return result;
    }

    @GetMapping("/namecheck.ajax")
    @ResponseBody
    public Map<String, Object> nameCheck(@RequestParam(required = false) String name) {
        Map<String, Object> result = new HashMap<>();
        String value = name == null ? "" : name.trim();
        if (value.length() < 2 || value.length() > 20) {
            result.put("count", 1); result.put("code", "INVALID_NAME"); return result;
        }
        result.put("count", memberLoginService.nameExists(value) ? 1 : 0);
        return result;
    }

    @GetMapping("/statusCheck.ajax")
    @ResponseBody
    public Map<String, Object> statusCheck(@RequestParam(required = false) String id) {
        Map<String, Object> result = new HashMap<>();
        if (id == null || id.trim().isEmpty()) {
            result.put("success", false); result.put("status", null);
            result.put("code", "INVALID_ID"); return result;
        }
        Integer status = memberLoginService.statusCheck(id.trim());
        result.put("status", status);
        if (status == null) { result.put("success", false); result.put("code", "NOT_FOUND"); }
        else if (status == 0) { result.put("success", true); result.put("code", "WITHDRAWN"); }
        else if (status == -1) { result.put("success", true); result.put("code", "STOP"); }
        else { result.put("success", true); result.put("code", "ACTIVE"); }
        return result;
    }
}
