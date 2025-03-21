package com.rickmorty.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationDto(
        Long id,
        @JsonProperty("name")
        String name,
        @JsonProperty("type")
        String type,
        @JsonProperty("dimension")
        String dimension,
        @JsonProperty("residents")
        List<String> residents,
        @JsonProperty("url")
        String url
) {

}
