package com.rushi.healthcare_app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.HashMap;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @GET("appointments/index.php")
    Call<AppointmentResponse> getAppointments();

    @GET("patients/get.php")
    Call<PatientResponse> getPatientDetails(@Query("patient_id") String patientId);

    @GET("patients/index.php")
    Call<PatientsListResponse> getPatients();

    @POST("patients/index.php")
    Call<PatientResponse> createPatient(@Body HashMap<String, String> patientData);
}