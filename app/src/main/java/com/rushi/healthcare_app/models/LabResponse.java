package com.rushi.healthcare_app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LabResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("records")
    public List<LabRecord> records;
}