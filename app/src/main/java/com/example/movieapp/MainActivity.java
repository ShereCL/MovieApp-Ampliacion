package com.example.movieapp;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.movieapp.data.model.PendienteFS;
import com.example.movieapp.data.util.CustomAlertDialog;
import com.example.movieapp.databinding.ActivityMainBinding;
import com.example.movieapp.ui.viewModel.PendienteViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private NavController navController;
    private PendienteViewModel pendienteViewModel;
    private boolean dialogoMostrado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("dark-mode", false);
        AppCompatDelegate.setDefaultNightMode(darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Aplicar el idioma
        String language = prefs.getString("language", "es-ES");
        aplicarIdioma(language);

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.fondoGrisClaro));
        WindowInsetsControllerCompat controllerTransparent = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controllerTransparent.setAppearanceLightStatusBars(true);

        //toolbar
        MaterialToolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        //NavController

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            //Los fragment que no llevan flechita
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.explorarFragment, R.id.pendientesFragment, R.id.seguimientoFragment, R.id.perfilFragment).build();

            //Conecto BottomNav con navController
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            //Conecto el bottomNavigation con el navController

            /*AQUÍ EL NUEVO BOTTONMENU PARA NAVEGAR ENTRE DIFERENTES OPCIONES*/

            BottomNavigationView bottomNav = binding.bottomNavigationView;
            bottomNav.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.ic_menu_tint));
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Actualizar el título según destino
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.detalleFragment || destination.getId() == R.id.addSeguimientoFragment || destination.getId() == R.id.detallePersonaFragment) {
                    bottomNav.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                }
            });
        }

        pendienteViewModel = new ViewModelProvider(this).get(PendienteViewModel.class);
        mostrarDialogoBienvenida();
    }

    //METODOS PARA LOS DIALOGOS

    private void mostrarDialogoBienvenida() {

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        if (pendienteViewModel.isDialogMostrado()) return;

        pendienteViewModel.getTodosPendientes().observe(this, pendientes -> {

            if (pendienteViewModel.isDialogMostrado()) return;

            if (pendientes == null || pendientes.isEmpty()) return;

            Random random = new Random();
            PendienteFS pendienteAleatorio = pendientes.get(random.nextInt(pendientes.size()));

            String titulo = pendienteAleatorio.getTitulo();
            String tipo = pendienteAleatorio.getTipo();
            String tipoTraducido = tipo.equals("movie") ? getString(R.string.opcionPelicula) : getString(R.string.opcionSerie);

            String mensaje = getString(R.string.mensaje_bienvenido, username, tipoTraducido, titulo);

            CustomAlertDialog.show(this, getString(R.string.bienvenido), mensaje, getString(R.string.add_seguimiento_btn), getString(R.string.aun_no),

                    () -> {
                        pendienteViewModel.setDialogMostrado(true);

                        Bundle bundle = new Bundle();
                        bundle.putString("titulo", titulo);
                        bundle.putString("tipo", tipo);
                        bundle.putInt("idTMDB", pendienteAleatorio.getIdTMDB());
                        bundle.putString("posterPath", pendienteAleatorio.getPosterPath());

                        navController.navigate(R.id.addSeguimientoFragment, bundle);
                    }, () -> {
                        pendienteViewModel.setDialogMostrado(true);
                    });
        });
    }

    //Mostrar el dialogo con pendientes

    private void mostrarDialogoConPendiente(String username, List<PendienteFS> pendientes) {
        Random random = new Random();
        PendienteFS pendienteAleatorio = pendientes.get(random.nextInt(pendientes.size()));

        String titulo = pendienteAleatorio.getTitulo();
        String tipo = pendienteAleatorio.getTipo();
        String tipoTraducido = tipo.equals("movie") ? getString(R.string.opcionPelicula) : getString(R.string.opcionSerie);
        String mensaje = getString(R.string.mensaje_bienvenido, username, tipoTraducido, titulo);

        CustomAlertDialog.show(this, getString(R.string.bienvenido), mensaje, getString(R.string.add_seguimiento_btn), getString(R.string.aun_no), false, () -> {
            Bundle bundle = new Bundle();
            bundle.putString("titulo", titulo);
            bundle.putString("tipo", tipo);
            bundle.putInt("idTMDB", pendienteAleatorio.getIdTMDB());
            bundle.putString("posterPath", pendienteAleatorio.getPosterPath());
            navController.navigate(R.id.addSeguimientoFragment, bundle);
        }, null);
    }

    private void mostrarDialogoSinNombre() {
        CustomAlertDialog.show(this, getString(R.string.configuracion), getString(R.string.mensaje_config), getString(R.string.ir_ajustes), getString(R.string.mas_tarde), () -> navController.navigate(R.id.ajustesFragment));
    }


    // CARGAR EL MENU DE OPCIONES DE LA TOOLBAR (AJUSTES)

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ajustes, menu);
        return true;
    }

    // Gestionar los clics en las opciones del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);

    }

    //este le he mejorado porque me daba errores
    public void aplicarIdioma(String languageCode) {
        Locale locale = new Locale(languageCode.split("-")[0]);
        Locale current = getResources().getConfiguration().getLocales().get(0);

        //Solo recrear si realmente es diferente
        if (!current.getLanguage().equals(locale.getLanguage())) {
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            recreate(); //Esto solo se ejecutará una vez si el cambio es real
        }
    }


    /// BOTON DE RETROCESO ///

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}