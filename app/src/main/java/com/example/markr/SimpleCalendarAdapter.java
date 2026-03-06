package com.example.markr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SimpleCalendarAdapter extends BaseAdapter {
    
    private Context context;
    private List<CalendarDay> calendarDays;
    private OnDateClickListener listener;
    private int currentYear;
    private int currentMonth;
    private int selectedDay = -1;
    
    public interface OnDateClickListener {
        void onDateClick(int day, int month, int year);
    }
    
    public SimpleCalendarAdapter(Context context, OnDateClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.calendarDays = new ArrayList<>();
    }
    
    public void setSelectedDay(int day) {
        selectedDay = day;
        notifyDataSetChanged();
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
            calendarDays.add(new CalendarDay(day, hasLecture, isToday));
        }
        
        notifyDataSetChanged();
        
        // Debug: Log calendar generation
        android.util.Log.d("SimpleCalendarAdapter", "Generated " + calendarDays.size() + " calendar days for " + 
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
    
    @Override
    public int getCount() {
        return calendarDays.size();
    }
    
    @Override
    public Object getItem(int position) {
        return calendarDays.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.simple_calendar_day_item, parent, false);
            holder = new ViewHolder();
            holder.dayNumber = convertView.findViewById(R.id.dayNumber);
            holder.subjectIndicator = convertView.findViewById(R.id.subjectIndicator);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        CalendarDay day = calendarDays.get(position);
        
        if (day.getDay() == 0) {
            holder.dayNumber.setText("");
            holder.subjectIndicator.setVisibility(View.INVISIBLE);
            convertView.setClickable(false);
            convertView.setAlpha(0.3f);
            // Set background for empty days
            convertView.setBackgroundResource(R.drawable.calendar_normal_border);
        } else {
            holder.dayNumber.setText(String.valueOf(day.getDay()));
            convertView.setAlpha(1.0f);
            convertView.setClickable(true);
            
            // Show subject indicator if there's a lecture
            if (day.hasLecture()) {
                holder.subjectIndicator.setVisibility(View.VISIBLE);
            } else {
                holder.subjectIndicator.setVisibility(View.INVISIBLE);
            }
            
            // Set background and text color based on selection and today status
            boolean isSelected = (day.getDay() == selectedDay);
            
            if (isSelected && day.isToday()) {
                // Today's date when selected - light blue background with dark text
                convertView.setBackgroundResource(R.drawable.calendar_today_border);
                holder.dayNumber.setTextColor(context.getResources().getColor(R.color.primary_text));
                holder.dayNumber.setTypeface(null, android.graphics.Typeface.BOLD);
                holder.dayNumber.setShadowLayer(0, 0, 0, 0); // Clear shadow
            } else if (isSelected) {
                // Selected date (not today) - light blue background with blue border
                convertView.setBackgroundResource(R.drawable.calendar_selected_border);
                holder.dayNumber.setTextColor(context.getResources().getColor(R.color.primary_text));
                holder.dayNumber.setTypeface(null, android.graphics.Typeface.BOLD);
                holder.dayNumber.setShadowLayer(0, 0, 0, 0); // Clear shadow
            } else if (day.isToday()) {
                // Today (not selected) - light blue background with dark text
                convertView.setBackgroundResource(R.drawable.calendar_today_border);
                holder.dayNumber.setTextColor(context.getResources().getColor(R.color.primary_text));
                holder.dayNumber.setTypeface(null, android.graphics.Typeface.BOLD);
                holder.dayNumber.setShadowLayer(0, 0, 0, 0); // Clear shadow
            } else {
                // Normal day - light border
                convertView.setBackgroundResource(R.drawable.calendar_normal_border);
                holder.dayNumber.setTextColor(context.getResources().getColor(R.color.primary_text));
                holder.dayNumber.setTypeface(null, android.graphics.Typeface.NORMAL);
                holder.dayNumber.setShadowLayer(0, 0, 0, 0); // Clear shadow
            }
            
            // Set click listener
            final int dayNum = day.getDay();
            convertView.setOnClickListener(v -> {
                if (listener != null && dayNum > 0) {
                    listener.onDateClick(dayNum, currentMonth, currentYear);
                }
            });
            
            // Debug: Log each day being displayed
            android.util.Log.d("SimpleCalendarAdapter", "Displaying day " + day.getDay() + " at position " + position);
        }
        
        return convertView;
    }
    
    static class ViewHolder {
        TextView dayNumber;
        TextView subjectIndicator;
    }
    
    static class CalendarDay {
        private int day;
        private boolean hasLecture;
        private boolean isToday;
        
        public CalendarDay(int day, boolean hasLecture, boolean isToday) {
            this.day = day;
            this.hasLecture = hasLecture;
            this.isToday = isToday;
        }
        
        public int getDay() { return day; }
        public boolean hasLecture() { return hasLecture; }
        public boolean isToday() { return isToday; }
    }
}
