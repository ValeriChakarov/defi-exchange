package com.example.defiExchange.controller;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Route for the login page
    @GetMapping("/login")
    public String login() {
        return "login";  // Return the "login.html" page
    }

    // Route for the login dashboard page
    @GetMapping("/login/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            // Check the attribute key that corresponds to the name in the OAuth2 provider
            model.addAttribute("name", principal.getAttribute("login"));  // Use "login" for GitHub login
            model.addAttribute("email", principal.getAttribute("email"));
        }
        return "dashboard";  // Show user dashboard
    }

    // Route for the logout page
    @GetMapping("/logout")
    public String logoutSuccess() {
        return "logout";  // Return the "logout.html" page
    }
}
