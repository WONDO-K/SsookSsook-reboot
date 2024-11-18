package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "nutrient_upper")
public class NutrientUpper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "upper_id", nullable = false)
    private Integer id;

    private Integer age;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Float vitA;
    private Float vitC;
    private Float iron;
    private Float calcium;

}
