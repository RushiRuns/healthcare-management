package com.rushi.healthcare_app;

public class Patient {
    private String id;
    private String name;
    private String allergies;
    private String conditions;

    public Patient(String id, String name, String allergies, String conditions) {
        this.id = id;
        this.name = name;
        this.allergies = allergies;
        this.conditions = conditions;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAllergies() { return allergies; }
    public String getConditions() { return conditions; }
}