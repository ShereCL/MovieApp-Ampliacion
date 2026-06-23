package com.example.movieapp.ui.detail;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.movieapp.R;
import com.example.movieapp.data.model.Genre;
import com.example.movieapp.data.model.GenreResponse;
import com.example.movieapp.data.network.ApiService;
import com.example.movieapp.data.network.MediaItem;
import com.example.movieapp.data.network.RetrofitClient;
import com.example.movieapp.data.util.CustomToast;
import com.example.movieapp.databinding.FragmentAddSeguimientoBinding;
import com.example.movieapp.ui.viewModel.AddSeguimientoViewModel;
import com.example.movieapp.ui.viewModel.SeguimientoFSViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSeguimientoFragment extends Fragment {

    //Variable para gestionar permisos
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private FragmentAddSeguimientoBinding binding;
    private SeguimientoFSViewModel viewModel;
    private AddSeguimientoViewModel addViewModel;
    private String tipoSeleccionado = "pelicula"; //porque pondré por defecto pelicula
    private String fechaSeleccionada = "";
    private Uri imagenUri = null;
    private List<MediaItem> resultadosBusqueda = new ArrayList<>();
    private MediaItem mediaSeleccionada = null;

    //Este es el "launcher" para seleccionar la imagen de la galería
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private ApiService apiService;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar el launcher para pedir permisos y acceder al movil a la galeria
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirGaleria();
                    } else {
                        CustomToast.show(getContext(), getString(R.string.errorPermisoDenegado), CustomToast.Type.ERROR);
                    }
                }
        );

        // Inicializar el launcher para seleccionar imagen
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        imagenUri = result.getData().getData();
                        // CardView
                        binding.cardImagen.setVisibility(View.VISIBLE);

                        binding.ivPreviewRecuerdo.setVisibility(View.VISIBLE);
                        binding.ivPreviewRecuerdo.setImageURI(imagenUri);

                        //Oculto la imagen de añadir del +
                        binding.frameAddImagen.getChildAt(0).setVisibility(View.GONE);

                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddSeguimientoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Inicializo ViewModel y ApiService
        viewModel = new ViewModelProvider(this).get(SeguimientoFSViewModel.class);
        addViewModel = new ViewModelProvider(this).get(AddSeguimientoViewModel.class);
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        //Cargar los datos precargados ya
        cargarDatosPrecargados();

        //Y llamo a la función que realiza la búsqueda y el guardado del seguimiento
        setupListeners();
        observarEstadoGuardado();
    }

    private void observarEstadoGuardado() {
        viewModel.getEstadoGuardado().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    binding.btnGuardar.setEnabled(false);
                    break;
                case SUCCESS:
                    CustomToast.show(getContext(), getString(R.string.msgSeguimientoGuardadoExitoso), CustomToast.Type.SUCCESS);
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                    break;
                case ERROR:
                    binding.btnGuardar.setEnabled(true);
                    CustomToast.show(getContext(), "Error: " + resource.message,
                            CustomToast.Type.ERROR);
                    break;
            }
        });
    }

    //Funcion para cargar los datos precargados de lo que he seleccionado el en alertDialog
    private void cargarDatosPrecargados() {
        if (getArguments() != null) {
            String tituloPrecargado = getArguments().getString("titulo");
            String tipoPrecargado = getArguments().getString("tipo");
            int idTMDB = getArguments().getInt("idTMDB", -1);
            String posterPath = getArguments().getString("posterPath");

            if (tituloPrecargado != null && tipoPrecargado != null && idTMDB != -1) {

                if (tipoPrecargado.equals("movie")) {
                    binding.radioPelicula.setChecked(true);
                    tipoSeleccionado = "pelicula";
                } else if (tipoPrecargado.equals("tv")) {
                    binding.radioSerie.setChecked(true);
                    tipoSeleccionado = "serie";
                }

                binding.etBusqueda.setText(tituloPrecargado);

                MediaItem itemPrecargado = new MediaItem();
                itemPrecargado.setId(idTMDB);
                itemPrecargado.setTitle(tituloPrecargado);
                itemPrecargado.setPosterPath(posterPath);

                resultadosBusqueda = new ArrayList<>();
                resultadosBusqueda.add(itemPrecargado);
                mediaSeleccionada = itemPrecargado;

                List<String> titulos = new ArrayList<>();
                titulos.add(tituloPrecargado);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        titulos
                );
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                binding.spinnerResultados.setAdapter(adapter);
                binding.spinnerResultados.setVisibility(View.VISIBLE);
                binding.spinnerResultados.setSelection(0);

                CustomToast.show(getContext(), getString(R.string.msgDatosPrecargados), CustomToast.Type.INFO);
            }
        }
    }

    private void setupListeners() {

        //1º) Selecciono el tipo de peli o serie con RadioGroup
        binding.radioGroupTipo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioPelicula) {
                tipoSeleccionado = "pelicula";
            } else if (checkedId == R.id.radioSerie) {
                tipoSeleccionado = "serie";
            }
            //limpia la búsqueda anterior
            limpiarBusqueda();
        });

        //boton de buscar que se activa al clickar y llama a la función de búsqueda
        binding.btnBuscar.setOnClickListener(v -> buscarEnTmdb());

        //2º) Los resultados en un spinner
        binding.spinnerResultados.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position >= 0 && position < resultadosBusqueda.size()) {
                            mediaSeleccionada = resultadosBusqueda.get(position);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        mediaSeleccionada = null;
                    }
                }
        );

        //3º) El botón de seleccionar la fecha que llama a la función que abre DatePickerDialog
        binding.btnSeleccionarFecha.setOnClickListener(v -> mostrarDatePcker());

        //4º)Aquí actualizo la puntuación
        binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            binding.tvPuntuacion.setText(String.format(Locale.getDefault(), "%.1f/5", rating));
        });

        //5º) Botón de seleccionar la imagen llamando a la función
        binding.frameAddImagen.setOnClickListener(v -> verificarPermisosYSeleccionarImagen());

        //6º) Botón de guardar el seguimiento
        binding.btnGuardar.setOnClickListener(v -> guardarSeguimiento());
    }

    /// FUNCIONES ///


    //Usando la arquitectura MVVM en condiciones...
    private void buscarEnTmdb() {

        String query = binding.etBusqueda.getText().toString().trim();

        if (query.isEmpty()) {
            CustomToast.show(getContext(), getString(R.string.errorEscribeUnTitulo), CustomToast.Type.INFO);
            return;
        }

        binding.btnBuscar.setEnabled(false);

        addViewModel.buscar(query, tipoSeleccionado);

        // Observar los resultados de la búsqueda
        addViewModel.getResultados().observe(getViewLifecycleOwner(), resultados -> {
            if (resultados != null && !resultados.isEmpty()) {
                resultadosBusqueda = resultados;

                List<String> titulos = new ArrayList<>();
                for (MediaItem item : resultados) {
                    titulos.add(item.toString());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        titulos
                );
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                binding.spinnerResultados.setAdapter(adapter);
                binding.spinnerResultados.setVisibility(View.VISIBLE);
                binding.btnBuscar.setEnabled(true);
            } else {
                CustomToast.show(getContext(), getString(R.string.errorNoResultadosEncontrados), CustomToast.Type.ERROR);
                binding.btnBuscar.setEnabled(true);
            }
        });
    }

    private void mostrarDatePcker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    SimpleDateFormat sdfGuardar = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    fechaSeleccionada = sdfGuardar.format(selectedDate.getTime());

                    SimpleDateFormat sdfMostrar = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    binding.tvFechaSeleccionada.setText(sdfMostrar.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void guardarSeguimiento() {

        //Validaciones:

        if (mediaSeleccionada == null) {
            CustomToast.show(getContext(), getString(R.string.errorSeleccionaPeliculaOSerie), CustomToast.Type.INFO);
            return;
        }

        if (fechaSeleccionada.isEmpty()) {
            CustomToast.show(getContext(), getString(R.string.errorSeleccionaFechaVisualizacion), CustomToast.Type.INFO);
            return;
        }

        float puntuacion = binding.ratingBar.getRating();
        if (puntuacion == 0) {
            CustomToast.show(getContext(), getString(R.string.errorAsignaPuntuacion), CustomToast.Type.INFO);
            return;
        }

        //Verificacion de si ya existe el seguimiento que se quiere ingresar

        viewModel.existe(mediaSeleccionada.getId(), tipoSeleccionado, existe -> {
            if (existe) {
                requireActivity().runOnUiThread(() -> {
                    CustomToast.show(getContext(),
                            tipoSeleccionado.equals("pelicula") ?
                                    getString(R.string.errorYaExisteSeguimientoPelicula) :
                                    getString(R.string.errorYaExisteSeguimientoSerie),
                            CustomToast.Type.ERROR);
                });
            } else {
                //controlar el género antes de guardar
                resolverGeneroYGuardar();
            }
        });


    }

    //Función para limpiar la busqueda
    private void limpiarBusqueda() {
        binding.etBusqueda.setText("");
        binding.spinnerResultados.setVisibility(View.GONE);
        resultadosBusqueda.clear();
        mediaSeleccionada = null;
    }

    //Metodos para la gestion de permisos al abrir la galeria y seleccionar una imagen

    private void verificarPermisosYSeleccionarImagen() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            // Android 12 y anteriores
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    //controlar que tiene género y guardar
    private void resolverGeneroYGuardar() {
        String lang = Locale.getDefault().getLanguage().equals("es") ? "es-ES" : "en-US";
        Call<GenreResponse> call = tipoSeleccionado.equals("pelicula")
                ? apiService.getMovieGenres(lang)
                : apiService.getTvGenres(lang);

        call.enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                int genreId = 0;
                String genreName = "";
                List<Integer> ids = mediaSeleccionada.getGenreIds();
                if (response.isSuccessful() && response.body() != null
                        && ids != null && !ids.isEmpty()) {
                    int firstId = ids.get(0);
                    for (Genre g : response.body().getGenres()) {
                        if (g.getId() == firstId) {
                            genreId = g.getId();
                            genreName = g.getName();
                            break;
                        }
                    }
                }

                final int gId = genreId;
                final String gName = genreName;

                viewModel.guardar(
                        tipoSeleccionado,
                        mediaSeleccionada.getId(),
                        mediaSeleccionada.getTitle(),
                        mediaSeleccionada.getPosterPath(),
                        fechaSeleccionada,
                        binding.ratingBar.getRating(),
                        imagenUri,
                        gId,
                        gName
                );


            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                // Si falla la red, guarda con genreId=0 sin bloquear al usuario
                requireActivity().runOnUiThread(() ->
                        viewModel.guardar(tipoSeleccionado, mediaSeleccionada.getId(),
                                mediaSeleccionada.getTitle(), mediaSeleccionada.getPosterPath(),
                                fechaSeleccionada, binding.ratingBar.getRating(),
                                imagenUri, 0, "")
                );
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}