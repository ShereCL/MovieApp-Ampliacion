package com.example.movieapp.ui.detail;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.databinding.FragmentTabPerfilBinding;
import com.example.movieapp.ui.list.RecomendacionAdapter;
import com.example.movieapp.ui.viewModel.PerfilViewModel;
import com.example.movieapp.ui.viewModel.RecomendacionViewModel;

public class TabPerfilFragment extends Fragment {

    private FragmentTabPerfilBinding binding;
    private PerfilViewModel viewModel;
    private RecomendacionViewModel recomViewModel;
    private RecomendacionAdapter recomAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTabPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(PerfilViewModel.class);
        // Personas favoritas
        binding.rvPersonasFavoritas.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        viewModel.getFavoritosUsuario().observe(getViewLifecycleOwner(), listaFavs -> {
            if (listaFavs == null || listaFavs.isEmpty()) {
                binding.rvPersonasFavoritas.setVisibility(View.GONE);
                binding.layoutEmptyPersonas.setVisibility(View.VISIBLE);
            } else {
                binding.layoutEmptyPersonas.setVisibility(View.GONE);
                binding.rvPersonasFavoritas.setVisibility(View.VISIBLE);
                com.example.movieapp.ui.list.FavoritoPersonaFSAdapter adapter =
                        new com.example.movieapp.ui.list.FavoritoPersonaFSAdapter(listaFavs, personaId -> {
                            Bundle bundle = new Bundle();
                            bundle.putInt("personId", personaId);
                            androidx.navigation.Navigation.findNavController(requireView())
                                    .navigate(R.id.action_perfilFragment_to_detallePersonaFragment, bundle);
                        });
                binding.rvPersonasFavoritas.setAdapter(adapter);
            }
        });
        setupRecomendaciones();
    }
    private void setupRecomendaciones() {
        recomAdapter = new RecomendacionAdapter();
        LinearLayoutManager lm = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvRecomendaciones.setLayoutManager(lm);
        binding.rvRecomendaciones.setAdapter(recomAdapter);

        recomViewModel = new ViewModelProvider(this).get(RecomendacionViewModel.class);
        recomViewModel.getRecomendados().observe(getViewLifecycleOwner(), result -> {
            if (result == null || result.peliculas.isEmpty()) {
                // Sin datos: muestra el placeholder "Próximamente"
                binding.rvRecomendaciones.setVisibility(View.GONE);
                binding.cardRecomendacionesPlaceholder.setVisibility(View.VISIBLE);
            } else {
                binding.cardRecomendacionesPlaceholder.setVisibility(View.GONE);
                binding.rvRecomendaciones.setVisibility(View.VISIBLE);
                recomAdapter.setItems(result.peliculas, result.tipo);
            }
        });

        recomAdapter.setOnItemClickListener((movie, tipo) -> {
            Bundle bundle = new Bundle();
            bundle.putInt("movie_id", movie.getId());
            bundle.putBoolean("id_tv_show", tipo.equals("serie"));
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_perfilFragment_to_detalleFragment, bundle);
        });
        binding.rvRecomendaciones.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            private float startX, startY;

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = e.getX();
                        startY = e.getY();
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = Math.abs(e.getX() - startX);
                        float dy = Math.abs(e.getY() - startY);
                        if (dx > dy) {
                            rv.getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            rv.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        rv.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}