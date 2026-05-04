package com.rushi.healthcare_app;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetailActivity extends AppCompatActivity {

    private String patientId;

    private TextView tvAvatar, tvPatientId, tvDemographics, tvBloodType, tvPhone, tvAllergies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize header views
        tvAvatar = findViewById(R.id.avatar);
        tvPatientId = findViewById(R.id.patientId);
        tvDemographics = findViewById(R.id.patientDemographics);
        tvBloodType = findViewById(R.id.bloodType);
        tvPhone = findViewById(R.id.patientPhone);
        tvAllergies = findViewById(R.id.patientAllergies);

        patientId = getIntent().getStringExtra("PATIENT_ID");
        if (patientId == null) patientId = "1"; // Fallback

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

        fetchPatientHeader();
    }

    private void fetchPatientHeader() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getPatientDetails(patientId).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Patient p = response.body().getData();
                    if (p != null) {
                        if (p.getName() != null && !p.getName().isEmpty()) {
                            tvAvatar.setText(p.getName().substring(0, 1).toUpperCase());
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(p.getName());
                            }
                        }
                        tvPatientId.setText(p.getMedicalId());
                        tvDemographics.setText(p.getAge() + " • " + p.getGender());
                        tvBloodType.setText(p.getBloodType());
                        tvPhone.setText(p.getPhone());
                        tvAllergies.setText(p.getAllergiesSummary());
                    }
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                Toast.makeText(PatientDetailActivity.this, "Failed to load header", Toast.LENGTH_SHORT).show();
            }
        });
    }
}