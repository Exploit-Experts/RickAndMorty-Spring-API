package com.rickmorty.DTO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationCharacterDto(
        @JsonProperty("name") String name,
        @JsonProperty("url") String url

) {}
