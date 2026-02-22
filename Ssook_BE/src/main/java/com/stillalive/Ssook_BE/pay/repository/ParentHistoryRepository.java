package com.stillalive.Ssook_BE.pay.repository;

import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.ChildHistory;
import com.stillalive.Ssook_BE.domain.Parent;
import com.stillalive.Ssook_BE.domain.ParentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ParentHistoryRepository extends JpaRepository<ParentHistory, Integer> {

    List<ParentHistory> findAllByParent(Parent parent);

    List<ParentHistory> findAllByParentAndCreatedAtAfter(Parent parent, LocalDateTime localDateTime);

    // 특정 결제 번호(impUid)가 이미 DB에 존재하는지 확인
    boolean existsByImpUid(String impUid);
}
