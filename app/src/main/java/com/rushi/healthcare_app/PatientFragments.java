package com.rushi.healthcare_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.util.Log;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rushi.healthcare_app.adapters.NotesAdapter;
import com.rushi.healthcare_app.adapters.RxAdapter;
import com.rushi.healthcare_app.adapters.VitalsAdapter;
import com.rushi.healthcare_app.models.ConsultationNoteResponse;
import com.rushi.healthcare_app.models.PrescriptionResponse;
import com.rushi.healthcare_app.models.VitalSignResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientFragments {

    public static class OverviewFragment extends Fragment {
        private String patientId;
        private RecyclerView recyclerView;
        private com.rushi.healthcare_app.adapters.HistoryAdapter adapter;

        public static OverviewFragment newInstance(String patientId) {
            OverviewFragment fragment = new OverviewFragment();
            Bundle args = new Bundle();
            args.putString("PATIENT_ID", patientId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                patientId = getArguments().getString("PATIENT_ID");
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_overview, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.d("PatientFrag", "OverviewFragment created with patientId: " + patientId);
            recyclerView = view.findViewById(R.id.recyclerViewHistory);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            fetchOverviewData();
        }

        private void fetchOverviewData() {
            ApiService apiService = RetrofitClient.getApiService();
            apiService.getPatientDetails(patientId).enqueue(new Callback<PatientResponse>() {
                @Override
                public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        List<MedicalHistory> history = response.body().getData().getMedicalHistory();
                        if (history != null && !history.isEmpty()) {
                            adapter = new com.rushi.healthcare_app.adapters.HistoryAdapter(history);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
                @Override
                public void onFailure(Call<PatientResponse> call, Throwable t) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load overview", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static class RxFragment extends Fragment {
        private String patientId;
        private RecyclerView recyclerView;
        private RxAdapter adapter;

        public static RxFragment newInstance(String patientId) {
            RxFragment fragment = new RxFragment();
            Bundle args = new Bundle();
            args.putString("PATIENT_ID", patientId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                patientId = getArguments().getString("PATIENT_ID");
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_rx, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.d("PatientFrag", "RxFragment created with patientId: " + patientId);
            recyclerView = view.findViewById(R.id.recyclerViewRx);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            fetchPrescriptions();
        }

        private void fetchPrescriptions() {
            ApiService apiService = RetrofitClient.getApiService();
            apiService.getPrescriptions(patientId).enqueue(new Callback<PrescriptionResponse>() {
                @Override
                public void onResponse(Call<PrescriptionResponse> call, Response<PrescriptionResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().records != null) {
                        adapter = new RxAdapter(response.body().records);
                        recyclerView.setAdapter(adapter);
                    }
                }
                @Override
                public void onFailure(Call<PrescriptionResponse> call, Throwable t) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load prescriptions", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static class NotesFragment extends Fragment {
        private String patientId;
        private RecyclerView recyclerView;
        private NotesAdapter adapter;

        public static NotesFragment newInstance(String patientId) {
            NotesFragment fragment = new NotesFragment();
            Bundle args = new Bundle();
            args.putString("PATIENT_ID", patientId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                patientId = getArguments().getString("PATIENT_ID");
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_notes, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.d("PatientFrag", "NotesFragment created with patientId: " + patientId);
            recyclerView = view.findViewById(R.id.recyclerViewNotes);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            fetchNotes();
        }

        private void fetchNotes() {
            ApiService apiService = RetrofitClient.getApiService();
            apiService.getNotes(patientId).enqueue(new Callback<ConsultationNoteResponse>() {
                @Override
                public void onResponse(Call<ConsultationNoteResponse> call, Response<ConsultationNoteResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().records != null) {
                        adapter = new NotesAdapter(response.body().records);
                        recyclerView.setAdapter(adapter);
                    }
                }
                @Override
                public void onFailure(Call<ConsultationNoteResponse> call, Throwable t) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load notes", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static class VitalsFragment extends Fragment {
        private String patientId;
        private RecyclerView recyclerView;
        private VitalsAdapter adapter;

        public static VitalsFragment newInstance(String patientId) {
            VitalsFragment fragment = new VitalsFragment();
            Bundle args = new Bundle();
            args.putString("PATIENT_ID", patientId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                patientId = getArguments().getString("PATIENT_ID");
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_vitals, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.d("PatientFrag", "VitalsFragment created with patientId: " + patientId);
            recyclerView = view.findViewById(R.id.recyclerViewVitals);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            fetchVitals();
        }

        private void fetchVitals() {
            ApiService apiService = RetrofitClient.getApiService();
            apiService.getVitals(patientId).enqueue(new Callback<VitalSignResponse>() {
                @Override
                public void onResponse(Call<VitalSignResponse> call, Response<VitalSignResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().records != null) {
                        adapter = new VitalsAdapter(response.body().records);
                        recyclerView.setAdapter(adapter);
                    }
                }
                @Override
                public void onFailure(Call<VitalSignResponse> call, Throwable t) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load vitals", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}