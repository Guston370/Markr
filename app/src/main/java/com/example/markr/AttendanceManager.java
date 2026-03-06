package com.example.markr;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;
import java.text.SimpleDateFormat;

public class AttendanceManager {
    private static AttendanceManager instance;
    private List<Subject> subjects;
    private Map<String, Map<String, List<AttendanceSession.Status>>> attendanceData;
    private SimpleDateFormat dateFormat;
    private Context context;
    private static final String PREFS_NAME = "attendance_data";
    private static final String SUBJECTS_KEY = "subjects";
    private static final String ATTENDANCE_KEY = "attendance";
    
    private AttendanceManager() {
        this.subjects = new ArrayList<>();
        this.attendanceData = new HashMap<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
    
    public static AttendanceManager getInstance() {
        if (instance == null) {
            instance = new AttendanceManager();
        }
        return instance;
    }
    
    public void setContext(Context context) {
        this.context = context;
        loadData();
    }
    
    // Subject Management
    public void addSubject(Subject subject) {
        if (subject != null && !isDuplicateSubject(subject.getName())) {
            subjects.add(subject);
            attendanceData.put(subject.getName(), new HashMap<>());
            saveData();
        }
    }
    
    public void removeSubject(String subjectName) {
        subjects.removeIf(subject -> subject.getName().equals(subjectName));
        attendanceData.remove(subjectName);
        saveData();
    }
    
    public List<Subject> getSubjects() {
        return new ArrayList<>(subjects);
    }
    
    public Subject getSubject(String name) {
        for (Subject subject : subjects) {
            if (subject.getName().equals(name)) {
                return subject;
            }
        }
        return null;
    }
    
    private boolean isDuplicateSubject(String name) {
        return subjects.stream().anyMatch(subject -> 
            subject.getName().toLowerCase().equals(name.toLowerCase()));
    }
    
    // Attendance Management
    public void markAttendance(String subjectName, String dateString, 
                              AttendanceSession.Status status, int sessionIndex) {
        Subject subject = getSubject(subjectName);
        if (subject == null) return;
        
        // Validate session index
        String dayName = getDayNameFromDate(dateString);
        if (!subject.hasLectureOnDay(dayName) || sessionIndex >= subject.getSessionsForDay(dayName)) {
            return;
        }
        
        Map<String, List<AttendanceSession.Status>> subjectAttendance = 
            attendanceData.getOrDefault(subjectName, new HashMap<>());
        List<AttendanceSession.Status> daySessions = 
            subjectAttendance.getOrDefault(dateString, new ArrayList<>());
        
        // Ensure list is large enough
        while (daySessions.size() <= sessionIndex) {
            daySessions.add(AttendanceSession.Status.UNMARKED);
        }
        
        daySessions.set(sessionIndex, status);
        subjectAttendance.put(dateString, daySessions);
        attendanceData.put(subjectName, subjectAttendance);
        
        updateSubjectStats(subject);
        saveData();
    }
    
    public AttendanceSession.Status getAttendanceStatus(String subjectName, String dateString, int sessionIndex) {
        Map<String, List<AttendanceSession.Status>> subjectAttendance = attendanceData.get(subjectName);
        if (subjectAttendance == null) return AttendanceSession.Status.UNMARKED;
        
        List<AttendanceSession.Status> daySessions = subjectAttendance.get(dateString);
        if (daySessions == null || sessionIndex >= daySessions.size()) {
            return AttendanceSession.Status.UNMARKED;
        }
        
        return daySessions.get(sessionIndex);
    }
    
    private void updateSubjectStats(Subject subject) {
        Map<String, List<AttendanceSession.Status>> subjectAttendance = 
            attendanceData.get(subject.getName());
        if (subjectAttendance == null) return;
        
        int totalSessions = 0;
        int attendedSessions = 0;
        
        for (Map.Entry<String, List<AttendanceSession.Status>> entry : subjectAttendance.entrySet()) {
            String dateString = entry.getKey();
            List<AttendanceSession.Status> sessions = entry.getValue();
            String dayName = getDayNameFromDate(dateString);
            
            if (subject.hasLectureOnDay(dayName)) {
                int maxSessions = subject.getSessionsForDay(dayName);
                for (int i = 0; i < Math.min(sessions.size(), maxSessions); i++) {
                    AttendanceSession.Status status = sessions.get(i);
                    if (status == AttendanceSession.Status.PRESENT || 
                        status == AttendanceSession.Status.ABSENT) {
                        totalSessions++;
                        if (status == AttendanceSession.Status.PRESENT) {
                            attendedSessions++;
                        }
                    }
                }
            }
        }
        
        subject.setTotalSessions(totalSessions);
        subject.setAttendedSessions(attendedSessions);
    }
    
    // Calendar and Date Utilities
    public String getDayNameFromDate(String dateString) {
        try {
            Date date = dateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            return dayNames[dayOfWeek - 1];
        } catch (Exception e) {
            return "";
        }
    }
    
    public String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return dateFormat.format(calendar.getTime());
    }
    
