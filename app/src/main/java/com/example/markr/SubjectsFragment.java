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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.TextView;
import java.util.List;

public class SubjectsFragment extends Fragment {
    
    // Subjects management
    private MaterialButton addSubjectButton;
    private RecyclerView subjectsRecyclerView;
    private View emptyStateLayout;
    
    // Calculator components
    private TextInputEditText subjectEditText;
    private TextInputEditText totalClassesEditText;
    private TextInputEditText attendedClassesEditText;
    private MaterialButton calculateButton;
    private View resultsCard;
    private TextView attendancePercentageText;
    private TextView attendanceStatusText;
    private TextView classesNeededText;
    
    private SubjectAdapter subjectAdapter;
    private AttendanceManager attendanceManager;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_subjects, container, false);
            
            initializeViews(view);
            initializeSubjects();
            setupClickListeners();
            
            return view;
        } catch (Exception e) {
            android.util.Log.e("SubjectsFragment", "Error creating view: " + e.getMessage(), e);
            // Return a simple view to prevent crash
            View errorView = new View(getContext());
            return errorView;
        }
    }
    
    private void initializeViews(View view) {
        // Subjects management views
        addSubjectButton = view.findViewById(R.id.addSubjectButton);
        subjectsRecyclerView = view.findViewById(R.id.subjectsRecyclerView);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        
        // Calculator views
        subjectEditText = view.findViewById(R.id.subjectEditText);
        totalClassesEditText = view.findViewById(R.id.totalClassesEditText);
        attendedClassesEditText = view.findViewById(R.id.attendedClassesEditText);
        calculateButton = view.findViewById(R.id.calculateButton);
        resultsCard = view.findViewById(R.id.resultsCard);
        attendancePercentageText = view.findViewById(R.id.attendancePercentageText);
        attendanceStatusText = view.findViewById(R.id.attendanceStatusText);
        classesNeededText = view.findViewById(R.id.classesNeededText);
    }
    
    private void initializeSubjects() {
        try {
            attendanceManager = AttendanceManager.getInstance();
            
            subjectAdapter = new SubjectAdapter(attendanceManager.getSubjects(), new SubjectAdapter.OnSubjectActionListener() {
                @Override
                public void onEditSubject(Subject subject) {
                    showEditSubjectDialog(subject);
                }
                
                @Override
                public void onDeleteSubject(Subject subject) {
                    showDeleteConfirmationDialog(subject);
                }
                
                @Override
                public void onViewDetails(Subject subject) {
                    showSubjectDetailsDialog(subject);
                }
            });
            
            subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            subjectsRecyclerView.setAdapter(subjectAdapter);
            
            updateEmptyState();
        } catch (Exception e) {
            android.util.Log.e("SubjectsFragment", "Error initializing subjects: " + e.getMessage(), e);
        }
    }
    
    private void setupClickListeners() {
        addSubjectButton.setOnClickListener(v -> showAddSubjectDialog());
        calculateButton.setOnClickListener(v -> calculateAttendance());
    }
    
    private void showAddSubjectDialog() {
        SubjectManagementDialog dialog = SubjectManagementDialog.newInstance(null, new SubjectManagementDialog.OnSubjectSavedListener() {
            @Override
            public void onSubjectSaved(Subject subject, boolean isEdit) {
                attendanceManager.addSubject(subject);
                refreshSubjects();
                Toast.makeText(getContext(), "Subject added successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show(getParentFragmentManager(), "add_subject_dialog");
    }
    
    private void showEditSubjectDialog(Subject subject) {
        SubjectManagementDialog dialog = SubjectManagementDialog.newInstance(subject, new SubjectManagementDialog.OnSubjectSavedListener() {
            @Override
            public void onSubjectSaved(Subject subject, boolean isEdit) {
                // Update the subject in AttendanceManager
                attendanceManager.removeSubject(subject.getName());
                attendanceManager.addSubject(subject);
                refreshSubjects();
                Toast.makeText(getContext(), "Subject updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show(getParentFragmentManager(), "edit_subject_dialog");
    }
    
    private void showDeleteConfirmationDialog(Subject subject) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Delete Subject")
                .setMessage("Are you sure you want to delete \"" + subject.getName() + "\"? This will also delete all attendance records for this subject.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    attendanceManager.removeSubject(subject.getName());
                    refreshSubjects();
                    Toast.makeText(getContext(), "Subject deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showSubjectDetailsDialog(Subject subject) {
        StringBuilder details = new StringBuilder();
        details.append("Subject: ").append(subject.getName()).append("\n\n");
        details.append("Weekdays: ").append(String.join(", ", subject.getWeekdays())).append("\n\n");
        details.append("Sessions per week: ").append(subject.getSessionsPerWeek()).append("\n\n");
        details.append("Total sessions: ").append(subject.getTotalSessions()).append("\n");
        details.append("Attended sessions: ").append(subject.getAttendedSessions()).append("\n");
        details.append("Absent sessions: ").append(subject.getTotalSessions() - subject.getAttendedSessions()).append("\n\n");
        details.append("Attendance: ").append(String.format("%.1f%%", subject.getAttendancePercentage())).append("\n\n");
        details.append("Advice: ").append(subject.getAttendanceAdvice());
        
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Subject Details")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }
    
    private void refreshSubjects() {
        subjectAdapter.updateSubjects(attendanceManager.getSubjects());
        updateEmptyState();
    }
    
    private void updateEmptyState() {
        if (attendanceManager.getSubjects().isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            subjectsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            subjectsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        refreshSubjects();
    }
    
    // Calculator functionality
    private void calculateAttendance() {
        String subjectName = subjectEditText.getText().toString().trim();
        String totalClassesStr = totalClassesEditText.getText().toString().trim();
        String attendedClassesStr = attendedClassesEditText.getText().toString().trim();
        
        // Validate inputs
        if (TextUtils.isEmpty(subjectName)) {
            subjectEditText.setError("Please enter subject name");
            return;
        }
        
        if (TextUtils.isEmpty(totalClassesStr)) {
            totalClassesEditText.setError("Please enter total classes");
            return;
        }
        
        if (TextUtils.isEmpty(attendedClassesStr)) {
            attendedClassesEditText.setError("Please enter attended classes");
            return;
        }
        
        try {
            int totalClasses = Integer.parseInt(totalClassesStr);
            int attendedClasses = Integer.parseInt(attendedClassesStr);
            
            if (totalClasses <= 0) {
                totalClassesEditText.setError("Total classes must be greater than 0");
                return;
            }
            
            if (attendedClasses < 0) {
                attendedClassesEditText.setError("Attended classes cannot be negative");
                return;
            }
            
            if (attendedClasses > totalClasses) {
                attendedClassesEditText.setError("Attended classes cannot be more than total classes");
                return;
            }
            
            // Calculate attendance
            double percentage = (double) attendedClasses / totalClasses * 100;
            int absentClasses = totalClasses - attendedClasses;
            
            // Display results
            attendancePercentageText.setText(String.format("%.1f%%", percentage));
            
            if (percentage >= 75) {
                attendanceStatusText.setText("✅ Good attendance!");
                attendanceStatusText.setTextColor(getResources().getColor(R.color.success_color));
            } else {
                attendanceStatusText.setText("⚠️ Low attendance!");
                attendanceStatusText.setTextColor(getResources().getColor(R.color.warning_color));
            }
            
            int classesNeeded = (int) Math.ceil((0.75 * totalClasses) - attendedClasses);
            if (classesNeeded <= 0) {
                classesNeededText.setText("You can miss " + Math.abs(classesNeeded) + " more classes and still maintain 75% attendance.");
            } else {
                classesNeededText.setText("You need to attend " + classesNeeded + " more classes to reach 75% attendance.");
            }
            
            resultsCard.setVisibility(View.VISIBLE);
            
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}
