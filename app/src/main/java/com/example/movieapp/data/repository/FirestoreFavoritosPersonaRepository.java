package com.example.movieapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.R;
import com.example.movieapp.data.model.FavoritoPersonaFS;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirestoreFavoritosPersonaRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String COLECCION = "personas_favoritas";

    public interface Callback {
        void onSuccess();

        void onError(String error);
    }

    public interface ExisteCallback {
        void onResult(boolean existe);
    }

    //obtenemos el userId del usuario logueado
    private String getUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    //guardar persona favorita
    public void guardar(FavoritoPersonaFS favorito, Callback callback) {
        db.collection(COLECCION).add(favorito).addOnSuccessListener(ref -> callback.onSuccess()).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    //eliminar por id persona
    public void eliminar(int personaId, Callback callback) {
        String userId = getUserId();
        if (userId == null) {
            callback.onError(String.valueOf(R.string.usuarioNoAutenticado));
            return;
        }

        db.collection(COLECCION).whereEqualTo("userId", userId).whereEqualTo("personaId", personaId).get().addOnSuccessListener(query -> {
            if (!query.isEmpty()) {
                query.getDocuments().get(0).getReference().delete().addOnSuccessListener(v -> callback.onSuccess()).addOnFailureListener(e -> callback.onError(e.getMessage()));
            } else {
                callback.onSuccess();
            }
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    //Comprueba si ya está en favoritos
    public void esFavorito(int personaId, ExisteCallback callback) {
        String userId = getUserId();
        if (userId == null) {
            callback.onResult(false);
            return;
        }

        db.collection(COLECCION).whereEqualTo("userId", userId).whereEqualTo("personaId", personaId).get().addOnSuccessListener(query -> callback.onResult(!query.isEmpty())).addOnFailureListener(e -> callback.onResult(false));
    }

    //obtenemos todos los favoritos del usuario
    public LiveData<List<FavoritoPersonaFS>> obtenerTodos() {
        MutableLiveData<List<FavoritoPersonaFS>> liveData = new MutableLiveData<>();
        String userId = getUserId();
        if (userId == null) {
            liveData.setValue(new ArrayList<>());
            return liveData;
        }

        db.collection(COLECCION).whereEqualTo("userId", userId).addSnapshotListener((snapshot, e) -> {
            if (e != null || snapshot == null) return;
            List<FavoritoPersonaFS> lista = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshot) {
                lista.add(doc.toObject(FavoritoPersonaFS.class));
            }
            liveData.setValue(lista);
        });

        return liveData;
    }

    //obtener las personas favoritas
    public LiveData<List<FavoritoPersonaFS>> obtenerFavoritos(String uid) {
        MutableLiveData<List<FavoritoPersonaFS>> liveData = new MutableLiveData<>();
        db.collection("personas_favoritas")
                .whereEqualTo("userId", uid)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;
                    List<FavoritoPersonaFS> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        lista.add(doc.toObject(FavoritoPersonaFS.class));
                    }
                    liveData.setValue(lista);
                });
        return liveData;
    }
}