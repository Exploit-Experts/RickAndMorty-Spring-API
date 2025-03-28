package com.rickmorty.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("LOCATION")
@Table(name = "favorite_locations")
public class FavoriteLocationModel extends FavoriteModel {
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationModel location;
}
