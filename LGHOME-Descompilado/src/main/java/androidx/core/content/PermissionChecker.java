package androidx.core.content;

import android.content.Context;
import android.os.Binder;
import android.os.Process;
import androidx.core.app.AppOpsManagerCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class PermissionChecker {
    public static final int PERMISSION_DENIED = -1;
    public static final int PERMISSION_DENIED_APP_OP = -2;
    public static final int PERMISSION_GRANTED = 0;

    @Retention(RetentionPolicy.SOURCE)
    public @interface PermissionResult {
    }

    private PermissionChecker() {
    }

    public static int checkPermission(Context context, String permission, int pid, int uid, String packageName) {
        if (context.checkPermission(permission, pid, uid) == -1) {
            return -1;
        }
        String strPermissionToOp = AppOpsManagerCompat.permissionToOp(permission);
        if (strPermissionToOp == null) {
            return 0;
        }
        if (packageName == null) {
            String[] packagesForUid = context.getPackageManager().getPackagesForUid(uid);
            if (packagesForUid == null || packagesForUid.length <= 0) {
                return -1;
            }
            packageName = packagesForUid[0];
        }
        return AppOpsManagerCompat.noteProxyOpNoThrow(context, strPermissionToOp, packageName) != 0 ? -2 : 0;
    }

    public static int checkSelfPermission(Context context, String permission) {
        return checkPermission(context, permission, Process.myPid(), Process.myUid(), context.getPackageName());
    }

    public static int checkCallingPermission(Context context, String permission, String packageName) {
        if (Binder.getCallingPid() == Process.myPid()) {
            return -1;
        }
        return checkPermission(context, permission, Binder.getCallingPid(), Binder.getCallingUid(), packageName);
    }

    public static int checkCallingOrSelfPermission(Context context, String permission) {
        return checkPermission(context, permission, Binder.getCallingPid(), Binder.getCallingUid(), Binder.getCallingPid() == Process.myPid() ? context.getPackageName() : null);
    }
}
