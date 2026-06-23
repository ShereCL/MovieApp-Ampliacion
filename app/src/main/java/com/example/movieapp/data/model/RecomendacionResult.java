package com.example.movieapp.data.model;

import java.util.List;

public class RecomendacionResult {
    public final List<Movie> peliculas;
    public final String tipo;

    public RecomendacionResult(List<Movie> peliculas, String tipo) {
        this.peliculas = peliculas;
        this.tipo = tipo;
    }
}