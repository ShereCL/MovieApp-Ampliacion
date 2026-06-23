package com.example.movieapp.ui.detail;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;
import com.example.movieapp.databinding.FragmentPerfilBinding;
import com.example.movieapp.ui.list.PerfilPagerAdapter;
import com.example.movieapp.ui.viewModel.PerfilViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayoutMediator;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        // Cargar nombre y foto
        viewModel.getPerfil().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;
            if (data.containsKey("nombre"))
                binding.tvNombrePerfil.setText((String) data.get("nombre"));
            if (data.containsKey("bio")) binding.tvBioPerfil.setText((String) data.get("bio"));
            if (data.containsKey("fotoUrl")) {
                String fotoUrl = (String) data.get("fotoUrl");
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(fotoUrl)
                            .circleCrop()
                            .placeholder(R.drawable.directordecine)
                            .error(R.drawable.directordecine)
                            .into(binding.imgFotoPerfil);
                } else {
                    binding.imgFotoPerfil.setImageResource(R.drawable.directordecine);
                }
            } else {
                binding.imgFotoPerfil.setImageResource(R.drawable.directordecine);
            }

        });
        binding.tvBioPerfil.setOnClickListener(v -> mostrarDialogoEditarBio());
        binding.viewPagerPerfil.setOffscreenPageLimit(1);
        binding.viewPagerPerfil.setAdapter(new PerfilPagerAdapter(this));
        new TabLayoutMediator(binding.tabLayoutPerfil, binding.viewPagerPerfil, (tab, position) ->
                tab.setText(position == 0 ? getString(R.string.tabPerfil) : getString(R.string.tabEstadisticas))
        ).attach();
    }

    private void mostrarDialogoEditarBio() {
        EditText input = new EditText(getContext());
        input.setText(binding.tvBioPerfil.getText());
        input.setPadding(48, 24, 48, 24);

        new MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialog)
                .setTitle(getString(R.string.dialogoEditarBioTitulo))
                .setView(input)
                .setPositiveButton(getString(R.string.btnGuardar), (dialog, which) -> {
                    String nuevaBio = input.getText().toString();
                    binding.tvBioPerfil.setText(nuevaBio);
                    viewModel.guardarBio(nuevaBio);
                })
                .setNegativeButton(getString(R.string.btnCancelar), null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}