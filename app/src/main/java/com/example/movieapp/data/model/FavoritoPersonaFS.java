package com.example.movieapp.data.model;

public class FavoritoPersonaFS {
    private int personaId;
    private String nombre;
    private String fotoUrl;
    private String departamento;
    private String userId;
    private long fechaGuardado;

    //Constructores
    public FavoritoPersonaFS() {}

    public FavoritoPersonaFS(int personaId, String nombre, String fotoUrl, String departamento, String userId) {
        this.personaId = personaId;
        this.nombre = nombre;
        this.fotoUrl = fotoUrl;
        this.departamento = departamento;
        this.userId = userId;
        this.fechaGuardado = System.currentTimeMillis();
    }

    //Getters

    public int getPersonaId() {
        return personaId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getDepartamento() {
        return departamento;
    }

    public String getUserId() {
        return userId;
    }

    public long getFechaGuardado() {
        return fechaGuardado;
    }

    //Setters

    public void setPersonaId(int personaId) {
        this.personaId = personaId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFechaGuardado(long fechaGuardado) {
        this.fechaGuardado = fechaGuardado;
    }
}
