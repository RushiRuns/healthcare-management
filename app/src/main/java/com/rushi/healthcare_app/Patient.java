package com.rushi.healthcare_app;

import com.google.gson.annotations.SerializedName;
import java.util.Calendar;
import java.util.List;

public class Patient {
    @SerializedName("patient_id")
    private int id;

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

    @SerializedName("blood_type")
    private String bloodType;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("allergies")
    private String allergies;

    @SerializedName("medical_history")
    private List<MedicalHistory> medicalHistory;

    public String getId() { return String.valueOf(id); }
    public String getMedicalId() { return medicalId; }
    public String getName() { return firstName + " " + lastName; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getBloodType() { return bloodType != null ? bloodType : "N/A"; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public List<MedicalHistory> getMedicalHistory() { return medicalHistory; }

    public String getAge() {
        if (dob == null || dob.length() < 4) return "";
        try {
            int birthYear = Integer.parseInt(dob.substring(0, 4));
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            return (currentYear - birthYear) + "y";
        } catch (Exception e) { return ""; }
    }

    public String getAllergiesSummary() {
        if (allergies == null || allergies.trim().isEmpty()) return "None";
        return allergies;
    }

    public String getConditionsSummary() {
        if (medicalHistory == null || medicalHistory.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for (MedicalHistory h : medicalHistory) sb.append(h.getConditionName()).append(", ");
        return sb.substring(0, sb.length() - 2);
    }
}