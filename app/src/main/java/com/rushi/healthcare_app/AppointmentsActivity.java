package com.rushi.healthcare_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private List<Appointment> dummyAppointments;
    private Map<String, Patient> dummyPatients;

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

        toggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar, 0, 0);
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

        setupDummyData();

        adapter = new AppointmentsAdapter(dummyAppointments, appointment -> {
            showPatientDetails(appointment);
        });
        recyclerView.setAdapter(adapter);

        btnOpenFullRecord.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            Intent intent = new Intent(AppointmentsActivity.this, PatientDetailActivity.class);
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

    private void showPatientDetails(Appointment appointment) {
        Patient patient = dummyPatients.get(appointment.getPatientId());

        if (patient != null) {
            sheetPatientName.setText(patient.getName());
            sheetAllergies.setText(patient.getAllergies());
            sheetConditions.setText(patient.getConditions());
        } else {
            sheetPatientName.setText(appointment.getPatientName());
            sheetAllergies.setText("No data");
            sheetConditions.setText("No data");
        }

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void setupDummyData() {
        dummyPatients = new HashMap<>();
        dummyPatients.put("p1", new Patient("p1", "Sarah Connor", "Penicillin", "Hypertension", "PT-2001", "30 yrs"));
        dummyPatients.put("p2", new Patient("p2", "John Wick", "None", "Lacerations, Fatigue", "PT-2002", "42 yrs"));
        dummyPatients.put("p3", new Patient("p3", "Bruce Wayne", "Dust", "Insomnia, Multiple Fractures", "PT-2003", "38 yrs"));

        dummyAppointments = new ArrayList<>();
        dummyAppointments.add(new Appointment("a1", "p1", "Sarah Connor", "09:00 AM", "Done"));
        dummyAppointments.add(new Appointment("a2", "p2", "John Wick", "10:30 AM", "Wait"));
        dummyAppointments.add(new Appointment("a3", "p3", "Bruce Wayne", "01:00 PM", "Cancel"));
        dummyAppointments.add(new Appointment("a4", "p1", "Sarah Connor", "03:00 PM", "Wait"));
    }
}