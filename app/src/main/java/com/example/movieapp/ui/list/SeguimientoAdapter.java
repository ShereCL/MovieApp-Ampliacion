package com.example.movieapp.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.data.model.SeguimientoFS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeguimientoAdapter extends RecyclerView.Adapter<SeguimientoAdapter.SeguimientoViewHolder> {

    private List<SeguimientoFS> seguimientos = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SeguimientoFS seguimiento);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSeguimiento(List<SeguimientoFS> seguimientos) {
        this.seguimientos = seguimientos;
        notifyDataSetChanged();
    }

    public SeguimientoFS getSeguimiento(int position) {
        return seguimientos.get(position);
    }

    @NonNull
    @Override
    public SeguimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seguimiento, parent, false);
        return new SeguimientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeguimientoViewHolder holder, int position) {
        holder.bind(seguimientos.get(position));
    }

    @Override
    public int getItemCount() {
        return seguimientos.size();
    }

    class SeguimientoViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitulo;
        private final TextView tvFecha;
        private final TextView tvPuntuacion;
        private final TextView tvTipo;
        private final ImageView ivPoster;
        private final RatingBar ratingBar;

        public SeguimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloSeguimiento);
            tvFecha = itemView.findViewById(R.id.tvFechaSeguimiento);
            tvPuntuacion = itemView.findViewById(R.id.tvPuntuacionSeguimiento);
            tvTipo = itemView.findViewById(R.id.tvTipoSeguimiento);
            ivPoster = itemView.findViewById(R.id.ivPosterSeguimiento);
            ratingBar = itemView.findViewById(R.id.ratingBarSeguimiento);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_ID) {
                    listener.onItemClick(seguimientos.get(position));
                }
            });
        }

        public void bind(SeguimientoFS seguimiento) {
            tvTitulo.setText(seguimiento.getTitulo());
            //Formateo de fecha
            String fechaRaw = seguimiento.getFechaVisualizacion();
            if (fechaRaw != null && fechaRaw.matches("\\d{4}-\\d{2}-\\d{2}")) {
                try {
                    SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tvFecha.setText(sdfOut.format(sdfIn.parse(fechaRaw)));
                } catch (ParseException e) {
                    tvFecha.setText(fechaRaw);
                }
            } else {
                tvFecha.setText(fechaRaw);
            }
            tvPuntuacion.setText(String.format("%.1f/5", seguimiento.getPuntuacion()));
            tvTipo.setText(seguimiento.getTipo().equalsIgnoreCase("pelicula") ? itemView.getContext().getString(R.string.tipoPelicula)
                    : itemView.getContext().getString(R.string.tipoSerie));
            ratingBar.setRating(seguimiento.getPuntuacion());

            Glide.with(itemView.getContext())
                    .load("https://image.tmdb.org/t/p/w500" + seguimiento.getPosterPath())
                    .placeholder(R.drawable.camaracine)
                    .error(R.drawable.camaracine)
                    .into(ivPoster);
        }
    }
}