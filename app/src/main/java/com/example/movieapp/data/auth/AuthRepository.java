package com.example.movieapp.data.auth;

import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthRepository {

    private final FirebaseAuth auth;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String message);
    }

    //Login

    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(auth.getCurrentUser()))
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }

    //Registro

    public void register(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(auth.getCurrentUser()))
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }

    //devuelve el usuario que hay autenticado si lo hay

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    //Cerrar la sesión actual
    public void logout() {
        auth.signOut();
    }

    private String mapError(Exception e) {
        if (e == null || e.getMessage() == null) return "Error desconocido.";
        return e.getMessage();
    }

    //Enviar un email para cambiar contraseña si se ha olvidado

    public void resetPassword(String email, AuthCallback callback) {
        if(email == null || email.trim().isEmpty()) {
            callback.onError("El email no puede estar vacío");
            return;
        }
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused ->
                        callback.onSuccess(null)) //devuelve una confirmacion
                .addOnFailureListener(e ->
                        callback.onError(mapError(e)));
    }


    //Login con google
    public void loginWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> callback.onSuccess(auth.getCurrentUser()))
                .addOnFailureListener(e -> callback.onError(mapError(e)));
    }
}
