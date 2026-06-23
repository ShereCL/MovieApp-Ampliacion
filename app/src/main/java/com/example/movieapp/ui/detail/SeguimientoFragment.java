package com.example.movieapp.ui.detail;

import com.example.movieapp.data.util.CustomToast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.movieapp.R;
import com.example.movieapp.data.model.SeguimientoFS;
import com.example.movieapp.databinding.FragmentSeguimientoBinding;
import com.example.movieapp.ui.list.SeguimientoAdapter;
import com.example.movieapp.ui.viewModel.SeguimientoFSViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeguimientoFragment extends Fragment {

    private FragmentSeguimientoBinding binding;
    private SeguimientoAdapter adapter;
    private SeguimientoFSViewModel viewModel;
    private LiveData<List<SeguimientoFS>> liveDataActivo;
    private String textoBusqueda = "";

    private String[] valoresPuntuacion;
    private String[] opcionesOrden;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSeguimientoBinding.inflate(inflater, container, false);

        valoresPuntuacion = new String[]{
                getString(R.string.labelSinLimite), "0.5", "1.0", "1.5",
                "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"
        };
        opcionesOrden = new String[]{
                getString(R.string.opcionOrdenFechaReciente),
                getString(R.string.opcionOrdenFechaAntigua),
                getString(R.string.opcionOrdenPuntuacionMayor),
                getString(R.string.opcionOrdenPuntuacionMenor)
        };

        setupRecyclerView();
        setupViewModel();
        setupSpinnerOrden();
        setupInputsPuntuacion();
        setupBusqueda();
        setupFiltrosAvanzados();
        setupFab();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new SeguimientoAdapter();

        adapter.setOnItemClickListener(seguimiento -> {
            Bundle bundle = new Bundle();
            bundle.putString("documentId", seguimiento.getDocumentId());
            NavHostFragment.findNavController(SeguimientoFragment.this)
                    .navigate(R.id.action_seguimientoFragment_to_detalleSeguimientoFragment, bundle);
        });

        binding.recyclerViewSeguimientos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSeguimientos.setAdapter(adapter);

        // El eliminar desplazando
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView,
                                  @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder,
                                  @NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                SeguimientoFS seguimiento = adapter.getSeguimiento(position);

                adapter.notifyItemRemoved(position);
                viewModel.eliminar(seguimiento.getDocumentId());
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.recyclerViewSeguimientos);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SeguimientoFSViewModel.class);
        aplicarFiltros();
    }

    private void setupSpinnerOrden() {
        binding.etOrden.setText(opcionesOrden[0]);

        binding.etOrden.setOnClickListener(v -> {
            //Aquí uso la misma clase que en la puntuación para el select
            ListPopupWindow popup = new ListPopupWindow(requireContext());
            //ancho fijo para el popUp
            int widthPx = (int) (getResources().getDisplayMetrics().density * 260);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    opcionesOrden
            );

            popup.setAdapter(adapter);
            popup.setAnchorView(binding.etOrden);
            popup.setWidth(widthPx);
            popup.setHeight(ListPopupWindow.WRAP_CONTENT);
            popup.setModal(true);
            popup.setBackgroundDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_spinner_popup)
            );

            popup.setOnItemClickListener((parent, view, which, id) -> {
                binding.etOrden.setText(opcionesOrden[which]);
                switch (which) {
                    case 0:
                        viewModel.setOrden("fechaVisualizacion", false);
                        break;
                    case 1:
                        viewModel.setOrden("fechaVisualizacion", true);
                        break;
                    case 2:
                        viewModel.setOrden("puntuacion", false);
                        break;
                    case 3:
                        viewModel.setOrden("puntuacion", true);
                        break;
                }
                aplicarFiltros();
                popup.dismiss();
            });

            popup.show();
        });
    }

    private void setupInputsPuntuacion() {
        binding.etPuntuacionMin.setOnClickListener(v -> mostrarDialogoPuntuacion(
                getString(R.string.hintPuntuacionMinima), binding.etPuntuacionMin));

        binding.etPuntuacionMax.setOnClickListener(v -> mostrarDialogoPuntuacion(
                getString(R.string.hintPuntuacionMaxima), binding.etPuntuacionMax));
    }

    private void mostrarDialogoPuntuacion(String titulo, TextInputEditText campo) {
        //Esta clase sirve para poder darle un estilo al select que se despliega para seleccionar una puntuación
        ListPopupWindow popup = new ListPopupWindow(requireContext());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                valoresPuntuacion
        );
        popup.setAdapter(adapter);
        popup.setAnchorView(campo);
        popup.setWidth(400);
        popup.setHeight(ListPopupWindow.WRAP_CONTENT);
        popup.setModal(true);
        popup.setBackgroundDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_spinner_popup)
        );


        popup.setOnItemClickListener((parent, view, position, id) -> {
            campo.setText(valoresPuntuacion[position]);
            popup.dismiss();
        });

        popup.show();
    }

    private void setupBusqueda() {
        binding.searchViewTitulo.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textoBusqueda = s.toString().trim().toLowerCase();
                aplicarFiltros();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }

    //validar filtros

    private boolean validarFiltros() {
        // Validar rango de fechas
        String tagDesde = binding.etFechaDesde.getTag() != null
                ? binding.etFechaDesde.getTag().toString() : "";
        String tagHasta = binding.etFechaHasta.getTag() != null
                ? binding.etFechaHasta.getTag().toString() : "";

        if (!tagDesde.isEmpty() && !tagHasta.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date desde = sdf.parse(tagDesde);
                Date hasta = sdf.parse(tagHasta);
                if (desde != null && hasta != null && desde.after(hasta)) {
                    CustomToast.show(getContext(), getString(R.string.errorFechaPosterior), CustomToast.Type.ERROR);
                    return false;
                }
            } catch (ParseException e) {
                System.out.println("Error de parseo en fecha: " + e);
            }
        }
        // Validar rango de puntuación
        String textoMin = binding.etPuntuacionMin.getText() != null ? binding.etPuntuacionMin.getText().toString() : "";
        String textoMax = binding.etPuntuacionMax.getText() != null ? binding.etPuntuacionMax.getText().toString() : "";
        int posMin = obtenerPosicion(textoMin);
        int posMax = obtenerPosicion(textoMax);


        if (posMin > 0 && posMax > 0 && posMin > posMax) {
            CustomToast.show(getContext(), getString(R.string.errorRangoPuntuacion), CustomToast.Type.ERROR);
            return false;
        }
        return true;
    }

    private int obtenerPosicion(String valor) {
        for (int i = 0; i < valoresPuntuacion.length; i++) {
            if (valoresPuntuacion[i].equals(valor)) return i;
        }
        return 0;
    }


    private void setupFiltrosAvanzados() {

        // Mostrar/ocultar panel
        binding.btnFiltrosAvanzados.setOnClickListener(v -> {
            if (binding.panelFiltrosAvanzados.getVisibility() == View.VISIBLE) {
                binding.panelFiltrosAvanzados.setVisibility(View.GONE);
            } else {
                binding.panelFiltrosAvanzados.setVisibility(View.VISIBLE);
            }
        });

        binding.etFechaDesde.setOnClickListener(v -> mostrarDatePicker(true));

        binding.etFechaHasta.setOnClickListener(v -> mostrarDatePicker(false));

        // Botón Aplicar
        binding.btnAplicarFiltros.setOnClickListener(v -> {
            if (validarFiltros()) {
                binding.panelFiltrosAvanzados.setVisibility(View.GONE);
                aplicarFiltros();
            }
        });

        // Botón Limpiar
        binding.btnLimpiarFiltros.setOnClickListener(v -> {
            binding.etFechaDesde.setText("");
            binding.etFechaDesde.setTag("");
            binding.etFechaHasta.setText("");
            binding.etFechaHasta.setTag("");
            binding.etPuntuacionMin.setText("");
            binding.etPuntuacionMax.setText("");
            viewModel.limpiarFiltros();
            actualizarOrdenDesdeSpinner();
            binding.panelFiltrosAvanzados.setVisibility(View.GONE);
            aplicarFiltros();
        });
    }

    private void mostrarDatePicker(boolean esDesde) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                requireContext(),
                (view, year, month, day) -> {
                    Calendar sel = Calendar.getInstance();
                    sel.set(year, month, day);

                    // Para guardarlo en Firestore = yyyy-MM-dd
                    SimpleDateFormat sdfFirestore = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    // Se muestra de un modo legible en dd/MM/yyyy
                    SimpleDateFormat sdfMostrar = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    String fechaFirestore = sdfFirestore.format(sel.getTime());
                    String fechaMostrar = sdfMostrar.format(sel.getTime());

                    if (esDesde) {
                        binding.etFechaDesde.setText(fechaMostrar);
                        binding.etFechaDesde.setTag(fechaFirestore);
                    } else {
                        binding.etFechaHasta.setText(fechaMostrar);
                        binding.etFechaHasta.setTag(fechaFirestore);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    //Aplicar los filtros
    public void aplicarFiltros() {
        //lee las fechas o el null si está vacío
        String fechaDesde = binding.etFechaDesde.getTag() != null && !binding.etFechaDesde.getTag().toString().isEmpty() ? binding.etFechaDesde.getTag().toString() : null;
        String fechaHasta = binding.etFechaHasta.getTag() != null && !binding.etFechaHasta.getTag().toString().isEmpty() ? binding.etFechaHasta.getTag().toString() : null;

        //lee la puntuacion
        String textoMin = binding.etPuntuacionMin.getText() != null ? binding.etPuntuacionMin.getText().toString() : "";
        String textoMax = binding.etPuntuacionMax.getText() != null ? binding.etPuntuacionMax.getText().toString() : "";
        Float puntuacionMin = textoMin.isEmpty() || textoMin.equals("Sin límite") ? null : Float.parseFloat(textoMin);
        Float puntuacionMax = textoMax.isEmpty() || textoMax.equals("Sin límite") ? null : Float.parseFloat(textoMax);
        viewModel.setFiltros(fechaDesde, fechaHasta, puntuacionMin, puntuacionMax);

        //se elimina el observer anterior para evitar los duplicados
        if (liveDataActivo != null) {
            liveDataActivo.removeObservers(getViewLifecycleOwner());
        }

        liveDataActivo = viewModel.obtenerTodosFiltrado();
        liveDataActivo.observe(getViewLifecycleOwner(), seguimientos -> {
            if (seguimientos == null) return;

            //Filtra por título
            List<SeguimientoFS> filtrados = new ArrayList<>();
            for (SeguimientoFS s : seguimientos) {
                if (TextUtils.isEmpty(textoBusqueda) || s.getTitulo().toLowerCase().contains(textoBusqueda)) {
                    filtrados.add(s);
                }
            }
            adapter.setSeguimiento(filtrados);
            if (filtrados.isEmpty()) {
                binding.recyclerViewSeguimientos.setVisibility(View.GONE);
                binding.layoutVacio.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewSeguimientos.setVisibility(View.VISIBLE);
                binding.layoutVacio.setVisibility(View.GONE);
            }
        });
    }

    private void setupFab() {
        binding.fabAgregarSeguimiento.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_seguimientoFragment_to_addSeguimientoFragment);
        });
    }

    private void actualizarOrdenDesdeSpinner() {
        binding.etOrden.setText(opcionesOrden[0]);
        viewModel.setOrden("fechaVisualizacion", false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
