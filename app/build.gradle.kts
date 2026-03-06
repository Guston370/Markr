import java.util.Properties

// ─── Load local.properties (overrides gradle.properties for secrets) ──────────
val localProps = Properties().also { props ->
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { props.load(it) }
}

fun secret(key: String): String =
    localProps.getProperty(key)
        ?: project.findProperty(key) as? String
        ?: error("Missing required property: $key — set it in local.properties")

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android") version "2.1.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
    // Google Services removed — using Supabase, not Firebase
}

android {
    namespace = "com.example.markr"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.markr"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "2.3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ── Inject Supabase credentials as BuildConfig fields ──────────────────
        buildConfigField("String", "SUPABASE_URL",      "\"${secret("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${secret("SUPABASE_ANON_KEY")}\"")
    }

    buildFeatures {
        buildConfig = true   // required so BuildConfig class is generated
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.fragment:fragment:1.6.2")

    // ── Supabase Android SDK ───────────────────────────────────────────────────
    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")   // Database CRUD
    implementation("io.github.jan-tennert.supabase:auth-kt")        // Authentication
    implementation("io.github.jan-tennert.supabase:realtime-kt")    // Realtime (optional)

    // ── Ktor engine (required by Supabase SDK on Android) ────────────────────
    implementation("io.ktor:ktor-client-android:3.1.3")
    implementation("io.ktor:ktor-client-core:3.1.3")

    // ── Coroutines (Supabase SDK is coroutine-based) ──────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ── JSON / Serialization ──────────────────────────────────────────────────
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // ── Lifecycle / ViewModel ─────────────────────────────────────────────────
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // ── OkHttp (logging) ──────────────────────────────────────────────────────
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // ── Tests ──────────────────────────────────────────────────────────────────
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
