package com.lge.launcher3.widgettray;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.icons.IconCache;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class LGWidgetPreviewLoader extends WidgetPreviewLoader {
    private static final String TAG = "LGWidgetPreviewLoader";

    public LGWidgetPreviewLoader(Context context, IconCache iconCache) {
        super(context, iconCache);
    }

    public Bitmap setNinepatchToBitmap(Context context, int width, int height) {
        Resources resources = context.getResources();
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(resources, R.drawable.default_widget_preview_holo);
        NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(resources, bitmapDecodeResource, bitmapDecodeResource.getNinePatchChunk(), new Rect(), null);
        ninePatchDrawable.setBounds(0, 0, width, height);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ninePatchDrawable.draw(new Canvas(bitmapCreateBitmap));
        return bitmapCreateBitmap;
    }

    @Override // com.android.launcher3.WidgetPreviewLoader
    public Bitmap generateWidgetPreview(BaseActivity baseActivity, LauncherAppWidgetProviderInfo info, int maxPreviewWidth, Bitmap preview, int[] preScaledWidthOut) {
        Drawable drawableLoadPreview;
        int intrinsicHeight;
        Bitmap ninepatchToBitmap;
        int intrinsicWidth;
        if (maxPreviewWidth < 0) {
            maxPreviewWidth = Integer.MAX_VALUE;
        }
        if (info.previewImage != 0) {
            drawableLoadPreview = this.mManager.loadPreview(info);
            if (drawableLoadPreview != null) {
                try {
                    drawableLoadPreview = mutateOnMainThread(drawableLoadPreview);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LGLog.w(TAG, "Can't load widget preview drawable 0x" + Integer.toHexString(info.previewImage) + " for provider: " + info.provider, new int[0]);
            }
        } else {
            drawableLoadPreview = null;
        }
        boolean z = drawableLoadPreview != null;
        Launcher launcher = Launcher.getLauncher(baseActivity);
        if (info.getSpanX(launcher) >= 1) {
            info.getSpanX(launcher);
        }
        if (info.getSpanY(launcher) >= 1) {
            info.getSpanY(launcher);
        }
        if (z) {
            intrinsicWidth = drawableLoadPreview.getIntrinsicWidth();
            intrinsicHeight = drawableLoadPreview.getIntrinsicHeight();
            ninepatchToBitmap = null;
        } else {
            intrinsicHeight = maxPreviewWidth;
            ninepatchToBitmap = setNinepatchToBitmap(this.mContext, maxPreviewWidth, maxPreviewWidth);
            intrinsicWidth = intrinsicHeight;
        }
        if (preScaledWidthOut != null) {
            preScaledWidthOut[0] = intrinsicWidth;
        }
        if (!z) {
            intrinsicWidth = launcher.getDeviceProfile().iconSizePx;
            intrinsicHeight = intrinsicWidth;
        }
        float f = intrinsicWidth > maxPreviewWidth ? maxPreviewWidth / intrinsicWidth : 1.0f;
        if (f != 1.0f) {
            intrinsicWidth = (int) (intrinsicWidth * f);
            intrinsicHeight = (int) (intrinsicHeight * f);
        }
        Canvas canvas = new Canvas();
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            intrinsicHeight = maxPreviewWidth;
        } else {
            maxPreviewWidth = intrinsicWidth;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(maxPreviewWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmapCreateBitmap);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (z) {
            drawableLoadPreview.setBounds(0, 0, maxPreviewWidth, intrinsicHeight);
            drawableLoadPreview.draw(canvas);
        } else {
            new Paint().setFilterBitmap(true);
            new Rect(0, 0, ninepatchToBitmap.getWidth(), ninepatchToBitmap.getHeight());
            new RectF(0.0f, 0.0f, ninepatchToBitmap.getWidth() * f, f * ninepatchToBitmap.getHeight());
            try {
                Drawable drawableMutateOnMainThread = mutateOnMainThread(this.mManager.loadIcon(info, this.mIconCache));
                if (drawableMutateOnMainThread != null) {
                    drawableMutateOnMainThread.setBounds(0, 0, maxPreviewWidth + 0, intrinsicHeight + 0);
                    drawableMutateOnMainThread.draw(canvas);
                }
            } catch (Resources.NotFoundException e2) {
                e2.printStackTrace();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            canvas.setBitmap(null);
        }
        return this.mManager.getBadgeBitmap(info, bitmapCreateBitmap, Math.min(bitmapCreateBitmap.getHeight(), intrinsicHeight + this.mProfileBadgeMargin));
    }

    @Override // com.android.launcher3.WidgetPreviewLoader
    public Bitmap generateShortcutPreview(BaseActivity baseActivity, ResolveInfo info, int maxWidth, int maxHeight, Bitmap preview) {
        Canvas canvas = new Canvas();
        int i = baseActivity.getDeviceProfile().iconSizePx;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmapCreateBitmap);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (this.mIconCache.getFullResIcon(info.activityInfo) != null) {
            try {
                Drawable drawableMutateOnMainThread = mutateOnMainThread(this.mIconCache.getFullResIcon(info.activityInfo));
                drawableMutateOnMainThread.setFilterBitmap(true);
                drawableMutateOnMainThread.setAlpha(255);
                drawableMutateOnMainThread.setColorFilter(null);
                drawableMutateOnMainThread.setBounds(0, 0, i, i);
                drawableMutateOnMainThread.draw(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
            canvas.setBitmap(null);
        }
        return bitmapCreateBitmap;
    }

    public void clearCacheDB() {
        LGLog.i(TAG, "clearCacheDB()");
        this.mDb.clearDB(this.mDb.getWritableDatabase());
    }
}
