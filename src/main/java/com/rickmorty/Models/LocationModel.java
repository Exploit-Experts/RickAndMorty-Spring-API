package com.rickmorty.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "locations")
public class LocationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String dimension;

    @Column(nullable = false)
    private String type;

    @OneToMany(mappedBy = "locationModel")
    private List<CharacterModel> characters;

}