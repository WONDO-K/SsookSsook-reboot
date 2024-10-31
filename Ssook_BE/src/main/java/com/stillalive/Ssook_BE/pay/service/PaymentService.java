package com.stillalive.Ssook_BE.pay.service;

import com.stillalive.Ssook_BE.pay.dto.MyCardResDto;
import com.stillalive.Ssook_BE.pay.dto.PaymentReqDto;
import com.stillalive.Ssook_BE.pay.dto.RegisterCardReqDto;
import org.springframework.security.core.Authentication;

public interface PaymentService {

    void processPayment(PaymentReqDto paymentReqDto);

    MyCardResDto getMyCard(int childId);

    void registerCard(RegisterCardReqDto dto, int childId);

    int getPointBalance(int childId);

}
