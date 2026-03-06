package com.example.markr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecordsFragment extends Fragment implements AttendanceRecordAdapter.OnRecordClickListener {
    
    private RecyclerView recordsRecyclerView;
    private View emptyStateLayout;
    
    private AttendanceDatabaseHelper databaseHelper;
    private AttendanceRecordAdapter adapter;
    private List<AttendanceRecord> records;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);
        
        initializeViews(view);
        initializeDatabase();
        
        return view;
    }
    
    private void initializeViews(View view) {
        recordsRecyclerView = view.findViewById(R.id.recordsRecyclerView);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
    }
    
    private void initializeDatabase() {
        databaseHelper = new AttendanceDatabaseHelper(getContext());
        records = databaseHelper.getAllRecords();
        adapter = new AttendanceRecordAdapter(records, this);
        recordsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recordsRecyclerView.setAdapter(adapter);
        
        updateEmptyState();
    }
    
    private void updateEmptyState() {
        if (records.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recordsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recordsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onEditClick(AttendanceRecord record) {
        showEditRecordDialog(record);
    }
    
    @Override
    public void onDeleteClick(AttendanceRecord record) {
        showDeleteConfirmationDialog(record);
    }
    
    private void showEditRecordDialog(AttendanceRecord record) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_record, null);
        
        com.google.android.material.textfield.TextInputEditText subjectEditText = dialogView.findViewById(R.id.subjectEditText);
        com.google.android.material.textfield.TextInputEditText totalClassesEditText = dialogView.findViewById(R.id.totalClassesEditText);
        com.google.android.material.textfield.TextInputEditText attendedClassesEditText = dialogView.findViewById(R.id.attendedClassesEditText);
        
        // Pre-fill with current values
        subjectEditText.setText(record.getSubject());
        totalClassesEditText.setText(String.valueOf(record.getTotalClasses()));
        attendedClassesEditText.setText(String.valueOf(record.getAttendedClasses()));
        
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("✏️ Edit Record")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        String subject = subjectEditText.getText().toString().trim();
                        int totalClasses = Integer.parseInt(totalClassesEditText.getText().toString().trim());
                        int attendedClasses = Integer.parseInt(attendedClassesEditText.getText().toString().trim());
                        
                        if (subject.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter subject name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        if (attendedClasses > totalClasses) {
                            Toast.makeText(getContext(), "Attended classes cannot be greater than total classes", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        // Update record
                        record.setSubject(subject);
                        record.setTotalClasses(totalClasses);
                        record.setAttendedClasses(attendedClasses);
                        
                        if (databaseHelper.updateRecord(record)) {
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Record updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update record", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showDeleteConfirmationDialog(AttendanceRecord record) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("🗑️ Delete Record")
                .setMessage("Are you sure you want to delete this record?\n\nSubject: " + record.getSubject() + 
                           "\nAttendance: " + String.format("%.1f%%", record.getAttendancePercentage()))
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (databaseHelper.deleteRecord(record.getId())) {
                        records.remove(record);
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                        Toast.makeText(getContext(), "Record deleted successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete record", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
