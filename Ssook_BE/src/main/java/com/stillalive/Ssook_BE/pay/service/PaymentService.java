package com.stillalive.Ssook_BE.pay.service;

import com.stillalive.Ssook_BE.domain.ChildHistory;
import com.stillalive.Ssook_BE.pay.dto.*;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PaymentService {

    void processPayment(PaymentReqDto paymentReqDto);

    MyCardResDto getMyCard(int childId);

    void registerCard(RegisterCardReqDto dto, int childId);

    PointBalanceResDto getPointBalance(int userId, boolean isChild);

    List<ChildHistoryResDto> getPaymentList(int childId, Integer months);

    ChildHistory getPaymentDetail(int userId, int historyId);

    void chargePoints(int parentId, int amount); // 포인트 충전

    void chargePoint(ChargePointReqDto dto);

}
