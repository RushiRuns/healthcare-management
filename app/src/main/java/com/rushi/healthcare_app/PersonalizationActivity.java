package com.rushi.healthcare_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PersonalizationActivity extends AppCompatActivity {

    private ConstraintLayout layoutWelcomeState;
    private ConstraintLayout layoutNameInputState;
    private EditText etFirstName;
    private EditText etLastName;
    private Button btnNextStep;
    private Button btnCompleteSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalization);

        layoutWelcomeState = findViewById(R.id.layoutWelcomeState);
        layoutNameInputState = findViewById(R.id.layoutNameInputState);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnNextStep = findViewById(R.id.btnNextStep);
        btnCompleteSetup = findViewById(R.id.btnCompleteSetup);

        btnNextStep.setOnClickListener(v -> {
            layoutWelcomeState.setVisibility(View.GONE);
            layoutNameInputState.setVisibility(View.VISIBLE);
        });

        btnCompleteSetup.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(PersonalizationActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Route to Dashboard after Personalization is complete
            Intent intent = new Intent(PersonalizationActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }
}