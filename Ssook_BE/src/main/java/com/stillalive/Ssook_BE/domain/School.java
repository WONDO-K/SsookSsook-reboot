package com.stillalive.Ssook_BE.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "school")
public class School {

    @Id
    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY)
    private List<Child> children = new ArrayList<>();
}
