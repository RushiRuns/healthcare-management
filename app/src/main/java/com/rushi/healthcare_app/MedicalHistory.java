package com.rushi.healthcare_app;

import com.google.gson.annotations.SerializedName;

public class MedicalHistory {
    @SerializedName("condition_name")
    private String conditionName;

    public String getConditionName() { return conditionName; }
}