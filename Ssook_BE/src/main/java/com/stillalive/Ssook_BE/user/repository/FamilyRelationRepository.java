package com.stillalive.Ssook_BE.user.repository;

import com.stillalive.Ssook_BE.domain.FamilyRelation;
import com.stillalive.Ssook_BE.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyRelationRepository extends JpaRepository<FamilyRelation, Integer> {
}
