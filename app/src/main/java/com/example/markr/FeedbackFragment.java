package com.example.markr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class FeedbackFragment extends Fragment {
    
    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText subjectEditText;
    private TextInputEditText messageEditText;
    private MaterialButton sendFeedbackButton;
    private MaterialButton bugReportButton;
    private MaterialButton featureRequestButton;
    private MaterialButton ratingButton;
    private MaterialButton generalFeedbackButton;
    
    private static final String FEEDBACK_EMAIL = "adij7499@gmail.com";
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_feedback, container, false);
            
            initializeViews(view);
            setupClickListeners();
            
            return view;
        } catch (Exception e) {
            android.util.Log.e("FeedbackFragment", "Error creating view: " + e.getMessage(), e);
            // Return a simple view to prevent crash
            View errorView = new View(getContext());
            return errorView;
        }
    }
    
    private void initializeViews(View view) {
        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        subjectEditText = view.findViewById(R.id.subjectEditText);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendFeedbackButton = view.findViewById(R.id.sendFeedbackButton);
        bugReportButton = view.findViewById(R.id.bugReportButton);
        featureRequestButton = view.findViewById(R.id.featureRequestButton);
        ratingButton = view.findViewById(R.id.ratingButton);
        generalFeedbackButton = view.findViewById(R.id.generalFeedbackButton);
    }
    
    private void setupClickListeners() {
        sendFeedbackButton.setOnClickListener(v -> sendFeedback());
        bugReportButton.setOnClickListener(v -> setQuickFeedback("Bug Report", "🐛 Bug Report: "));
        featureRequestButton.setOnClickListener(v -> setQuickFeedback("Feature Request", "💡 Feature Request: "));
        ratingButton.setOnClickListener(v -> setQuickFeedback("App Rating", "⭐ App Rating: "));
        generalFeedbackButton.setOnClickListener(v -> setQuickFeedback("General Feedback", "💬 General Feedback: "));
    }
    
    private void setQuickFeedback(String subject, String prefix) {
        subjectEditText.setText(subject);
        String currentMessage = messageEditText.getText().toString();
        if (TextUtils.isEmpty(currentMessage)) {
            messageEditText.setText(prefix);
            messageEditText.setSelection(messageEditText.getText().length());
        }
    }
    
    private void sendFeedback() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String subject = subjectEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();
        
        // Validate required fields
        if (TextUtils.isEmpty(subject)) {
            subjectEditText.setError("Please enter a subject");
            return;
        }
        
        if (TextUtils.isEmpty(message)) {
            messageEditText.setError("Please enter your message");
            return;
        }
        
        // Validate email format if provided
        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            return;
        }
        
        // Create email intent
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + FEEDBACK_EMAIL));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        
        // Build email body
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Feedback from Markr App\n\n");
        
        if (!TextUtils.isEmpty(name)) {
            emailBody.append("Name: ").append(name).append("\n");
        }
        
        if (!TextUtils.isEmpty(email)) {
            emailBody.append("Email: ").append(email).append("\n");
        }
        
        emailBody.append("\nMessage:\n").append(message);
        
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody.toString());
        
        try {
            startActivity(Intent.createChooser(emailIntent, "Send feedback via email"));
            Toast.makeText(getContext(), "Opening email client...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "No email client found. Please email us at " + FEEDBACK_EMAIL, Toast.LENGTH_LONG).show();
        }
    }
}
