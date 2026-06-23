package com.example.movieapp.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.data.model.Movie;
import com.example.movieapp.data.model.Video;
import com.example.movieapp.data.network.NetworkUtils;
import com.example.movieapp.data.repository.MovieRepository;
import com.example.movieapp.data.util.CustomToast;
import com.example.movieapp.databinding.FragmentDetalleBinding;
import com.example.movieapp.ui.list.ComentarioAdapter;
import com.example.movieapp.ui.viewModel.ComentarioViewModel;
import com.example.movieapp.ui.viewModel.DetalleViewModel;

import java.util.ArrayList;
import java.util.List;

public class DetalleFragment extends Fragment {

    private static final String TAG = "DetalleFragment";
    private static final String ARG_MOVIE_ID = "movie_id";
    private static final String ARG_IS_TV_SHOW = "id_tv_show";

    private FragmentDetalleBinding binding;
    private DetalleViewModel viewModel;

    private int movieId;
    private boolean isTvShow;

    private Movie movie;

    public static DetalleFragment newInstance(int movieId, boolean isTvShow) {
        DetalleFragment fragment = new DetalleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        args.putBoolean(ARG_IS_TV_SHOW, isTvShow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt(ARG_MOVIE_ID, 0);
            isTvShow = getArguments().getBoolean(ARG_IS_TV_SHOW, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetalleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(DetalleViewModel.class);

        //Cargar detalles completos desde la api
        loadMovieDetails();

        //Comentarios
        ComentarioViewModel comentarioViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(ComentarioViewModel.class);

        ComentarioAdapter comentarioAdapter = new ComentarioAdapter();
        binding.rvComentarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvComentarios.setAdapter(comentarioAdapter);

        comentarioViewModel.getComentarios(movieId).observe(getViewLifecycleOwner(), comentarios -> {

            comentarioAdapter.setComentarios(comentarios);

            //mostrar recuento de comentarios
            binding.tvComentariosTitulo.setText(getString(R.string.comentariosTitulo, comentarios.size()));

            //mostrar mensaje si no hay comentarios
            if (comentarios.isEmpty()) {
                binding.tvSinComentarios.setVisibility(View.VISIBLE);
            } else {
                binding.tvSinComentarios.setVisibility(View.GONE);
            }
        });

        binding.btnEnviarComentario.setOnClickListener(v -> {
            String texto = binding.etComentario.getText().toString().trim();
            if (!texto.isEmpty()) {
                comentarioViewModel.publicar(movieId, texto);
                binding.etComentario.setText("");
            }
        });
    }

    private void loadMovieDetails() {
        // Mostrar loading
        binding.progressBar.setVisibility(View.VISIBLE);

        viewModel.getMovieDetails(movieId, isTvShow).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    if (resource.data != null) {
                        movie = resource.data;
                        displayMovieInfo(movie);
                    }
                    break;

                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    //Toast.makeText(getContext(), "Error al cargar detalles", Toast.LENGTH_SHORT).show();
                    // Si falla muestro la info que ya tengo del objeto
                    if (getArguments() != null) {
                        Movie movieFromArgs = (Movie) getArguments().getSerializable("movie_obj");
                        if (movieFromArgs != null) {
                            displayMovieInfo(movieFromArgs);
                        }
                    }
                    break;
            }
        });

        // Cargar videos
        loadVideos();
    }

    private void displayMovieInfo(Movie movie) {
        binding.tvTitle.setText(movie.getTitle());
        binding.tvOverview.setText(movie.getOverview());
        binding.tvReleaseDate.setText(getString(R.string.labelFecha, movie.getReleaseDate()));
        binding.tvVoteAverage.setText(getString(R.string.labelPuntuacion, movie.getVoteAverage()));
        //géneros
        if (movie.getGenreObjects() != null && !movie.getGenreObjects().isEmpty()) {
            //si viene del endpoint detalle, me devuelve objetos
            List<String> genreNames = new ArrayList<>();
            for (Movie.Genre g : movie.getGenreObjects()) {
                genreNames.add(g.getName());
            }
            binding.tvGenres.setText(String.join(" | ", genreNames));

        } else if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
            //si viene del listado lo mapeao
            List<String> genreNames = new ArrayList<>();
            for (Integer id : movie.getGenres()) {
                Integer resId = MovieRepository.GENRES_MAP.get(id);
                if (resId != null) genreNames.add(getString(resId));
                ;
            }
            binding.tvGenres.setText(!genreNames.isEmpty() ? String.join(" · ", genreNames) : getString(R.string.sinGenero));

        } else {
            binding.tvGenres.setText(getString(R.string.sinGenero));
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean wifiOnly = prefs.getBoolean("wifi_only", false);

        boolean shouldLoadImages = wifiOnly ? NetworkUtils.isWifiConnected(requireContext()) : NetworkUtils.isNetworkAvailable(requireContext());

        if (movie.getFullBackdropPath() != null && shouldLoadImages) {
            Glide.with(this).load(movie.getFullBackdropPath()).placeholder(R.drawable.camaracine).error(R.drawable.camaracine).into(binding.imgBackdrop);
        } else {
            binding.imgBackdrop.setImageResource(R.drawable.palomitasraton);
        }

        // Poster
        if (movie.getFullPosterPath() != null && shouldLoadImages) {
            Glide.with(this).load(movie.getFullPosterPath()).placeholder(R.drawable.camaracine).error(R.drawable.camaracine).into(binding.imgPoster);
        } else {
            binding.imgPoster.setImageResource(R.drawable.palomitasraton);
        }
    }

    //Cargar los trailers
    private void loadVideos() {
        viewModel.getVideos(movieId, isTvShow).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.btnTrailer.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    if (resource.data != null && resource.data.getResults() != null) {
                        List<Video> videos = resource.data.getResults();
                        Video trailer = findTrailer(videos);
                        if (trailer != null) {
                            setupTrailerButton(trailer);
                        } else {
                            binding.btnTrailer.setVisibility(View.GONE);
                        }
                    } else {
                        binding.btnTrailer.setVisibility(View.GONE);
                    }
                    break;

                case ERROR:
                    binding.btnTrailer.setVisibility(View.GONE);
                    break;
            }
        });
    }

    //SACO EL VIDEO DE YOUTUBE
    private Video findTrailer(List<Video> videos) {
        for (Video v : videos) {
            if (v.isYouTubeTrailer()) {
                return v;
            }
        }
        return null;
    }

    private void setupTrailerButton(Video trailer) {
        binding.btnTrailer.setVisibility(View.VISIBLE);
        binding.btnTrailer.setText(R.string.verTrailer);
        binding.btnTrailer.setOnClickListener(v -> {
            String url = trailer.getYouTubeUrl();
            if (url != null) {
                openYouTube(url);
            }
        });
    }

    //Abrir el video en youTube
    public void openYouTube(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            CustomToast.show(getContext(), getString(R.string.errorAbrirTrailer), CustomToast.Type.ERROR);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}