    public List<String> getLecturesForDate(String dateString) {
        String dayName = getDayNameFromDate(dateString);
        List<String> lectures = new ArrayList<>();
        
        for (Subject subject : subjects) {
            if (subject.hasLectureOnDay(dayName)) {
                lectures.add(subject.getName());
            }
        }
        
        return lectures;
    }
    
    // Attendance Calculations
    public AttendanceAdvice getAttendanceAdvice(String subjectName) {
        Subject subject = getSubject(subjectName);
        if (subject == null) return new AttendanceAdvice("", false);
        
        double percentage = subject.getAttendancePercentage();
        int present = subject.getAttendedSessions();
        int total = subject.getTotalSessions();
        
        if (total == 0) {
            return new AttendanceAdvice("No attendance data available", false);
        }
        
        if (percentage >= 75.0) {
            // Calculate how many can be missed
            int canMiss = (int) Math.floor((present - 0.75 * total) / 0.75);
            if (canMiss > 0) {
                String message = String.format("You can miss %d more session%s and stay above 75%%", 
                    canMiss, canMiss > 1 ? "s" : "");
                return new AttendanceAdvice(message, true);
            } else {
                return new AttendanceAdvice("Don't miss any more sessions to stay above 75%", false);
            }
        } else {
            // Calculate how many need to be attended
            int needed = (int) Math.ceil((0.75 * total - present) / 0.25);
            String message = String.format("Attend %d more session%s to reach 75%%", 
                needed, needed > 1 ? "s" : "");
            return new AttendanceAdvice(message, false);
        }
    }
    
    public double getOverallAttendance() {
        int totalPresent = 0;
        int totalSessions = 0;
        
        for (Subject subject : subjects) {
            totalPresent += subject.getAttendedSessions();
            totalSessions += subject.getTotalSessions();
        }
        
        return totalSessions > 0 ? (double) totalPresent / totalSessions * 100.0 : 0.0;
    }
    
    // Data Persistence
    public void saveData() {
        if (context == null) return;
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        try {
            // Save subjects
            JSONArray subjectsArray = new JSONArray();
            for (Subject subject : subjects) {
                JSONObject subjectJson = new JSONObject();
                subjectJson.put("name", subject.getName());
                
                JSONArray weekdaysArray = new JSONArray();
                for (String weekday : subject.getWeekdays()) {
                    weekdaysArray.put(weekday);
                }
                subjectJson.put("weekdays", weekdaysArray);
                
                JSONObject sessionsPerDayJson = new JSONObject();
                for (Map.Entry<String, Integer> entry : subject.getSessionsPerDay().entrySet()) {
                    sessionsPerDayJson.put(entry.getKey(), entry.getValue());
                }
                subjectJson.put("sessionsPerDay", sessionsPerDayJson);
                
                subjectJson.put("totalSessions", subject.getTotalSessions());
                subjectJson.put("attendedSessions", subject.getAttendedSessions());
                
                subjectsArray.put(subjectJson);
            }
            editor.putString(SUBJECTS_KEY, subjectsArray.toString());
            
            // Save attendance data
            JSONObject attendanceJson = new JSONObject();
            for (Map.Entry<String, Map<String, List<AttendanceSession.Status>>> subjectEntry : attendanceData.entrySet()) {
                JSONObject subjectAttendanceJson = new JSONObject();
                for (Map.Entry<String, List<AttendanceSession.Status>> dateEntry : subjectEntry.getValue().entrySet()) {
                    JSONArray sessionsArray = new JSONArray();
                    for (AttendanceSession.Status status : dateEntry.getValue()) {
                        sessionsArray.put(status.toString());
                    }
                    subjectAttendanceJson.put(dateEntry.getKey(), sessionsArray);
                }
                attendanceJson.put(subjectEntry.getKey(), subjectAttendanceJson);
            }
            editor.putString(ATTENDANCE_KEY, attendanceJson.toString());
            
            editor.apply();
            android.util.Log.d("AttendanceManager", "Data saved successfully");
            
        } catch (Exception e) {
            android.util.Log.e("AttendanceManager", "Error saving data: " + e.getMessage());
        }
    }
    
