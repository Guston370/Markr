package com.example.markr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;

public class AttendanceRecord {
    private String subject;
    private int totalClasses;
    private int attendedClasses;
    private double minAttendance;
    private double attendancePercentage;
    private String dateCreated;
    private long id;
    
    // New fields for advanced features
    private List<String> weekdays;
    private int sessionsPerWeek;
    private String attendanceAdvice;

    public AttendanceRecord() {
        this.dateCreated = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        this.weekdays = new ArrayList<>();
        this.sessionsPerWeek = 1;
    }

    public AttendanceRecord(String subject, int totalClasses, int attendedClasses, double minAttendance) {
        this();
        this.subject = subject;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
        this.minAttendance = minAttendance;
        this.attendancePercentage = calculatePercentage();
        this.attendanceAdvice = calculateAdvice();
    }

    public double calculatePercentage() {
        if (totalClasses == 0) return 0.0;
        return (double) attendedClasses / totalClasses * 100.0;
    }

    public int getClassesNeeded() {
        if (minAttendance <= 0) return 0;
        double requiredAttended = Math.ceil(totalClasses * minAttendance / 100.0);
        return Math.max(0, (int) requiredAttended - attendedClasses);
    }

    public int getClassesCanMiss() {
        if (minAttendance <= 0) return totalClasses;
        double maxCanMiss = Math.floor(totalClasses * (100 - minAttendance) / 100.0);
        return Math.max(0, (int) maxCanMiss - (totalClasses - attendedClasses));
    }
    
    public String calculateAdvice() {
        double percentage = getAttendancePercentage();
        int needed = getClassesNeeded();
        int canMiss = getClassesCanMiss();
        
        if (percentage >= minAttendance) {
            if (canMiss > 0) {
                return String.format("You can miss %d more classes and stay above %.1f%%", canMiss, minAttendance);
            } else {
                return "Perfect! Don't miss any more classes to maintain your attendance.";
            }
        } else {
            return String.format("Attend %d more classes to reach %.1f%%", needed, minAttendance);
        }
    }

    // Getters and Setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public int getTotalClasses() { return totalClasses; }
    public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }

    public int getAttendedClasses() { return attendedClasses; }
    public void setAttendedClasses(int attendedClasses) { this.attendedClasses = attendedClasses; }

    public double getMinAttendance() { return minAttendance; }
    public void setMinAttendance(double minAttendance) { this.minAttendance = minAttendance; }

    public double getAttendancePercentage() { 
        this.attendancePercentage = calculatePercentage();
        return attendancePercentage; 
    }

    public String getDateCreated() { return dateCreated; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public List<String> getWeekdays() { return weekdays; }
    public void setWeekdays(List<String> weekdays) { this.weekdays = weekdays; }

    public int getSessionsPerWeek() { return sessionsPerWeek; }
    public void setSessionsPerWeek(int sessionsPerWeek) { this.sessionsPerWeek = sessionsPerWeek; }

    public String getAttendanceAdvice() { 
        this.attendanceAdvice = calculateAdvice();
        return attendanceAdvice; 
    }
    public void setAttendanceAdvice(String attendanceAdvice) { this.attendanceAdvice = attendanceAdvice; }

    @Override
    public String toString() {
        return String.format("%s: %.1f%% (%d/%d)", subject, getAttendancePercentage(), attendedClasses, totalClasses);
    }
}
