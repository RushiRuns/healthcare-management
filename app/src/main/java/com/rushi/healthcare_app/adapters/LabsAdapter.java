package com.rushi.healthcare_app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rushi.healthcare_app.R;
import com.rushi.healthcare_app.models.LabRecord;
import java.util.List;

public class LabsAdapter extends RecyclerView.Adapter<LabsAdapter.LabViewHolder> {

    private List<LabRecord> labList;
    private OnLabActionListener listener;

    public interface OnLabActionListener {
        void onEdit(LabRecord record);
        void onDelete(LabRecord record);
    }

    public LabsAdapter(List<LabRecord> labList, OnLabActionListener listener) {
        this.labList = labList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lab, parent, false);
        return new LabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabViewHolder holder, int position) {
        LabRecord record = labList.get(position);
        holder.tvTestName.setText(record.test_name);
        holder.tvDate.setText(record.test_date);
        holder.tvResult.setText(record.result_value != null ? record.result_value : "N/A");
        holder.tvUnit.setText(record.unit != null ? record.unit : "");
        holder.tvRange.setText("Ref Range: " + (record.reference_range != null ? record.reference_range : "N/A"));

        if ("abnormal".equalsIgnoreCase(record.status) || "critical".equalsIgnoreCase(record.status)) {
            holder.tvResult.setTextColor(Color.parseColor("#D32F2F"));
        } else {
            holder.tvResult.setTextColor(MaterialColors.getColor(holder.tvResult, com.google.android.material.R.attr.colorPrimary));
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(record));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(record));
    }

    @Override
    public int getItemCount() {
        return labList != null ? labList.size() : 0;
    }

    static class LabViewHolder extends RecyclerView.ViewHolder {
        TextView tvTestName, tvDate, tvResult, tvUnit, tvRange;
        android.widget.ImageView btnEdit, btnDelete;

        public LabViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTestName = itemView.findViewById(R.id.textTestName);
            tvDate = itemView.findViewById(R.id.textTestDate);
            tvResult = itemView.findViewById(R.id.textResultValue);
            tvUnit = itemView.findViewById(R.id.textUnit);
            tvRange = itemView.findViewById(R.id.textReferenceRange);
            btnEdit = itemView.findViewById(R.id.btnEditLab);
            btnDelete = itemView.findViewById(R.id.btnDeleteLab);
        }
    }
}