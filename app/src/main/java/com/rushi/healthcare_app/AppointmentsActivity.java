package com.rushi.healthcare_app;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentsActivity extends AppCompatActivity {

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

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());

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
            Toast.makeText(this, "Navigating to Full Record...", Toast.LENGTH_SHORT).show();
        });

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
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
        dummyPatients.put("p1", new Patient("p1", "Sarah Connor", "Penicillin", "Hypertension"));
        dummyPatients.put("p2", new Patient("p2", "John Wick", "None", "Lacerations, Fatigue"));
        dummyPatients.put("p3", new Patient("p3", "Bruce Wayne", "Dust", "Insomnia, Multiple Fractures"));

        dummyAppointments = new ArrayList<>();
        dummyAppointments.add(new Appointment("a1", "p1", "Sarah Connor", "09:00 AM", "Done"));
        dummyAppointments.add(new Appointment("a2", "p2", "John Wick", "10:30 AM", "Wait"));
        dummyAppointments.add(new Appointment("a3", "p3", "Bruce Wayne", "01:00 PM", "Cancel"));
        dummyAppointments.add(new Appointment("a4", "p1", "Sarah Connor", "03:00 PM", "Wait"));
    }
}