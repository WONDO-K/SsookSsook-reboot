package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.domain.*;
import com.stillalive.Ssook_BE.enums.Gender;
import com.stillalive.Ssook_BE.enums.PayType;
import com.stillalive.Ssook_BE.enums.Progress;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.pay.dto.ParentHistoryResDto;
import com.stillalive.Ssook_BE.pay.repository.BalanceRepository;
import com.stillalive.Ssook_BE.pay.repository.ParentHistoryRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final BalanceRepository balanceRepository;

    private final ParentHistoryRepository parentHistoryRepository;


    @Transactional
    public void join(ParentSignupReqDto parentSignupReqDto) {

        String name = parentSignupReqDto.getName();
        String tel = parentSignupReqDto.getTel();
        LocalDate bday = parentSignupReqDto.getBday();
        Gender gender = parentSignupReqDto.getGender();
        String loginId = parentSignupReqDto.getLoginId();
        String password = parentSignupReqDto.getPassword();

        // 아이디 중복 체크
        if (existsByLoginId(loginId) || childRepository.existsByLoginId(loginId)) {
            throw new SsookException(ErrorCode.DUPLICATE_LOGIN_ID);
        }


        // 전화번호 중복 체크
        if (parentRepository.existsByTel(tel) || childRepository.existsByTel(tel)) {
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
        return parentRepository.existsByLoginId(loginId);
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

        // TODO (wondok) 자녀에게 알림 전송

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

        saveParentHistory(parent, PayType.SEND, dto.getAmount(), parent.getPoint());


    }

    private void saveParentHistory(Parent parent, PayType type, int price, int balance) {
        ParentHistory parentHistory = new ParentHistory();
        parentHistory.setParent(parent);
        parentHistory.setType(type);
        parentHistory.setPrice(price);
        parentHistory.setBalance(balance);
        parentHistory.setCreatedAt(LocalDateTime.now());
        parentHistoryRepository.save(parentHistory);
        log.info("부모 내역 저장 완료 - 부모 ID: {}, 내역 ID: {}", parent.getParentId(), parentHistory.getId());
    }


    public UserInfoResDto getUserInfo(String loginId) {
        Parent parent = parentRepository.findByLoginId(loginId)
                .orElseThrow(() -> new SsookException(ErrorCode.NOT_FOUND_PARENT));

        return UserInfoResDto.builder()
                .userId(parent.getParentId())
                .name(parent.getName())
                .tel(parent.getTel())
                .bday(parent.getBday())
                .gender(parent.getGender())
                .loginId(parent.getLoginId())
                .createdAt(parent.getCreatedAt())
                .updatedAt(parent.getUpdatedAt())
                .isParent(true)
                .build();
    }

    public MyChildPointResDto getMyChildPoint(Integer parentId, Integer childId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 ID입니다: " + parentId));
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀 ID입니다: " + childId));

        // 부모-자식 관계 확인
        familyRelationRepository.findByParentAndChildAndStatus(parent, child, Progress.YES)
                .orElseThrow(() -> new IllegalStateException("부모와 자녀 관계가 존재하지 않습니다."));

        return MyChildPointResDto.builder()
                .childId(child.getChildId())
                .childName(child.getName())
                .point(child.getPoint())
                .build();
    }

    public MyChildBalanceResDto getMyChildBalance(Integer parentId, Integer childId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 ID입니다: " + parentId));
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀 ID입니다: " + childId));

        // 부모-자식 관계 확인
        familyRelationRepository.findByParentAndChildAndStatus(parent, child, Progress.YES)
                .orElseThrow(() -> new IllegalStateException("부모와 자녀 관계가 존재하지 않습니다."));

        Card card = child.getCard();
        Balance balance = balanceRepository.findByCard(card)
                .orElseThrow(() -> new IllegalArgumentException("카드 정보가 존재하지 않습니다."));

        return MyChildBalanceResDto.builder()
                .childId(child.getChildId())
                .childName(child.getName())
                .balance(balance.getCurrentBalance())
                .build();
    }

    public List<ParentHistoryResDto> getParentPaymentList(int parentId, Integer months) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 ID입니다: " + parentId));

        List<ParentHistory> parentHistories;
        if (months == null) {
            parentHistories = parentHistoryRepository.findAllByParent(parent);
        } else {
            parentHistories = parentHistoryRepository.findAllByParentAndCreatedAtAfter(parent, LocalDateTime.now().minusMonths(months));
        }

        return parentHistories.stream()
                .map(ParentHistoryResDto::toDto)
                .collect(Collectors.toList());
    }
}
