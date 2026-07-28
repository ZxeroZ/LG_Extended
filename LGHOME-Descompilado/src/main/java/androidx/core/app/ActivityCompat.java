package androidx.core.app;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.View;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.core.content.LocusIdCompat;
import androidx.core.view.DragAndDropPermissionsCompat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ActivityCompat extends ContextCompat {
    private static PermissionCompatDelegate sDelegate;

    public interface OnRequestPermissionsResultCallback {
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    }

    public interface PermissionCompatDelegate {
        boolean onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

        boolean requestPermissions(Activity activity, String[] permissions, int requestCode);
    }

    public interface RequestPermissionsRequestCodeValidator {
        void validateRequestPermissionsRequestCode(int requestCode);
    }

    protected ActivityCompat() {
    }

    public static void setPermissionCompatDelegate(PermissionCompatDelegate delegate) {
        sDelegate = delegate;
    }

    public static PermissionCompatDelegate getPermissionCompatDelegate() {
        return sDelegate;
    }

    @Deprecated
    public static boolean invalidateOptionsMenu(Activity activity) {
        activity.invalidateOptionsMenu();
        return true;
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode, Bundle options) {
        if (Build.VERSION.SDK_INT >= 16) {
            activity.startActivityForResult(intent, requestCode, options);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static void startIntentSenderForResult(Activity activity, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        if (Build.VERSION.SDK_INT >= 16) {
            activity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } else {
            activity.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
        }
    }

    public static void finishAffinity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 16) {
            activity.finishAffinity();
        } else {
            activity.finish();
        }
    }

    public static void finishAfterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.finishAfterTransition();
        } else {
            activity.finish();
        }
    }

    public static Uri getReferrer(Activity activity) {
        if (Build.VERSION.SDK_INT >= 22) {
            return activity.getReferrer();
        }
        Intent intent = activity.getIntent();
        Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.REFERRER");
        if (uri != null) {
            return uri;
        }
        String stringExtra = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
        if (stringExtra != null) {
            return Uri.parse(stringExtra);
        }
        return null;
    }

    public static <T extends View> T requireViewById(Activity activity, int i) {
        if (Build.VERSION.SDK_INT >= 28) {
            return (T) activity.requireViewById(i);
        }
        T t = (T) activity.findViewById(i);
        if (t != null) {
            return t;
        }
        throw new IllegalArgumentException("ID does not reference a View inside this Activity");
    }

    public static void setEnterSharedElementCallback(Activity activity, SharedElementCallback callback) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.setEnterSharedElementCallback(callback != null ? new SharedElementCallback21Impl(callback) : null);
        }
    }

    public static void setExitSharedElementCallback(Activity activity, SharedElementCallback callback) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.setExitSharedElementCallback(callback != null ? new SharedElementCallback21Impl(callback) : null);
        }
    }

    public static void postponeEnterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.postponeEnterTransition();
        }
    }

    public static void startPostponedEnterTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.startPostponedEnterTransition();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: android.app.Activity */
    /* JADX WARN: Multi-variable type inference failed */
    public static void requestPermissions(final Activity activity, final String[] permissions, final int requestCode) {
        PermissionCompatDelegate permissionCompatDelegate = sDelegate;
        if (permissionCompatDelegate == null || !permissionCompatDelegate.requestPermissions(activity, permissions, requestCode)) {
            for (String str : permissions) {
                if (TextUtils.isEmpty(str)) {
                    throw new IllegalArgumentException("Permission request for permissions " + Arrays.toString(permissions) + " must not contain null or empty values");
                }
            }
            if (Build.VERSION.SDK_INT >= 23) {
                if (activity instanceof RequestPermissionsRequestCodeValidator) {
                    ((RequestPermissionsRequestCodeValidator) activity).validateRequestPermissionsRequestCode(requestCode);
                }
                activity.requestPermissions(permissions, requestCode);
            } else if (activity instanceof OnRequestPermissionsResultCallback) {
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: androidx.core.app.ActivityCompat.1
                    @Override // java.lang.Runnable
                    public void run() {
                        int[] iArr = new int[permissions.length];
                        PackageManager packageManager = activity.getPackageManager();
                        String packageName = activity.getPackageName();
                        int length = permissions.length;
                        for (int i = 0; i < length; i++) {
                            iArr[i] = packageManager.checkPermission(permissions[i], packageName);
                        }
                        ((OnRequestPermissionsResultCallback) activity).onRequestPermissionsResult(requestCode, permissions, iArr);
                    }
                });
            }
        }
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            return activity.shouldShowRequestPermissionRationale(permission);
        }
        return false;
    }

    public static DragAndDropPermissionsCompat requestDragAndDropPermissions(Activity activity, DragEvent dragEvent) {
        return DragAndDropPermissionsCompat.request(activity, dragEvent);
    }

    public static void recreate(final Activity activity) {
        if (Build.VERSION.SDK_INT >= 28) {
            activity.recreate();
        } else if (Build.VERSION.SDK_INT <= 23) {
            new Handler(activity.getMainLooper()).post(new Runnable() { // from class: androidx.core.app.ActivityCompat.2
                @Override // java.lang.Runnable
                public void run() {
                    if (activity.isFinishing() || ActivityRecreator.recreate(activity)) {
                        return;
                    }
                    activity.recreate();
                }
            });
        } else {
            if (ActivityRecreator.recreate(activity)) {
                return;
            }
            activity.recreate();
        }
    }

    public static void setLocusContext(final Activity activity, final LocusIdCompat locusId, final Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 30) {
            Api30Impl.setLocusContext(activity, locusId, bundle);
        }
    }

    private static class SharedElementCallback21Impl extends android.app.SharedElementCallback {
        private final SharedElementCallback mCallback;

        SharedElementCallback21Impl(SharedElementCallback callback) {
            this.mCallback = callback;
        }

        @Override // android.app.SharedElementCallback
        public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
            this.mCallback.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
        }

        @Override // android.app.SharedElementCallback
        public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
            this.mCallback.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
        }

        @Override // android.app.SharedElementCallback
        public void onRejectSharedElements(List<View> rejectedSharedElements) {
            this.mCallback.onRejectSharedElements(rejectedSharedElements);
        }

        @Override // android.app.SharedElementCallback
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            this.mCallback.onMapSharedElements(names, sharedElements);
        }

        @Override // android.app.SharedElementCallback
        public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
            return this.mCallback.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
        }

        @Override // android.app.SharedElementCallback
        public View onCreateSnapshotView(Context context, Parcelable snapshot) {
            return this.mCallback.onCreateSnapshotView(context, snapshot);
        }

        @Override // android.app.SharedElementCallback
        public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, final SharedElementCallback.OnSharedElementsReadyListener listener) {
            this.mCallback.onSharedElementsArrived(sharedElementNames, sharedElements, new SharedElementCallback.OnSharedElementsReadyListener() { // from class: androidx.core.app.ActivityCompat.SharedElementCallback21Impl.1
                @Override // androidx.core.app.SharedElementCallback.OnSharedElementsReadyListener
                public void onSharedElementsReady() {
                    listener.onSharedElementsReady();
                }
            });
        }
    }

    static class Api30Impl {
        private Api30Impl() {
        }

        static void setLocusContext(final Activity activity, final LocusIdCompat locusId, final Bundle bundle) {
            activity.setLocusContext(locusId == null ? null : locusId.toLocusId(), bundle);
        }
    }
}
