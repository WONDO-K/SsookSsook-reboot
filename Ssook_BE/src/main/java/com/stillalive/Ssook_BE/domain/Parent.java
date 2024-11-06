package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.domain.base.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "parent")
@SuperBuilder
@NoArgsConstructor
public class Parent extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_id", nullable = false)
    private Integer parentId;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<FamilyRelation> familyRelations = new ArrayList<>();

    // 부모 클래스에서 기본값 true 설정
    {
        isParent = true;
    }


}
