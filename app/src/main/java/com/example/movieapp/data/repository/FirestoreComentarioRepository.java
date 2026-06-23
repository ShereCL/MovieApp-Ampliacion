package com.example.movieapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.model.Comentario;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreComentarioRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Escucha los comentarios de una película en tiempo real
    public LiveData<List<Comentario>> obtenerComentarios(int tmdbId) {
        MutableLiveData<List<Comentario>> liveData = new MutableLiveData<>();

        db.collection("multimedia").document(String.valueOf(tmdbId))
                .collection("comments")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    List<Comentario> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Comentario c = doc.toObject(Comentario.class);
                        c.setId(doc.getId());
                        lista.add(c);
                    }
                    liveData.setValue(lista);
                });

        return liveData;
    }

    // Añade un comentario nuevo
    public void publicarComentario(int tmdbId, String uid,
                                   String nombreUsuario, String mensaje, String fotoUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("nombreUsuario", nombreUsuario);
        data.put("mensaje", mensaje);
        data.put("fecha", Timestamp.now());
        data.put("fotoUrl", fotoUrl != null ? fotoUrl : "");

        db.collection("multimedia").document(String.valueOf(tmdbId))
                .collection("comments")
                .add(data); //el id lo autogenera Firestore
    }
}