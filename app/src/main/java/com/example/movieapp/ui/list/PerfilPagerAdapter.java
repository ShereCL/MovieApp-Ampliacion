package com.example.movieapp.ui.list;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.movieapp.ui.detail.EstadisticasFragment;
import com.example.movieapp.ui.detail.TabPerfilFragment;

public class PerfilPagerAdapter extends FragmentStateAdapter {

    public PerfilPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TabPerfilFragment();
            case 1:
                return new EstadisticasFragment();
            default:
                return new TabPerfilFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
