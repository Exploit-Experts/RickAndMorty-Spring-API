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
@Table(name = "locations")
public class LocationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 15)
    private String dimension;

    @Column(name = "location_type", nullable = false, length = 20)
    private String LocationType;

    @OneToMany(mappedBy = "locationModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CharacterModel> characters;

}