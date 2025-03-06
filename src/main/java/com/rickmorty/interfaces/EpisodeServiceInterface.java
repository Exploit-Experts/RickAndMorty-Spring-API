package com.rickmorty.interfaces;

import com.rickmorty.DTO.ApiResponseDto;
import com.rickmorty.DTO.EpisodeDto;
import com.rickmorty.enums.SortEpisode;

public interface EpisodeServiceInterface {
  ApiResponseDto<EpisodeDto> findAllEpisodes(Integer page, String name, String episode, SortEpisode sort);

  EpisodeDto findEpisodeById(Long id);
}
