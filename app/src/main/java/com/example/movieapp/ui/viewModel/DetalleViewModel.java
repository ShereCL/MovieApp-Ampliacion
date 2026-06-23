package com.example.movieapp.ui.viewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.movieapp.data.model.Movie;
import com.example.movieapp.data.model.Video;
import com.example.movieapp.data.repository.VideoRepository;
import com.example.movieapp.data.util.Resource;

public class DetalleViewModel extends AndroidViewModel {

    private final VideoRepository repository;
    private final SharedPreferences prefs;

    public DetalleViewModel(@NonNull Application application) {
        super(application);
        repository = new VideoRepository();
        prefs = application.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    // Obtener detalles completos de película y serie
    public LiveData<Resource<Movie>> getMovieDetails(int id, boolean isTvShow) {
        String language = prefs.getString("language", "es-ES");

        if (isTvShow) {
            return repository.getTvShowDetails(id, language);
        } else {
            return repository.getMovieDetails(id, language);
        }
    }

    // Obtener videos
    public LiveData<Resource<Video.VideoResponse>> getVideos(int id, boolean isShow) {
        String language = prefs.getString("language", "es-ES");

        if(isShow) {
            return repository.getTvVideos(id, language);
        } else {
            return repository.getMovieVideos(id, language);
        }
    }
}