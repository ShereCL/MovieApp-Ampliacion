package com.example.movieapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.model.SeguimientoFS;
import com.example.movieapp.data.supabase.SupabaseClient;
import com.example.movieapp.data.supabase.SupabaseStorageApi;
import com.example.movieapp.data.util.Resource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirestoreSeguimientoRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String COLECCION = "seguimientos";
    private final SupabaseStorageApi storageApi;

    public FirestoreSeguimientoRepository() {
        storageApi = SupabaseClient.getClient().create(SupabaseStorageApi.class);
    }

    //interfaces
    public interface Callback {
        void onSuccess();
        void onError(String error);
    }

    public interface ExisteCallback {
        void onResult(boolean existe);
    }

    //Insertar
     public void insertar(SeguimientoFS seguimiento, Callback callback) {
        db.collection(COLECCION)
                .add(seguimiento)
                .addOnSuccessListener(ref -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
     }

     //Eliminar
     public void eliminar(String documentId, Callback callback) {
        db.collection(COLECCION).document(documentId)
                .delete()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
     }

     //Obtener todos
    public LiveData<List<SeguimientoFS>> obtenerTodos(String userId) {
        MutableLiveData<List<SeguimientoFS>> liveData = new MutableLiveData<>();
        db.collection(COLECCION)
                .whereEqualTo("userId", userId)
                .orderBy("fechaVisualizacion", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if(e != null || snapshot == null) return;
                    List<SeguimientoFS> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        SeguimientoFS s = doc.toObject(SeguimientoFS.class);
                        s.setDocumentId(doc.getId());
                        lista.add(s);
                    }
                    liveData.setValue(lista);
                });
        return liveData;
    }

    //Obtener por id
    public LiveData<SeguimientoFS> obtenerPorId(String documentId) {
        MutableLiveData<SeguimientoFS> liveData = new MutableLiveData<>();
        db.collection(COLECCION).document(documentId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null || !snapshot.exists()) return;
                    SeguimientoFS s = snapshot.toObject(SeguimientoFS.class);
                    if (s != null) s.setDocumentId(snapshot.getId());
                    liveData.setValue(s);
                });
        return liveData;
    }

    //Comprobar que existe
    public void existe(int tmdbId, String tipo, String userId, ExisteCallback callback) {
        db.collection(COLECCION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("tmdbId", tmdbId)
                .whereEqualTo("tipo", tipo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> callback.onResult(!queryDocumentSnapshots.isEmpty()))
                .addOnFailureListener(e -> callback.onResult(false));

    }

    //Subir una imagen a supabase
    public LiveData<Resource<String>> subirImg(File imageFile) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        RequestBody requestFile = RequestBody.create(imageFile, MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        String fileName = "seguimientos/" + imageFile.getName();

        Call<Void> call = storageApi.uploadImage(SupabaseClient.BUCKET, fileName, body);

        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    String fileUrl = response.raw().request().url().toString();
                    result.postValue(Resource.success(fileUrl));
                } else {
                    result.postValue(Resource.error("Error Supabase: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.postValue(Resource.error(t.getMessage()));
            }
        });
        return result;
    }

    //Obtener con filtros
    public LiveData<List<SeguimientoFS>> obtenerFiltrado( String userId, String ordenCampo, boolean ordenAscendente, String fechaDesde, String fechaHasta, Float puntuacionMin, Float puntuacionMax) {
        MutableLiveData<List<SeguimientoFS>> liveData = new MutableLiveData<>();

        Query query = db.collection(COLECCION).whereEqualTo("userId", userId);

        //Filtro por rango de fechas
        //desde
        if(fechaDesde != null && !fechaDesde.isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("fechaVisualizacion", fechaDesde);
        }
        //hasta
        if(fechaHasta != null && !fechaHasta.isEmpty()) {
            query = query.whereLessThanOrEqualTo("fechaVisualizacion", fechaHasta);        }

        //ORDENACION
        boolean hayFiltroFecha = (fechaDesde != null && !fechaDesde.isEmpty()) || (fechaHasta != null && !fechaHasta.isEmpty());

        if(hayFiltroFecha) {
            Query.Direction direction = ordenAscendente ? Query.Direction.ASCENDING : Query.Direction.DESCENDING;
            query = query.orderBy("fechaVisualizacion", direction);
        } else {
            Query.Direction direction = ordenAscendente ? Query.Direction.ASCENDING : Query.Direction.DESCENDING;
            query = query.orderBy(ordenCampo, direction);
        }

        final String campoOrdenFinal = ordenCampo;
        final boolean ascFinal = ordenAscendente;
        final Float pMin = puntuacionMin;
        final Float pMax = puntuacionMax;

        query.addSnapshotListener((snapshot, e) -> {
            if(e != null ) {
                liveData.setValue(new ArrayList<>());
                return;
            }
            List<SeguimientoFS> lista = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshot) {
                SeguimientoFS s = doc.toObject(SeguimientoFS.class);
                s.setDocumentId(doc.getId());
                lista.add(s);
            }
            //Filtro de puntuacion en cliente para que el comportamiento sea el que quiero
            if (pMin != null || pMax != null) {
                List<SeguimientoFS> filtrados = new ArrayList<>();
                for (SeguimientoFS s : lista) {
                    boolean cumpleMin = pMin == null || s.getPuntuacion() >= pMin;
                    boolean cumpleMax = pMax == null || s.getPuntuacion() <= pMax;
                    if (cumpleMin && cumpleMax) filtrados.add(s);
                }
                lista = filtrados;
            }
            if (hayFiltroFecha && campoOrdenFinal.equals("puntuacion")) {
                lista.sort((a, b) -> {
                    int cmp = Float.compare(a.getPuntuacion(), b.getPuntuacion());
                    return ascFinal ? cmp : -cmp;
                });
            }
            liveData.setValue(lista);
        });
        return liveData;
    }

}
