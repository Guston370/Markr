package com.example.markr;

import java.util.Date;

public class AttendanceSession {
    public enum Status {
        PRESENT, ABSENT, NOT_CONDUCTED, UNMARKED
    }
    
    private String subjectName;
    private String dateString; // Format: YYYY-MM-DD
    private int sessionIndex; // 0-based index for sessions on the same day
    private Status status;
    private Date timestamp;
    
    public AttendanceSession() {
        this.status = Status.UNMARKED;
        this.timestamp = new Date();
    }
    
    public AttendanceSession(String subjectName, String dateString, int sessionIndex, Status status) {
        this();
        this.subjectName = subjectName;
        this.dateString = dateString;
        this.sessionIndex = sessionIndex;
        this.status = status;
    }
    
    // Helper method to convert string to Status
    public static Status fromString(String statusStr) {
        if (statusStr == null) return Status.UNMARKED;
        switch (statusStr.toLowerCase()) {
            case "present": return Status.PRESENT;
            case "absent": return Status.ABSENT;
            case "not_conducted": return Status.NOT_CONDUCTED;
            default: return Status.UNMARKED;
        }
    }
    
    // Helper method to convert Status to string
    public static String toString(Status status) {
        switch (status) {
            case PRESENT: return "present";
            case ABSENT: return "absent";
            case NOT_CONDUCTED: return "not_conducted";
            default: return null;
        }
    }
    
    // Getters and Setters
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public String getDateString() { return dateString; }
    public void setDateString(String dateString) { this.dateString = dateString; }
    
    public int getSessionIndex() { return sessionIndex; }
    public void setSessionIndex(int sessionIndex) { this.sessionIndex = sessionIndex; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { 
        this.status = status;
        this.timestamp = new Date();
    }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return String.format("%s - %s (Session %d): %s", 
            subjectName, dateString, sessionIndex + 1, status);
    }
}
