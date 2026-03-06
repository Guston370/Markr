package com.example.markr;

import java.util.Date;

public class User {
    private String name;
    private String email;
    private String studentId;
    private String course;
    private String semester;
    private String college;
    private Date registrationDate;
    private int totalDaysTracked;
    private int perfectDays;
    private int currentStreak;
    private int longestStreak;
    private double overallAttendance;
    private boolean isFirstTime;

    public User() {
        this.isFirstTime = true;
        this.totalDaysTracked = 0;
        this.perfectDays = 0;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.overallAttendance = 0.0;
        this.registrationDate = new Date();
    }

    public User(String name, String email, String studentId, String course, String semester, String college) {
        this();
        this.name = name;
        this.email = email;
        this.studentId = studentId;
        this.course = course;
        this.semester = semester;
        this.college = college;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }

    public int getTotalDaysTracked() { return totalDaysTracked; }
    public void setTotalDaysTracked(int totalDaysTracked) { this.totalDaysTracked = totalDaysTracked; }

    public int getPerfectDays() { return perfectDays; }
    public void setPerfectDays(int perfectDays) { this.perfectDays = perfectDays; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public double getOverallAttendance() { return overallAttendance; }
    public void setOverallAttendance(double overallAttendance) { this.overallAttendance = overallAttendance; }

    public boolean isFirstTime() { return isFirstTime; }
    public void setFirstTime(boolean firstTime) { this.isFirstTime = firstTime; }

    // Helper methods
    public void incrementDaysTracked() {
        this.totalDaysTracked++;
    }

    public void incrementPerfectDays() {
        this.perfectDays++;
    }

    public void updateStreak(int newStreak) {
        this.currentStreak = newStreak;
        if (newStreak > this.longestStreak) {
            this.longestStreak = newStreak;
        }
    }

    public double getPerfectDayPercentage() {
        if (totalDaysTracked == 0) return 0.0;
        return (double) perfectDays / totalDaysTracked * 100.0;
    }

    @Override
    public String toString() {
        return String.format("User: %s (%s) - %s %s", name, studentId, course, semester);
    }
}
