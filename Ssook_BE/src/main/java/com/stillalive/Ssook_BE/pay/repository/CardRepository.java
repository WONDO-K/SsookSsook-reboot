package com.stillalive.Ssook_BE.pay.repository;

import com.stillalive.Ssook_BE.domain.Card;
import com.stillalive.Ssook_BE.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardToken(String cardToken);

    Optional<Card> findByChild(Child child);
}
