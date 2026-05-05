package com.rushi.healthcare_app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.HashMap;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.rushi.healthcare_app.models.PrescriptionResponse;
import com.rushi.healthcare_app.models.VitalSignResponse;
import com.rushi.healthcare_app.models.ConsultationNoteResponse;

public interface ApiService {
    @GET("appointments/index.php")
    Call<AppointmentResponse> getAppointments();

    @GET("patients/get.php")
    Call<PatientResponse> getPatientDetails(@Query("patient_id") String patientId);

    @GET("patients/index.php")
    Call<PatientsListResponse> getPatients();

    @POST("patients/index.php")
    Call<PatientResponse> createPatient(@Body HashMap<String, String> patientData);

    @POST("appointments/index.php")
    Call<AppointmentResponse> createAppointment(@Body HashMap<String, Object> appointmentData);

    @GET("prescriptions/index.php")
    Call<PrescriptionResponse> getPrescriptions(@Query("patient_id") String patientId);

    @GET("vitals/index.php")
    Call<VitalSignResponse> getVitals(@Query("patient_id") String patientId);

    @GET("notes/index.php")
    Call<ConsultationNoteResponse> getNotes(@Query("patient_id") String patientId);

    @POST("medical_history/add.php")
    Call<Void> addMedicalCondition(@Body HashMap<String, String> conditionData);

    @POST("patients/update_profile.php")
    Call<Void> updatePatientProfile(@Body HashMap<String, String> profileData);

    @POST("patients/add_allergy.php")
    Call<Void> addPatientAllergy(@Body HashMap<String, String> allergyData);

    @POST("medical_history/update.php")
    Call<Void> updateMedicalCondition(@Body HashMap<String, String> conditionData);

    @POST("medical_history/delete.php")
    Call<Void> deleteMedicalCondition(@Body HashMap<String, String> conditionData);

    @retrofit2.http.POST("prescriptions/add.php")
    retrofit2.Call<Void> addPrescription(@retrofit2.http.Body java.util.HashMap<String, String> data);

    @retrofit2.http.GET("prescriptions/history.php")
    retrofit2.Call<com.rushi.healthcare_app.models.PrescriptionResponse> getPastPrescriptions(@retrofit2.http.Query("patient_id") String patientId);

    @retrofit2.http.POST("prescriptions/update_status.php")
    retrofit2.Call<Void> updatePrescriptionStatus(@retrofit2.http.Body java.util.HashMap<String, String> data);

    @retrofit2.http.POST("prescriptions/update.php")
    retrofit2.Call<Void> updatePrescription(@retrofit2.http.Body java.util.HashMap<String, String> data);

    @retrofit2.http.POST("prescriptions/delete.php")
    retrofit2.Call<Void> deletePrescription(@retrofit2.http.Body java.util.HashMap<String, String> data);
}