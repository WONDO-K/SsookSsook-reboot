package com.stillalive.Ssook_BE.pay.service.impl;

import com.stillalive.Ssook_BE.domain.*;
import com.stillalive.Ssook_BE.enums.PayType;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import com.stillalive.Ssook_BE.menu.repository.MenuRepository;
import com.stillalive.Ssook_BE.menu.service.MenuNutService;
import com.stillalive.Ssook_BE.pay.dto.*;
import com.stillalive.Ssook_BE.pay.repository.BalanceRepository;
import com.stillalive.Ssook_BE.pay.repository.CardRepository;
import com.stillalive.Ssook_BE.pay.repository.HistoryRepository;
import com.stillalive.Ssook_BE.pay.service.PaymentService;
import com.stillalive.Ssook_BE.user.repository.ChildRepository;
import com.stillalive.Ssook_BE.user.repository.FamilyRelationRepository;
import com.stillalive.Ssook_BE.user.repository.ParentRepository;
import com.stillalive.Ssook_BE.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CardRepository cardRepository;
    private final ChildRepository childRepository;
    private final BalanceRepository balanceRepository;
    private final HistoryRepository historyRepository;
    private final FamilyRelationRepository familyRelationRepository;
    private final MenuRepository menuRepository;
    private final ParentRepository parentRepository;
