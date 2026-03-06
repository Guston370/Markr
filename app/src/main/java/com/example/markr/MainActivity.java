package com.example.markr;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.markr.viewmodel.AuthViewModel;

/**
 * MainActivity — host for the bottom-navigation fragments.
 *
 * Session guard: if Supabase reports no valid session, redirects to
 * LoginActivity.
 * All Firebase/CloudSync code has been removed; Supabase handles auth and data.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigation;
    private Fragment currentFragment;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d(TAG, "onCreate started");

        try {
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);

            ViewCompat.setOnApplyWindowInsetsListener(
                    findViewById(R.id.mainContent), (v, insets) -> {
                        Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                        v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                        return insets;
                    });

            authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

            // Guard: require a valid Supabase session
            if (!authViewModel.isLoggedIn()) {
                android.util.Log.w(TAG, "No Supabase session — redirecting to login");
                goToLogin();
                return;
            }

            android.util.Log.d(TAG, "Supabase session active for: "
                    + authViewModel.getCurrentUserEmail());

            initViews();
            setupNavigation();

            android.util.Log.d(TAG, "onCreate completed successfully");

        } catch (Exception e) {
            android.util.Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            goToLogin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-validate session on resume
        if (authViewModel != null && !authViewModel.isLoggedIn()) {
            android.util.Log.w(TAG, "Session expired — redirecting to login");
            goToLogin();
        }
    }

    // ── View binding ───────────────────────────────────────────────────────────

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    // ── Bottom Navigation ──────────────────────────────────────────────────────

    private void setupNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            try {
                int id = item.getItemId();
                if (id == R.id.nav_calendar) {
                    showFragment(new CalendarFragment());
                    return true;
                } else if (id == R.id.nav_subjects) {
                    showFragment(new SubjectsFragment());
                    return true;
                } else if (id == R.id.nav_settings) {
                    showFragment(new SettingsFragment());
                    return true;
                } else if (id == R.id.nav_feedback) {
                    showFragment(new FeedbackFragment());
                    return true;
                }
                return false;
            } catch (Exception e) {
                android.util.Log.e(TAG, "Navigation error: " + e.getMessage(), e);
                showFragment(new CalendarFragment());
                return true;
            }
        });

        // Default tab
        bottomNavigation.setSelectedItemId(R.id.nav_calendar);
        showFragment(new CalendarFragment());
    }

    // ── Fragment management ────────────────────────────────────────────────────

    private void showFragment(Fragment fragment) {
        if (currentFragment != null
                && currentFragment.getClass().equals(fragment.getClass())) {
            return; // already visible
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();
        currentFragment = fragment;
        android.util.Log.d(TAG, "Fragment → " + fragment.getClass().getSimpleName());
    }

    /** Called by child fragments that need to switch to the Subjects tab. */
    public void switchToSubjectsTab() {
        showFragment(new SubjectsFragment());
        bottomNavigation.setSelectedItemId(R.id.nav_subjects);
    }

    // ── Logout (called by SettingsFragment / ProfileFragment) ─────────────────

    public void logoutUser() {
        authViewModel.signOut();
        authViewModel.getAuthEvent().observe(this, event -> {
            if (event instanceof AuthViewModel.AuthEvent.SignedOut) {
                goToLogin();
            }
        });
    }

    // ── Navigation helper ──────────────────────────────────────────────────────

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
