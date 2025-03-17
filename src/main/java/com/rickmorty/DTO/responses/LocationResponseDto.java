package com.rickmorty.DTO.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rickmorty.Models.CharacterModel;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationResponseDto(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("type") String locationType,
        @JsonProperty("dimension") String dimension,
        @JsonProperty("residents") List<CharacterModel> residents
) { }
