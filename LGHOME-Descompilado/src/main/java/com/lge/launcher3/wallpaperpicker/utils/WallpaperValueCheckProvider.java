package com.lge.launcher3.wallpaperpicker.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import com.lge.launcher3.R;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperValueCheckProvider extends ContentProvider {
    private static final String BOOLEAN_TYPE = "boolean";
    private static final String FLOAT_TYPE = "float";
    private static final String INT_TYPE = "integer";
    private static final String KEY = "key";
    private static final String LONG_TYPE = "long";
    private static final int MATCH_DATA = 65536;
    private static final String STRING_TYPE = "string";
    private static final String TYPE = "type";
    public static Uri sBaseURI;
    private static UriMatcher sMatcher;
    public static String sPREFFERENCE_AUTHORITY;

    private static void init(Context context) {
        sPREFFERENCE_AUTHORITY = context.getString(R.string.wallpaperpicker_autority);
        UriMatcher uriMatcher = new UriMatcher(-1);
        sMatcher = uriMatcher;
        uriMatcher.addURI(sPREFFERENCE_AUTHORITY, "*/*", 65536);
        sBaseURI = Uri.parse("content://" + sPREFFERENCE_AUTHORITY);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        if (sMatcher != null) {
            return true;
        }
        init(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return "vnd.android.cursor.item/vnd." + sPREFFERENCE_AUTHORITY + ".item";
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (sMatcher.match(uri) == 65536) {
            PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).edit().clear().commit();
            return 0;
        }
        throw new IllegalArgumentException("Unsupported uri " + uri);
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        if (sMatcher.match(uri) == 65536) {
            SharedPreferences.Editor editorEdit = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext()).edit();
            for (Map.Entry<String, Object> entry : values.valueSet()) {
                Object value = entry.getValue();
                String key = entry.getKey();
                if (value == null) {
                    editorEdit.remove(key);
                } else if (value instanceof String) {
                    editorEdit.putString(key, (String) value);
                } else if (value instanceof Boolean) {
                    editorEdit.putBoolean(key, ((Boolean) value).booleanValue());
                } else if (value instanceof Long) {
                    editorEdit.putLong(key, ((Long) value).longValue());
                } else if (value instanceof Integer) {
                    editorEdit.putInt(key, ((Integer) value).intValue());
                } else if (value instanceof Float) {
                    editorEdit.putFloat(key, ((Float) value).floatValue());
                } else {
                    throw new IllegalArgumentException("Unsupported type " + uri);
                }
            }
            if (Build.VERSION.SDK_INT > 8) {
                editorEdit.apply();
                return null;
            }
            editorEdit.commit();
            return null;
        }
        throw new IllegalArgumentException("Unsupported uri " + uri);
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Object objValueOf;
        if (sMatcher.match(uri) == 65536) {
            String str3 = uri.getPathSegments().get(0);
            String str4 = uri.getPathSegments().get(1);
            MatrixCursor matrixCursor = new MatrixCursor(new String[]{str3});
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
            if (!defaultSharedPreferences.contains(str3)) {
                return matrixCursor;
            }
            MatrixCursor.RowBuilder rowBuilderNewRow = matrixCursor.newRow();
            if (STRING_TYPE.equals(str4)) {
                objValueOf = defaultSharedPreferences.getString(str3, null);
            } else if (BOOLEAN_TYPE.equals(str4)) {
                objValueOf = Integer.valueOf(defaultSharedPreferences.getBoolean(str3, false) ? 1 : 0);
            } else if (LONG_TYPE.equals(str4)) {
                objValueOf = Long.valueOf(defaultSharedPreferences.getLong(str3, 0L));
            } else if ("integer".equals(str4)) {
                objValueOf = Integer.valueOf(defaultSharedPreferences.getInt(str3, 0));
            } else if (FLOAT_TYPE.equals(str4)) {
                objValueOf = Float.valueOf(defaultSharedPreferences.getFloat(str3, 0.0f));
            } else {
                throw new IllegalArgumentException("Unsupported type " + uri);
            }
            rowBuilderNewRow.add(objValueOf);
            return matrixCursor;
        }
        throw new IllegalArgumentException("Unsupported uri " + uri);
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getStringValue(Cursor cursor, String def) {
        if (cursor == null) {
            return def;
        }
        if (cursor.moveToFirst()) {
            def = cursor.getString(0);
        }
        cursor.close();
        return def;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean getBooleanValue(Cursor cursor, boolean def) {
        if (cursor == null) {
            return def;
        }
        if (cursor.moveToFirst()) {
            def = false;
            if (cursor.getInt(0) > 0) {
                def = true;
            }
        }
        cursor.close();
        return def;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getIntValue(Cursor cursor, int def) {
        if (cursor == null) {
            return def;
        }
        if (cursor.moveToFirst()) {
            def = cursor.getInt(0);
        }
        cursor.close();
        return def;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long getLongValue(Cursor cursor, long def) {
        if (cursor == null) {
            return def;
        }
        if (cursor.moveToFirst()) {
            def = cursor.getLong(0);
        }
        cursor.close();
        return def;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float getFloatValue(Cursor cursor, float def) {
        if (cursor == null) {
            return def;
        }
        if (cursor.moveToFirst()) {
            def = cursor.getFloat(0);
        }
        cursor.close();
        return def;
    }

    public static Editor edit(Context context) {
        return new Editor(context);
    }

    public static WallpaperSettingsSharedPreferences getDefaultSharedPreferences(Context context) {
        return new WallpaperSettingsSharedPreferences(context);
    }

    public static class Editor {
        Context context;
        private ContentValues values;

        private Editor(Context context) {
            this.values = new ContentValues();
            this.context = context;
        }

        public void apply() {
            this.context.getContentResolver().insert(WallpaperValueCheckProvider.getContentUri(this.context, "key", "type"), this.values);
        }

        public void commit() {
            apply();
        }

        public Editor putString(String key, String value) {
            this.values.put(key, value);
            return this;
        }

        public Editor putLong(String key, long value) {
            this.values.put(key, Long.valueOf(value));
            return this;
        }

        public Editor putBoolean(String key, boolean value) {
            this.values.put(key, Boolean.valueOf(value));
            return this;
        }

        public Editor putInt(String key, int value) {
            this.values.put(key, Integer.valueOf(value));
            return this;
        }

        public Editor putFloat(String key, float value) {
            this.values.put(key, Float.valueOf(value));
            return this;
        }

        public void remove(String key) {
            this.values.putNull(key);
        }

        public void clear() {
            this.context.getContentResolver().delete(WallpaperValueCheckProvider.getContentUri(this.context, "key", "type"), null, null);
        }
    }

    public static class WallpaperSettingsSharedPreferences {
        private Context context;

        private WallpaperSettingsSharedPreferences(Context context) {
            this.context = context;
        }

        public Editor edit() {
            return new Editor(this.context);
        }

        public String getString(String key, String def) {
            return WallpaperValueCheckProvider.getStringValue(this.context.getContentResolver().query(WallpaperValueCheckProvider.getContentUri(this.context, key, WallpaperValueCheckProvider.STRING_TYPE), null, null, null, null), def);
        }

        public long getLong(String key, long def) {
            return WallpaperValueCheckProvider.getLongValue(this.context.getContentResolver().query(WallpaperValueCheckProvider.getContentUri(this.context, key, WallpaperValueCheckProvider.LONG_TYPE), null, null, null, null), def);
        }

        public float getFloat(String key, float def) {
            return WallpaperValueCheckProvider.getFloatValue(this.context.getContentResolver().query(WallpaperValueCheckProvider.getContentUri(this.context, key, WallpaperValueCheckProvider.FLOAT_TYPE), null, null, null, null), def);
        }

        public boolean getBoolean(String key, boolean def) {
            return WallpaperValueCheckProvider.getBooleanValue(this.context.getContentResolver().query(WallpaperValueCheckProvider.getContentUri(this.context, key, WallpaperValueCheckProvider.BOOLEAN_TYPE), null, null, null, null), def);
        }

        public int getInt(String key, int def) {
            return WallpaperValueCheckProvider.getIntValue(this.context.getContentResolver().query(WallpaperValueCheckProvider.getContentUri(this.context, key, "integer"), null, null, null, null), def);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Uri getContentUri(Context context, String key, String type) {
        if (sBaseURI == null) {
            init(context);
        }
        return sBaseURI.buildUpon().appendPath(key).appendPath(type).build();
    }
}
