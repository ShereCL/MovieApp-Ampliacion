package com.example.movieapp.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.model.RecomendacionResult;
import com.example.movieapp.data.repository.RecomendacionRepository;


public class RecomendacionViewModel extends ViewModel {
    private final RecomendacionRepository repo = new RecomendacionRepository();
    private LiveData<RecomendacionResult> recomendados;

    public LiveData<RecomendacionResult> getRecomendados() {
        if (recomendados == null) {
            recomendados = repo.obtenerRecomendados();
        }
        return recomendados;
    }

}
