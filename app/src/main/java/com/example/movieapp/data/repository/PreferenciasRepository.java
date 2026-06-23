package com.example.movieapp.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PreferenciasRepository {

    private SharedPreferences prefs;
    private FirebaseAuth auth;
    private Application application;

    public PreferenciasRepository(Application application) {
        this.application = application;
        this.prefs = application.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    // GETTERS
    public String getUsername() {
        return prefs.getString("username", "");
    }

    public String getLanguage() {
        return prefs.getString("language", "es-ES");
    }

    public boolean getWifiOnly() {
        return prefs.getBoolean("wifi_only", false);
    }

    public boolean getDarkMode() {
        return prefs.getBoolean("dark-mode", false);
    }

    // SETTERS
    public void savePreferencias(String language, boolean wifiOnly, boolean darkMode) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", language);
        editor.putBoolean("wifi_only", wifiOnly);
        editor.putBoolean("dark-mode", darkMode);
        editor.apply();
    }

    public void resetPreferencias() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", "es-ES");
        editor.putBoolean("wifi_only", false);
        editor.putBoolean("dark-mode", false);
        editor.apply();
    }

}