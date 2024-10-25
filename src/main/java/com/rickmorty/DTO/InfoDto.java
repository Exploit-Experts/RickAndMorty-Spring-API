package com.rickmorty.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InfoDto(
        @JsonProperty("count")
        int count,
        @JsonProperty("pages")
        int pages,
        @JsonProperty("next")
        String next,
        @JsonProperty("prev")
        String prev
) {}
