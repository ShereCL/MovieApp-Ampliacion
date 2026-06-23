package com.example.movieapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.model.Person;
import com.example.movieapp.data.network.ApiService;
import com.example.movieapp.data.network.MediaItem;
import com.example.movieapp.data.network.PersonResponse;
import com.example.movieapp.data.network.RetrofitClient;
import com.example.movieapp.data.network.TmdbResponse;
import com.example.movieapp.data.util.Resource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TmdbRepository {

    private final ApiService apiService;

    public TmdbRepository() {
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
    }

    public void buscar(String query, String tipo, MutableLiveData<List<MediaItem>> liveData) {
        Call<TmdbResponse> call =
                tipo.equals("pelicula")
                        ? apiService.searchMovies(query, "es-ES", 1)
                        : apiService.searchTv(query, "es-ES", 1);

        call.enqueue(new Callback<TmdbResponse>() {
            @Override
            public void onResponse(Call<TmdbResponse> call, Response<TmdbResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().getResults());
                } else {
                    liveData.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<TmdbResponse> call, Throwable t) {
                liveData.postValue(new ArrayList<>());
            }
        });
    }

    public void buscarPersonas(String query, MutableLiveData<List<Person>> liveData) {
        apiService.searchPersons(query, "es-ES", 1).enqueue(new Callback<PersonResponse>() {
            @Override
            public void onResponse(Call<PersonResponse> call, Response<PersonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().getResults());
                } else {
                    liveData.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<PersonResponse> call, Throwable t) {
                liveData.postValue(new ArrayList<>());
            }
        });
    }

    public void obtenerDetallePersona(int personId, MutableLiveData<Person> liveData) {
        apiService.getPersonDetails(personId, "es-ES", "combined_credits").enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                liveData.postValue(null);
            }
        });
    }

    public LiveData<Resource<List<Person>>> getTrendingPersonas(int page) {
        MutableLiveData<Resource<List<Person>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getTrendingPersonas("day", page)
                .enqueue(new Callback<PersonResponse>() {
                    @Override
                    public void onResponse(Call<PersonResponse> call, Response<PersonResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(Resource.success(response.body().getResults()));
                        } else {
                            //lo pongo en inglés porque es el idioma más conocido y este literal no se puede pasar a String
                            //como tal, además el error lo gestiono en el repo con un CustonToast que es lo que ve el usuario
                            result.setValue(Resource.error("getTrendingPersonas failed"));
                        }
                    }

                    @Override
                    public void onFailure(Call<PersonResponse> call, Throwable t) {
                        result.setValue(Resource.error(t.getMessage()));
                    }
                });

        return result;
    }
}