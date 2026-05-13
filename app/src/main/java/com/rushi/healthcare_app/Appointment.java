package com.rushi.healthcare_app;

import com.google.gson.annotations.SerializedName;

public class Appointment {
    @SerializedName("appointment_id")
    private String id;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("patient_name")
    private String patientName;

    @SerializedName("appointment_date")
    private String time;

    @SerializedName("reason_for_visit")
    private String reasonForVisit;

    private String status;

    public Appointment(String id, String patientId, String patientName, String time, String reasonForVisit, String status) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.time = time;
        this.reasonForVisit = reasonForVisit;
        this.status = status;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getTime() { return time; }
    public String getReasonForVisit() { return reasonForVisit; }
    public String getStatus() { return status; }
}