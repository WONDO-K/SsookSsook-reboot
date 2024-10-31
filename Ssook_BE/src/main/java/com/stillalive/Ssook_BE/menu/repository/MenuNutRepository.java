package com.stillalive.Ssook_BE.menu.repository;

import com.stillalive.Ssook_BE.domain.MenuNut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuNutRepository extends JpaRepository<MenuNut, Long> {
    Optional<MenuNut> findByMenuName(String menuName);
}
