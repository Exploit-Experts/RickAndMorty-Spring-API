package com.rickmorty.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EpisodeDto(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("episode") String episodeCode,
        @JsonAlias("air_date") String releaseDate,
        @JsonProperty("characters") List<String>characters 
) {}
