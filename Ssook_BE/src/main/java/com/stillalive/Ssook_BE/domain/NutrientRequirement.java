package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "nutrient_req")
public class NutrientRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "req_id", nullable = false)
    private Integer id;

    private Integer age;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Float carb;
    private Float protein;
    private Float vitA;
    private Float vitC;

    private Float ribof;
    private Float thiam;
    private Float iron;
    private Float calcium;

}
