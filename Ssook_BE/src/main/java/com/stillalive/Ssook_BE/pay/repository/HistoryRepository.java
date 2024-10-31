package com.stillalive.Ssook_BE.pay.repository;

import com.stillalive.Ssook_BE.domain.ChildHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<ChildHistory, Long> {
}
