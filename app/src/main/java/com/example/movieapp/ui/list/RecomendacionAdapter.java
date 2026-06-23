package com.example.movieapp.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.data.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class RecomendacionAdapter extends RecyclerView.Adapter<RecomendacionAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(Movie movie, String tipo);
    }

    private List<Movie> items = new ArrayList<>();
    private String tipo = "pelicula";
    private OnItemClickListener listener;

    public void setItems(List<Movie> items, String tipo) {
        this.items = items;
        this.tipo = tipo;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recomendacion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Movie m = items.get(position);
        //String titulo = m.getTitle() != null ? m.getTitle() : m.getTitle();
        h.tvTitulo.setText(m.getTitle());

        Glide.with(h.ivPoster.getContext())
                .load(m.getFullPosterPath())
                .placeholder(R.drawable.camaracine)
                .error(R.drawable.camaracine)
                .into(h.ivPoster);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(m, tipo);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitulo;

        ViewHolder(@NonNull View v) {
            super(v);
            ivPoster = v.findViewById(R.id.ivPosterRecomendacion);
            tvTitulo = v.findViewById(R.id.tvTituloRecomendacion);
        }
    }
}