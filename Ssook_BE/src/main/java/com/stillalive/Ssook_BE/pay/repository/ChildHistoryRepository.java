package com.stillalive.Ssook_BE.pay.repository;

import com.stillalive.Ssook_BE.domain.ChildHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChildHistoryRepository extends JpaRepository<ChildHistory, Integer> {

    @Query("SELECT h FROM child_history h WHERE h.card.child.childId = :childId AND h.historyTime BETWEEN :startDate AND :endDate ORDER BY h.historyTime DESC")
    List<ChildHistory> findByChildIdAndDateRange(@Param("childId") int childId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

}
