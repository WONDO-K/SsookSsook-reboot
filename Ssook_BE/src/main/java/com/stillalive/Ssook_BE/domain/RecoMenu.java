package com.stillalive.Ssook_BE.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "reco_menu")
public class RecoMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;
}
