package com.stillalive.Ssook_BE.user.repository;

import com.stillalive.Ssook_BE.domain.FamilyRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyRelationRepository extends JpaRepository<FamilyRelation, Integer> {

    Optional<FamilyRelation> findByParentIdAndChildId(int parentId, int childId);

}
