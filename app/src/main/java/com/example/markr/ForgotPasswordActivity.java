package com.example.markr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private MaterialButton resetPasswordButton;
    private MaterialButton backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);
    }

    private void setupClickListeners() {
        resetPasswordButton.setOnClickListener(v -> performPasswordReset());
        backToLoginButton.setOnClickListener(v -> startLoginActivity());
    }

    private void performPasswordReset() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        resetPasswordButton.setEnabled(false);
        resetPasswordButton.setText("Sending...");

        // Simulate password reset process
        new android.os.Handler().postDelayed(() -> {
            // In a real app, this would send an email with reset instructions
            // For now, we'll just show a success message
            Toast.makeText(this, "Password reset instructions sent to " + email, Toast.LENGTH_LONG).show();
            
            // Reset button state
            resetPasswordButton.setEnabled(true);
            resetPasswordButton.setText("Send Reset Instructions");
            
            // Go back to login after a delay
            new android.os.Handler().postDelayed(() -> {
                startLoginActivity();
            }, 2000);
        }, 2000);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
