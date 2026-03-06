package com.example.markr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.card.MaterialCardView;
import com.example.markr.utils.SessionManager;
import com.example.markr.models.User;
import java.util.List;

public class ProfileFragment extends Fragment {
    
    // Profile Header
    private MaterialTextView userNameText;
    private MaterialTextView userEmailText;
    private MaterialTextView userRoleText;
    private MaterialTextView userStudentIdText;
    private MaterialTextView userAcademicText;
    private MaterialTextView userCollegeText;
    
    // Statistics Cards
    private MaterialTextView overallAttendanceText;
    private MaterialTextView subjectsCountText;
    private MaterialTextView totalSessionsText;
    private MaterialTextView attendedSessionsText;
    private MaterialTextView streakText;
    private MaterialTextView bestSubjectText;
    
    // Achievement Cards
    private MaterialCardView perfectAttendanceCard;
    private MaterialCardView consistentCard;
    private MaterialCardView improvementCard;
    
    // Quick Actions
    private MaterialButton viewRecordsButton;
    private MaterialButton exportDataButton;
    private MaterialButton editProfileButton;
    private MaterialButton settingsButton;
    
    private AttendanceManager attendanceManager;
    private UserManager userManager;
    private SessionManager sessionManager;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        initializeViews(view);
        initializeData();
        setupClickListeners();
        
        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            showUserRegistrationDialog();
        } else {
            loadProfileData();
        }
        
        return view;
    }
    
    private void initializeViews(View view) {
        // Profile Header
        userNameText = view.findViewById(R.id.userNameText);
        userEmailText = view.findViewById(R.id.userEmailText);
        userRoleText = view.findViewById(R.id.userRoleText);
        userStudentIdText = view.findViewById(R.id.userStudentIdText);
        userAcademicText = view.findViewById(R.id.userAcademicText);
        userCollegeText = view.findViewById(R.id.userCollegeText);
        
        // Statistics
        overallAttendanceText = view.findViewById(R.id.overallAttendanceText);
        subjectsCountText = view.findViewById(R.id.subjectsCountText);
        totalSessionsText = view.findViewById(R.id.totalSessionsText);
        attendedSessionsText = view.findViewById(R.id.attendedSessionsText);
        streakText = view.findViewById(R.id.streakText);
        bestSubjectText = view.findViewById(R.id.bestSubjectText);
        
        // Achievement Cards
        perfectAttendanceCard = view.findViewById(R.id.perfectAttendanceCard);
        consistentCard = view.findViewById(R.id.consistentCard);
        improvementCard = view.findViewById(R.id.improvementCard);
        
        // Quick Actions
        viewRecordsButton = view.findViewById(R.id.viewRecordsButton);
        exportDataButton = view.findViewById(R.id.exportDataButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        settingsButton = view.findViewById(R.id.settingsButton);
    }
    
    private void initializeData() {
        attendanceManager = AttendanceManager.getInstance();
        userManager = UserManager.getInstance();
        userManager.setContext(getContext());
        sessionManager = new SessionManager(getContext());
    }
    
    private void setupClickListeners() {
        viewRecordsButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "📊 Records: " + getTotalRecords() + " entries", Toast.LENGTH_SHORT).show();
        });
        
        exportDataButton.setOnClickListener(v -> {
            showExportOptionsDialog();
        });
        
        editProfileButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "✏️ Edit profile moved to Settings tab", Toast.LENGTH_SHORT).show();
        });
        
        settingsButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "⚙️ Settings feature coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        // Achievement card click listeners
        perfectAttendanceCard.setOnClickListener(v -> {
            showAchievementDetails("Perfect Attendance", "Maintain 95%+ attendance", "🎯");
        });
        
        consistentCard.setOnClickListener(v -> {
            showAchievementDetails("Consistent Performer", "80%+ attendance across 3+ subjects", "📈");
        });
        
        improvementCard.setOnClickListener(v -> {
            showAchievementDetails("Streak Master", "10+ day attendance streak", "🔥");
        });
    }
    
    private void showUserRegistrationDialog() {
        // Profile editing coming soon - will integrate with Supabase
        Toast.makeText(getContext(), "Profile editing coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void showAchievementDetails(String title, String description, String icon) {
        Toast.makeText(getContext(), icon + " " + title + ": " + description, Toast.LENGTH_LONG).show();
    }
    
    private void loadProfileData() {
        User user = sessionManager.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "No user data found. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Update user stats if UserManager is available
        if (userManager.isUserRegistered()) {
            userManager.updateUserStats(attendanceManager);
        }
        
        // Load user data from session
        userNameText.setText(user.getName());
        userEmailText.setText(user.getEmail());
        userRoleText.setText(user.getCourse() + " Student");
        userStudentIdText.setText("Student ID: " + user.getStudentId());
        userAcademicText.setText(user.getCourse() + " - " + user.getSemester() + " Semester");
        userCollegeText.setText(user.getCollege());
        
        // Calculate and display statistics
        List<Subject> subjects = attendanceManager.getSubjects();
        double overallAttendance = userManager.isUserRegistered() ? userManager.getCurrentUser().getOverallAttendance() : 0.0;
        
        // Overall attendance
        overallAttendanceText.setText(String.format("%.1f%%", overallAttendance));
        setAttendanceColor(overallAttendanceText, overallAttendance);
        
        // Subjects count
        subjectsCountText.setText(String.valueOf(subjects.size()));
        
        // Calculate total and attended sessions
        int totalSessions = 0;
        int attendedSessions = 0;
        String bestSubject = "None";
        double bestPercentage = 0;
        
        for (Subject subject : subjects) {
            totalSessions += subject.getTotalSessions();
            attendedSessions += subject.getAttendedSessions();
            
            if (subject.getAttendancePercentage() > bestPercentage) {
                bestPercentage = subject.getAttendancePercentage();
                bestSubject = subject.getName();
            }
        }
        
        totalSessionsText.setText(String.valueOf(totalSessions));
        attendedSessionsText.setText(String.valueOf(attendedSessions));
        bestSubjectText.setText(bestSubject);
        
        // Streak
        int streak = userManager.isUserRegistered() ? userManager.getCurrentUser().getCurrentStreak() : 0;
        streakText.setText(String.valueOf(streak));
        
        // Update achievement cards
        updateAchievementCards();
    }
    
    private void setAttendanceColor(MaterialTextView textView, double percentage) {
        if (percentage >= 75) {
            textView.setTextColor(getResources().getColor(R.color.success_color));
        } else if (percentage >= 65) {
            textView.setTextColor(getResources().getColor(R.color.warning_color));
        } else {
            textView.setTextColor(getResources().getColor(R.color.error_color));
        }
    }
    
    private void updateAchievementCards() {
        List<Achievement> achievements = userManager.getAchievements();
        
        // Perfect Attendance Achievement
        Achievement perfectAttendance = getAchievementById(achievements, "perfect_attendance");
        if (perfectAttendance != null && perfectAttendance.isUnlocked()) {
            perfectAttendanceCard.setCardBackgroundColor(getResources().getColor(R.color.success_color));
            perfectAttendanceCard.setAlpha(1.0f);
        } else {
            perfectAttendanceCard.setCardBackgroundColor(getResources().getColor(R.color.surface_color));
            perfectAttendanceCard.setAlpha(0.6f);
        }
        
        // Consistent Performance Achievement
        Achievement consistent = getAchievementById(achievements, "consistent_performer");
        if (consistent != null && consistent.isUnlocked()) {
            consistentCard.setCardBackgroundColor(getResources().getColor(R.color.info_color));
            consistentCard.setAlpha(1.0f);
        } else {
            consistentCard.setCardBackgroundColor(getResources().getColor(R.color.surface_color));
            consistentCard.setAlpha(0.6f);
        }
        
        // Improvement Tracking Achievement
        Achievement streakMaster = getAchievementById(achievements, "streak_master");
        if (streakMaster != null && streakMaster.isUnlocked()) {
            improvementCard.setCardBackgroundColor(getResources().getColor(R.color.accent_color));
            improvementCard.setAlpha(1.0f);
        } else {
            improvementCard.setCardBackgroundColor(getResources().getColor(R.color.surface_color));
            improvementCard.setAlpha(0.6f);
        }
    }
    
    private Achievement getAchievementById(List<Achievement> achievements, String id) {
        for (Achievement achievement : achievements) {
            if (achievement.getId().equals(id)) {
                return achievement;
            }
        }
        return null;
    }
    
    private int getTotalRecords() {
        List<Subject> subjects = attendanceManager.getSubjects();
        int totalRecords = 0;
        for (Subject subject : subjects) {
            totalRecords += subject.getTotalSessions();
        }
        return totalRecords;
    }
    
    private void showExportOptionsDialog() {
        String[] options = {"Export Attendance Data", "Export User Profile", "Export All Data"};
        
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("📤 Export Data")
                .setItems(options, (dialog, which) -> {
                    DataExportManager exportManager = DataExportManager.getInstance();
                    exportManager.setContext(getContext());
                    
                    switch (which) {
                        case 0:
                            exportManager.exportAttendanceData();
                            break;
                        case 1:
                            exportManager.exportUserProfile();
                            break;
                        case 2:
                            exportManager.exportAttendanceData();
                            // Small delay before exporting profile
                            new android.os.Handler().postDelayed(() -> {
                                exportManager.exportUserProfile();
                            }, 1000);
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            loadProfileData();
        }
    }
}
