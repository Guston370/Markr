package com.example.markr.supabase

import com.example.markr.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime

/**
 * Application-wide Supabase client singleton.
 *
 * Call [SupabaseManager.init] exactly once from [MarkrApplication.onCreate].
 * All repositories reference [SupabaseManager.client] to get the same instance.
 *
 * Credentials come exclusively from [BuildConfig] — they are injected at compile
 * time from local.properties and never appear in source code.
 */
object SupabaseManager {

    lateinit var client: SupabaseClient
        private set

    fun init() {
        client = createSupabaseClient(
            supabaseUrl  = BuildConfig.SUPABASE_URL,
            supabaseKey  = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)        // Authentication (email/password, session persistence)
            install(Postgrest)   // Database CRUD
            install(Realtime)    // Realtime subscriptions (optional but installed)
        }
    }

    /** Convenience shorthand for the Auth plugin. */
    fun getAuth() = client.auth

    /** Convenience shorthand for the Postgrest plugin. */
    fun getDb() = client.postgrest
}
