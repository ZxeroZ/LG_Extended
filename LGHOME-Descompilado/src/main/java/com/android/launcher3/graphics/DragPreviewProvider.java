package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.Workspace;
import com.android.launcher3.config.ProviderConfig;
import com.android.launcher3.folder.FolderIcon;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class DragPreviewProvider {
    protected final int blurSizeOutline;
    public Bitmap generatedDragOutline;
    private final Rect mTempRect;
    protected final View mView;
    public final int previewPadding;

    public DragPreviewProvider(View view) {
        this(view, view.getContext());
    }

    public DragPreviewProvider(View view, Context context) {
        this.mTempRect = new Rect();
        this.mView = view;
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        this.blurSizeOutline = dimensionPixelSize;
        if (view instanceof TextView) {
            Rect drawableBounds = getDrawableBounds(Workspace.getTextViewIcon((TextView) view));
            this.previewPadding = (dimensionPixelSize - drawableBounds.left) - drawableBounds.top;
        } else {
            this.previewPadding = dimensionPixelSize;
        }
    }

    private void drawDragView(Canvas destCanvas) {
        boolean z;
        boolean z2;
        destCanvas.save();
        View view = this.mView;
        if (view instanceof TextView) {
            Drawable textViewIcon = Workspace.getTextViewIcon((TextView) view);
            if (textViewIcon instanceof FastBitmapDrawable) {
                ((FastBitmapDrawable) textViewIcon).setDesaturationAndBrightness();
            }
            Rect drawableBounds = getDrawableBounds(textViewIcon);
            destCanvas.translate((this.blurSizeOutline / 2) - drawableBounds.left, (this.blurSizeOutline / 2) - drawableBounds.top);
            textViewIcon.draw(destCanvas);
        } else {
            Rect rect = this.mTempRect;
            view.getDrawingRect(rect);
            View view2 = this.mView;
            if (view2 instanceof FolderIcon) {
                if (((FolderIcon) view2).getTextVisible()) {
                    ((FolderIcon) this.mView).setTextVisible(false);
                    z = true;
                } else {
                    z = false;
                }
                if (!LGHomeFeature.Config.FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME.getValue() || LGHomeFeature.isDisableEasyHome() || this.mView.getBackground() == null) {
                    z2 = false;
                } else {
                    this.mView.setBackground(null);
                    z2 = true;
                }
            } else {
                z = false;
                z2 = false;
            }
            View view3 = this.mView;
            if ((view3 instanceof FolderIcon) && ((FolderIcon) view3).getFolderInfo().container == -101) {
                FolderIcon folderIcon = (FolderIcon) this.mView;
                rect.set(0, 0, folderIcon.mPreviewBackground.getWidth() + this.blurSizeOutline, folderIcon.mPreviewBackground.getHeight() + this.blurSizeOutline);
                destCanvas.clipRect(rect);
                destCanvas.translate((this.blurSizeOutline / 2) - folderIcon.mPreviewBackground.getLeft(), (this.blurSizeOutline / 2) - folderIcon.mPreviewBackground.getTop());
            } else {
                destCanvas.translate((-this.mView.getScrollX()) + (this.blurSizeOutline / 2), (-this.mView.getScrollY()) + (this.blurSizeOutline / 2));
                destCanvas.clipRect(rect);
            }
            this.mView.draw(destCanvas);
            if (z) {
                ((FolderIcon) this.mView).setTextVisible(true);
            }
            if (z2) {
                this.mView.setBackgroundResource(R.drawable.bg_easyhome_widget);
            }
        }
        destCanvas.restore();
    }

    public Bitmap createDragBitmap(Canvas canvas) {
        Bitmap bitmapCreateBitmap;
        this.mView.getWidth();
        this.mView.getHeight();
        View view = this.mView;
        float scaleToFit = 1.0f;
        if (view instanceof TextView) {
            Rect drawableBounds = getDrawableBounds(Workspace.getTextViewIcon((TextView) view));
            int iWidth = drawableBounds.width();
            int iHeight = drawableBounds.height();
            int i = this.blurSizeOutline;
            bitmapCreateBitmap = Bitmap.createBitmap(iWidth + i, iHeight + i, Bitmap.Config.ARGB_8888);
        } else if (view instanceof LauncherAppWidgetHostView) {
            scaleToFit = ((LauncherAppWidgetHostView) view).getScaleToFit();
            int i2 = this.blurSizeOutline;
            bitmapCreateBitmap = Bitmap.createBitmap(((int) (this.mView.getWidth() * scaleToFit)) + i2, ((int) (this.mView.getHeight() * scaleToFit)) + i2, Bitmap.Config.ARGB_8888);
        } else if ((view instanceof FolderIcon) && ((FolderIcon) view).getFolderInfo().container == -101) {
            FolderIcon folderIcon = (FolderIcon) this.mView;
            bitmapCreateBitmap = Bitmap.createBitmap(folderIcon.mPreviewBackground.getWidth() + this.blurSizeOutline, folderIcon.mPreviewBackground.getHeight() + this.blurSizeOutline, Bitmap.Config.ARGB_8888);
        } else {
            bitmapCreateBitmap = Bitmap.createBitmap(this.mView.getWidth() + this.blurSizeOutline, this.mView.getHeight() + this.blurSizeOutline, Bitmap.Config.ARGB_8888);
        }
        canvas.setBitmap(bitmapCreateBitmap);
        canvas.save();
        canvas.scale(scaleToFit, scaleToFit);
        drawDragView(canvas);
        canvas.restore();
        canvas.setBitmap(null);
        return bitmapCreateBitmap;
    }

    public final void generateDragOutline(Canvas canvas) {
        if (ProviderConfig.IS_DOGFOOD_BUILD && this.generatedDragOutline != null) {
            throw new RuntimeException("Drag outline generated twice");
        }
        this.generatedDragOutline = createDragOutline(canvas);
    }

    public Bitmap createDragOutline(Canvas canvas) {
        float f;
        int width = this.mView.getWidth();
        int height = this.mView.getHeight();
        View view = this.mView;
        if (view instanceof LauncherAppWidgetHostView) {
            float scaleToFit = ((LauncherAppWidgetHostView) view).getScaleToFit();
            int iFloor = (int) Math.floor(this.mView.getWidth() * scaleToFit);
            int iFloor2 = (int) Math.floor(this.mView.getHeight() * scaleToFit);
            f = scaleToFit;
            width = iFloor;
            height = iFloor2;
        } else {
            f = 1.0f;
        }
        int i = this.blurSizeOutline;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width + i, height + i, Bitmap.Config.ALPHA_8);
        canvas.setBitmap(bitmapCreateBitmap);
        canvas.save();
        canvas.scale(f, f);
        drawDragView(canvas);
        canvas.restore();
        canvas.setBitmap(null);
        return bitmapCreateBitmap;
    }

    protected static Rect getDrawableBounds(Drawable d) {
        Rect rect = new Rect();
        d.copyBounds(rect);
        if (rect.width() == 0 || rect.height() == 0) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        } else {
            rect.offsetTo(0, 0);
        }
        return rect;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0042  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public float getScaleAndPosition(android.graphics.Bitmap r8, int[] r9) {
        /*
            r7 = this;
            android.view.View r0 = r7.mView
            android.content.Context r0 = r0.getContext()
            com.android.launcher3.Launcher r0 = com.android.launcher3.Launcher.getLauncher(r0)
            com.android.launcher3.dragndrop.DragLayer r0 = r0.getDragLayer()
            android.view.View r1 = r7.mView
            float r0 = r0.getLocationInDragLayer(r1, r9)
            android.view.View r1 = r7.mView
            boolean r2 = r1 instanceof com.android.launcher3.LauncherAppWidgetHostView
            if (r2 == 0) goto L21
            com.android.launcher3.LauncherAppWidgetHostView r1 = (com.android.launcher3.LauncherAppWidgetHostView) r1
            float r1 = r1.getScaleToFit()
            float r0 = r0 / r1
        L21:
            android.view.View r1 = r7.mView
            boolean r2 = r1 instanceof android.widget.TextView
            r3 = 1073741824(0x40000000, float:2.0)
            r4 = 0
            if (r2 == 0) goto L34
            android.widget.TextView r1 = (android.widget.TextView) r1
            android.graphics.drawable.Drawable[] r1 = r1.getCompoundDrawables()
            r1 = r1[r4]
            if (r1 != 0) goto L42
        L34:
            android.view.View r1 = r7.mView
            boolean r2 = r1 instanceof com.android.launcher3.folder.FolderIcon
            if (r2 == 0) goto L5c
            com.android.launcher3.folder.FolderIcon r1 = (com.android.launcher3.folder.FolderIcon) r1
            boolean r1 = r1.isLayoutHorizontal()
            if (r1 == 0) goto L5c
        L42:
            r1 = r9[r4]
            float r1 = (float) r1
            android.view.View r2 = r7.mView
            int r2 = r2.getPaddingStart()
            float r2 = (float) r2
            float r2 = r2 * r0
            android.view.View r5 = r7.mView
            float r5 = r5.getScaleX()
            float r2 = r2 * r5
            float r1 = r1 + r2
            int r1 = java.lang.Math.round(r1)
            r9[r4] = r1
            goto L7c
        L5c:
            r1 = r9[r4]
            float r1 = (float) r1
            int r2 = r8.getWidth()
            float r2 = (float) r2
            android.view.View r5 = r7.mView
            int r5 = r5.getWidth()
            float r5 = (float) r5
            float r5 = r5 * r0
            android.view.View r6 = r7.mView
            float r6 = r6.getScaleX()
            float r5 = r5 * r6
            float r2 = r2 - r5
            float r2 = r2 / r3
            float r1 = r1 - r2
            int r1 = java.lang.Math.round(r1)
            r9[r4] = r1
        L7c:
            r1 = 1
            r2 = r9[r1]
            float r2 = (float) r2
            r4 = 1065353216(0x3f800000, float:1.0)
            float r4 = r4 - r0
            int r8 = r8.getHeight()
            float r8 = (float) r8
            float r4 = r4 * r8
            float r4 = r4 / r3
            float r2 = r2 - r4
            int r8 = r7.previewPadding
            int r8 = r8 / 2
            float r8 = (float) r8
            float r2 = r2 - r8
            int r8 = java.lang.Math.round(r2)
            r9[r1] = r8
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.graphics.DragPreviewProvider.getScaleAndPosition(android.graphics.Bitmap, int[]):float");
    }
}
