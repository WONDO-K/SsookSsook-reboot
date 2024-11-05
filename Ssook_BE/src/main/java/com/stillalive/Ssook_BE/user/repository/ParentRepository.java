package com.stillalive.Ssook_BE.user.repository;

import com.stillalive.Ssook_BE.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Integer> {
    Optional<Parent> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByTel(String tel);

    boolean existsByParentId(int userId);
}
