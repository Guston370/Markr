package com.example.markr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "attendance_records.db";
    private static final int DATABASE_VERSION = 1;
    
    private static final String TABLE_RECORDS = "records";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_TOTAL_CLASSES = "total_classes";
    private static final String COLUMN_ATTENDED_CLASSES = "attended_classes";
    private static final String COLUMN_MIN_ATTENDANCE = "min_attendance";
    private static final String COLUMN_DATE_CREATED = "date_created";

    public AttendanceDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_RECORDS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT + " TEXT NOT NULL, " +
                COLUMN_TOTAL_CLASSES + " INTEGER NOT NULL, " +
                COLUMN_ATTENDED_CLASSES + " INTEGER NOT NULL, " +
                COLUMN_MIN_ATTENDANCE + " REAL NOT NULL, " +
                COLUMN_DATE_CREATED + " TEXT NOT NULL" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    public long insertRecord(AttendanceRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_SUBJECT, record.getSubject());
        values.put(COLUMN_TOTAL_CLASSES, record.getTotalClasses());
        values.put(COLUMN_ATTENDED_CLASSES, record.getAttendedClasses());
        values.put(COLUMN_MIN_ATTENDANCE, record.getMinAttendance());
        values.put(COLUMN_DATE_CREATED, record.getDateCreated());
        
        long id = db.insert(TABLE_RECORDS, null, values);
        db.close();
        return id;
    }

    public List<AttendanceRecord> getAllRecords() {
        List<AttendanceRecord> records = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RECORDS + " ORDER BY " + COLUMN_DATE_CREATED + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                AttendanceRecord record = new AttendanceRecord();
                record.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                record.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT)));
                record.setTotalClasses(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_CLASSES)));
                record.setAttendedClasses(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ATTENDED_CLASSES)));
                record.setMinAttendance(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MIN_ATTENDANCE)));
                record.setDateCreated(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_CREATED)));
                records.add(record);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return records;
    }

    public boolean updateRecord(AttendanceRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_SUBJECT, record.getSubject());
        values.put(COLUMN_TOTAL_CLASSES, record.getTotalClasses());
        values.put(COLUMN_ATTENDED_CLASSES, record.getAttendedClasses());
        values.put(COLUMN_MIN_ATTENDANCE, record.getMinAttendance());
        
        int rowsAffected = db.update(TABLE_RECORDS, values, COLUMN_ID + " = ?", 
                new String[]{String.valueOf(record.getId())});
        db.close();
        
        return rowsAffected > 0;
    }

    public boolean deleteRecord(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_RECORDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }
}
