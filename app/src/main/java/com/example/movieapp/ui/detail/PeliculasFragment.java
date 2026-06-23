package com.example.movieapp.ui.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.movieapp.R;
import com.example.movieapp.data.model.Movie;
import com.example.movieapp.data.model.PendienteFS;
import com.example.movieapp.data.util.CustomToast;
import com.example.movieapp.databinding.FragmentPeliculasBinding;
import com.example.movieapp.ui.viewModel.PendienteViewModel;
import com.example.movieapp.ui.list.MovieAdapter;
import com.example.movieapp.ui.viewModel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;

public class PeliculasFragment extends Fragment {

    private FragmentPeliculasBinding binding;
    private MovieViewModel viewModel;
    private MovieAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPeliculasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        setupRecyclerView();
        setupSwipeRefresh();
        observeMovies();

    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter(movie -> {
            navigateToDetail(movie);
        });

        PendienteViewModel pendienteViewModel =
                new ViewModelProvider(requireActivity()).get(PendienteViewModel.class);

        pendienteViewModel.getTodosPendientes().observe(getViewLifecycleOwner(), pendientes -> {
            if (pendientes != null) {
                List<Integer> ids = new ArrayList<>();
                for (PendienteFS p : pendientes) {
                    ids.add(p.getIdTMDB());
                }
                adapter.updatePendingIds(ids);
            }
        });

        //Listener para el boton de pendientes
        adapter.setAddPendingClickListener(movie -> {
            PendienteFS pendiente = new PendienteFS(
                    0,
                    "pelicula",
                    movie.getId(),
                    movie.getTitle(),
                    movie.getGenres(),
                    movie.getOverview(),
                    movie.getPoster_path(),
                    movie.getBackdropPath(),
                    movie.getReleaseDate(),
                    movie.getVoteAverage()
            );

            pendienteViewModel.insertarPendiente(pendiente);
            CustomToast.show(getContext(), getString(R.string.msgPeliculaAnadidaPendientes, movie.getTitle()), CustomToast.Type.SUCCESS);
        });

        binding.rvMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMovies.setAdapter(adapter);
    }

    //Navegar al detalle
    private void navigateToDetail(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("movie_obj", movie);
        bundle.putInt("movie_id", movie.getId());
        bundle.putBoolean("id_tv_show", false);

        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.action_explorarFragment_to_detalleFragment, bundle);
    }


    /// EXPLICACIÓN MÉTODO NUEVO PARA REFRESCAR LA PÁGINA ///
    /// este método lo que hace es que al detectar que el ususario desliza
    /// hacia abajo con el listener específico setOnRefreshListener, actualiza
    /// la paginación a la página 1 viewModel.moviePage.setValue(1);, llamando
    /// al viewModel que recarga las peliculas o las series en el fragment de
    /// las series, desde el inicio
    /// los estados se gestionan desde observeMovies(), mas abajo
    /// en el xml envuelve el recyclerView de los fragment de pelis y series
    /// item_movie.xml son las tarjetas de cada peli o serie, y se vincula desde el
    /// binding de MovieAdapter

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.moviePage.setValue(1);
        });
    }

    private void observeMovies() {

        viewModel.getMovies().observe(getViewLifecycleOwner(), resource -> {

            switch (resource.status) {
                case LOADING:
                    binding.swipeRefresh.setRefreshing(true);
                    break;

                case SUCCESS:
                    if (resource.data != null) {
                        if (resource.data.getResults() != null) {
                        }
                    }

                    binding.swipeRefresh.setRefreshing(false);
                    if (resource.data != null && resource.data.getResults() != null) {
                        adapter.setMovies(resource.data.getResults());
                    }
                    break;

                case ERROR:
                    binding.swipeRefresh.setRefreshing(false);
                    CustomToast.show(getContext(), getString(R.string.errorCargaPeliculas, resource.message), CustomToast.Type.ERROR);
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}