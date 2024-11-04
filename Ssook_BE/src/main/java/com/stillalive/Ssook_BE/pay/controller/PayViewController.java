package com.stillalive.Ssook_BE.pay.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/pay/view")
public class PayViewController {

    @GetMapping("/point/charge")
    @Operation(summary = "포인트 충전 페이지", description = "포인트 충전 페이지를 렌더링합니다.")
    public String showPaymentPage(Model model) {
        model.addAttribute("loginId", "exampleLoginId"); // 예시 로그인 ID
        model.addAttribute("userId", "12345"); // 예시 사용자 ID
        return "payment"; // templates/payment.mustache를 렌더링
    }

//    @GetMapping("/point/charge")
//    @Operation(summary = "포인트 충전 페이지", description = "포인트 충전 페이지를 렌더링합니다.")
//    public String showPaymentPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
//        model.addAttribute("loginId", userDetails.getLoginId()); // 로그인 ID
//        model.addAttribute("userId", userDetails.getUserId()); // 사용자 ID
//        return "payment"; // templates/payment.mustache를 렌더링
//    }
}