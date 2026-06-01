package com.rushi.healthcare_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import com.rushi.healthcare_app.databinding.ActivityDashboardBinding;
import java.util.Calendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        toggle.syncState();

        // Set sidebar width to 70% of screen width
        android.view.View sidebar = (android.view.View) binding.navigationView.getParent();
        android.view.ViewGroup.LayoutParams params = sidebar.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7f);
        sidebar.setLayoutParams(params);

        binding.navigationView.setCheckedItem(R.id.nav_dashboard);

        binding.btnSignOut.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        binding.btnQuickAddPatient.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddPatientActivity.class);
            startActivity(intent);
        });

        binding.btnQuickAddAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddAppointmentActivity.class);
            startActivity(intent);
        });

        binding.btnViewSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AppointmentsActivity.class);
            intent.putExtra("FILTER_TODAY", true);
            startActivity(intent);
        });

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_appointments) {
                Intent intent = new Intent(DashboardActivity.this, AppointmentsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_patients) {
                Intent intent = new Intent(DashboardActivity.this, PatientsActivity.class);
                startActivity(intent);
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        updateGreetingAndName();
        fetchDashboardStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGreetingAndName();
        fetchDashboardStats();
    }

    private void updateGreetingAndName() {
        // Handle Time-based Greeting
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Good Morning,";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = "Good Afternoon,";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            greeting = "Good Evening,";
        } else {
            greeting = "Good Night,";
        }
        binding.tvGreetingTime.setText(greeting);

        // Handle Personalized Doctor Name (Fixed Keys)
        SharedPreferences prefs = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String firstName = prefs.getString("user_first_name", "");
        String lastName = prefs.getString("user_last_name", "");
        String qualifications = prefs.getString("user_qualifications", "");

        if (firstName.isEmpty() && lastName.isEmpty()) {
            binding.tvDoctorName.setText("Doctor");
        } else {
            String fullName = "Dr. " + firstName + " " + lastName;
            if (!qualifications.isEmpty()) {
                fullName += ", " + qualifications;
            }
            binding.tvDoctorName.setText(fullName);
        }
    }

    private void fetchDashboardStats() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<DashboardStatsResponse> call = apiService.getDashboardStats();

        call.enqueue(new Callback<DashboardStatsResponse>() {
            @Override
            public void onResponse(Call<DashboardStatsResponse> call, Response<DashboardStatsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    binding.tvTodaysAppointments.setText(String.valueOf(response.body().getTodaysAppointments()));
                    binding.tvNewPatients.setText(String.valueOf(response.body().getNewPatients()));
                    binding.tvPendingFollowUps.setText(String.valueOf(response.body().getPendingFollowups()));
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load dashboard statistics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardStatsResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}