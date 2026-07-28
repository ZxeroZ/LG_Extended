package com.android.launcher3.dragndrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.widget.WidgetCell;

/* JADX INFO: loaded from: classes.dex */
public class LivePreviewWidgetCell extends WidgetCell {
    private RemoteViews mPreview;

    public LivePreviewWidgetCell(Context context) {
        this(context, null);
    }

    public LivePreviewWidgetCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LivePreviewWidgetCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPreview(RemoteViews view) {
        this.mPreview = view;
    }

    @Override // com.android.launcher3.widget.WidgetCell
    public void ensurePreview() {
        Bitmap bitmapGenerateFromRemoteViews;
        if (this.mPreview != null && this.mActiveRequest == null && (bitmapGenerateFromRemoteViews = generateFromRemoteViews(this.mLauncher, this.mPreview, this.mItem.widgetInfo, this.mPresetPreviewSize, new int[1])) != null) {
            applyPreview(bitmapGenerateFromRemoteViews);
        } else {
            super.ensurePreview();
        }
    }

    public static Bitmap generateFromRemoteViews(BaseActivity activity, RemoteViews views, LauncherAppWidgetProviderInfo info, int previewSize, int[] preScaledWidthOut) {
        float f;
        DeviceProfile deviceProfile = activity.getDeviceProfile();
        int i = deviceProfile.cellWidthPx * info.spanX;
        int i2 = deviceProfile.cellHeightPx * info.spanY;
        try {
            View viewApply = views.apply(activity, new FrameLayout(activity));
            viewApply.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(i2, 1073741824));
            int measuredWidth = viewApply.getMeasuredWidth();
            int measuredHeight = viewApply.getMeasuredHeight();
            viewApply.layout(0, 0, measuredWidth, measuredHeight);
            preScaledWidthOut[0] = measuredWidth;
            if (measuredWidth > previewSize) {
                f = previewSize / measuredWidth;
                measuredHeight = (int) (measuredHeight * f);
            } else {
                f = 1.0f;
                previewSize = measuredWidth;
            }
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(previewSize, measuredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            canvas.scale(f, f);
            viewApply.draw(canvas);
            canvas.setBitmap(null);
            return bitmapCreateBitmap;
        } catch (Exception unused) {
            return null;
        }
    }
}
