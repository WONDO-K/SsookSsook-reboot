package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.enums.PayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "child_history")
@Builder
public class ChildHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_history_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayType historyType;

    @Column(name = "cardPrice", nullable = false)
    @ColumnDefault("0")
    private Integer cardPrice;

    @Column(name = "pointPrice", nullable = false)
    @ColumnDefault("0")
    private Integer pointPrice;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime historyTime;

    @OneToMany(mappedBy = "childHistory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PayDetail> payDetails = new ArrayList<>(); // 리스트 기본 초기화 추가

}
