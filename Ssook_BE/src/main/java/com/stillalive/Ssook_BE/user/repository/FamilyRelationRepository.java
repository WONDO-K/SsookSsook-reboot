package com.stillalive.Ssook_BE.user.repository;

import com.stillalive.Ssook_BE.domain.FamilyRelation;
import com.stillalive.Ssook_BE.domain.Parent;
import com.stillalive.Ssook_BE.enums.Progress;
import com.stillalive.Ssook_BE.user.dto.FamilyReqResDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FamilyRelationRepository extends JpaRepository<FamilyRelation, Integer> {
    
    List<FamilyRelation> findByChild_ChildIdAndStatus(Integer childId, Progress progress);

    Optional<FamilyRelation> findByParentIdAndChildId(int parentId, int childId);

}
