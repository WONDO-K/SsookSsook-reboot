package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.domain.base.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "child")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Child extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id", nullable = false)
    private Integer childId;

    @OneToMany(mappedBy = "child", fetch = FetchType.LAZY)
    private List<FamilyRelation> familyRelations = new ArrayList<>();

    @OneToOne(mappedBy = "child", fetch = FetchType.LAZY)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private School school;

}