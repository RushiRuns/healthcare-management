package com.rushi.healthcare_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // Filtering variables
    private List<Appointment> masterAppointmentList = new ArrayList<>();
    private EditText editSearchAppointment;
    private TextView filterAll, filterToday, filterWeek, filterMonth;
    private String currentActiveFilter = "All";
    private String currentSearchQuery = "";

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

        android.view.View sidebar = (android.view.View) navigationView.getParent();
        android.view.ViewGroup.LayoutParams params = sidebar.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7f);
        sidebar.setLayoutParams(params);

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

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        sheetPatientName = findViewById(R.id.sheetPatientName);
        sheetAllergies = findViewById(R.id.sheetAllergies);
        sheetConditions = findViewById(R.id.sheetConditions);
        btnOpenFullRecord = findViewById(R.id.btnOpenFullRecord);

        recyclerView = findViewById(R.id.recyclerViewAppointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty list to prevent crash
        adapter = new AppointmentsAdapter(new ArrayList<>(), appointment -> showPatientDetails(appointment));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAddAppointment).setOnClickListener(v -> {
            startActivity(new Intent(AppointmentsActivity.this, AddAppointmentActivity.class));
        });

        btnOpenFullRecord.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            Intent intent = new Intent(AppointmentsActivity.this, PatientDetailActivity.class);
            intent.putExtra("PATIENT_NAME", sheetPatientName.getText().toString());
            intent.putExtra("PATIENT_ID", currentSelectedPatientId);
            startActivity(intent);
        });

        // Setup Filtering UI
        setupSearchAndFilters();

        fetchAppointments();

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

    private void setupSearchAndFilters() {
        editSearchAppointment = findViewById(R.id.editSearchAppointment);
        filterAll = findViewById(R.id.filterAll);
        filterToday = findViewById(R.id.filterToday);
        filterWeek = findViewById(R.id.filterWeek);
        filterMonth = findViewById(R.id.filterMonth);

        LinearLayout filterLayout = findViewById(R.id.filterLayout);
        android.widget.ImageView btnToolbarFilter = findViewById(R.id.btnToolbarFilter);

        btnToolbarFilter.setOnClickListener(v -> {
            if (filterLayout.getVisibility() == android.view.View.VISIBLE) {
                filterLayout.setVisibility(android.view.View.GONE);
            } else {
                filterLayout.setVisibility(android.view.View.VISIBLE);
            }
        });

        editSearchAppointment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterAll.setOnClickListener(v -> {
            currentActiveFilter = "All";
            updateFilterUI(filterAll);
            applyFilters();
        });

        filterToday.setOnClickListener(v -> {
            currentActiveFilter = "Today";
            updateFilterUI(filterToday);
            applyFilters();
        });

        filterWeek.setOnClickListener(v -> {
            currentActiveFilter = "This Week";
            updateFilterUI(filterWeek);
            applyFilters();
        });

        filterMonth.setOnClickListener(v -> {
            currentActiveFilter = "This Month";
            updateFilterUI(filterMonth);
            applyFilters();
        });
    }

    private void updateFilterUI(TextView activeTextView) {
        TextView[] filters = {filterAll, filterToday, filterWeek, filterMonth};
        for (TextView tv : filters) {
            if (tv == activeTextView) {
                tv.setTextColor(Color.parseColor("#FFFFFF"));
                tv.setBackgroundResource(R.drawable.bg_filter_selected);
            } else {
                tv.setTextColor(Color.parseColor("#1A2535"));
                tv.setBackgroundResource(R.drawable.bg_filter_unselected);
            }
        }
    }

    private void applyFilters() {
        List<Appointment> filteredList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayString = sdf.format(new Date());

        Calendar now = Calendar.getInstance();

        for (Appointment appt : masterAppointmentList) {
            // Search Text Condition
            boolean matchesSearch = appt.getPatientName().toLowerCase().contains(currentSearchQuery);
            if (!matchesSearch) continue;

            // Date Condition
            boolean matchesDate = false;
            String apptDateRaw = appt.getTime(); // Assuming "yyyy-MM-dd HH:mm:ss"
            if (apptDateRaw == null || apptDateRaw.length() < 10) continue;
            String apptDateStr = apptDateRaw.substring(0, 10);

            if (currentActiveFilter.equals("All")) {
                matchesDate = true;
            } else if (currentActiveFilter.equals("Today")) {
                matchesDate = apptDateStr.equals(todayString);
            } else {
                try {
                    Date apptDate = sdf.parse(apptDateStr);
                    Calendar apptCal = Calendar.getInstance();
                    if (apptDate != null) apptCal.setTime(apptDate);

                    if (currentActiveFilter.equals("This Week")) {
                        matchesDate = (apptCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                                apptCal.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR));
                    } else if (currentActiveFilter.equals("This Month")) {
                        matchesDate = (apptCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                                apptCal.get(Calendar.MONTH) == now.get(Calendar.MONTH));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (matchesDate) {
                filteredList.add(appt);
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }

    private void fetchAppointments() {
        ApiService apiService = RetrofitClient.getApiService();

        apiService.getAppointments().enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    masterAppointmentList = response.body().getData();
                    applyFilters(); // Populates list and applies default 'All' state
                } else {
                    String errorMsg = "Server error";
                    if (response.errorBody() != null) {
                        try { errorMsg += ": " + response.code(); } catch (Exception e) {}
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
        sheetPatientName.setText(appointment.getPatientName());
        sheetAllergies.setText("Loading...");
        sheetConditions.setText("Loading...");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        ApiService apiService = RetrofitClient.getApiService();

        apiService.getPatientDetails(appointment.getPatientId()).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Patient patient = response.body().getData();
                    sheetPatientName.setText(patient.getName());
                    sheetAllergies.setText(patient.getAllergiesSummary());
                    sheetConditions.setText(patient.getConditionsSummary());
                } else {
                    sheetAllergies.setText("Data unavailable");
                    sheetConditions.setText("Data unavailable");
                    Log.e("AppointmentsActivity", "Response Error Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                sheetAllergies.setText("Connection failed");
                sheetConditions.setText("Connection failed");
                Log.e("AppointmentsActivity", "API Call Failed", t);
            }
        });
    }
}