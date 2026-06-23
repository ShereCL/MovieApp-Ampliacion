package com.example.movieapp.data.model;

import com.google.firebase.firestore.DocumentId;

public class SeguimientoFS {

    @DocumentId
    private String documentId;
    private String userId;
    private String tipo;
    private int tmdbId;
    private String titulo;
    private String posterPath;
    private String fechaVisualizacion;
    private float puntuacion;
    private String imgRecuerdo;
    private int genreId;
    private String genreName;

    //Constructores

    //Para Firestore
    public SeguimientoFS() {
    }

    public SeguimientoFS(String documentId, String userId, String tipo, int tmdbId, String titulo, String posterPath, String fechaVisualizacion, float puntuacion, String imgRecuerdo) {
        this.documentId = documentId;
        this.userId = userId;
        this.tipo = tipo;
        this.tmdbId = tmdbId;
        this.titulo = titulo;
        this.posterPath = posterPath;
        this.fechaVisualizacion = fechaVisualizacion;
        this.puntuacion = puntuacion;
        this.imgRecuerdo = imgRecuerdo;
    }

    //constructor con generos
    public SeguimientoFS(String documentId, String userId, String tipo, int tmdbId, String titulo, String posterPath, String fechaVisualizacion, float puntuacion, String imgRecuerdo, int genreId, String genreName) {
        this.documentId = documentId;
        this.userId = userId;
        this.tipo = tipo;
        this.tmdbId = tmdbId;
        this.titulo = titulo;
        this.posterPath = posterPath;
        this.fechaVisualizacion = fechaVisualizacion;
        this.puntuacion = puntuacion;
        this.imgRecuerdo = imgRecuerdo;
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public SeguimientoFS(String userId, String tipo, int tmdbId, String titulo, String posterPath, String fechaVisualizacion, float puntuacion, String imgRecuerdo) {
        this.userId = userId;
        this.tipo = tipo;
        this.tmdbId = tmdbId;
        this.titulo = titulo;
        this.posterPath = posterPath;
        this.fechaVisualizacion = fechaVisualizacion;
        this.puntuacion = puntuacion;
        this.imgRecuerdo = imgRecuerdo;
    }

    //Getters

    public String getDocumentId() {
        return documentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTipo() {
        return tipo;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getFechaVisualizacion() {
        return fechaVisualizacion;
    }

    public float getPuntuacion() {
        return puntuacion;
    }

    public String getImgRecuerdo() {
        return imgRecuerdo;
    }

    public int getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }

//Setters


    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setFechaVisualizacion(String fechaVisualizacion) {
        this.fechaVisualizacion = fechaVisualizacion;
    }

    public void setPuntuacion(float puntuacion) {
        this.puntuacion = puntuacion;
    }

    public void setImgRecuerdo(String imgRecuerdo) {
        this.imgRecuerdo = imgRecuerdo;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
