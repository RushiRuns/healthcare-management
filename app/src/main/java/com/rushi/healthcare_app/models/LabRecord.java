package com.rushi.healthcare_app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LabRecord {
    @SerializedName("lab_result_id")
    public String lab_result_id;

    @SerializedName("test_name")
    public String test_name;

    @SerializedName("test_date")
    public String test_date;

    @SerializedName("image_paths")
    public List<String> image_paths;
}