package androidx.core.os;

import android.os.Build;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class EnvironmentCompat {
    public static final String MEDIA_UNKNOWN = "unknown";
    private static final String TAG = "EnvironmentCompat";

    public static String getStorageState(File path) {
        if (Build.VERSION.SDK_INT >= 21) {
            return Environment.getExternalStorageState(path);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            return Environment.getStorageState(path);
        }
        try {
            return path.getCanonicalPath().startsWith(Environment.getExternalStorageDirectory().getCanonicalPath()) ? Environment.getExternalStorageState() : "unknown";
        } catch (IOException e) {
            Log.w(TAG, "Failed to resolve canonical path: " + e);
            return "unknown";
        }
    }

    private EnvironmentCompat() {
    }
}
