package com.zxerox.lg_extended.log;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zxerox.lg_extended.R;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<LogWriter.LogEntry> entries;

    public LogAdapter(List<LogWriter.LogEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogWriter.LogEntry entry = entries.get(position);

        holder.logMessage.setText(entry.message);
        holder.logTimestamp.setText(entry.timestamp);

        holder.logLevel.setText(entry.level);
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setCornerRadius(8f);

        int indicatorColor;
        int badgeTextColor;

        switch (entry.level) {
            case "OK":
                badgeBg.setColor(Color.parseColor("#E8F5E9"));
                badgeTextColor = Color.parseColor("#34C759");
                indicatorColor = Color.parseColor("#34C759");
                break;
            case "ERR":
                badgeBg.setColor(Color.parseColor("#FFEBEE"));
                badgeTextColor = Color.parseColor("#FF3B30");
                indicatorColor = Color.parseColor("#FF3B30");
                break;
            default:
                badgeBg.setColor(Color.parseColor("#E3F2FD"));
                badgeTextColor = Color.parseColor("#007AFF");
                indicatorColor = Color.parseColor("#007AFF");
                break;
        }

        holder.logLevel.setBackground(badgeBg);
        holder.logLevel.setTextColor(badgeTextColor);

        GradientDrawable dot = new GradientDrawable();
        dot.setShape(GradientDrawable.OVAL);
        dot.setColor(indicatorColor);
        holder.logIndicator.setBackground(dot);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void updateData(List<LogWriter.LogEntry> newEntries) {
        this.entries = newEntries;
        notifyDataSetChanged();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        View logIndicator;
        TextView logMessage, logTimestamp, logLevel;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            logIndicator = itemView.findViewById(R.id.logIndicator);
            logMessage = itemView.findViewById(R.id.logMessage);
            logTimestamp = itemView.findViewById(R.id.logTimestamp);
            logLevel = itemView.findViewById(R.id.logLevel);
        }
    }
}
