package com.stillalive.Ssook_BE.school.repository;

import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.ChildSchoolMeal;
import com.stillalive.Ssook_BE.domain.SchoolMeal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildSchoolMealRepository extends JpaRepository<ChildSchoolMeal, Integer> {
    boolean existsByChildAndSchoolMeal(Child child, SchoolMeal schoolMeal);
}