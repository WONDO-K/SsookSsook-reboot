package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.domain.base.Nutrient;
import com.stillalive.Ssook_BE.enums.Meal;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Entity(name = "school_meal")
@Getter
public class SchoolMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_meal_id", nullable = false)
    private Integer schoolMealId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code", nullable = false)
    private School school;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "meal", nullable = false)
    @Enumerated(EnumType.STRING)
    private Meal meal;

    @Column(name = "menu", nullable = false)
    private String menu;

    @Embedded
    private Nutrient nutrient;

}
