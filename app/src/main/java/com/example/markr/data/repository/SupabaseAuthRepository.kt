package com.example.markr.data.repository

import android.util.Log
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "SupabaseAuthRepository"

/**
 * Data-layer class responsible for all Supabase authentication operations.
 *
 * Runs every network call on the IO dispatcher and wraps results in
 * [Result] so callers never need try/catch.
 */
class SupabaseAuthRepository(private val client: SupabaseClient) {

    // ── Sign Up ────────────────────────────────────────────────────────────────

    /**
     * Register a new user with email + password.
     * On success Supabase sends a confirmation email (configured in dashboard).
     */
    suspend fun signUp(email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                Log.d(TAG, "🔵 SIGNUP ATTEMPT: email=$email")
                
                client.auth.signUpWith(Email) {
                    this.email    = email.trim().lowercase()
                    this.password = password
                }
                
                // Check if user was created by examining current session
                val currentUser = client.auth.currentUserOrNull()
                val currentSession = client.auth.currentSessionOrNull()
                
                Log.d(TAG, "🟢 SIGNUP COMPLETED")
                Log.d(TAG, "   Current User: $currentUser")
                Log.d(TAG, "   User ID: ${currentUser?.id}")
                Log.d(TAG, "   User Email: ${currentUser?.email}")
                Log.d(TAG, "   Email Confirmed At: ${currentUser?.emailConfirmedAt}")
                Log.d(TAG, "   Current Session: ${if (currentSession != null) "EXISTS" else "NULL"}")
                Log.d(TAG, "   Session Token: ${currentSession?.accessToken?.take(20)}...")
                
                Unit
            }.onFailure { e ->
                Log.e(TAG, "🔴 SIGNUP FAILED: ${e.message}", e)
                Log.e(TAG, "   Exception type: ${e.javaClass.simpleName}")
                Log.e(TAG, "   Cause: ${e.cause?.message}")
            }
        }

    // ── Sign In ────────────────────────────────────────────────────────────────

    /**
     * Sign in with email + password.
     * The Supabase SDK persists the session automatically.
     */
    suspend fun signIn(email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                client.auth.signInWith(Email) {
                    this.email    = email.trim().lowercase()
                    this.password = password
                }
                Log.d(TAG, "signIn success: $email")
                Unit
            }
        }

    // ── Sign Out ───────────────────────────────────────────────────────────────

    suspend fun signOut(): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                client.auth.signOut()
                Log.d(TAG, "signOut success")
                Unit
            }
        }

    // ── Password reset ─────────────────────────────────────────────────────────

    /**
     * Sends a Supabase password-reset email.
     * The email link is configured in the Supabase dashboard under
     * Authentication → Email Templates → Reset Password.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                client.auth.resetPasswordForEmail(email.trim().lowercase())
                Log.d(TAG, "Password reset email sent to $email")
                Unit
            }
        }

    // ── Session helpers ────────────────────────────────────────────────────────

    /** Returns the currently logged-in user, or null if not authenticated. */
    fun getCurrentUser(): UserInfo? = client.auth.currentUserOrNull()

    /** Returns true if a valid session exists. */
    fun isLoggedIn(): Boolean = client.auth.currentUserOrNull() != null

    /** Returns the current user's UUID (used as FK in the subjects table). */
    fun getCurrentUserId(): String? = client.auth.currentUserOrNull()?.id

    /** Returns the current user's email address. */
    fun getCurrentUserEmail(): String? =
        client.auth.currentUserOrNull()?.email

    /**
     * Restore a persisted session from shared preferences.
     * Call this once on app start (e.g. in Application.onCreate).
     */
    suspend fun restoreSession(): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                client.auth.awaitInitialization()
                Log.d(TAG, "Session restore complete. Logged in: ${isLoggedIn()}")
                Unit
            }
        }
}
