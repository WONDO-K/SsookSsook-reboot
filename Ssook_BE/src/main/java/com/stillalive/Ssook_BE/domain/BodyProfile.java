package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.domain.base.BaseTimeEntity;
import com.stillalive.Ssook_BE.enums.Activity;
import com.stillalive.Ssook_BE.enums.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "body_profile")
public class BodyProfile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bf_id", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(name = "height", nullable = false)
    private Float height;

    @Column(name = "weight", nullable = false)
    private Float weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity", nullable = false)
    private Activity activity = Activity.MEDIUM;

    @Column(name = "calory_eer", nullable = false)
    private Integer caloryEer;

    public void calculateEER(Child child) {

        // 성별, 나이, 키, 몸무게, 활동 수준 정보 가져오기
        // 오늘 년도 - 생년월일 년도 = 나이
        int age = LocalDate.now().getYear() - child.getBday().getYear();
        Gender gender = child.getGender();
        float height = this.height;
        float weight = this.weight;
        Activity activity = this.activity;
        double alpha, beta, gamma, delta;
        double pa;

        // 성별에 따른 상수 값 설정 (아동 및 청소년만 고려)
        if (gender == Gender.MALE) {
            alpha = 88.5;
            beta = 61.9;
            gamma = 26.7;
            delta = 903.0;
        } else { // 여자
            alpha = 135.3;
            beta = 30.8;
            gamma = 10.0;
            delta = 934.0;
        }

        // 활동 수준에 따른 PA 값 설정 (아동 및 청소년만 고려)
        switch (activity) {
            case MEDIUM:
                pa = (gender.equals("남")) ? 1.13 : 1.16;
                break;
            case HIGH:
                pa = (gender.equals("남")) ? 1.26 : 1.31;
                break;
            case VERY_HIGH:
                pa = (gender.equals("남")) ? 1.42 : 1.56;
                break;
            default: // 기본적으로 비활동적 (Sedentary)
                pa = 1.00;
                break;
        }

        // EER 계산
        this.caloryEer = (int) Math.round(alpha + (beta * age) + (pa * ((gamma * weight) + (delta * height / 100.0))));
    }

//    생성자 함수
    public BodyProfile(Child child, float height, float weight, Activity activity) {
        this.child = child;
        this.height = height;
        this.weight = weight;
        this.activity = activity;
        calculateEER(child);
    }

    public void updateProfile(float height, float weight, Activity activity) {
        this.height = height;
        this.weight = weight;
        this.activity = activity;
        calculateEER(this.child);
    }
}
