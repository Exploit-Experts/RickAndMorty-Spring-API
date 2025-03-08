package com.rickmorty.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "characters")
public class CharacterModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String species;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String image;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationModel locationModel;

    @ManyToMany
    @JoinTable(
            name = "character_episodes",
            joinColumns = @JoinColumn(name = "character_id"),
            inverseJoinColumns = @JoinColumn(name = "episode_id")
    )
    private List<EpisodeModel> episodes;

}