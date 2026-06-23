package com.example.movieapp.ui.viewModel;

import android.app.Application;
import android.hardware.lights.LightsManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.network.MediaItem;
import com.example.movieapp.data.repository.TmdbRepository;

import java.util.List;

public class AddSeguimientoViewModel extends AndroidViewModel {

    private final TmdbRepository repository = new TmdbRepository();
    private final MutableLiveData<List<MediaItem>> resultados = new MutableLiveData<>();
    public AddSeguimientoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<MediaItem>> getResultados() {
        return resultados;
    }

    public void buscar(String query, String tipo) {
        repository.buscar(query, tipo, resultados);
    }

}
