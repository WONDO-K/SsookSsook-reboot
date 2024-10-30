package com.stillalive.Ssook_BE.domain.base;

import com.stillalive.Ssook_BE.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

@Getter
@MappedSuperclass
public abstract class User extends BaseTimeEntity {

    private String name;

    private String tel;

    @Temporal(TemporalType.DATE)
    private Date bday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String loginId;

    private String password;

    @Column(name = "point", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer point;

}