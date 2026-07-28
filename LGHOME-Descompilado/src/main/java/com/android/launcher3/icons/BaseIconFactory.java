package com.android.launcher3.icons;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.icons.BitmapInfo;

/* JADX INFO: loaded from: classes.dex */
public class BaseIconFactory implements AutoCloseable {
    static final boolean ATLEAST_OREO;
    static final boolean ATLEAST_P;
    private static final int DEFAULT_WRAPPER_BACKGROUND = -1;
    private static final float ICON_BADGE_SCALE = 0.444f;
    private static final String TAG = "BaseIconFactory";
    private boolean mBadgeOnLeft;
    private final Canvas mCanvas;
    private final ColorExtractor mColorExtractor;
    protected final Context mContext;
    private boolean mDisableColorExtractor;
    protected final int mFillResIconDpi;
    protected final int mIconBitmapSize;
    private IconNormalizer mNormalizer;
    private final Rect mOldBounds;
    private final PackageManager mPm;
    private ShadowGenerator mShadowGenerator;
    private final boolean mShapeDetection;
    private int mWrapperBackgroundColor;
    private Drawable mWrapperIcon;

    public static int getBadgeSizeForIconSize(int iconSize) {
        return (int) (iconSize * ICON_BADGE_SCALE);
    }

    static {
        ATLEAST_OREO = Build.VERSION.SDK_INT >= 26;
        ATLEAST_P = Build.VERSION.SDK_INT >= 28;
    }

    protected BaseIconFactory(Context context, int fillResIconDpi, int iconBitmapSize, boolean shapeDetection) {
        this.mOldBounds = new Rect();
        this.mBadgeOnLeft = false;
        this.mWrapperBackgroundColor = -1;
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mShapeDetection = shapeDetection;
        this.mFillResIconDpi = fillResIconDpi;
        this.mIconBitmapSize = iconBitmapSize;
        this.mPm = applicationContext.getPackageManager();
        this.mColorExtractor = new ColorExtractor();
        Canvas canvas = new Canvas();
        this.mCanvas = canvas;
        canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        clear();
    }

    protected BaseIconFactory(Context context, int fillResIconDpi, int iconBitmapSize) {
        this(context, fillResIconDpi, iconBitmapSize, false);
    }

    protected void clear() {
        this.mWrapperBackgroundColor = -1;
        this.mDisableColorExtractor = false;
        this.mBadgeOnLeft = false;
    }

    public ShadowGenerator getShadowGenerator() {
        if (this.mShadowGenerator == null) {
            this.mShadowGenerator = new ShadowGenerator(this.mIconBitmapSize);
        }
        return this.mShadowGenerator;
    }

    public IconNormalizer getNormalizer() {
        if (this.mNormalizer == null) {
            this.mNormalizer = new IconNormalizer(this.mContext, this.mIconBitmapSize, this.mShapeDetection);
        }
        return this.mNormalizer;
    }

