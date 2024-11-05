package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.FamilyRelation;
import com.stillalive.Ssook_BE.domain.Parent;
import com.stillalive.Ssook_BE.enums.Gender;
import com.stillalive.Ssook_BE.enums.Progress;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.user.dto.*;
import com.stillalive.Ssook_BE.user.repository.ChildRepository;
import com.stillalive.Ssook_BE.user.repository.FamilyRelationRepository;
import com.stillalive.Ssook_BE.user.repository.ParentRepository;
import com.stillalive.Ssook_BE.util.alert.AlertService;
import com.stillalive.Ssook_BE.util.alert.dto.AlertDtoMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final FamilyRelationRepository familyRelationRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AlertService alertService;
    private static final Logger log = LoggerFactory.getLogger(ParentService.class);


    @Transactional
    public void join(ParentSignupReqDto parentSignupReqDto) {

        String name = parentSignupReqDto.getName();
        String tel = parentSignupReqDto.getTel();
        Date bday = parentSignupReqDto.getBday();
        Gender gender = parentSignupReqDto.getGender();
        String loginId = parentSignupReqDto.getLoginId();
        String password = parentSignupReqDto.getPassword();

        // 아이디 중복 체크
        if (existsByLoginId(loginId)) {
            throw new SsookException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        // 전화번호 중복 체크
        if (parentRepository.existsByTel(tel)) {
            throw new SsookException(ErrorCode.DUPLICATE_TEL);
        }

        parentRepository.save(Parent.builder()
                .name(name)
                .tel(tel)
                .bday(bday)
                .gender(gender)
                .loginId(loginId)
                .password(bCryptPasswordEncoder.encode(password))
                .build());
    }

    // 아이디 중복 체크
    public boolean existsByLoginId(String loginId) {
        boolean result = parentRepository.existsByLoginId(loginId);
        if (result) {
            // 이미 존재하는 아이디 예외발생
            throw new SsookException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        return result;
    }

    @Transactional
    public void addChild(Integer parentId, AddChildReqDto addChildReqDto) {

        // 해당 부모 회원 조회
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_PARENT));

        // 해당 전화번호를 가진 청소년 회원 조회
        Child child = childRepository.findByTel(addChildReqDto.getTel())
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_CHILD));

        // 가족의 연 관계 설정 (신청)
        familyRelationRepository.save(FamilyRelation.builder()
                .parent(parent)
                .child(child)
                .status(Progress.PENDING)
                .build());

    }

    public AddChildReqListResDto getReqChildList(Integer parentId) {
        List<AddChildReqResDto> list = familyRelationRepository.findByParent_ParentIdAndStatus(parentId, Progress.PENDING)
                .stream()
                .map(familyRelation -> AddChildReqResDto.builder()
                        .familyRelationId(familyRelation.getId())
                        .childName(familyRelation.getChild().getName())
                        .childTel(familyRelation.getChild().getTel())
                        .requestedAt(familyRelation.getCreatedAt())
                        .build())
                .toList();

        return AddChildReqListResDto.builder()
                .addChildReqList(list)
                .build();
    }

    public ChildListResDto findChildList(Integer parentId) {

        List<FamilyRelation> familyRelations = familyRelationRepository.findAllByParent_ParentIdAndStatus(parentId, Progress.YES);

        // Get the child IDs from the family relations
        List<Integer> childIds = familyRelations.stream()
                .map(familyRelation -> familyRelation.getChild().getChildId())
                .collect(Collectors.toList());

        // Find all children using the child IDs
        List<Child> children = childRepository.findAllById(childIds);

        List<ChildResDto> childResDtos = children.stream()
                .map(child -> ChildResDto.builder()
                        .childId(child.getChildId())
                        .name(child.getName())
                        .tel(child.getTel())
                        .bday(child.getBday())
                        .gender(child.getGender())
                        .point(child.getPoint())
                        .schoolName(child.getSchool().getName())
                        .build())
                .collect(Collectors.toList());

        return ChildListResDto.builder()
                .childList(childResDtos)
                .totalItems(childResDtos.size())
                .build();

    }

    // 자녀 상세 조회
    public ChildResDto findChild(Integer childId) {
        Child child = childRepository.findById(childId).orElseThrow(() -> {
            throw new SsookException(ErrorCode.NOT_FOUND_CHILD);
        });

        return ChildResDto.builder()
                .childId(child.getChildId())
                .name(child.getName())
                .tel(child.getTel())
                .bday(child.getBday())
                .gender(child.getGender())
                .point(child.getPoint())
                .schoolName(child.getSchool().getName())
                .build();
    }

    public void transferPoint(Integer parentId, PointTransferReqDto dto) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 ID입니다: " + dto.getParentId()));
        Child child = childRepository.findById(dto.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀 ID입니다: " + dto.getChildId()));

        // 부모-자식 관계 확인
        familyRelationRepository.findByParentAndChildAndStatus(parent, child, Progress.YES)
                .orElseThrow(() -> new IllegalStateException("부모와 자녀 관계가 존재하지 않습니다."));

        if (parent.getPoint() >= dto.getAmount()) {
            parent.setPoint(parent.getPoint() - dto.getAmount());
            child.setPoint(child.getPoint() + dto.getAmount());
            parentRepository.save(parent);
            childRepository.save(child);

            // 알림 전송
            alertService.sendAlert(child.getChildId(), AlertDtoMapper.toTransferPointAlert(child.getChildId(), dto.getAmount()));

            log.info("포인트 전송 완료 - 부모 ID: {}, 자녀 ID: {}, 전송 금액: {}", parent.getParentId(), child.getChildId(), dto.getAmount());
        } else {
            log.error("포인트 전송 실패 - 부모 ID: {}, 잔액 부족", parent.getParentId());
            throw new IllegalArgumentException("포인트 잔액이 부족합니다.");
        }
    }
}
