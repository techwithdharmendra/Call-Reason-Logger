package com.example.calllogger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class LogActivity extends AppCompatActivity {

    private String phoneNumber = "Unknown";
    private String callType = "Unknown";
    private String contactName = "Unknown";

    private DatabaseHelper dbHelper;
    private EditText etNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        dbHelper = new DatabaseHelper(this);

        TextView tvContact = findViewById(R.id.tvContact);
        TextView tvPhoneType = findViewById(R.id.tvPhoneType);
        etNote = findViewById(R.id.etNote);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnRemind = findViewById(R.id.btnRemind);

        if (getIntent() != null) {
            if (getIntent().hasExtra("phone_number")) {
                phoneNumber = getIntent().getStringExtra("phone_number");
            }
            if (getIntent().hasExtra("call_type")) {
                callType = getIntent().getStringExtra("call_type");
            }
        }

        contactName = getContactName(phoneNumber);
        
        if (contactName.equals("Unknown")) {
            tvContact.setText(phoneNumber);
        } else {
            tvContact.setText(contactName + " (" + phoneNumber + ")");
        }

        tvPhoneType.setText(callType);

        btnSave.setOnClickListener(v -> saveLog());
        btnRemind.setOnClickListener(v -> scheduleReminder());
    }

    @SuppressLint("Range")
    private String getContactName(String number) {
        if (number == null || number.isEmpty() || number.equals("Unknown")) return "Unknown";
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return "Unknown";
        }
        
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
        
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                cursor.close();
                return name != null ? name : "Unknown";
            }
            cursor.close();
        }
        return "Unknown";
    }

    private void saveLog() {
        String note = etNote.getText().toString().trim();
        CallLogModel log = new CallLogModel(
                0,
                contactName.equals("Unknown") ? phoneNumber : contactName,
                phoneNumber,
                callType,
                note,
                System.currentTimeMillis()
        );
        dbHelper.insertLog(log);
        Toast.makeText(this, "Log saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void scheduleReminder() {
        String note = etNote.getText().toString().trim();

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("contact_name", contactName.equals("Unknown") ? phoneNumber : contactName);
        intent.putExtra("note", note);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 
                (int) System.currentTimeMillis(), 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            long timeInMillis = System.currentTimeMillis() + (60 * 60 * 1000); // 1 hour reminder
            
            // Using set() does not require EXACT alarms permission in Android 12+
            // and fulfills the basic background reminder requirement gracefully.
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            Toast.makeText(this, "Reminder scheduled for 1 hour from now.", Toast.LENGTH_SHORT).show();
        }
    }
}
