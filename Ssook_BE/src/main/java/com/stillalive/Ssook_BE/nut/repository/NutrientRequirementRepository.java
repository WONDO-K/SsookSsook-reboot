package com.stillalive.Ssook_BE.nut.repository;

import com.stillalive.Ssook_BE.domain.NutrientRequirement;
import com.stillalive.Ssook_BE.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NutrientRequirementRepository extends JpaRepository<NutrientRequirement, Integer> {

//  성별과 나이에 따른 요구량 조회
    Optional<NutrientRequirement> findByGenderAndAge(Gender gender, int age);
}
