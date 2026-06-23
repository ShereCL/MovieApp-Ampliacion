package com.example.movieapp.ui.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.model.PendienteFS;
import com.example.movieapp.data.repository.FirestorePendienteRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class PendienteViewModel extends AndroidViewModel {

    private final FirestorePendienteRepository repository;
    private final LiveData<List<PendienteFS>> todos;
    private final String userId;
    private boolean dialogMostrado = false;

    public PendienteViewModel(@NonNull Application application) {
        super(application);
        repository = new FirestorePendienteRepository();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            todos = repository.obtenerPendientes(userId);
        } else {
            userId = null;
            todos = new MutableLiveData<>(new ArrayList<>());
        }
    }

    //Me devuelve la lista de pendientes
    public LiveData<List<PendienteFS>> getTodosPendientes() {
        return todos;
    }

    //Insertar una peli o serie en pendientes
    public void insertarPendiente(PendienteFS pendiente) {
        if (userId == null) return;
        pendiente.setUserId(userId);
        repository.insertar(userId, pendiente);
    }

    //Comprobar si existe en la lista
    public void existePendiente(int idTMDB, java.util.function.Consumer<Boolean> callback) {
        repository.existePendiente(userId, idTMDB, callback);
    }

    //elimina una peli o serie de pendientes
    public void eliminarPendiente(PendienteFS pendiente) {
        if (userId == null) return;
        repository.eliminar(userId, pendiente);
    }

    public boolean isDialogMostrado() {
        return dialogMostrado;
    }

    public void setDialogMostrado(boolean value) {
        dialogMostrado = value;
    }
}
