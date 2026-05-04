package com.rushi.healthcare_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetailActivity extends AppCompatActivity {

    private String patientId;
    private TextView tvAvatar, tvPatientId, tvDemographics, tvBloodType, tvPhone, tvAllergies;
    private ImageButton btnEditProfile, btnAddAllergy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvAvatar = findViewById(R.id.avatar);
        tvPatientId = findViewById(R.id.patientId);
        tvDemographics = findViewById(R.id.patientDemographics);
        tvBloodType = findViewById(R.id.bloodType);
        tvPhone = findViewById(R.id.patientPhone);
        tvAllergies = findViewById(R.id.patientAllergies);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnAddAllergy = findViewById(R.id.btnAddAllergy);

        patientId = getIntent().getStringExtra("PATIENT_ID");
        if (patientId == null) patientId = "1";

        btnEditProfile.setOnClickListener(v -> showEditProfileBottomSheet());
        btnAddAllergy.setOnClickListener(v -> showAddAllergyBottomSheet());

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        PatientPagerAdapter adapter = new PatientPagerAdapter(this, patientId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Overview"); break;
                case 1: tab.setText("Rx"); break;
                case 2: tab.setText("Notes"); break;
                case 3: tab.setText("Vitals"); break;
            }
        }).attach();

        fetchPatientHeader();
    }

    private void showEditProfileBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_edit_profile, null);
        dialog.setContentView(view);

        EditText etPhone = view.findViewById(R.id.inputEditPhone);
        EditText etBlood = view.findViewById(R.id.inputEditBloodType);
        Button btnSave = view.findViewById(R.id.btnSaveProfile);

        // Pre-fill with current data
        etPhone.setText(tvPhone.getText().toString());
        etBlood.setText(tvBloodType.getText().toString());

        btnSave.setOnClickListener(v -> {
            HashMap<String, String> data = new HashMap<>();
            data.put("patient_id", patientId);
            data.put("phone", etPhone.getText().toString());
            data.put("blood_type", etBlood.getText().toString());

            RetrofitClient.getApiService().updatePatientProfile(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        fetchPatientHeader();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(PatientDetailActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

    private void showAddAllergyBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_add_allergy, null);
        dialog.setContentView(view);

        EditText etAllergy = view.findViewById(R.id.inputAllergyName);
        Button btnSave = view.findViewById(R.id.btnSaveAllergy);

        btnSave.setOnClickListener(v -> {
            HashMap<String, String> data = new HashMap<>();
            data.put("patient_id", patientId);
            data.put("allergy", etAllergy.getText().toString());

            RetrofitClient.getApiService().addPatientAllergy(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        fetchPatientHeader();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(PatientDetailActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

    private void fetchPatientHeader() {
        RetrofitClient.getApiService().getPatientDetails(patientId).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Patient p = response.body().getData();
                    if (p != null) {
                        if (p.getName() != null && !p.getName().isEmpty()) {
                            tvAvatar.setText(p.getName().substring(0, 1).toUpperCase());
                            if (getSupportActionBar() != null) getSupportActionBar().setTitle(p.getName());
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
                Toast.makeText(PatientDetailActivity.this, "Header error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}