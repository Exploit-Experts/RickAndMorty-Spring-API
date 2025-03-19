package com.rickmorty.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InfoDto(
        @JsonProperty("count")
        long count,
        @JsonProperty("pages")
        long pages,
        @JsonProperty("next")
        String next,
        @JsonProperty("prev")
        String prev
) {}
