package com.android.launcher3.shortcuts;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.DragPreviewProvider;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutDragPreviewProvider extends DragPreviewProvider {
    private final Point mPositionShift;

    public ShortcutDragPreviewProvider(View icon, Point shift) {
        super(icon);
        this.mPositionShift = shift;
    }

    @Override // com.android.launcher3.graphics.DragPreviewProvider
    public Bitmap createDragOutline(Canvas canvas) {
        Bitmap bitmapDrawScaledPreview = drawScaledPreview(canvas, Bitmap.Config.ALPHA_8);
        canvas.setBitmap(null);
        return bitmapDrawScaledPreview;
    }

    @Override // com.android.launcher3.graphics.DragPreviewProvider
    public Bitmap createDragBitmap(Canvas canvas) {
        Bitmap bitmapDrawScaledPreview = drawScaledPreview(canvas, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(null);
        return bitmapDrawScaledPreview;
    }

    private Bitmap drawScaledPreview(Canvas canvas, Bitmap.Config config) {
        Drawable background = this.mView.getBackground();
        Rect drawableBounds = getDrawableBounds(background);
        int i = Launcher.getLauncher(this.mView.getContext()).getDeviceProfile().iconSizePx;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(this.blurSizeOutline + i, this.blurSizeOutline + i, config);
        canvas.setBitmap(bitmapCreateBitmap);
        canvas.save(1);
        canvas.translate(this.blurSizeOutline / 2, this.blurSizeOutline / 2);
        float f = i;
        canvas.scale(f / drawableBounds.width(), f / drawableBounds.height(), 0.0f, 0.0f);
        canvas.translate(drawableBounds.left, drawableBounds.top);
        background.draw(canvas);
        canvas.restore();
        return bitmapCreateBitmap;
    }

    @Override // com.android.launcher3.graphics.DragPreviewProvider
    public float getScaleAndPosition(Bitmap preview, int[] outPos) {
        Launcher launcher = Launcher.getLauncher(this.mView.getContext());
        int iWidth = getDrawableBounds(this.mView.getBackground()).width();
        float locationInDragLayer = launcher.getDragLayer().getLocationInDragLayer(this.mView, outPos);
        int paddingStart = this.mView.getPaddingStart();
        if (Utilities.isRtl(this.mView.getResources())) {
            paddingStart = (this.mView.getWidth() - iWidth) - paddingStart;
        }
        float f = iWidth * locationInDragLayer;
        outPos[0] = outPos[0] + Math.round((paddingStart * locationInDragLayer) + ((f - preview.getWidth()) / 2.0f) + this.mPositionShift.x);
        outPos[1] = outPos[1] + Math.round((((locationInDragLayer * this.mView.getHeight()) - preview.getHeight()) / 2.0f) + this.mPositionShift.y);
        return f / launcher.getDeviceProfile().iconSizePx;
    }
}
