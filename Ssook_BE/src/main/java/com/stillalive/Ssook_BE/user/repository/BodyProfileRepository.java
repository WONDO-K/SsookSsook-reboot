package com.stillalive.Ssook_BE.user.repository;

import com.stillalive.Ssook_BE.domain.BodyProfile;
import com.stillalive.Ssook_BE.domain.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BodyProfileRepository  extends JpaRepository<BodyProfile, Integer> {
    Optional<BodyProfile> findByChild(Child child);
}
