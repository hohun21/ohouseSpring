package com.ohouse.web.controller.auth;

import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ohouse.web.domain.auth.SellerAuthDTO;
import com.ohouse.web.service.auth.PasswordChangeService;
import com.ohouse.web.service.auth.SellerAuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PasswordChangeController {
    private final PasswordChangeService memberService;
    private final SellerAuthService sellerService;

    private boolean loggedIn(Authentication auth) { return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken); }

    @GetMapping("/changePwd.htm")
    public String form(Authentication auth) { return loggedIn(auth) ? "member/password_change" : "redirect:/auth/login.htm"; }

    @PostMapping(value = "/checkCurrentPwd.ajax", produces = "application/json")
    @ResponseBody
    public Map<String, Boolean> check(@RequestParam(required = false) String currentPwd, Authentication auth,
            HttpServletRequest request) {

        if (!loggedIn(auth)) return Collections.singletonMap("isMatch", false);

        SellerAuthDTO seller = (SellerAuthDTO) request.getSession().getAttribute("sellerAuth");

        boolean match = seller != null ? sellerService.checkPassword(seller.getSellerId(), seller.getEmail(), currentPwd) :
                memberService.check(auth.getName(), currentPwd);

        return Collections.singletonMap("isMatch", match);
    }

    @PostMapping("/changePwdPro.htm")
    public String change(@RequestParam String currentPwd, @RequestParam String newPwd, @RequestParam String confirmPwd,
            Authentication auth, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirect) {

        if (!loggedIn(auth)) return "redirect:/auth/login.htm";

        if (newPwd == null || !newPwd.matches("^(?=.*[A-Za-z])(?=.*[0-9])[\\x21-\\x7E]{8,20}$") ||
                !newPwd.equals(confirmPwd) || newPwd.equals(currentPwd)) {
            model.addAttribute("passwordError", "새 비밀번호 형식과 확인값을 다시 확인해주세요.");
            return "member/password_change";
        }

        SellerAuthDTO seller = (SellerAuthDTO) request.getSession().getAttribute("sellerAuth");

        try {
            if (seller == null) memberService.change(auth.getName(), currentPwd, newPwd);
            else sellerService.changePassword(seller.getSellerId(), seller.getEmail(), currentPwd, newPwd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("passwordError", e.getMessage());
            return "member/password_change";
        }

        new org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler("remember-me")
                .logout(request, response, auth);
        new SecurityContextLogoutHandler().logout(request, response, auth);

        redirect.addFlashAttribute("passwordChanged", true);
        return seller == null ? "redirect:/auth/login.htm" : "redirect:/seller/login.htm";
    }
}
