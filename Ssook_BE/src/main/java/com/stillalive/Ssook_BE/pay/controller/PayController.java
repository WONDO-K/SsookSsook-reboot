package com.stillalive.Ssook_BE.pay.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.domain.Card;
import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.ChildHistory;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.pay.dto.*;
import com.stillalive.Ssook_BE.pay.service.PaymentService;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.repository.ChildRepository;
import com.stillalive.Ssook_BE.user.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.stillalive.Ssook_BE.util.utill.PortoneClient;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pay")
@AllArgsConstructor
public class PayController {

    private static final Logger log = LoggerFactory.getLogger(PayController.class);

    private final PaymentService paymentService;
    private final ChildRepository childRepository;
    private final ParentService parentService;
    private final PortoneClient portoneClient;

    /**
     * 카드 등록 API
     */

    @PostMapping("/register")
    @Operation(summary = "카드 등록", description = "카드 정보를 토대로 카드를 등록합니다.")
    public ResponseEntity<ApiResponse<?>> registerCard(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RegisterCardReqDto dto) {
        int childId = userDetails.getChildId();
        paymentService.registerCard(dto, childId);
        return ResponseEntity
                .ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "카드가 등록되었습니다.", null));
    }

    @PutMapping("/register/change")
    @Operation(summary = "등록 카드 변경", description = "등록된 카드를 변경합니다.")
    public ResponseEntity<ApiResponse<?>> changeCard(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RegisterCardReqDto dto) {
        int childId = userDetails.getChildId();
        paymentService.changeCard(dto, childId);
        return ResponseEntity
                .ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "카드가 변경되었습니다.", null));
    }

    /**
     * 포인트 조회
     */
    @GetMapping("/point/balance")
    @Operation(summary = "포인트 조회", description = "포인트 조회 API")
    public ResponseEntity<ApiResponse<?>> getPointBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        int userId;
        boolean isChild = userDetails.isChild();

        if (!isChild) {
            userId = userDetails.getParentId();
        } else {
            userId = userDetails.getChildId();
        }

        PointBalanceResDto dto = paymentService.getPointBalance(userId, isChild);

        String message = "포인트 잔액은 " + dto.getPointBalance() + " 원 입니다";

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), message, dto));
    }

    /**
     * 결제 처리 API
     * 
     * @param dto 결제 요청 정보
     *            - userId: 유저 ID
     *            - cardId: 카드 ID
     *            - amount: 결제 금액
     *            - menuNames: 주문한 메뉴 이름 리스트
     *            - cardToken: 카드 토큰
     *            - expirationDate: 카드 만료일
     *            - isActive: 카드 활성화 여부
     * @return ResponseEntity<ResponseStatusDto>
     *         - 200: 결제가 완료되었습니다.
     *         - 그 이외는 CustomException을 통해 처리 -> 200이 아니면 결제 실패
     *
     */
    @PostMapping
    @Operation(summary = "결제 처리", description = "결제 처리 API")
    public ResponseEntity<ApiResponse<?>> processPayment(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PaymentReqDto dto) {

        Child child = childRepository.findById(userDetails.getChildId())
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_CHILD));
        log.info("결제 요청 - 사용자 ID: {}, 결제 금액: {}", userDetails.getChildId(), dto.getAmount());
        paymentService.processPayment(dto, child);
        return ResponseEntity
                .ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "결제가 완료되었습니다.", null));
    }

    @PostMapping("/nfc")
    @Operation(summary = "NFC 결제 처리", description = "NFC 결제 처리 API")
    public ResponseEntity<ApiResponse<?>> processPayment(@RequestBody NfcPaymentReqDto dto) {

        Child child = childRepository.findById(dto.getChildId())
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_CHILD));
        log.info("결제 요청 - 사용자 ID: {}, 결제 금액: {}", dto.getChildId(), dto.getAmount());
        paymentService.nfcProcessPayment(dto, child);
        return ResponseEntity
                .ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "NFC 결제가 완료되었습니다.", null));
    }

    /**
     * 내 카드 조회 API
     * 
     * @return ResponseEntity<MyCardResDto>
     *
     */
    @GetMapping("/my-card")
    @Operation(summary = "내 카드 조회", description = "내 카드 조회 API")
    public ResponseEntity<MyCardResDto> getMyCard(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(paymentService.getMyCard(userDetails.getChildId()));
    }

    /**
     * 거래내역 리스트 조회
     */
    @GetMapping("/list")
    @Operation(summary = "거래내역 리스트 조회", description = "거래내역 리스트 조회 API")
    public ResponseEntity<ApiResponse<?>> getPaymentList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "months", required = false) Integer months) {

        int childId = userDetails.getChildId();
        List<ChildHistoryResDto> paymentList = paymentService.getPaymentList(childId, months);

        return ResponseEntity
                .ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "거래내역 조회 성공", paymentList));
    }

    /**
     * 거래내역 상세 조회
     */
    @GetMapping("/detail/{historyId}")
    @Operation(summary = "거래내역 상세 조회", description = "거래내역 상세 조회 API")
    public ResponseEntity<ApiResponse<?>> getPaymentDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(value = "historyId") int historyId) {

        int userId;
        boolean isChild = userDetails.isChild();

        if (isChild) {
            userId = userDetails.getChildId();
        } else {
            log.info("접근자 타입 확인 - 부모 유저, 부모 유저는 부모용 자녀 거래내역 조회 API를 사용해야 합니다.");
            throw new SsookException(ErrorCode.ACCESS_DENIED);
        }

        // 거래 내역 상세 조회를 위한 서비스 호출
        ChildHistory paymentDetail = paymentService.getPaymentDetail(userId, historyId);

        // ChildHistory 객체를 ChildHistoryResDto로 변환
        ChildHistoryResDto paymentDetailDto = ChildHistoryResDto.toDto(paymentDetail);

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "거래내역 상세 조회 성공",
                paymentDetailDto));
    }

    // @PostMapping("/kakaopay/success")
    // @Operation(summary = "카카오페이 결제 성공 처리", description = "카카오페이 결제 성공 시 호출되는
    // API")
    // public ResponseEntity<ApiResponse<?>> chargePoints(@RequestBody
    // KakaoPaySuccessResDto responseDto) {
    // paymentService.chargePoints(responseDto.getParentId(),
    // responseDto.getAmount());
    // return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(),
    // HttpStatus.OK.getReasonPhrase(), "포인트 충전 성공", null));
    // }

    /**
     * 포인트 충전 API
     */
    @PostMapping("/point/charge")
    @Operation(summary = "포인트 충전", description = "포인트 충전 API")
    public ResponseEntity<ApiResponse<?>> chargePoint(@RequestBody ChargePointReqDto dto) {

        log.info("포인트 충전 요청 - 사용자 ID: {}, 충전 금액: {}, 결제 고유 ID: {}",
                dto.getUserId(), dto.getAmount(), dto.getImpUid());
        try {
            // 1. 우리 서버 DB 작업 수행 (포인트 지급, 내역 저장)
            paymentService.chargePoint(dto);

            return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "SUCCESS", "포인트가 충전되었습니다.", null));
        } catch (Exception e) {
            // 2. 우리 서버 작업 실패 시 보상 트랜잭션 발동!
            log.error("포인트 충전 로직 실패로 인한 결제 취소 시도 - impUid: {}, 에러: {}", dto.getImpUid(), e.getMessage());

            // 포트원 서버에 결체 취소 요청 전송
            // dto.getImpUid()가 포트원 결제 고유 ID(paymentId)입니다.
            portoneClient.cancelPayment(dto.getImpUid(), "서버 내부 오류로 인한 포인트 기록 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "FAIL",
                            "충전 중 문제가 발생하여 결제가 자동 취소되었습니다.", null));
        }
    }

}
