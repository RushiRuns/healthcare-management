package com.rushi.healthcare_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAppointmentActivity extends AppCompatActivity {

    private AutoCompleteTextView searchPatientAuto;
    private TextInputEditText editDate, editTime, editReason;
    private MaterialButton btnSchedule;
    private SwitchCompat switchPayment;
    private TextView textPaymentStatus;
    private String selectedPatientId = null;
    private ApiService apiService;
    private List<Patient> searchResults = new ArrayList<>();

    private boolean isEditMode = false;
    private String editAppointmentId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        topAppBar.setNavigationOnClickListener(v -> finish());

        searchPatientAuto = findViewById(R.id.searchPatientAuto);
        searchPatientAuto.setThreshold(0);
        searchPatientAuto.setOnClickListener(v -> searchPatientAuto.showDropDown());
        searchPatientAuto.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) searchPatientAuto.showDropDown();
        });

        editDate = findViewById(R.id.editDate);
        editTime = findViewById(R.id.editTime);
        editReason = findViewById(R.id.editReason);
        btnSchedule = findViewById(R.id.btnSchedule);
        switchPayment = findViewById(R.id.switchPayment);
        textPaymentStatus = findViewById(R.id.textPaymentStatus);

        apiService = RetrofitClient.getApiService();

        setupPickers();
        loadPatientsForDropdown();

        // Payment gatekeeper logic (Logic + Visual State)
        btnSchedule.setEnabled(false);

        switchPayment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnSchedule.setEnabled(isChecked);
            if (isChecked) {
                textPaymentStatus.setText("Fee Collected");
                textPaymentStatus.setTextColor(Color.parseColor("#1A2535"));
                btnSchedule.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#185FA5")));
                btnSchedule.setTextColor(Color.WHITE);
            } else {
                textPaymentStatus.setText("Consultation Fee: Pending");
                textPaymentStatus.setTextColor(Color.parseColor("#64748B"));
                btnSchedule.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E2E8F0")));
                btnSchedule.setTextColor(Color.parseColor("#94A3B8"));
            }
        });

        isEditMode = getIntent().getBooleanExtra("IS_EDIT", false);
        if (isEditMode) {
            topAppBar.setTitle("Edit Appointment");
            btnSchedule.setText("Update Appointment");
            editAppointmentId = getIntent().getStringExtra("APPT_ID");
            selectedPatientId = getIntent().getStringExtra("PATIENT_ID");
            searchPatientAuto.setText(getIntent().getStringExtra("PATIENT_NAME"));

            String rawDateTime = getIntent().getStringExtra("DATETIME");
            if (rawDateTime != null && rawDateTime.length() >= 10) {
                editDate.setText(rawDateTime.substring(0, 10));
                if (rawDateTime.length() > 10) {
                    editTime.setText(rawDateTime.substring(11).trim());
                }
            }

            String reason = getIntent().getStringExtra("REASON");
            if (reason != null && !reason.equals("null")) {
                editReason.setText(reason);
            }
        }

        btnSchedule.setOnClickListener(v -> saveAppointment());
    }

    private void loadPatientsForDropdown() {
        apiService.getPatients().enqueue(new Callback<PatientsListResponse>() {
            @Override
            public void onResponse(Call<PatientsListResponse> call, Response<PatientsListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResults = response.body().getData();
                    List<String> displayNames = new ArrayList<>();

                    for (Patient p : searchResults) {
                        displayNames.add(p.getName() + " (" + p.getMedicalId() + ")");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddAppointmentActivity.this,
                            R.layout.item_dropdown, displayNames);
                    searchPatientAuto.setAdapter(adapter);

                    searchPatientAuto.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedText = (String) parent.getItemAtPosition(position);
                        for (Patient p : searchResults) {
                            if ((p.getName() + " (" + p.getMedicalId() + ")").equals(selectedText)) {
                                selectedPatientId = p.getId();
                                searchPatientAuto.setText(p.getName());
                                break;
                            }
                        }
                    });
                }
            }
            @Override public void onFailure(Call<PatientsListResponse> call, Throwable t) {}
        });
    }

    private void setupPickers() {
        editDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog, (view, y, m, d) ->
                    editDate.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        editTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog, (view, h, m) ->
                    editTime.setText(String.format(Locale.US, "%02d:%02d:00", h, m)),
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }

    private void saveAppointment() {
        if (selectedPatientId == null) {
            Toast.makeText(this, "Please select a patient", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("patient_id", selectedPatientId);
        map.put("appointment_date", editDate.getText().toString() + " " + editTime.getText().toString());
        map.put("reason_for_visit", editReason.getText().toString());

        map.put("payment_status", true);
        map.put("transaction_id", "MANUAL_CASH");

        String patientName = searchPatientAuto.getText().toString();
        String dateStr = editDate.getText().toString();
        String timeStr = editTime.getText().toString();

        if (isEditMode) {
            map.put("appointment_id", editAppointmentId);
            apiService.updateAppointment(map).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        scheduleNotification(dateStr, timeStr, patientName);
                        Toast.makeText(AddAppointmentActivity.this, "Appointment Updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddAppointmentActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<Void> call, Throwable t) {}
            });
        } else {
            map.put("doctor_id", 1);
            map.put("status", "scheduled");
            map.put("duration_minutes", 30);

            apiService.createAppointment(map).enqueue(new Callback<AppointmentResponse>() {
                @Override
                public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                    if (response.isSuccessful()) {
                        scheduleNotification(dateStr, timeStr, patientName);
                        Toast.makeText(AddAppointmentActivity.this, "Appointment Scheduled!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                @Override public void onFailure(Call<AppointmentResponse> call, Throwable t) {}
            });
        }
    }

    private void scheduleNotification(String date, String time, String patientName) {
        try {
            // KEEPING THE 10-SECOND TEST ACTIVE
            long triggerTime = System.currentTimeMillis() + 10000;

            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
            android.content.Intent intent = new android.content.Intent(this, NotificationReceiver.class);
            intent.putExtra("patient_name", patientName);
            intent.putExtra("time", time);

            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
            );

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    // FALLBACK: If exact alarms are denied by Android, force a standard alarm
                    alarmManager.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }

            android.widget.Toast.makeText(this, "Alarm set for 10 seconds from now", android.widget.Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}