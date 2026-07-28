package com.android.launcher3.graphics;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.lge.launcher3.R;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class LauncherIcons {
    private static final int DEFAULT_WRAPPER_BACKGROUND = -1;
    private static final Canvas sCanvas;
    private static final Rect sOldBounds = new Rect();

    static {
        Canvas canvas = new Canvas();
        sCanvas = canvas;
        canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
    }

    public static Bitmap createIconBitmap(Intent.ShortcutIconResource iconRes, Context context) {
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(iconRes.packageName);
            if (resourcesForApplication != null) {
                return createIconBitmap(resourcesForApplication.getDrawableForDensity(resourcesForApplication.getIdentifier(iconRes.resourceName, null, null), LauncherAppState.getIDP(context).fillResIconDpi), context);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public static Bitmap createIconBitmap(Bitmap icon, Context context) {
        int i = LauncherAppState.getIDP(context).iconBitmapSize;
        return (i == icon.getWidth() && i == icon.getHeight()) ? icon : createIconBitmap(new BitmapDrawable(context.getResources(), icon), context);
    }

    public static Bitmap createBadgedIconBitmap(Drawable icon, UserHandle user, Context context, int iconAppTargetSdk) {
        float scale;
        Drawable drawableWrapToAdaptiveIconDrawable;
        if (FeatureFlags.LAUNCHER3_DISABLE_ICON_NORMALIZATION) {
            scale = 1.0f;
        } else {
            IconNormalizer iconNormalizer = IconNormalizer.getInstance(context);
            if (Utilities.isAtLeastO() && iconAppTargetSdk >= 26) {
                boolean[] zArr = new boolean[1];
                AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) context.getDrawable(R.drawable.adaptive_icon_drawable_wrapper).mutate();
                adaptiveIconDrawable.setBounds(0, 0, 1, 1);
                scale = iconNormalizer.getScale(icon, null, adaptiveIconDrawable.getIconMask(), zArr);
                if (!zArr[0] && (drawableWrapToAdaptiveIconDrawable = wrapToAdaptiveIconDrawable(context, icon, scale)) != icon) {
                    scale = iconNormalizer.getScale(drawableWrapToAdaptiveIconDrawable, null, null, null);
                    icon = drawableWrapToAdaptiveIconDrawable;
                }
            } else {
                scale = iconNormalizer.getScale(icon, null, null, null);
            }
        }
        return badgeIconForUser(createIconBitmap(icon, context, scale), user, context);
    }

    public static Bitmap badgeIconForUser(Bitmap icon, UserHandle user, Context context) {
        if (user == null || Process.myUserHandle().equals(user)) {
            return icon;
        }
        Drawable userBadgedIcon = context.getPackageManager().getUserBadgedIcon(new FixedSizeBitmapDrawable(icon), user);
        if (userBadgedIcon instanceof BitmapDrawable) {
            return ((BitmapDrawable) userBadgedIcon).getBitmap();
        }
        return createIconBitmap(userBadgedIcon, context);
    }

    public static Bitmap createScaledBitmapWithoutShadow(Drawable icon, Context context, int iconAppTargetSdk) {
        float scale;
        Drawable drawableWrapToAdaptiveIconDrawable;
        RectF rectF = new RectF();
        if (FeatureFlags.LAUNCHER3_DISABLE_ICON_NORMALIZATION) {
            scale = 1.0f;
        } else {
            IconNormalizer iconNormalizer = IconNormalizer.getInstance(context);
            if (Utilities.isAtLeastO() && iconAppTargetSdk >= 26) {
                boolean[] zArr = new boolean[1];
                AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) context.getDrawable(R.drawable.adaptive_icon_drawable_wrapper).mutate();
                adaptiveIconDrawable.setBounds(0, 0, 1, 1);
                scale = iconNormalizer.getScale(icon, rectF, adaptiveIconDrawable.getIconMask(), zArr);
                if (Utilities.isAtLeastO() && !zArr[0] && (drawableWrapToAdaptiveIconDrawable = wrapToAdaptiveIconDrawable(context, icon, scale)) != icon) {
                    scale = iconNormalizer.getScale(drawableWrapToAdaptiveIconDrawable, rectF, null, null);
                    icon = drawableWrapToAdaptiveIconDrawable;
                }
            } else {
                scale = iconNormalizer.getScale(icon, rectF, null, null);
            }
        }
        return createIconBitmap(icon, context, scale);
    }

    public static Bitmap addShadowToIcon(Bitmap icon, Context context) {
        return ShadowGenerator.getInstance(context).recreateIcon(icon);
    }

    public static Bitmap badgeWithBitmap(Bitmap srcTgt, Bitmap badge, Context context) {
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.profile_badge_size);
        Canvas canvas = sCanvas;
        synchronized (canvas) {
            canvas.setBitmap(srcTgt);
            canvas.drawBitmap(badge, new Rect(0, 0, badge.getWidth(), badge.getHeight()), new Rect(srcTgt.getWidth() - dimensionPixelSize, srcTgt.getHeight() - dimensionPixelSize, srcTgt.getWidth(), srcTgt.getHeight()), new Paint(2));
            canvas.setBitmap(null);
        }
        return srcTgt;
    }

    public static Bitmap createIconBitmap(Drawable icon, Context context) {
        return createIconBitmap(icon, context, 1.0f);
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x007f A[ADDED_TO_REGION, Catch: all -> 0x00a0, REMOVE, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x000d, B:14:0x0035, B:17:0x0041, B:19:0x0046, B:23:0x0055, B:25:0x0075, B:27:0x0079, B:29:0x0084, B:30:0x009e, B:28:0x007f, B:21:0x004e, B:7:0x0017, B:9:0x001b, B:11:0x0024, B:13:0x002a), top: B:35:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Bitmap createIconBitmap(android.graphics.drawable.Drawable r8, android.content.Context r9, float r10) {
        /*
            android.graphics.Canvas r0 = com.android.launcher3.graphics.LauncherIcons.sCanvas
            monitor-enter(r0)
            com.android.launcher3.InvariantDeviceProfile r1 = com.android.launcher3.LauncherAppState.getIDP(r9)     // Catch: java.lang.Throwable -> La0
            int r1 = r1.iconBitmapSize     // Catch: java.lang.Throwable -> La0
            boolean r2 = r8 instanceof android.graphics.drawable.PaintDrawable     // Catch: java.lang.Throwable -> La0
            if (r2 == 0) goto L17
            r9 = r8
            android.graphics.drawable.PaintDrawable r9 = (android.graphics.drawable.PaintDrawable) r9     // Catch: java.lang.Throwable -> La0
            r9.setIntrinsicWidth(r1)     // Catch: java.lang.Throwable -> La0
            r9.setIntrinsicHeight(r1)     // Catch: java.lang.Throwable -> La0
            goto L35
        L17:
            boolean r2 = r8 instanceof android.graphics.drawable.BitmapDrawable     // Catch: java.lang.Throwable -> La0
            if (r2 == 0) goto L35
            r2 = r8
            android.graphics.drawable.BitmapDrawable r2 = (android.graphics.drawable.BitmapDrawable) r2     // Catch: java.lang.Throwable -> La0
            android.graphics.Bitmap r3 = r2.getBitmap()     // Catch: java.lang.Throwable -> La0
            if (r3 == 0) goto L35
            int r3 = r3.getDensity()     // Catch: java.lang.Throwable -> La0
            if (r3 != 0) goto L35
            android.content.res.Resources r9 = r9.getResources()     // Catch: java.lang.Throwable -> La0
            android.util.DisplayMetrics r9 = r9.getDisplayMetrics()     // Catch: java.lang.Throwable -> La0
            r2.setTargetDensity(r9)     // Catch: java.lang.Throwable -> La0
        L35:
            int r9 = r8.getIntrinsicWidth()     // Catch: java.lang.Throwable -> La0
            int r2 = r8.getIntrinsicHeight()     // Catch: java.lang.Throwable -> La0
            if (r9 <= 0) goto L53
            if (r2 <= 0) goto L53
            float r3 = (float) r9     // Catch: java.lang.Throwable -> La0
            float r4 = (float) r2     // Catch: java.lang.Throwable -> La0
            float r3 = r3 / r4
            if (r9 <= r2) goto L4c
            float r9 = (float) r1     // Catch: java.lang.Throwable -> La0
            float r9 = r9 / r3
            int r9 = (int) r9     // Catch: java.lang.Throwable -> La0
            r2 = r9
            r9 = r1
            goto L55
        L4c:
            if (r2 <= r9) goto L53
            float r9 = (float) r1     // Catch: java.lang.Throwable -> La0
            float r9 = r9 * r3
            int r9 = (int) r9     // Catch: java.lang.Throwable -> La0
            r2 = r1
            goto L55
        L53:
            r9 = r1
            r2 = r9
        L55:
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch: java.lang.Throwable -> La0
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r1, r1, r3)     // Catch: java.lang.Throwable -> La0
            r0.setBitmap(r3)     // Catch: java.lang.Throwable -> La0
            int r4 = r1 - r9
            int r4 = r4 / 2
            int r5 = r1 - r2
            int r5 = r5 / 2
            android.graphics.Rect r6 = com.android.launcher3.graphics.LauncherIcons.sOldBounds     // Catch: java.lang.Throwable -> La0
            android.graphics.Rect r7 = r8.getBounds()     // Catch: java.lang.Throwable -> La0
            r6.set(r7)     // Catch: java.lang.Throwable -> La0
            boolean r7 = com.android.launcher3.Utilities.isAtLeastO()     // Catch: java.lang.Throwable -> La0
            if (r7 == 0) goto L7f
            boolean r7 = r8 instanceof android.graphics.drawable.AdaptiveIconDrawable     // Catch: java.lang.Throwable -> La0
            if (r7 == 0) goto L7f
            int r9 = r9 + r4
            int r2 = r2 + r5
            r8.setBounds(r4, r5, r9, r2)     // Catch: java.lang.Throwable -> La0
            goto L84
        L7f:
            int r9 = r9 + r4
            int r2 = r2 + r5
            r8.setBounds(r4, r5, r9, r2)     // Catch: java.lang.Throwable -> La0
        L84:
            r9 = 1
            r0.save(r9)     // Catch: java.lang.Throwable -> La0
            int r9 = r1 / 2
            float r9 = (float) r9     // Catch: java.lang.Throwable -> La0
            int r1 = r1 / 2
            float r1 = (float) r1     // Catch: java.lang.Throwable -> La0
            r0.scale(r10, r10, r9, r1)     // Catch: java.lang.Throwable -> La0
            r8.draw(r0)     // Catch: java.lang.Throwable -> La0
            r0.restore()     // Catch: java.lang.Throwable -> La0
            r8.setBounds(r6)     // Catch: java.lang.Throwable -> La0
            r8 = 0
            r0.setBitmap(r8)     // Catch: java.lang.Throwable -> La0
            monitor-exit(r0)     // Catch: java.lang.Throwable -> La0
            return r3
        La0:
            r8 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> La0
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.graphics.LauncherIcons.createIconBitmap(android.graphics.drawable.Drawable, android.content.Context, float):android.graphics.Bitmap");
    }

    public static Bitmap createIconBitmap(Drawable icon, Context context, float scale, int width, int height, boolean isMonochrome) {
        BitmapDrawable bitmapDrawable;
        Bitmap bitmap;
        Bitmap bitmapCreateBitmap;
        int i = width;
        int i2 = height;
        int color = isMonochrome ? context.getResources().getColor(R.color.themed_icon_bg_color) : -1;
        Canvas canvas = sCanvas;
        synchronized (canvas) {
            int i3 = LauncherAppState.getIDP(context).iconBitmapSize;
            boolean z = i == context.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_width);
            if (icon instanceof PaintDrawable) {
                PaintDrawable paintDrawable = (PaintDrawable) icon;
                paintDrawable.setIntrinsicWidth(i);
                paintDrawable.setIntrinsicHeight(i2);
            } else if ((icon instanceof BitmapDrawable) && (bitmap = (bitmapDrawable = (BitmapDrawable) icon).getBitmap()) != null && bitmap.getDensity() == 0) {
                bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
            }
            int intrinsicWidth = icon.getIntrinsicWidth();
            int intrinsicHeight = icon.getIntrinsicHeight();
            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                float f = intrinsicWidth / intrinsicHeight;
                if (intrinsicWidth > intrinsicHeight) {
                    i2 = (int) (i / f);
                } else if (intrinsicHeight > intrinsicWidth) {
                    i = (int) (i2 * f);
                }
            }
            bitmapCreateBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
            canvas.setBitmap(bitmapCreateBitmap);
            int i4 = (i - i) / 2;
            int i5 = (i2 - i2) / 2;
            Rect rect = sOldBounds;
            rect.set(icon.getBounds());
            if (!Utilities.isAtLeastO() || (icon instanceof AdaptiveIconDrawable)) {
                icon.setBounds(i4, i5, i4 + i, i5 + i2);
            } else {
                icon.setBounds(i4, i5, i4 + i, i5 + i2);
            }
            Path path = new Path();
            float f2 = i;
            float f3 = 1.0f - scale;
            float f4 = (f2 * f3) / 2.0f;
            float f5 = i2;
            float f6 = (f3 * f5) / 2.0f;
            path.addRoundRect(new RectF(f4, f6, (f2 * scale) + f4, (f5 * scale) + f6), new float[]{30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f}, Path.Direction.CW);
            canvas.save(1);
            canvas.clipPath(path);
            canvas.scale(scale, scale, i / 2, i2 / 2);
            if (z) {
                Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                bitmapCreateBitmap2.eraseColor(color);
                canvas.drawBitmap(bitmapCreateBitmap2, 0.0f, 0.0f, (Paint) null);
            }
            icon.draw(canvas);
            canvas.restore();
            icon.setBounds(rect);
            canvas.setBitmap(null);
        }
        return bitmapCreateBitmap;
    }

    public static Drawable wrapToAdaptiveIconDrawable(Context context, Drawable drawable, float scale) {
        if (!Utilities.isAtLeastO()) {
            return drawable;
        }
        try {
            if (!(drawable instanceof AdaptiveIconDrawable)) {
                AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) context.getDrawable(R.drawable.adaptive_icon_drawable_wrapper).mutate();
                FixedScaleDrawable fixedScaleDrawable = (FixedScaleDrawable) adaptiveIconDrawable.getForeground();
                fixedScaleDrawable.setDrawable(drawable);
                fixedScaleDrawable.setScale(scale);
                return adaptiveIconDrawable;
            }
        } catch (Exception unused) {
        }
        return drawable;
    }

    public static void applyTaskColor(AdaptiveIconDrawable dr, int color) {
        if (Color.alpha(color) < 255) {
            color = -1;
        }
        if (dr.getBackground() instanceof ColorDrawable) {
            ((ColorDrawable) dr.getBackground()).setColor(color);
        }
    }

    public static Bitmap createShortcutIcon(ShortcutInfoCompat shortcutInfo, Context context) {
        return createShortcutIcon(shortcutInfo, context, true);
    }

    public static Bitmap createShortcutIcon(ShortcutInfoCompat shortcutInfo, Context context, boolean badged) {
        Bitmap bitmapCreateScaledBitmapWithoutShadow;
        Bitmap bitmap;
        LauncherAppState launcherAppState = LauncherAppState.getInstance(context);
        Drawable shortcutIconDrawable = DeepShortcutManager.getInstance(context).getShortcutIconDrawable(shortcutInfo, launcherAppState.getInvariantDeviceProfile().fillResIconDpi);
        IconCache iconCache = launcherAppState.getIconCache();
        if (shortcutIconDrawable == null) {
            bitmapCreateScaledBitmapWithoutShadow = iconCache.getDefaultIcon(Process.myUserHandle());
        } else {
            bitmapCreateScaledBitmapWithoutShadow = createScaledBitmapWithoutShadow(shortcutIconDrawable, context, 26);
        }
        if (!badged) {
            return bitmapCreateScaledBitmapWithoutShadow;
        }
        Bitmap bitmapAddShadowToIcon = addShadowToIcon(bitmapCreateScaledBitmapWithoutShadow, context);
        ComponentName activity = shortcutInfo.getActivity();
        if (activity != null) {
            AppInfo appInfo = new AppInfo();
            appInfo.user = shortcutInfo.getUserHandle();
            appInfo.componentName = activity;
            appInfo.intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("android.intent.category.LAUNCHER").setComponent(activity);
            iconCache.getTitleAndIcon(appInfo, false);
            bitmap = appInfo.iconBitmap;
        } else {
            PackageItemInfo packageItemInfo = new PackageItemInfo(shortcutInfo.getPackage());
            iconCache.getTitleAndIconForApp(packageItemInfo, false);
            bitmap = packageItemInfo.iconBitmap;
        }
        return Utilities.badgeWithBitmap(bitmapAddShadowToIcon, bitmap, context);
    }

    public static Bitmap getShortcutInfoBadge(ShortcutInfoCompat shortcutInfo, IconCache cache) {
        ComponentName activity = shortcutInfo.getActivity();
        if (activity != null) {
            AppInfo appInfo = new AppInfo();
            appInfo.user = shortcutInfo.getUserHandle();
            appInfo.componentName = activity;
            appInfo.intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("android.intent.category.LAUNCHER").setComponent(activity);
            cache.getTitleAndIcon(appInfo, false);
            return appInfo.iconBitmap;
        }
        PackageItemInfo packageItemInfo = new PackageItemInfo(shortcutInfo.getPackage());
        cache.getTitleAndIconForApp(packageItemInfo, false);
        return packageItemInfo.iconBitmap;
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
