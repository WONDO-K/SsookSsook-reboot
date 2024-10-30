package com.stillalive.Ssook_BE.diner.repository;

import com.stillalive.Ssook_BE.domain.Diner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DinerRepository extends JpaRepository<Diner, Integer> {
}
