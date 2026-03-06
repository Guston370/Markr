package com.example.markr;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthenticationManager {
    private static AuthenticationManager instance;
    private Context context;
    private static final String PREFS_NAME = "auth_data";
    private static final String USERS_KEY = "users";
    private static final String CURRENT_USER_KEY = "current_user";
    private static final String IS_LOGGED_IN_KEY = "is_logged_in";

    private AuthenticationManager() {
        // Private constructor for singleton
    }

    public static AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // User model for authentication
    public static class AuthUser {
        private String email;
        private String password;
        private String name;
        private String studentId;
        private String course;
        private String semester;
        private String year;
        private String degree;
        private String college;
        private long createdAt;

        public AuthUser(String email, String password, String name, String studentId, 
                       String course, String semester, String year, String degree, String college) {
            this.email = email;
            this.password = hashPassword(password);
            this.name = name;
            this.studentId = studentId;
            this.course = course;
            this.semester = semester;
            this.year = year;
            this.degree = degree;
            this.college = college;
            this.createdAt = System.currentTimeMillis();
        }

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = hashPassword(password); }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }

        public String getCourse() { return course; }
        public void setCourse(String course) { this.course = course; }

        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }

        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }

        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }

        public String getCollege() { return college; }
        public void setCollege(String college) { this.college = college; }

        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

        private String hashPassword(String password) {
            // Skip hashing for testing - return password as is
            return password;
            
            // Original hashing code (commented out for testing)
            /*
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(password.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                return password; // Fallback to plain text (not recommended for production)
            }
            */
        }
        
        // Static method to hash password without creating AuthUser instance
        public static String hashPasswordStatic(String password) {
            // Skip hashing for testing - return password as is
            return password;
            
            // Original hashing code (commented out for testing)
            /*
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(password.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                return password; // Fallback to plain text (not recommended for production)
            }
            */
        }
    }

    // Authentication methods
    public boolean signUp(String email, String password, String name, String studentId, 
                         String course, String semester, String year, String degree, String college) {
        if (context == null) {
            android.util.Log.d("AuthManager", "Context is null!");
            return false;
        }

        android.util.Log.d("AuthManager", "SignUp attempt for: " + email);

        // Skip user exists check for testing
        // if (isUserExists(email)) {
        //     android.util.Log.d("AuthManager", "User already exists: " + email);
        //     return false;
        // }

        // Create new user
        AuthUser newUser = new AuthUser(email, password, name, studentId, course, semester, year, degree, college);
        
        android.util.Log.d("AuthManager", "Creating new user: " + email);
        android.util.Log.d("AuthManager", "Password for new user: " + newUser.getPassword());
        
        // Save user to storage
        List<AuthUser> users = getAllUsers();
        users.add(newUser);
        saveUsers(users);
        
        android.util.Log.d("AuthManager", "User saved. Total users now: " + users.size());

        // Sync with UserManager for profile data
        syncWithUserManager(newUser);

        android.util.Log.d("AuthManager", "SignUp successful for: " + email);
        return true;
    }

    public boolean login(String email, String password) {
        if (context == null) {
            android.util.Log.d("AuthManager", "Login: Context is null!");
            return false;
        }

        android.util.Log.d("AuthManager", "Login attempt for email: " + email);
        android.util.Log.d("AuthManager", "Login password: " + password);

        // Skip all validation - just accept any login for testing
        android.util.Log.d("AuthManager", "Login successful (no validation): " + email);
        
        // Create a dummy user for testing
        AuthUser dummyUser = new AuthUser(email, password, "Test User", "TEST123", "Test Course", "1st", "2024-25", "B.Tech", "Test College");
        
        // Save current user
        saveCurrentUser(dummyUser);
        setLoggedIn(true);
        
        // Sync with UserManager
        syncWithUserManager(dummyUser);
        
        return true;
    }

    public void logout() {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(CURRENT_USER_KEY);
        editor.putBoolean(IS_LOGGED_IN_KEY, false);
        editor.apply();
    }

    public boolean isLoggedIn() {
        if (context == null) return false;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(IS_LOGGED_IN_KEY, false);
    }

    public AuthUser getCurrentUser() {
        if (context == null) return null;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userJsonString = prefs.getString(CURRENT_USER_KEY, "");

        if (userJsonString.isEmpty()) return null;

        try {
                JSONObject userJson = new JSONObject(userJsonString);
                AuthUser user = new AuthUser("", "", "", "", "", "", "", "", "");
                user.setEmail(userJson.getString("email"));
                user.setName(userJson.getString("name"));
                user.setStudentId(userJson.getString("studentId"));
                user.setCourse(userJson.getString("course"));
                user.setSemester(userJson.getString("semester"));
                user.setYear(userJson.getString("year"));
                user.setDegree(userJson.getString("degree"));
                user.setCollege(userJson.getString("college"));
                user.setCreatedAt(userJson.getLong("createdAt"));
            return user;
        } catch (Exception e) {
            android.util.Log.e("AuthenticationManager", "Error loading current user: " + e.getMessage());
            return null;
        }
    }

    private boolean isUserExists(String email) {
        List<AuthUser> users = getAllUsers();
        for (AuthUser user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private List<AuthUser> getAllUsers() {
        if (context == null) return new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String usersJsonString = prefs.getString(USERS_KEY, "");

        List<AuthUser> users = new ArrayList<>();
        if (usersJsonString.isEmpty()) return users;

        try {
            JSONArray usersArray = new JSONArray(usersJsonString);
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userJson = usersArray.getJSONObject(i);
                AuthUser user = new AuthUser("", "", "", "", "", "", "", "", "");
                user.setEmail(userJson.getString("email"));
                user.setPassword(userJson.getString("password"));
                user.setName(userJson.getString("name"));
                user.setStudentId(userJson.getString("studentId"));
                user.setCourse(userJson.getString("course"));
                user.setSemester(userJson.getString("semester"));
                user.setYear(userJson.getString("year"));
                user.setDegree(userJson.getString("degree"));
                user.setCollege(userJson.getString("college"));
                user.setCreatedAt(userJson.getLong("createdAt"));
                users.add(user);
            }
        } catch (Exception e) {
            android.util.Log.e("AuthenticationManager", "Error loading users: " + e.getMessage());
        }

        return users;
    }

    private void saveUsers(List<AuthUser> users) {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        try {
            JSONArray usersArray = new JSONArray();
            for (AuthUser user : users) {
                JSONObject userJson = new JSONObject();
                userJson.put("email", user.getEmail());
                userJson.put("password", user.getPassword());
                userJson.put("name", user.getName());
                userJson.put("studentId", user.getStudentId());
                userJson.put("course", user.getCourse());
                userJson.put("semester", user.getSemester());
                userJson.put("year", user.getYear());
                userJson.put("degree", user.getDegree());
                userJson.put("college", user.getCollege());
                userJson.put("createdAt", user.getCreatedAt());
                usersArray.put(userJson);
            }
            editor.putString(USERS_KEY, usersArray.toString());
            editor.apply();
        } catch (Exception e) {
            android.util.Log.e("AuthenticationManager", "Error saving users: " + e.getMessage());
        }
    }

    private void saveCurrentUser(AuthUser user) {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        try {
            JSONObject userJson = new JSONObject();
            userJson.put("email", user.getEmail());
            userJson.put("name", user.getName());
            userJson.put("studentId", user.getStudentId());
            userJson.put("course", user.getCourse());
            userJson.put("semester", user.getSemester());
            userJson.put("year", user.getYear());
            userJson.put("degree", user.getDegree());
            userJson.put("college", user.getCollege());
            userJson.put("createdAt", user.getCreatedAt());
            
            editor.putString(CURRENT_USER_KEY, userJson.toString());
            editor.apply();
        } catch (Exception e) {
            android.util.Log.e("AuthenticationManager", "Error saving current user: " + e.getMessage());
        }
    }

    private void setLoggedIn(boolean isLoggedIn) {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_LOGGED_IN_KEY, isLoggedIn);
        editor.apply();
    }

    // Test method to verify password hashing
    public void testPasswordHashing() {
        String testPassword = "123456";
        String hashed1 = AuthUser.hashPasswordStatic(testPassword);
        String hashed2 = AuthUser.hashPasswordStatic(testPassword);
        
        android.util.Log.d("AuthManager", "Test password: " + testPassword);
        android.util.Log.d("AuthManager", "Hashed 1: " + hashed1);
        android.util.Log.d("AuthManager", "Hashed 2: " + hashed2);
        android.util.Log.d("AuthManager", "Hashes match: " + hashed1.equals(hashed2));
    }
    
    private void syncWithUserManager(AuthUser authUser) {
        // Sync authentication data with UserManager
        UserManager userManager = UserManager.getInstance();
        userManager.registerUser(authUser.getName(), authUser.getEmail(), authUser.getStudentId(),
                                authUser.getCourse(), authUser.getSemester(), authUser.getCollege());
    }
}
