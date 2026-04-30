package com.rushi.healthcare_app;

public class Appointment {
    private String id;
    private String patientId;
    private String patientName;
    private String time;
    private String status;

    public Appointment(String id, String patientId, String patientName, String time, String status) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.time = time;
        this.status = status;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
}