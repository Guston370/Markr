package com.example.markr;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserManager {
    private static UserManager instance;
    private User currentUser;
    private List<Achievement> achievements;
    private Context context;
    private static final String PREFS_NAME = "user_data";
    private static final String USER_KEY = "user";
    private static final String ACHIEVEMENTS_KEY = "achievements";
    private SimpleDateFormat dateFormat;

    private UserManager() {
        this.currentUser = new User();
        this.achievements = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        initializeAchievements();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
        loadUserData();
        loadAchievements();
    }

    private void initializeAchievements() {
        achievements.clear();
        
        achievements.add(new Achievement(
            "perfect_attendance",
            "Perfect Attendance",
            "Maintain 95%+ attendance",
            "🎯",
            "95%+ overall attendance",
            95,
            Achievement.AchievementType.PERFECT_ATTENDANCE
        ));
        
        achievements.add(new Achievement(
            "consistent_performer",
            "Consistent Performer",
            "80%+ attendance across 3+ subjects",
            "📈",
            "80%+ attendance with 3+ subjects",
            80,
            Achievement.AchievementType.CONSISTENT_PERFORMER
        ));
        
        achievements.add(new Achievement(
            "streak_master",
            "Streak Master",
            "10+ day attendance streak",
            "🔥",
            "10+ consecutive days",
            10,
            Achievement.AchievementType.STREAK_MASTER
        ));
        
        achievements.add(new Achievement(
            "early_bird",
            "Early Bird",
            "Track attendance for 30+ days",
            "🌅",
            "30+ days of tracking",
            30,
            Achievement.AchievementType.EARLY_BIRD
        ));
        
        achievements.add(new Achievement(
            "dedicated_student",
            "Dedicated Student",
            "20+ perfect attendance days",
            "⭐",
            "20+ perfect days",
            20,
            Achievement.AchievementType.DEDICATED_STUDENT
        ));
        
        achievements.add(new Achievement(
            "attendance_champion",
            "Attendance Champion",
            "90%+ attendance with 15+ day streak",
            "🏆",
            "90%+ attendance + 15+ streak",
            90,
            Achievement.AchievementType.ATTENDANCE_CHAMPION
        ));
    }

    // User Management
    public void registerUser(String name, String email, String studentId, 
                           String course, String semester, String college) {
        currentUser = new User(name, email, studentId, course, semester, college);
        currentUser.setFirstTime(false);
        saveUserData();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isUserRegistered() {
        return currentUser != null && !currentUser.isFirstTime();
    }

    public void updateUserStats(AttendanceManager attendanceManager) {
        if (currentUser == null) return;

        // Update overall attendance
        double overallAttendance = attendanceManager.getOverallAttendance();
        currentUser.setOverallAttendance(overallAttendance);

        // Update streak (simplified calculation)
        int streak = calculateStreak(attendanceManager);
        currentUser.updateStreak(streak);

        // Update achievements
        updateAchievements(attendanceManager);

        saveUserData();
        saveAchievements();
    }

    private int calculateStreak(AttendanceManager attendanceManager) {
        // Simplified streak calculation based on overall attendance
        double attendance = attendanceManager.getOverallAttendance();
        if (attendance >= 95) return 20;
        if (attendance >= 90) return 15;
        if (attendance >= 85) return 10;
        if (attendance >= 80) return 7;
        if (attendance >= 75) return 5;
        if (attendance >= 70) return 3;
        return 1;
    }

    // Achievement Management
    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements);
    }

    public List<Achievement> getUnlockedAchievements() {
        List<Achievement> unlocked = new ArrayList<>();
        for (Achievement achievement : achievements) {
            if (achievement.isUnlocked()) {
                unlocked.add(achievement);
            }
        }
        return unlocked;
    }

    public List<Achievement> getLockedAchievements() {
        List<Achievement> locked = new ArrayList<>();
        for (Achievement achievement : achievements) {
            if (!achievement.isUnlocked()) {
                locked.add(achievement);
            }
        }
        return locked;
    }

    private void updateAchievements(AttendanceManager attendanceManager) {
        for (Achievement achievement : achievements) {
            achievement.updateProgress(currentUser, attendanceManager);
        }
    }

    // Data Persistence
    public void saveUserData() {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        try {
            JSONObject userJson = new JSONObject();
            userJson.put("name", currentUser.getName());
            userJson.put("email", currentUser.getEmail());
            userJson.put("studentId", currentUser.getStudentId());
            userJson.put("course", currentUser.getCourse());
            userJson.put("semester", currentUser.getSemester());
            userJson.put("college", currentUser.getCollege());
            userJson.put("registrationDate", dateFormat.format(currentUser.getRegistrationDate()));
            userJson.put("totalDaysTracked", currentUser.getTotalDaysTracked());
            userJson.put("perfectDays", currentUser.getPerfectDays());
            userJson.put("currentStreak", currentUser.getCurrentStreak());
            userJson.put("longestStreak", currentUser.getLongestStreak());
            userJson.put("overallAttendance", currentUser.getOverallAttendance());
            userJson.put("isFirstTime", currentUser.isFirstTime());

            editor.putString(USER_KEY, userJson.toString());
            editor.apply();

            android.util.Log.d("UserManager", "User data saved successfully");

        } catch (Exception e) {
            android.util.Log.e("UserManager", "Error saving user data: " + e.getMessage());
        }
    }

    public void loadUserData() {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        try {
            String userJsonString = prefs.getString(USER_KEY, "");
            if (!userJsonString.isEmpty()) {
                JSONObject userJson = new JSONObject(userJsonString);
                
                currentUser = new User();
                currentUser.setName(userJson.getString("name"));
                currentUser.setEmail(userJson.getString("email"));
                currentUser.setStudentId(userJson.getString("studentId"));
                currentUser.setCourse(userJson.getString("course"));
                currentUser.setSemester(userJson.getString("semester"));
                currentUser.setCollege(userJson.getString("college"));
                currentUser.setTotalDaysTracked(userJson.getInt("totalDaysTracked"));
                currentUser.setPerfectDays(userJson.getInt("perfectDays"));
                currentUser.setCurrentStreak(userJson.getInt("currentStreak"));
                currentUser.setLongestStreak(userJson.getInt("longestStreak"));
                currentUser.setOverallAttendance(userJson.getDouble("overallAttendance"));
                currentUser.setFirstTime(userJson.getBoolean("isFirstTime"));

                android.util.Log.d("UserManager", "User data loaded successfully");
            }

        } catch (Exception e) {
            android.util.Log.e("UserManager", "Error loading user data: " + e.getMessage());
        }
    }

    public void saveAchievements() {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        try {
            JSONObject achievementsJson = new JSONObject();
            for (Achievement achievement : achievements) {
                JSONObject achievementJson = new JSONObject();
                achievementJson.put("id", achievement.getId());
                achievementJson.put("title", achievement.getTitle());
                achievementJson.put("description", achievement.getDescription());
                achievementJson.put("icon", achievement.getIcon());
                achievementJson.put("isUnlocked", achievement.isUnlocked());
                achievementJson.put("progress", achievement.getProgress());
                achievementJson.put("target", achievement.getTarget());
                achievementJson.put("type", achievement.getType().toString());
                
                achievementsJson.put(achievement.getId(), achievementJson);
            }

            editor.putString(ACHIEVEMENTS_KEY, achievementsJson.toString());
            editor.apply();

            android.util.Log.d("UserManager", "Achievements saved successfully");

        } catch (Exception e) {
            android.util.Log.e("UserManager", "Error saving achievements: " + e.getMessage());
        }
    }

    public void loadAchievements() {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        try {
            String achievementsJsonString = prefs.getString(ACHIEVEMENTS_KEY, "");
            if (!achievementsJsonString.isEmpty()) {
                JSONObject achievementsJson = new JSONObject(achievementsJsonString);
                
                for (Achievement achievement : achievements) {
                    if (achievementsJson.has(achievement.getId())) {
                        JSONObject achievementJson = achievementsJson.getJSONObject(achievement.getId());
                        achievement.setUnlocked(achievementJson.getBoolean("isUnlocked"));
                        achievement.setProgress(achievementJson.getInt("progress"));
                    }
                }

                android.util.Log.d("UserManager", "Achievements loaded successfully");
            }

        } catch (Exception e) {
            android.util.Log.e("UserManager", "Error loading achievements: " + e.getMessage());
        }
    }
}
