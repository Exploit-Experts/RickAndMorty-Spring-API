package com.rickmorty.Repository;

import com.rickmorty.Models.FavoriteCharacterModel;
import com.rickmorty.Models.FavoriteLocationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteLocationRepository extends JpaRepository<FavoriteLocationModel, Long> {
    @Query("SELECT f FROM FavoriteLocationModel f " +
            "JOIN f.user u " +
            "WHERE u.id = :userId AND u.active = 1")
    Page<FavoriteLocationModel> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
