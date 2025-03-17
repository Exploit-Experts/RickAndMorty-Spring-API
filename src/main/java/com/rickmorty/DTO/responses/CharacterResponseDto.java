package com.rickmorty.DTO.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rickmorty.Models.LocationModel;
import com.rickmorty.enums.Gender;
import com.rickmorty.enums.LifeStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CharacterResponseDto(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("status") LifeStatus status,
        @JsonProperty("species") String species,
        @JsonProperty("character_type") String characterType,
        @JsonProperty("gender") Gender gender,
        @JsonProperty("location") LocationModel location
) { }
