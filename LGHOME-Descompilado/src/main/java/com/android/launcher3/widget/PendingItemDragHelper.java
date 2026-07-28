package com.android.launcher3.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.LivePreviewWidgetCell;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.graphics.LauncherIcons;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class PendingItemDragHelper extends DragPreviewProvider {
    private static final float MAX_WIDGET_SCALE = 1.25f;
    private final PendingAddItemInfo mAddInfo;
    private RemoteViews mPreview;
    private Bitmap mPreviewBitmap;

    public PendingItemDragHelper(View view) {
        super(view);
        this.mAddInfo = (PendingAddItemInfo) view.getTag();
    }

    public void setPreview(RemoteViews preview) {
        this.mPreview = preview;
    }

    public void startDrag(Rect previewBounds, int previewBitmapWidth, int previewViewWidth, Point screenPos, DragSource source, DragOptions options) {
        float width;
        Bitmap bitmap;
        Rect rect;
        Point point;
        Launcher launcher = Launcher.getLauncher(this.mView.getContext());
        LauncherAppState launcherAppState = LauncherAppState.getInstance(launcher);
        PendingAddItemInfo pendingAddItemInfo = this.mAddInfo;
        if (pendingAddItemInfo instanceof PendingAddWidgetInfo) {
            PendingAddWidgetInfo pendingAddWidgetInfo = (PendingAddWidgetInfo) pendingAddItemInfo;
            int iMin = Math.min((int) (previewBitmapWidth * MAX_WIDGET_SCALE), launcher.getWorkspace().estimateItemSize(pendingAddWidgetInfo, true, false)[0]);
            int[] iArr = new int[1];
            RemoteViews remoteViews = this.mPreview;
            Bitmap bitmapGenerateFromRemoteViews = remoteViews != null ? LivePreviewWidgetCell.generateFromRemoteViews(launcher, remoteViews, pendingAddWidgetInfo.info, iMin, iArr) : null;
            if (bitmapGenerateFromRemoteViews == null) {
                bitmapGenerateFromRemoteViews = launcherAppState.getWidgetCache().generateWidgetPreview(launcher, pendingAddWidgetInfo.info, iMin, null, iArr);
            }
            if (iArr[0] < previewBitmapWidth) {
                int i = (previewBitmapWidth - iArr[0]) / 2;
                if (previewBitmapWidth > previewViewWidth) {
                    i = (i * previewViewWidth) / previewBitmapWidth;
                }
                previewBounds.left += i;
                previewBounds.right -= i;
            }
            launcher.getDragController().addDragListener(new WidgetHostViewLoader(launcher, this.mView));
            width = previewBounds.width() / bitmapGenerateFromRemoteViews.getWidth();
            bitmap = bitmapGenerateFromRemoteViews;
            point = null;
            rect = null;
        } else {
            Bitmap bitmapCreateScaledBitmapWithoutShadow = LauncherIcons.createScaledBitmapWithoutShadow(((PendingAddShortcutInfo) pendingAddItemInfo).activityInfo.getFullResIcon(launcherAppState.getIconCache()), launcher, 26);
            PendingAddItemInfo pendingAddItemInfo2 = this.mAddInfo;
            pendingAddItemInfo2.spanY = 1;
            pendingAddItemInfo2.spanX = 1;
            Point point2 = new Point(this.previewPadding / 2, this.previewPadding / 2);
            int[] iArrEstimateItemSize = launcher.getWorkspace().estimateItemSize(this.mAddInfo, false, true);
            DeviceProfile deviceProfile = launcher.getDeviceProfile();
            int i2 = deviceProfile.iconSizePx;
            int dimensionPixelSize = launcher.getResources().getDimensionPixelSize(R.dimen.widget_preview_shortcut_padding);
            previewBounds.left += dimensionPixelSize;
            previewBounds.top += dimensionPixelSize;
            Rect rect2 = new Rect();
            rect2.left = (iArrEstimateItemSize[0] - i2) / 2;
            rect2.right = rect2.left + i2;
            rect2.top = (((iArrEstimateItemSize[1] - i2) - deviceProfile.iconTextSizePx) - deviceProfile.iconDrawablePaddingPx) / 2;
            rect2.bottom = rect2.top + i2;
            width = launcher.getDeviceProfile().iconSizePx / bitmapCreateScaledBitmapWithoutShadow.getWidth();
            bitmap = bitmapCreateScaledBitmapWithoutShadow;
            rect = rect2;
            point = point2;
        }
        if (this.mAddInfo instanceof PendingAddShortcutInfo) {
            launcher.getWorkspace().setDragOutLine(bitmap);
        } else {
            launcher.getWorkspace().setDragOutLine(this.mView);
        }
        launcher.getWorkspace().prepareDragWithProvider(this);
        int width2 = screenPos.x + previewBounds.left + ((int) (((bitmap.getWidth() * width) - bitmap.getWidth()) / 2.0f));
        int height = screenPos.y + previewBounds.top + ((int) (((bitmap.getHeight() * width) - bitmap.getHeight()) / 2.0f));
        this.mPreviewBitmap = bitmap;
        launcher.getDragController().startDragForDeepShortcut(bitmap, width2, height, source, this.mAddInfo, point, rect, width, options);
    }

    @Override // com.android.launcher3.graphics.DragPreviewProvider
    public Bitmap createDragOutline(Canvas canvas) {
        if (this.mAddInfo instanceof PendingAddShortcutInfo) {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(this.mPreviewBitmap.getWidth() + this.blurSizeOutline, this.mPreviewBitmap.getHeight() + this.blurSizeOutline, Bitmap.Config.ALPHA_8);
            canvas.setBitmap(bitmapCreateBitmap);
            int i = Launcher.getLauncher(this.mView.getContext()).getDeviceProfile().iconSizePx;
            Rect rect = new Rect(0, 0, this.mPreviewBitmap.getWidth(), this.mPreviewBitmap.getHeight());
            Rect rect2 = new Rect(0, 0, i, i);
            rect2.offset(this.blurSizeOutline / 2, this.blurSizeOutline / 2);
            canvas.drawBitmap(this.mPreviewBitmap, rect, rect2, new Paint(2));
            canvas.setBitmap(null);
            return bitmapCreateBitmap;
        }
        int[] iArrEstimateItemSize = Launcher.getLauncher(this.mView.getContext()).getWorkspace().estimateItemSize(this.mAddInfo, false, false);
        int i2 = iArrEstimateItemSize[0];
        int i3 = iArrEstimateItemSize[1];
        Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap(i2, i3, Bitmap.Config.ALPHA_8);
        canvas.setBitmap(bitmapCreateBitmap2);
        Rect rect3 = new Rect(0, 0, this.mPreviewBitmap.getWidth(), this.mPreviewBitmap.getHeight());
        float fMin = Math.min((i2 - this.blurSizeOutline) / this.mPreviewBitmap.getWidth(), (i3 - this.blurSizeOutline) / this.mPreviewBitmap.getHeight());
        int width = (int) (this.mPreviewBitmap.getWidth() * fMin);
        int height = (int) (fMin * this.mPreviewBitmap.getHeight());
        Rect rect4 = new Rect(0, 0, width, height);
        rect4.offset((i2 - width) / 2, (i3 - height) / 2);
        canvas.drawBitmap(this.mPreviewBitmap, rect3, rect4, (Paint) null);
        canvas.setBitmap(null);
        return bitmapCreateBitmap2;
    }
}
