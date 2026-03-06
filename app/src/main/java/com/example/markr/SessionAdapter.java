package com.example.markr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import java.util.ArrayList;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
    private List<SessionItem> sessions;
    private OnSessionClickListener listener;

    public interface OnSessionClickListener {
        void onSessionClick(String subjectName, int sessionIndex, String status);
    }

    public SessionAdapter(OnSessionClickListener listener) {
        this.listener = listener;
        this.sessions = new ArrayList<>();
    }

    public void updateSessions(List<SessionItem> sessions) {
        this.sessions = sessions != null ? sessions : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_item, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        SessionItem session = sessions.get(position);
        holder.bind(session, listener);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        private TextView sessionNumberText;
        private TextView sessionStatusText;
        private MaterialButton presentButton;
        private MaterialButton absentButton;
        private MaterialButton notConductedButton;
        private MaterialButton unmarkedButton;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionNumberText = itemView.findViewById(R.id.sessionNumberText);
            sessionStatusText = itemView.findViewById(R.id.sessionStatusText);
            presentButton = itemView.findViewById(R.id.presentButton);
            absentButton = itemView.findViewById(R.id.absentButton);
            notConductedButton = itemView.findViewById(R.id.notConductedButton);
            unmarkedButton = itemView.findViewById(R.id.unmarkedButton);
        }

        public void bind(SessionItem session, OnSessionClickListener listener) {
            sessionNumberText.setText("Session " + (session.getSessionIndex() + 1));
            
            // Update current status text
            String currentStatus = session.getCurrentStatus();
            String statusText = getStatusDisplayText(currentStatus);
            sessionStatusText.setText(statusText);
            
            // Set status text color
            int statusColor = getStatusColor(currentStatus);
            sessionStatusText.setTextColor(itemView.getContext().getResources().getColor(statusColor));

            // Set button states based on current attendance
            updateButtonStates(currentStatus);

            // Set click listeners
            presentButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSessionClick(session.getSubjectName(), session.getSessionIndex(), "present");
                }
            });

            absentButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSessionClick(session.getSubjectName(), session.getSessionIndex(), "absent");
                }
            });

            notConductedButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSessionClick(session.getSubjectName(), session.getSessionIndex(), "not_conducted");
                }
            });

            unmarkedButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSessionClick(session.getSubjectName(), session.getSessionIndex(), "unmarked");
                }
            });
        }
        
        private String getStatusDisplayText(String status) {
            switch (status) {
                case "present": return "Present";
                case "absent": return "Absent";
                case "not_conducted": return "N/A";
                default: return "Unmarked";
            }
        }
        
        private int getStatusColor(String status) {
            switch (status) {
                case "present": return R.color.session_present;
                case "absent": return R.color.session_absent;
                case "not_conducted": return R.color.session_not_conducted;
                default: return R.color.session_unmarked;
            }
        }

        private void updateButtonStates(String currentStatus) {
            // Reset all buttons
            presentButton.setSelected(false);
            absentButton.setSelected(false);
            notConductedButton.setSelected(false);
            unmarkedButton.setSelected(false);

            // Set selected state
            switch (currentStatus) {
                case "present":
                    presentButton.setSelected(true);
                    break;
                case "absent":
                    absentButton.setSelected(true);
                    break;
                case "not_conducted":
                    notConductedButton.setSelected(true);
                    break;
                case "unmarked":
                default:
                    unmarkedButton.setSelected(true);
                    break;
            }
        }
    }

    public static class SessionItem {
        private String subjectName;
        private int sessionIndex;
        private String currentStatus;

        public SessionItem(String subjectName, int sessionIndex, String currentStatus) {
            this.subjectName = subjectName;
            this.sessionIndex = sessionIndex;
            this.currentStatus = currentStatus;
        }

        public String getSubjectName() { return subjectName; }
        public int getSessionIndex() { return sessionIndex; }
        public String getCurrentStatus() { return currentStatus; }
    }
}
