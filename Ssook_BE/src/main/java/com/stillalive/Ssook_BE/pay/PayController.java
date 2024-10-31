package com.stillalive.Ssook_BE.pay;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.pay.dto.MyCardResDto;
import com.stillalive.Ssook_BE.pay.dto.PaymentReqDto;
import com.stillalive.Ssook_BE.pay.dto.RegisterCardReqDto;
import com.stillalive.Ssook_BE.pay.service.PaymentService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pay")
@AllArgsConstructor
public class PayController {

    private final PaymentService paymentService;

    /**
     * 카드 등록 API
     * */

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerCard(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody RegisterCardReqDto dto) {
        int childId = userDetails.getChildId();
        paymentService.registerCard(dto, childId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "카드가 등록되었습니다.", null));
    }

    /**
     * 포인트 조회
     * */
    @GetMapping("/point/balance")
    public ResponseEntity<ApiResponse<?>> getPointBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int childId = userDetails.getChildId();
        int pointBalance = paymentService.getPointBalance(childId);
        String message = "포인트 잔액은 " + pointBalance + " 원 입니다";
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), message, pointBalance));    }



    /**
     * 결제 처리 API
     * @param dto 결제 요청 정보
     *            - userId: 유저 ID
     *            - cardId: 카드 ID
     *            - amount: 결제 금액
     *            - menuNames: 주문한 메뉴 이름 리스트
     *            - cardToken: 카드 토큰
     *            - expirationDate: 카드 만료일
     *            - isActive: 카드 활성화 여부
     * @return ResponseEntity<ResponseStatusDto>
     *     - 200: 결제가 완료되었습니다.
     *     - 그 이외는 CustomException을 통해 처리 -> 200이 아니면 결제 실패
     *
     * */
    @PostMapping
    public ResponseEntity<ApiResponse<?>>  processPayment(@RequestBody PaymentReqDto dto) {
        paymentService.processPayment(dto);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "결제가 완료되었습니다.", null));
    }

    /**
        * 내 카드 조회 API
     * @return ResponseEntity<MyCardResDto>
     *
     * */
    @GetMapping("/my-card")
    public ResponseEntity<MyCardResDto> getMyCard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(paymentService.getMyCard(userDetails.getChildId()));
    }

}

