package com.stillalive.Ssook_BE.pay.dto;

import java.util.List;

public interface PaymentRequest {
    int getDinerId();
    int getAmount();
    List<PayDetailDto> getPayDetails();
}
