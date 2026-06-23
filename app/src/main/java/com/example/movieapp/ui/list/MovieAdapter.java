package com.example.movieapp.ui.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.data.model.Movie;
import com.example.movieapp.data.network.NetworkUtils;
import com.example.movieapp.data.repository.MovieRepository;
import com.example.movieapp.databinding.ItemMovieBinding;
import com.gold24park.popcornview.popcornview.PopcornShape;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies = new ArrayList<>();
    private List<Integer> pendingMoviesId = new ArrayList<>();
    private OnItemClickListener listener;

    //Interfaz que maneja los clicks
    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    //Esta interfaz maneja el click del boton de añadir a paendientes

    public interface OnAddPendingClickListener {
        void onAddPendingClick(Movie movie);
    }

    private OnAddPendingClickListener addPendingListener;

    //Setter para el listener del boton de añadir pendientes
    public void setAddPendingClickListener(OnAddPendingClickListener listener) {
        this.addPendingListener = listener;
    }

    //Constructor para  el adapter
    public MovieAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    //Actualizar la lista de peliculas

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    //metodo que actualiza los ids de pendientes
    public void updatePendingIds(List<Integer> ids) {
        this.pendingMoviesId.clear();
        this.pendingMoviesId.addAll(ids);
        notifyDataSetChanged();
    }

    //metodo para verificar si la peli ya está en pendientes
    private boolean isMoviePending(int movieId) {
        return pendingMoviesId.contains(movieId);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MovieViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    //ViewHolder

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ItemMovieBinding binding; // Binding que vincula con item_movie.xml  (tarjetitas individuales)


        public MovieViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        //metodo para cargar las imagenes desde la api si hay wifi y si no mostrar imagen por defecto

        private void loadImgConected(Movie movie) {
            Context context = binding.imgPoster.getContext();
            SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
            boolean wifiOnly = prefs.getBoolean("wifi_only", false);

            if (movie.getFullPosterPath() != null) {
                if (wifiOnly) {
                    // Solo cargar si hay wifi
                    if (NetworkUtils.isWifiConnected(context)) {
                        Glide.with(context)
                                .load(movie.getFullPosterPath())
                                .placeholder(R.drawable.camaracine)
                                .error(R.drawable.camaracine)
                                .into(binding.imgPoster);
                    } else {
                        binding.imgPoster.setImageResource(R.drawable.camaracine);
                    }
                } else {
                    //con wifi
                    if (NetworkUtils.isNetworkAvailable(context)) {
                        Glide.with(context)
                                .load(movie.getFullPosterPath())
                                .placeholder(R.drawable.camaracine)
                                .error(R.drawable.camaracine)
                                .into(binding.imgPoster);
                    } else {
                        //sin wifi
                        binding.imgPoster.setImageResource(R.drawable.camaracine);
                    }
                }
            } else {
                binding.imgPoster.setImageResource(R.drawable.camaracine);
            }
        }


        public void bind(final Movie movie) {
            Context context = binding.getRoot().getContext();
            binding.tvTitle.setText(movie.getTitle());

            if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                List<String> genreNames = new ArrayList<>();
                for (Integer id : movie.getGenres()) {
                    Integer resId = MovieRepository.GENRES_MAP.get(id);
                    if (resId != null) genreNames.add(context.getString(resId));
                }
                if (!genreNames.isEmpty()) {
                    binding.tvGenre.setText(String.join(" | ", genreNames));
                } else {
                    binding.tvGenre.setText(context.getString(R.string.sinGenero));

                }
            } else {
                binding.tvGenre.setText(context.getString(R.string.sinGenero));
            }

            binding.tvReleaseDate.setText(movie.getReleaseDate());
            binding.tvVoteAverage.setText(String.format("⭐ %.1f/10", movie.getVoteAverage()));            binding.tvOverview.setText(movie.getOverview());

            loadImgConected(movie);
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(movie);
            });
            updateButtonState(isMoviePending(movie.getId()));

            binding.btnAddPending.setOnClickListener(v -> {

                pendingMoviesId.add(movie.getId());
                updateButtonState(true);
                // Si NO está en pendientes, ejecutar la animación
                if (addPendingListener != null) {
                    addPendingListener.onAddPendingClick(movie);
                }

                PopcornShape.DrawableShape shape = new PopcornShape.DrawableShape(
                        100f,
                        ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.popcorn)
                );

                /*Con todo esto obtengo las coordenadas del botón (Es de una librería externa
                 * me lo ha programado la IA para que salga así*/
                int[] location = new int[2];
                binding.btnAddPending.getLocationInWindow(location);
                float startX = location[0] + (binding.btnAddPending.getWidth() / 2f);
                float startY = location[1] + (binding.btnAddPending.getWidth() / 2f);

                binding.popcornView.setVisibility(View.VISIBLE);
                binding.popcornView.start(shape, 30, 1, startX, startY);
                binding.popcornView.setGravity(0.5f);
                binding.popcornView.setMinVelocity(25);
                binding.popcornView.setMaxVelocity(60);
                binding.popcornView.setAngleRangeStart(45);
                binding.popcornView.setAngleRangeEnd(135);
                binding.popcornView.setElasticity(0.6f);
                binding.popcornView.setFriction(0.39f);

                binding.popcornView.postDelayed(() -> {
                    binding.popcornView.setVisibility(View.GONE);
                }, 2000);
            });
        }

        //metodo para actualizar el estado visual del button de añadir
        private void updateButtonState(boolean isPending) {
            if (isPending) {
                binding.btnAddPending.setVisibility(View.INVISIBLE);
                binding.btnAddPending.setEnabled(false);
                binding.tvPendingStatus.setVisibility(View.VISIBLE);
            } else {
                binding.btnAddPending.setVisibility(View.VISIBLE);
                binding.btnAddPending.setImageResource(R.drawable.btn_add);
                binding.btnAddPending.setAlpha(1.0f);
                binding.btnAddPending.setEnabled(true);
                binding.tvPendingStatus.setVisibility(View.GONE);
            }
        }
    }
}

