package com.rickmorty.Repository;

import com.rickmorty.Models.FavoriteEpisodeModel;
import com.rickmorty.Models.FavoriteLocationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteEpisodeRepository extends JpaRepository<FavoriteEpisodeModel, Long> {
    @Query("SELECT f FROM FavoriteEpisodeModel f " +
            "JOIN f.user u " +
            "WHERE u.id = :userId AND u.active = 1")
    Page<FavoriteEpisodeModel> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
