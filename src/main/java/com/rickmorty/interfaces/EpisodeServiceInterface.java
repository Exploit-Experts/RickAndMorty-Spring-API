package com.rickmorty.interfaces;

import com.rickmorty.dtos.ApiResponseDto;
import com.rickmorty.dtos.EpisodeDto;
import com.rickmorty.enums.SortEpisode;

public interface EpisodeServiceInterface {
  ApiResponseDto<EpisodeDto> findAllEpisodes(Integer page, String name, String episode, SortEpisode sort);

  EpisodeDto findEpisodeById(Long id);
}
