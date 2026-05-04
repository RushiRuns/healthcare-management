package com.rushi.healthcare_app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PatientPagerAdapter extends FragmentStateAdapter {

    private String patientId;

    public PatientPagerAdapter(@NonNull FragmentActivity fragmentActivity, String patientId) {
        super(fragmentActivity);
        this.patientId = patientId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return PatientFragments.OverviewFragment.newInstance(patientId);
            case 1: return PatientFragments.RxFragment.newInstance(patientId);
            case 2: return PatientFragments.NotesFragment.newInstance(patientId);
            case 3: return PatientFragments.VitalsFragment.newInstance(patientId);
            default: return PatientFragments.OverviewFragment.newInstance(patientId);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}