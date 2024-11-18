package com.stillalive.Ssook_BE.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "balance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Balance {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Card card;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int currentBalance;

    private Timestamp lastUpdated;

    @ColumnDefault("0")
    private int dailySpentAmount;  // 일일 지출 금액

    private Date lastSpentDate;  // 마지막 지출 날짜

    private int monthlySupportAmount = 24000;  // 매월 갱신되는 지원금 (24000원)

    private int lastUpdatedMonth;  // 마지막으로 잔액이 갱신된 달

}
