package com.rushi.healthcare_app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PatientDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Get patient details from intent
        String patientName = getIntent().getStringExtra("PATIENT_NAME");
        String patientId = getIntent().getStringExtra("PATIENT_ID");

        if (patientName != null) {
            getSupportActionBar().setTitle(patientName);
        }
        if (patientId == null) {
            patientId = "1"; // Fallback for testing
        }

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        PatientPagerAdapter adapter = new PatientPagerAdapter(this, patientId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Overview"); break;
                        case 1: tab.setText("Rx"); break;
                        case 2: tab.setText("Notes"); break;
                        case 3: tab.setText("Vitals"); break;
                    }
                }
        ).attach();
    }
}