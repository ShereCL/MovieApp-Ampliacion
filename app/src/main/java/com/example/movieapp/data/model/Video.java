package com.example.movieapp.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Video {

    @SerializedName("key")
    private String key;

    @SerializedName("site")
    private String site;

    @SerializedName("type")
    private String type;

    @SerializedName("name")
    private String name;

    //Getters


    public String getKey() {
        return key;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    //Método para verificar si un trailer es de YouTube

    public boolean isYouTubeTrailer() {
        return "YouTube".equalsIgnoreCase(site) && "Trailer".equalsIgnoreCase(type);
    }

    //Método para obtener la url de youtube

    public String getYouTubeUrl() {
        if(key != null && "YouTube".equalsIgnoreCase(site)) {
            return "https://www.youtube.com/watch?v=" + key;
        }
        return null;
    }

    //Clase interna para las respuestas de los videos
    public static class VideoResponse {
        @SerializedName("results")
        private List<Video> results;

        public List<Video> getResults() {
            return results;
        }
    }
}
