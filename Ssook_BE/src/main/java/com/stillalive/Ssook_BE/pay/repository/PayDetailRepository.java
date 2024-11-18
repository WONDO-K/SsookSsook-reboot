package com.stillalive.Ssook_BE.pay.repository;

import com.stillalive.Ssook_BE.domain.PayDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PayDetailRepository extends JpaRepository<PayDetail, Integer> {

//    child_history_id_list 로 모든 메뉴이름 리스트 들고오기
    List<PayDetail> findByChildHistoryIdIn(List<Integer> child_history_id_list);

}
