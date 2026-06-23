package com.example.movieapp.ui.auth;

import static androidx.constraintlayout.motion.widget.TransitionBuilder.validate;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.R;
import com.example.movieapp.data.auth.AuthRepository;
import com.example.movieapp.data.auth.AuthState;
import com.example.movieapp.data.repository.FirestoreUserRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<AuthState> authState = new MutableLiveData<>();

    private final FirestoreUserRepository userRepo = new FirestoreUserRepository();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository();
    }

    public MutableLiveData<AuthState> getAuthState() {
        return authState;
    }

    public FirebaseUser getCurrentUser() {
        return repository.getCurrentUser();
    }

    public void logout() {
        repository.logout();
    }

    //Login
    public void login(String email, String password) {
        String error = validate(email, password);
        if (error != null) {
            authState.setValue(AuthState.error(error));
            return;
        }

        authState.setValue(AuthState.loading());
        repository.login(email.trim(), password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(user));
            }

            @Override
            public void onError(String message) {
                authState.postValue(AuthState.error(traducirErrorFirebase(message)));
            }
        });
    }

    //registro
    public void register(String email, String password, String nombre, @Nullable String fotoUrl) {
        authState.setValue(AuthState.loading());

        final String urlFinal = (fotoUrl == null || fotoUrl.isEmpty())
                ? "https://namzgkjglxpiwveqfufj.supabase.co/storage/v1/object/public/perfiles/perfildefectoapp.jpg"
                : fotoUrl;
        repository.register(email.trim(), password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                userRepo.guardarPerfil(user.getUid(), nombre, urlFinal);
                authState.postValue(AuthState.success(user));
            }

            @Override
            public void onError(String message) {
                authState.postValue(AuthState.error(message));
            }
        });
    }

    //Reset de password
    public void resetPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            authState.setValue(AuthState.error(getApplication().getString(R.string.errorEmailValido)));
            return;
        }
        authState.setValue(AuthState.loading());
        repository.resetPassword(email, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(null));
            }

            @Override
            public void onError(String message) {
                authState.postValue(AuthState.error(message));

            }
        });
    }

    //Login con Google

    public void loginWithGoogle(String idToken) {
        if (idToken == null || idToken.trim().isEmpty()) {
            authState.setValue(AuthState.error(getApplication().getString(R.string.errorTokenGoogle)));
            return;
        }

        authState.setValue(AuthState.loading());

        repository.loginWithGoogle(idToken, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                authState.postValue(AuthState.success(user));
            }

            @Override
            public void onError(String message) {
                authState.postValue(AuthState.error(message));
            }
        });
    }

    //Método auxiliar para las validadciones
    private String validate(String email, String password) {
        if (email == null || email.trim().isEmpty())
            return getApplication().getString(R.string.errorCorreoObligatorio);
        if (password == null || password.isEmpty())
            return getApplication().getString(R.string.errorPasswordObligatoria);
        return null;
    }

    //Método traductor de errores al español para enterarme mejor
    private String traducirErrorFirebase(String errorMessage) {
        if (errorMessage != null &&
                (errorMessage.contains("incorrect, malformed or has expired") ||
                        errorMessage.contains("auth credential is incorrect"))) {
            return getApplication().getString(R.string.errorCredenciales);
        }
        return errorMessage;
    }

}
