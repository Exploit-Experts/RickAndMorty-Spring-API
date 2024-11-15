
package com.rickmorty.Models;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.List;

public class EpisodeModel {

    private int id;
    private String name;
    private String episodeCode;
    private String releaseDate;
    private List<String> characters;
}