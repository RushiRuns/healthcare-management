package com.rushi.healthcare_app;

import java.util.List;

public class AppointmentResponse {
    private boolean success;
    private String message;
    private List<Appointment> data;

    public boolean isSuccess() { return success; }
    public List<Appointment> getData() { return data; }
}