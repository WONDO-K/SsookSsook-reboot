package com.stillalive.Ssook_BE.domain;

import com.stillalive.Ssook_BE.domain.base.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "parent")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parent extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parent_id", nullable = false)
    private Integer parentId;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<FamilyRelation> familyRelations = new ArrayList<>();


}
