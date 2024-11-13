package com.rickmorty.Models;

import com.rickmorty.enums.ItemType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "favorites")
public class FavoriteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String apiId;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @ManyToMany(mappedBy = "favorites")
    private Set<UserModel> users = new HashSet<>();
}