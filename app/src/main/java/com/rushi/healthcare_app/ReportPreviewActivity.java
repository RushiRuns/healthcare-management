package com.rushi.healthcare_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.rushi.healthcare_app.models.ConsultationNoteResponse;
import com.rushi.healthcare_app.models.LabResponse;
import com.rushi.healthcare_app.models.PrescriptionResponse;
import com.rushi.healthcare_app.models.VitalSignResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportPreviewActivity extends AppCompatActivity {

    private String patientId;
    private TextView reportPatientName, reportPatientId, reportDemographics, reportBloodType, reportDate, reportContent;
    private StringBuilder documentBody = new StringBuilder();
    private LinearLayout layoutPrintableArea;
    private LinearLayout layoutLabImages; // New container for images
    private Patient currentPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_preview);

        patientId = getIntent().getStringExtra("PATIENT_ID");
        if (patientId == null) {
            Toast.makeText(this, "Error: No Patient ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI
        MaterialToolbar toolbar = findViewById(R.id.toolbarReport);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        layoutPrintableArea = findViewById(R.id.layoutPrintableArea);
        reportPatientName = findViewById(R.id.reportPatientName);
        reportPatientId = findViewById(R.id.reportPatientId);
        reportDemographics = findViewById(R.id.reportDemographics);
        reportBloodType = findViewById(R.id.reportBloodType);
        reportDate = findViewById(R.id.reportDate);
        reportContent = findViewById(R.id.reportContent);

        // Find or create the image container dynamically if missing from XML
        layoutLabImages = new LinearLayout(this);
        layoutLabImages.setOrientation(LinearLayout.VERTICAL);
        layoutLabImages.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layoutPrintableArea.addView(layoutLabImages);

        // Set Current Date
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        reportDate.setText("Generated: " + currentDate);

        // Setup Export Button (Trigger File Picker)
        findViewById(R.id.btnExportPdf).setOnClickListener(v -> {
            String fileName = "Patient_Report_" + patientId + "_" + System.currentTimeMillis() + ".pdf";
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, fileName);
            createPdfLauncher.launch(intent);
        });

        // Start Fetching Chain
        fetchPatientHeader();
    }

    private void fetchPatientHeader() {
        RetrofitClient.getApiService().getPatientDetails(patientId).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentPatient = response.body().getData();
                    reportPatientName.setText(currentPatient.getName());
                    reportPatientId.setText(currentPatient.getMedicalId());
                    reportDemographics.setText(currentPatient.getAge() + " / " + currentPatient.getGender());
                    reportBloodType.setText(currentPatient.getBloodType());

                    // Add Conditions to top of document
                    documentBody.append("--- ACTIVE CONDITIONS ---\n");
                    documentBody.append(currentPatient.getConditionsSummary()).append("\n\n");
                }
                fetchPrescriptions();
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                fetchPrescriptions();
            }
        });
    }

    private void fetchPrescriptions() {
        RetrofitClient.getApiService().getPrescriptions(patientId).enqueue(new Callback<PrescriptionResponse>() {
            @Override
            public void onResponse(Call<PrescriptionResponse> call, Response<PrescriptionResponse> response) {
                documentBody.append("--- ACTIVE PRESCRIPTIONS ---\n");
                if (response.isSuccessful() && response.body() != null && response.body().records != null && !response.body().records.isEmpty()) {
                    for (int i = 0; i < response.body().records.size(); i++) {
                        documentBody.append("• ").append(response.body().records.get(i).getMedicationName())
                                .append(" (").append(response.body().records.get(i).getDosage()).append(")\n");
                    }
                } else {
                    documentBody.append("No active prescriptions.\n");
                }
                fetchNotes();
            }

            @Override
            public void onFailure(Call<PrescriptionResponse> call, Throwable t) {
                fetchNotes();
            }
        });
    }

    private void fetchNotes() {
        RetrofitClient.getApiService().getNotes(patientId).enqueue(new Callback<ConsultationNoteResponse>() {
            @Override
            public void onResponse(Call<ConsultationNoteResponse> call, Response<ConsultationNoteResponse> response) {
                documentBody.append("\n--- CLINICAL NOTES ---\n");
                if (response.isSuccessful() && response.body() != null && response.body().records != null && !response.body().records.isEmpty()) {
                    for (int i = 0; i < response.body().records.size(); i++) {
                        com.rushi.healthcare_app.models.ConsultationNote note = response.body().records.get(i);
                        String diag = note != null && note.diagnosis != null ? note.diagnosis : "-";
                        documentBody.append("Diagnosis: ").append(diag).append("\n");
                    }
                } else {
                    documentBody.append("No clinical notes found.\n");
                }
                fetchVitals();
            }

            @Override
            public void onFailure(Call<ConsultationNoteResponse> call, Throwable t) {
                fetchVitals();
            }
        });
    }

    private void fetchVitals() {
        RetrofitClient.getApiService().getVitals(patientId).enqueue(new Callback<VitalSignResponse>() {
            @Override
            public void onResponse(Call<VitalSignResponse> call, Response<VitalSignResponse> response) {
                documentBody.append("\n--- RECENT VITALS ---\n");
                if (response.isSuccessful() && response.body() != null && response.body().records != null && !response.body().records.isEmpty()) {
                    com.rushi.healthcare_app.models.VitalSign vs = response.body().records.get(0);
                    String bp = (vs != null && vs.blood_pressure != null) ? vs.blood_pressure : "-";
                    String hr = (vs != null && vs.heart_rate != null) ? vs.heart_rate : "-";
                    documentBody.append("BP: ").append(bp).append(" | ");
                    documentBody.append("HR: ").append(hr).append("\n");
                } else {
                    documentBody.append("No vitals recorded.\n");
                }
                fetchLabs();
            }

            @Override
            public void onFailure(Call<VitalSignResponse> call, Throwable t) {
                fetchLabs();
            }
        });
    }

    private void fetchLabs() {
        RetrofitClient.getApiService().getLabs(patientId).enqueue(new Callback<LabResponse>() {
            @Override
            public void onResponse(Call<LabResponse> call, Response<LabResponse> response) {
                documentBody.append("\n--- LAB RESULTS ---\n");
                if (response.isSuccessful() && response.body() != null && response.body().records != null && !response.body().records.isEmpty()) {
                    for (int i = 0; i < response.body().records.size(); i++) {
                        documentBody.append("• ").append(response.body().records.get(i).test_name).append("\n");

                        // Load Images dynamically
                        if(response.body().records.get(i).image_paths != null) {
                            for(String imageUrl : response.body().records.get(i).image_paths) {
                                addImageToDocument(imageUrl, response.body().records.get(i).test_name);
                            }
                        }
                    }
                } else {
                    documentBody.append("No lab records found.\n");
                }

                reportContent.setText(documentBody.toString());
            }

            @Override
            public void onFailure(Call<LabResponse> call, Throwable t) {
                reportContent.setText(documentBody.toString());
            }
        });
    }

    private void addImageToDocument(String url, String testName) {
        // Dynamically get the base URL and remove "api/" to point to the root folder where images are saved
        String baseUrl = RetrofitClient.BASE_URL.replace("api/", "");
        String fullUrl = baseUrl + url;

        TextView imgLabel = new TextView(this);
        imgLabel.setText("Image: " + testName);
        imgLabel.setTextSize(12f);
        imgLabel.setTextColor(Color.parseColor("#64748B"));
        imgLabel.setPadding(0, 24, 0, 8);
        layoutLabImages.addView(imgLabel);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 800)); // Fixed height for PDF scaling
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        layoutLabImages.addView(imageView);

        // Download and set image
        Glide.with(this)
                .asBitmap()
                .load(fullUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }
                    @Override
                    public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {}
                });
    }

    // --- Task 2: Native PDF Picker Logic ---
    private final ActivityResultLauncher<Intent> createPdfLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        savePdfToUri(uri);
                    }
                }
            }
    );

    private void savePdfToUri(Uri uri) {
        try {
            PdfDocument document = new PdfDocument();

            // Allow layout to fully measure the newly added images
            layoutPrintableArea.measure(
                    View.MeasureSpec.makeMeasureSpec(layoutPrintableArea.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            layoutPrintableArea.layout(0, 0, layoutPrintableArea.getMeasuredWidth(), layoutPrintableArea.getMeasuredHeight());

            int width = layoutPrintableArea.getMeasuredWidth();
            int height = layoutPrintableArea.getMeasuredHeight();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            canvas.drawColor(Color.WHITE);
            layoutPrintableArea.draw(canvas);

            document.finishPage(page);

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                document.writeTo(outputStream);
                document.close();
                outputStream.close();
                Toast.makeText(this, "PDF Saved Successfully!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}