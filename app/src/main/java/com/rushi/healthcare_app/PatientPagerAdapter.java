package com.rushi.healthcare_app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PatientPagerAdapter extends FragmentStateAdapter {

    public PatientPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new PatientFragments.OverviewFragment();
            case 1: return new PatientFragments.RxFragment();
            case 2: return new PatientFragments.NotesFragment();
            case 3: return new PatientFragments.VitalsFragment();
            default: return new PatientFragments.OverviewFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}