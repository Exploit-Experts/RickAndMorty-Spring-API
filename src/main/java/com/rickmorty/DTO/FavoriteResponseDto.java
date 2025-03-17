package com.rickmorty.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rickmorty.enums.FavoriteTypes;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FavoriteResponseDto(
        @JsonProperty("id") Long id,
        @JsonProperty("apiId") Long apiId,
        @JsonProperty("itemType") FavoriteTypes favoriteTypes,
        @JsonProperty("userId") Long userId
) {
}
