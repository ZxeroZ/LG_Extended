package com.zxerox.lg_extended.ui;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zxerox.lg_extended.R;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private List<ApplicationInfo> appList;
    private PackageManager packageManager;
    private SharedPreferences prefs;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ApplicationInfo appInfo);
    }

    public AppAdapter(List<ApplicationInfo> appList, PackageManager packageManager, SharedPreferences prefs, OnItemClickListener listener) {
        this.appList = appList;
        this.packageManager = packageManager;
        this.prefs = prefs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_light, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        ApplicationInfo appInfo = appList.get(position);

        holder.tvAppName.setText(packageManager.getApplicationLabel(appInfo));
        holder.tvAppPackage.setText(appInfo.packageName);
        holder.imgAppIcon.setImageDrawable(packageManager.getApplicationIcon(appInfo));

        int currentDpi = prefs.getInt(appInfo.packageName, 0);
        if (currentDpi > 0) {
            holder.tvAppDpi.setText(currentDpi + " DPI");
            holder.tvAppDpi.setVisibility(View.VISIBLE);
        } else {
            holder.tvAppDpi.setText("Default");
            holder.tvAppDpi.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(appInfo));
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAppIcon;
        TextView tvAppName, tvAppPackage, tvAppDpi;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAppIcon = itemView.findViewById(R.id.imgAppIcon);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvAppPackage = itemView.findViewById(R.id.tvAppPackage);
            tvAppDpi = itemView.findViewById(R.id.tvAppDpi);
        }
    }
}