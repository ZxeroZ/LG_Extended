package com.android.launcher3;

import android.R;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.provider.Settings;
import android.util.PathParser;
import android.util.SparseArray;
import com.lge.launcher3.homesettings.IconFramesPrefActivity;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherIconsForShadow {
    private static final int AMBIENT_SHADOW_ALPHA = 30;
    public static final float ICON_FACTOR = 0.9599999f;
    public static final int ICON_MASK_CIRCLE = 5;
    public static final int ICON_MASK_CYLINDER = 3;
    public static final int ICON_MASK_ROUND = 2;
    public static final int ICON_MASK_ROUND_SQUARE = 1;
    public static final int ICON_MASK_SQUARE = 4;
    public static final int ICON_ORIGINAL = 0;
    private static final float ICON_SIZE_BLUR_FACTOR = 0.010416667f;
    private static final float ICON_SIZE_KEY_SHADOW_DELTA_FACTOR = 0.020833334f;
    private static final int KEY_SHADOW_ALPHA = 61;
    public static final float MASK_SIZE = 100.0f;
    static final String TAG = "LauncherIconsForShadow";
    private Context mContext;
    private final int mIconSize;
    private int mMonochromeBgColor;
    private final Resources mRes;
    private final SparseArray<Bitmap> mShadowCache = new SparseArray<>();

    public LauncherIconsForShadow(Context context) {
        this.mContext = context;
        Resources resources = context.getResources();
        this.mRes = resources;
        this.mIconSize = resources.getDimensionPixelSize(R.dimen.app_icon_size);
        this.mMonochromeBgColor = resources.getColor(com.lge.launcher3.R.color.themed_icon_bg_color);
    }

    public Drawable wrapIconDrawableWithShadow(Drawable drawable, boolean isTransparentBG) {
        return wrapIconDrawableWithShadow(drawable, isTransparentBG, false);
    }

    public Drawable wrapIconDrawableWithShadow(Drawable drawable, boolean isTransparentBG, boolean isUseMonochrome) {
        Bitmap shadowBitmap = getShadowBitmap(drawable, isTransparentBG, isUseMonochrome);
        return shadowBitmap == null ? drawable : new ShadowDrawable(shadowBitmap, drawable);
    }

    private Bitmap getShadowBitmap(Drawable d, boolean isTransparentBG, boolean isUseMonochrome) {
        Path updateMaskPathBoundsInternal;
        if (d == null) {
            return null;
        }
        int iMax = Math.max(this.mIconSize, d.getIntrinsicHeight());
        d.setBounds(0, 0, iMax, iMax);
        float f = iMax;
        float f2 = 0.010416667f * f;
        float f3 = 0.020833334f * f;
        if (isUseMonochrome) {
            f2 = 0.0f;
            f3 = 0.0f;
        }
        int i = (int) (f + (f2 * 2.0f) + f3);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        float f4 = (f3 / 2.0f) + f2;
        canvas.translate(f4, f2);
        Paint paint = new Paint(1);
        paint.setColor(isUseMonochrome ? this.mMonochromeBgColor : 0);
        if (d instanceof AdaptiveIconDrawable) {
            updateMaskPathBoundsInternal = ((AdaptiveIconDrawable) d).getIconMask();
        } else {
            int framdIconID = getFramdIconID();
            if (framdIconID == 0 && !isTransparentBG) {
                return null;
            }
            if (framdIconID == 0 && isTransparentBG) {
                framdIconID = getIdentifierForLGRes("config_icon_mask", "string");
            }
            updateMaskPathBoundsInternal = getUpdateMaskPathBoundsInternal(d.getBounds(), framdIconID);
        }
        paint.setShadowLayer(f2, 0.0f, 0.0f, 503316480);
        canvas.drawPath(updateMaskPathBoundsInternal, paint);
        canvas.translate(0.0f, f3);
        paint.setShadowLayer(f2, 0.0f, 0.0f, 1023410176);
        canvas.drawPath(updateMaskPathBoundsInternal, paint);
        canvas.setMatrix(null);
        canvas.translate(f4, f2);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        paint.setColor(isUseMonochrome ? this.mMonochromeBgColor : 0);
        if (isTransparentBG) {
            canvas.drawPath(updateMaskPathBoundsInternal, paint);
        } else {
            Matrix matrix = new Matrix();
            if (!isUseMonochrome) {
                matrix.postScale(0.98f, 0.98f);
            }
            Path path = new Path();
            updateMaskPathBoundsInternal.transform(matrix, path);
            canvas.drawPath(path, paint);
        }
        canvas.setBitmap(null);
        return bitmapCreateBitmap;
    }

    private int getFramdIconID() {
        int currentUser;
        int intForUser;
        try {
            currentUser = ActivityManager.getCurrentUser();
        } catch (Exception unused) {
            LGLog.v(TAG, "Can not get CurrentUserId.. It is not a System Process!");
            currentUser = -1;
        }
        try {
            if (currentUser == -1) {
                intForUser = Settings.System.getInt(this.mContext.getContentResolver(), IconFramesPrefActivity.SETTINGS_ICON_FRAMES);
            } else {
                intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), IconFramesPrefActivity.SETTINGS_ICON_FRAMES, 0, currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            intForUser = 0;
        }
        if (intForUser == 1) {
            return getIdentifierForLGRes("config_icon_mask_rounded_square", "string");
        }
        if (intForUser == 2) {
            return getIdentifierForLGRes("config_icon_mask_more_rounded_square", "string");
        }
        if (intForUser == 3) {
            return getIdentifierForLGRes("config_icon_mask_cylinder", "string");
        }
        if (intForUser == 4) {
            return getIdentifierForLGRes("config_icon_mask_square", "string");
        }
        if (intForUser != 5) {
            return 0;
        }
        return getIdentifierForLGRes("config_icon_mask_circle", "string");
    }

    public int getIdentifierForLGRes(String resoureName, String type) {
        return this.mRes.getIdentifier(resoureName, type, "com.lge");
    }

    private Path getUpdateMaskPathBoundsInternal(Rect b, int resourceId) {
        String string = Resources.getSystem().getString(resourceId);
        Path pathCreatePathFromPathData = PathParser.createPathFromPathData(string);
        Path pathCreatePathFromPathData2 = PathParser.createPathFromPathData(string);
        Matrix matrix = new Matrix();
        matrix.setScale(b.width() / 100.0f, b.height() / 100.0f);
        matrix.postTranslate(b.left, b.top);
        pathCreatePathFromPathData.reset();
        pathCreatePathFromPathData2.transform(matrix, pathCreatePathFromPathData);
        return pathCreatePathFromPathData;
    }

    private static class ShadowDrawable extends DrawableWrapper {
        final MyConstantState mState;

        public ShadowDrawable(Bitmap shadow, Drawable dr) {
            super(dr);
            this.mState = new MyConstantState(shadow, dr.getConstantState());
        }

        ShadowDrawable(MyConstantState state) {
            super(state.mChildState.newDrawable());
            this.mState = state;
        }

        @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
        public Drawable.ConstantState getConstantState() {
            return this.mState;
        }

        @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.drawBitmap(this.mState.mShadow, (Rect) null, getBounds(), this.mState.mPaint);
            canvas.save();
            canvas.scale(0.9599999f, 0.9599999f, r0.width() / 2, r0.height() / 2);
            super.draw(canvas);
            canvas.restore();
        }

        private static class MyConstantState extends Drawable.ConstantState {
            final Drawable.ConstantState mChildState;
            final Paint mPaint = new Paint(2);
            final Bitmap mShadow;

            MyConstantState(Bitmap shadow, Drawable.ConstantState childState) {
                this.mShadow = shadow;
                this.mChildState = childState;
            }

            @Override // android.graphics.drawable.Drawable.ConstantState
            public Drawable newDrawable() {
                return new ShadowDrawable(this);
            }

            @Override // android.graphics.drawable.Drawable.ConstantState
            public int getChangingConfigurations() {
                return this.mChildState.getChangingConfigurations();
            }
        }
    }
}
