package com.rickmorty.Repository;

import com.rickmorty.Models.LocationModel;
import jakarta.persistence.Query;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationModel, Long> {
    @Query("SELECT l FROM LocationModel l WHERE " +
            "(:name IS NULL OR l.name LIKE %:name%) AND " +
            "(:type IS NULL OR l.locationType LIKE %:type%) AND " +  // Mantido como locationType
            "(:dimension IS NULL OR l.dimension LIKE %:dimension%)")
    Page<LocationModel> findWithFilters(
            @Param("name") String name,
            @Param("type") String type,
            @Param("dimension") String dimension,
            SpringDataWebProperties.Pageable pageable);
}