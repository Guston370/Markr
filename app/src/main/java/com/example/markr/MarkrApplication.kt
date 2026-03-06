package com.example.markr

import android.app.Application
import android.util.Log
import com.example.markr.supabase.SupabaseManager

/**
 * Application class — entry point for app-level initialisation.
 *
 * Only Supabase is initialised here. Firebase has been removed.
 */
class MarkrApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            Log.d(TAG, "MarkrApplication onCreate started")

            // Initialise the Supabase client once for the entire app lifecycle
            SupabaseManager.init()
            Log.d(TAG, "Supabase client initialised successfully")

            Log.d(TAG, "MarkrApplication onCreate completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error during app initialisation", e)
        }
    }

    companion object {
        private const val TAG = "MarkrApplication"
    }
}
