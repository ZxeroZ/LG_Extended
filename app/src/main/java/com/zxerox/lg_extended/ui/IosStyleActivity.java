package com.zxerox.lg_extended.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.zxerox.lg_extended.R;

public class IosStyleActivity extends AppCompatActivity {

    private static final Uri PREFS_URI = Uri.parse("content://com.zxerox.lg_extended.prefs/prefs");
    private MaterialSwitch switchIosRecents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ios_style);

        switchIosRecents = findViewById(R.id.switchIosRecents);

        boolean isEnabled = leerEstadoGuardado("recents_enabled", false);
        switchIosRecents.setChecked(isEnabled);

        switchIosRecents.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ContentValues values = new ContentValues();
            values.put("key", "recents_enabled");
            values.put("type", "boolean");
            values.put("value", isChecked);
            getContentResolver().insert(PREFS_URI, values);

            Toast.makeText(this, "Restart Launcher to apply changes", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnRegresar).setOnClickListener(v -> finish());
    }

    private boolean leerEstadoGuardado(String key, boolean fallback) {
        try {
            Cursor c = getContentResolver().query(
                    PREFS_URI,
                    new String[]{key},
                    "boolean",
                    new String[]{String.valueOf(fallback)},
                    null
            );
            if (c != null && c.moveToFirst()) {
                String valor = c.getString(0);
                c.close();
                if (valor != null && !valor.isEmpty()) {
                    return Boolean.parseBoolean(valor);
                }
            }
        } catch (Throwable ignored) {}
        return fallback;
    }
}