package com.example.markr;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class CalculatorFragment extends Fragment {
    
    private TextInputEditText subjectEditText;
    private TextInputEditText totalClassesEditText;
    private TextInputEditText attendedClassesEditText;
    private TextInputEditText minAttendanceEditText;
    private MaterialButton calculateButton;
    private MaterialButton saveButton;
    private MaterialCardView resultsCard;
    private MaterialTextView attendancePercentageText;
    private MaterialTextView attendanceStatusText;
    private MaterialTextView classesNeededText;
    
    private AttendanceDatabaseHelper databaseHelper;
    private AttendanceRecordAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        
        initializeViews(view);
        initializeDatabase();
        setupClickListeners();
        
        return view;
    }
    
    private void initializeViews(View view) {
        subjectEditText = view.findViewById(R.id.subjectEditText);
        totalClassesEditText = view.findViewById(R.id.totalClassesEditText);
        attendedClassesEditText = view.findViewById(R.id.attendedClassesEditText);
        minAttendanceEditText = view.findViewById(R.id.minAttendanceEditText);
        calculateButton = view.findViewById(R.id.calculateButton);
        saveButton = view.findViewById(R.id.saveButton);
        resultsCard = view.findViewById(R.id.resultsCard);
        attendancePercentageText = view.findViewById(R.id.attendancePercentageText);
        attendanceStatusText = view.findViewById(R.id.attendanceStatusText);
        classesNeededText = view.findViewById(R.id.classesNeededText);
    }
    
    private void initializeDatabase() {
        databaseHelper = new AttendanceDatabaseHelper(getContext());
    }
    
    private void setupClickListeners() {
        calculateButton.setOnClickListener(v -> calculateAttendance());
        saveButton.setOnClickListener(v -> saveRecord());
    }
    
    private void calculateAttendance() {
        if (!validateInputs()) {
            return;
        }

        String subject = subjectEditText.getText().toString().trim();
        int totalClasses = Integer.parseInt(totalClassesEditText.getText().toString());
        int attendedClasses = Integer.parseInt(attendedClassesEditText.getText().toString());
        double minAttendance = Double.parseDouble(minAttendanceEditText.getText().toString());

        if (attendedClasses > totalClasses) {
            Toast.makeText(getContext(), getString(R.string.error_attended_greater), Toast.LENGTH_SHORT).show();
            return;
        }

        AttendanceRecord record = new AttendanceRecord(subject, totalClasses, attendedClasses, minAttendance);
        displayResults(record);
    }
    
    private boolean validateInputs() {
        if (TextUtils.isEmpty(subjectEditText.getText())) {
            Toast.makeText(getContext(), getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(totalClassesEditText.getText()) || 
            TextUtils.isEmpty(attendedClassesEditText.getText()) || 
            TextUtils.isEmpty(minAttendanceEditText.getText())) {
            Toast.makeText(getContext(), getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Integer.parseInt(totalClassesEditText.getText().toString());
            Integer.parseInt(attendedClassesEditText.getText().toString());
            Double.parseDouble(minAttendanceEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), getString(R.string.error_invalid_input), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    
    private void displayResults(AttendanceRecord record) {
        double percentage = record.getAttendancePercentage();
        double minRequired = record.getMinAttendance();

        attendancePercentageText.setText(String.format("%.1f%%", percentage));

        if (percentage >= minRequired) {
            attendanceStatusText.setText(getString(R.string.attendance_good));
            attendanceStatusText.setTextColor(getResources().getColor(R.color.success_color));
            
            int canMiss = record.getClassesCanMiss();
            if (canMiss > 0) {
                classesNeededText.setText(String.format(getString(R.string.classes_safe_format), 
                        canMiss, minRequired));
            } else {
                classesNeededText.setText("Perfect attendance!");
            }
        } else {
            int needed = record.getClassesNeeded();
            if (percentage >= minRequired - 10) {
                attendanceStatusText.setText(getString(R.string.attendance_warning));
                attendanceStatusText.setTextColor(getResources().getColor(R.color.warning_color));
            } else {
                attendanceStatusText.setText(getString(R.string.attendance_critical));
                attendanceStatusText.setTextColor(getResources().getColor(R.color.error_color));
            }
            
            classesNeededText.setText(String.format(getString(R.string.classes_needed_format), 
                    needed, minRequired));
        }

        resultsCard.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
    }
    
    private void saveRecord() {
        if (!validateInputs()) {
            return;
        }

        String subject = subjectEditText.getText().toString().trim();
        int totalClasses = Integer.parseInt(totalClassesEditText.getText().toString());
        int attendedClasses = Integer.parseInt(attendedClassesEditText.getText().toString());
        double minAttendance = Double.parseDouble(minAttendanceEditText.getText().toString());

        AttendanceRecord record = new AttendanceRecord(subject, totalClasses, attendedClasses, minAttendance);
        long id = databaseHelper.insertRecord(record);
        
        if (id != -1) {
            Toast.makeText(getContext(), "Record saved successfully!", Toast.LENGTH_SHORT).show();
            clearInputs();
        } else {
            Toast.makeText(getContext(), "Failed to save record", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void clearInputs() {
        subjectEditText.setText("");
        totalClassesEditText.setText("");
        attendedClassesEditText.setText("");
        minAttendanceEditText.setText("75");
        resultsCard.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
