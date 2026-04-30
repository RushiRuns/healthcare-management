package com.rushi.healthcare_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PatientsActivity extends AppCompatActivity {

    private RecyclerView recyclerPatients;
    private PatientsAdapter adapter;
    private EditText editSearch;
    private List<Patient> dummyPatients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        recyclerPatients = findViewById(R.id.recyclerPatients);
        editSearch = findViewById(R.id.editSearch);

        setupDummyData();

        adapter = new PatientsAdapter(dummyPatients, patient -> {
            Intent intent = new Intent(PatientsActivity.this, PatientDetailActivity.class);
            // You can pass patient details here using intent.putExtra if needed
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
    }

    private void setupDummyData() {
        dummyPatients = new ArrayList<>();
        dummyPatients.add(new Patient("1", "Rahul Sharma", "Penicillin", "Hypertension", "PT-1001", "45 yrs"));
        dummyPatients.add(new Patient("2", "Priya Desai", "None", "Asthma", "PT-1002", "32 yrs"));
        dummyPatients.add(new Patient("3", "Amit Patel", "Peanuts", "Diabetes Type 2", "PT-1003", "50 yrs"));
        dummyPatients.add(new Patient("4", "Sneha Kapoor", "Sulfa Drugs", "None", "PT-1004", "28 yrs"));
    }
}