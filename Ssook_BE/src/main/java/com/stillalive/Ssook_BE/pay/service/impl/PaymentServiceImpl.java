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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    @Transactional // balance와 user의 데이터를 변경하는 트랜잭션 처리
    public void processPayment(PaymentReqDto dto) {

        // 1. 카드 유효성 검증 (카드 존재 여부)
        Card card = cardRepository.findById(dto.getCardId())
                .orElseThrow(() -> new SsookException(ErrorCode.CARD_NOT_FOUND));

//        // 1. 카드 유효성 검증 (카드 존재 여부)
//        Card card = cardRepository.findByCardToken(dto.getCardToken())
//                .orElseThrow(() -> new SsookException(ErrorCode.CARD_NOT_FOUND));

        // 2. 카드 만료 여부 확인
        if (card.getExpirationDate().before(new java.util.Date())) {
            throw new SsookException(ErrorCode.CARD_EXPIRED);
        }

        // 3. 카드 활성화 여부 확인
        if (!card.isActive()) {
            throw new SsookException(ErrorCode.PAYMENT_PROCESSING_FAILED);
        }

        // 4. 유저 정보 확인
        Child child = card.getChild();
        int paymentAmount = dto.getAmount(); // 결제해야 하는 금액

        // 5. 결제 금액이 0인 경우 잔액 확인 없이 결제 처리
        if (dto.getAmount() == 0) {
            // 결제 내역 저장 (잔액 관련 처리 제외)
            ChildHistory childHistory = createHistory(dto, card, 0, 0);
            historyRepository.save(childHistory);
            return;
        }

        // 6. 잔액 및 포인트 확인 및 결제 처리
        Balance balance = balanceRepository.findByCard(card)
                .orElseThrow(() -> new SsookException(ErrorCode.INSUFFICIENT_BALANCE));

        // 잔액의 현재 값을 변수에 저장 -> 중복 호출 방지
        int currentBalance = balance.getCurrentBalance();

        // 7. 잔액과 포인트가 결제 금액보다 부족한 경우 예외 처리
        int totalAvailable = currentBalance + child.getPoint();

        if (totalAvailable < paymentAmount) {
            throw new SsookException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        int cardPrice = 0;
        int pointPrice = 0;
        // 8. 잔액 차감 및 포인트 사용
        if (currentBalance >= paymentAmount) { // 잔액이 결제 금액보다 큰 경우
            balance.setCurrentBalance(currentBalance - paymentAmount);
            cardPrice = paymentAmount;
        } else { // 잔액이 결제 금액보다 작은 경우 (부족 -> 포인트 사용)
            // pointPrice : 부족한 금액만큼 사용한 포인트
            pointPrice = paymentAmount - currentBalance;
            balance.setCurrentBalance(0); // 잔액 0으로 초기화
            child.setPoint(child.getPoint() - pointPrice);
        }

        balance.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        balanceRepository.save(balance);
        childRepository.save(child);

        // 9. 결제 내역 저장
        ChildHistory childHistory = createHistory(dto, card, cardPrice, pointPrice);
        historyRepository.save(childHistory);

    }

    @Override
    public MyCardResDto getMyCard(int childId) {
        // 인증된 사용자의 정보를 통해 카드 정보 조회
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new SsookException(ErrorCode.USER_NOT_FOUND));
        Card card = cardRepository.findByChild(child)
                .orElseThrow(() -> new SsookException(ErrorCode.CARD_NOT_FOUND));
        Balance balance = balanceRepository.findByCard(card).orElseThrow(() -> new SsookException(ErrorCode.BALANCE_NOT_FOUND));

        // MyCardResDto 내의 toDto 메소드를 사용하여 DTO 객체 매핑
        return MyCardResDto.toDto(childId, card, balance);
    }

    @Override
    public void registerCard(RegisterCardReqDto dto, int childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new SsookException(ErrorCode.USER_NOT_FOUND));

        // 카드번호와 CVC 토큰화 (JWT)
        String cardToken;

        try {
            // 카드번호와 cvc 코드는 직접 저장하면 불법 -> JWT로 토큰화하여 저장
            // 통합시 jwtUtil은 클래스 명에 따라 변경 필요
            cardToken = jwtUtil.tokenizationCard(dto.getCardNumber(), dto.getCvcCode());
        } catch (Exception e) {
            // JWT 토큰화 오류 처리
            throw new SsookException(ErrorCode.TOKENIZATION_ERROR);
        }

        try {
            Card card = Card.builder()
                    .child(child)
                    .cardToken(cardToken)
                    .expirationDate(dto.getExpirationDate())
                    .isActive(dto.isActive())
                    .build();
            // 카드 정보 저장
            cardRepository.save(card);

            Balance balance = Balance.builder()
                    .card(card)
                    .currentBalance(0)
                    .build();

            balanceRepository.save(balance);
        } catch (Exception e) {
            // DB 저장 오류 처리
            throw new SsookException(ErrorCode.CARD_REGISTRATION_FAILED);
        }
    }

    @Override
    public int getPointBalance(int childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new SsookException(ErrorCode.USER_NOT_FOUND));
        return child.getPoint();
    }


    @Override
    @Transactional
    public void chargePoints(int parentId, int amount) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new SsookException(ErrorCode.USER_NOT_FOUND));

        int newPointBalance = parent.getPoint() + amount;
        parent.setPoint(newPointBalance);
        parentRepository.save(parent);
    }



    @Override
    public List<ChildHistoryResDto> getPaymentList(int childId, Integer months) {

        if (months == null || months <= 0){
            months = 1;
        }
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<ChildHistory> histories = historyRepository.findByChildIdAndDateRange(childId, startDate, endDate);

        return histories.stream().map(ChildHistoryResDto::toDto).collect(Collectors.toList());
    }

    @Override
    public ChildHistory getPaymentDetail(int userId, int historyId) {
        // 거래 내역 조회
        ChildHistory childHistory = historyRepository.findById(historyId)
                .orElseThrow(() -> new SsookException(ErrorCode.HISTORY_NOT_FOUND));

        int historyChildId = childHistory.getCard().getChild().getChildId();

        // 자식 본인 여부 확인
        if (historyChildId == userId) {
            return childHistory;
        }

        // 부모가 자식의 거래 내역에 접근하려는 경우, 부모-자식 관계 확인
        boolean isRelated = familyRelationRepository.findByParent_ParentIdAndChild_ChildId(userId, historyChildId).isPresent();
        if (isRelated) {
            return childHistory;
        }

        // 관계가 없으면 접근 불가 예외 발생
        throw new SsookException(ErrorCode.ACCESS_DENIED);
    }

    // createHistory 메서드 수정
    private ChildHistory createHistory(PaymentReqDto dto, Card card, int cardPrice, int pointPrice) {
        ChildHistory childHistory = ChildHistory.builder()
                .card(card)
                .cardPrice(cardPrice)
                .pointPrice(pointPrice)
                .historyType(PayType.PAYMENT)
                .build();

        List<PayDetail> payDetailList = dto.getPayDetails().stream().map(detailDto -> {
            Menu menu = menuRepository.findById(detailDto.getMenuId())
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
        // 부모 유저의 존재 여부 확인
        Parent parent = parentRepository.findById(dto.getUserId())
                .orElseThrow(() -> new SsookException(ErrorCode.USER_NOT_FOUND));

        // 포인트 충전 로직
        parent.setPoint(parent.getPoint() + dto.getAmount());
        parentRepository.save(parent); // 충전된 포인트를 부모 계정에 저장
    }

}
