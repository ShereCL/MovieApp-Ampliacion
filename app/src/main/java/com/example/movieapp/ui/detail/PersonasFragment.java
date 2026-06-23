package com.example.movieapp.ui.detail;

import static com.example.movieapp.data.util.Resource.Status.ERROR;
import static com.example.movieapp.data.util.Resource.Status.LOADING;
import static com.example.movieapp.data.util.Resource.Status.SUCCESS;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.databinding.FragmentPersonasBinding;
import com.example.movieapp.ui.list.PersonAdapter;
import com.example.movieapp.ui.viewModel.PersonasViewModel;

public class PersonasFragment extends Fragment {

    private FragmentPersonasBinding binding;
    private PersonasViewModel viewModel;
    private PersonAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPersonasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PersonasViewModel.class);

        setupRecyclerView();
        setupBuscador();
        setupFiltros();
        setupSwipeRefresh();
        observeTrending();
        observeBusqueda();
    }

    private void setupRecyclerView() {
        adapter = new PersonAdapter(persona -> {
            Bundle args = new Bundle();
            args.putInt("personId", persona.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_explorarFragment_to_detallePersonaFragment, args);
        });

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.rvPersonas.setLayoutManager(layoutManager);
        binding.rvPersonas.setAdapter(adapter);

        binding.rvPersonas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (dy <= 0) return;
                int totalItems = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                if (lastVisible >= totalItems - 4) {
                    Boolean enBusqueda = viewModel.getModosBusqueda().getValue();
                    if (enBusqueda == null || !enBusqueda) {
                        viewModel.cargarMasPersonas();
                    }
                }
            }
        });
    }

    private void setupBuscador() {
        binding.etBuscarPersona.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.length() >= 2) {
                    viewModel.buscar(query);
                } else if (query.isEmpty()) {
                    viewModel.limpiarBusqueda();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupFiltros() {
        //mostrar/ocultar panel
        binding.btnFiltrosPersonas.setOnClickListener(v -> {
            boolean visible = binding.panelFiltrosPersonas.getVisibility() == View.VISIBLE;
            binding.panelFiltrosPersonas.setVisibility(visible ? View.GONE : View.VISIBLE);
        });

        binding.chipGroupDepartamento.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipTodos) {
                adapter.setFiltroDepartamento(null);
            } else if (id == R.id.chipActing) {
                adapter.setFiltroDepartamento("Acting");
            } else if (id == R.id.chipDirecting) {
                adapter.setFiltroDepartamento("Directing");
            } else if (id == R.id.chipWriting) {
                adapter.setFiltroDepartamento("Writing");
            } else if (id == R.id.chipProduction) {
                adapter.setFiltroDepartamento("Production");
            }
        });

        //chip para el orden
        binding.chipGroupOrden.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipOrdenRelevancia) {
                adapter.setOrden(PersonAdapter.ORDEN_RELEVANCIA);
            } else if (id == R.id.chipOrdenAZ) {
                adapter.setOrden(PersonAdapter.ORDEN_AZ);
            } else if (id == R.id.chipOrdenZA) {
                adapter.setOrden(PersonAdapter.ORDEN_ZA);
            }
        });

        //limpiar filtros
        binding.btnLimpiarFiltrosPersonas.setOnClickListener(v -> {
            adapter.limpiarFiltros();
            binding.chipGroupDepartamento.check(R.id.chipTodos);
            binding.chipGroupOrden.check(R.id.chipOrdenRelevancia);
        });

        //es aplicar lo que hace es cerrar el panel
        binding.btnAplicarFiltrosPersonas.setOnClickListener(v ->
                binding.panelFiltrosPersonas.setVisibility(View.GONE)
        );
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            String query = binding.etBuscarPersona.getText().toString().trim();
            if (query.length() >= 2) {
                viewModel.buscar(query);
            } else {
                viewModel.recargar();
            }
            binding.swipeRefresh.setRefreshing(false);
        });
    }

    private void observeTrending() {
        viewModel.getTrendingPersonas().observe(getViewLifecycleOwner(), resource -> {
            Boolean enBusqueda = viewModel.getModosBusqueda().getValue();
            if (enBusqueda != null && enBusqueda) return;

            switch (resource.status) {
                case LOADING:
                    binding.swipeRefresh.setRefreshing(true);
                    binding.tvSinResultados.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    binding.swipeRefresh.setRefreshing(false);
                    if (resource.data != null && !resource.data.isEmpty()) {
                        adapter.setPersonas(resource.data);
                        binding.tvSinResultados.setVisibility(View.GONE);
                    } else {
                        binding.tvSinResultados.setVisibility(View.VISIBLE);
                    }
                    break;

                case ERROR:
                    binding.swipeRefresh.setRefreshing(false);
                    binding.tvSinResultados.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void observeBusqueda() {
        viewModel.getPersonasBusqueda().observe(getViewLifecycleOwner(), personas -> {
            Boolean enBusqueda = viewModel.getModosBusqueda().getValue();
            if (enBusqueda == null || !enBusqueda) return;

            adapter.setPersonas(personas);
            binding.tvSinResultados.setVisibility(
                    personas == null || personas.isEmpty() ? View.VISIBLE : View.GONE
            );
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}