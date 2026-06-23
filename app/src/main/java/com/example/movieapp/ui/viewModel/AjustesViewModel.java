package com.example.movieapp.ui.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.data.repository.FirestoreUserRepository;
import com.example.movieapp.data.repository.PreferenciasRepository;
import com.google.firebase.auth.FirebaseAuth;

public class AjustesViewModel extends AndroidViewModel {

    private PreferenciasRepository repository;

    private MutableLiveData<String> username = new MutableLiveData<>();
    private MutableLiveData<String> language = new MutableLiveData<>();
    private MutableLiveData<Boolean> wifiOnly = new MutableLiveData<>();
    private MutableLiveData<Boolean> darkMode = new MutableLiveData<>();
    private MutableLiveData<String> fotoUrl = new MutableLiveData<>();

    private final FirestoreUserRepository userRepo = new FirestoreUserRepository();
    private String uid;

    public AjustesViewModel(@NonNull Application application) {
        super(application);
        repository = new PreferenciasRepository(application);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cargarPreferencias();
    }

    private void cargarPreferencias() {
        //Nombre desde Firestore
        userRepo.obtenerNombre(uid).observeForever(nombre -> {
            username.setValue(nombre != null ? nombre : "");
        });
        userRepo.obternerFotoUrl(uid).observeForever(url -> {
            fotoUrl.setValue(url);
        });
        language.setValue(repository.getLanguage());
        wifiOnly.setValue(repository.getWifiOnly());
        darkMode.setValue(repository.getDarkMode());
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<String> getLanguage() {
        return language;
    }

    public LiveData<Boolean> getWifiOnly() {
        return wifiOnly;
    }

    public LiveData<Boolean> getDarkMode() {
        return darkMode;
    }

    public MutableLiveData<String> getFotoUrl() {
        return fotoUrl;
    }

    public void setUsername(String value) {
        username.setValue(value);
    }

    public void setLanguage(String value) {
        language.setValue(value);
    }

    public void setWifiOnly(boolean value) {
        wifiOnly.setValue(value);
    }

    public void setDarkMode(boolean value) {
        darkMode.setValue(value);
    }

    public void setFotoUrl(String url) { fotoUrl.setValue(url); }

    public boolean guardarPreferencias() {
        boolean oldDarkMode = repository.getDarkMode();

        //Firestore
        userRepo.guardarPerfil(uid, username.getValue(), fotoUrl.getValue());

        repository.savePreferencias(
                language.getValue() != null ? language.getValue() : "es-ES",
                wifiOnly.getValue() != null && wifiOnly.getValue(),
                darkMode.getValue() != null && darkMode.getValue()
        );

        return oldDarkMode != darkMode.getValue();
    }

    public boolean resetPreferencias() {
        boolean oldDarkMode = repository.getDarkMode();

        repository.resetPreferencias();
        cargarPreferencias();

        // Retorna true si cambió el tema
        return oldDarkMode != false;
    }
}