    public BitmapInfo createIconBitmap(Intent.ShortcutIconResource iconRes) {
        try {
            Resources resourcesForApplication = this.mPm.getResourcesForApplication(iconRes.packageName);
            if (resourcesForApplication != null) {
                return createBadgedIconBitmap(resourcesForApplication.getDrawableForDensity(resourcesForApplication.getIdentifier(iconRes.resourceName, null, null), this.mFillResIconDpi), Process.myUserHandle(), false);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public BitmapInfo createIconBitmap(Bitmap icon) {
        if (this.mIconBitmapSize != icon.getWidth() || this.mIconBitmapSize != icon.getHeight()) {
            icon = createIconBitmap(new BitmapDrawable(this.mContext.getResources(), icon), 1.0f);
        }
        return BitmapInfo.of(icon, extractColor(icon));
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, boolean shrinkNonAdaptiveIcons) {
        return createBadgedIconBitmap(icon, user, shrinkNonAdaptiveIcons, false, (float[]) null);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, int iconAppTargetSdk) {
        return createBadgedIconBitmap(icon, user, iconAppTargetSdk, false);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, int iconAppTargetSdk, boolean isInstantApp) {
        return createBadgedIconBitmap(icon, user, iconAppTargetSdk, isInstantApp, (float[]) null);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, int iconAppTargetSdk, boolean isInstantApp, float[] scale) {
        return createBadgedIconBitmap(icon, user, ATLEAST_P || (ATLEAST_OREO && iconAppTargetSdk >= 26), isInstantApp, scale);
    }

    public Bitmap createScaledBitmapWithoutShadow(Drawable icon, int iconAppTargetSdk) {
        return createScaledBitmapWithoutShadow(icon, ATLEAST_P || (ATLEAST_OREO && iconAppTargetSdk >= 26));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: android.graphics.drawable.Drawable */
    /* JADX WARN: Multi-variable type inference failed */
    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, boolean shrinkNonAdaptiveIcons, boolean isInstantApp, float[] scale) {
        if (scale == null) {
            scale = new float[1];
        }
        Drawable drawableNormalizeAndWrapToAdaptiveIcon = normalizeAndWrapToAdaptiveIcon(icon, shrinkNonAdaptiveIcons, null, scale);
        Bitmap bitmapCreateIconBitmap = createIconBitmap(drawableNormalizeAndWrapToAdaptiveIcon, scale[0]);
        if (ATLEAST_OREO && (drawableNormalizeAndWrapToAdaptiveIcon instanceof AdaptiveIconDrawable)) {
            this.mCanvas.setBitmap(bitmapCreateIconBitmap);
            getShadowGenerator().recreateIcon(Bitmap.createBitmap(bitmapCreateIconBitmap), this.mCanvas);
            this.mCanvas.setBitmap(null);
        }
        if (isInstantApp) {
            badgeWithDrawable(bitmapCreateIconBitmap, this.mContext.getDrawable(R.drawable.ic_instant_app_badge));
        }
        if (user != null) {
            Drawable userBadgedIcon = this.mPm.getUserBadgedIcon(new FixedSizeBitmapDrawable(bitmapCreateIconBitmap), user);
            if (userBadgedIcon instanceof BitmapDrawable) {
                bitmapCreateIconBitmap = ((BitmapDrawable) userBadgedIcon).getBitmap();
            } else {
                bitmapCreateIconBitmap = createIconBitmap(userBadgedIcon, 1.0f);
            }
        }
        int iExtractColor = extractColor(bitmapCreateIconBitmap);
        if (drawableNormalizeAndWrapToAdaptiveIcon instanceof BitmapInfo.Extender) {
            return ((BitmapInfo.Extender) drawableNormalizeAndWrapToAdaptiveIcon).getExtendedInfo(bitmapCreateIconBitmap, iExtractColor, this);
        }
        return BitmapInfo.of(bitmapCreateIconBitmap, iExtractColor);
    }

    public Bitmap createScaledBitmapWithoutShadow(Drawable icon, boolean shrinkNonAdaptiveIcons) {
        RectF rectF = new RectF();
        float[] fArr = new float[1];
        return createIconBitmap(normalizeAndWrapToAdaptiveIcon(icon, shrinkNonAdaptiveIcons, rectF, fArr), Math.min(fArr[0], ShadowGenerator.getScaleForBounds(rectF)));
    }

    public void setBadgeOnLeft(boolean badgeOnLeft) {
        this.mBadgeOnLeft = badgeOnLeft;
    }

    public void setWrapperBackgroundColor(int color) {
        if (Color.alpha(color) < 255) {
            color = -1;
        }
        this.mWrapperBackgroundColor = color;
    }

    public void disableColorExtraction() {
        this.mDisableColorExtractor = true;
    }

    private Drawable normalizeAndWrapToAdaptiveIcon(Drawable icon, boolean shrinkNonAdaptiveIcons, RectF outIconBounds, float[] outScale) {
        float scale;
        if (icon == null) {
            return null;
        }
        if (shrinkNonAdaptiveIcons && ATLEAST_OREO) {
            if (this.mWrapperIcon == null) {
                this.mWrapperIcon = this.mContext.getDrawable(R.drawable.adaptive_icon_drawable_wrapper).mutate();
            }
            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) this.mWrapperIcon;
            adaptiveIconDrawable.setBounds(0, 0, 1, 1);
            boolean[] zArr = new boolean[1];
            scale = getNormalizer().getScale(icon, outIconBounds, adaptiveIconDrawable.getIconMask(), zArr);
            if (!(icon instanceof AdaptiveIconDrawable) && !zArr[0]) {
                FixedScaleDrawable fixedScaleDrawable = (FixedScaleDrawable) adaptiveIconDrawable.getForeground();
                fixedScaleDrawable.setDrawable(icon);
                fixedScaleDrawable.setScale(scale);
                scale = getNormalizer().getScale(adaptiveIconDrawable, outIconBounds, null, null);
                ((ColorDrawable) adaptiveIconDrawable.getBackground()).setColor(this.mWrapperBackgroundColor);
                icon = adaptiveIconDrawable;
            }
        } else {
            scale = getNormalizer().getScale(icon, outIconBounds, null, null);
        }
        outScale[0] = scale;
        return icon;
    }

    public void badgeWithDrawable(Bitmap target, Drawable badge) {
        this.mCanvas.setBitmap(target);
        badgeWithDrawable(this.mCanvas, badge);
        this.mCanvas.setBitmap(null);
    }

    public void badgeWithDrawable(Canvas target, Drawable badge) {
        int badgeSizeForIconSize = getBadgeSizeForIconSize(this.mIconBitmapSize);
        if (this.mBadgeOnLeft) {
            int i = this.mIconBitmapSize;
            badge.setBounds(0, i - badgeSizeForIconSize, badgeSizeForIconSize, i);
        } else {
            int i2 = this.mIconBitmapSize;
            badge.setBounds(i2 - badgeSizeForIconSize, i2 - badgeSizeForIconSize, i2, i2);
        }
        badge.draw(target);
    }

    private Bitmap createIconBitmap(Drawable icon, float scale) {
        return createIconBitmap(icon, scale, this.mIconBitmapSize);
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0081  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public android.graphics.Bitmap createIconBitmap(android.graphics.drawable.Drawable r6, float r7, int r8) {
        /*
            r5 = this;
            android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r8, r8, r0)
            if (r6 != 0) goto L9
            return r0
        L9:
            android.graphics.Canvas r1 = r5.mCanvas
            r1.setBitmap(r0)
            android.graphics.Rect r1 = r5.mOldBounds
            android.graphics.Rect r2 = r6.getBounds()
            r1.set(r2)
            boolean r1 = com.android.launcher3.icons.BaseIconFactory.ATLEAST_OREO
            if (r1 == 0) goto L43
            boolean r1 = r6 instanceof android.graphics.drawable.AdaptiveIconDrawable
            if (r1 == 0) goto L43
            r1 = 1009429163(0x3c2aaaab, float:0.010416667)
            float r2 = (float) r8
            float r1 = r1 * r2
            double r3 = (double) r1
            double r3 = java.lang.Math.ceil(r3)
            int r1 = (int) r3
            r3 = 1065353216(0x3f800000, float:1.0)
            float r3 = r3 - r7
            float r2 = r2 * r3
            r7 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 / r7
            int r7 = java.lang.Math.round(r2)
            int r7 = java.lang.Math.max(r1, r7)
            int r8 = r8 - r7
            r6.setBounds(r7, r7, r8, r8)
            android.graphics.Canvas r7 = r5.mCanvas
            r6.draw(r7)
            goto La7
        L43:
            boolean r1 = r6 instanceof android.graphics.drawable.BitmapDrawable
            if (r1 == 0) goto L63
            r1 = r6
            android.graphics.drawable.BitmapDrawable r1 = (android.graphics.drawable.BitmapDrawable) r1
            android.graphics.Bitmap r2 = r1.getBitmap()
            if (r0 == 0) goto L63
            int r2 = r2.getDensity()
            if (r2 != 0) goto L63
            android.content.Context r2 = r5.mContext
            android.content.res.Resources r2 = r2.getResources()
            android.util.DisplayMetrics r2 = r2.getDisplayMetrics()
            r1.setTargetDensity(r2)
        L63:
            int r1 = r6.getIntrinsicWidth()
            int r2 = r6.getIntrinsicHeight()
            if (r1 <= 0) goto L81
            if (r2 <= 0) goto L81
            float r3 = (float) r1
            float r4 = (float) r2
            float r3 = r3 / r4
            if (r1 <= r2) goto L7a
            float r1 = (float) r8
            float r1 = r1 / r3
            int r1 = (int) r1
            r2 = r1
            r1 = r8
            goto L83
        L7a:
            if (r2 <= r1) goto L81
            float r1 = (float) r8
            float r1 = r1 * r3
            int r1 = (int) r1
            r2 = r8
            goto L83
        L81:
            r1 = r8
            r2 = r1
        L83:
            int r3 = r8 - r1
            int r3 = r3 / 2
            int r4 = r8 - r2
            int r4 = r4 / 2
            int r1 = r1 + r3
            int r2 = r2 + r4
            r6.setBounds(r3, r4, r1, r2)
            android.graphics.Canvas r1 = r5.mCanvas
            r1.save()
            android.graphics.Canvas r1 = r5.mCanvas
            int r8 = r8 / 2
            float r8 = (float) r8
            r1.scale(r7, r7, r8, r8)
            android.graphics.Canvas r7 = r5.mCanvas
            r6.draw(r7)
            android.graphics.Canvas r7 = r5.mCanvas
            r7.restore()
        La7:
            android.graphics.Rect r7 = r5.mOldBounds
            r6.setBounds(r7)
            android.graphics.Canvas r6 = r5.mCanvas
            r7 = 0
            r6.setBitmap(r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.BaseIconFactory.createIconBitmap(android.graphics.drawable.Drawable, float, int):android.graphics.Bitmap");
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        clear();
    }

    public BitmapInfo makeDefaultIcon(UserHandle user) {
        return createBadgedIconBitmap(getFullResDefaultActivityIcon(this.mFillResIconDpi), user, Build.VERSION.SDK_INT);
    }

    public static Drawable getFullResDefaultActivityIcon(int iconDpi) {
        return Resources.getSystem().getDrawableForDensity(Build.VERSION.SDK_INT >= 26 ? android.R.drawable.sym_def_app_icon : android.R.mipmap.sym_def_app_icon, iconDpi);
    }

    public BitmapInfo badgeBitmap(final Bitmap source, final BitmapInfo badgeInfo) {
        int i = this.mIconBitmapSize;
        return BitmapInfo.of(BitmapRenderer.createHardwareBitmap(i, i, new BitmapRenderer() { // from class: com.android.launcher3.icons.-$$Lambda$BaseIconFactory$h7JAFwixd36huBKZbWdf7v03KXg
            @Override // com.android.launcher3.icons.BitmapRenderer
            public final void draw(Canvas canvas) {
                this.f$0.lambda$badgeBitmap$0$BaseIconFactory(source, badgeInfo, canvas);
            }
        }), badgeInfo.color);
    }

    public /* synthetic */ void lambda$badgeBitmap$0$BaseIconFactory(Bitmap bitmap, BitmapInfo bitmapInfo, Canvas canvas) {
        getShadowGenerator().recreateIcon(bitmap, canvas);
        badgeWithDrawable(canvas, new FixedSizeBitmapDrawable(bitmapInfo.icon));
    }

    private int extractColor(Bitmap bitmap) {
        if (this.mDisableColorExtractor) {
            return 0;
        }
        return this.mColorExtractor.findDominantColorByHue(bitmap);
    }

    private static class FixedSizeBitmapDrawable extends BitmapDrawable {
        public FixedSizeBitmapDrawable(Bitmap bitmap) {
            super((Resources) null, bitmap);
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return getBitmap().getWidth();
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return getBitmap().getWidth();
        }
    }
}
