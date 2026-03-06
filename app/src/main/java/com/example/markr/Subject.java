package com.example.markr;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Subject {
    private String name;
    private List<String> weekdays;
    private Map<String, Integer> sessionsPerDay;
    private int totalSessions;
    private int attendedSessions;
    private double attendancePercentage;
    private String attendanceAdvice;

    public Subject() {
        this.weekdays = new ArrayList<>();
        this.sessionsPerDay = new HashMap<>();
        this.totalSessions = 0;
        this.attendedSessions = 0;
    }

    public Subject(String name, List<String> weekdays, Map<String, Integer> sessionsPerDay) {
        this();
        this.name = name;
        this.weekdays = weekdays != null ? weekdays : new ArrayList<>();
        this.sessionsPerDay = sessionsPerDay != null ? sessionsPerDay : new HashMap<>();
        calculateTotalSessions();
    }

    public void calculateTotalSessions() {
        totalSessions = 0;
        for (String weekday : weekdays) {
            totalSessions += sessionsPerDay.getOrDefault(weekday, 1);
        }
    }

    public double calculateAttendancePercentage() {
        if (totalSessions == 0) return 0.0;
        return (double) attendedSessions / totalSessions * 100.0;
    }

    public String calculateAttendanceAdvice() {
        double percentage = calculateAttendancePercentage();
        
        if (percentage >= 75.0) {
            // Calculate how many sessions can be missed
            int canMiss = (int) Math.floor((attendedSessions - 0.75 * totalSessions) / 0.75);
            if (canMiss > 0) {
                return String.format("You can miss %d more session%s and stay above 75%%", 
                    canMiss, canMiss > 1 ? "s" : "");
            } else {
                return "Don't miss any more sessions to stay above 75%";
            }
        } else {
            // Calculate how many sessions need to be attended
            int needed = (int) Math.ceil((0.75 * totalSessions - attendedSessions) / 0.25);
            return String.format("Attend %d more session%s to reach 75%%", 
                needed, needed > 1 ? "s" : "");
        }
    }

    public boolean hasLectureOnDay(String dayName) {
        return weekdays.contains(dayName);
    }

    public int getSessionsForDay(String dayName) {
        return sessionsPerDay.getOrDefault(dayName, 0);
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getWeekdays() { return weekdays; }
    public void setWeekdays(List<String> weekdays) { 
        this.weekdays = weekdays != null ? weekdays : new ArrayList<>();
        calculateTotalSessions();
    }

    public Map<String, Integer> getSessionsPerDay() { return sessionsPerDay; }
    public void setSessionsPerDay(Map<String, Integer> sessionsPerDay) { 
        this.sessionsPerDay = sessionsPerDay != null ? sessionsPerDay : new HashMap<>();
        calculateTotalSessions();
    }

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

    public int getAttendedSessions() { return attendedSessions; }
    public void setAttendedSessions(int attendedSessions) { 
        this.attendedSessions = attendedSessions;
        this.attendancePercentage = calculateAttendancePercentage();
        this.attendanceAdvice = calculateAttendanceAdvice();
    }

    public double getAttendancePercentage() { 
        this.attendancePercentage = calculateAttendancePercentage();
        return attendancePercentage; 
    }

    public String getAttendanceAdvice() { 
        this.attendanceAdvice = calculateAttendanceAdvice();
        return attendanceAdvice; 
    }

    public int getSessionsPerWeek() { 
        int total = 0;
        for (String weekday : weekdays) {
            total += sessionsPerDay.getOrDefault(weekday, 1);
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("%s: %.1f%% (%d/%d sessions)", 
            name, getAttendancePercentage(), attendedSessions, totalSessions);
    }
}