    public void loadData() {
        if (context == null) return;
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        try {
            // Load subjects
            String subjectsJsonString = prefs.getString(SUBJECTS_KEY, "");
            if (!subjectsJsonString.isEmpty()) {
                JSONArray subjectsArray = new JSONArray(subjectsJsonString);
                subjects.clear();
                
                for (int i = 0; i < subjectsArray.length(); i++) {
                    JSONObject subjectJson = subjectsArray.getJSONObject(i);
                    
                    String name = subjectJson.getString("name");
                    
                    JSONArray weekdaysArray = subjectJson.getJSONArray("weekdays");
                    List<String> weekdays = new ArrayList<>();
                    for (int j = 0; j < weekdaysArray.length(); j++) {
                        weekdays.add(weekdaysArray.getString(j));
                    }
                    
                    JSONObject sessionsPerDayJson = subjectJson.getJSONObject("sessionsPerDay");
                    Map<String, Integer> sessionsPerDay = new HashMap<>();
                    Iterator<String> keys = sessionsPerDayJson.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        sessionsPerDay.put(key, sessionsPerDayJson.getInt(key));
                    }
                    
                    Subject subject = new Subject(name, weekdays, sessionsPerDay);
                    subject.setTotalSessions(subjectJson.getInt("totalSessions"));
                    subject.setAttendedSessions(subjectJson.getInt("attendedSessions"));
                    
                    subjects.add(subject);
                }
            }
            
            // Load attendance data
            String attendanceJsonString = prefs.getString(ATTENDANCE_KEY, "");
            if (!attendanceJsonString.isEmpty()) {
                JSONObject attendanceJson = new JSONObject(attendanceJsonString);
                attendanceData.clear();
                
                Iterator<String> subjectKeys = attendanceJson.keys();
                while (subjectKeys.hasNext()) {
                    String subjectName = subjectKeys.next();
                    JSONObject subjectAttendanceJson = attendanceJson.getJSONObject(subjectName);
                    
                    Map<String, List<AttendanceSession.Status>> subjectAttendance = new HashMap<>();
                    Iterator<String> dateKeys = subjectAttendanceJson.keys();
                    while (dateKeys.hasNext()) {
                        String dateString = dateKeys.next();
                        JSONArray sessionsArray = subjectAttendanceJson.getJSONArray(dateString);
                        
                        List<AttendanceSession.Status> sessions = new ArrayList<>();
                        for (int i = 0; i < sessionsArray.length(); i++) {
                            String statusString = sessionsArray.getString(i);
                            AttendanceSession.Status status = AttendanceSession.Status.valueOf(statusString);
                            sessions.add(status);
                        }
                        
                        subjectAttendance.put(dateString, sessions);
                    }
                    
                    attendanceData.put(subjectName, subjectAttendance);
                }
            }
            
            android.util.Log.d("AttendanceManager", "Data loaded successfully: " + subjects.size() + " subjects");
            
        } catch (Exception e) {
            android.util.Log.e("AttendanceManager", "Error loading data: " + e.getMessage());
        }
    }
    
    // Helper class for attendance advice
    public static class AttendanceAdvice {
        private String message;
        private boolean isGood;
        
        public AttendanceAdvice(String message, boolean isGood) {
            this.message = message;
            this.isGood = isGood;
        }
        
        public String getMessage() { return message; }
        public boolean isGood() { return isGood; }
    }
}
