//package com.stillalive.Ssook_BE.common;
//
//import com.stillalive.Ssook_BE.exception.ErrorCode;
//import com.stillalive.Ssook_BE.exception.SsookException;
//import com.stillalive.Ssook_BE.util.JWTUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class RefreshService {
//
//    private final RefreshRepository refreshRepository;
//    private final JWTUtil jwtUtil;
//
//    @Transactional
//    public void saveRefreshToken(String refresh) {
//        RefreshEntity refreshEntity = RefreshEntity.builder()
//                .refresh(refresh)
//                .build();
//
//        refreshRepository.save(refreshEntity);
//    }
//
//    @Transactional
//    public void deleteRefreshTokenByRefresh(String refresh) {
//        refreshRepository.deleteById(refresh);
//    }
//
//    public boolean existsByRefresh(String refresh) {
//        return refreshRepository.existsById(refresh);
//    }
//
//    public Boolean confirmRefreshToken(String refresh) {
//        return jwtUtil.getCategory(refresh).equals("refresh");
//    }
//
//    public Boolean isRefreshTokenExpired(String refresh) {
//        return jwtUtil.isExpired(refresh);
//    }
//
////    // 새롭게 추가된 updateRefreshToken 메서드
////    @Transactional
////    public void updateRefreshToken(String oldRefreshToken, String newRefreshToken) {
////        // 기존의 refreshToken이 DB에 존재하는지 확인
////        RefreshEntity existingRefreshToken = refreshRepository.findById(oldRefreshToken)
////                .orElseThrow(() -> new SsookException(ErrorCode.INVALID_REFRESH_TOKEN));
////
////        // 기존의 RefreshToken을 새로운 값으로 업데이트
////        existingRefreshToken.setRefresh(newRefreshToken);
////
////        // 업데이트된 엔티티를 저장
////        refreshRepository.save(existingRefreshToken);
////    }
//}
