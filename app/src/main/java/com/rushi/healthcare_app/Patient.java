package com.rushi.healthcare_app;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Patient {
    @SerializedName("patient_id")
    private String id;

    @SerializedName("medical_id")
    private String medicalId;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("date_of_birth")
    private String dob;

    @SerializedName("gender")
    private String gender;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    private List<Allergy> allergies;

    @SerializedName("medical_history")
    private List<MedicalHistory> medicalHistory;

    public String getId() { return id; }
    public String getMedicalId() { return medicalId; }
    public String getName() { return firstName + " " + lastName; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public String getAllergiesSummary() {
        if (allergies == null || allergies.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for (Allergy a : allergies) sb.append(a.getAllergen()).append(", ");
        return sb.substring(0, sb.length() - 2);
    }

    public String getConditionsSummary() {
        if (medicalHistory == null || medicalHistory.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for (MedicalHistory h : medicalHistory) sb.append(h.getConditionName()).append(", ");
        return sb.substring(0, sb.length() - 2);
    }
}