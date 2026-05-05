package com.rushi.healthcare_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppointmentsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private RecyclerView recyclerView;
    private AppointmentsAdapter adapter;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    private TextView sheetPatientName;
    private TextView sheetAllergies;
    private TextView sheetConditions;
    private MaterialButton btnOpenFullRecord;
    private String currentSelectedPatientId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

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

        navigationView.setCheckedItem(R.id.nav_appointments);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                Intent intent = new Intent(AppointmentsActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_patients) {
                Intent intent = new Intent(AppointmentsActivity.this, PatientsActivity.class);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        LinearLayout btnSignOut = findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(v -> {
            Intent intent = new Intent(AppointmentsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        LinearLayout bottomSheetLayout = findViewById(R.id.bottomSheetLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        sheetPatientName = findViewById(R.id.sheetPatientName);
        sheetAllergies = findViewById(R.id.sheetAllergies);
        sheetConditions = findViewById(R.id.sheetConditions);
        btnOpenFullRecord = findViewById(R.id.btnOpenFullRecord);

        recyclerView = findViewById(R.id.recyclerViewAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        com.google.android.material.floatingactionbutton.FloatingActionButton fabAddAppointment = findViewById(R.id.fabAddAppointment);

        // Set the click listener to open the new Activity
        fabAddAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(AppointmentsActivity.this, AddAppointmentActivity.class);
            startActivity(intent);
        });


        // Trigger dynamic data fetch
        fetchAppointments();

        btnOpenFullRecord.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            Intent intent = new Intent(AppointmentsActivity.this, PatientDetailActivity.class);
            intent.putExtra("PATIENT_NAME", sheetPatientName.getText().toString());
            intent.putExtra("PATIENT_ID", currentSelectedPatientId);
            startActivity(intent);
        });

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void fetchAppointments() {
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getAppointments().enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Appointment> appointmentList = response.body().getData();
                    adapter = new AppointmentsAdapter(appointmentList, appointment -> {
                        showPatientDetails(appointment);
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    String errorMsg = "Server error";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += ": " + response.code();
                        } catch (Exception e) {}
                    }
                    Toast.makeText(AppointmentsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(AppointmentsActivity.this, "Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPatientDetails(Appointment appointment) {
        currentSelectedPatientId = appointment.getPatientId();
        // Show temporary loading state
        sheetPatientName.setText(appointment.getPatientName());
        sheetAllergies.setText("Loading...");
        sheetConditions.setText("Loading...");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        ApiService apiService = RetrofitClient.getApiService();

        apiService.getPatientDetails(appointment.getPatientId()).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                // Changed to check if getData() is not null (same as PatientDetailActivity)
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Patient patient = response.body().getData();
                    sheetPatientName.setText(patient.getName());
                    sheetAllergies.setText(patient.getAllergiesSummary());
                    sheetConditions.setText(patient.getConditionsSummary());
                } else {
                    sheetAllergies.setText("Data unavailable");
                    sheetConditions.setText("Data unavailable"); // Fixed stuck "Loading..." text

                    // Log the error to find out why it failed
                    Log.e("AppointmentsActivity", "Response Error Code: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("AppointmentsActivity", "Response Error Body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                sheetAllergies.setText("Connection failed");
                sheetConditions.setText("Connection failed"); // Fixed stuck "Loading..." text
                Log.e("AppointmentsActivity", "API Call Failed", t);
            }
        });
    }
}