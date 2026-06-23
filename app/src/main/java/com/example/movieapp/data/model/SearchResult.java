package com.example.movieapp.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {

    //Modelo que recibe datos de la búsqueda

    @SerializedName("result")
    private List<SearchItem> result;

    public List<SearchItem> getResult() {
        return result;
    }
}
