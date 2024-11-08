package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.enums.PayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "parent_history")
public class ParentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_history_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayType type;

    @Column(name = "price", nullable = false)
    @ColumnDefault("0")
    private Integer price; // 결제, 전송 금액

    @Column(name = "balance", nullable = false)
    @ColumnDefault("0")
    private Integer balance; // 잔액

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

}