//    private final MenuNutService menuNutService;
    private final JWTUtil jwtUtil;



    // 로그
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Transactional // balance와 user의 데이터를 변경하는 트랜잭션 처리
    public void processPayment(PaymentReqDto dto, Child child) {

        Card card = cardRepository.findByChild(child)
                .orElseThrow(() -> {
                    log.error("카드 정보 조회 실패 - 자녀 ID: {}", child.getChildId());
                    return new SsookException(ErrorCode.CARD_NOT_FOUND);
                });

//        // 1. 카드 유효성 검증 (카드 존재 여부)
//        Card card = cardRepository.findById(dto.getCardId())
//                .orElseThrow(() -> new SsookException(ErrorCode.CARD_NOT_FOUND));

//        // 1. 카드 유효성 검증 (카드 존재 여부)
//        Card card = cardRepository.findByCardToken(dto.getCardToken())
//                .orElseThrow(() -> new SsookException(ErrorCode.CARD_NOT_FOUND));

        // 2. 카드 만료 여부 확인
        // 현재 날짜를 기준으로 유효기간 확인
        YearMonth now = YearMonth.now();
        if (card.getExpirationDate().isBefore(now)) {
            throw new SsookException(ErrorCode.CARD_EXPIRED);
        }
        // 3. 카드 활성화 여부 확인
        if (!card.isActive()) {
            log.error("비활성화된 카드 사용 시도 - 카드 ID: {}", card.getId());
            throw new SsookException(ErrorCode.PAYMENT_PROCESSING_FAILED);
        }

        // 4. 유저 정보 확인
        int paymentAmount = dto.getAmount(); // 결제해야 하는 금액

        // 5. 결제 금액이 0인 경우 잔액 확인 없이 결제 처리
        if (dto.getAmount() == 0) {
            log.info("결제 금액 0원 - 잔액 확인 없이 결제 처리 진행");
            ChildHistory childHistory = createHistory(dto, card, 0, 0);
            historyRepository.save(childHistory);
            log.info("결제 내역 저장 완료 - 결제 금액: 0원");
            return;
        }

        int maxCardLimit = 8000; // 아동 급식 카드 일일 결제 한도

        // 6. 잔액 및 포인트 확인 및 결제 처리
        Balance balance = balanceRepository.findByCard(card)
                .orElseThrow(() -> {
                    log.error("잔액 정보 조회 실패 - 카드 ID: {}", card.getId());
                    return new SsookException(ErrorCode.INSUFFICIENT_BALANCE);
                });

        // 7. 일일 한도 검사
        int dailySpent = balance.getDailySpentAmount();
        if ((dailySpent + paymentAmount) > 24000) {
            log.error("일일 한도 초과 - 누적 지출 금액: {}", dailySpent + paymentAmount);
            throw new SsookException(ErrorCode.EXCEEDS_DAILY_LIMIT);
        }

        int currentBalance = balance.getCurrentBalance();
        int availableCardAmount = Math.min(currentBalance, maxCardLimit); // 카드로 결제 가능한 최대 금액 (잔액과 8000원 한도 중 작은 값)
        int requiredPointAmount = paymentAmount > availableCardAmount ? paymentAmount - availableCardAmount : 0;

        // 포인트 부족 확인
        if (requiredPointAmount > child.getPoint()) {
            log.error("포인트 부족 - 필요 포인트: {}, 현재 포인트: {}", requiredPointAmount, child.getPoint());
            throw new SsookException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        int cardPrice = 0;
        int pointPrice = 0;

        // 8. 카드 잔액 및 포인트 차감
        if (paymentAmount <= availableCardAmount) {
            balance.setCurrentBalance(currentBalance - paymentAmount);
            cardPrice = paymentAmount;
            log.info("잔액 차감 완료 - 차감 금액: {}", cardPrice);
        } else {
            balance.setCurrentBalance(currentBalance - availableCardAmount);
            cardPrice = availableCardAmount;
            pointPrice = requiredPointAmount;
            child.setPoint(child.getPoint() - pointPrice);
            log.info("잔액 및 포인트 차감 완료 - 카드 사용 금액: {}, 포인트 사용 금액: {}", cardPrice, pointPrice);
        }

        // 9. 일일 지출 금액, 최근 사용 날짜 및 갱신 시간 업데이트
        balance.setDailySpentAmount(dailySpent + paymentAmount);
        balance.setLastSpentDate(Timestamp.valueOf(LocalDateTime.now()));
        balance.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        balanceRepository.save(balance);
        childRepository.save(child);

        log.info("dto의 paydetails: {}", dto.getPayDetails());

        // 10. 결제 내역 저장
        ChildHistory childHistory = createHistory(dto, card, cardPrice, pointPrice);
        historyRepository.save(childHistory);
        log.info("결제 내역 저장 완료 - 카드 사용 금액: {}, 포인트 사용 금액: {}", cardPrice, pointPrice);
    }

    @Override
    public MyCardResDto getMyCard(int childId) {
        log.info("카드 정보 조회 시작 - 자녀 ID: {}", childId);

        // 자녀 정보 조회
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.error("사용자 정보 조회 실패 - 자녀 ID: {}", childId);
                    return new SsookException(ErrorCode.USER_NOT_FOUND);
                });
        log.info("사용자 정보 조회 성공 - 자녀 ID: {}", childId);

        // 카드 정보 조회
        Card card = cardRepository.findByChild(child)
                .orElseThrow(() -> {
                    log.error("카드 정보 조회 실패 - 자녀 ID: {}", childId);
                    return new SsookException(ErrorCode.CARD_NOT_FOUND);
                });
        log.info("카드 정보 조회 성공 - 카드 ID: {}", card.getId());

        // 잔액 정보 조회
        Balance balance = balanceRepository.findByCard(card)
                .orElseThrow(() -> {
                    log.error("잔액 정보 조회 실패 - 카드 ID: {}", card.getId());
                    return new SsookException(ErrorCode.BALANCE_NOT_FOUND);
                });
        log.info("잔액 정보 조회 성공 - 카드 ID: {}, 잔액: {}", card.getId(), balance.getCurrentBalance());

        // MyCardResDto 생성 및 반환
        MyCardResDto response = MyCardResDto.toDto(childId, card, balance);
        log.info("카드 정보 반환 - 자녀 ID: {}, 카드 ID: {}, 잔액: {}", childId, card.getId(), balance.getCurrentBalance());

        return response;
    }

    @Override
    public void registerCard(RegisterCardReqDto dto, int childId) {
        log.info("카드 등록 시작 - 자녀 ID: {}", childId);

        // 자녀 정보 조회
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.error("사용자 정보 조회 실패 - 자녀 ID: {}", childId);
                    return new SsookException(ErrorCode.USER_NOT_FOUND);
                });
        log.info("사용자 정보 조회 성공 - 자녀 ID: {}", childId);

        // 카드 존재 여부 확인
        boolean exists = cardRepository.existsByChild(child);
        if (exists) {
            log.error("이미 등록된 카드 존재 - 자녀 ID: {}", childId);
            throw new SsookException(ErrorCode.CARD_ALREADY_REGISTERED);
        }
        log.info("카드 중복 없음 - 신규 카드 등록 진행");

        // 카드번호와 CVC 토큰화
        String cardToken;
        try {
            cardToken = jwtUtil.tokenizationCard(dto.getCardNumber(), dto.getCvcCode());
            log.info("카드 토큰화 성공 - 자녀 ID: {}", childId);
        } catch (Exception e) {
            log.error("카드 토큰화 실패 - 자녀 ID: {}", childId, e);
            throw new SsookException(ErrorCode.TOKENIZATION_ERROR);
        }

        // 카드 및 잔액 정보 저장
        try {
            Card card = Card.builder()
                    .child(child)
                    .cardToken(cardToken)
                    .expirationDate(dto.getExpirationAsYearMonth())
                    .isActive(dto.isActive())
                    .build();
            cardRepository.save(card);
            log.info("카드 정보 저장 성공 - 카드 ID: {}", card.getId());

            Balance balance = Balance.builder()
                    .card(card)
                    .currentBalance(50000)
                    .build();
            balanceRepository.save(balance);
            log.info("잔액 정보 초기화 및 저장 성공 - 카드 ID: {}", card.getId());
        } catch (Exception e) {
            log.error("카드 등록 중 DB 저장 오류 - 자녀 ID: {}", childId, e);
            throw new SsookException(ErrorCode.CARD_REGISTRATION_FAILED);
        }

        log.info("카드 등록 완료 - 자녀 ID: {}", childId);
    }

    @Override
    public PointBalanceResDto getPointBalance(int childId, boolean isChild) {
        log.info("포인트 잔액 조회 시작 - 사용자 ID: {}, isChild: {}", childId, isChild);

        if (isChild) {
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> {
                        log.error("자녀 정보 조회 실패 - 자녀 ID: {}", childId);
                        return new SsookException(ErrorCode.USER_NOT_FOUND);
                    });
            log.info("자녀 포인트 잔액 조회 성공 - 자녀 ID: {}, 포인트 잔액: {}", childId, child.getPoint());
            return PointBalanceResDto.builder().pointBalance(child.getPoint()).build();
        } else {
            Parent parent = parentRepository.findById(childId)
                    .orElseThrow(() -> {
                        log.error("부모 정보 조회 실패 - 부모 ID: {}", childId);
                        return new SsookException(ErrorCode.USER_NOT_FOUND);
                    });
            log.info("부모 포인트 잔액 조회 성공 - 부모 ID: {}, 포인트 잔액: {}", childId, parent.getPoint());
            return PointBalanceResDto.builder().pointBalance(parent.getPoint()).build();
        }
    }

    @Override
    @Transactional
    public void chargePoints(int parentId, int amount) {
        log.info("포인트 충전 시작 - 부모 ID: {}, 충전 금액: {}", parentId, amount);

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> {
                    log.error("부모 정보 조회 실패 - 부모 ID: {}", parentId);
                    return new SsookException(ErrorCode.USER_NOT_FOUND);
                });

        int newPointBalance = parent.getPoint() + amount;
        parent.setPoint(newPointBalance);
        parentRepository.save(parent);
        log.info("포인트 충전 성공 - 부모 ID: {}, 충전 후 잔액: {}", parentId, newPointBalance);
    }


    @Override
    public List<ChildHistoryResDto> getPaymentList(int childId, Integer months) {
        log.info("결제 내역 조회 시작 - 자녀 ID: {}, 조회 기간: {}개월", childId, months);

        if (months == null || months <= 0) {
            months = 1;
        }
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<ChildHistory> histories = historyRepository.findByChildIdAndDateRange(childId, startDate, endDate);
        log.info("결제 내역 조회 완료 - 자녀 ID: {}, 내역 수: {}", childId, histories.size());

        return histories.stream().map(ChildHistoryResDto::toDto).collect(Collectors.toList());
    }

    @Override
    public ChildHistory getPaymentDetail(int userId, int historyId) {
        log.info("결제 상세 내역 조회 시작 - 사용자 ID: {}, 내역 ID: {}", userId, historyId);

        // 거래 내역 조회
        ChildHistory childHistory = historyRepository.findById(historyId)
                .orElseThrow(() -> {
                    log.error("거래 내역 조회 실패 - 내역 ID: {}", historyId);
                    return new SsookException(ErrorCode.HISTORY_NOT_FOUND);
                });
        log.info("거래 내역 조회 성공 - 내역 ID: {}", historyId);

        int historyChildId = childHistory.getCard().getChild().getChildId();

        // 자식 본인 여부 확인
        if (historyChildId == userId) {
            log.info("본인 거래 내역 조회 허용 - 사용자 ID: {}", userId);
            return childHistory;
        }

        // 부모가 자식의 거래 내역에 접근하려는 경우, 부모-자식 관계 확인
        boolean isRelated = familyRelationRepository.findByParent_ParentIdAndChild_ChildId(userId, historyChildId).isPresent();
        if (isRelated) {
            log.info("부모-자식 관계 확인 - 부모 ID: {}, 자녀 ID: {}", userId, historyChildId);
            return childHistory;
        }

        // 관계가 없으면 접근 불가 예외 발생
        log.error("접근 불가 - 사용자 ID: {}, 내역 ID: {}", userId, historyId);
        throw new SsookException(ErrorCode.ACCESS_DENIED);
    }

    // createHistory 메서드 수정
    private ChildHistory createHistory(PaymentReqDto dto, Card card, int cardPrice, int pointPrice) {
        ChildHistory childHistory = ChildHistory.builder()
                .card(card)
                .cardPrice(cardPrice)
                .pointPrice(pointPrice)
                .historyTime(LocalDateTime.now())
                .historyType(PayType.PAYMENT)
                .build();

        List<PayDetail> payDetailList = dto.getPayDetails().stream().map(detailDto -> {
            Menu menu = menuRepository.findByName(detailDto.getMenuName())
                    .orElseThrow(() -> new SsookException(ErrorCode.MENU_NOT_FOUND));

            // MenuNut 조회 또는 생성
//            menuNutService.createMenuNutIfNotExists(menu.getName());

            return new PayDetail(childHistory, menu, detailDto.getQuantity());
        }).collect(Collectors.toList());

        childHistory.getPayDetails().addAll(payDetailList);
        return childHistory;
    }

    @Override
    @Transactional
    public void chargePoint(ChargePointReqDto dto) {
        log.info("포인트 충전 시작 - 부모 ID: {}, 충전 금액: {}", dto.getUserId(), dto.getAmount());

        // 부모 유저의 존재 여부 확인
        Parent parent = parentRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.error("부모 정보 조회 실패 - 부모 ID: {}", dto.getUserId());
                    return new SsookException(ErrorCode.USER_NOT_FOUND);
                });
        log.info("부모 정보 조회 성공 - 부모 ID: {}", dto.getUserId());

        // 포인트 충전 로직
        int newPointBalance = parent.getPoint() + dto.getAmount();
        parent.setPoint(newPointBalance);
        parentRepository.save(parent); // 충전된 포인트를 부모 계정에 저장
        log.info("포인트 충전 완료 - 부모 ID: {}, 충전 후 잔액: {}", dto.getUserId(), newPointBalance);
    }

    @Override
    public void changeCard(RegisterCardReqDto dto, int childId) {
        log.info("카드 정보 변경 시작 - 자녀 ID: {}", childId);

        // 자녀 정보 조회
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.error("자녀 정보 조회 실패 - 자녀 ID: {}", childId);
                    return new SsookException(ErrorCode.USER_NOT_FOUND);
                });
        log.info("자녀 정보 조회 성공 - 자녀 ID: {}", childId);

        // 카드 정보 조회
        Card card = cardRepository.findByChild(child)
                .orElseThrow(() -> {
                    log.error("카드 정보 조회 실패 - 자녀 ID: {}", childId);
                    return new SsookException(ErrorCode.CARD_NOT_FOUND);
                });
        log.info("카드 정보 조회 성공 - 카드 ID: {}", card.getId());

        // 카드번호와 CVC 토큰화
        String cardToken;
        try {
            cardToken = jwtUtil.tokenizationCard(dto.getCardNumber(), dto.getCvcCode());
            log.info("카드 토큰화 성공 - 자녀 ID: {}", childId);
        } catch (Exception e) {
            log.error("카드 토큰화 실패 - 자녀 ID: {}", childId, e);
            throw new SsookException(ErrorCode.TOKENIZATION_ERROR);
        }

        // 카드 정보 업데이트
        try {
            card.setCardToken(cardToken);
            card.setExpirationDate(dto.getExpirationAsYearMonth());
            card.setActive(dto.isActive());
            cardRepository.save(card);
            log.info("카드 정보 업데이트 성공 - 카드 ID: {}", card.getId());
        } catch (Exception e) {
            log.error("카드 정보 업데이트 중 DB 저장 오류 - 자녀 ID: {}", childId, e);
            throw new SsookException(ErrorCode.CARD_REGISTRATION_FAILED);
        }

        log.info("카드 정보 변경 완료 - 자녀 ID: {}", childId);
    }

}
