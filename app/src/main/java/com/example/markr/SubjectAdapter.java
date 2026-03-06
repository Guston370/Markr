package com.example.markr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private List<Subject> subjects;
    private OnSubjectActionListener listener;

    public interface OnSubjectActionListener {
        void onEditSubject(Subject subject);
        void onDeleteSubject(Subject subject);
        void onViewDetails(Subject subject);
    }

    public SubjectAdapter(List<Subject> subjects, OnSubjectActionListener listener) {
        this.subjects = subjects;
        this.listener = listener;
    }

    public void updateSubjects(List<Subject> newSubjects) {
        this.subjects = newSubjects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.bind(subject, listener);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectName;
        private TextView attendancePercentage;
        private TextView weekdaysText;
        private TextView sessionsText;
        private TextView presentCount;
        private TextView absentCount;
        private TextView attendanceAdvice;
        private MaterialButton editButton;
        private MaterialButton deleteButton;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectName);
            attendancePercentage = itemView.findViewById(R.id.attendancePercentage);
            weekdaysText = itemView.findViewById(R.id.weekdaysText);
            sessionsText = itemView.findViewById(R.id.sessionsText);
            presentCount = itemView.findViewById(R.id.presentCount);
            absentCount = itemView.findViewById(R.id.absentCount);
            attendanceAdvice = itemView.findViewById(R.id.attendanceAdvice);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Subject subject, OnSubjectActionListener listener) {
            subjectName.setText(subject.getName());
            attendancePercentage.setText(String.format("%.1f%%", subject.getAttendancePercentage()));
            
            // Set percentage color based on attendance
            if (subject.getAttendancePercentage() >= 75) {
                attendancePercentage.setTextColor(itemView.getContext().getResources().getColor(R.color.success_color));
            } else if (subject.getAttendancePercentage() >= 65) {
                attendancePercentage.setTextColor(itemView.getContext().getResources().getColor(R.color.warning_color));
            } else {
                attendancePercentage.setTextColor(itemView.getContext().getResources().getColor(R.color.error_color));
            }
            
            // Set weekdays text
            if (subject.getWeekdays() != null && !subject.getWeekdays().isEmpty()) {
                weekdaysText.setText(String.join(", ", subject.getWeekdays()));
            } else {
                weekdaysText.setText("Not set");
            }
            
            sessionsText.setText(String.format("%d sessions/week", subject.getSessionsPerWeek()));
            presentCount.setText(String.format("Present: %d", subject.getAttendedSessions()));
            absentCount.setText(String.format("Absent: %d", subject.getTotalSessions() - subject.getAttendedSessions()));
            attendanceAdvice.setText(subject.getAttendanceAdvice());
            
            // Set advice text color
            if (subject.getAttendanceAdvice().contains("can miss")) {
                attendanceAdvice.setTextColor(itemView.getContext().getResources().getColor(R.color.success_color));
            } else if (subject.getAttendanceAdvice().contains("Attend")) {
                attendanceAdvice.setTextColor(itemView.getContext().getResources().getColor(R.color.error_color));
            } else {
                attendanceAdvice.setTextColor(itemView.getContext().getResources().getColor(R.color.warning_color));
            }

            editButton.setOnClickListener(v -> listener.onEditSubject(subject));
            deleteButton.setOnClickListener(v -> listener.onDeleteSubject(subject));
            
            // Add click listener to the entire item for details view
            itemView.setOnClickListener(v -> listener.onViewDetails(subject));
        }
    }
}
