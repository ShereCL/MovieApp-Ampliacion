package com.example.movieapp.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movieapp.data.model.Person;
import com.example.movieapp.data.repository.TmdbRepository;
import com.example.movieapp.data.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class PersonasViewModel extends ViewModel {

    private final TmdbRepository repository = new TmdbRepository();

    public MutableLiveData<Integer> trendingPage = new MutableLiveData<>(1);

    private final MutableLiveData<Resource<List<Person>>> trendingPersonasAcumuladas = new MutableLiveData<>();
    private final List<Person> listaAcumulada = new ArrayList<>();
    private boolean cargando = false;

    private final MutableLiveData<List<Person>> personasBusqueda = new MutableLiveData<>();
    private final MutableLiveData<Boolean> modoBusqueda = new MutableLiveData<>(false);

    public PersonasViewModel() {
        cargarPagina(1);
    }

    public LiveData<Resource<List<Person>>> getTrendingPersonas() {
        return trendingPersonasAcumuladas;
    }

    public LiveData<List<Person>> getPersonasBusqueda() {
        return personasBusqueda;
    }

    public LiveData<Boolean> getModosBusqueda() {
        return modoBusqueda;
    }

    public void cargarMasPersonas() {
        if (cargando) return;
        Integer paginaActual = trendingPage.getValue();
        if (paginaActual == null) paginaActual = 1;
        cargarPagina(paginaActual + 1);
    }

    public void recargar() {
        listaAcumulada.clear();
        cargarPagina(1);
    }

    private void cargarPagina(int pagina) {
        cargando = true;
        trendingPersonasAcumuladas.setValue(Resource.loading());

        repository.getTrendingPersonas(pagina).observeForever(resource -> {
            if (resource == null) {
                cargando = false;
                return;
            }
            switch (resource.status) {
                case SUCCESS:
                    if (resource.data != null && !resource.data.isEmpty()) {
                        if (pagina == 1) listaAcumulada.clear();
                        listaAcumulada.addAll(resource.data);
                        trendingPage.setValue(pagina);
                        trendingPersonasAcumuladas.setValue(Resource.success(new ArrayList<>(listaAcumulada)));
                    }
                    cargando = false;
                    break;
                case ERROR:
                    trendingPersonasAcumuladas.setValue(Resource.error(resource.message));
                    cargando = false;
                    break;
                case LOADING:
                    break;
            }
        });
    }

    public void buscar(String query) {
        modoBusqueda.setValue(true);
        repository.buscarPersonas(query, personasBusqueda);
    }

    public void limpiarBusqueda() {
        modoBusqueda.setValue(false);
        personasBusqueda.setValue(null);
    }
}