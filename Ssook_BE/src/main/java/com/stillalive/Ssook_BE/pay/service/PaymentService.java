package com.stillalive.Ssook_BE.pay.service;

import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.ChildHistory;
import com.stillalive.Ssook_BE.pay.dto.*;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PaymentService {

    void processPayment(PaymentReqDto paymentReqDto, Child child);

    MyCardResDto getMyCard(int childId);

    void registerCard(RegisterCardReqDto dto, int childId);

    PointBalanceResDto getPointBalance(int userId, boolean isChild);

    List<ChildHistoryResDto> getPaymentList(int childId, Integer months);

    ChildHistory getPaymentDetail(int userId, int historyId);

    void chargePoints(int parentId, int amount); // 포인트 충전

    void chargePoint(ChargePointReqDto dto);

    void changeCard(RegisterCardReqDto dto, int childId);

    //  최근 일주일 동안 먹은 메뉴 리스트 조회
    List<String> getMenuListWeek(List<Integer> child_history_id_list);
}
