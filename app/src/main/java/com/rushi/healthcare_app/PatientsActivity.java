package com.rushi.healthcare_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class PatientsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private RecyclerView recyclerPatients;
    private PatientsAdapter adapter;
    private EditText editSearch;
    private List<Patient> dummyPatients;

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

        toggle = new ActionBarDrawerToggle(this, drawerLayout, topAppBar, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
        editSearch = findViewById(R.id.editSearch);

        setupDummyData();

        adapter = new PatientsAdapter(dummyPatients, patient -> {
            Intent intent = new Intent(PatientsActivity.this, PatientDetailActivity.class);
            startActivity(intent);
        });

        recyclerPatients.setLayoutManager(new LinearLayoutManager(this));
        recyclerPatients.setAdapter(adapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
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

    private void setupDummyData() {
        dummyPatients = new ArrayList<>();
        dummyPatients.add(new Patient("1", "Rahul Sharma", "Penicillin", "Hypertension", "PT-1001", "45 yrs"));
        dummyPatients.add(new Patient("2", "Priya Desai", "None", "Asthma", "PT-1002", "32 yrs"));
        dummyPatients.add(new Patient("3", "Amit Patel", "Peanuts", "Diabetes Type 2", "PT-1003", "50 yrs"));
        dummyPatients.add(new Patient("4", "Sneha Kapoor", "Sulfa Drugs", "None", "PT-1004", "28 yrs"));
    }
}