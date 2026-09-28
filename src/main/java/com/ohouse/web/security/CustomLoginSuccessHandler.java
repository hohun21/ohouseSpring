package com.ohouse.web.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    @org.springframework.beans.factory.annotation.Autowired
    private com.ohouse.web.service.auth.LegacyPasswordUpgradeService passwordUpgradeService;

    @Override

	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		log.warn("😘😘😘 Login Success...");
		// 인증사용자가 가지고 있는 롤(Role) == 권한
	
		List<String> roleNames = new ArrayList<String>();         
		authentication.getAuthorities().forEach( auth -> {
			roleNames.add(auth.getAuthority());
		} );
	
		log.warn("👍 > ROLE NAMES : " + roleNames );
	
        com.ohouse.web.domain.security.CustomerUser user =
                (com.ohouse.web.domain.security.CustomerUser) authentication.getPrincipal();
        com.ohouse.web.domain.member.MemberVO member = user.getMember_vo();
        String raw = request.getParameter("password");
        if (raw != null) passwordUpgradeService.upgrade(member, raw);
        request.getSession().removeAttribute("sellerAuth");
        String role = roleNames.contains("ROLE_ADMIN") ? "ADMIN" : "USER";
        request.getSession().setAttribute("authUser", new com.ohouse.web.domain.auth.AuthUser(
                member.getMemberId(), member.getId(), member.getName(), role));
        response.sendRedirect(request.getContextPath() + "/main.htm");
	}
}
