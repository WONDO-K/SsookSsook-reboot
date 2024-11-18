package com.stillalive.Ssook_BE.user.repository;

import com.stillalive.Ssook_BE.domain.Child;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Integer> {

    @EntityGraph(attributePaths = {"bodyProfile"})
    Optional<Child> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByTel(String tel);

    Optional<Child> findByTel(String tel);

    boolean existsByChildId(int userId);

    Optional<Child> findByChildId(Integer childId);
}
