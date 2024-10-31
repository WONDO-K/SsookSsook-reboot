package com.stillalive.Ssook_BE.user.repository;

import com.stillalive.Ssook_BE.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Integer> {

    Optional<Child> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByTel(String tel);
}
