package com.stillalive.Ssook_BE.menu.repository;

import com.stillalive.Ssook_BE.domain.RecoMenu;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecoMenuRepository extends JpaRepository<RecoMenu, Integer> {
    @Query("SELECT r.name FROM RecoMenu r")
    List<String> findAllNames();
}
