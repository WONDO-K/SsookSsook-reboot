package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.domain.base.Nutrient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "menu_nut")
public class MenuNut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_nut_id", nullable = false)
    private Integer id;

    @Column(name = "menu_name", nullable = false, unique = true)
    private String menuName;

    @Embedded
    private Nutrient nutrient;
}
