package com.example.markr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class DataExportManager {
    private static DataExportManager instance;
    private Context context;

    private DataExportManager() {}

    public static synchronized DataExportManager getInstance() {
        if (instance == null) {
            instance = new DataExportManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void exportAttendanceData() {
        if (context == null) {
            Toast.makeText(context, "Context not available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Get attendance data
            AttendanceManager attendanceManager = AttendanceManager.getInstance();
            List<Subject> subjects = attendanceManager.getSubjects();
            List<AttendanceRecord> records = new ArrayList<>(); // Simplified for now

            // Create CSV content
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("Markr Attendance Export\n");
            csvContent.append("Generated on: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n\n");

            // Export subjects
            csvContent.append("=== SUBJECTS ===\n");
            csvContent.append("Subject Name,Weekdays,Sessions Per Day,Total Sessions,Attended Sessions,Attendance %\n");
            
            for (Subject subject : subjects) {
                List<AttendanceSession> sessions = new ArrayList<>(); // Simplified for now
                int totalSessions = sessions.size();
                int attendedSessions = 0;
                
                for (AttendanceSession session : sessions) {
                    if (session.getStatus() == AttendanceSession.Status.PRESENT) {
                        attendedSessions++;
                    }
                }
                
                double attendancePercentage = totalSessions > 0 ? (double) attendedSessions / totalSessions * 100 : 0;
                
                csvContent.append(subject.getName()).append(",");
                csvContent.append(String.join(";", subject.getWeekdays())).append(",");
                csvContent.append(subject.getSessionsPerWeek()).append(",");
                csvContent.append(totalSessions).append(",");
                csvContent.append(attendedSessions).append(",");
                csvContent.append(String.format(Locale.getDefault(), "%.1f", attendancePercentage)).append("\n");
            }

            // Export attendance records
            csvContent.append("\n=== ATTENDANCE RECORDS ===\n");
            csvContent.append("Subject,Total Classes,Attended Classes,Attendance %,Date Saved\n");
            
            for (AttendanceRecord record : records) {
                csvContent.append(record.getSubject()).append(",");
                csvContent.append(record.getTotalClasses()).append(",");
                csvContent.append(record.getAttendedClasses()).append(",");
                csvContent.append(String.format(Locale.getDefault(), "%.1f", record.getAttendancePercentage())).append(",");
                csvContent.append(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())).append("\n");
            }

            // Save to file
            String fileName = "Markr_Attendance_Export_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";
            File exportFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            
            FileWriter writer = new FileWriter(exportFile);
            writer.write(csvContent.toString());
            writer.close();

            // Share the file
            shareFile(exportFile, fileName);

        } catch (IOException e) {
            Toast.makeText(context, "Failed to export data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void shareFile(File file, String fileName) {
        try {
            Uri fileUri = Uri.fromFile(file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/csv");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Markr Attendance Export");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Here's my attendance data exported from Markr app.");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "Share Attendance Data"));
            Toast.makeText(context, "Data exported successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to share file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void exportUserProfile() {
        if (context == null) {
            Toast.makeText(context, "Context not available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            UserManager userManager = UserManager.getInstance();
            User user = userManager.getCurrentUser();
            
            if (user == null) {
                Toast.makeText(context, "No user data found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create profile export content
            StringBuilder profileContent = new StringBuilder();
            profileContent.append("Markr User Profile Export\n");
            profileContent.append("Generated on: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n\n");

            profileContent.append("=== USER PROFILE ===\n");
            profileContent.append("Name: ").append(user.getName()).append("\n");
            profileContent.append("Email: ").append(user.getEmail()).append("\n");
            profileContent.append("Student ID: ").append(user.getStudentId()).append("\n");
            profileContent.append("Course: ").append(user.getCourse()).append("\n");
            profileContent.append("Semester: ").append(user.getSemester()).append("\n");
            profileContent.append("College: ").append(user.getCollege()).append("\n");
            profileContent.append("Registration Date: ").append(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())).append("\n");

            // Save to file
            String fileName = "Markr_Profile_Export_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".txt";
            File exportFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            
            FileWriter writer = new FileWriter(exportFile);
            writer.write(profileContent.toString());
            writer.close();

            // Share the file
            shareFile(exportFile, fileName);

        } catch (IOException e) {
            Toast.makeText(context, "Failed to export profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
