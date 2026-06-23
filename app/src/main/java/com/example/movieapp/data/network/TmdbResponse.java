package com.example.movieapp.data.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TmdbResponse {

    //Esta clase sirve para parsear el JSON que me devuelve TMDB

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<MediaItem> results; /* Esta lista es la que va a mostrar todas
                                        las pelis y series encontradas*/

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    // Constructor vacío
    public TmdbResponse() {
    }

    //GETTERS
    public int getPage() {
        return page;
    }

    public List<MediaItem> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    //SETTERS
    public void setPage(int page) {
        this.page = page;
    }

    public void setResults(List<MediaItem> results) {
        this.results = results;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
