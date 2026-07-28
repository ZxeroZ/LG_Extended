package com.zxerox.lg_extended.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxerox.lg_extended.R;
import com.zxerox.lg_extended.log.LogAdapter;
import com.zxerox.lg_extended.log.LogWriter;
import com.zxerox.lg_extended.prefs.ModPrefs;
import com.zxerox.lg_extended.root.DeviceInfoProvider;
import com.zxerox.lg_extended.root.RootUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FrameLayout contentFrame;
    private LinearLayout navHome, navHooks, navLogs, navSettings;
    private ImageView navHomeIcon, navHooksIcon, navLogsIcon, navSettingsIcon;
    private TextView navHomeLabel, navHooksLabel, navLogsLabel, navSettingsLabel;
    private int currentTab = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);


        contentFrame = findViewById(R.id.contentFrame);
        navHome = findViewById(R.id.navHome);
        navHooks = findViewById(R.id.navHooks);
        navLogs = findViewById(R.id.navLogs);
        navSettings = findViewById(R.id.navSettings);

        navHomeIcon = findViewById(R.id.navHomeIcon);
        navHooksIcon = findViewById(R.id.navHooksIcon);
        navLogsIcon = findViewById(R.id.navLogsIcon);
        navSettingsIcon = findViewById(R.id.navSettingsIcon);

        navHomeLabel = findViewById(R.id.navHomeLabel);
        navHooksLabel = findViewById(R.id.navHooksLabel);
        navLogsLabel = findViewById(R.id.navLogsLabel);
        navSettingsLabel = findViewById(R.id.navSettingsLabel);

        navHome.setOnClickListener(v -> switchTab(0));
        navHooks.setOnClickListener(v -> switchTab(1));
        navLogs.setOnClickListener(v -> switchTab(2));
        navSettings.setOnClickListener(v -> switchTab(3));

        switchTab(0);

        findViewById(R.id.btnRestart).setOnClickListener(v ->
                RootUtils.reiniciarSystemUI(() -> runOnUiThread(() ->
                        Toast.makeText(this, "SystemUI reiniciado", Toast.LENGTH_SHORT).show())));
    }

    private void switchTab(int index) {
        currentTab = index;
        updateNavColors();

        View tabView;
        switch (index) {
            case 1:
                tabView = LayoutInflater.from(this).inflate(R.layout.tab_hooks, contentFrame, false);
                setupHooksTab(tabView);
                break;
            case 2:
                tabView = LayoutInflater.from(this).inflate(R.layout.tab_logs, contentFrame, false);
                setupLogsTab(tabView);
                break;
            case 3:
                tabView = LayoutInflater.from(this).inflate(R.layout.tab_settings, contentFrame, false);
                setupSettingsTab(tabView);
                break;
            case 0:
            default:
                tabView = LayoutInflater.from(this).inflate(R.layout.tab_inicio, contentFrame, false);
                setupInicioTab(tabView);
                break;
        }

        contentFrame.removeAllViews();
        contentFrame.addView(tabView);
    }

    private void updateNavColors() {
        int active = getColor(R.color.color_nav_active);
        int inactive = getColor(R.color.color_nav_inactive);

        navHomeIcon.setColorFilter(currentTab == 0 ? active : inactive);
        navHooksIcon.setColorFilter(currentTab == 1 ? active : inactive);
        navLogsIcon.setColorFilter(currentTab == 2 ? active : inactive);
        navSettingsIcon.setColorFilter(currentTab == 3 ? active : inactive);

        navHomeLabel.setTextColor(currentTab == 0 ? active : inactive);
        navHomeLabel.setTypeface(null, currentTab == 0 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        navHooksLabel.setTextColor(currentTab == 1 ? active : inactive);
        navHooksLabel.setTypeface(null, currentTab == 1 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        navLogsLabel.setTextColor(currentTab == 2 ? active : inactive);
        navLogsLabel.setTypeface(null, currentTab == 2 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        navSettingsLabel.setTextColor(currentTab == 3 ? active : inactive);
        navSettingsLabel.setTypeface(null, currentTab == 3 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
    }

    private void setupInicioTab(View view) {
        TextView lsposedDetail = view.findViewById(R.id.lsposedDetail);
        TextView rootDetail = view.findViewById(R.id.rootDetail);
        TextView magiskDetail = view.findViewById(R.id.magiskDetail);
        TextView deviceModel = view.findViewById(R.id.deviceModel);
        TextView androidVersion = view.findViewById(R.id.androidVersion);
        TextView kernelVersion = view.findViewById(R.id.kernelVersion);
        TextView deviceArch = view.findViewById(R.id.deviceArch);

        // LSPosed detection via hook flags
        boolean hookActive = false;
        try {
            Cursor c = getContentResolver().query(
                    ModPrefs.CONTENT_URI,
                    new String[]{"hook_active_battery"},
                    "boolean", new String[]{"false"}, null);
            if (c != null && c.moveToFirst()) {
                hookActive = Boolean.parseBoolean(c.getString(0));
                c.close();
            }
        } catch (Throwable ignored) {}

        // Fallback: also check other hooks
        if (!hookActive) {
            try {
                Cursor c = getContentResolver().query(
                        ModPrefs.CONTENT_URI,
                        new String[]{"hook_active_dpi"},
                        "boolean", new String[]{"false"}, null);
                if (c != null && c.moveToFirst()) {
                    hookActive = Boolean.parseBoolean(c.getString(0));
                    c.close();
                }
            } catch (Throwable ignored) {}
        }

        if (hookActive) {
            lsposedDetail.setText(R.string.lsposed_active);
            lsposedDetail.setTextColor(getColor(R.color.color_success));
        } else {
            lsposedDetail.setText(R.string.lsposed_inactive);
            lsposedDetail.setTextColor(getColor(R.color.color_danger));
        }

        DeviceInfoProvider.fetch(data -> runOnUiThread(() -> {
            deviceModel.setText(data.deviceModel);
            androidVersion.setText(data.androidVersion + " (API " + android.os.Build.VERSION.SDK_INT + ")");
            deviceArch.setText(data.arch);

            if (data.hasRoot) {
                rootDetail.setText(R.string.root_granted);
                rootDetail.setTextColor(getColor(R.color.color_success));
                kernelVersion.setText(data.kernelVersion);
                magiskDetail.setText(data.rootManager);
            } else {
                rootDetail.setText(R.string.root_denied);
                rootDetail.setTextColor(getColor(R.color.color_danger));
                kernelVersion.setText("N/A (no root)");
                magiskDetail.setText("N/A");
            }
        }));
    }

    private void setupHooksTab(View view) {
        view.findViewById(R.id.btnBatteryStyle).setOnClickListener(v ->
                startActivity(new Intent(this, BatteryStyleActivity.class)));

        view.findViewById(R.id.btnIosStyle).setOnClickListener(v ->
                startActivity(new Intent(this, IosStyleActivity.class)));

        view.findViewById(R.id.btnDpi).setOnClickListener(v ->
                startActivity(new Intent(this, DpiActivity.class)));

        view.findViewById(R.id.btnBypass).setOnClickListener(v ->
                startActivity(new Intent(this, BypassActivity.class)));

        view.findViewById(R.id.btnCustomizeSettings).setOnClickListener(v ->
                startActivity(new Intent(this, CustomizeSettingsActivity.class)));

        android.widget.Switch switchSettingsIcons = view.findViewById(R.id.switchSettingsIcons);
        boolean settingsIconsEnabled = false;
        try {
            Cursor c = getContentResolver().query(
                    ModPrefs.CONTENT_URI,
                    new String[]{"hook_settings_icons"},
                    "boolean", new String[]{"false"}, null);
            if (c != null && c.moveToFirst()) {
                settingsIconsEnabled = Boolean.parseBoolean(c.getString(0));
                c.close();
            }
        } catch (Throwable ignored) {}

        switchSettingsIcons.setChecked(settingsIconsEnabled);

        view.findViewById(R.id.btnSettingsIcons).setOnClickListener(v -> {
            boolean newState = !switchSettingsIcons.isChecked();
            switchSettingsIcons.setChecked(newState);
            
            ContentValues values = new ContentValues();
            values.put("key", "hook_settings_icons");
            values.put("type", "boolean");
            values.put("value", String.valueOf(newState));
            getContentResolver().insert(ModPrefs.CONTENT_URI, values);
            
            killSettingsApp();
        });
    }

    private void killSettingsApp() {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "am force-stop com.android.settings"});
            android.widget.Toast.makeText(this, "Ajustes reiniciados para aplicar cambios", android.widget.Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Ajustes actualizados. Cierra la app de Ajustes manualmente.", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void setupLogsTab(View view) {
        RecyclerView recycler = view.findViewById(R.id.recyclerLogs);
        View emptyState = view.findViewById(R.id.emptyState);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        java.util.List<LogWriter.LogEntry> logs = LogWriter.readLogs(this);
        LogAdapter adapter = new LogAdapter(new ArrayList<>(logs));
        recycler.setAdapter(adapter);

        if (logs.isEmpty()) {
            recycler.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }

        view.findViewById(R.id.btnClearLogs).setOnClickListener(v -> {
            LogWriter.clearLogs(this);
            adapter.updateData(new ArrayList<>());
            recycler.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Logs cleared", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSettingsTab(View view) {
        // Now empty. UI moved to CustomizeSettingsActivity.
    }
}
