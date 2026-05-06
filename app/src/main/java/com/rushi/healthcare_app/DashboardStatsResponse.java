package com.rushi.healthcare_app;

import com.google.gson.annotations.SerializedName;

public class DashboardStatsResponse {
    private boolean success;

    @SerializedName("todays_appointments")
    private int todaysAppointments;

    @SerializedName("new_patients")
    private int newPatients;

    @SerializedName("pending_followups")
    private int pendingFollowups;

    public boolean isSuccess() {
        return success;
    }

    public int getTodaysAppointments() {
        return todaysAppointments;
    }

    public int getNewPatients() {
        return newPatients;
    }

    public int getPendingFollowups() {
        return pendingFollowups;
    }
}