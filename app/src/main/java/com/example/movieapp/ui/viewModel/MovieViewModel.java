package com.example.movieapp.ui.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.model.MovieResponse;
import com.example.movieapp.data.repository.MovieRepository;
import com.example.movieapp.data.util.Resource;

public class MovieViewModel extends AndroidViewModel {

    private final MovieRepository repository;

    public MutableLiveData<Integer> moviePage = new MutableLiveData<>(1);
    public MutableLiveData<Integer> tvPage = new MutableLiveData<>(1);

    public MovieViewModel(@NonNull Application application) {
        super(application);
        repository = new MovieRepository(application);
    }

    //peliculas
    public LiveData<Resource<MovieResponse>> getMovies() {
        return Transformations.switchMap(moviePage, repository::getPopularMovies);
    }

    //series
    public LiveData<Resource<MovieResponse>> getTvShows() {
        return Transformations.switchMap(tvPage, repository::getPopularTvShows);
    }

}
