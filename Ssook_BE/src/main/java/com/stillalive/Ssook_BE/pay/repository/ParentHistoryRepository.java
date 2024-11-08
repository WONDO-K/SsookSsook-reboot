package com.stillalive.Ssook_BE.pay.repository;

import com.stillalive.Ssook_BE.domain.ChildHistory;
import com.stillalive.Ssook_BE.domain.Parent;
import com.stillalive.Ssook_BE.domain.ParentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ParentHistoryRepository extends JpaRepository<ParentHistory, Integer> {

    List<ParentHistory> findAllByParent(Parent parent);

    List<ParentHistory> findAllByParentAndCreatedAtAfter(Parent parent, LocalDateTime localDateTime);
}
