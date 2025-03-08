package com.rickmorty.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "episodes")
public class EpisodeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "local_date", nullable = false)
    private String localDate;

    @Column(name = "episode_code", nullable = false, length = 10)
    private String episodeCode;

    @ManyToMany(mappedBy = "episodes", fetch = FetchType.LAZY)
    private List<CharacterModel> characters;

}
