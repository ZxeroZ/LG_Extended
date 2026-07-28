package com.zxerox.lg_extended.prefs;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.zxerox.lg_extended.log.LogWriter;

public class ModPrefs extends ContentProvider {

    public static final String AUTHORITY = "com.zxerox.lg_extended.prefs";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/prefs");

    private static final String PREFS_NAME = "lg_extended_prefs";

    private SharedPreferences prefs() {
        return getContext().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (projection == null || projection.length == 0) return null;
        String key = projection[0];

        MatrixCursor cursor = new MatrixCursor(new String[]{"value"});
        SharedPreferences p = prefs();

        Object valor = p.getAll().get(key);
        if (valor == null) {
            valor = (selectionArgs != null && selectionArgs.length > 0) ? selectionArgs[0] : "";
        }

        cursor.addRow(new Object[]{String.valueOf(valor)});
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (values == null) return null;

        String key = values.getAsString("key");
        String type = values.getAsString("type");

        if ("__log_entry".equals(key) && "log".equals(type)) {
            String raw = values.getAsString("value");
            if (raw != null) {
                String[] parts = raw.split(" \\| ", 2);
                if (parts.length == 2) {
                    LogWriter.write(getContext(), parts[0].trim(), parts[1].trim());
                } else {
                    LogWriter.write(getContext(), "INFO", raw);
                }
            }
            return uri;
        }

        SharedPreferences.Editor editor = prefs().edit();

        if ("int".equals(type)) {
            editor.putInt(key, values.getAsInteger("value"));
        } else if ("boolean".equals(type)) {
            editor.putBoolean(key, values.getAsBoolean("value"));
        } else {
            editor.putString(key, values.getAsString("value"));
        }
        editor.apply();

        hacerPrefsLegibles();

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return uri;
    }

    private void hacerPrefsLegibles() {
        try {
            java.io.File prefsFile = new java.io.File(
                    "/data/data/" + getContext().getPackageName() + "/shared_prefs/" + PREFS_NAME + ".xml"
            );
            prefsFile.setReadable(true, false);
        } catch (Exception e) {
            android.util.Log.e("ModPrefs", "No se pudo hacer legible el archivo de prefs: " + e.getMessage());
        }
    }

    @Override
    public String getType(Uri uri) { return null; }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }
}