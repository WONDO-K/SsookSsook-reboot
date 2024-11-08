package com.stillalive.Ssook_BE.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "diner")
public class Diner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diner_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "lat", nullable = false)
    private Float lat;

    @Column(name = "lng", nullable = false)
    private Float lng;

    @Column(name = "tel")
    private String tel;

    @Column(name = "is_angel", nullable = false)
    @ColumnDefault("false")
    private Boolean isAngel;

    @OneToMany(mappedBy = "diner", fetch = FetchType.LAZY)
    private List<Menu> menus = new ArrayList<>();
}
