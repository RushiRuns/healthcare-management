package com.rushi.healthcare_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rushi.healthcare_app.R;
import com.rushi.healthcare_app.models.LabRecord;
import java.util.List;

public class LabsAdapter extends RecyclerView.Adapter<LabsAdapter.LabViewHolder> {

    private List<LabRecord> labList;
    private OnLabActionListener listener;

    public interface OnLabActionListener {
        void onDelete(LabRecord record);
        void onView(LabRecord record);
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

        int count = (record.image_paths != null) ? record.image_paths.size() : 0;
        holder.tvImageCount.setText(count + " image(s) attached");

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(record));
        holder.itemView.setOnClickListener(v -> listener.onView(record));
    }

    @Override
    public int getItemCount() {
        return labList != null ? labList.size() : 0;
    }

    static class LabViewHolder extends RecyclerView.ViewHolder {
        TextView tvTestName, tvDate, tvImageCount;
        android.widget.ImageView btnDelete;

        public LabViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTestName = itemView.findViewById(R.id.textTestName);
            tvDate = itemView.findViewById(R.id.textTestDate);
            tvImageCount = itemView.findViewById(R.id.textImageCount);
            btnDelete = itemView.findViewById(R.id.btnDeleteLab);
        }
    }
}