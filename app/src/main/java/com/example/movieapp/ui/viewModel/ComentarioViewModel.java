package com.example.movieapp.ui.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.model.Comentario;
import com.example.movieapp.data.repository.FirestoreComentarioRepository;
import com.example.movieapp.data.repository.FirestoreUserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ComentarioViewModel extends AndroidViewModel {

    private final FirestoreComentarioRepository comentarioRepo;
    private final FirestoreUserRepository userRepo;
    private final String uid;

    public ComentarioViewModel(@NonNull Application application) {
        super(application);
        comentarioRepo = new FirestoreComentarioRepository();
        userRepo = new FirestoreUserRepository();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = (user != null) ? user.getUid() : null;
    }

    public LiveData<List<Comentario>> getComentarios(int tmdbId) {
        if (uid == null) return new MutableLiveData<>(new ArrayList<>());
        return comentarioRepo.obtenerComentarios(tmdbId);
    }

    public void publicar(int tmdbId, String mensaje) {
        if (uid == null) return;

        userRepo.obtenerDatosUsuario(uid).observeForever(datos -> {
            String nombre = (datos != null && datos.first != null && !datos.first.isEmpty()) ? datos.first : "Usuario";
            String fotoUrl = (datos != null && datos.second != null) ? datos.second : "";
            comentarioRepo.publicarComentario(tmdbId, uid, nombre, mensaje, fotoUrl);
        });
    }
}