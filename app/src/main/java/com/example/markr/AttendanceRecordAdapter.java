package com.example.markr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AttendanceRecordAdapter extends RecyclerView.Adapter<AttendanceRecordAdapter.RecordViewHolder> {
    private List<AttendanceRecord> records;
    private OnRecordClickListener listener;

    public interface OnRecordClickListener {
        void onEditClick(AttendanceRecord record);
        void onDeleteClick(AttendanceRecord record);
    }

    public AttendanceRecordAdapter(List<AttendanceRecord> records, OnRecordClickListener listener) {
        this.records = records;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        AttendanceRecord record = records.get(position);
        holder.bind(record, listener);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void updateRecords(List<AttendanceRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectText;
        private TextView attendanceText;
        private TextView dateText;
        private TextView statusText;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
            attendanceText = itemView.findViewById(R.id.attendanceText);
            dateText = itemView.findViewById(R.id.dateText);
            statusText = itemView.findViewById(R.id.statusText);
        }

        public void bind(AttendanceRecord record, OnRecordClickListener listener) {
            subjectText.setText(record.getSubject());
            attendanceText.setText(String.format("%.1f%% (%d/%d)", 
                    record.getAttendancePercentage(), 
                    record.getAttendedClasses(), 
                    record.getTotalClasses()));
            dateText.setText(record.getDateCreated());

            // Set status color based on attendance
            double percentage = record.getAttendancePercentage();
            double minRequired = record.getMinAttendance();
            
            if (percentage >= minRequired) {
                statusText.setText("Good");
                statusText.setTextColor(itemView.getContext().getResources().getColor(R.color.success_color));
            } else if (percentage >= minRequired - 10) {
                statusText.setText("Warning");
                statusText.setTextColor(itemView.getContext().getResources().getColor(R.color.warning_color));
            } else {
                statusText.setText("Critical");
                statusText.setTextColor(itemView.getContext().getResources().getColor(R.color.error_color));
            }

            // Set click listeners
            itemView.setOnClickListener(v -> listener.onEditClick(record));
        }
    }
}
