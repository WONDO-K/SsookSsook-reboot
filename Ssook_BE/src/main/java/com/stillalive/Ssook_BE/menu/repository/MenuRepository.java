package com.stillalive.Ssook_BE.menu.repository;

import com.stillalive.Ssook_BE.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
    Optional<Menu> findByIdAndDinerId(int menuId, int dinerId);
}
