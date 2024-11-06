package com.stillalive.Ssook_BE.nut.repository;

import com.stillalive.Ssook_BE.domain.NutHistory;
import com.stillalive.Ssook_BE.enums.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface NutHistoryRepository extends JpaRepository<NutHistory, Integer> {
    Optional<NutHistory> findByChild_ChildIdAndEatDateAndMeal(Integer childId, LocalDate eatDate, Meal meal);
}
