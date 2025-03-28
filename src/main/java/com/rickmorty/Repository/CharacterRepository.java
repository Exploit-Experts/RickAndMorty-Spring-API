package com.rickmorty.Repository;

import com.rickmorty.Models.CharacterModel;
import com.rickmorty.enums.Gender;
import com.rickmorty.enums.LifeStatus;
import com.rickmorty.enums.Species;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepository extends JpaRepository<CharacterModel, Long> {

    @Query("SELECT c FROM CharacterModel c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:species IS NULL OR LOWER(c.species) LIKE LOWER(CONCAT('%', :species, '%'))) AND " +
            "(:characterType IS NULL OR LOWER(c.characterType) LIKE LOWER(CONCAT('%', :characterType, '%'))) AND " +
            "(:gender IS NULL OR c.gender = :gender)")
    Page<CharacterModel> findAllWithFilters(
            @Param("name") String name,
            @Param("status") LifeStatus status,
            @Param("species") String species,
            @Param("characterType") String characterType,
            @Param("gender") Gender gender,
            Pageable pageable
    );
}
