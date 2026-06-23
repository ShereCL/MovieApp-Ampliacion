package com.example.movieapp.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.R;
import com.example.movieapp.data.model.MovieResponse;
import com.example.movieapp.data.network.ApiService;
import com.example.movieapp.data.network.RetrofitClient;
import com.example.movieapp.data.util.Resource;
import com.google.android.material.shadow.ShadowRenderer;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {

    private static final String TAG = "MovieRepository";
    private final ApiService apiService;
    private final SharedPreferences prefs;

    /// La API para los géneros devielve códigos enteros, entonces
    /// creo un mapa clave/valor y a cada código le doy un valor
    /// que este caso corresponde a un género (toda la API está en inglés
    /// y los géneros en español...pero bueno)

    public static final Map<Integer, Integer> GENRES_MAP = new HashMap<>();

    static {
        GENRES_MAP.put(28, R.string.generoAccion);
        GENRES_MAP.put(12, R.string.generoAventura);
        GENRES_MAP.put(16, R.string.generoAnimacion);
        GENRES_MAP.put(35, R.string.generoComedia);
        GENRES_MAP.put(80, R.string.generoCrimen);
        GENRES_MAP.put(99, R.string.generoDocumental);
        GENRES_MAP.put(18, R.string.generoDrama);
        GENRES_MAP.put(10751, R.string.generoFamilia);
        GENRES_MAP.put(14, R.string.generoFantasia);
        GENRES_MAP.put(36, R.string.generoHistoria);
        GENRES_MAP.put(27, R.string.generoTerror);
        GENRES_MAP.put(10402, R.string.generoMusica);
        GENRES_MAP.put(9648, R.string.generoMisterio);
        GENRES_MAP.put(10749, R.string.generoRomance);
        GENRES_MAP.put(878, R.string.generoCienciaFiccion);
        GENRES_MAP.put(10770, R.string.generoPeliculaTv);
        GENRES_MAP.put(53, R.string.generoSuspense);
        GENRES_MAP.put(10752, R.string.generoBelica);
        GENRES_MAP.put(37, R.string.generoWestern);
    }

    public MovieRepository(Context context) {
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

    }

    public LiveData<Resource<MovieResponse>> getPopularMovies(int page) {


        MutableLiveData<Resource<MovieResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());


        String language = prefs.getString("language", "es-ES");
        Call<MovieResponse> call = apiService.getPopularMovies(page, language);


        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    MovieResponse movieResponse = response.body();
                    result.setValue(Resource.success(movieResponse));
                } else {
                    String errorMsg = "Error " + response.code() + ": " + response.message();
                    result.setValue(Resource.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {

                result.setValue(Resource.error("Error de red: " + t.getMessage()));
            }
        });

        return result;
    }

    public LiveData<Resource<MovieResponse>> getPopularTvShows(int page) {


        MutableLiveData<Resource<MovieResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());


        String language = prefs.getString("language", "es-ES");

        Call<MovieResponse> call = apiService.getPopularTvShows(page, language);


        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    MovieResponse tvResponse = response.body();
                    result.setValue(Resource.success(tvResponse));
                } else {
                    String errorMsg = "Error " + response.code() + ": " + response.message();
                    result.setValue(Resource.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                result.setValue(Resource.error("Error red: " + t.getMessage()));
            }
        });

        return result;
    }

}