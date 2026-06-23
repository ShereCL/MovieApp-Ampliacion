package com.example.movieapp.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.model.SeguimientoFS;
import com.example.movieapp.data.repository.FirestoreSeguimientoRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class EstadisticasViewModel extends ViewModel {

    private final FirestoreSeguimientoRepository repository = new FirestoreSeguimientoRepository();
    private final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private final MediatorLiveData<List<SeguimientoFS>> seguimientos = new MediatorLiveData<>();

    public EstadisticasViewModel() {
        LiveData<List<SeguimientoFS>> source = repository.obtenerTodos(uid);
        seguimientos.addSource(source, value -> seguimientos.setValue(value));
    }

    public LiveData<List<SeguimientoFS>> getSeguimientos() {
        return seguimientos;
    }
}
