package com.stillalive.Ssook_BE.school.repository;

import com.stillalive.Ssook_BE.domain.Diner;
import com.stillalive.Ssook_BE.domain.School;
import com.stillalive.Ssook_BE.domain.SchoolMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface SchoolMealRepository extends JpaRepository<SchoolMeal, Integer> {

    List<SchoolMeal> findBySchoolCodeAndDateBetween(Integer schoolCode, LocalDate startOfWeek, LocalDate endOfWeek);
}
