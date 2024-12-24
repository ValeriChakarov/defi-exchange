package com.example.defiExchange.controller;

import com.example.defiExchange.service.DeFiService;
import com.example.defiExchange.service.Web3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    private final DeFiService deFiService;
    private final Web3Service web3Service;

    @Autowired
    public DashboardController(DeFiService deFiService, Web3Service web3Service) {
        this.deFiService = deFiService;
        this.web3Service = web3Service;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal OAuth2User principal, Model model) throws Exception {
        if (principal != null) {
            model.addAttribute("name", principal.getAttribute("name"));
            model.addAttribute("email", principal.getAttribute("email"));
        }

        // Get the user's Ethereum balance (replace with actual user address)
        String userAddress = "0xB98a862BE65Ad29a71D97de1014b5c53f6E2Efea";
        BigDecimal balance = web3Service.getEthBalance(userAddress);
        model.addAttribute("ethBalance", balance);

        // Fetch and pass the DeFi protocols
        model.addAttribute("deFiProtocols", deFiService.getDeFiProtocols());

        // Fetch the Uniswap trading pairs
        List<String> uniswapTradingPairs = deFiService.getUniswapTradingPairs();
        if (uniswapTradingPairs.isEmpty()) {
            model.addAttribute("uniswapTradingPairs", new ArrayList<String>());
        } else {
            model.addAttribute("uniswapTradingPairs", uniswapTradingPairs);  // Add them to the model
        }

        return "dashboard";
    }
}
