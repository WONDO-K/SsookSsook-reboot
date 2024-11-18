package com.stillalive.Ssook_BE.domain.base;

import com.stillalive.Ssook_BE.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.Date;

@Getter
@SuperBuilder
@MappedSuperclass
public abstract class User extends BaseTimeEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false)
//    @Temporal(TemporalType.DATE)
//    private Date bday;
    private LocalDate bday; // LocalDate 타입으로 변경

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(name = "point", nullable = false)
    @ColumnDefault("0")
    private Integer point = 0;

    protected boolean isParent; // 부모 여부를 나타내는 필드

    // 기본 생성자
    protected User() {
        super();
    }

    @PrePersist
    protected void onPrePersist() {
        if (this.point == null) {
            this.point = 0;
        }
    }

    public void setPoint(int point) {
        this.point = point;
    }

}