package com.rickmorty.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rickmorty.enums.FavoriteTypes;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FavoriteDto(
    @JsonProperty("favorite_id")
    Long favoriteId,

    @JsonProperty("favorite_type")
    FavoriteTypes favoriteType
) { }
