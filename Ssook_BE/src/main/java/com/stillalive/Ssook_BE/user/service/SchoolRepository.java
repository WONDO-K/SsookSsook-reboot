package com.stillalive.Ssook_BE.user.service;

import com.stillalive.Ssook_BE.domain.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {
}
