package com.example.movieapp.ui.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.movieapp.R;
import com.example.movieapp.data.model.Movie;
import com.example.movieapp.data.model.PendienteFS;
import com.example.movieapp.data.util.CustomToast;
import com.example.movieapp.databinding.FragmentPendientesBinding;
import com.example.movieapp.ui.list.PendienteAdapter;
import com.example.movieapp.ui.viewModel.PendienteViewModel;

public class PendientesFragment extends Fragment {

    private FragmentPendientesBinding binding;
    private PendienteViewModel viewModel;
    private PendienteAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPendientesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Inicializo el viewModel
        viewModel = new ViewModelProvider(this).get(PendienteViewModel.class);

        //Configuro recycler

        adapter = new PendienteAdapter();
        binding.rvPendientes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPendientes.setAdapter(adapter);

        // Configuro ItemTouchHelper para eliminar el iten con swipe
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                PendienteFS pendiente = adapter.getPendienteAt(position);

                viewModel.eliminarPendiente(pendiente);
                CustomToast.show(requireContext(), getString(R.string.msgElementoEliminado, pendiente.getTitulo()), CustomToast.Type.SUCCESS);
            }
        };

// Adjunto al RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.rvPendientes);


        //Observo LiveData
        viewModel.getTodosPendientes().observe(getViewLifecycleOwner(), pendientes -> {
            if (pendientes.isEmpty()) {
                binding.tvListEmpty.setVisibility(View.VISIBLE);
                binding.tvEmpty.setVisibility(View.VISIBLE);
                binding.rvPendientes.setVisibility(View.GONE);
                binding.ivPalomitas.setVisibility(View.VISIBLE);
                binding.ivPalomitas.setImageResource(R.drawable.palomitasraton);
                binding.btnExplorar.setVisibility(View.VISIBLE);
            } else {
                binding.tvListEmpty.setVisibility(View.GONE);
                binding.tvEmpty.setVisibility(View.GONE);
                binding.ivPalomitas.setVisibility(View.GONE);
                binding.rvPendientes.setVisibility(View.VISIBLE);
                binding.btnExplorar.setVisibility(View.GONE);
                adapter.setPendientes(pendientes);
            }


        });


        //Click en cada item de peli o serie para navegar al detalle
        adapter.setOnItemClickListener(new PendienteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PendienteFS pendiente) {
                //Convierto pendiente a Movie para que se vean todos los detalles al navegar
                Movie movie = new Movie();
                movie.setId(pendiente.getIdTMDB());
                movie.setTitle(pendiente.getTitulo());
                movie.setOverview(pendiente.getDescripcion());
                movie.setPoster_path(pendiente.getPosterPath());
                movie.setBackdropPath(pendiente.getBackdropPath());
                movie.setReleaseDate(pendiente.getReleaseDate());
                movie.setVoteAverage(pendiente.getVoteAverage());
                movie.setGenres(pendiente.getGeneros());


                Bundle bundle = new Bundle();
                bundle.putSerializable("movie_obj", movie);
                Navigation.findNavController(view).navigate(
                        R.id.action_pendientesFragment_to_detalleFragment, bundle
                );
            }

            @Override
            public void onEliminarClick(PendienteFS pendiente) {
                viewModel.eliminarPendiente(pendiente);
            }
        });


        //Botón para explorar si la lista está vacía
        binding.btnExplorar.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_pendientesFragment_to_explorarFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}