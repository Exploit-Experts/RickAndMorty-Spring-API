package com.rickmorty.Repository;

import com.rickmorty.Models.EpisodeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodeRepository extends JpaRepository<EpisodeModel, Long> {

    // Busca episódios por nome (ignorando maiúsculas/minúsculas)
    Page<EpisodeModel> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Busca episódios por código do episódio (ignorando maiúsculas/minúsculas)
    Page<EpisodeModel> findByEpisodeCodeContainingIgnoreCase(String episodeCode, Pageable pageable);

    // Busca episódios por nome e código do episódio (ignorando maiúsculas/minúsculas)
    Page<EpisodeModel> findByNameContainingIgnoreCaseAndEpisodeCodeContainingIgnoreCase(
            String name, String episodeCode, Pageable pageable);

    // Busca episódios por ID de personagem (relacionamento ManyToMany)
    @Query("SELECT e FROM EpisodeModel e JOIN e.characters c WHERE c.id = :characterId")
    Page<EpisodeModel> findByCharacterId(@Param("characterId") Long characterId, Pageable pageable);
}

