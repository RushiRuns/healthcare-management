package com.rushi.healthcare_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rushi.healthcare_app.MedicalHistory;
import com.rushi.healthcare_app.R;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<MedicalHistory> historyList;
    private OnHistoryActionListener listener;

    public interface OnHistoryActionListener {
        void onEdit(MedicalHistory history);
        void onDelete(MedicalHistory history);
    }

    public HistoryAdapter(List<MedicalHistory> historyList, OnHistoryActionListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        MedicalHistory item = historyList.get(position);
        holder.tvConditionName.setText(item.getConditionName() + " (" + item.getDiagnosisDate() + ")");

        holder.btnEditHistory.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });

        holder.btnDeleteHistory.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvConditionName;
        MaterialButton btnEditHistory;
        MaterialButton btnDeleteHistory;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvConditionName = itemView.findViewById(R.id.tvConditionName);
            btnEditHistory = itemView.findViewById(R.id.btnEditHistory);
            btnDeleteHistory = itemView.findViewById(R.id.btnDeleteHistory);
        }
    }
}