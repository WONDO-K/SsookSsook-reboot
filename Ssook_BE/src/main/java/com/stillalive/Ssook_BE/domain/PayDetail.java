package com.stillalive.Ssook_BE.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "pay_detail")
public class PayDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_detail_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "child_history_id", nullable = false)
    ChildHistory childHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "menu_id", nullable = false)
    Menu menu;

    @Column(name = "quantity", nullable = false)
    @ColumnDefault("1")
    private Integer quantity;

    // 필요한 3개의 필드만 받는 생성자 추가
    public PayDetail(ChildHistory childHistory, Menu menu, Integer quantity) {
        this.childHistory = childHistory;
        this.menu = menu;
        this.quantity = quantity;
    }

}


