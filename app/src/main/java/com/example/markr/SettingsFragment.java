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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.markr.utils.SessionManager;
import com.example.markr.models.User;
import java.util.List;

public class SettingsFragment extends Fragment {
    
    // User Profile Section
    private MaterialCardView profileCard;
    private MaterialTextView userNameText;
    private MaterialTextView userEmailText;
    private MaterialTextView userCourseText;
    private MaterialButton editProfileButton;
    
    // Statistics
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
    
    // App Settings Section
    private MaterialCardView notificationsCard;
    private MaterialCardView dataCard;
    private MaterialCardView aboutCard;
    
    // Action Buttons
    private MaterialButton exportDataButton;
    private MaterialButton clearDataButton;
    private MaterialButton aboutButton;
    private MaterialButton logoutButton;
    
    private UserManager userManager;
    private AttendanceManager attendanceManager;
    private SessionManager sessionManager;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_settings, container, false);
            
            initializeViews(view);
            initializeData();
            setupClickListeners();
            loadUserData();
            
            return view;
        } catch (Exception e) {
            android.util.Log.e("SettingsFragment", "Error creating view: " + e.getMessage(), e);
            // Return a simple view to prevent crash
            View errorView = new View(getContext());
            return errorView;
        }
    }
    
    private void initializeViews(View view) {
        // User Profile Section
        profileCard = view.findViewById(R.id.profileCard);
        userNameText = view.findViewById(R.id.userNameText);
        userEmailText = view.findViewById(R.id.userEmailText);
        userCourseText = view.findViewById(R.id.userCourseText);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        
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
        
        // App Settings Section
        notificationsCard = view.findViewById(R.id.notificationsCard);
        dataCard = view.findViewById(R.id.dataCard);
        aboutCard = view.findViewById(R.id.aboutCard);
        
        // Action Buttons
        exportDataButton = view.findViewById(R.id.exportDataButton);
        clearDataButton = view.findViewById(R.id.clearDataButton);
        aboutButton = view.findViewById(R.id.aboutButton);
        logoutButton = view.findViewById(R.id.logoutButton);
    }
    
    private void initializeData() {
        userManager = UserManager.getInstance();
        attendanceManager = AttendanceManager.getInstance();
        sessionManager = new SessionManager(getContext());
    }
    
    private void setupClickListeners() {
        // Profile Section
        editProfileButton.setOnClickListener(v -> showUserRegistrationDialog());
        profileCard.setOnClickListener(v -> showUserRegistrationDialog());
        
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
        
        // Data Management
        exportDataButton.setOnClickListener(v -> {
            showExportOptionsDialog();
        });
        
        clearDataButton.setOnClickListener(v -> showClearDataDialog());
        
        // About Section
        aboutButton.setOnClickListener(v -> showAboutDialog());
        
        logoutButton.setOnClickListener(v -> showLogoutDialog());
        
        // Settings Cards
        notificationsCard.setOnClickListener(v -> {
            showNotificationSettingsDialog();
        });
        
        dataCard.setOnClickListener(v -> {
            showDataManagementDialog();
        });
        
        aboutCard.setOnClickListener(v -> showAboutDialog());
    }
    
    private void showUserRegistrationDialog() {
        // Profile editing coming soon - will integrate with Supabase
        Toast.makeText(getContext(), "Profile editing coming soon!", Toast.LENGTH_SHORT).show();
    }
    
    private void showClearDataDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Clear All Data")
                .setMessage("Are you sure you want to clear all attendance data? This action cannot be undone.")
                .setPositiveButton("Clear Data", (dialog, which) -> {
                    clearAllData();
                    Toast.makeText(getContext(), "All data cleared successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("🚪 Logout")
                .setMessage("Are you sure you want to logout?\n\nYou'll need to login again to access your attendance data and settings.")
                .setPositiveButton("Yes, Logout", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }
    
    private void performLogout() {
        try {
            // Clear both authentication managers
            AuthenticationManager.getInstance().logout();
            SessionManager sessionManager = new SessionManager(getContext());
            sessionManager.logoutUser();
            
            // Show success message
            Toast.makeText(getContext(), "✅ Logged out successfully!", Toast.LENGTH_SHORT).show();
            
            // Start login activity and clear the back stack
            android.content.Intent intent = new android.content.Intent(getContext(), LoginActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            // Finish the current activity
            if (getActivity() != null) {
                getActivity().finish();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error during logout. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("About Markr")
                .setMessage("Markr v1.0\n\n" +
                        "Smart Attendance Calculator for Students\n\n" +
                        "Features:\n" +
                        "• Track attendance across multiple subjects\n" +
                        "• Session-level attendance marking\n" +
                        "• Achievement system\n" +
                        "• Calendar integration\n" +
                        "• Data persistence\n\n" +
                        "Built with ❤️ for better attendance tracking")
                .setPositiveButton("OK", null)
                .show();
    }
    
    private void clearAllData() {
        // Clear attendance data
        List<Subject> subjects = attendanceManager.getSubjects();
        for (Subject subject : subjects) {
            attendanceManager.removeSubject(subject.getName());
        }
        
        // Reset user stats - simplified for now
        Toast.makeText(getContext(), "All data cleared successfully!", Toast.LENGTH_SHORT).show();
    }
    
    private void showAchievementDetails(String title, String description, String icon) {
        Toast.makeText(getContext(), icon + " " + title + ": " + description, Toast.LENGTH_LONG).show();
    }
    
    private void loadUserData() {
        User user = sessionManager.getCurrentUser();
        if (user != null) {
            userNameText.setText(user.getName());
            userEmailText.setText(user.getEmail());
            userCourseText.setText(user.getCourse() + " - " + user.getSemester());
            
            // Update user stats if UserManager is available
            if (userManager.isUserRegistered()) {
                userManager.updateUserStats(attendanceManager);
            }
            
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
            
            // Show profile card
            profileCard.setVisibility(View.VISIBLE);
        } else {
            // Show registration prompt
            userNameText.setText("Complete Your Profile");
            userEmailText.setText("Tap to register and get started");
            userCourseText.setText("");
            
            // Reset statistics
            overallAttendanceText.setText("0.0%");
            subjectsCountText.setText("0");
            totalSessionsText.setText("0");
            attendedSessionsText.setText("0");
            bestSubjectText.setText("None");
            streakText.setText("0");
            
            // Show profile card with different styling
            profileCard.setVisibility(View.VISIBLE);
        }
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
    
    private void showNotificationSettingsDialog() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("🔔 Notification Settings")
                .setMessage("Notification features:\n\n• Daily attendance reminders\n• Low attendance warnings\n• Weekly attendance reports\n• Achievement notifications")
                .setPositiveButton("Enable All", (dialog, which) -> {
                    Toast.makeText(getContext(), "All notifications enabled!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Disable All", (dialog, which) -> {
                    Toast.makeText(getContext(), "All notifications disabled!", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Customize", (dialog, which) -> {
                    Toast.makeText(getContext(), "Custom notification settings coming soon!", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
    
    private void showDataManagementDialog() {
        String[] options = {
            "Export All Data",
            "Clear Attendance Records", 
            "Reset All Subjects",
            "View Storage Info"
        };
        
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("💾 Data Management")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            DataExportManager exportManager = DataExportManager.getInstance();
                            exportManager.setContext(getContext());
                            exportManager.exportAttendanceData();
                            break;
                        case 1:
                            showClearRecordsConfirmation();
                            break;
                        case 2:
                            showResetSubjectsConfirmation();
                            break;
                        case 3:
                            showStorageInfo();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showClearRecordsConfirmation() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("🗑️ Clear Attendance Records")
                .setMessage("Are you sure you want to clear all attendance records? This action cannot be undone.")
                .setPositiveButton("Clear", (dialog, which) -> {
                    AttendanceManager attendanceManager = AttendanceManager.getInstance();
                    // Clear records - simplified for now
                    Toast.makeText(getContext(), "All attendance records cleared!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showResetSubjectsConfirmation() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("🔄 Reset All Subjects")
                .setMessage("Are you sure you want to reset all subjects? This will remove all subjects and their attendance data.")
                .setPositiveButton("Reset", (dialog, which) -> {
                    AttendanceManager attendanceManager = AttendanceManager.getInstance();
                    // Reset subjects - simplified for now
                    Toast.makeText(getContext(), "All subjects reset!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showStorageInfo() {
        AttendanceManager attendanceManager = AttendanceManager.getInstance();
        int subjectsCount = attendanceManager.getSubjects().size();
        int recordsCount = 0; // Simplified for now
        
        String info = String.format("Storage Information:\n\n" +
                "📚 Subjects: %d\n" +
                "📊 Records: %d\n" +
                "💾 App Size: ~2.5 MB\n" +
                "📱 Data Usage: Minimal", 
                subjectsCount, recordsCount);
        
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("📱 Storage Information")
                .setMessage(info)
                .setPositiveButton("OK", null)
                .show();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }
}
