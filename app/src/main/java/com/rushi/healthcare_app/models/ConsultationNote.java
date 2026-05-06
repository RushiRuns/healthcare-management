package com.rushi.healthcare_app.models;

import com.google.gson.annotations.SerializedName;

public class ConsultationNote {

    @SerializedName("note_id")
    public String id;

    @SerializedName("patient_id")
    public String patient_id;

    @SerializedName("created_at")
    public String consultation_date;

    @SerializedName("symptoms")
    public String symptoms;

    @SerializedName("observations")
    public String observations;

    @SerializedName("diagnosis")
    public String diagnosis;

    @SerializedName("treatment_plan")
    public String plan;

    @SerializedName("follow_up_required")
    public String follow_up_required;

    @SerializedName("follow_up_days")
    public String follow_up_days;

    @SerializedName("follow_up_status")
    public String follow_up_status;
}