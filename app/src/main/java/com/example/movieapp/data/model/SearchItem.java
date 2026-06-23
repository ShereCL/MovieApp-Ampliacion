package com.example.movieapp.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;

public class SearchItem {

    //Modelo para traer los datos que quiero dede la búsqueda
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("name")
    private String name;
    @SerializedName("poster_path")
    private String posterPath;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title != null ? title :name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
