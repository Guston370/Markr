package com.example.markr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.markr.viewmodel.AuthViewModel;

/**
 * LoginActivity — email/password sign-in via Supabase.
 *
 * All network logic lives in {@link AuthViewModel}; this activity only
 * observes LiveData and updates the UI.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;
    private MaterialButton signUpButton;
    private MaterialButton forgotPasswordButton;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("LoginActivity", "🔵 onCreate started");
        
        setContentView(R.layout.activity_login);

        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();

        android.util.Log.d("LoginActivity", "🔍 Checking for existing session...");
        // If a valid Supabase session already exists, go straight to main
        authViewModel.restoreSession();
    }

    // ── View binding ───────────────────────────────────────────────────────────

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
    }

    // ── ViewModel ──────────────────────────────────────────────────────────────

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    // ── LiveData observers ─────────────────────────────────────────────────────

    private void setupObservers() {
        // Loading spinner
        authViewModel.getLoading().observe(this, loading -> {
            loginButton.setEnabled(!loading);
            loginButton.setText(loading ? "Signing in…" : "Login");
        });

        // Auth events
        authViewModel.getAuthEvent().observe(this, event -> {
            if (event instanceof AuthViewModel.AuthEvent.Success) {
                String msg = ((AuthViewModel.AuthEvent.Success) event).getMessage();
                android.util.Log.d("LoginActivity", "✅ Login success: " + msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                goToMain();

            } else if (event instanceof AuthViewModel.AuthEvent.Failure) {
                String err = ((AuthViewModel.AuthEvent.Failure) event).getError();
                android.util.Log.e("LoginActivity", "❌ Login failed: " + err);
                Toast.makeText(this, err, Toast.LENGTH_LONG).show();

            } else if (event instanceof AuthViewModel.AuthEvent.SessionRestored) {
                android.util.Log.d("LoginActivity", "🔄 Session restored - redirecting to MainActivity");
                goToMain();
            }
        });
    }

    // ── Click listeners ────────────────────────────────────────────────────────

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> performLogin());
        signUpButton.setOnClickListener(v -> startActivity(
                new Intent(this, SignUpActivity.class)));
        forgotPasswordButton.setOnClickListener(v -> startActivity(
                new Intent(this, ForgotPasswordActivity.class)));
    }

    // ── Login logic ────────────────────────────────────────────────────────────

    private void performLogin() {
        String email = emailEditText.getText() != null
                ? emailEditText.getText().toString().trim()
                : "";
        String password = passwordEditText.getText() != null
                ? passwordEditText.getText().toString()
                : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        authViewModel.signIn(email, password);
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    private void goToMain() {
        android.util.Log.d("LoginActivity", "🚀 Navigating to MainActivity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
