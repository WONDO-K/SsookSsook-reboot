package com.stillalive.Ssook_BE.user.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.domain.ChildHistory;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.pay.controller.PayController;
import com.stillalive.Ssook_BE.pay.dto.ChildHistoryResDto;
import com.stillalive.Ssook_BE.pay.dto.ParentHistoryResDto;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.dto.*;
import com.stillalive.Ssook_BE.user.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Parent Controller", description = "부모 API")
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;
    private static final Logger log = LoggerFactory.getLogger(ParentController.class);


    @Operation(summary = "부모 회원가입", description = "부모 회원가입을 진행합니다.")
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> join(@RequestBody ParentSignupReqDto parentSignupReqDto) {

        parentService.join(parentSignupReqDto);

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "부모 회원가입이 완료되었습니다.",
                        null
                )
        );
    }

    // 로그인, 로그아웃은 필터에서 구현하고 스웨거 문서에만 표시하기위해 작성---
    @Operation(summary = "부모 로그인", description = "부모 로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@RequestBody ParentLoginReqDto parentLoginReqDto) {

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "부모 로그인이 완료되었습니다.",
                        null
                )
        );
    }

    @Operation(summary = "부모 parentId 찾기", description = "parentId를 찾습니다.")
    @GetMapping("/parentId")
    public ResponseEntity<ApiResponse<Integer>> findParentId(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "parentId를 찾았습니다.",
                        customUserDetails.getParentId()
                )
        );

    }

    @Operation(summary = "자녀 추가", description = "자녀를 추가합니다.")
    @PostMapping("/child")
    public ResponseEntity<ApiResponse<Void>> addChild(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody AddChildReqDto addChildReqDto) {

        Integer parentId = customUserDetails.getParentId();

        parentService.addChild(parentId, addChildReqDto);

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "자녀 추가가 완료되었습니다.",
                        null
                )
        );
    }

    @Operation(summary = "신청한 자녀 추가 목록 조회", description = "신청한 자녀 추가 목록을 조회합니다.")
    @GetMapping("/req-list")
    public ResponseEntity<ApiResponse<AddChildReqListResDto>> findReqChildList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Integer parentId = customUserDetails.getParentId();

        AddChildReqListResDto reqChildListResDto = parentService.getReqChildList(parentId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "신청한 자녀 추가 목록을 조회했습니다.",
                        reqChildListResDto
                )
        );
    }

    @Operation(summary = "자녀 목록 조회", description = "자녀 목록을 조회합니다.")
    @GetMapping("/child")
    public ResponseEntity<ApiResponse<ChildListResDto>> findChildList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Integer parentId = customUserDetails.getParentId();

        ChildListResDto childListResDto = parentService.findChildList(parentId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "자녀 목록을 조회했습니다.",
                        childListResDto
                )
        );
    }

    @Operation(summary = "자녀 정보 상세 조회", description = "자녀 정보를 상세를 조회합니다.")
    @GetMapping("/child/{childId}")
    public ResponseEntity<ApiResponse<ChildDetailResDto>> findChild(@PathVariable Integer childId) {

        ChildDetailResDto childDetailResDto = parentService.findChild(childId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "자녀 상세를 조회했습니다.",
                        childDetailResDto
                )
        );
    }
    @PostMapping("/point/transfer")
    @Operation(summary = "포인트 전송", description = "포인트를 전송합니다.")
    public ResponseEntity<ApiResponse<Void>> transferPoint(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody PointTransferReqDto dto) {

        Integer parentId = userDetails.getParentId();
        dto.setParentId(parentId);

        parentService.transferPoint(parentId, dto);

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "포인트 전송이 완료되었습니다.",
                        null
                )
        );
    }

    @GetMapping("/point/{childId}")
    @Operation(summary = "자녀 포인트 조회", description = "자녀의 포인트를 조회합니다.")
    public ResponseEntity<ApiResponse<MyChildPointResDto>> findPoint(@AuthenticationPrincipal CustomUserDetails userDetails ,@PathVariable(value = "childId") Integer childId) {

        int parentId = userDetails.getParentId();

        MyChildPointResDto myChildPointResDto = parentService.getMyChildPoint(parentId,childId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "자녀의 포인트를 조회했습니다.",
                        myChildPointResDto
                )
        );
    }

    @GetMapping("/balance/{childId}")
    @Operation(summary = "자녀 카드 잔액 조회", description = "자녀의 카드 잔액를 조회합니다.")
    public ResponseEntity<ApiResponse<MyChildBalanceResDto>> findBalance(@AuthenticationPrincipal CustomUserDetails userDetails ,@PathVariable(value = "childId") Integer childId) {

        int parentId = userDetails.getParentId();

        MyChildBalanceResDto myChildBalanceResDto = parentService.getMyChildBalance(parentId,childId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "자녀의 카드 잔액을 조회했습니다.",
                        myChildBalanceResDto
                )
        );
    }

    /**
     * 부모 거래 내역 리스트 조회
     * */
    @GetMapping("/history/list")
    @Operation(summary = "부모 거래 내역 리스트 조회", description = "부모 거래 내역 리스트 조회 API")
    public ResponseEntity<ApiResponse<?>> getParentPaymentList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "months", required = false) Integer months) {

        int parentId = userDetails.getParentId();
        List<ParentHistoryResDto> paymentList = parentService.getParentPaymentList(parentId, months);

        return ResponseEntity.ok(
                ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "거래내역 조회 성공", paymentList));
    }

    @GetMapping("/history/list/{childId}")
    @Operation(summary = "자녀 거래 내역 리스트 조회", description = "자녀 거래 내역 리스트 조회 API")
    public ResponseEntity<ApiResponse<?>> getChildPaymentList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(value = "childId") Integer childId,
            @RequestParam(value = "months", required = false) Integer months) {

        int parentId = userDetails.getParentId();
        List<ChildHistoryResDto> paymentList = parentService.getChildPaymentList(parentId, childId, months);

        return ResponseEntity.ok(
                ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "거래내역 조회 성공", paymentList));
    }

    @GetMapping("/history/detail/{historyId}")
    @Operation(summary = "자녀 거래내역 상세 조회", description = "자녀 거래내역 상세 조회 API")
    public ResponseEntity<ApiResponse<?>> getChildPaymentDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "childId") Integer childId,
            @PathVariable(value = "historyId") Integer historyId) {

        int userId;
        boolean isParent = userDetails.isParent();

        if (isParent) {
            userId = userDetails.getParentId();
        } else {
           log.info("접근자 타입 확인 - 자녀 유저, 자녀 유저는 자녀용 자녀 거래내역 조회 API를 사용해야 합니다.");
              throw new SsookException(ErrorCode.ACCESS_DENIED);
        }

        ChildHistory paymentDetail = parentService.getChildPaymentDetail(userId, childId ,historyId);

        ChildHistoryResDto paymentDetailDto = ChildHistoryResDto.toDto(paymentDetail);

        return ResponseEntity.ok(
                ApiResponse.of(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), "거래내역 상세 조회 성공", paymentDetailDto));

    }

}
