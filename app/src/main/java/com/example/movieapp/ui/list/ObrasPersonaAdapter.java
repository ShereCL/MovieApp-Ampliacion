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
import com.example.movieapp.data.model.Person.KnownForItem;

import java.util.List;

public class ObrasPersonaAdapter extends RecyclerView.Adapter<ObrasPersonaAdapter.ObraViewHolder> {

    public interface OnObraClickListener {
        void onObraClick(KnownForItem obra);
    }

    private final List<KnownForItem> obras;
    private final OnObraClickListener listener;

    public ObrasPersonaAdapter(List<KnownForItem> obras, OnObraClickListener listener) {
        this.obras = obras;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ObraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_obra_persona, parent, false);
        return new ObraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObraViewHolder holder, int position) {
        holder.bind(obras.get(position));
    }

    @Override
    public int getItemCount() {
        return obras != null ? obras.size() : 0;
    }

    class ObraViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgPoster;
        private final TextView tvTitulo;
        private final TextView tvAnio;

        public ObraViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPosterObra);
            tvTitulo = itemView.findViewById(R.id.tvTituloObra);
            tvAnio = itemView.findViewById(R.id.tvAnioObra);
        }

        public void bind(KnownForItem obra) {
            tvTitulo.setText(obra.getDisplayTitle());
            tvAnio.setText(obra.getYear());

            if (obra.getFullPosterUrl() != null) {
                Glide.with(itemView.getContext())
                        .load(obra.getFullPosterUrl())
                        .placeholder(R.drawable.ic_add_foto)
                        .into(imgPoster);
            } else {
                imgPoster.setImageResource(R.drawable.ic_add_foto);
            }

            itemView.setOnClickListener(v -> listener.onObraClick(obra));
        }
    }
}