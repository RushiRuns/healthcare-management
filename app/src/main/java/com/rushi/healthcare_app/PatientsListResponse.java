package com.rushi.healthcare_app;

import java.util.List;

public class PatientsListResponse {
    private boolean success;
    private List<Patient> data;

    public boolean isSuccess() { return success; }
    public List<Patient> getData() { return data; }
}