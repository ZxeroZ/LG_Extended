package com.android.launcher3.dragndrop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.android.launcher3.Launcher;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.ShiftedBitmapDrawable;
import com.android.launcher3.icons.BitmapRenderer;
import com.android.launcher3.util.Preconditions;
import com.lge.launcher3.R;
import com.lge.launcher3.folder.FolderColorUtil;
import com.lge.launcher3.util.DDTUtils;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes.dex */
public class FolderAdaptiveIcon extends AdaptiveIconDrawable {
    private static final String TAG = "FolderAdaptiveIcon";
    private final Drawable mBadge;
    private final Drawable.ConstantState mConstantState;
    private final Path mMask;

    private FolderAdaptiveIcon(Drawable bg, Drawable fg, Drawable badge, Path mask) {
        super(bg, fg);
        this.mBadge = badge;
        this.mMask = mask;
        this.mConstantState = new MyConstantState(bg.getConstantState(), fg.getConstantState(), badge.getConstantState(), mask);
    }

    @Override // android.graphics.drawable.AdaptiveIconDrawable
    public Path getIconMask() {
        return this.mMask;
    }

    public Drawable getBadge() {
        return this.mBadge;
    }

    public static FolderAdaptiveIcon createFolderAdaptiveIcon(final Launcher launcher, final int folderId, final Point dragViewSize) {
        Preconditions.assertNonUiThread();
        int dimensionPixelSize = launcher.getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        final Bitmap bitmapCreateBitmap = Bitmap.createBitmap(dragViewSize.x - dimensionPixelSize, dragViewSize.y - dimensionPixelSize, Bitmap.Config.ARGB_8888);
        try {
            return (FolderAdaptiveIcon) new MainThreadExecutor().submit(new Callable() { // from class: com.android.launcher3.dragndrop.-$$Lambda$FolderAdaptiveIcon$NhL5ZWdXoj2nMjxQ9Ckuhd9VDn4
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return FolderAdaptiveIcon.lambda$createFolderAdaptiveIcon$0(launcher, folderId, bitmapCreateBitmap, dragViewSize);
                }
            }).get();
        } catch (Exception e) {
            Log.e(TAG, "Unable to create folder icon", e);
            return null;
        }
    }

    static /* synthetic */ FolderAdaptiveIcon lambda$createFolderAdaptiveIcon$0(Launcher launcher, int i, Bitmap bitmap, Point point) throws Exception {
        FolderIcon folderIconFindFolderIcon = launcher.findFolderIcon(i);
        if (folderIconFindFolderIcon == null) {
            return null;
        }
        return createDrawableOnUiThread(folderIconFindFolderIcon, bitmap, point);
    }

    private static FolderAdaptiveIcon createDrawableOnUiThread(FolderIcon icon, Bitmap badgeBitmap, Point dragViewSize) {
        Preconditions.assertUIThread();
        float dimension = icon.getResources().getDimension(R.dimen.blur_size_medium_outline) / 2.0f;
        new Canvas().setBitmap(badgeBitmap);
        float extraInsetFraction = (AdaptiveIconDrawable.getExtraInsetFraction() * 2.0f) + 1.0f;
        int i = (int) (dragViewSize.x * extraInsetFraction);
        int i2 = (int) (dragViewSize.y * extraInsetFraction);
        float extraInsetFraction2 = AdaptiveIconDrawable.getExtraInsetFraction() / extraInsetFraction;
        final float f = i * extraInsetFraction2;
        final float f2 = extraInsetFraction2 * i2;
        Bitmap bitmapCreateHardwareBitmap = BitmapRenderer.createHardwareBitmap(i, i2, new BitmapRenderer() { // from class: com.android.launcher3.dragndrop.-$$Lambda$FolderAdaptiveIcon$VAvyUUlxbAXKhU244DygbtLtDlU
            @Override // com.android.launcher3.icons.BitmapRenderer
            public final void draw(Canvas canvas) {
                FolderAdaptiveIcon.lambda$createDrawableOnUiThread$1(f, f2, canvas);
            }
        });
        Path path = new Path();
        new Matrix().setTranslate(dimension, dimension);
        return new FolderAdaptiveIcon(new ColorDrawable(0), new ShiftedBitmapDrawable(bitmapCreateHardwareBitmap, dimension - f, dimension - f2), new ShiftedBitmapDrawable(badgeBitmap, dimension, dimension), path);
    }

    static /* synthetic */ void lambda$createDrawableOnUiThread$1(float f, float f2, Canvas canvas) {
        int iSave = canvas.save();
        canvas.translate(f, f2);
        canvas.restoreToCount(iSave);
    }

    public static FolderAdaptiveIcon createFolderAdaptiveIcon(final Launcher launcher, final long folderId, Point dragViewSize) {
        Preconditions.assertNonUiThread();
        int dimensionPixelSize = launcher.getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        if (dragViewSize.x > dimensionPixelSize && dragViewSize.y > dimensionPixelSize) {
            final Bitmap bitmapCreateBitmap = Bitmap.createBitmap(dragViewSize.x - dimensionPixelSize, dragViewSize.y - dimensionPixelSize, Bitmap.Config.ARGB_8888);
            try {
                return (FolderAdaptiveIcon) new MainThreadExecutor().submit(new Callable() { // from class: com.android.launcher3.dragndrop.-$$Lambda$FolderAdaptiveIcon$F-P3kmQEyxiJH4Az2jz9Trl0ZBQ
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        return FolderAdaptiveIcon.lambda$createFolderAdaptiveIcon$2(launcher, folderId, bitmapCreateBitmap);
                    }
                }).get();
            } catch (Exception e) {
                Log.e(TAG, "Unable to create folder icon", e);
            }
        }
        return null;
    }

    static /* synthetic */ FolderAdaptiveIcon lambda$createFolderAdaptiveIcon$2(Launcher launcher, long j, Bitmap bitmap) throws Exception {
        FolderIcon folderIconFindFolderIcon = launcher.findFolderIcon(j);
        if (folderIconFindFolderIcon == null) {
            return null;
        }
        return createDrawableOnUiThread(folderIconFindFolderIcon, bitmap);
    }

    private static FolderAdaptiveIcon createDrawableOnUiThread(final FolderIcon icon, Bitmap badgeBitmap) {
        Preconditions.assertUIThread();
        float dimension = icon.getResources().getDimension(R.dimen.blur_size_medium_outline) / 2.0f;
        boolean z = DDTUtils.isAdditionalIconThemeApplied(icon.getContext()) || DDTUtils.isAdditionalThemeApplied(icon.getContext());
        int width = icon.mPreviewBackground.getWidth();
        int height = icon.mPreviewBackground.getHeight();
        Bitmap bitmapCreateHardwareBitmap = BitmapRenderer.createHardwareBitmap(width, height, new BitmapRenderer() { // from class: com.android.launcher3.dragndrop.-$$Lambda$FolderAdaptiveIcon$_bLVvhq3FX9q75HJDQNKmZTVhoc
            @Override // com.android.launcher3.icons.BitmapRenderer
            public final void draw(Canvas canvas) {
                FolderAdaptiveIcon.lambda$createDrawableOnUiThread$3(icon, canvas);
            }
        });
        Path path = z ? new Path() : new Path(DDTUtils.getCurrentMaskInfo(icon.getContext(), new Rect(0, 0, width, height), false).getMask());
        ShiftedBitmapDrawable shiftedBitmapDrawable = new ShiftedBitmapDrawable(badgeBitmap, dimension, dimension);
        ShiftedBitmapDrawable shiftedBitmapDrawable2 = new ShiftedBitmapDrawable(bitmapCreateHardwareBitmap, 0.0f, 0.0f);
        int folderBGColor = z ? 0 : FolderColorUtil.getFolderBGColor(icon.getContext(), icon.getFolderInfo().folderColor);
        if (!z && folderBGColor == 0) {
            folderBGColor = icon.getResources().getColor(R.color.folder_colorpicker_dialog_color);
        }
        if (z) {
            Drawable shiftedBitmapDrawable3 = new ShiftedBitmapDrawable(Bitmap.createScaledBitmap(FolderColorUtil.getFolderIconMask(icon.getContext()), (int) (width * icon.getScaleY()), (int) (height * icon.getScaleY()), false), 0.0f, 0.0f);
            if (!z) {
                shiftedBitmapDrawable3 = new ColorDrawable(folderBGColor);
            }
            return new FolderAdaptiveIcon(shiftedBitmapDrawable3, shiftedBitmapDrawable2, shiftedBitmapDrawable, path);
        }
        return new FolderAdaptiveIcon(new ColorDrawable(folderBGColor), shiftedBitmapDrawable2, shiftedBitmapDrawable, path);
    }

    static /* synthetic */ void lambda$createDrawableOnUiThread$3(FolderIcon folderIcon, Canvas canvas) {
        int iSave = canvas.save();
        folderIcon.drawPreviewFromExternal(canvas);
        canvas.restoreToCount(iSave);
    }

    @Override // android.graphics.drawable.AdaptiveIconDrawable, android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        return this.mConstantState;
    }

    private static class MyConstantState extends Drawable.ConstantState {
        private final Drawable.ConstantState mBadge;
        private final Drawable.ConstantState mBg;
        private final Drawable.ConstantState mFg;
        private final Path mMask;

        MyConstantState(Drawable.ConstantState bg, Drawable.ConstantState fg, Drawable.ConstantState badge, Path mask) {
            this.mBg = bg;
            this.mFg = fg;
            this.mBadge = badge;
            this.mMask = mask;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new FolderAdaptiveIcon(this.mBg.newDrawable(), this.mFg.newDrawable(), this.mBadge.newDrawable(), this.mMask);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mBg.getChangingConfigurations() & this.mFg.getChangingConfigurations() & this.mBadge.getChangingConfigurations();
        }
    }
}
