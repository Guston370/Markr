package com.example.markr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.markr.data.repository.SupabaseAuthRepository
import com.example.markr.supabase.SupabaseManager
import kotlinx.coroutines.launch

/**
 * ViewModel for Login and Sign-Up screens.
 *
 * Responsibilities:
 *  - Expose UI state as LiveData (no business logic in Activity)
 *  - Call [SupabaseAuthRepository] on the viewModelScope (auto-cancelled on destroy)
 *  - Never reference Android Views
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SupabaseAuthRepository(SupabaseManager.client)

    // ── UI state ───────────────────────────────────────────────────────────────

    private val _loading    = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _authEvent  = MutableLiveData<AuthEvent>()
    val authEvent: LiveData<AuthEvent> = _authEvent

    // ── Public actions (called from Activity) ──────────────────────────────────

    fun signIn(email: String, password: String) {
        _loading.value = true
        android.util.Log.d("AuthViewModel", "📱 UI: Starting signup for $email")
        
        viewModelScope.launch {
            repo.signIn(email, password)
                .onSuccess {
                    android.util.Log.d("AuthViewModel", "✅ UI: Signup successful")
                    _authEvent.postValue(AuthEvent.Success("Login successful!"))
                }
                .onFailure { e ->
                    android.util.Log.e("AuthViewModel", "❌ UI: Signup failed - ${e.message}")
                    _authEvent.postValue(AuthEvent.Failure(friendlyMessage(e)))
                }
            _loading.postValue(false)
        }
    }

    fun signUp(email: String, password: String) {
        _loading.value = true
        android.util.Log.d("AuthViewModel", "📱 UI: Starting signup for $email")
        
        viewModelScope.launch {
            repo.signUp(email, password)
                .onSuccess {
                    android.util.Log.d("AuthViewModel", "✅ UI: Signup successful")
                    _authEvent.postValue(
                        AuthEvent.Success("Account created! Please check your email to confirm.")
                    )
                }
                .onFailure { e ->
                    android.util.Log.e("AuthViewModel", "❌ UI: Signup failed - ${e.message}")
                    _authEvent.postValue(AuthEvent.Failure(friendlyMessage(e)))
                }
            _loading.postValue(false)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repo.signOut()
            _authEvent.postValue(AuthEvent.SignedOut)
        }
    }

    fun isLoggedIn(): Boolean = repo.isLoggedIn()

    fun getCurrentUserId(): String? = repo.getCurrentUserId()

    fun getCurrentUserEmail(): String? = repo.getCurrentUserEmail()

    /** Restore a persisted session on app start. */
    fun restoreSession() {
        viewModelScope.launch {
            repo.restoreSession()
                .onSuccess {
                    if (repo.isLoggedIn()) {
                        _authEvent.postValue(AuthEvent.SessionRestored)
                    }
                }
        }
    }

    /** Send a Supabase password-reset email. */
    fun sendPasswordReset(email: String) {
        _loading.value = true
        viewModelScope.launch {
            repo.sendPasswordReset(email)
                .onSuccess {
                    _authEvent.postValue(AuthEvent.Success("Reset email sent"))
                }
                .onFailure { e ->
                    _authEvent.postValue(AuthEvent.Failure(friendlyMessage(e)))
                }
            _loading.postValue(false)
        }
    }


    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun friendlyMessage(e: Throwable): String {
        val msg = e.message ?: "Unknown error"
        return when {
            "Invalid login credentials" in msg -> "Incorrect email or password."
            "Email not confirmed"        in msg -> "Please confirm your email before signing in."
            "User already registered"   in msg -> "An account with this email already exists."
            "Network"                   in msg -> "Network error. Please check your connection."
            else                               -> msg
        }
    }

    // ── Event sealed class ─────────────────────────────────────────────────────

    sealed class AuthEvent {
        data class Success(val message: String) : AuthEvent()
        data class Failure(val error: String)   : AuthEvent()
        object SessionRestored                  : AuthEvent()
        object SignedOut                        : AuthEvent()
    }
}
