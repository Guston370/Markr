package com.example.markr;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.markr.viewmodel.AuthViewModel;

/**
 * SignUpActivity — creates a new Supabase Auth user (email + password).
 *
 * On success Supabase sends a confirmation email. The user is redirected
 * back to {@link LoginActivity} after account creation.
 */
public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText nameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;

    private MaterialButton signUpButton;
    private MaterialButton loginButton;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();
    }

    // ── View binding ───────────────────────────────────────────────────────────

    private void initViews() {
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.loginButton);
    }

    // ── ViewModel ──────────────────────────────────────────────────────────────

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    // ── LiveData observers ─────────────────────────────────────────────────────

    private void setupObservers() {
        authViewModel.getLoading().observe(this, loading -> {
            signUpButton.setEnabled(!loading);
            signUpButton.setText(loading ? "Creating Account…" : "Create Account");
        });

        authViewModel.getAuthEvent().observe(this, event -> {
            if (event instanceof AuthViewModel.AuthEvent.Success) {
                String msg = ((AuthViewModel.AuthEvent.Success) event).getMessage();
                Toast.makeText(this, "✅ " + msg, Toast.LENGTH_LONG).show();
                // Return to login after 2 s so user can read the message
                new android.os.Handler().postDelayed(this::goToLogin, 2000);

            } else if (event instanceof AuthViewModel.AuthEvent.Failure) {
                String err = ((AuthViewModel.AuthEvent.Failure) event).getError();
                Toast.makeText(this, err, Toast.LENGTH_LONG).show();
            }
        });
    }

    // ── Click listeners ────────────────────────────────────────────────────────

    private void setupClickListeners() {
        signUpButton.setOnClickListener(v -> performSignUp());
        loginButton.setOnClickListener(v -> goToLogin());
    }

    // ── Sign-up logic ──────────────────────────────────────────────────────────

    private void performSignUp() {
        String name = text(nameEditText);
        String email = text(emailEditText);
        String pass = text(passwordEditText);
        String confirm = text(confirmPasswordEditText);

        // Validation
        if (name.isEmpty()) {
            toast("Please enter your name");
            return;
        }
        if (name.length() < 2) {
            toast("Name must be at least 2 characters");
            return;
        }
        if (email.isEmpty()) {
            toast("Please enter your email");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Please enter a valid email address");
            return;
        }
        if (pass.isEmpty()) {
            toast("Please enter a password");
            return;
        }
        if (pass.length() < 6) {
            toast("Password must be at least 6 characters");
            return;
        }
        if (!pass.equals(confirm)) {
            toast("Passwords do not match");
            return;
        }

        authViewModel.signUp(email, pass);
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private String text(TextInputEditText et) {
        return et != null && et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
