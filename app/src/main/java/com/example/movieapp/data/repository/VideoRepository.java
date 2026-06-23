package com.example.movieapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.model.Movie;
import com.example.movieapp.data.model.Video;
import com.example.movieapp.data.network.ApiService;
import com.example.movieapp.data.network.RetrofitClient;
import com.example.movieapp.data.util.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoRepository {

    private static final String TAG = "VideoRepository";
    private final ApiService apiService;

    public VideoRepository() {
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
    }

    //Detalles de película
    public LiveData<Resource<Movie>> getMovieDetails(int movieId, String language) {
        MutableLiveData<Resource<Movie>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getMovieDetails(movieId, language).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    String errorMsg = "Error " + response.code() + ": " + response.message();
                    result.setValue(Resource.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                result.setValue(Resource.error("Error de red: " + t.getMessage()));
            }
        });

        return result;
    }

    //Detalles de serie
    public LiveData<Resource<Movie>> getTvShowDetails(int tvId, String language) {
        MutableLiveData<Resource<Movie>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getTvShowDetails(tvId, language).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    String errorMsg = "Error " + response.code() + ": " + response.message();
                    result.setValue(Resource.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                result.setValue(Resource.error("Error de red: " + t.getMessage()));
            }
        });

        return result;
    }

    //Videos de película
    public LiveData<Resource<Video.VideoResponse>> getMovieVideos(int movieId, String language) {
        MutableLiveData<Resource<Video.VideoResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getMovieVideos(movieId, language).enqueue(new Callback<Video.VideoResponse>() {
            @Override
            public void onResponse(Call<Video.VideoResponse> call, Response<Video.VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Video.VideoResponse videoResponse = response.body();
                    result.setValue(Resource.success(videoResponse));
                } else {
                    String errorMsg = "Error " + response.code() + ": " + response.message();
                    result.setValue(Resource.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<Video.VideoResponse> call, Throwable t) {
                result.setValue(Resource.error("Error de red: " + t.getMessage()));
            }
        });

        return result;
    }

    //Videos de serie
    public LiveData<Resource<Video.VideoResponse>> getTvVideos(int tvId, String language) {
        MutableLiveData<Resource<Video.VideoResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getTvVideos(tvId, language).enqueue(new Callback<Video.VideoResponse>() {
            @Override
            public void onResponse(Call<Video.VideoResponse> call, Response<Video.VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Video.VideoResponse videoResponse = response.body();
                    result.setValue(Resource.success(videoResponse));
                } else {
                    String errorMsg = "Error " + response.code() + ": " + response.message();
                    result.setValue(Resource.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<Video.VideoResponse> call, Throwable t) {
                result.setValue(Resource.error("Error de red: " + t.getMessage()));
            }
        });

        return result;
    }
}