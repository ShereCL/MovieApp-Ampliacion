package com.example.movieapp.data.repository;

import androidx.lifecycle.LifecycleCoroutineScope;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.model.PendienteFS;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.invoke.MutableCallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestorePendienteRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String coleccion( String uid) {
        return "users/" + uid + "/pendientes";
    }

    // Devuelve el LiveData con la lista en tiempo real
    public LiveData<List<PendienteFS>> obtenerPendientes(String uid) {
        MutableLiveData<List<PendienteFS>> liveData = new MutableLiveData<>();

        db.collection("users").document(uid)
                .collection("pendientes")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    List<PendienteFS> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        PendienteFS p = doc.toObject(PendienteFS.class);
                        lista.add(p);
                    }
                    liveData.setValue(lista);
                });

        return liveData;
    }

    // Guarda/sobreescribe
    public void insertar(String uid, PendienteFS pendiente) {
        Map<String, Object> data = new HashMap<>();
        data.put("idTMDB",       pendiente.getIdTMDB());
        data.put("tipo",         pendiente.getTipo());
        data.put("titulo",       pendiente.getTitulo());
        data.put("descripcion",  pendiente.getDescripcion());
        data.put("posterPath",   pendiente.getPosterPath());
        data.put("backdropPath", pendiente.getBackdropPath());
        data.put("releaseDate",  pendiente.getReleaseDate());
        data.put("voteAverage",  pendiente.getVoteAverage());
        data.put("generos",      pendiente.getGeneros());
        data.put("userId",       uid);

        db.collection("users").document(uid)
                .collection("pendientes")
                .document(String.valueOf(pendiente.getIdTMDB()))
                .set(data);
    }

    //Elimina
    public void eliminar(String uid, PendienteFS pendiente) {
        db.collection("users").document(uid)
                .collection("pendientes")
                .document(String.valueOf(pendiente.getIdTMDB()))
                .delete();
    }
    //Compreba que existe
    public void existePendiente(String uid, int idTMDB,
                                java.util.function.Consumer<Boolean> callback) {
        db.collection("users").document(uid)
                .collection("pendientes")
                .document(String.valueOf(idTMDB))
                .get()
                .addOnSuccessListener(doc -> callback.accept(doc.exists()))
                .addOnFailureListener(e -> callback.accept(false));
    }
}
