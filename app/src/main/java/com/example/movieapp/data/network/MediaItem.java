package com.example.movieapp.data.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MediaItem {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;

    @SerializedName("name")
    private String name;
    @SerializedName("poster_path")
    private String posterPath;

    //Peliculas
    @SerializedName("release_date")
    private String releaseDate;
    //Series
    @SerializedName("first_air_date")
    private String firstAirDate;
    @SerializedName("media_type")
    private String mediaType;

    @SerializedName("genre_ids")
    private List<Integer> genreIds;
    public List<Integer> getGenreIds() { return genreIds; }

    //Constructor vacío

    public MediaItem() {
    }

    //Getters

    public int getId() {
        return id;
    }

    public String getTitle() {
        if(title != null && !title.isEmpty()) {
            return title; //pelicula
        } else if(name != null && !name.isEmpty()) {
            return name; //serie
        }
        return "Sin título";
    }

    public String getName() {
        return name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public String getMediaType() {
        return mediaType;
    }


    //Setters


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    /// ///////// METODOS AUXILIARES ////////////////

    //metodo para otener el título
    //metodo para obtener el título
    public String getDisplayTitle() {
        if(title != null && !title.isEmpty()) {
            return title; //pelicula
        } else if(name != null && !name.isEmpty()) {
            return name; //serie
        }
        return "Sin título";
    }

    //método para obtener el año
    public String getYear() {
        String date = releaseDate != null ? releaseDate : firstAirDate;
        if(date != null && date.length() >= 4) {
            return date.substring(0,4); /*esto lo que hace es que extrae solo lo que es el año
             como cuento con la fecha en formato yyyy-MM-aa con substring cojo los 4 primeros dígitos*/
        }
        return "";
    }

    //To String para ,mostrar visible en el spinner y que muestre el nombre y el año

    @Override
    public String toString() {
        String displayTitle = getDisplayTitle();
        String year = getYear();
        if(!year.isEmpty()) {
            return displayTitle + " (" + year + ")";
        }
        return displayTitle;
    }
}
