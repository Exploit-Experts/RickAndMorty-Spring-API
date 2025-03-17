package com.rickmorty.DTO.responses;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rickmorty.Models.CharacterModel;

import java.time.LocalDate;
import java.util.List;

public record EpisodeResponseDto(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("episode") String episodeCode,
        @JsonAlias("air_date") LocalDate airDate,
        @JsonProperty("characters") List<CharacterModel> characters
) { }
