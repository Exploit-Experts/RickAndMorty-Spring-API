package com.rickmorty.repositories;

import com.rickmorty.models.EpisodeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpisodeRepository extends JpaRepository<EpisodeModel, Long> {
}