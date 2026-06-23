package com.example.movieapp.ui.viewModel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.R;
import com.example.movieapp.data.model.SeguimientoFS;
import com.example.movieapp.data.repository.FirestoreSeguimientoRepository;
import com.example.movieapp.data.util.ImageUtils;
import com.example.movieapp.data.util.Resource;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SeguimientoFSViewModel extends AndroidViewModel {

    private final FirestoreSeguimientoRepository repository;
    private final String userId;
    private MutableLiveData<Resource<Boolean>> estadoGuardado = new MutableLiveData<>();

    //Estado acrual de los filtros del seguimiento
    private String ordenCampo = "fechaVisualizacion";
    private boolean ordenAscendente = false;
    private String fechaDesde = null;
    private String fechaHasta = null;
    private Float puntuacionMin = null;
    private Float puntuacionMax = null;

    private LiveData<List<SeguimientoFS>> seguimientosActivos;

    public SeguimientoFSViewModel(@NonNull Application application) {
        super(application);
        repository = new FirestoreSeguimientoRepository();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    //Obtener todos
    public LiveData<List<SeguimientoFS>> obternerTodos() {
        return repository.obtenerTodos(userId);
    }

    //obtener todos con filtro
    public LiveData<List<SeguimientoFS>> obtenerTodosFiltrado() {
        seguimientosActivos = repository.obtenerFiltrado(userId, ordenCampo, ordenAscendente, fechaDesde, fechaHasta, puntuacionMin, puntuacionMax);
        return seguimientosActivos;
    }

    //Configurar ordenacion
    public void setOrden(String campo, boolean ascendente) {
        this.ordenCampo = campo;
        this.ordenAscendente = ascendente;
    }

    //Configurar los filtros avanzados
    public void setFiltros(String fechaDesde, String fechaHasta, Float puntuacionMin, Float puntuacionMax) {
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.puntuacionMin = puntuacionMin;
        this.puntuacionMax = puntuacionMax;
    }

    //Limpiar todos los filtros
    public void limpiarFiltros() {
        this.ordenCampo = "fechaVisualizacion";
        this.ordenAscendente = false;
        this.fechaDesde = null;
        this.fechaHasta = null;
        this.puntuacionMin = null;
        this.puntuacionMax = null;
    }

    //Getters del estado de cada filtro para restaurar la vista


    public String getOrdenCampo() {
        return ordenCampo;
    }

    public boolean isOrdenAscendente() {
        return ordenAscendente;
    }

    public String getFechaDesde() {
        return fechaDesde;
    }

    public String getFechaHasta() {
        return fechaHasta;
    }

    public Float getPuntuacionMin() {
        return puntuacionMin;
    }

    public Float getPuntuacionMax() {
        return puntuacionMax;
    }

    //Obtener por id
    public LiveData<SeguimientoFS> obternerPorId(String docId) {
        return repository.obtenerPorId(docId);
    }

    //Eliminar
    public void eliminar(String documentId) {
        repository.eliminar(documentId, new FirestoreSeguimientoRepository.Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    //Comprobar si existe
    public void existe(int tmdbId, String tipo, FirestoreSeguimientoRepository.ExisteCallback callback) {
        repository.existe(tmdbId, tipo, userId, callback);
    }

    //Guardar
    public void guardar(String tipo, int tmdbId, String titulo, String posterPath, String fecha, float puntuacion, Uri imagenUri, int genreId, String genreName) {
        if (imagenUri != null) {
            //subo imuagen a supabase
            try {
                File file = ImageUtils.getFileFromUri(getApplication(), imagenUri);
                repository.subirImg(file).observeForever(subirRes -> {
                    if (subirRes == null) return;
                    if (subirRes.status == Resource.Status.SUCCESS) {
                        //Guardamos la imagen en Firestore
                        guardarEnFirestore(tipo, tmdbId, titulo, posterPath, fecha, puntuacion, subirRes.data, genreId, genreName);
                    } else if (subirRes.status == Resource.Status.ERROR) {
                        estadoGuardado.postValue(Resource.error(getApplication().getString(R.string.errorSubirImagen)));
                    }
                });
            } catch (IOException e) {
                estadoGuardado.postValue(Resource.error(getApplication().getString(R.string.errorProcesarImagen)));
            }
        } else {
            //Sin imagen, se guarda directamente
            guardarEnFirestore(tipo, tmdbId, titulo, posterPath, fecha, puntuacion, null, genreId, genreName);
        }
    }

    private void guardarEnFirestore(String tipo, int tmdbId, String titulo,
                                    String posterPath, String fecha,
                                    float puntuacion, String imgUrl, int genreId, String genreName) {
        SeguimientoFS seg = new SeguimientoFS(null, userId, tipo, tmdbId, titulo, posterPath, fecha, puntuacion, imgUrl, genreId, genreName);
        repository.insertar(seg, new FirestoreSeguimientoRepository.Callback() {
            public void onSuccess() {
                estadoGuardado.postValue(Resource.success(true));
            }

            public void onError(String e) {
                estadoGuardado.postValue(Resource.error(e));
            }
        });
    }

    public LiveData<Resource<Boolean>> getEstadoGuardado() {
        return estadoGuardado;
    }

    public String getUserId() {
        return userId;
    }
}
