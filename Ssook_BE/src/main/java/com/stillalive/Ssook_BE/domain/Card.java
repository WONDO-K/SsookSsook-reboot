package com.stillalive.Ssook_BE.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "card")
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(name = "alias")
    private String alias;

    @Column(name = "card_token")
    private String cardToken;

    @Column(name = "exiration_date")
    @Temporal(TemporalType.DATE)
    private Date expirationDate;

    @Column(name = "is_active")
    @ColumnDefault("1")
    private boolean isActive;

}
