package com.rickmorty.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResponseDto<T>(
        @JsonProperty("info")
        InfoDto info,
        @JsonProperty("results")
        List<T> results
) {}

