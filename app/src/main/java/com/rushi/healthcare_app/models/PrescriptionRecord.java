package com.rushi.healthcare_app.models;

import com.google.gson.annotations.SerializedName;

public class PrescriptionRecord {
    @SerializedName("prescription_id")
    private String prescriptionId;

    @SerializedName("medication_name")
    private String medicationName;

    @SerializedName("dosage")
    private String dosage;

    @SerializedName("frequency")
    private String frequency;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("status")
    private String status;

    public String getPrescriptionId() { return prescriptionId; }
    public String getMedicationName() { return medicationName; }
    public String getDosage() { return dosage; }
    public String getFrequency() { return frequency; }
    public String getStartDate() { return startDate; }
    public String getStatus() { return status; }
}