package com.example.movieapp.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.movieapp.ActivityLogin;
import com.example.movieapp.MainActivity;
import com.example.movieapp.R;
import com.example.movieapp.data.supabase.SupabaseClient;
import com.example.movieapp.data.supabase.SupabaseStorageApi;
import com.example.movieapp.data.util.CustomToast;
import com.example.movieapp.data.util.ImageUtils;
import com.example.movieapp.databinding.FragmentAjustesBinding;
import com.example.movieapp.ui.viewModel.AjustesViewModel;
import com.example.movieapp.ui.auth.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class AjustesFragment extends Fragment {

    private FragmentAjustesBinding binding;
    private AjustesViewModel viewModel;
    private String[] idiomasCode;
    private AuthViewModel authViewModel;

    private GoogleSignInClient googleClient;
    private FirebaseUser user;

    private ActivityResultLauncher<String> galeriaLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAjustesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AjustesViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        user = authViewModel.getCurrentUser();
        configurarGoogleSignIn();


        //Botón de cerrar sesión

        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            if (isGoogleLogin() && googleClient != null) {
                googleClient.signOut();
            }

            // Volver al login
            Intent intent = new Intent(requireContext(), ActivityLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });

        //Cargar imagen de foto de perfil
        galeriaLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                binding.ivFotoPerfil.setImageURI(uri);
                subirYActualizarFoto(uri);
            }
        });
        binding.fotoPerfilContainer.setOnClickListener(v -> galeriaLauncher.launch("image/*"));

        setupUI();
        observeViewModel();
    }

    //método para la subida de imagenes

    private void subirYActualizarFoto(Uri uri) {
        new Thread(() -> {
            try {
                String fileName = "perfil_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_" + System.currentTimeMillis() + ".jpg";

                byte[] bytes = ImageUtils.uriToBytes(requireContext(), uri);
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", fileName, requestBody);

                SupabaseStorageApi api = SupabaseClient.getClient().create(SupabaseStorageApi.class);
                Response<Void> response = api.uploadImage(SupabaseClient.BUCKET, fileName, part).execute();

                if (response.isSuccessful()) {
                    String url = "https://namzgkjglxpiwveqfufj.supabase.co/storage/v1/object/public/" + SupabaseClient.BUCKET + "/" + fileName;

                    requireActivity().runOnUiThread(() -> {
                        viewModel.setFotoUrl(url);
                        CustomToast.show(requireContext(), getString(R.string.msgFotoActualizada), CustomToast.Type.SUCCESS);
                    });
                } else {
                    requireActivity().runOnUiThread(() -> CustomToast.show(requireContext(), getString(R.string.msgErrorSubirFoto), CustomToast.Type.ERROR));
                }

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> CustomToast.show(requireContext(), getString(R.string.msgErrorSubirFoto), CustomToast.Type.ERROR));
            }
        }).start();
    }

    // Métodos para logout con Google

    private void configurarGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken(getString(R.string.default_web_client_id)).build();
        googleClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    private boolean isGoogleLogin() {
        if (user == null) return false;

        for (UserInfo profile : user.getProviderData()) {
            if ("google.com".equals(profile.getProviderId())) {
                return true;
            }
        }
        return false;
    }

    private void setupUI() {
        // Nombre del usuario
        binding.etUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setUsername(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Spinner de idiomas
        String[] idiomas = getResources().getStringArray(R.array.opcionLanguage);
        idiomasCode = getResources().getStringArray(R.array.apiLanguage);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, idiomas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLanguage.setAdapter(adapter);

        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setLanguage(idiomasCode[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // switch wifi
        binding.switchWifi.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.setWifiOnly(isChecked));

        // switch tema
        binding.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setDarkMode(isChecked);
            binding.tvThemeStatus.setText(isChecked ? getString(R.string.themeDark) : getString(R.string.themeLigth));
        });

        // boton de guardar
        binding.btnSave.setOnClickListener(v -> {
            //obtengos valores actuales
            SharedPreferences prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
            String oldLanguage = prefs.getString("language", "es-ES");
            boolean oldDarkMode = prefs.getBoolean("dark-mode", false);

            // Guard nuevas preferencias
            boolean themeChanged = viewModel.guardarPreferencias();

            // obtengo los valores nuevos
            String newLanguage = viewModel.getLanguage().getValue();
            Boolean newDarkMode = viewModel.getDarkMode().getValue();

            // detecta si se ha cambiado el idioma
            boolean languageChanged = newLanguage != null && !oldLanguage.equals(newLanguage);

            CustomToast.show(getContext(), getString(R.string.msgPreferenciasGuardadas), CustomToast.Type.SUCCESS);
            // Se aplican todos los cambios
            if (languageChanged || themeChanged) {
                // idioma
                if (newLanguage != null) {
                    ((MainActivity) requireActivity()).aplicarIdioma(newLanguage);
                }

                // tema
                if (newDarkMode != null && newDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                requireActivity().recreate();
            }
        });

        // botón de reset
        binding.btnReset.setOnClickListener(v -> {
            boolean themeChanged = viewModel.resetPreferencias();
            CustomToast.show(getContext(), getString(R.string.msgPreferenciasRestablecidas), CustomToast.Type.INFO);
            if (themeChanged) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                requireActivity().recreate();
            }
        });
    }

    private void observeViewModel() {
        // Observar nombre
        viewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            if (!binding.etUser.getText().toString().equals(username)) {
                binding.etUser.setText(username);
            }
        });

        // Obsservar idiomas
        viewModel.getLanguage().observe(getViewLifecycleOwner(), language -> {
            int index = Arrays.asList(idiomasCode).indexOf(language);
            if (binding.spinnerLanguage.getSelectedItemPosition() != index) {
                binding.spinnerLanguage.setSelection(index);
            }
        });

        // observa el wifi
        viewModel.getWifiOnly().observe(getViewLifecycleOwner(), wifiOnly -> {
            if (binding.switchWifi.isChecked() != wifiOnly) {
                binding.switchWifi.setChecked(wifiOnly);
            }
        });

        // observar el tema
        viewModel.getDarkMode().observe(getViewLifecycleOwner(), darkMode -> {
            if (binding.switchTheme.isChecked() != darkMode) {
                binding.switchTheme.setChecked(darkMode);
            }
            binding.tvThemeStatus.setText(darkMode ? getString(R.string.themeDark) : getString(R.string.themeLigth));
        });

        //Observar la foto de perfil
        viewModel.getFotoUrl().observe(getViewLifecycleOwner(), url -> {
            if (url != null && !url.isEmpty()) {
                Glide.with(this)
                        .load(url)
                        .circleCrop()
                        .placeholder(R.drawable.ic_add_foto)
                        .error(R.drawable.directordecine)
                        .into(binding.ivFotoPerfil);
            } else {
                binding.ivFotoPerfil.setImageResource(R.drawable.directordecine);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}