package com.example.markr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.markr.models.User;

import java.util.ArrayList;
import java.util.List;

public class OfflineUserDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "OfflineUserDB";
    private static final String DATABASE_NAME = "offline_users.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_OFFLINE_USERS = "offline_users";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_STUDENT_ID = "student_id";
    private static final String COLUMN_COURSE = "course";
    private static final String COLUMN_SEMESTER = "semester";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_DEGREE = "degree";
    private static final String COLUMN_COLLEGE = "college";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_IS_SYNCED = "is_synced";

    // Create table SQL
    private static final String CREATE_TABLE_OFFLINE_USERS = 
        "CREATE TABLE " + TABLE_OFFLINE_USERS + "("
        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + COLUMN_NAME + " TEXT NOT NULL,"
        + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
        + COLUMN_PASSWORD + " TEXT NOT NULL,"
        + COLUMN_STUDENT_ID + " TEXT UNIQUE NOT NULL,"
        + COLUMN_COURSE + " TEXT NOT NULL,"
        + COLUMN_SEMESTER + " TEXT NOT NULL,"
        + COLUMN_YEAR + " TEXT NOT NULL,"
        + COLUMN_DEGREE + " TEXT NOT NULL,"
        + COLUMN_COLLEGE + " TEXT NOT NULL,"
        + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
        + COLUMN_IS_SYNCED + " INTEGER DEFAULT 0"
        + ")";

    public OfflineUserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_OFFLINE_USERS);
        Log.d(TAG, "Offline users table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_USERS);
        onCreate(db);
    }

    // Add offline user
    public long addOfflineUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_STUDENT_ID, user.getStudentId());
        values.put(COLUMN_COURSE, user.getCourse());
        values.put(COLUMN_SEMESTER, user.getSemester());
        values.put(COLUMN_YEAR, user.getYear());
        values.put(COLUMN_DEGREE, user.getDegree());
        values.put(COLUMN_COLLEGE, user.getCollege());
        values.put(COLUMN_IS_SYNCED, 0); // Not synced yet

        long id = db.insert(TABLE_OFFLINE_USERS, null, values);
        db.close();
        
        Log.d(TAG, "Offline user added with ID: " + id);
        return id;
    }

    // Get all offline users
    public List<User> getAllOfflineUsers() {
        List<User> userList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_OFFLINE_USERS + " ORDER BY " + COLUMN_CREATED_AT + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
                user.setStudentId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
                user.setCourse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE)));
                user.setSemester(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEMESTER)));
                user.setYear(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
                user.setDegree(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEGREE)));
                user.setCollege(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLLEGE)));
                user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                
                userList.add(user);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return userList;
    }

    // Get unsynced users
    public List<User> getUnsyncedUsers() {
        List<User> userList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_OFFLINE_USERS + " WHERE " + COLUMN_IS_SYNCED + " = 0 ORDER BY " + COLUMN_CREATED_AT + " ASC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
                user.setStudentId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
                user.setCourse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE)));
                user.setSemester(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEMESTER)));
                user.setYear(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
                user.setDegree(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEGREE)));
                user.setCollege(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLLEGE)));
                user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                
                userList.add(user);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return userList;
    }

    // Mark user as synced
    public void markUserAsSynced(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_SYNCED, 1);
        
        db.update(TABLE_OFFLINE_USERS, values, COLUMN_ID + " = ?", new String[]{userId});
        db.close();
        
        Log.d(TAG, "User " + userId + " marked as synced");
    }

    // Delete synced user
    public void deleteSyncedUser(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OFFLINE_USERS, COLUMN_ID + " = ?", new String[]{userId});
        db.close();
        
        Log.d(TAG, "Synced user " + userId + " deleted from offline storage");
    }

    // Check if email exists
    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_OFFLINE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        
        return exists;
    }
    
    // Get user by email
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_OFFLINE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setStudentId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
            user.setCourse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE)));
            user.setSemester(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEMESTER)));
            user.setYear(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
            user.setDegree(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEGREE)));
            user.setCollege(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLLEGE)));
            user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
            user.setRole("student");
            user.setActive(true);
            user.setEmailVerified(false);
        }
        
        cursor.close();
        db.close();
        return user;
    }

    // Check if student ID exists
    public boolean studentIdExists(String studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_OFFLINE_USERS + " WHERE " + COLUMN_STUDENT_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{studentId});
        
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        
        return exists;
    }

    // Get offline user count
    public int getOfflineUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_OFFLINE_USERS;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // Get unsynced user count
    public int getUnsyncedUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_OFFLINE_USERS + " WHERE " + COLUMN_IS_SYNCED + " = 0";
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // Update offline user
    public boolean updateOfflineUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_STUDENT_ID, user.getStudentId());
        values.put(COLUMN_COURSE, user.getCourse());
        values.put(COLUMN_SEMESTER, user.getSemester());
        values.put(COLUMN_YEAR, user.getYear());
        values.put(COLUMN_DEGREE, user.getDegree());
        values.put(COLUMN_COLLEGE, user.getCollege());
        values.put(COLUMN_IS_SYNCED, 0); // Mark as not synced after update

        int rowsAffected = db.update(TABLE_OFFLINE_USERS, values, COLUMN_ID + " = ?", new String[]{user.getId()});
        db.close();
        
        Log.d(TAG, "Offline user updated: " + (rowsAffected > 0 ? "Success" : "Failed"));
        return rowsAffected > 0;
    }

    // Manually add a user from server to offline database (for testing/sync purposes)
    public long addServerUserToOffline(String name, String email, String studentId, String course, 
                                     String semester, String year, String degree, String college) {
        Log.d(TAG, "Adding server user to offline database: " + email);
        
        // Check if user already exists
        if (emailExists(email)) {
            Log.d(TAG, "User already exists in offline database: " + email);
            return 1; // Return success without inserting
        }
        
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, "REQUIRES_SERVER_AUTH"); // Mark as requiring server authentication
        values.put(COLUMN_STUDENT_ID, studentId);
        values.put(COLUMN_COURSE, course);
        values.put(COLUMN_SEMESTER, semester);
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_DEGREE, degree);
        values.put(COLUMN_COLLEGE, college);
        values.put(COLUMN_IS_SYNCED, 1); // Mark as synced from server

        long id = db.insert(TABLE_OFFLINE_USERS, null, values);
        db.close();
        
        Log.d(TAG, "Server user synced to offline database with ID: " + id);
        
        if (id == -1) {
            Log.e(TAG, "Failed to insert server user - check for duplicate email or student ID");
        }
        
        return id;
    }

    // Clear all offline users
    public void clearAllOfflineUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OFFLINE_USERS, null, null);
        db.close();
        Log.d(TAG, "All offline users cleared");
    }

    // Clear all users and reset auto-increment
    public void resetOfflineDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OFFLINE_USERS, null, null);
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + TABLE_OFFLINE_USERS + "'");
        db.close();
        Log.d(TAG, "Offline database reset completely");
    }
}
