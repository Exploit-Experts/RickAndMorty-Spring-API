
package com.rickmorty.Repository;


import com.rickmorty.Models.FavoriteModel;
import com.rickmorty.enums.ItemType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository  extends JpaRepository<FavoriteModel, Long> {

    @Query("SELECT f FROM FavoriteModel f WHERE f.apiId = :apiId AND f.itemType = :itemType")
    Optional<FavoriteModel> findByApiIdAndItemType(@Param("apiId") Long apiId, @Param("itemType") ItemType itemType);

    @Query("SELECT f FROM FavoriteModel f " + " JOIN f.users u " + " WHERE u.id = :userId AND u.active = 1 ")
    Optional<List<FavoriteModel>> findFavoriteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "INSERT INTO user_favorites (user_id, favorite_id) VALUES (:userId, :favoriteId)", nativeQuery = true)
    void addFavoriteToUser(@Param("userId") Long userId, @Param("favoriteId") Long favoriteId);

    @Query(value = "SELECT COUNT(*) FROM user_favorites uf WHERE uf.user_id = :userId AND uf.favorite_id = :favoriteId", nativeQuery = true)
    Long existsByUserIdAndFavoriteId(@Param("userId") Long userId, @Param("favoriteId") Long favoriteId);

    @Modifying
    @Query(value = "DELETE FROM user_favorites WHERE user_id = :userId AND favorite_id = :favoriteId", nativeQuery = true)
    void deleteByUserIdAndFavoriteId(@Param("userId") Long userId, @Param("favoriteId") Long favoriteId);

    @Modifying
    @Query(value = "DELETE FROM user_favorites WHERE user_id = :userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Long userId);
}