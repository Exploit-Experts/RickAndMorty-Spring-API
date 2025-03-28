package com.rickmorty.exceptions;

public class FavoriteNotFound extends RuntimeException {
    public FavoriteNotFound() {
        super("Favorite not found");
    }
}
