package com.rickmorty.Repository;

import com.rickmorty.Models.FavoriteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository  extends JpaRepository<FavoriteModel, Long> {
    @Query("SELECT f FROM FavoriteModel f WHERE f.user.id = :userId AND f.id = :favoriteId")
    FavoriteModel findByUserFavoriteId(@Param("userId") Long userId, @Param("favoriteId") Long favoriteId);

    @Modifying
    @Query(value = "DELETE FROM user_favorites WHERE user_id = :userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Long userId);
}