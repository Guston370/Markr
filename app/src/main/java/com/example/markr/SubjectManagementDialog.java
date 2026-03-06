package com.example.markr;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectManagementDialog extends DialogFragment {
    
    public interface OnSubjectSavedListener {
        void onSubjectSaved(Subject subject, boolean isEdit);
    }
    
    private EditText subjectNameEditText;
    private LinearLayout weekdaysContainer;
    private LinearLayout sessionsPerDayContainer;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    
    private Subject subjectToEdit;
    private boolean isEditMode;
    private OnSubjectSavedListener listener;
    
    private String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private Map<String, CheckBox> weekdayCheckboxes;
    private Map<String, Spinner> sessionsSpinners;
    
    public static SubjectManagementDialog newInstance(Subject subject, OnSubjectSavedListener listener) {
        SubjectManagementDialog dialog = new SubjectManagementDialog();
        dialog.subjectToEdit = subject;
        dialog.isEditMode = subject != null;
        dialog.listener = listener;
        return dialog;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_subject_management, container, false);
        
        initializeViews(view);
        setupWeekdays();
        setupSessionsPerDay();
        populateFields();
        setupClickListeners();
        
        return view;
    }
    
    private void initializeViews(View view) {
        subjectNameEditText = view.findViewById(R.id.subjectNameEditText);
        weekdaysContainer = view.findViewById(R.id.weekdaysContainer);
        sessionsPerDayContainer = view.findViewById(R.id.sessionsPerDayContainer);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        
        weekdayCheckboxes = new HashMap<>();
        sessionsSpinners = new HashMap<>();
    }
    
    private void setupWeekdays() {
        for (String weekday : weekdays) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(weekday);
            checkBox.setTextSize(16);
            checkBox.setTextColor(getResources().getColor(R.color.primary_text));
            checkBox.setPadding(16, 12, 16, 12);
            
            // Add listener to show/hide sessions spinner when checkbox is toggled
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateSessionsVisibility(weekday, isChecked);
            });
            
            weekdaysContainer.addView(checkBox);
            weekdayCheckboxes.put(weekday, checkBox);
        }
    }
    
    private void setupSessionsPerDay() {
        String[] sessionOptions = {"1", "2", "3", "4", "5", "6", "7", "8"};
        
        for (String weekday : weekdays) {
            LinearLayout dayLayout = new LinearLayout(getContext());
            dayLayout.setOrientation(LinearLayout.HORIZONTAL);
            dayLayout.setPadding(16, 8, 16, 8);
            dayLayout.setVisibility(View.GONE); // Initially hidden
            
            TextView dayLabel = new TextView(getContext());
            dayLabel.setText(weekday + ":");
            dayLabel.setTextSize(16);
            dayLabel.setTextColor(getResources().getColor(R.color.primary_text));
            dayLabel.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            
            Spinner spinner = new Spinner(getContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sessionOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setLayoutParams(new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
            
            dayLayout.addView(dayLabel);
            dayLayout.addView(spinner);
            
            sessionsPerDayContainer.addView(dayLayout);
            sessionsSpinners.put(weekday, spinner);
        }
    }
    
    private void updateSessionsVisibility(String weekday, boolean isChecked) {
        Spinner spinner = sessionsSpinners.get(weekday);
        if (spinner != null) {
            View parentLayout = (View) spinner.getParent();
            if (parentLayout != null) {
                parentLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        }
    }
    
    private void populateFields() {
        if (isEditMode && subjectToEdit != null) {
            subjectNameEditText.setText(subjectToEdit.getName());
            
            // Check weekdays and show corresponding sessions
            if (subjectToEdit.getWeekdays() != null) {
                for (String weekday : subjectToEdit.getWeekdays()) {
                    CheckBox checkBox = weekdayCheckboxes.get(weekday);
                    if (checkBox != null) {
                        checkBox.setChecked(true);
                        // Show the sessions spinner for this weekday
                        updateSessionsVisibility(weekday, true);
                    }
                }
            }
            
            // Set sessions per day
            if (subjectToEdit.getSessionsPerDay() != null) {
                for (Map.Entry<String, Integer> entry : subjectToEdit.getSessionsPerDay().entrySet()) {
                    Spinner spinner = sessionsSpinners.get(entry.getKey());
                    if (spinner != null) {
                        int sessions = entry.getValue();
                        if (sessions > 0 && sessions <= 8) {
                            spinner.setSelection(sessions - 1);
                        }
                    }
                }
            }
        }
    }
    
    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveSubject());
        cancelButton.setOnClickListener(v -> dismiss());
    }
    
    private void saveSubject() {
        String subjectName = subjectNameEditText.getText().toString().trim();
        
        if (subjectName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a subject name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get selected weekdays
        List<String> selectedWeekdays = new ArrayList<>();
        Map<String, Integer> sessionsPerDay = new HashMap<>();
        
        for (String weekday : weekdays) {
            CheckBox checkBox = weekdayCheckboxes.get(weekday);
            Spinner spinner = sessionsSpinners.get(weekday);
            
            if (checkBox != null && checkBox.isChecked()) {
                selectedWeekdays.add(weekday);
                if (spinner != null) {
                    int sessions = Integer.parseInt(spinner.getSelectedItem().toString());
                    sessionsPerDay.put(weekday, sessions);
                }
            }
        }
        
        if (selectedWeekdays.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one weekday", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create or update subject
        Subject subject;
        if (isEditMode) {
            subject = subjectToEdit;
            subject.setName(subjectName);
        } else {
            subject = new Subject(subjectName, selectedWeekdays, sessionsPerDay);
        }
        
        subject.setWeekdays(selectedWeekdays);
        subject.setSessionsPerDay(sessionsPerDay);
        
        if (listener != null) {
            listener.onSubjectSaved(subject, isEditMode);
        }
        
        dismiss();
    }
}
