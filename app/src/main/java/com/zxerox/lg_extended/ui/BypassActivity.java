package com.zxerox.lg_extended.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.zxerox.lg_extended.R;

public class BypassActivity extends AppCompatActivity {

    private static final Uri PREFS_URI = Uri.parse("content://com.zxerox.lg_extended.prefs/prefs");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bypass);

        findViewById(R.id.btnRegresar).setOnClickListener(v -> finish());

        Switch switchFlagSecure = findViewById(R.id.switchFlagSecure);
        switchFlagSecure.setChecked(leerEstadoGuardado());

        switchFlagSecure.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            guardarEstado(isChecked);
        });
    }

    private boolean leerEstadoGuardado() {
        try {
            Cursor c = getContentResolver().query(
                    PREFS_URI,
                    new String[]{"bypass_flag_secure"},
                    "boolean", new String[]{"true"}, null
            );
            if (c != null && c.moveToFirst()) {
                String valor = c.getString(0);
                c.close();
                return Boolean.parseBoolean(valor);
            }
        } catch (Throwable ignored) {}
        return true;
    }

    private void guardarEstado(boolean activo) {
        ContentValues values = new ContentValues();
        values.put("key", "bypass_flag_secure");
        values.put("type", "boolean");
        values.put("value", activo);
        getContentResolver().insert(PREFS_URI, values);
    }
}