package com.example.movieapp.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.data.model.FavoritoPersonaFS;
import com.example.movieapp.data.model.Person;
import com.example.movieapp.data.repository.FirestoreFavoritosPersonaRepository;
import com.example.movieapp.data.repository.TmdbRepository;
import com.example.movieapp.data.util.CustomToast;
import com.example.movieapp.databinding.FragmentDetallePersonaBinding;
import com.example.movieapp.ui.list.ObrasPersonaAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class DetallePersonaFragment extends Fragment {

    private FragmentDetallePersonaBinding binding;
    private TmdbRepository tmdbRepository;
    private final MutableLiveData<Person> personaLiveData = new MutableLiveData<>();
    private FirestoreFavoritosPersonaRepository favoritosPersonaRepository;
    private boolean esFavorito = false;
    private Person personaActual;
    private boolean bioExpandida = false; //controlo la bio con un booleano

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetallePersonaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tmdbRepository = new TmdbRepository();
        favoritosPersonaRepository = new FirestoreFavoritosPersonaRepository();

        int personId;
        if (getArguments() != null) {
            personId = getArguments().getInt("personId", 0);
        } else {
            personId = 0;
        }

        if (personId == 0) {
            Navigation.findNavController(view).popBackStack();
            return;
        }

        tmdbRepository.obtenerDetallePersona(personId, personaLiveData);

        personaLiveData.observe(getViewLifecycleOwner(), persona -> {
            if (persona == null) return;
            rellenarUI(persona, view, personId);
        });
    }

    private void comprobarFavorito(int personId) {
        favoritosPersonaRepository.esFavorito(personId, esFav -> {
            esFavorito = esFav;
            actualizarBoton();
        });
    }
    private void actualizarBoton() {
        if (esFavorito) {
            binding.tvTextoFavorito.setText(getString(R.string.enFavoritos));
            binding.iconFavorito.setImageResource(R.drawable.ic_heart_filled);
        } else {
            binding.tvTextoFavorito.setText(getString(R.string.guardarEnFav));
            binding.iconFavorito.setImageResource(R.drawable.ic_heart_outline);
        }
    }

    private void toggleFavorito(int personId) {
        if (esFavorito) {
            favoritosPersonaRepository.eliminar(personId, new FirestoreFavoritosPersonaRepository.Callback() {
                @Override
                public void onSuccess() {
                    esFavorito = false;
                    requireActivity().runOnUiThread(() -> {
                        actualizarBoton();
                        CustomToast.show(getContext(), getString(R.string.eliminadoFavoritos), CustomToast.Type.SUCCESS);                    });
                }

                @Override
                public void onError(String error) {
                    CustomToast.show(getContext(), getString(R.string.error) + error, CustomToast.Type.ERROR);
                }
            });
        } else {
            if (personaActual == null) return;
            FavoritoPersonaFS favorito = new FavoritoPersonaFS(
                    personaActual.getId(),
                    personaActual.getName(),
                    personaActual.getFullProfileUrl(),
                    personaActual.getKnownForDepartment(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid()
            );
            favoritosPersonaRepository.guardar(favorito, new FirestoreFavoritosPersonaRepository.Callback() {
                @Override
                public void onSuccess() {
                    esFavorito = true;
                    requireActivity().runOnUiThread(() -> {
                        actualizarBoton();
                        CustomToast.show(getContext(), getString(R.string.guardadoFavoritos), CustomToast.Type.SUCCESS);                    });
                }

                @Override
                public void onError(String error) {
                    CustomToast.show(getContext(), getString(R.string.error) + error, CustomToast.Type.ERROR);
                }
            });
        }
    }

    private void rellenarUI(Person persona, View view, int personId) {

        //Foto
        String fotoUrl = persona.getFullProfileUrl();
        if (fotoUrl != null) {
            Glide.with(this).load(fotoUrl).placeholder(R.drawable.directordecine).into(binding.imgFotoPersona);
        } else {
            binding.imgFotoPersona.setImageResource(R.drawable.directordecine);
        }

        //Nombre
        binding.tvNombrePersona.setText(persona.getName());

        //Departamento
        binding.tvDepartamento.setText(persona.getKnownForDepartment() != null ? persona.getKnownForDepartment() : "");

        //Fecha de nacimiento
        if (persona.getBirthday() != null && !persona.getBirthday().isEmpty()) {
            binding.tvFechaNacimiento.setText(persona.getBirthday());
        } else {
            binding.tvFechaNacimiento.setVisibility(View.GONE);
        }

        //Lugar de nacimiento
        if (persona.getPlaceOfBirth() != null && !persona.getPlaceOfBirth().isEmpty()) {
            binding.tvLugarNacimiento.setText(persona.getPlaceOfBirth());
        } else {
            binding.tvLugarNacimiento.setVisibility(View.GONE);
        }

        //Biografía
        String bio = persona.getBiography();
        if (bio != null && !bio.isEmpty()) {
            binding.tvBiografia.setText(bio);

            //muestra el leer mas si la bio ocupa mas de 4 lineas
            binding.tvBiografia.post(() -> {
                if (binding.tvBiografia.getLineCount() > 4) {
                    binding.tvBiografia.setMaxLines(4);
                    binding.tvLeerMas.setVisibility(View.VISIBLE);
                    binding.tvLeerMas.setText(getString(R.string.leerMas));
                }
            });

            //toggle expandir/colapsar la bio
            binding.tvLeerMas.setOnClickListener(v -> {
                bioExpandida = !bioExpandida;
                if (bioExpandida) {
                    binding.tvBiografia.setMaxLines(Integer.MAX_VALUE);
                    binding.tvLeerMas.setText(getString(R.string.leerMenos));
                } else {
                    binding.tvBiografia.setMaxLines(4);
                    binding.tvLeerMas.setText(getString(R.string.leerMas));
                }
            });
        } else {
            binding.tvBiografia.setText(getString(R.string.sinBiografia));
        }

        //obras conocidas
        if (persona.getCombinedCredits() != null
                && persona.getCombinedCredits().getCast() != null
                && !persona.getCombinedCredits().getCast().isEmpty()) {

            List<Person.KnownForItem> cast = persona.getCombinedCredits().getCast();

            ObrasPersonaAdapter adapter = new ObrasPersonaAdapter(cast, obra -> {
                Bundle args = new Bundle();
                args.putInt("movie_id", obra.getId());
                boolean isTv = "tv".equals(obra.getMediaType());
                args.putBoolean("id_tv_show", isTv);
                Navigation.findNavController(view).navigate(
                        R.id.action_detallePersonaFragment_to_detalleFragment, args);
            });

            binding.rvObrasPersona.setLayoutManager(
                    new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.rvObrasPersona.setAdapter(adapter);

        } else {
            binding.rvObrasPersona.setVisibility(View.GONE);
        }

        binding.btnFavoritoPersona.setOnClickListener(v -> toggleFavorito(personId));

        personaActual = persona;
        comprobarFavorito(personId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}