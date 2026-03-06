# Logcat Access Guide

## Build Status
✅ **BUILD SUCCESSFUL** - APK ready for installation and testing

## APK Location
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## Method 1: Access Logcat via ADB (Command Line)

### Step 1: Install the APK
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Step 2: Start Logcat Monitoring
Open a terminal and run one of these commands:

#### Option A: Filter for App-Specific Logs
```bash
adb logcat -s SupabaseAuthRepository AuthViewModel LoginActivity SignUpActivity MainActivity MarkrApplication
```

#### Option B: Filter by Package Name
```bash
adb logcat | grep "com.example.markr"
```

#### Option C: View All Logs (Verbose)
```bash
adb logcat
```

#### Option D: Clear and Monitor Fresh Logs
```bash
adb logcat -c
adb logcat -s SupabaseAuthRepository AuthViewModel LoginActivity
```

### Step 3: Launch the App
```bash
adb shell am start -n com.example.markr/.LoginActivity
```

### Step 4: Test Signup
1. Open the app on your device/emulator
2. Navigate to Sign Up screen
3. Enter test credentials:
   - Name: Test User
   - Email: test@example.com
   - Password: test123456
   - Confirm: test123456
4. Click "Create Account"
5. Watch the logcat output

---

## Method 2: Access Logcat via Android Studio

### Step 1: Open Android Studio
1. Open Android Studio
2. Open this project: `E:\Markr Firebase`

### Step 2: Connect Device/Emulator
1. Connect your Android device via USB (with USB debugging enabled)
   OR
2. Start an Android emulator

### Step 3: Open Logcat Window
1. Click on "Logcat" tab at the bottom of Android Studio
   OR
2. Go to: View → Tool Windows → Logcat

### Step 4: Filter Logs
In the Logcat filter box, enter:
```
SupabaseAuthRepository|AuthViewModel|LoginActivity
```

### Step 5: Install and Run
1. Click the "Run" button (green play icon)
2. Select your device/emulator
3. App will install and launch automatically

---

## Expected Log Output

### When App Starts (LoginActivity)
```
D/LoginActivity: 🔵 onCreate started
D/LoginActivity: 🔍 Checking for existing session...
D/SupabaseAuthRepository: Session restore complete. Logged in: false
```

### When User Signs Up (Success)
```
D/AuthViewModel: 📱 UI: Starting signup for test@example.com
D/SupabaseAuthRepository: 🔵 SIGNUP ATTEMPT: email=test@example.com
D/SupabaseAuthRepository: 🟢 SIGNUP COMPLETED
D/SupabaseAuthRepository:    Current User: UserInfo(id=xxx-xxx-xxx, email=test@example.com, ...)
D/SupabaseAuthRepository:    User ID: xxx-xxx-xxx-xxx-xxx
D/SupabaseAuthRepository:    User Email: test@example.com
D/SupabaseAuthRepository:    Email Confirmed At: null
D/SupabaseAuthRepository:    Current Session: NULL
D/SupabaseAuthRepository:    Session Token: null...
D/AuthViewModel: ✅ UI: Signup successful
```

### When User Signs Up (Failure)
```
D/AuthViewModel: 📱 UI: Starting signup for test@example.com
D/SupabaseAuthRepository: 🔵 SIGNUP ATTEMPT: email=test@example.com
E/SupabaseAuthRepository: 🔴 SIGNUP FAILED: User already registered
E/SupabaseAuthRepository:    Exception type: RestException
E/SupabaseAuthRepository:    Cause: null
E/AuthViewModel: ❌ UI: Signup failed - User already registered
```

### When User Logs In (Success)
```
D/LoginActivity: ✅ Login success: Login successful!
D/LoginActivity: 🚀 Navigating to MainActivity
D/MainActivity: onCreate started
D/MainActivity: Supabase session active for: test@example.com
D/MainActivity: onCreate completed successfully
```

---

## Quick Test Commands

### Full Test Sequence
```bash
# 1. Clear old logs
adb logcat -c

# 2. Start monitoring (in one terminal)
adb logcat -s SupabaseAuthRepository AuthViewModel LoginActivity SignUpActivity MainActivity

# 3. Install app (in another terminal)
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 4. Launch app
adb shell am start -n com.example.markr/.LoginActivity

# 5. Test signup in the app UI
# Watch the logs in the first terminal
```

### Save Logs to File
```bash
adb logcat -s SupabaseAuthRepository AuthViewModel LoginActivity > signup_logs.txt
```

### Check Connected Devices
```bash
adb devices
```

---

## Troubleshooting

### If ADB is not recognized
1. Install Android SDK Platform Tools
2. Add to PATH: `C:\Users\<YourUser>\AppData\Local\Android\Sdk\platform-tools`

### If No Device Found
```bash
# Check devices
adb devices

# If empty, either:
# 1. Connect Android device with USB debugging enabled
# 2. Start Android emulator from Android Studio
```

### If App Crashes
```bash
# View crash logs
adb logcat | grep "AndroidRuntime"
```

### If Logs Too Verbose
```bash
# Use stricter filter
adb logcat -s SupabaseAuthRepository:D AuthViewModel:D *:S
```

---

## What to Look For

### 1. Signup Diagnosis
- ✅ Does "🔵 SIGNUP ATTEMPT" appear?
- ✅ Does "🟢 SIGNUP COMPLETED" appear?
- ✅ Is User ID present?
- ✅ Is Email Confirmed At null or has timestamp?
- ✅ Is Current Session NULL or EXISTS?

### 2. Dashboard Verification
After signup, check Supabase Dashboard:
```
https://supabase.com/dashboard
→ Project: wvoelfjpbxqtfvaxaylc
→ Authentication → Users
```

Look for:
- User email appears
- Status: Confirmed or Unconfirmed
- User ID matches log output

### 3. Email Confirmation
If "Email Confirmed At: null" and "Current Session: NULL":
- Email confirmation is ON
- Check email inbox for confirmation link
- User cannot login until confirmed

---

## Next Steps After Testing

1. **Copy logcat output** and share it
2. **Check Supabase Dashboard** for user creation
3. **Report findings**:
   - Did user appear in dashboard?
   - What was the status?
   - Any error messages?
   - Did confirmation email arrive?

---

**Build Status**: ✅ SUCCESSFUL
**APK Ready**: ✅ YES
**Logging Enhanced**: ✅ YES
**Ready for Testing**: ✅ YES
