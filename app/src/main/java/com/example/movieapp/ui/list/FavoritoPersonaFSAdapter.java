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
import com.example.movieapp.data.model.FavoritoPersonaFS;

import java.util.List;

public class FavoritoPersonaFSAdapter extends RecyclerView.Adapter<FavoritoPersonaFSAdapter.FavoritoViewHolder> {
    private List<FavoritoPersonaFS> listaFavoritos;
    private final OnFavoritoClickListener listener;

    public interface OnFavoritoClickListener {
        void onFavoritoClick(int personaId);
    }

    public FavoritoPersonaFSAdapter(List<FavoritoPersonaFS> lista, OnFavoritoClickListener listener) {
        this.listaFavoritos = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_persona_favorita, parent, false);
        return new FavoritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritoViewHolder holder, int position) {
        holder.bind(listaFavoritos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return listaFavoritos.size();
    }

    static class FavoritoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPerfil;
        private final TextView tvNombre;

        public FavoritoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPerfil = itemView.findViewById(R.id.imgPerfil);
            tvNombre = itemView.findViewById(R.id.tvNombre);
        }

        public void bind(FavoritoPersonaFS fav, OnFavoritoClickListener listener) {
            tvNombre.setText(fav.getNombre());
            Glide.with(itemView.getContext()).load(fav.getFotoUrl()).placeholder(R.drawable.claqueta).into(imgPerfil);

            itemView.setOnClickListener(v -> listener.onFavoritoClick(fav.getPersonaId()));
        }
    }

}
