package com.example.calllogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CallLoggerDB.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "contact_name TEXT, " +
                "phone_number TEXT, " +
                "call_type TEXT, " +
                "note TEXT, " +
                "timestamp INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS logs");
        onCreate(db);
    }

    public void insertLog(CallLogModel log) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("contact_name", log.contactName);
        cv.put("phone_number", log.phoneNumber);
        cv.put("call_type", log.callType);
        cv.put("note", log.note);
        cv.put("timestamp", log.timestamp);
        db.insert("logs", null, cv);
        db.close();
    }

    public List<CallLogModel> getAllLogs(String query) {
        List<CallLogModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = null;
        String[] selectionArgs = null;
        
        if (query != null && !query.isEmpty()) {
            selection = "contact_name LIKE ? OR note LIKE ?";
            selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};
        }
        
        Cursor cursor = db.query("logs", null, selection, selectionArgs, null, null, "timestamp DESC");
        
        if (cursor.moveToFirst()) {
            do {
                CallLogModel log = new CallLogModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("contact_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phone_number")),
                    cursor.getString(cursor.getColumnIndexOrThrow("call_type")),
                    cursor.getString(cursor.getColumnIndexOrThrow("note")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                );
                list.add(log);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}
