package com.example.movieapp.data.network;

import com.example.movieapp.data.model.Person;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonResponse {
    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<Person> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    //Constructores
    public PersonResponse() {
    }

    //Getters

    public int getPage() {
        return page;
    }

    public List<Person> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    //Setters

    public void setPage(int page) {
        this.page = page;
    }

    public void setResults(List<Person> results) {
        this.results = results;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
