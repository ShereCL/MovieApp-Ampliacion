package com.example.movieapp.data.model;

import com.google.firebase.Timestamp;

public class Comentario {

    private String id;
    private String uid;
    private String nombreUsuario;
    private String mensaje;
    private Timestamp fecha;
    private String fotoUrl;

    public Comentario() {};

    //Getters

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Timestamp getFecha() {
        return fecha;
    }
    public String getFotoUrl() { return fotoUrl; }

    //Setters

    public void setId(String id) {
        this.id = id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
