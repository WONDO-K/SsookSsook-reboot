package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.domain.base.BaseTimeEntity;
import com.stillalive.Ssook_BE.domain.base.Nutrient;
import com.stillalive.Ssook_BE.enums.Meal;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "nut_history")
public class NutHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nut_history_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(name = "eat_date", nullable = false)
    private LocalDateTime eatDate;

    @Column(name = "meal", nullable = false)
    private Meal meal;

    @Embedded
    private Nutrient nutrient;

}
