package com.example.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.movieapp.databinding.ActivityLoginBinding;
import com.example.movieapp.ui.auth.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class ActivityLogin extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Se aplica el idioma gardado desde la aplicación al login y registro también
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String language = prefs.getString("language", "es-ES");
        Locale locale = new Locale(language.split("-")[0]);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        //Si hay un usuario logueado, se salta al main directamente
        if(viewModel.getCurrentUser() != null) {
            goToMain();
            return;
        }
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}