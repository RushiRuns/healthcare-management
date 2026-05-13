package com.rushi.healthcare_app;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private RecyclerView recyclerPatients;
    private PatientsAdapter adapter;
    private EditText editSearch;

    private LinearLayout layoutFilterOptions;
    private TextView textFilterActive, textFilterInactive, textFilterAll;
    private ImageView btnToolbarFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        setSupportActionBar(topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View sidebar = (View) navigationView.getParent();
        android.view.ViewGroup.LayoutParams params = sidebar.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7f);
        sidebar.setLayoutParams(params);

        navigationView.setCheckedItem(R.id.nav_patients);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                Intent intent = new Intent(PatientsActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_appointments) {
                Intent intent = new Intent(PatientsActivity.this, AppointmentsActivity.class);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        LinearLayout btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> {
            Intent intent = new Intent(PatientsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        recyclerPatients = findViewById(R.id.recyclerPatients);
        recyclerPatients.setLayoutManager(new LinearLayoutManager(this));
        editSearch = findViewById(R.id.editSearch);

        btnToolbarFilter = findViewById(R.id.btnToolbarFilter);
        layoutFilterOptions = findViewById(R.id.layoutFilterOptions);
        textFilterActive = findViewById(R.id.textFilterActive);
        textFilterInactive = findViewById(R.id.textFilterInactive);
        textFilterAll = findViewById(R.id.textFilterAll);

        com.google.android.material.floatingactionbutton.FloatingActionButton fabAddPatient = findViewById(R.id.fabAddPatient);
        fabAddPatient.setOnClickListener(v -> {
            Intent intent = new Intent(PatientsActivity.this, AddPatientActivity.class);
            startActivity(intent);
        });

        setupFilterLogic();
        fetchPatients();

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filterText(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void setupFilterLogic() {
        btnToolbarFilter.setOnClickListener(v -> {
            if (layoutFilterOptions.getVisibility() == View.VISIBLE) {
                layoutFilterOptions.setVisibility(View.GONE);
                btnToolbarFilter.setColorFilter(Color.parseColor("#64748B"));
            } else {
                layoutFilterOptions.setVisibility(View.VISIBLE);
                btnToolbarFilter.setColorFilter(Color.parseColor("#1A2535"));
            }
        });

        textFilterActive.setOnClickListener(v -> updateFilterUI(textFilterActive, "Active"));
        textFilterInactive.setOnClickListener(v -> updateFilterUI(textFilterInactive, "Inactive"));
        textFilterAll.setOnClickListener(v -> updateFilterUI(textFilterAll, "All"));
    }

    private void updateFilterUI(TextView selectedText, String status) {
        // Reset all to unselected state
        textFilterActive.setTextColor(Color.parseColor("#1A2535"));
        textFilterActive.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        textFilterActive.setBackgroundResource(R.drawable.bg_filter_unselected);

        textFilterInactive.setTextColor(Color.parseColor("#1A2535"));
        textFilterInactive.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        textFilterInactive.setBackgroundResource(R.drawable.bg_filter_unselected);

        textFilterAll.setTextColor(Color.parseColor("#1A2535"));
        textFilterAll.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        textFilterAll.setBackgroundResource(R.drawable.bg_filter_unselected);

        // Highlight the selected state
        selectedText.setTextColor(Color.parseColor("#FFFFFF"));
        selectedText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        selectedText.setBackgroundResource(R.drawable.bg_filter_selected);

        if (adapter != null) {
            adapter.filterStatus(status);
        }
    }

    private void fetchPatients() {
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getPatients().enqueue(new Callback<PatientsListResponse>() {
            @Override
            public void onResponse(Call<PatientsListResponse> call, Response<PatientsListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Patient> patients = response.body().getData();
                    adapter = new PatientsAdapter(patients, patient -> {
                        Intent intent = new Intent(PatientsActivity.this, PatientDetailActivity.class);
                        intent.putExtra("PATIENT_ID", patient.getId());
                        startActivity(intent);
                    });
                    recyclerPatients.setAdapter(adapter);
                } else {
                    String error = "Failed to load patients";
                    if (response.code() != 200) error += " (Error: " + response.code() + ")";
                    Toast.makeText(PatientsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PatientsListResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(PatientsActivity.this, "Failed to load patients", Toast.LENGTH_SHORT).show();
            }
        });
    }
}