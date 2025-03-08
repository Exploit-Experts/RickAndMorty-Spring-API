package com.rickmorty.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "episodes")
public class EpisodeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String airDate;

    @Column(nullable = false)
    private String episodeCode;

    @ManyToMany(mappedBy = "episodes")
    private List<CharacterModel> characters;

}
