package com.example.movieapp.ui.auth;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavAction;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.movieapp.MainActivity;
import com.example.movieapp.R;
import com.example.movieapp.data.util.CustomToast;
import com.example.movieapp.data.util.Validadores;
import com.example.movieapp.databinding.FragmentLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LoginFragment extends Fragment {
    private ActivityResultLauncher<Intent> googleLauncher;
    private GoogleSignInClient googleClient;
    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Inicialización normal
        observeAuthState();
        configurarValidaciones(); // aqui aplico la fución que gestina las validaciones
        inicializarBotones();

        // Inicialización con Google
        configurarGoogleSignIn();
        inicializarLauncherGoogleSignIn();
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

    }


    private void observeAuthState() {

        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            //Mostrar/Ocultar el loading
            habilitarInterfaz(state.loading);

            //Si hay algun error, lo muestro
            if (state.error != null) {
                CustomToast.show(requireContext(), state.error, CustomToast.Type.ERROR);
                return;
            }

            //Si hay usuario, navegar al main de listas de pelis y series
            if (state.user != null) {
                goToMain();
            }

            //Para cuando el reset de contraseña sea exitoso
            if (state.user == null && state.error == null && !state.loading) {
                CustomToast.show(requireContext(), getString(R.string.emailRestablecerEnviado), CustomToast.Type.INFO);
            }
        });


    }

    private void habilitarInterfaz(boolean cargando) {
        if (cargando) {
            binding.loginButton.setEnabled(false);
            binding.googleSignInButton.setEnabled(false);
            binding.emailEditText.setEnabled(false);
            binding.passwordEditText.setEnabled(false);
            binding.resetPasswordText.setEnabled(false);
            binding.goToRegisterText.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.loginButton.setEnabled(true);
            binding.googleSignInButton.setEnabled(true);
            binding.emailEditText.setEnabled(true);
            binding.passwordEditText.setEnabled(true);
            binding.resetPasswordText.setEnabled(true);
            binding.goToRegisterText.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void inicializarBotones() {
        //Login con validaciones
        binding.loginButton.setOnClickListener(v -> {
            if (validarFormulario()) {
                String email = binding.emailEditText.getText().toString();
                String password = binding.passwordEditText.getText().toString();
                viewModel.login(email, password);
            }

        });

        //Navegar al registro
        binding.goToRegisterText.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
        });

        //Resetar la contraseña
        binding.resetPasswordText.setOnClickListener(v -> mostrarDialogoResetPassword());

        //Login con Google, de momento nada
        binding.googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = googleClient.getSignInIntent();
            googleLauncher.launch(signInIntent);
        });
    }


    //Función validadora del formulario

    public boolean validarFormulario() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString();
        boolean esValido = true;

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

        return esValido;

    }


    //Funcion para reset contraseña

    private void mostrarDialogoResetPassword() {
        final EditText input = new EditText(requireContext());
        input.setHint(getString(R.string.hintEmail));
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setPadding(48, 24, 48, 24);

        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog).setTitle(R.string.restablecerPassword).setView(input).setPositiveButton(getString(R.string.enviar), (dialog, which) -> {
            String email = input.getText().toString().trim();
            viewModel.resetPassword(email);
        }).setNegativeButton(getString(R.string.cancelar), (dialog, which) -> dialog.cancel()).show();
    }


    //Google login

    private void configurarGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken(getString(R.string.default_web_client_id)).build();

        googleClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    private void inicializarLauncherGoogleSignIn() {
        googleLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                // Usuario canceló el login o no se recibieron datos
                CustomToast.show(requireContext(), getString(R.string.loginGoogleCancelado), CustomToast.Type.INFO);
                return;
            }

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

            gestionarResultadoSignIn(task);
        });
    }

    private void gestionarResultadoSignIn(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            if (account == null || account.getIdToken() == null) {
                CustomToast.show(requireContext(), getString(R.string.errorCuentaGoogle), CustomToast.Type.ERROR);
                return;
            }

            // Enviamos el idToken al ViewModel para autenticar en Firebase
            viewModel.loginWithGoogle(account.getIdToken());

        } catch (ApiException e) {
            CustomToast.show(requireContext(), getString(R.string.errorGoogleSignIn) + e.getMessage(), CustomToast.Type.ERROR);
        }
    }

    //Navegar al main
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
