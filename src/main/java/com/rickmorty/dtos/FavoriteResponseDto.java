package com.rickmorty.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rickmorty.enums.ItemType;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FavoriteResponseDto(
        @JsonProperty("id") Long id,
        @JsonProperty("apiId") Long apiId,
        @JsonProperty("itemType") ItemType itemType,
        @JsonProperty("userId") Long userId
) {
}
