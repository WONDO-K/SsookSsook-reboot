package com.stillalive.Ssook_BE.school.repository;

import com.stillalive.Ssook_BE.domain.Diner;
import com.stillalive.Ssook_BE.domain.School;
import com.stillalive.Ssook_BE.domain.SchoolMeal;
import com.stillalive.Ssook_BE.enums.Meal;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolMealRepository extends JpaRepository<SchoolMeal, Integer> {

    List<SchoolMeal> findBySchoolCodeAndDateBetween(Integer schoolCode, LocalDate startOfWeek, LocalDate endOfWeek);

    @Query("SELECT s.code as code, s.name as name FROM School s WHERE s.name =:name")
    Optional<Tuple> findSchoolByExactMatch(@Param("name") String name);

    @Query("SELECT s.code as code, s.name as name FROM School s WHERE s.name LIKE CONCAT(:name, '%')")
    List<Tuple> findSchoolByStartingWith(@Param("name") String name);

    @Query("SELECT s.code as code, s.name as name FROM School s WHERE s.name LIKE CONCAT('%', :name, '%')")
    List<Tuple> findSchoolByContaining(@Param("name") String name);

    Optional<SchoolMeal> findSchoolMealIdBySchoolCodeAndDateAndMeal(Integer code, Date date, Meal meal);
}
