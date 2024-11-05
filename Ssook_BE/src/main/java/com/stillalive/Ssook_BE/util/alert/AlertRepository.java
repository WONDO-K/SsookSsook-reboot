package com.stillalive.Ssook_BE.util.alert;


import com.stillalive.Ssook_BE.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Integer> {

    // 부모 ID로 알림 내역을 시간순으로 조회
    List<Alert> findAllByReceiverIdOrderByTimestampDesc(int receiverId);

    // 자식 ID로 알림 내역을 시간순으로 조회
    List<Alert> findAllBySenderIdOrderByTimestampDesc(int senderId);

    // 부모 ID로 읽지 않은 알림 수 조회
    long countByReceiverIdAndIsReadFalse(int receiverId);

    // 자식 ID로 읽지 않은 알림 수 조회
    long countBySenderIdAndIsReadFalse(int senderId);

    // 부모 ID로 읽지 않은 알림 조회
    List<Alert> findAllByReceiverIdAndIsReadFalse(int receiverId);

    // 자식 ID로 읽지 않은 알림 조회
    List<Alert> findAllBySenderIdAndIsReadFalse(int senderId);
}
