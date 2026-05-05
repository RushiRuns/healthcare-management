package com.rushi.healthcare_app;

import com.google.gson.annotations.SerializedName;

public class MedicalHistory {
    @SerializedName("history_id")
    private String historyId;

    @SerializedName("condition_name")
    private String conditionName;

    @SerializedName("status")
    private String status;

    @SerializedName("diagnosis_date")
    private String diagnosisDate;

    public String getHistoryId() { return historyId; }
    public String getConditionName() { return conditionName; }
    public String getStatus() { return status; }
    public String getDiagnosisDate() { return diagnosisDate; }
}