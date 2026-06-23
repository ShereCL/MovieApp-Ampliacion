package com.example.movieapp.ui.detail;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.data.model.SeguimientoFS;
import com.example.movieapp.data.util.CustomAlertDialog;
import com.example.movieapp.data.util.CustomToast;
import com.example.movieapp.databinding.FragmentDetalleSeguimientoBinding;
import com.example.movieapp.ui.viewModel.SeguimientoFSViewModel;

import java.util.Locale;

public class DetalleSeguimientoFragment extends Fragment {

    private FragmentDetalleSeguimientoBinding binding;
    private SeguimientoFSViewModel viewModel;
    private SeguimientoFS seguimientoActual;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalleSeguimientoBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SeguimientoFSViewModel.class);

        //Obtengo el seguimiento
        if (getArguments() != null) {
            String documentId = getArguments().getString("documentId");

            if (documentId != null) {
                cargarSeguimiento(documentId);

            } else {
                CustomToast.show(getContext(), getString(R.string.errorCargarSeguimiento), CustomToast.Type.ERROR);
                Navigation.findNavController(view).navigateUp();
            }
        }

        setupListeners();
    }

    //Función para cargar el seguimiento
    private void cargarSeguimiento(String documentId) {
        viewModel.obternerPorId(documentId).observe(getViewLifecycleOwner(), seguimiento -> {
            if (seguimiento != null) {
                seguimientoActual = seguimiento;
                mostrarDatos(seguimiento);
            } else {
                CustomToast.show(getContext(), getString(R.string.errorNoEncontradoSeguimiento), CustomToast.Type.ERROR);
                Navigation.findNavController(binding.getRoot()).navigateUp();
            }
        });
    }

    //Función para mostrar los datos
    private void mostrarDatos(SeguimientoFS seguimiento) {

        //Titulo
        binding.tvTitulo.setText(seguimiento.getTitulo());

        //Tipo
        String tipo = seguimiento.getTipo().equalsIgnoreCase("pelicula")
                ? getString(R.string.tipoPelicula)
                : getString(R.string.tipoSerie);
        binding.tvTipo.setText(tipo);

        // Fecha
        binding.tvFecha.setText(seguimiento.getFechaVisualizacion());

        //Puntuacion
        binding.ratingBarPuntuacion.setRating(seguimiento.getPuntuacion());
        binding.tvPuntuacion.setText(String.format(Locale.getDefault(), getString(R.string.formatoPuntuacionCinco), seguimiento.getPuntuacion()));
        //Poster de la peli/serie
        String posterUrl = "https://image.tmdb.org/t/p/w500" + seguimiento.getPosterPath();

        Glide.with(this).load(posterUrl).placeholder(R.drawable.camaracine).error(R.drawable.delete).into(binding.ivPoster);

        //Imagen de recuerdo

        if (seguimiento.getImgRecuerdo() != null && !seguimiento.getImgRecuerdo().isEmpty()) {
            binding.cardImagenRecuerdo.setVisibility(View.VISIBLE);
            Uri imagenUri = Uri.parse(seguimiento.getImgRecuerdo());
            Glide.with(this).load(imagenUri).placeholder(R.drawable.camaracine).error(R.drawable.delete).into(binding.ivImagenRecuerdo);
        } else {
            binding.cardImagenRecuerdo.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        binding.btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    //Función que te pregunta si estás seguro de eliminar el seguimiento

    private void mostrarDialogoEliminar() {
        CustomAlertDialog.show(requireContext(),
                getString(R.string.dialogoEliminarTitulo),
                getString(R.string.dialogoEliminarMensaje),
                getString(R.string.btnEliminar),
                getString(R.string.btnCancelar), () -> {
                    if (seguimientoActual != null) {
                        viewModel.eliminar(seguimientoActual.getDocumentId());
                        CustomToast.show(getContext(), getString(R.string.msgSeguimientoEliminado), CustomToast.Type.SUCCESS);
                        Navigation.findNavController(binding.getRoot()).navigateUp();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
