package androidx.core.net;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class ConnectivityManagerCompat {
    public static final int RESTRICT_BACKGROUND_STATUS_DISABLED = 1;
    public static final int RESTRICT_BACKGROUND_STATUS_ENABLED = 3;
    public static final int RESTRICT_BACKGROUND_STATUS_WHITELISTED = 2;

    @Retention(RetentionPolicy.SOURCE)
    public @interface RestrictBackgroundStatus {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager cm) {
        if (Build.VERSION.SDK_INT >= 16) {
            return cm.isActiveNetworkMetered();
        }
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return true;
        }
        int type = activeNetworkInfo.getType();
        return (type == 1 || type == 7 || type == 9) ? false : true;
    }

    public static NetworkInfo getNetworkInfoFromBroadcast(ConnectivityManager cm, Intent intent) {
        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
        if (networkInfo != null) {
            return cm.getNetworkInfo(networkInfo.getType());
        }
        return null;
    }

    public static int getRestrictBackgroundStatus(ConnectivityManager cm) {
        if (Build.VERSION.SDK_INT >= 24) {
            return cm.getRestrictBackgroundStatus();
        }
        return 3;
    }

    private ConnectivityManagerCompat() {
    }
}
