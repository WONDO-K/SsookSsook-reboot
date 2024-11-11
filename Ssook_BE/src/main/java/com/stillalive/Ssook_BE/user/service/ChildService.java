package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.domain.*;
import com.stillalive.Ssook_BE.enums.Gender;
import com.stillalive.Ssook_BE.enums.Progress;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.user.dto.RequestPointReqDto;
import com.stillalive.Ssook_BE.user.dto.*;
import com.stillalive.Ssook_BE.user.repository.*;
import com.stillalive.Ssook_BE.util.alert.AlertService;
import com.stillalive.Ssook_BE.util.alert.dto.AlertDtoMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final FamilyRelationRepository familyRelationRepository;
    private final SchoolRepository schoolRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ParentRepository parentRepository;
    private final AlertService alertService;
    private final BodyProfileRepository bodyProfileRepository;

    private static final Logger log = LoggerFactory.getLogger(ChildService.class);

    @Transactional
    public void join(ChildSignupReqDto childSignupReqDto) {

        String name = childSignupReqDto.getName();
        String tel = childSignupReqDto.getTel();
        LocalDate bday = childSignupReqDto.getBday();
        Gender gender = childSignupReqDto.getGender();
        String loginId = childSignupReqDto.getLoginId();
        String password = childSignupReqDto.getPassword();
        Integer schoolId = childSignupReqDto.getSchoolId();

        // 아이디 중복 체크
        if (existsByLoginId(loginId) || parentRepository.existsByLoginId(loginId)) {
            throw new SsookException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 전화번호 중복 체크
        if (childRepository.existsByTel(tel) || parentRepository.existsByTel(tel)) {
            throw new SsookException(ErrorCode.DUPLICATE_TEL);
        }

        // 해당 학교가 없을 때
        Optional<School> school = schoolRepository.findById(schoolId).or(() -> {
            throw new SsookException(ErrorCode.NOT_FOUND_SCHOOL);
        });

        childRepository.save(Child.builder()
                .name(name)
                .tel(tel)
                .bday(bday)
                .gender(gender)
                .loginId(loginId)
                .password(bCryptPasswordEncoder.encode(password))
                .school(school.get())
                .build());


    }

    public boolean existsByLoginId(String loginId) {
        return childRepository.existsByLoginId(loginId);
    }

    // 가족 신청 목록 조회
    public FamilyReqListResDto getFamilyReqList(Integer childId) {
        List<FamilyReqResDto> list = familyRelationRepository.findByChild_ChildIdAndStatus(childId, Progress.PENDING)
                .stream()
                .map(familyRelation -> FamilyReqResDto.builder()
                        .familyRelationId(familyRelation.getId())
                        .parentName(familyRelation.getParent().getName())
                        .parentTel(familyRelation.getParent().getTel())
                        .requestedAt(familyRelation.getCreatedAt())
                        .build())
                .toList();

        return FamilyReqListResDto.builder()
                .familyReqList(list)
                .build();

    }

    // 가족 신청 수락
    @Transactional
    public void acceptParent(Integer childId, Integer familyRelationId) {

        FamilyRelation familyRelation = familyRelationRepository.findById(familyRelationId).orElseThrow(() -> {
            throw new SsookException(ErrorCode.NOT_FOUND_FAMILY_RELATION);
        });

        if (!familyRelation.getChild().getChildId().equals(childId)) {
            throw new SsookException(ErrorCode.NOT_MY_FAMILY_RELATION);
        }

        // 가족 신청 상태 변경
        familyRelation.accept();

        alertService.sendAlert(
                familyRelation.getParent().getParentId(),
                AlertDtoMapper.toAcceptChildAlert(familyRelation.getParent().getParentId(), familyRelation.getChild().getName())
        );

    }

    // 부모 목록 조회
    public ParentListResDto getParentList(Integer childId) {
        List<ParentResDto> list = familyRelationRepository.findAllByChild_ChildIdAndStatus(childId, Progress.YES)
                .stream()
                .map(familyRelation -> ParentResDto.builder()
                        .parentId(familyRelation.getParent().getParentId())
                        .name(familyRelation.getParent().getName())
                        .tel(familyRelation.getParent().getTel())
                        .bday(familyRelation.getParent().getBday())
                        .gender(familyRelation.getParent().getGender())
                        .build())
                .toList();

        return ParentListResDto.builder()
                .parentList(list)
                .totalItems(list.size())
                .build();
    }

    public void requestPoint(RequestPointReqDto dto) {
        Child child = childRepository.findById(dto.getChildId())
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_CHILD));
        Parent parent = parentRepository.findById(dto.getParentId())
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_PARENT));

        // 부모-자식 관계 확인
        familyRelationRepository.findByParentAndChildAndStatus(parent, child, Progress.YES)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_MY_FAMILY_RELATION));


        // 알림 전송
        alertService.sendAlert(
                parent.getParentId(),
                AlertDtoMapper.toRequestPointAlert(parent.getParentId(), child.getName())
        );

        log.info("포인트 요청 알림 전송 완료 - 부모 ID: {}, 자녀 ID: {}", parent.getParentId(), child.getChildId());
    }

    public UserInfoResDto getUserInfo(String loginId) {
        Child child = childRepository.findByLoginId(loginId).orElseThrow(() -> {
            throw new SsookException(ErrorCode.NOT_FOUND_CHILD);
        });

        return UserInfoResDto.builder()
                .userId(child.getChildId())
                .name(child.getName())
                .tel(child.getTel())
                .bday(child.getBday())
                .gender(child.getGender())
                .loginId(child.getLoginId())
                .createdAt(child.getCreatedAt())
                .updatedAt(child.getUpdatedAt())
                .isParent(false)
                .build();
    }

    //    아이 신체정보 기입
    @Transactional
    public void inputBodyProfile(Integer childId, BodyProfileReqDto bodyProfileReqDto) {
        Child child = childRepository.findById(childId).orElseThrow(() -> {
            throw new SsookException(ErrorCode.NOT_FOUND_CHILD);
        });

        BodyProfile bodyProfile = new BodyProfile(child, bodyProfileReqDto.getHeight(), bodyProfileReqDto.getWeight(), bodyProfileReqDto.getActivity());

        bodyProfileRepository.save(bodyProfile);
        log.info("아이 신체 정보 기입 완료 - 아이 ID: {}", childId);

    }

    //   아이 신체정보 수정
    @Transactional
    public void updateBodyProfile(Integer childId, BodyProfileReqDto bodyProfileReqDto) {
        Child child = childRepository.findById(childId).orElseThrow(() -> {
            throw new SsookException(ErrorCode.NOT_FOUND_CHILD);
        });

        BodyProfile bodyProfile = bodyProfileRepository.findByChild(child).orElseThrow(() -> {
            throw new SsookException(ErrorCode.NOT_FOUND_BODYPROFILE);
        });

        bodyProfile.updateProfile(bodyProfileReqDto.getHeight(), bodyProfileReqDto.getWeight(), bodyProfileReqDto.getActivity());

        log.info("아이 신체 정보 수정 완료 - 아이 ID: {}", childId);
    }
}