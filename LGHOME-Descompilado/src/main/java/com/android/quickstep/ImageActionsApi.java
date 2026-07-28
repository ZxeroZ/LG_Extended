package com.android.quickstep;

import android.app.prediction.AppTarget;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import com.android.launcher3.util.Executors;
import com.android.quickstep.util.ImageActionUtils;
import com.android.systemui.shared.recents.model.Task;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class ImageActionsApi {
    private static final String TAG = "com.lge.launcher3ImageActionsApi";
    protected final Supplier<Bitmap> mBitmapSupplier;
    protected final Context mContext;
    protected final SystemUiProxy mSystemUiProxy;

    public ImageActionsApi(Context context, Supplier<Bitmap> bitmapSupplier) {
        this.mContext = context;
        this.mBitmapSupplier = bitmapSupplier;
        this.mSystemUiProxy = SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(context);
    }

    public void shareWithExplicitIntent(Rect crop, Intent intent) {
        addImageAndSendIntent(crop, intent, false, null);
    }

    public void shareAsDataWithExplicitIntent(Rect crop, Intent intent, Runnable exceptionCallback) {
        addImageAndSendIntent(crop, intent, true, exceptionCallback);
    }

    private void addImageAndSendIntent(final Rect crop, final Intent intent, final boolean setData, final Runnable exceptionCallback) {
        if (this.mBitmapSupplier.get() == null) {
            Log.e(TAG, "No snapshot available, not starting share.");
        } else {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.-$$Lambda$ImageActionsApi$fhMI0Zbp3bWPyw9E64bmIrGFx1o
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$addImageAndSendIntent$1$ImageActionsApi(crop, intent, setData, exceptionCallback);
                }
            });
        }
    }

    public /* synthetic */ void lambda$addImageAndSendIntent$1$ImageActionsApi(Rect rect, Intent intent, final boolean z, Runnable runnable) {
        ImageActionUtils.persistBitmapAndStartActivity(this.mContext, this.mBitmapSupplier.get(), rect, intent, (BiFunction<Uri, Intent, Intent[]>) new BiFunction() { // from class: com.android.quickstep.-$$Lambda$ImageActionsApi$nWC0jeyLa9IaZ5DUob-uR9FhXvM
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ImageActionsApi.lambda$addImageAndSendIntent$0(z, (Uri) obj, (Intent) obj2);
            }
        }, TAG, runnable);
    }

    static /* synthetic */ Intent[] lambda$addImageAndSendIntent$0(boolean z, Uri uri, Intent intent) {
        intent.addFlags(1);
        if (z) {
            intent.setData(uri);
        } else {
            intent.putExtra("android.intent.extra.STREAM", uri);
        }
        return new Intent[]{intent};
    }

    public void startShareActivity(Rect crop) {
        ImageActionUtils.startShareActivity(this.mContext, this.mBitmapSupplier, crop, null, TAG);
    }

    public void saveScreenshot(Bitmap screenshot, Rect screenshotBounds, Insets visibleInsets, Task.TaskKey task) {
        ImageActionUtils.saveScreenshot(this.mSystemUiProxy, screenshot, screenshotBounds, visibleInsets, task);
    }

    public void shareImage(RectF rectF, ShortcutInfo shortcutInfo, AppTarget appTarget) {
        ImageActionUtils.shareImage(this.mContext, this.mBitmapSupplier, rectF, shortcutInfo, appTarget, TAG);
    }
}
