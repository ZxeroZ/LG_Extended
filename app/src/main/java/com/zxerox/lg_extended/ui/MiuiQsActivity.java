package com.zxerox.lg_extended.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zxerox.lg_extended.R;

public class MiuiQsActivity extends AppCompatActivity {

    private static final Uri PREFS_URI = Uri.parse("content://com.zxerox.lg_extended.prefs/prefs");

    private Switch switchMiuiQs;
    private Switch switchHideArrow;
    private Switch switchSeparateCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miui_qs);

        switchMiuiQs = findViewById(R.id.switchMiuiQs);
        switchHideArrow = findViewById(R.id.switchHideArrow);
        switchSeparateCards = findViewById(R.id.switchSeparateCards);

        loadPreferences();

        findViewById(R.id.btnMiuiQs).setOnClickListener(v -> {
            boolean newState = !switchMiuiQs.isChecked();
            switchMiuiQs.setChecked(newState);
            savePreference("pref_enable_miui_qs", newState);
            
            try {
                Runtime.getRuntime().exec(new String[]{"su", "-c", "echo " + newState + " > /data/local/tmp/lg_ext_miui_qs && chmod 666 /data/local/tmp/lg_ext_miui_qs"});
            } catch (Exception ignored) {}
            
            Toast.makeText(this, "Ajuste actualizado. Reinicia SystemUI para aplicar.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnHideArrow).setOnClickListener(v -> {
            boolean newState = !switchHideArrow.isChecked();
            switchHideArrow.setChecked(newState);
            savePreference("pref_hide_notification_arrow", newState);
            
            try {
                Runtime.getRuntime().exec(new String[]{"su", "-c", "echo " + newState + " > /data/local/tmp/lg_ext_hide_arrow && chmod 666 /data/local/tmp/lg_ext_hide_arrow"});
            } catch (Exception ignored) {}
            
            Toast.makeText(this, "Ajuste actualizado. Reinicia SystemUI para aplicar.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnSeparateCards).setOnClickListener(v -> {
            boolean newState = !switchSeparateCards.isChecked();
            switchSeparateCards.setChecked(newState);
            savePreference("pref_separate_notification_cards", newState);
            
            try {
                Runtime.getRuntime().exec(new String[]{"su", "-c", "echo " + newState + " > /data/local/tmp/lg_ext_separate_cards && chmod 666 /data/local/tmp/lg_ext_separate_cards"});
            } catch (Exception ignored) {}
            
            Toast.makeText(this, "Ajuste actualizado. Reinicia SystemUI para aplicar.", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadPreferences() {
        switchMiuiQs.setChecked(getBooleanPref("pref_enable_miui_qs", false));
        switchHideArrow.setChecked(getBooleanPref("pref_hide_notification_arrow", false));
        switchSeparateCards.setChecked(getBooleanPref("pref_separate_notification_cards", false));
    }

    private boolean getBooleanPref(String key, boolean defValue) {
        boolean result = defValue;
        try {
            Cursor c = getContentResolver().query(
                    PREFS_URI,
                    new String[]{key},
                    "boolean", new String[]{String.valueOf(defValue)}, null);
            if (c != null && c.moveToFirst()) {
                result = Boolean.parseBoolean(c.getString(0));
                c.close();
            }
        } catch (Throwable ignored) {}
        return result;
    }

    private void savePreference(String key, boolean value) {
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("type", "boolean");
        values.put("value", String.valueOf(value));
        getContentResolver().insert(PREFS_URI, values);
    }
}
