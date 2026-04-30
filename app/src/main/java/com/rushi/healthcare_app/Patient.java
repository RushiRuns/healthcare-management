package com.rushi.healthcare_app;

public class Patient {
    private String id;
    private String name;
    private String allergies;
    private String conditions;
    private String medicalId;
    private String age;

    public Patient(String id, String name, String allergies, String conditions, String medicalId, String age) {
        this.id = id;
        this.name = name;
        this.allergies = allergies;
        this.conditions = conditions;
        this.medicalId = medicalId;
        this.age = age;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAllergies() { return allergies; }
    public String getConditions() { return conditions; }
    public String getMedicalId() { return medicalId; }
    public String getAge() { return age; }
}