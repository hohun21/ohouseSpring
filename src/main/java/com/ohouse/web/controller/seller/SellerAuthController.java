package com.ohouse.web.controller.seller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ohouse.web.domain.auth.SellerAuthDTO;
import com.ohouse.web.domain.auth.SellerSignupRequest;
import com.ohouse.web.domain.seller.SellerDTO;
import com.ohouse.web.service.auth.SellerAuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SellerAuthController {

    private final SellerAuthService service;

    @GetMapping("/seller/signup.htm")
    public String signupForm() { return "seller/sellerSignup"; }

    @PostMapping("/seller/signup.htm")
    public String signup(SellerSignupRequest req, Model model, RedirectAttributes redirect) {
        Map<String, Boolean> errors = new HashMap<>();
        req.validate(errors);

        if (errors.isEmpty()) {
            try { service.signup(req, errors); }
            catch (RuntimeException e) { model.addAttribute("signupError", "가입 처리 중 오류가 발생했습니다."); return "seller/sellerSignup"; }
        }

        if (!errors.isEmpty()) { model.addAttribute("errors", errors); return "seller/sellerSignup"; }

        redirect.addFlashAttribute("signupComplete", true);
        return "redirect:/seller/sellerSignupStatus.htm";
    }

    @GetMapping("/seller/login.htm")
    public String loginForm() { return "seller/sellerLogin"; }

    @PostMapping("/seller/login.htm")
    public String login(@RequestParam String email, @RequestParam String password,
            @RequestParam String businessNumber, HttpServletRequest request, Model model) {

        String normalized = SellerAuthService.digits(businessNumber);
        model.addAttribute("email", email);
        model.addAttribute("businessNumber", normalized);

        if (email.trim().isEmpty() || !normalized.matches("[0-9]{10}") || password.isEmpty()) {
            model.addAttribute("loginError", "이메일, 비밀번호와 사업자등록번호를 확인해주세요.");
            return "seller/sellerLogin";
        }

        try {
            SellerDTO seller = service.authenticate(email.trim(), password, normalized);
            
            HttpSession old = request.getSession(false);
            if (old != null) old.invalidate();

            HttpSession session = request.getSession(true);
            SellerAuthDTO auth = new SellerAuthDTO(seller.getSellerId(), seller.getEmail(), seller.getBusinessNumber(), seller.getBrandName(), seller.getStatus());
            session.setAttribute("sellerAuth", auth);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(auth, null, AuthorityUtils.createAuthorityList("ROLE_SELLER")));
            SecurityContextHolder.setContext(context);
            
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
            return "redirect:/main.htm";

        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("loginError", e.getMessage());
            return "seller/sellerLogin";
        }
    }

    @GetMapping("/seller/sellerSignupStatus.htm")
    public String statusForm() { return "seller/sellerSignupStatus"; }

    @PostMapping("/seller/sellerSignupStatus.htm")
    @ResponseBody
    public Map<String, Object> status(@RequestParam String email, @RequestParam String password,
            @RequestParam String businessNumber) {

        Map<String, Object> result = new HashMap<>();

        try {
            String normalized = SellerAuthService.digits(businessNumber);
            if (email.trim().isEmpty() || !normalized.matches("[0-9]{10}") || password.isEmpty())
                throw new IllegalArgumentException("입력값을 다시 확인해 주세요.");

            result.put("success", true); 
            result.put("status", service.signupStatus(email.trim(), password, normalized));
            result.put("message", "입점 상태 조회가 완료되었습니다.");

        } catch (IllegalArgumentException e) {
            result.put("success", false); 
            result.put("status", null); 
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/sellerStatusCheck.ajax")
    @ResponseBody
    public Map<String, Object> statusCheck(@RequestParam(required = false) String email) {

        Map<String, Object> result = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            result.put("success", false); 
            result.put("code", "INVALID_EMAIL"); 
            return result;
        }

        String status = service.status(email.trim());

        result.put("success", true); 
        result.put("status", status);
        result.put("code", status == null ? "NOT_FOUND" : status);
        return result;
    }

    @PostMapping(value = {"/emailcheck.ajax", "/brandnamecheck.ajax", "/businessnumbercheck.ajax", "/mailordernumbercheck.ajax"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> duplicate(@RequestParam Map<String, String> params, HttpServletRequest request) {

        String path = request.getServletPath();
        String key = path.equals("/emailcheck.ajax") ? "email" : path.equals("/brandnamecheck.ajax") ? "brandName" :
                path.equals("/businessnumbercheck.ajax") ? "businessNumber" : "mailOrderNumber";
        String value = params.get(key);

        Map<String, Object> response = new HashMap<>();

        if (value == null || value.trim().isEmpty()) {
            response.put("count", 0); 
            response.put("code", "INVALID_VALUE");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("count", service.count(key, value.trim()));
        return ResponseEntity.ok(response);
    }
}