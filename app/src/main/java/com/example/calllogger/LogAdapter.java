package com.example.calllogger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<CallLogModel> logList;

    public LogAdapter(List<CallLogModel> logList) {
        this.logList = logList;
    }

    public void updateList(List<CallLogModel> newList) {
        this.logList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        CallLogModel log = logList.get(position);
        
        holder.tvContact.setText(log.contactName != null && !log.contactName.isEmpty() ? log.contactName : log.phoneNumber);
        holder.tvType.setText(log.callType);
        holder.tvNote.setText(log.note);
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(log.timestamp)));

        holder.itemView.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                .setTitle("Call Details")
                .setMessage("Contact: " + (log.contactName != null ? log.contactName : "Unknown") + "\n" +
                            "Phone: " + log.phoneNumber + "\n" +
                            "Type: " + log.callType + "\n\n" +
                            "Note: \n" + log.note)
                .setPositiveButton("Close", null)
                .show();
        });
    }

    @Override
    public int getItemCount() {
        return logList != null ? logList.size() : 0;
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvContact, tvDate, tvType, tvNote;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContact = itemView.findViewById(R.id.itemContact);
            tvDate = itemView.findViewById(R.id.itemDate);
            tvType = itemView.findViewById(R.id.itemType);
            tvNote = itemView.findViewById(R.id.itemNote);
        }
    }
}
