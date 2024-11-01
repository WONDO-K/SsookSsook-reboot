package com.stillalive.Ssook_BE.pay.repository;

import com.stillalive.Ssook_BE.domain.Balance;
import com.stillalive.Ssook_BE.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByCard(Card card);
}
