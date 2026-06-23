package com.example.movieapp.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.movieapp.MainActivity;
import com.example.movieapp.R;
import com.example.movieapp.data.supabase.SupabaseClient;
import com.example.movieapp.data.supabase.SupabaseStorageApi;
import com.example.movieapp.data.util.CustomToast;
import com.example.movieapp.data.util.ImageUtils;
import com.example.movieapp.data.util.Validadores;
import com.example.movieapp.databinding.FragmentRegisterBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private ActivityResultLauncher<Intent> googleLauncher;
    private ActivityResultLauncher<String> galeriaLauncher;
    private GoogleSignInClient googleClient;
    private AuthViewModel viewModel;

    private Uri fotoUri = null;
    private String fotoUrl = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);


        //Inicialización normal
        observeAuthState();
        configurarValidaciones(); // aqui aplico la fución que gestina las validaciones
        inicializarBotones();

        // Inicialización con Google
        configurarGoogleSignIn();
        inicializarLauncherGoogleSignIn();

        //Imagen de perfil
        galeriaLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                fotoUri = uri;
                //esto muestra la previsualizacion de la imagen que se ha seleccionado
                binding.ivFotoPerfil.setImageURI(uri);
            }
        });
        binding.fotoPerfilContainer.setOnClickListener(v -> {
            galeriaLauncher.launch("image/*");
        });
    }

    /// APLICACIÓN DE VALIDACIONES ///
    private void configurarValidaciones() {
        //cuando se escribe en el mail se borra el error saltado
        binding.emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.emailInputLayout.setError(null);
            }
        });

        //Aqquí lo mismo pero con la contraseña
        binding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.passwordInputLayout.setError(null);
            }
        });

        //Y lo mismo para la confirmación de contraseña
        binding.confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.confirmPasswordInputLayout.setError(null);

            }
        });
    }


    private void observeAuthState() {

        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            habilitarInterfaz(state.loading);

            if (state.error != null) {
                CustomToast.show(requireContext(), state.error, CustomToast.Type.ERROR);
                return;
            }

            if (state.user != null) {
                goToMain();
            }
        });
    }


    private void habilitarInterfaz(boolean cargando) {
        if (cargando) {
            binding.registerButton.setEnabled(false);
            binding.googleSignInButton.setEnabled(false);
            binding.emailEditText.setEnabled(false);
            binding.passwordEditText.setEnabled(false);
            binding.confirmPasswordEditText.setEnabled(false);
            binding.goToLoginText.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.registerButton.setEnabled(true);
            binding.googleSignInButton.setEnabled(true);
            binding.emailEditText.setEnabled(true);
            binding.passwordEditText.setEnabled(true);
            binding.confirmPasswordEditText.setEnabled(true);
            binding.goToLoginText.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void inicializarBotones() {
        //registro con validaciones
        binding.registerButton.setOnClickListener(v -> {
            if (validarFormulario()) {
                String email = binding.emailEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                String nombre = binding.etNombre.getText().toString().trim();
                //mostrar el loading
                habilitarInterfaz(true);
                if (fotoUri != null) {
                    subirFotoYRegistrar(email, password, nombre);
                } else {
                    viewModel.register(email, password, nombre, null);
                }
            }
        });

        //Volver al login
        binding.goToLoginText.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });


        //Registro con Google, de momento nada
        binding.googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = googleClient.getSignInIntent();
            googleLauncher.launch(signInIntent);
        });
    }

    //Función para subir la foto de perfil y registrar al usuario
    private void subirFotoYRegistrar(String email, String password, String nombre) {
        new Thread(() -> {
            try {
                byte[] bytes = ImageUtils.uriToBytes(requireContext(), fotoUri);

                String fileName = "perfil_" + System.currentTimeMillis() + ".jpg";
                RequestBody requestBody = RequestBody.create(bytes, MediaType.parse("image/jpeg"));
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", fileName, requestBody);

                //llama de forma síncrona a supabase
                SupabaseStorageApi api = SupabaseClient.getClient().create(SupabaseStorageApi.class);
                Response<Void> response = api.uploadImage(SupabaseClient.BUCKET, fileName, part).execute();

                if (response.isSuccessful()) {
                    String url = "https://namzgkjglxpiwveqfufj.supabase.co/storage/v1/object/public/"
                            + SupabaseClient.BUCKET + "/" + fileName;

                    requireActivity().runOnUiThread(() ->
                            viewModel.register(email, password, nombre, url)
                    );
                } else {
                    requireActivity().runOnUiThread(() -> {
                        habilitarInterfaz(false);
                        CustomToast.show(requireContext(), getString(R.string.errorSubirFotoRegistrarSinElla), CustomToast.Type.ERROR);
                        viewModel.register(email, password, nombre, null);
                    });
                }

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    habilitarInterfaz(false);
                    CustomToast.show(requireContext(),
                            "Error al subir foto, se registrará sin ella",
                            CustomToast.Type.ERROR);
                    viewModel.register(email, password, nombre, null);
                });
            }
        }).start();
    }

    //Función validadora del formulario

    public boolean validarFormulario() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString();
        String nombre = binding.etNombre.getText().toString().trim();
        boolean esValido = true;

        //valido nombre
        if (nombre.isEmpty()) {
            binding.nombreInputLayout.setError(getString(R.string.errorNombreObligatorio));
            esValido = false;
        }

        //valido email
        if (!Validadores.validarEmail(email)) {
            binding.emailInputLayout.setError(Validadores.getMensajeErrorEmail(requireContext(), email));
            esValido = false;
        }

        //valido password
        if (!Validadores.validarPassword(password)) {
            binding.passwordInputLayout.setError(Validadores.getMensajeErrorPassword(requireContext(), password));
            esValido = false;
        }

        //valido que las contraseñas coincidan
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordInputLayout.setError(getString(R.string.errorConfirmarPassword));
            esValido = false;
        } else if (!password.equals(confirmPassword)) {
            binding.confirmPasswordInputLayout.setError(getString(R.string.errorPasswordNoCoinciden));
            esValido = false;
        }
        return esValido;
    }


//Google registro

    private void configurarGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        googleClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    private void inicializarLauncherGoogleSignIn() {
        googleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                        CustomToast.show(requireContext(), "Registro con Google cancelado.", CustomToast.Type.INFO);
                        return;
                    }

                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    gestionarResultadoSignIn(task);
                }
        );
    }

    private void gestionarResultadoSignIn(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            if (account == null || account.getIdToken() == null) {
                CustomToast.show(requireContext(), "No se pudo obtener la cuenta de Google.", CustomToast.Type.ERROR);
                return;
            }

            viewModel.loginWithGoogle(account.getIdToken());

        } catch (ApiException e) {
            CustomToast.show(requireContext(), "Error Google Sign-In: " + e.getMessage(), CustomToast.Type.ERROR);
        }
    }


    private void goToMain() {
        startActivity(new Intent(requireContext(), MainActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}