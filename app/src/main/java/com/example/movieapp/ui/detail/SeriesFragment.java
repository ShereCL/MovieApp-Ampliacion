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
import com.example.movieapp.databinding.FragmentSeriesBinding;
import com.example.movieapp.ui.viewModel.PendienteViewModel;
import com.example.movieapp.ui.list.MovieAdapter;
import com.example.movieapp.ui.viewModel.MovieViewModel;

import java.util.ArrayList;
import java.util.List;


public class SeriesFragment extends Fragment {

    private FragmentSeriesBinding binding;
    private MovieAdapter adapter;
    private MovieViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSeriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        setupRecyclerView();
        setupSwipeRefresh();
        observeTvShows();
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
        adapter.setAddPendingClickListener(serie -> {
            PendienteFS pendiente = new PendienteFS(
                    0,
                    "serie",
                    serie.getId(),
                    serie.getTitle(),
                    serie.getGenres(),
                    serie.getOverview(),
                    serie.getPoster_path(),
                    serie.getBackdropPath(),
                    serie.getReleaseDate(),
                    serie.getVoteAverage()
            );

            pendienteViewModel.insertarPendiente(pendiente);
            CustomToast.show(getContext(), getString(R.string.msgSerieAnadidaPendientes, serie.getTitle()), CustomToast.Type.SUCCESS);        });

        binding.rvSeries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSeries.setAdapter(adapter);
    }

    private void navigateToDetail(Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("movie_obj", movie);
        bundle.putInt("movie_id", movie.getId());
        bundle.putBoolean("id_tv_show", true);

        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.action_explorarFragment_to_detalleFragment, bundle);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.tvPage.setValue(1);
        });
    }

    private void observeTvShows() {
        viewModel.getTvShows().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.swipeRefresh.setRefreshing(true);
                    break;
                case SUCCESS:
                    binding.swipeRefresh.setRefreshing(false);
                    if (resource.data != null && resource.data.getResults() != null) {
                        adapter.setMovies(resource.data.getResults());
                    }
                    break;
                case ERROR:
                    binding.swipeRefresh.setRefreshing(false);
                    CustomToast.show(getContext(), getString(R.string.errorCargaSeries, resource.message), CustomToast.Type.ERROR);                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


