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
import com.example.movieapp.data.model.PendienteFS;

import java.util.ArrayList;
import java.util.List;

public class PendienteAdapter extends RecyclerView.Adapter<PendienteAdapter.PendienteViewHolder> {

    private List<PendienteFS> pendientes = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PendienteFS pendiente);

        void onEliminarClick(PendienteFS pendiente);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setPendientes(List<PendienteFS> listaPendientes) {
        this.pendientes = listaPendientes;
        notifyDataSetChanged();
    }

    public PendienteFS getPendienteAt(int position) {
        return pendientes.get(position);
    }

    @NonNull
    @Override
    public PendienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pendiente, parent, false);
        return new PendienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendienteViewHolder holder, int position) {
        holder.bind(pendientes.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return pendientes.size();
    }

    static class PendienteViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPoster;
        TextView tvTitulo, tvTipo, tvSinopsis;

        public PendienteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvSinopsis = itemView.findViewById(R.id.tvSinopsis);
        }

        public void bind(PendienteFS pendiente, OnItemClickListener listener) {
            tvTitulo.setText(pendiente.getTitulo());
            tvTipo.setText(pendiente.getTipo());

            if (pendiente.getDescripcion() != null && !pendiente.getDescripcion().isEmpty()) {
                tvSinopsis.setText(pendiente.getDescripcion());
            } else {
                tvSinopsis.setText(R.string.sinDescripcion);
            }

            if (pendiente.getPosterPath() != null && !pendiente.getPosterPath().isEmpty()) {
                String url = "https://image.tmdb.org/t/p/w500" + pendiente.getPosterPath();
                Glide.with(ivPoster.getContext())
                        .load(url)
                        .placeholder(R.drawable.palomitasraton)
                        .into(ivPoster);
            } else {
                ivPoster.setImageResource(R.drawable.palomitasraton);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(pendiente);
            });
        }
    }
}
