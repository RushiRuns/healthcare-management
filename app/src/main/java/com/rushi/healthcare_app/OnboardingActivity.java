package com.rushi.healthcare_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rushi.healthcare_app.adapters.OnboardingAdapter;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private MaterialButton btnAction;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        btnAction = findViewById(R.id.btnAction);

        setupOnboardingItems();

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adapter.getItemCount() - 1) {
                    btnAction.setText("Get Started");
                } else {
                    btnAction.setText("Next");
                }
            }
        });

        btnAction.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                completeOnboarding();
            }
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(R.drawable.ic_onboard_calendar, "Manage Appointments", "Effortlessly schedule, filter, and track daily patient visits in one place."));
        items.add(new OnboardingItem(R.drawable.ic_onboard_records, "Complete Patient Records", "Access full medical histories, vitals, and clinical notes instantly."));
        items.add(new OnboardingItem(R.drawable.ic_onboard_rx, "Digital Prescriptions", "Create, edit, and manage dynamic medication workflows seamlessly."));
        adapter = new OnboardingAdapter(items);
    }

    private void completeOnboarding() {
        // Route to Login instead of Personalization
        startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
        finish();
    }
}