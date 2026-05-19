package com.example.calllogger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_CODE = 100;

    private RecyclerView recyclerView;
    private LogAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<CallLogModel> logsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new LogAdapter(logsList);
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadLogs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadLogs(newText);
                return true;
            }
        });

        checkPermissions();

        ExtendedFloatingActionButton fabExport = findViewById(R.id.fabExport);
        fabExport.setOnClickListener(v -> exportLogsToCSV());
    }

    private void exportLogsToCSV() {
        try {
            File exportDir = new File(getCacheDir(), "exports");
            if (!exportDir.exists()) exportDir.mkdirs();
            File file = new File(exportDir, "call_logs.csv");

            FileWriter writer = new FileWriter(file);
            writer.append("ID,Contact,Phone,Type,Note,Timestamp\n");

            for (CallLogModel log : logsList) {
                writer.append(String.valueOf(log.id)).append(",");
                String contact = log.contactName != null ? log.contactName.replace("\"", "\"\"") : "";
                writer.append("\"").append(contact).append("\",");
                writer.append("\"").append(log.phoneNumber).append("\",");
                writer.append("\"").append(log.callType).append("\",");
                String note = log.note != null ? log.note.replace("\"", "\"\"") : "";
                writer.append("\"").append(note).append("\",");
                writer.append(String.valueOf(log.timestamp)).append("\n");
            }
            writer.flush();
            writer.close();

            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Export Call Logs"));

        } catch (Exception e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLogs("");
    }

    private void loadLogs(String query) {
        logsList.clear();
        logsList.addAll(dbHelper.getAllLogs(query));
        adapter.notifyDataSetChanged();
        updateDashboardStats();
    }

    private void updateDashboardStats() {
        int incoming = 0, outgoing = 0, missed = 0;
        for (CallLogModel log : logsList) {
            String type = log.callType != null ? log.callType.toLowerCase() : "";
            if (type.contains("incoming")) incoming++;
            else if (type.contains("outgoing")) outgoing++;
            else if (type.contains("missed")) missed++;
        }

        TextView tvIncoming = findViewById(R.id.tvStatIncoming);
        TextView tvOutgoing = findViewById(R.id.tvStatOutgoing);
        TextView tvMissed = findViewById(R.id.tvStatMissed);

        if (tvIncoming != null) tvIncoming.setText(String.valueOf(incoming));
        if (tvOutgoing != null) tvOutgoing.setText(String.valueOf(outgoing));
        if (tvMissed != null) tvMissed.setText(String.valueOf(missed));
    }

    private void checkPermissions() {
        List<String> needed = new ArrayList<>();
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            needed.add(Manifest.permission.READ_PHONE_STATE);
            
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            needed.add(Manifest.permission.READ_CALL_LOG);
            
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            needed.add(Manifest.permission.READ_CONTACTS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                needed.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!needed.isEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toArray(new String[0]), PERMISSION_REQ_CODE);
        }
    }
}
