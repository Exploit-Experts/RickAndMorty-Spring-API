package com.rickmorty.models;

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
    private Long apiId;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @ManyToMany(mappedBy = "favorites", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<UserModel> users = new HashSet<>();

}