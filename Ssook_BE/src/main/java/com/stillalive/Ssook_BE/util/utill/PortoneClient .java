package com.stillalive.Ssook_BE.util.utill;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class PortoneClient {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${portone.api.secret}") // application.properties나 yml에 정의된 시크릿 키
    private String apiSecret;

    public void cancelPayment(String paymentId, String reason) {
        String url = "https://api.portone.io/payments/" + paymentId + "/cancel";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Portone " + apiSecret); // V2는 'Portone ' 접두어를 사용합니다.
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of("reason", reason);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            // 취소 요청 자체가 실패했을 때의 로그
            System.err.println("결제 취소 API 호출 실패: " + paymentId + ", 사유: " + e.getMessage());
        }
    }
}
