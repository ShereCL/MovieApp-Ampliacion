package com.example.movieapp.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Movie implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("title") //películas
    private String title;

    @SerializedName("name") //series de televisión
    private String name;

    @SerializedName("overview")
    private String overview;
    @SerializedName("poster_path")
    private String poster_path;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("first_air_date") //series
    private String firstAirDate;

    //Genero

    @SerializedName("genre_ids")
    private List<Integer> genres;

    @SerializedName("genres")
    private List<Genre> genreObjects;


    public List<Integer> getGenres() {
        return genres;
    }

    public void setGenres(List<Integer> genres) {
        this.genres = genres;
    }

    //Constructores


    public Movie() {
    }

    //Getters


    public int getId() {
        return id;
    }

    public String getTitle() {
        //cuando es pelicula se usa title y cuando es serie name
        return title != null ? title : name;
    }

    public String getOverview() {
        return overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate != null ? releaseDate : firstAirDate;
    }

    //Al final no lo uso//
    public boolean isTvShow() {
        return name != null;
    }

    //url completa del poster de la serie o peli
    public String getFullPosterPath() {
        if (poster_path != null) {
            return "https://image.tmdb.org/t/p/w500" + poster_path;
        }
        return null;
    }

    //obtener url completa del backdrop
    public String getFullBackdropPath() {
        if (backdropPath != null) {
            return "https://image.tmdb.org/t/p/w780" + backdropPath;
        }
        return null;
    }

    public List<Genre> getGenreObjects() {
        return genreObjects;
    }

    //SETTERS


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }
    public void setGenreObjects(List<Genre> genreObjects) {
        this.genreObjects = genreObjects;
    }

    //clase interna para los géneros
    public static class Genre implements Serializable {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        public int getId() { return id; }
        public String getName() { return name; }
    }
}
