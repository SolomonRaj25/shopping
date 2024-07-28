package com.online.shopping.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        var authority = authentication.getAuthorities();
        var roles = authority.stream().map(r->r.getAuthority()).findFirst();
        if (roles.orElse("").equals("ADMIN")) {
            response.sendRedirect("/online_shopping/admin/home");
        } else if (roles.orElse("").equals("USER")) {
            response.sendRedirect("/online_shopping/home/page");
        } else {
            response.sendRedirect("/online_shopping/user/login");
        }
    }
}
