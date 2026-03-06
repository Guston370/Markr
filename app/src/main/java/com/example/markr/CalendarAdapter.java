package com.example.markr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private List<CalendarDay> calendarDays;
    private OnDateClickListener listener;
    private int selectedDay = -1;
    private int currentYear;
    private int currentMonth;

    public interface OnDateClickListener {
        void onDateClick(int day, int month, int year);
    }

    public CalendarAdapter(OnDateClickListener listener) {
        this.listener = listener;
        this.calendarDays = new ArrayList<>();
        this.attendanceManager = AttendanceManager.getInstance();
    }

    public void updateCalendar(int year, int month, List<Subject> subjects) {
        this.currentYear = year;
        this.currentMonth = month;
        calendarDays.clear();
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        
        // Get first day of month and number of days
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Add empty days for alignment
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarDays.add(new CalendarDay(0, false, false));
        }
        
        // Add days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            boolean hasLecture = hasLectureOnDay(day, month, year, subjects);
            boolean isToday = isToday(day, month, year);
            List<Subject> subjectsForDay = getSubjectsForDay(day, month, year, subjects);
            String attendanceStatus = getAttendanceStatusForDay(day, month, year, subjects);
            calendarDays.add(new CalendarDay(day, hasLecture, isToday, subjectsForDay, attendanceStatus));
        }
        
        notifyDataSetChanged();
        
        // Debug: Log calendar generation
        android.util.Log.d("CalendarAdapter", "Generated " + calendarDays.size() + " calendar days for " + 
            new java.text.DateFormatSymbols().getMonths()[month] + " " + year);
    }

    private boolean hasLectureOnDay(int day, int month, int year, List<Subject> subjects) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        String dayName = getDayName(calendar.get(Calendar.DAY_OF_WEEK));
        
        for (Subject subject : subjects) {
            if (subject.hasLectureOnDay(dayName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isToday(int day, int month, int year) {
        Calendar today = Calendar.getInstance();
        Calendar checkDate = Calendar.getInstance();
        checkDate.set(year, month, day);
        
        return today.get(Calendar.YEAR) == checkDate.get(Calendar.YEAR) &&
               today.get(Calendar.MONTH) == checkDate.get(Calendar.MONTH) &&
               today.get(Calendar.DAY_OF_MONTH) == checkDate.get(Calendar.DAY_OF_MONTH);
    }

    private String getDayName(int dayOfWeek) {
        String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[dayOfWeek];
    }
    
    private List<Subject> getSubjectsForDay(int day, int month, int year, List<Subject> subjects) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        String dayName = getDayName(calendar.get(Calendar.DAY_OF_WEEK));
        
        List<Subject> subjectsForDay = new ArrayList<>();
        for (Subject subject : subjects) {
            if (subject.hasLectureOnDay(dayName)) {
                subjectsForDay.add(subject);
            }
        }
        return subjectsForDay;
    }
    
    private String getAttendanceStatusForDay(int day, int month, int year, List<Subject> subjects) {
        // Get attendance status from AttendanceManager
        String dateStr = String.format("%d-%02d-%02d", year, month + 1, day);
        
        // Check if any subject has attendance marked for this day
        for (Subject subject : subjects) {
            String dayName = getDayNameFromDate(dateStr);
            if (subject.hasLectureOnDay(dayName)) {
                // Check all sessions for this subject on this day
                int sessionsForDay = subject.getSessionsForDay(dayName);
                for (int sessionIndex = 0; sessionIndex < sessionsForDay; sessionIndex++) {
                    AttendanceSession.Status status = attendanceManager.getAttendanceStatus(
                        subject.getName(), dateStr, sessionIndex);
                    if (status != AttendanceSession.Status.UNMARKED) {
                        // Return the most recent status (or could be more sophisticated)
                        return status.toString().toLowerCase();
                    }
                }
            }
        }
        return null;
    }
    
    private AttendanceManager attendanceManager;
    
    private String getDayNameFromDate(String dateString) {
        try {
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Date date = dateFormat.parse(dateString);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(date);
            
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            return dayNames[dayOfWeek - 1];
        } catch (Exception e) {
            return "";
        }
    }

    public void setSelectedDay(int day) {
        selectedDay = day;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_day_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDay day = calendarDays.get(position);
        holder.bind(day, position == selectedDay, listener);
        
        // Debug: Log each day being bound
        if (day.getDay() > 0) {
            android.util.Log.d("CalendarAdapter", "Binding day " + day.getDay() + " at position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return calendarDays.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        private TextView dayNumber;
        private View subjectIndicator;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayNumber = itemView.findViewById(R.id.dayNumber);
            subjectIndicator = itemView.findViewById(R.id.subjectIndicator);
        }

        public void bind(CalendarDay day, boolean isSelected, OnDateClickListener listener) {
            if (day.getDay() == 0) {
                dayNumber.setText("");
                subjectIndicator.setVisibility(View.GONE);
                itemView.setClickable(false);
                itemView.setAlpha(0.3f);
                // Set background for empty days
                itemView.setBackgroundResource(R.drawable.calendar_normal_border);
                return;
            }

            dayNumber.setText(String.valueOf(day.getDay()));
            itemView.setAlpha(1.0f);
            
            // Show subject indicator if there are lectures
            if (day.hasLecture()) {
                subjectIndicator.setVisibility(View.VISIBLE);
            } else {
                subjectIndicator.setVisibility(View.GONE);
            }

            // Set background based on selection and today status
            if (isSelected) {
                // Selected date - blue border
                itemView.setBackgroundResource(R.drawable.calendar_selected_border);
                dayNumber.setTextColor(itemView.getContext().getResources().getColor(R.color.calendar_selected_border));
            } else if (day.isToday()) {
                // Today - blue background with border
                itemView.setBackgroundResource(R.drawable.calendar_today_border);
                dayNumber.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } else {
                // Normal day - light border
                itemView.setBackgroundResource(R.drawable.calendar_normal_border);
                dayNumber.setTextColor(itemView.getContext().getResources().getColor(R.color.primary_text));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null && day.getDay() > 0) {
                    listener.onDateClick(day.getDay(), currentMonth, currentYear);
                }
            });
        }
    }

    static class CalendarDay {
        private int day;
        private boolean hasLecture;
        private boolean isToday;
        private List<Subject> subjectsForDay;
        private String attendanceStatus;

        public CalendarDay(int day, boolean hasLecture, boolean isToday) {
            this.day = day;
            this.hasLecture = hasLecture;
            this.isToday = isToday;
            this.subjectsForDay = new ArrayList<>();
            this.attendanceStatus = null;
        }

        public CalendarDay(int day, boolean hasLecture, boolean isToday, List<Subject> subjectsForDay, String attendanceStatus) {
            this.day = day;
            this.hasLecture = hasLecture;
            this.isToday = isToday;
            this.subjectsForDay = subjectsForDay != null ? subjectsForDay : new ArrayList<>();
            this.attendanceStatus = attendanceStatus;
        }

        public int getDay() { return day; }
        public boolean hasLecture() { return hasLecture; }
        public boolean isToday() { return isToday; }
        public List<Subject> getSubjectsForDay() { return subjectsForDay; }
        public String getAttendanceStatus() { return attendanceStatus; }
    }
}