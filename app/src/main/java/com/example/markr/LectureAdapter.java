package com.example.markr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import java.util.ArrayList;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.LectureViewHolder> {
    private List<LectureItem> lectures;
    private OnAttendanceClickListener listener;

    public interface OnAttendanceClickListener {
        void onAttendanceClick(String subjectName, int sessionIndex, String status);
    }

    public LectureAdapter(OnAttendanceClickListener listener) {
        this.listener = listener;
        this.lectures = new ArrayList<>();
    }

    public void updateLectures(List<LectureItem> lectures) {
        this.lectures = lectures != null ? lectures : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LectureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lecture_item_with_sessions, parent, false);
        return new LectureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureViewHolder holder, int position) {
        LectureItem lecture = lectures.get(position);
        holder.bind(lecture, listener);
    }

    @Override
    public int getItemCount() {
        return lectures.size();
    }

    class LectureViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectName;
        private TextView attendanceAdvice;
        private TextView sessionsCountText;
        private RecyclerView sessionsRecyclerView;
        private SessionAdapter sessionAdapter;
        private OnAttendanceClickListener listener;

        public LectureViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectName);
            attendanceAdvice = itemView.findViewById(R.id.attendanceAdvice);
            sessionsCountText = itemView.findViewById(R.id.sessionsCountText);
            sessionsRecyclerView = itemView.findViewById(R.id.sessionsRecyclerView);
            
            // Setup sessions RecyclerView
            sessionsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            sessionAdapter = new SessionAdapter(new SessionAdapter.OnSessionClickListener() {
                @Override
                public void onSessionClick(String subjectName, int sessionIndex, String status) {
                    // Forward to parent listener
                    if (LectureViewHolder.this.listener != null) {
                        LectureViewHolder.this.listener.onAttendanceClick(subjectName, sessionIndex, status);
                    }
                }
            });
            sessionsRecyclerView.setAdapter(sessionAdapter);
        }

        public void bind(LectureItem lecture, OnAttendanceClickListener listener) {
            this.listener = listener;
            
            subjectName.setText(lecture.getSubjectName());
            attendanceAdvice.setText(lecture.getAdvice());
            
            // Set sessions count text
            String countText = lecture.getSessionsCount() == 1 ? "1 session" : lecture.getSessionsCount() + " sessions";
            sessionsCountText.setText(countText);
            
            // Set advice text color based on content
            if (lecture.getAdvice().contains("can miss")) {
                attendanceAdvice.setTextColor(itemView.getContext().getResources().getColor(R.color.success_color));
            } else if (lecture.getAdvice().contains("Attend")) {
                attendanceAdvice.setTextColor(itemView.getContext().getResources().getColor(R.color.error_color));
            } else {
                attendanceAdvice.setTextColor(itemView.getContext().getResources().getColor(R.color.warning_color));
            }

            // Update sessions
            List<SessionAdapter.SessionItem> sessionItems = new ArrayList<>();
            for (int i = 0; i < lecture.getSessionsCount(); i++) {
                String sessionStatus = lecture.getSessionStatus(i);
                sessionItems.add(new SessionAdapter.SessionItem(lecture.getSubjectName(), i, sessionStatus));
            }
            sessionAdapter.updateSessions(sessionItems);
        }
    }

    public static class LectureItem {
        private String subjectName;
        private String advice;
        private int sessionsCount;
        private List<String> sessionStatuses;

        public LectureItem(String subjectName, String advice, int sessionsCount) {
            this.subjectName = subjectName;
            this.advice = advice;
            this.sessionsCount = sessionsCount;
            this.sessionStatuses = new ArrayList<>();
            
            // Initialize all sessions as unmarked
            for (int i = 0; i < sessionsCount; i++) {
                sessionStatuses.add("unmarked");
            }
        }
        
        public LectureItem(String subjectName, String advice, int sessionsCount, List<String> sessionStatuses) {
            this.subjectName = subjectName;
            this.advice = advice;
            this.sessionsCount = sessionsCount;
            this.sessionStatuses = sessionStatuses != null ? sessionStatuses : new ArrayList<>();
            
            // Ensure we have the right number of session statuses
            while (this.sessionStatuses.size() < sessionsCount) {
                this.sessionStatuses.add("unmarked");
            }
        }

        public String getSubjectName() { return subjectName; }
        public String getAdvice() { return advice; }
        public int getSessionsCount() { return sessionsCount; }
        public String getSessionStatus(int sessionIndex) { 
            return sessionIndex < sessionStatuses.size() ? sessionStatuses.get(sessionIndex) : "unmarked";
        }
        public void setSessionStatus(int sessionIndex, String status) {
            if (sessionIndex < sessionStatuses.size()) {
                sessionStatuses.set(sessionIndex, status);
            }
        }
    }
}
