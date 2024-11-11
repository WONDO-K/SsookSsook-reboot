package com.stillalive.Ssook_BE.user.controller;

import com.stillalive.Ssook_BE.common.ApiResponse;
import com.stillalive.Ssook_BE.user.dto.RequestPointReqDto;
import com.stillalive.Ssook_BE.user.CustomUserDetails;
import com.stillalive.Ssook_BE.user.dto.*;
import com.stillalive.Ssook_BE.user.repository.FamilyRelationRepository;
import com.stillalive.Ssook_BE.user.service.ChildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "Child Controller", description = "자녀 API")
@RequestMapping("/api/v1/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;
    private final FamilyRelationRepository familyRelationRepository;

    @Operation(summary = "청소년 회원가입", description = "청소년 회원가입을 진행합니다.")
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Void>> join(@RequestBody ChildSignupReqDto childSignupReqDto) {
        log.info("childSignupReqDto = {}", childSignupReqDto);
        childService.join(childSignupReqDto);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "청소년 회원가입이 완료되었습니다.",
                        null
                )
        );
    }

    // 로그인, 로그아웃은 필터에서 구현하고 스웨거 문서에만 표시하기위해 작성---
    @Operation(summary = "청소년 로그인", description = "청소년 로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@RequestBody ChildLoginReqDto childLoginReqDto) {

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "청소년 로그인이 완료되었습니다.",
                        null
                )
        );
    }

    @Operation(summary = "청소년 childId 찾기", description = "childId를 찾습니다.")
    @GetMapping("/childId")
    public ResponseEntity<ApiResponse<Integer>> findChildId(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println("customUserDetails.getChildId() = " + customUserDetails.getChildId());

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "childId를 찾았습니다.",
                        customUserDetails.getChildId()
                )
        );
    }

    @Operation(summary = "받은 가족 신청 목록 조회", description = "받은 가족 신청 목록을 조회합니다.")
    @GetMapping("/family")
    public ResponseEntity<ApiResponse<FamilyReqListResDto>> findFamilyReqList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Integer childId = customUserDetails.getChildId();

        // 서비스에서 가족 요청 목록 조회
        FamilyReqListResDto familiyReqListResDto = childService.getFamilyReqList(childId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "받은 가족 신청 목록을 조회했습니다.",
                        familiyReqListResDto
                )
        );
    }

    @Operation(summary = "자녀 추가 수락", description = "자녀 추가 요청을 수락합니다.")
    @PostMapping("/parent")
    public ResponseEntity<ApiResponse<Void>> acceptParent(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody FamilyReqAcceptReqDto familyReqAcceptReqDto) {

        Integer childId = customUserDetails.getChildId();

        childService.acceptParent(childId, familyReqAcceptReqDto.getFamilyRelationId());

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "자녀 추가 요청을 수락했습니다.",
                        null
                )
        );
    }

    @Operation(summary = "부모 목록 조회", description = "부모 목록을 조회합니다.")
    @GetMapping("/parent")
    public ResponseEntity<ApiResponse<ParentListResDto>> findParentList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Integer childId = customUserDetails.getChildId();

        // 서비스에서 부모 목록 조회
        ParentListResDto parentListResDto = childService.getParentList(childId);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "부모 목록을 조회했습니다.",
                        parentListResDto
                )
        );
    }

    @Operation(summary = "포인트 요청", description = "포인트 요청을 합니다.")
    @PostMapping("/point/request")
    public ResponseEntity<ApiResponse<Void>> requestPoint(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody RequestPointReqDto dto) {

            Integer childId = customUserDetails.getChildId();
            dto.setChildId(childId);

            childService.requestPoint(dto);

            return ResponseEntity.ok(
                    ApiResponse.of(
                            200,
                            "OK",
                            "포인트 요청을 했습니다.",
                            null
                    )
            );
        }

    // 아이 신체 정보 기입
    @Operation(summary = "아이 신체 정보 기입", description = "아이 신체 정보를 기입합니다.")
    @PostMapping("/bodyprofile")
    public ResponseEntity<ApiResponse<Void>> inputBodyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BodyProfileReqDto bodyProfileReqDto) {
        Integer childId = customUserDetails.getChildId();

        childService.inputBodyProfile(childId,bodyProfileReqDto);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "아이 신체 정보를 기입했습니다.",
                        null
                )
        );
    }

    // 아이 신체 정보 수정
    @Operation(summary = "아이 신체 정보 수정", description = "아이 신체 정보를 수정합니다.")
    @PutMapping("/bodyprofile")
    public ResponseEntity<ApiResponse<Void>> updateBodyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BodyProfileReqDto bodyProfileReqDto) {
        Integer childId = customUserDetails.getChildId();

        childService.updateBodyProfile(childId,bodyProfileReqDto);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "OK",
                        "아이 신체 정보를 수정했습니다.",
                        null
                )
        );
    }
}