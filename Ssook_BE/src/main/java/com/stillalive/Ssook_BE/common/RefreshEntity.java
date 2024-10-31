//package com.stillalive.Ssook_BE.common;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.ToString;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.redis.core.RedisHash;
//
//@RedisHash(value = "refreshtoken", timeToLive = 86400)
//@AllArgsConstructor
//@Getter
//@ToString
//@Builder
//public class RefreshEntity {
//    @Id
//    private String refresh;
//
//    // 기존 refresh 필드를 업데이트할 수 있는 메서드 추가
//    public void setRefresh(String refresh) {
//        this.refresh = refresh;
//    }
//}
