package com.example.markr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {
    
    private MaterialTextView monthYearText;
    private MaterialButton prevMonthButton;
    private MaterialButton nextMonthButton;
    private GridView calendarGridView;
    private RecyclerView lecturesRecyclerView;
    private LinearLayout selectedDateCard;
    private LinearLayout emptyStateCard;
    private MaterialButton goToCalculatorButton;
    private MaterialTextView selectedDateText;
    private MaterialTextView lecturesCountText;

    private SimpleCalendarAdapter calendarAdapter;
    private LectureAdapter lectureAdapter;
    private AttendanceManager attendanceManager;
    private Calendar currentCalendar;
    private int selectedDay = -1;
    private int selectedMonth = -1;
    private int selectedYear = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        initializeViews(view);
        initializeCalendar();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        monthYearText = view.findViewById(R.id.monthYearText);
        prevMonthButton = view.findViewById(R.id.prevMonthButton);
        nextMonthButton = view.findViewById(R.id.nextMonthButton);
        calendarGridView = view.findViewById(R.id.calendarGridView);
        lecturesRecyclerView = view.findViewById(R.id.lecturesRecyclerView);
        selectedDateCard = view.findViewById(R.id.selectedDateCard);
        emptyStateCard = view.findViewById(R.id.emptyStateCard);
        goToCalculatorButton = view.findViewById(R.id.goToCalculatorButton);
        selectedDateText = view.findViewById(R.id.selectedDateText);
        lecturesCountText = view.findViewById(R.id.lecturesCountText);
    }
    
    private void initializeCalendar() {
        attendanceManager = AttendanceManager.getInstance();
        currentCalendar = Calendar.getInstance();

        // Initialize calendar adapter
        calendarAdapter = new SimpleCalendarAdapter(getContext(), new SimpleCalendarAdapter.OnDateClickListener() {
            @Override
            public void onDateClick(int day, int month, int year) {
                selectedDay = day;
                selectedMonth = month;
                selectedYear = year;
                // Update the adapter to show selected date with border
                calendarAdapter.setSelectedDay(day);
                updateLecturesForSelectedDate(day, month, year);
                showSelectedDateCard();
            }
        });

        calendarGridView.setAdapter(calendarAdapter);

        // Initialize lecture adapter
        lectureAdapter = new LectureAdapter(new LectureAdapter.OnAttendanceClickListener() {
            @Override
            public void onAttendanceClick(String subjectName, int sessionIndex, String status) {
                markAttendance(subjectName, sessionIndex, status);
            }
        });

        lecturesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lecturesRecyclerView.setAdapter(lectureAdapter);

        updateCalendarView();
        updateEmptyState();
        
        // Debug: Log calendar initialization
        android.util.Log.d("CalendarFragment", "Calendar initialized with " + attendanceManager.getSubjects().size() + " subjects");
    }
    
    private void setupClickListeners() {
        prevMonthButton.setOnClickListener(v -> navigateMonth(-1));
        nextMonthButton.setOnClickListener(v -> navigateMonth(1));
        goToCalculatorButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                Toast.makeText(getContext(), "Please use the Calculator tab to add subjects", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateMonth(int direction) {
        currentCalendar.add(Calendar.MONTH, direction);
        updateCalendarView();
        hideSelectedDateCard();
    }
    
    private void updateCalendarView() {
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        
        monthYearText.setText(String.format("%s %d", 
            new java.text.DateFormatSymbols().getMonths()[month], year));
        
        calendarAdapter.updateCalendar(year, month, attendanceManager.getSubjects());
        
        // Debug: Log calendar update
        android.util.Log.d("CalendarFragment", "Calendar updated for " + 
            new java.text.DateFormatSymbols().getMonths()[month] + " " + year + 
            " with " + attendanceManager.getSubjects().size() + " subjects");
    }
    
    private void updateLecturesForSelectedDate(int day, int intMonth, int year) {
        if (day <= 0) {
            return;
        }

        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, intMonth, day);

        String dayName = getDayName(selectedDate.get(Calendar.DAY_OF_WEEK));
        String dateStr = String.format("%d-%02d-%02d", year, intMonth + 1, day);

        List<LectureAdapter.LectureItem> lectures = new ArrayList<>();
        for (Subject subject : attendanceManager.getSubjects()) {
            if (subject.hasLectureOnDay(dayName)) {
                AttendanceManager.AttendanceAdvice advice = attendanceManager.getAttendanceAdvice(subject.getName());
                int sessionsCount = subject.getSessionsForDay(dayName);
                
                // Get status for each session
                List<String> sessionStatuses = new ArrayList<>();
                for (int i = 0; i < sessionsCount; i++) {
                    String sessionStatus = getAttendanceStatus(subject.getName(), dateStr, i);
                    sessionStatuses.add(sessionStatus != null ? sessionStatus : "unmarked");
                }

                lectures.add(new LectureAdapter.LectureItem(
                    subject.getName(),
                    advice.getMessage(),
                    sessionsCount,
                    sessionStatuses
                ));
            }
        }

        lectureAdapter.updateLectures(lectures);

        String countText = lectures.size() == 1 ? "1 lecture" : lectures.size() + " lectures";
        lecturesCountText.setText(countText);

        String[] monthNames = new java.text.DateFormatSymbols().getMonths();
        selectedDateText.setText(String.format("%s %d, %d", monthNames[intMonth], day, year));
    }

    private String getDayName(int dayOfWeek) {
        String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[dayOfWeek];
    }

    private String getAttendanceStatus(String subjectName, String dateStr, int sessionIndex) {
        AttendanceSession.Status status = attendanceManager.getAttendanceStatus(subjectName, dateStr, sessionIndex);
        return status.toString().toLowerCase();
    }

    private void markAttendance(String subjectName, int sessionIndex, String status) {
        if (selectedDay <= 0) return;

        String dateStr = String.format("%d-%02d-%02d",
            selectedYear, selectedMonth + 1, selectedDay);

        AttendanceSession.Status statusEnum;
        switch (status) {
            case "present": statusEnum = AttendanceSession.Status.PRESENT; break;
            case "absent": statusEnum = AttendanceSession.Status.ABSENT; break;
            case "not_conducted": statusEnum = AttendanceSession.Status.NOT_CONDUCTED; break;
            default: statusEnum = AttendanceSession.Status.UNMARKED; break;
        }

        attendanceManager.markAttendance(subjectName, dateStr, statusEnum, sessionIndex);

        updateLecturesForSelectedDate(selectedDay, selectedMonth, selectedYear);

        updateCalendarView();

        Toast.makeText(getContext(), "Session " + (sessionIndex + 1) + " marked: " + status, Toast.LENGTH_SHORT).show();
    }

    private void showSelectedDateCard() {
        if (selectedDateCard.getVisibility() != View.VISIBLE) {
            selectedDateCard.setVisibility(View.VISIBLE);
            selectedDateCard.setAlpha(0f);
            selectedDateCard.animate()
                .alpha(1.0f)
                .setDuration(300)
                .start();
        }
    }

    private void hideSelectedDateCard() {
        if (selectedDateCard.getVisibility() == View.VISIBLE) {
            selectedDateCard.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    selectedDateCard.setVisibility(View.GONE);
                    selectedDay = -1;
                    selectedMonth = -1;
                    selectedYear = -1;
                })
                .start();
        } else {
            selectedDay = -1;
            selectedMonth = -1;
            selectedYear = -1;
        }
    }

    private void updateEmptyState() {
        if (attendanceManager.getSubjects().isEmpty()) {
            emptyStateCard.setVisibility(View.VISIBLE);
            calendarGridView.setVisibility(View.GONE);
        } else {
            emptyStateCard.setVisibility(View.GONE);
            calendarGridView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCalendarView();
        updateEmptyState();
    }
}