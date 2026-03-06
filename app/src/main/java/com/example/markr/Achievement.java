package com.example.markr;

public class Achievement {
    private String id;
    private String title;
    private String description;
    private String icon;
    private boolean isUnlocked;
    private String unlockCondition;
    private int progress;
    private int target;
    private AchievementType type;

    public enum AchievementType {
        PERFECT_ATTENDANCE,
        CONSISTENT_PERFORMER,
        STREAK_MASTER,
        EARLY_BIRD,
        DEDICATED_STUDENT,
        ATTENDANCE_CHAMPION
    }

    public Achievement(String id, String title, String description, String icon, 
                      String unlockCondition, int target, AchievementType type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.unlockCondition = unlockCondition;
        this.target = target;
        this.type = type;
        this.isUnlocked = false;
        this.progress = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { this.isUnlocked = unlocked; }

    public String getUnlockCondition() { return unlockCondition; }
    public void setUnlockCondition(String unlockCondition) { this.unlockCondition = unlockCondition; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public int getTarget() { return target; }
    public void setTarget(int target) { this.target = target; }

    public AchievementType getType() { return type; }
    public void setType(AchievementType type) { this.type = type; }

    // Helper methods
    public double getProgressPercentage() {
        if (target == 0) return 0.0;
        return Math.min(100.0, (double) progress / target * 100.0);
    }

    public boolean checkUnlockCondition(User user, AttendanceManager attendanceManager) {
        switch (type) {
            case PERFECT_ATTENDANCE:
                return user.getOverallAttendance() >= 95.0;
            case CONSISTENT_PERFORMER:
                return attendanceManager.getSubjects().size() >= 3 && user.getOverallAttendance() >= 80.0;
            case STREAK_MASTER:
                return user.getCurrentStreak() >= 10;
            case EARLY_BIRD:
                return user.getTotalDaysTracked() >= 30;
            case DEDICATED_STUDENT:
                return user.getPerfectDays() >= 20;
            case ATTENDANCE_CHAMPION:
                return user.getOverallAttendance() >= 90.0 && user.getCurrentStreak() >= 15;
            default:
                return false;
        }
    }

    public void updateProgress(User user, AttendanceManager attendanceManager) {
        switch (type) {
            case PERFECT_ATTENDANCE:
                progress = (int) user.getOverallAttendance();
                break;
            case CONSISTENT_PERFORMER:
                progress = Math.min(100, (int) user.getOverallAttendance());
                break;
            case STREAK_MASTER:
                progress = user.getCurrentStreak();
                break;
            case EARLY_BIRD:
                progress = user.getTotalDaysTracked();
                break;
            case DEDICATED_STUDENT:
                progress = user.getPerfectDays();
                break;
            case ATTENDANCE_CHAMPION:
                progress = Math.min(100, (int) user.getOverallAttendance());
                break;
        }
        
        isUnlocked = checkUnlockCondition(user, attendanceManager);
    }
}
