package com.rickmorty.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import com.rickmorty.DTO.CharacterDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CharacterDto(
        @JsonProperty("id") int id,
        @JsonProperty("name") String name,
        @JsonProperty("status") String status,
        @JsonProperty("species") String species,
        @JsonProperty("type") String type,
        @JsonProperty("gender") String gender,
        @JsonProperty("image") String image,
        @JsonProperty("episode") List<String> episode,
        @JsonProperty("location") LocationCharacterDto location

) {}
