package com.rushi.healthcare_app;

public class PatientResponse {
    private boolean success;
    private Patient data;

    public boolean isSuccess() { return success; }
    public Patient getData() { return data; }
}