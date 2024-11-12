package com.stillalive.Ssook_BE.diner.repository;

import com.stillalive.Ssook_BE.domain.Diner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DinerRepository extends JpaRepository<Diner, Integer> {
    Collection<Diner> findAllByIsAngelTrue();

    Integer countAllByIsAngelTrue();

    @Query(value = "SELECT *, " +
            "(6371 * ACOS(COS(RADIANS(:userLat)) * COS(RADIANS(lat)) * " +
            "COS(RADIANS(lng) - RADIANS(:userLng)) + SIN(RADIANS(:userLat)) * SIN(RADIANS(lat)))) AS distance " +
            "FROM diner " +
            "HAVING distance < :range " +
            "ORDER BY distance",
            nativeQuery = true)
    List<Diner> findNearbyDiners(@Param("userLat") double userLat,
                                 @Param("userLng") double userLng,
                                 @Param("range") double range);

    Page<Diner> findAll(Pageable pageable);
}
