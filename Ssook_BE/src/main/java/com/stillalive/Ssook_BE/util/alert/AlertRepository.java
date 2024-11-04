package com.stillalive.Ssook_BE.util.alert;


import com.stillalive.Ssook_BE.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Integer> {
    List<Alert> findAllByUserIdOrderByTimestampDesc(int userId);

    long countByUserIdAndIsReadFalse(int userId);

    List<Alert> findAllByUserIdAndIsReadFalse(int userId);
}
