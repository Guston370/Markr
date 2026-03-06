package com.example.markr;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedCalculatorFragment extends Fragment {
    
    private AttendanceManager attendanceManager;
    private SubjectAdapter subjectAdapter;
    private RecyclerView subjectsRecyclerView;
    private FloatingActionButton addSubjectFab;
    private MaterialCardView addSubjectCard;
    private EditText subjectNameEditText;
    private LinearLayout weekdaysLayout;
    private MaterialButton saveSubjectButton;
    private MaterialButton cancelSubjectButton;
    private TextView subjectsCountText;
    private MaterialCardView emptyStateCard;
    private MaterialButton addFirstSubjectButton;
    private TextView overallAttendanceText;
    private TextView totalSubjectsText;
    
    private boolean isAddingSubject = false;
    private List<String> selectedWeekdays = new ArrayList<>();
    private Map<String, Integer> sessionsPerDay = new HashMap<>();
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced_calculator, container, false);
        
        initializeViews(view);
        initializeAttendanceManager();
        setupClickListeners();
        setupWeekdaysLayout();
        updateDisplay();
        
        return view;
    }
    
    private void initializeViews(View view) {
        subjectsRecyclerView = view.findViewById(R.id.subjectsRecyclerView);
        addSubjectFab = view.findViewById(R.id.addSubjectFab);
        addSubjectCard = view.findViewById(R.id.addSubjectCard);
        subjectNameEditText = view.findViewById(R.id.subjectNameEditText);
        weekdaysLayout = view.findViewById(R.id.weekdaysLayout);
        saveSubjectButton = view.findViewById(R.id.saveSubjectButton);
        cancelSubjectButton = view.findViewById(R.id.cancelSubjectButton);
        subjectsCountText = view.findViewById(R.id.subjectsCountText);
        emptyStateCard = view.findViewById(R.id.emptyStateCard);
        addFirstSubjectButton = view.findViewById(R.id.addFirstSubjectButton);
        overallAttendanceText = view.findViewById(R.id.overallAttendanceText);
        totalSubjectsText = view.findViewById(R.id.totalSubjectsText);
        
        // Setup RecyclerView
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    
    private void initializeAttendanceManager() {
        attendanceManager = AttendanceManager.getInstance();
        subjectAdapter = new SubjectAdapter(attendanceManager.getSubjects(), new SubjectAdapter.OnSubjectActionListener() {
            @Override
            public void onEditSubject(Subject subject) {
                editSubject(subject);
            }
            
            @Override
            public void onDeleteSubject(Subject subject) {
                deleteSubject(subject);
            }
            
            @Override
            public void onViewDetails(Subject subject) {
                showSubjectDetails(subject);
            }
        });
        subjectsRecyclerView.setAdapter(subjectAdapter);
    }
    
    private void setupClickListeners() {
        addSubjectFab.setOnClickListener(v -> toggleAddSubjectForm());
        saveSubjectButton.setOnClickListener(v -> saveSubject());
        cancelSubjectButton.setOnClickListener(v -> cancelAddSubject());
        addFirstSubjectButton.setOnClickListener(v -> toggleAddSubjectForm());
    }
    
    private void setupWeekdaysLayout() {
        String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        
        for (String weekday : weekdays) {
            // Create weekday container
            LinearLayout weekdayContainer = new LinearLayout(getContext());
            weekdayContainer.setOrientation(LinearLayout.HORIZONTAL);
            weekdayContainer.setPadding(0, 8, 0, 8);
            
            // Create checkbox
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(weekday);
            checkBox.setTextColor(getResources().getColor(R.color.primary_text));
            checkBox.setTextSize(16);
            checkBox.setPadding(0, 0, 16, 0);
            
            // Create sessions spinner container
            LinearLayout spinnerContainer = new LinearLayout(getContext());
            spinnerContainer.setOrientation(LinearLayout.HORIZONTAL);
            spinnerContainer.setVisibility(View.GONE);
            
            TextView sessionsLabel = new TextView(getContext());
            sessionsLabel.setText("Sessions:");
            sessionsLabel.setTextColor(getResources().getColor(R.color.secondary_text));
            sessionsLabel.setTextSize(14);
            sessionsLabel.setPadding(0, 0, 8, 0);
            
            Spinner sessionsSpinner = new Spinner(getContext());
            ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, new Integer[]{1, 2, 3, 4, 5, 6, 7});
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sessionsSpinner.setAdapter(spinnerAdapter);
            sessionsSpinner.setMinimumWidth(120);
            
            spinnerContainer.addView(sessionsLabel);
            spinnerContainer.addView(sessionsSpinner);
            
            // Set up checkbox listener
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedWeekdays.add(weekday);
                    sessionsPerDay.put(weekday, 1);
                    spinnerContainer.setVisibility(View.VISIBLE);
                } else {
                    selectedWeekdays.remove(weekday);
                    sessionsPerDay.remove(weekday);
                    spinnerContainer.setVisibility(View.GONE);
                }
            });
            
            // Set up spinner listener
            sessionsSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    if (selectedWeekdays.contains(weekday)) {
                        sessionsPerDay.put(weekday, (Integer) parent.getItemAtPosition(position));
                    }
                }
                
                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
            
            weekdayContainer.addView(checkBox);
            weekdayContainer.addView(spinnerContainer);
            
            weekdaysLayout.addView(weekdayContainer);
        }
    }
    
    private void toggleAddSubjectForm() {
        isAddingSubject = !isAddingSubject;
        if (isAddingSubject) {
            addSubjectCard.setVisibility(View.VISIBLE);
            addSubjectFab.setVisibility(View.GONE);
        } else {
            addSubjectCard.setVisibility(View.GONE);
            addSubjectFab.setVisibility(View.VISIBLE);
            clearAddSubjectForm();
        }
    }
    
    private void saveSubject() {
        String subjectName = subjectNameEditText.getText().toString().trim();
        
        if (TextUtils.isEmpty(subjectName)) {
            showError("Please enter a subject name");
            return;
        }
        
        if (selectedWeekdays.isEmpty()) {
            showError("Please select at least one weekday");
            return;
        }
        
        // Check for duplicates
        if (attendanceManager.getSubject(subjectName) != null) {
            showError("A subject with this name already exists");
            return;
        }
        
        // Validate sessions per day
        boolean hasValidSessions = false;
        for (String weekday : selectedWeekdays) {
            if (sessionsPerDay.getOrDefault(weekday, 0) > 0) {
                hasValidSessions = true;
                break;
            }
        }
        
        if (!hasValidSessions) {
            showError("Please set sessions for at least one weekday");
            return;
        }
        
        Subject subject = new Subject(subjectName, new ArrayList<>(selectedWeekdays), new HashMap<>(sessionsPerDay));
        attendanceManager.addSubject(subject);
        
        showSuccess("Subject added successfully!");
        updateDisplay();
        toggleAddSubjectForm();
    }
    
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
    
    private void showSuccess(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    private void cancelAddSubject() {
        toggleAddSubjectForm();
    }
    
    private void clearAddSubjectForm() {
        subjectNameEditText.setText("");
        selectedWeekdays.clear();
        sessionsPerDay.clear();
        
        // Uncheck all checkboxes and hide spinners
        for (int i = 0; i < weekdaysLayout.getChildCount(); i++) {
            View child = weekdaysLayout.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout container = (LinearLayout) child;
                for (int j = 0; j < container.getChildCount(); j++) {
                    View subChild = container.getChildAt(j);
                    if (subChild instanceof CheckBox) {
                        ((CheckBox) subChild).setChecked(false);
                    } else if (subChild instanceof LinearLayout) {
                        subChild.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
    
    private void editSubject(Subject subject) {
        // Create edit dialog with pre-filled data
        SubjectManagementDialog dialog = SubjectManagementDialog.newInstance(subject, new SubjectManagementDialog.OnSubjectSavedListener() {
            @Override
            public void onSubjectSaved(Subject updatedSubject, boolean isEdit) {
                if (isEdit) {
                    // Update the subject in AttendanceManager
                    attendanceManager.removeSubject(subject.getName());
                    attendanceManager.addSubject(updatedSubject);
                    Toast.makeText(getContext(), "Subject updated successfully!", Toast.LENGTH_SHORT).show();
                    updateDisplay();
                }
            }
        });
        
        dialog.show(getParentFragmentManager(), "edit_subject_dialog");
    }
    
    private void deleteSubject(Subject subject) {
        attendanceManager.removeSubject(subject.getName());
        Toast.makeText(getContext(), "Subject deleted", Toast.LENGTH_SHORT).show();
        updateDisplay();
    }
    
    private void showSubjectDetails(Subject subject) {
        // Create a detailed view dialog for the subject
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_subject_details, null);
        
        // Initialize views
        android.widget.TextView subjectNameText = dialogView.findViewById(R.id.subjectNameText);
        android.widget.TextView weekdaysText = dialogView.findViewById(R.id.weekdaysText);
        android.widget.TextView sessionsPerDayText = dialogView.findViewById(R.id.sessionsPerDayText);
        android.widget.TextView totalSessionsText = dialogView.findViewById(R.id.totalSessionsText);
        android.widget.TextView attendedSessionsText = dialogView.findViewById(R.id.attendedSessionsText);
        android.widget.TextView attendancePercentageText = dialogView.findViewById(R.id.attendancePercentageText);
        android.widget.TextView statusText = dialogView.findViewById(R.id.statusText);
        
        // Set subject data
        subjectNameText.setText(subject.getName());
        weekdaysText.setText(String.join(", ", subject.getWeekdays()));
        sessionsPerDayText.setText(String.valueOf(subject.getSessionsPerWeek()));
        
        // Calculate attendance statistics
        List<AttendanceSession> sessions = new ArrayList<>(); // Simplified for now
        int totalSessions = sessions.size();
        int attendedSessions = 0;
        int absentSessions = 0;
        int notConductedSessions = 0;
        
        for (AttendanceSession session : sessions) {
            switch (session.getStatus()) {
                case PRESENT:
                    attendedSessions++;
                    break;
                case ABSENT:
                    absentSessions++;
                    break;
                case NOT_CONDUCTED:
                    notConductedSessions++;
                    break;
            }
        }
        
        double attendancePercentage = totalSessions > 0 ? (double) attendedSessions / totalSessions * 100 : 0;
        
        totalSessionsText.setText(String.valueOf(totalSessions));
        attendedSessionsText.setText(String.valueOf(attendedSessions));
        attendancePercentageText.setText(String.format("%.1f%%", attendancePercentage));
        
        // Set status
        if (attendancePercentage >= 75) {
            statusText.setText("✅ Good Attendance");
            statusText.setTextColor(getResources().getColor(R.color.success_color));
        } else if (attendancePercentage >= 50) {
            statusText.setText("⚠️ Low Attendance");
            statusText.setTextColor(getResources().getColor(R.color.warning_color));
        } else {
            statusText.setText("❌ Critical Attendance");
            statusText.setTextColor(getResources().getColor(R.color.error_color));
        }
        
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("📚 Subject Details")
                .setView(dialogView)
                .setPositiveButton("Edit", (dialog, which) -> editSubject(subject))
                .setNegativeButton("Close", null)
                .show();
    }
    
    private void updateDisplay() {
        subjectAdapter.updateSubjects(attendanceManager.getSubjects());
        
        double overallAttendance = attendanceManager.getOverallAttendance();
        overallAttendanceText.setText(String.format("%.1f%%", overallAttendance));
        
        int totalSubjects = attendanceManager.getSubjects().size();
        totalSubjectsText.setText(String.valueOf(totalSubjects));
        
        // Update subjects count
        String countText = totalSubjects == 1 ? "1 subject" : totalSubjects + " subjects";
        subjectsCountText.setText(countText);
        
        // Show/hide empty state
        if (totalSubjects == 0) {
            emptyStateCard.setVisibility(View.VISIBLE);
            subjectsRecyclerView.setVisibility(View.GONE);
            addFirstSubjectButton.setVisibility(View.VISIBLE);
        } else {
            emptyStateCard.setVisibility(View.GONE);
            subjectsRecyclerView.setVisibility(View.VISIBLE);
            addFirstSubjectButton.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        attendanceManager.saveData();
    }
}

