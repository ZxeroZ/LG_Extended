package com.lge.launcher3.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class SharedPreferencesManager {
    private static SharedPreferences getSharedPreferences(Context context, int mode) {
        return context.getSharedPreferences(SharedPreferencesConst.PREFERENCES_FILE_NAME, mode);
    }

    public static void clear(Context context, int mode) {
        SharedPreferences sharedPreferences = getSharedPreferences(context, mode);
        if (sharedPreferences == null || sharedPreferences.edit() == null) {
            return;
        }
        sharedPreferences.edit().clear();
        sharedPreferences.edit().commit();
    }

    public static final String toKeyString(SharedPreferencesConst.PreferenceKey key) {
        return key.getClass().getSimpleName().toLowerCase(Locale.ENGLISH) + "_" + key.toString().toLowerCase(Locale.ENGLISH);
    }

    public static boolean getBoolean(Context context, int mode, SharedPreferencesConst.PreferenceKey key, boolean defValue) {
        return getSharedPreferences(context, mode).getBoolean(toKeyString(key), defValue);
    }

    public static boolean putBoolean(Context context, int mode, SharedPreferencesConst.PreferenceKey key, boolean value) {
        return getSharedPreferences(context, mode).edit().putBoolean(toKeyString(key), value).commit();
    }

    public static String getString(Context context, int mode, SharedPreferencesConst.PreferenceKey key, String defValue) {
        return getSharedPreferences(context, mode).getString(toKeyString(key), defValue);
    }

    public static boolean putString(Context context, int mode, SharedPreferencesConst.PreferenceKey key, String string) {
        return getSharedPreferences(context, mode).edit().putString(toKeyString(key), string).commit();
    }

    public static int getInt(Context context, int mode, SharedPreferencesConst.PreferenceKey key, int defValue) {
        return getSharedPreferences(context, mode).getInt(toKeyString(key), defValue);
    }

    public static boolean putInt(Context context, int mode, SharedPreferencesConst.PreferenceKey key, int value) {
        return getSharedPreferences(context, mode).edit().putInt(toKeyString(key), value).commit();
    }

    public static long getLong(Context context, int mode, SharedPreferencesConst.PreferenceKey key, long defValue) {
        return getSharedPreferences(context, mode).getLong(toKeyString(key), defValue);
    }

    public static boolean putLong(Context context, int mode, SharedPreferencesConst.PreferenceKey key, long value) {
        return getSharedPreferences(context, mode).edit().putLong(toKeyString(key), value).commit();
    }

    public static float getFloat(Context context, int mode, SharedPreferencesConst.PreferenceKey key, float defValue) {
        return getSharedPreferences(context, mode).getFloat(toKeyString(key), defValue);
    }

    public static boolean putFloat(Context context, int mode, SharedPreferencesConst.PreferenceKey key, float value) {
        return getSharedPreferences(context, mode).edit().putFloat(toKeyString(key), value).commit();
    }

    public static void registerOnSharedPreferenceChangeListener(Context context, int mode, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences(context, mode).registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(Context context, int mode, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences(context, mode).unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static boolean contains(Context context, int mode, SharedPreferencesConst.PreferenceKey key) {
        return getSharedPreferences(context, mode).contains(toKeyString(key));
    }

    public static void remove(Context context, int mode, SharedPreferencesConst.PreferenceKey key) {
        getSharedPreferences(context, mode).edit().remove(toKeyString(key)).commit();
    }
}
