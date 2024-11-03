package com.stillalive.Ssook_BE.pay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/pay/view")
public class PayViewController {

    @GetMapping("/point/charge")
    public String showPaymentPage(Model model) {
        model.addAttribute("loginId", "exampleLoginId"); // 예시 로그인 ID
        model.addAttribute("userId", "12345"); // 예시 사용자 ID
        return "payment"; // templates/payment.mustache를 렌더링
    }
}