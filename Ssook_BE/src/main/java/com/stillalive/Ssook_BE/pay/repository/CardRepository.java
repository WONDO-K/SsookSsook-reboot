package com.stillalive.Ssook_BE.pay.repository;

import com.stillalive.Ssook_BE.domain.Card;
import com.stillalive.Ssook_BE.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    Optional<Card> findByCardToken(String cardToken);

    Optional<Card> findByChild(Child child);

    // 현재 유저에게 등록된 카드 여부 확인
    boolean existsByChild(Child child);

}
