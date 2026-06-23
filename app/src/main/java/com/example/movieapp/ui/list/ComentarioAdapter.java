package com.example.movieapp.ui.list;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.movieapp.R;
import com.example.movieapp.data.model.Comentario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ViewHolder> {

    private List<Comentario> lista = new ArrayList<>();

    public void setComentarios(List<Comentario> nuevos) {
        lista = nuevos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comentario, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comentario c = lista.get(position);

        holder.tvNombre.setText(c.getNombreUsuario());
        holder.tvMensaje.setText(c.getMensaje());

        if (c.getFecha() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvFecha.setText(sdf.format(c.getFecha().toDate()));
        }

        //foto o iniciales aunque siempre existirá la foto
        String fotoUrl = c.getFotoUrl();
        String nombre = c.getNombreUsuario();
        String inicial = (nombre != null && !nombre.isEmpty())
                ? String.valueOf(nombre.charAt(0)).toUpperCase() : "?";

        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            holder.tvInicial.setVisibility(View.GONE);
            holder.ivAvatar.setVisibility(View.VISIBLE);
            Glide.with(holder.ivAvatar.getContext())
                    .load(fotoUrl)
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.bg_avatar_circle)
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setVisibility(View.GONE);
            holder.tvInicial.setVisibility(View.VISIBLE);
            holder.tvInicial.setText(inicial);
        }
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvMensaje, tvFecha, tvInicial;
        ImageView ivAvatar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre  = itemView.findViewById(R.id.tvNombreComentario);
            tvMensaje = itemView.findViewById(R.id.tvMensajeComentario);
            tvFecha   = itemView.findViewById(R.id.tvFechaComentario);
            tvInicial = itemView.findViewById(R.id.tvInicialComentario);
            ivAvatar  = itemView.findViewById(R.id.ivAvatarComentario);
        }
    }
}