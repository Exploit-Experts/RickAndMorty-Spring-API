package com.rickmorty.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResponseDto(
        @JsonProperty("info")
        InfoDto info,
        @JsonProperty("results")
        List<LocationDto> results
) {}

