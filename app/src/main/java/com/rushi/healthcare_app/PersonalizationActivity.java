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
    private EditText etQualifications;
    private EditText etHospitalName;
    private EditText etPhone;
    private EditText etAddress;
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
        etQualifications = findViewById(R.id.etQualifications);
        etHospitalName = findViewById(R.id.etHospitalName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        btnNextStep = findViewById(R.id.btnNextStep);
        btnCompleteSetup = findViewById(R.id.btnCompleteSetup);

        btnNextStep.setOnClickListener(v -> {
            layoutWelcomeState.setVisibility(View.GONE);
            layoutNameInputState.setVisibility(View.VISIBLE);
        });

        btnCompleteSetup.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String qualifications = etQualifications.getText().toString().trim();
            String hospitalName = etHospitalName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || qualifications.isEmpty() ||
                    hospitalName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(PersonalizationActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int currentVersionCode = 1;
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    currentVersionCode = (int) pInfo.getLongVersionCode();
                } else {
                    currentVersionCode = pInfo.versionCode;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            SharedPreferences preferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user_first_name", firstName);
            editor.putString("user_last_name", lastName);
            editor.putString("user_qualifications", qualifications);
            editor.putString("user_hospital_name", hospitalName);
            editor.putString("user_phone", phone);
            editor.putString("user_address", address);
            editor.putInt("last_personalized_version", currentVersionCode);
            editor.apply();

            Intent intent = new Intent(PersonalizationActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }
}