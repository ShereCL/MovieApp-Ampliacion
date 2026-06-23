package com.example.movieapp.data.model;

import java.util.List;

public class PendienteFS {
    private int id;
    private String userId;
    private String tipo;
    private int idTMDB;
    private String titulo;
    private List<Integer> generos;
    private String descripcion;
    private String posterPath;
    private String backdropPath;
    private String releaseDate;
    private double voteAverage;

    public PendienteFS(int id, String tipo, int idTMDB, String titulo, List<Integer> generos,
                       String descripcion, String posterPath, String backdropPath,
                       String releaseDate, double voteAverage) {
        this.id = id;
        this.tipo = tipo;
        this.idTMDB = idTMDB;
        this.titulo = titulo;
        this.generos = generos;
        this.descripcion = descripcion;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    //Constructor vacío para firestore
    public PendienteFS() {
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public int getIdTMDB() {
        return idTMDB;
    }

    public String getTitulo() {
        return titulo;
    }

    public List<Integer> getGeneros() {
        return generos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getUserId() {
        return userId;
    }

    // SETTERS
    public void setId(int id) {
        this.id = id;
    }

    public void setIdTMDB(int idTMDB) {
        this.idTMDB = idTMDB;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setGeneros(List<Integer> generos) {
        this.generos = generos;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
