package com.rickmorty.Repository;

import com.rickmorty.Models.LocationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // IMPORT CORRETO
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationModel, Long> {

    @Query("SELECT l FROM LocationModel l WHERE " +
            "(COALESCE(:name, '') = '' OR l.name LIKE %:name%) AND " +
            "(COALESCE(:type, '') = '' OR l.type LIKE %:type%) AND " + // ou locationType conforme sua entidade
            "(COALESCE(:dimension, '') = '' OR l.dimension LIKE %:dimension%)")
    Page<LocationModel> findWithFilters(
            @Param("name") String name,
            @Param("type") String type,
            @Param("dimension") String dimension,
            Pageable pageable);
}