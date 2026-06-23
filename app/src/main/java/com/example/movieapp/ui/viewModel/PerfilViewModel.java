package com.example.movieapp.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.model.FavoritoPersonaFS;
import com.example.movieapp.data.repository.FirestoreFavoritosPersonaRepository;
import com.example.movieapp.data.repository.FirestoreUserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firestore.v1.StructuredQuery;

import java.util.List;
import java.util.Map;

public class PerfilViewModel  extends ViewModel {

    private final FirestoreUserRepository repository = new FirestoreUserRepository();
    private final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final FirestoreFavoritosPersonaRepository favRepository = new FirestoreFavoritosPersonaRepository();
    public LiveData<Map<String, Object>> getPerfil() {
        return repository.obtenerPerfilCompleto(uid);
    }

    public LiveData<List<FavoritoPersonaFS>> getFavoritosUsuario() {
        return favRepository.obtenerFavoritos(uid);
    }

    public void guardarBio(String biografia) {
        repository.guardarBiografia(uid, biografia);
    }
}
