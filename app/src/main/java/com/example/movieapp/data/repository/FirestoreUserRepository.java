package com.example.movieapp.data.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreUserRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Guarda/actualizar perfil del user
    public void guardarPerfil(String uid, String nombre, @Nullable String fotoUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);
        data.put("uid", uid);
        if (fotoUrl != null) {
            data.put("fotoUrl", fotoUrl);
        }

        db.collection("users").document(uid)
                .set(data, com.google.firebase.firestore.SetOptions.merge());
    }

    //Lee el nombre del usuario y lo devuelve por LiveData
    public LiveData<String> obtenerNombre(String uid) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        liveData.setValue(doc.getString("nombre"));
                    } else {
                        liveData.setValue("");
                    }
                });
        return liveData;
    }

    //lee la url de la foto y la devuelve
    public LiveData<String> obternerFotoUrl(String uid) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                liveData.setValue(doc.getString("fotoUrl"));
            } else {
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    //Guarda la bio del usuario
    public void guardarBiografia(String uid, String biografia) {
        db.collection("users").document(uid).update("biografia", biografia);
    }

    //Obtener todo el perfil del usuario
    public LiveData<Map<String, Object>> obtenerPerfilCompleto(String uid) {
        MutableLiveData<Map<String, Object>> liveData = new MutableLiveData<>();
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                liveData.setValue(doc.getData());
            }
        });
        return liveData;
    }
    //Obtengo los datos del usuario, se usa para comentarios, asi muestra la foto de perfil
    public LiveData<android.util.Pair<String, String>> obtenerDatosUsuario(String uid) {
        MutableLiveData<android.util.Pair<String, String>> liveData = new MutableLiveData<>();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String nombre = doc.getString("nombre");
                        String fotoUrl = doc.getString("fotoUrl");
                        liveData.setValue(new android.util.Pair<>(nombre, fotoUrl));
                    } else {
                        liveData.setValue(new android.util.Pair<>("", null));
                    }
                });
        return liveData;
    }
}
