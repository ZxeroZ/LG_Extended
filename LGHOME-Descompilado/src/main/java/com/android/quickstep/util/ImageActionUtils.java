package com.android.quickstep.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.prediction.AppTarget;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import androidx.core.content.FileProvider;
import androidx.core.content.pm.ShortcutManagerCompat;
import com.android.internal.util.ScreenshotHelper;
import com.android.launcher3.util.Executors;
import com.android.quickstep.SystemUiProxy;
import com.android.systemui.shared.recents.model.Task;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class ImageActionUtils {
    private static final String AUTHORITY = "com.lge.launcher3.overview.fileprovider";
    private static final String BASE_NAME = "overview_image_";
    private static final long FILE_LIFE = 86400000;
    private static final String SUB_FOLDER = "Overview";
    private static final String TAG = "ImageActionUtils";

    public static void saveScreenshot(SystemUiProxy systemUiProxy, Bitmap screenshot, Rect screenshotBounds, Insets visibleInsets, Task.TaskKey task) {
        systemUiProxy.handleImageBundleAsScreenshot(ScreenshotHelper.HardwareBitmapBundler.hardwareBitmapToBundle(screenshot), screenshotBounds, visibleInsets, task);
    }

    public static void shareImage(Context context, Supplier<Bitmap> bitmapSupplier, RectF rectF, ShortcutInfo shortcutInfo, AppTarget appTarget, String tag) {
        if (bitmapSupplier.get() == null) {
            return;
        }
        Rect rect = new Rect();
        rectF.round(rect);
        Intent intent = new Intent();
        Uri imageUri = getImageUri(bitmapSupplier.get(), rect, context, tag);
        intent.setAction("android.intent.action.SEND").setComponent(new ComponentName(appTarget.getPackageName(), appTarget.getClassName())).addFlags(268435456).addFlags(1).setType("image/png").putExtra("android.intent.extra.STREAM", imageUri).putExtra(ShortcutManagerCompat.EXTRA_SHORTCUT_ID, shortcutInfo.getId()).setClipData(new ClipData(new ClipDescription("content", new String[]{"image/png"}), new ClipData.Item(imageUri)));
        if (context.getUserId() != appTarget.getUser().getIdentifier()) {
            intent.prepareToLeaveUser(context.getUserId());
            intent.fixUris(context.getUserId());
            context.startActivityAsUser(intent, appTarget.getUser());
            return;
        }
        context.startActivity(intent);
    }

    public static void startShareActivity(final Context context, final Supplier<Bitmap> bitmapSupplier, final Rect crop, final Intent intent, final String tag) {
        if (bitmapSupplier.get() == null) {
            Log.e(tag, "No snapshot available, not starting share.");
        } else {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.util.-$$Lambda$ImageActionUtils$eooofEQGOkySVgXfGJvET3mJapU
                @Override // java.lang.Runnable
                public final void run() {
                    ImageActionUtils.persistBitmapAndStartActivity(context, (Bitmap) bitmapSupplier.get(), crop, intent, $$Lambda$ImageActionUtils$iWnfhZSJ59KYEAx__KXAH75ft8.INSTANCE, tag);
                }
            });
        }
    }

    public static void startShareActivity(final Context context, final Supplier<Bitmap> bitmapSupplier, final Rect crop, final Intent intent, final String tag, final View sharedElement) {
        if (bitmapSupplier.get() == null) {
            Log.e(tag, "No snapshot available, not starting share.");
        } else {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.util.-$$Lambda$ImageActionUtils$alrOjJpnsrna-g4TMXu9CMW0oT0
                @Override // java.lang.Runnable
                public final void run() {
                    ImageActionUtils.persistBitmapAndStartActivity(context, (Bitmap) bitmapSupplier.get(), crop, intent, $$Lambda$ImageActionUtils$iWnfhZSJ59KYEAx__KXAH75ft8.INSTANCE, tag, sharedElement);
                }
            });
        }
    }

    public static void persistBitmapAndStartActivity(Context context, Bitmap bitmap, Rect crop, Intent intent, BiFunction<Uri, Intent, Intent[]> uriToIntentMap, String tag) {
        persistBitmapAndStartActivity(context, bitmap, crop, intent, uriToIntentMap, tag, (Runnable) null);
    }

    public static void persistBitmapAndStartActivity(Context context, Bitmap bitmap, Rect crop, Intent intent, BiFunction<Uri, Intent, Intent[]> uriToIntentMap, String tag, Runnable exceptionCallback) {
        Intent[] intentArrApply = uriToIntentMap.apply(getImageUri(bitmap, crop, context, tag), intent);
        try {
            if (intentArrApply.length == 1) {
                context.startActivity(intentArrApply[0]);
            } else {
                context.startActivities(intentArrApply);
            }
        } catch (ActivityNotFoundException unused) {
            Log.e(TAG, "No activity found to receive image intent");
            if (exceptionCallback != null) {
                exceptionCallback.run();
            }
        }
    }

    public static void persistBitmapAndStartActivity(final Context context, Bitmap bitmap, Rect crop, Intent intent, BiFunction<Uri, Intent, Intent[]> uriToIntentMap, String tag, final View scaledImage) {
        final Intent[] intentArrApply = uriToIntentMap.apply(getImageUri(bitmap, crop, context, tag), intent);
        if (intentArrApply.length == 1) {
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.util.-$$Lambda$ImageActionUtils$0uHpe8W6sfY6MTFCUWdlF6CZjVM
                @Override // java.lang.Runnable
                public final void run() {
                    Context context2 = context;
                    context2.startActivity(intentArrApply[0], ActivityOptions.makeSceneTransitionAnimation((Activity) context2, scaledImage, "screenshot_preview_image").toBundle());
                }
            });
        } else {
            Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.util.-$$Lambda$ImageActionUtils$R5un6NqyWrG9EyREK4bzbAcG5TY
                @Override // java.lang.Runnable
                public final void run() {
                    Context context2 = context;
                    context2.startActivities(intentArrApply, ActivityOptions.makeSceneTransitionAnimation((Activity) context2, scaledImage, "screenshot_preview_image").toBundle());
                }
            });
        }
    }

    public static Uri getImageUri(Bitmap bitmap, Rect crop, Context context, String tag) {
        clearOldCacheFiles(context);
        Bitmap bitmapCropBitmap = cropBitmap(bitmap, crop);
        int iHashCode = crop == null ? 0 : crop.hashCode();
        String str = BASE_NAME + bitmap.hashCode() + "_" + iHashCode + ".png";
        File file = new File(context.getCacheDir(), SUB_FOLDER);
        file.mkdir();
        File file2 = new File(file, str);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            try {
                bitmapCropBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
            } finally {
            }
        } catch (IOException e) {
            Log.e(tag, "Error saving image", e);
        }
        return FileProvider.getUriForFile(context, AUTHORITY, file2);
    }

    public static Bitmap cropBitmap(Bitmap bitmap, Rect crop) {
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        if (crop == null) {
            crop = new Rect(rect);
        }
        if (crop.equals(rect)) {
            return bitmap;
        }
        if (bitmap.getConfig() != Bitmap.Config.HARDWARE) {
            return Bitmap.createBitmap(bitmap, crop.left, crop.top, crop.width(), crop.height());
        }
        Picture picture = new Picture();
        picture.beginRecording(crop.width(), crop.height()).drawBitmap(bitmap, -crop.left, -crop.top, (Paint) null);
        picture.endRecording();
        return Bitmap.createBitmap(picture, crop.width(), crop.height(), Bitmap.Config.ARGB_8888);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Intent[] getShareIntentForImageUri(Uri uri, Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.setAction("android.intent.action.SEND").setComponent(null).addFlags(268435456).addFlags(1).setType("image/png").putExtra("android.intent.extra.STREAM", uri).setClipData(new ClipData(new ClipDescription("content", new String[]{"image/png"}), new ClipData.Item(uri)));
        return new Intent[]{Intent.createChooser(intent, null).addFlags(268435456)};
    }

    private static void clearOldCacheFiles(final Context context) {
        Executors.THREAD_POOL_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.util.-$$Lambda$ImageActionUtils$xmuZmcRgg3ANn0VQmSNrfRiX7Hs
            @Override // java.lang.Runnable
            public final void run() {
                ImageActionUtils.lambda$clearOldCacheFiles$5(context);
            }
        });
    }

    static /* synthetic */ void lambda$clearOldCacheFiles$5(Context context) {
        File[] fileArrListFiles = new File(context.getCacheDir(), SUB_FOLDER).listFiles(new FilenameFilter() { // from class: com.android.quickstep.util.-$$Lambda$ImageActionUtils$VUkVC-FVMOkMKcJgnCajKCuGDnc
            @Override // java.io.FilenameFilter
            public final boolean accept(File file, String str) {
                return str.startsWith(ImageActionUtils.BASE_NAME);
            }
        });
        if (fileArrListFiles != null) {
            for (File file : fileArrListFiles) {
                if (file.lastModified() + FILE_LIFE < System.currentTimeMillis()) {
                    file.delete();
                }
            }
        }
    }
}
