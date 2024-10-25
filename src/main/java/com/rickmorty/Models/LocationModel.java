package com.rickmorty.Models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LocationModel {

    private int id;

    private String name;

    private String type;

    private String dimension;

    private List<String> residents;

    private String url;

}
