package com.zxerox.lg_extended.log;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zxerox.lg_extended.prefs.ModPrefs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogWriter {

    private static final String KEY_LOGS = "module_logs";
    private static final String KEY_LOG_COUNT = "module_log_count";
    private static final int MAX_LOGS = 200;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static class LogEntry {
        public String timestamp;
        public String level;
        public String message;

        public LogEntry(String timestamp, String level, String message) {
            this.timestamp = timestamp;
            this.level = level;
            this.message = message;
        }
    }

    public static void write(Context context, String level, String message) {
        try {
            ContentResolver resolver = context.getContentResolver();
            String timestamp = SDF.format(new Date());
            String line = timestamp + " | " + level + " | " + message;

            String existing = readRawLogs(resolver);
            int count = readCount(resolver);

            if (count >= MAX_LOGS) {
                int firstNewline = existing.indexOf('\n');
                if (firstNewline >= 0) {
                    existing = existing.substring(firstNewline + 1);
                }
                count--;
            }

            if (!existing.isEmpty()) {
                existing += "\n" + line;
            } else {
                existing = line;
            }
            count++;

            ContentValues cv = new ContentValues();
            cv.put("key", KEY_LOGS);
            cv.put("type", "string");
            cv.put("value", existing);
            resolver.insert(ModPrefs.CONTENT_URI, cv);

            ContentValues cvCount = new ContentValues();
            cvCount.put("key", KEY_LOG_COUNT);
            cvCount.put("type", "int");
            cvCount.put("value", count);
            resolver.insert(ModPrefs.CONTENT_URI, cvCount);
        } catch (Throwable ignored) {}
    }

    public static void write(Context context, String level, String hookName, String packageName, boolean success) {
        String status = success ? "applied" : "FAILED";
        String message = hookName + " " + status + " in " + packageName;
        write(context, success ? "OK" : "ERR", message);
    }

    public static List<LogEntry> readLogs(Context context) {
        List<LogEntry> entries = new ArrayList<>();
        try {
            String raw = readRawLogs(context.getContentResolver());
            if (raw.isEmpty()) return entries;

            String[] lines = raw.split("\n");
            for (String line : lines) {
                LogEntry entry = parseLine(line.trim());
                if (entry != null) {
                    entries.add(entry);
                }
            }
        } catch (Throwable ignored) {}
        return entries;
    }

    public static void clearLogs(Context context) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("key", KEY_LOGS);
            cv.put("type", "string");
            cv.put("value", "");
            context.getContentResolver().insert(ModPrefs.CONTENT_URI, cv);

            ContentValues cvCount = new ContentValues();
            cvCount.put("key", KEY_LOG_COUNT);
            cvCount.put("type", "int");
            cvCount.put("value", 0);
            context.getContentResolver().insert(ModPrefs.CONTENT_URI, cvCount);
        } catch (Throwable ignored) {}
    }

    private static String readRawLogs(ContentResolver resolver) {
        try {
            Cursor c = resolver.query(ModPrefs.CONTENT_URI,
                    new String[]{KEY_LOGS}, null, new String[]{""}, null);
            if (c != null && c.moveToFirst()) {
                String val = c.getString(0);
                c.close();
                return val != null ? val : "";
            }
        } catch (Throwable ignored) {}
        return "";
    }

    private static int readCount(ContentResolver resolver) {
        try {
            Cursor c = resolver.query(ModPrefs.CONTENT_URI,
                    new String[]{KEY_LOG_COUNT}, "int", new String[]{"0"}, null);
            if (c != null && c.moveToFirst()) {
                int val = Integer.parseInt(c.getString(0));
                c.close();
                return val;
            }
        } catch (Throwable ignored) {}
        return 0;
    }

    private static LogEntry parseLine(String line) {
        String[] parts = line.split(" \\| ", 3);
        if (parts.length >= 3) {
            return new LogEntry(parts[0].trim(), parts[1].trim(), parts[2].trim());
        }
        return null;
    }
}
