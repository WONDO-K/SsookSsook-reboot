package com.stillalive.Ssook_BE.user.repository;

import com.stillalive.Ssook_BE.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository extends JpaRepository<Child, Integer> {
}
