package com.example.movieapp.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.data.model.Person;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    public interface OnPersonClickListener {
        void onPersonClick(Person person);
    }

    private List<Person> personasOriginal = new ArrayList<>();
    private List<Person> personasFiltradas = new ArrayList<>();

    private final OnPersonClickListener listener;

    //estado de filtros
    private String filtroDepartamento = null; //esto es igual a todos
    private int ordenActual = ORDEN_RELEVANCIA;

    public static final int ORDEN_RELEVANCIA = 0;
    public static final int ORDEN_AZ = 1;
    public static final int ORDEN_ZA = 2;

    public PersonAdapter(OnPersonClickListener listener) {
        this.listener = listener;
    }

    public void setPersonas(List<Person> personas) {
        this.personasOriginal = personas != null ? new ArrayList<>(personas) : new ArrayList<>();
        aplicarFiltros();
    }

    public void setFiltroDepartamento(String departamento) {
        this.filtroDepartamento = departamento;
        aplicarFiltros();
    }

    public void setOrden(int orden) {
        this.ordenActual = orden;
        aplicarFiltros();
    }

    //reseteo de fitros
    public void limpiarFiltros() {
        this.filtroDepartamento = null;
        this.ordenActual = ORDEN_RELEVANCIA;
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        List<Person> resultado = new ArrayList<>();

        for (Person p : personasOriginal) {
            if (filtroDepartamento == null) {
                resultado.add(p);
            } else {
                String dept = p.getKnownForDepartment();
                if (dept != null && dept.equalsIgnoreCase(filtroDepartamento)) {
                    resultado.add(p);
                }
            }
        }

        switch (ordenActual) {
            case ORDEN_AZ:
                Collections.sort(resultado, (a, b) -> {
                    String na = a.getName() != null ? a.getName() : "";
                    String nb = b.getName() != null ? b.getName() : "";
                    return na.compareToIgnoreCase(nb);
                });
                break;
            case ORDEN_ZA:
                Collections.sort(resultado, (a, b) -> {
                    String na = a.getName() != null ? a.getName() : "";
                    String nb = b.getName() != null ? b.getName() : "";
                    return nb.compareToIgnoreCase(na);
                });
                break;
            case ORDEN_RELEVANCIA:
            default:
                break;
        }

        personasFiltradas = resultado;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_persona, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        holder.bind(personasFiltradas.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return personasFiltradas.size();
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgPerfil;
        private final TextView tvNombre;
        private final TextView tvKnownFor;
        private final Chip chipDepartamento;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPerfil = itemView.findViewById(R.id.imgPerfil);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvKnownFor = itemView.findViewById(R.id.tvKnownFor);
            chipDepartamento = itemView.findViewById(R.id.chipDepartamento);
        }

        public void bind(Person persona, OnPersonClickListener listener) {
            tvNombre.setText(persona.getName());

            //chip para saber el tipo de persona que es
            String dept = persona.getKnownForDepartment();
            if (dept != null && !dept.isEmpty()) {
                chipDepartamento.setText(traducirDepartamento(itemView.getContext(), dept));
                chipDepartamento.setVisibility(View.VISIBLE);
            } else {
                chipDepartamento.setVisibility(View.GONE);
            }

            //obras conocidas
            if (persona.getKnownFor() != null && !persona.getKnownFor().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                int max = Math.min(3, persona.getKnownFor().size());
                for (int i = 0; i < max; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(persona.getKnownFor().get(i).getDisplayTitle());
                }
                tvKnownFor.setText(sb.toString());
                tvKnownFor.setVisibility(View.VISIBLE);
            } else {
                tvKnownFor.setVisibility(View.GONE);
            }

            Glide.with(itemView.getContext())
                    .load(persona.getFullProfileUrl())
                    .placeholder(R.drawable.claqueta)
                    .error(R.drawable.claqueta)
                    .centerCrop()
                    .into(imgPerfil);

            itemView.setOnClickListener(v -> listener.onPersonClick(persona));
        }

        private String traducirDepartamento(Context context, String dept) {
            if (dept == null) return "";
            switch (dept) {
                case "Acting":
                    return context.getString(R.string.deptActing);
                case "Directing":
                    return context.getString(R.string.deptDirecting);
                case "Writing":
                    return context.getString(R.string.deptWriting);
                case "Production":
                    return context.getString(R.string.deptProduction);
                case "Editing":
                    return context.getString(R.string.deptEditing);
                case "Sound":
                    return context.getString(R.string.deptSound);
                case "Camera":
                    return context.getString(R.string.deptCamera);
                case "Art":
                    return context.getString(R.string.deptArt);
                default:
                    return dept;
            }
        }
    }
}