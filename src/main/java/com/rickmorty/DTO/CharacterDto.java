package com.rickmorty.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import com.rickmorty.DTO.CharacterDto;
import com.rickmorty.enums.Gender;
import com.rickmorty.enums.LifeStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CharacterDto(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("status") String status,
        @JsonProperty("species") String species,
        @JsonProperty("type") String type,
        @JsonProperty("gender") String gender,
        @JsonProperty("image") String image,
        @JsonProperty("episode") List<String> episode,
        @JsonProperty("location") LocationCharacterDto location
) {}
