package com.example.movieapp.ui.detail;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.movieapp.R;
import com.example.movieapp.databinding.FragmentExplorarBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ExplorarFragment extends Fragment {

    private static final String KEY_SELECTED_TAB = "selected_tab";
    private FragmentExplorarBinding binding;
    private int selectedTabId = R.id.peliculasFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExplorarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavExplorar = binding.bottomNavigationViewExplorar;

        if (savedInstanceState != null) {
            selectedTabId = savedInstanceState.getInt(KEY_SELECTED_TAB, R.id.peliculasFragment);
        }

        bottomNavExplorar.setOnItemSelectedListener(item -> {
            selectedTabId = item.getItemId();
            mostrarFragmentSegunTab(selectedTabId);
            return true;
        });
        bottomNavExplorar.setSelectedItemId(selectedTabId);
        mostrarFragmentSegunTab(selectedTabId);
    }

    private void mostrarFragmentSegunTab(int itemId) {
        Fragment fragment;

        if (itemId == R.id.peliculasFragment) {
            fragment = new PeliculasFragment();
        } else if (itemId == R.id.seriesFragment) {
            fragment = new SeriesFragment();
        } else if (itemId == R.id.personasFragment) {
            fragment = new PersonasFragment();
        } else {
            return;
        }

        Fragment fragmentActual = getChildFragmentManager().findFragmentById(R.id.explorar_container);

        if (fragmentActual != null && fragmentActual.getClass().equals(fragment.getClass())) {
            return;
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.explorar_container, fragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_TAB, selectedTabId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}