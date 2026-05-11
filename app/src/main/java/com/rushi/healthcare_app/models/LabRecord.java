package com.rushi.healthcare_app.models;

import com.google.gson.annotations.SerializedName;

public class LabRecord {
    @SerializedName("lab_result_id")
    public String lab_result_id;

    @SerializedName("test_name")
    public String test_name;

    @SerializedName("test_date")
    public String test_date;

    @SerializedName("result_value")
    public String result_value;

    @SerializedName("unit")
    public String unit;

    @SerializedName("reference_range")
    public String reference_range;

    @SerializedName("status")
    public String status;

    @SerializedName("notes")
    public String notes;
}