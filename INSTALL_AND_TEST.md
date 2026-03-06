# Install and Test - Fixed Version

## What Was Fixed
✅ Added `android:name=".MarkrApplication"` to AndroidManifest.xml
✅ This ensures SupabaseManager.init() is called before the app starts
✅ Also changed `usesCleartextTraffic` to `false` for security

## Build Status
✅ **BUILD SUCCESSFUL** - Ready to install

---

## Quick Install & Test

### Step 1: Uninstall Old Version (Important!)
```bash
adb uninstall com.example.markr
```

### Step 2: Install New Version
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Step 3: Start Logcat Monitoring
```bash
adb logcat -s MarkrApplication SupabaseAuthRepository AuthViewModel LoginActivity SignUpActivity MainActivity
```

### Step 4: Launch App
```bash
adb shell am start -n com.example.markr/.LoginActivity
```

---

## Expected Logs (Success)

### App Initialization
```
D/MarkrApplication: MarkrApplication onCreate started
D/MarkrApplication: Supabase client initialised successfully
D/MarkrApplication: MarkrApplication onCreate completed
```

### Login Screen Loads
```
D/LoginActivity: 🔵 onCreate started
D/LoginActivity: 🔍 Checking for existing session...
D/SupabaseAuthRepository: Session restore complete. Logged in: false
```

### User Signs Up
```
D/AuthViewModel: 📱 UI: Starting signup for test@example.com
D/SupabaseAuthRepository: 🔵 SIGNUP ATTEMPT: email=test@example.com
D/SupabaseAuthRepository: 🟢 SIGNUP COMPLETED
D/SupabaseAuthRepository:    Current User: UserInfo(...)
D/SupabaseAuthRepository:    User ID: xxx-xxx-xxx
D/SupabaseAuthRepository:    User Email: test@example.com
D/AuthViewModel: ✅ UI: Signup successful
```

---

## If Still Crashes

### Check for Initialization Error
```bash
adb logcat | grep "MarkrApplication"
```

Should see:
```
D/MarkrApplication: Supabase client initialised successfully
```

If you see an error instead, it means:
- Supabase credentials are missing
- BuildConfig not generated properly

### Verify BuildConfig
The app needs these in BuildConfig:
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`

These come from `local.properties`:
```
SUPABASE_URL=https://wvoelfjpbxqtfvaxaylc.supabase.co
SUPABASE_ANON_KEY=sb_publishable_8KdRkxumAWq77bdX9Xpdgg_Tan8nrZa
```

---

## Test Signup Flow

1. **Open app** - Should show Login screen
2. **Click "Sign Up"** - Navigate to signup
3. **Enter details:**
   - Name: Test User
   - Email: test@example.com
   - Password: test123456
   - Confirm: test123456
4. **Click "Create Account"**
5. **Watch logs** for signup process
6. **Check Supabase Dashboard:**
   - Go to: https://supabase.com/dashboard
   - Project: wvoelfjpbxqtfvaxaylc
   - Authentication → Users
   - Look for: test@example.com

---

## All-in-One Test Script

```bash
# Uninstall old version
adb uninstall com.example.markr

# Install new version
adb install app/build/outputs/apk/debug/app-debug.apk

# Clear logs
adb logcat -c

# Start monitoring in background
adb logcat -s MarkrApplication SupabaseAuthRepository AuthViewModel LoginActivity > test_logs.txt &

# Launch app
adb shell am start -n com.example.markr/.LoginActivity

# Wait for user to test signup...
# Then check test_logs.txt for results
```

---

## Success Indicators

✅ App launches without crash
✅ Login screen appears
✅ Can navigate to Sign Up
✅ Signup shows "Creating Account..." button
✅ Success message appears
✅ User appears in Supabase Dashboard

---

**Status**: ✅ FIXED - Ready for testing
**Issue**: MarkrApplication not registered in manifest
**Solution**: Added android:name=".MarkrApplication"
