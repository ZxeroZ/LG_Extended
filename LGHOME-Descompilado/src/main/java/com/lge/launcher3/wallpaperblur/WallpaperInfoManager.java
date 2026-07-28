package com.lge.launcher3.wallpaperblur;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.android.launcher3.util.WallpaperUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperInfoManager {
    public static final String TAG = "WallpaperInfoManager";
    private Context mContext;
    private WallpaperManager mWallpaperManager;
    private Point mRealWallpaperSize = new Point();
    private Point mDefaultWallpaperSize = new Point();
    private Point mDesiredWallpaperSize = new Point();
    private Point mWallpaperStartOffset = new Point();

    private enum SizeState {
        SM_WIDTH_SM_HEIGHT,
        SM_WIDTH_EQ_HEIGHT,
        SM_WIDTH_LA_HEIGHT,
        EQ_WIDTH_SM_HEIGHT,
        EQ_WIDTH_EQ_HEIGHT,
        EQ_WIDTH_LA_HEIGHT,
        LA_WIDTH_SM_HEIGHT,
        LA_WIDTH_EQ_HEIGHT,
        LA_WIDTH_LA_HEIGHT
    }

    public WallpaperInfoManager(Context context) {
        this.mContext = null;
        this.mWallpaperManager = null;
        this.mContext = context;
        this.mWallpaperManager = WallpaperManager.getInstance(context);
    }

    public Drawable getWallpaperDrawable() {
        Drawable drawable = this.mWallpaperManager.getDrawable();
        if (drawable == null) {
            LGLog.i(TAG, String.format("getWallpaperDrawable() : Wallpaper drawable is null", new Object[0]));
            return null;
        }
        this.mWallpaperManager.forgetLoadedWallpaper();
        updateWallpaperSizeInfo(drawable);
        return drawable;
    }

    private void updateWallpaperSizeInfo(Drawable wallpaperDrawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) wallpaperDrawable;
        this.mRealWallpaperSize.set(bitmapDrawable.getBitmap().getWidth(), bitmapDrawable.getBitmap().getHeight());
        this.mDefaultWallpaperSize = WallpaperUtils.getDefaultWallpaperSize(this.mContext.getResources(), WindowUtils.getWindowManager(this.mContext));
        this.mDesiredWallpaperSize.set(this.mWallpaperManager.getDesiredMinimumWidth(), this.mWallpaperManager.getDesiredMinimumHeight());
        Point displayRealSize = WindowUtils.getDisplayRealSize(this.mContext);
        String str = TAG;
        LGLog.i(str, String.format("mRealWallpaperSize    : %s", this.mRealWallpaperSize));
        LGLog.i(str, String.format("DisplaySize           : %s", displayRealSize));
        LGLog.i(str, String.format("mDefaultWallpaperSize : %s", this.mDefaultWallpaperSize));
        LGLog.i(str, String.format("mDesiredWallpaperSize : %s", this.mDesiredWallpaperSize));
        this.mWallpaperStartOffset.set((this.mRealWallpaperSize.x - displayRealSize.x) / 2, (this.mRealWallpaperSize.y - displayRealSize.y) / 2);
        LGLog.i(str, String.format("mWallpaperStartOffset : %s", displayRealSize));
        LGLog.i(str, String.format("RealWallpaperSizeRatio : %.3f (%.3f, %.3f)", Float.valueOf(getWallpaperScaledRatio()), Float.valueOf(getWallpaperWidthScaledRatio()), Float.valueOf(getWallpaperHeightScaledRatio())));
        LGLog.i(str, String.format("getWallpaperStartOffset : (%d, %d)", Integer.valueOf(getWallpaperStartOffsetX()), Integer.valueOf(getWallpaperStartOffsetY())));
    }

    public boolean isLiveWallpaperMode() {
        return this.mWallpaperManager.getWallpaperInfo() != null;
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0056  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int getWallpaperStartOffsetX() {
        /*
            r7 = this;
            float r0 = r7.getWallpaperScaledRatio()
            com.lge.launcher3.wallpaperblur.WallpaperInfoManager$SizeState r1 = r7.getWallpaperSizeState()
            android.content.Context r2 = r7.mContext
            boolean r2 = com.lge.launcher3.util.OrientationUtils.isPortrait(r2)
            r3 = 0
            if (r2 == 0) goto L57
            android.graphics.Point r2 = r7.mRealWallpaperSize
            int r2 = r2.x
            float r2 = (float) r2
            r4 = 1065353216(0x3f800000, float:1.0)
            float r4 = r4 - r0
            float r2 = r2 * r4
            r4 = 1056964608(0x3f000000, float:0.5)
            float r2 = r2 * r4
            int[] r5 = com.lge.launcher3.wallpaperblur.WallpaperInfoManager.AnonymousClass1.$SwitchMap$com$lge$launcher3$wallpaperblur$WallpaperInfoManager$SizeState
            int r1 = r1.ordinal()
            r1 = r5[r1]
            r5 = 1
            if (r1 == r5) goto L2f
            r0 = 2
            if (r1 == r0) goto L57
            r0 = 3
            if (r1 == r0) goto L57
            goto L56
        L2f:
            float r1 = r7.getWallpaperWidthScaledRatio()
            float r5 = r7.getWallpaperHeightScaledRatio()
            int r6 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r6 >= 0) goto L3c
            goto L57
        L3c:
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 != 0) goto L41
            goto L57
        L41:
            if (r1 <= 0) goto L56
            android.content.Context r1 = r7.mContext
            android.graphics.Point r1 = com.lge.launcher3.util.WindowUtils.getDisplayRealSize(r1)
            int r1 = r1.x
            android.graphics.Point r2 = r7.mRealWallpaperSize
            int r2 = r2.x
            float r2 = (float) r2
            float r1 = (float) r1
            float r1 = r1 * r0
            float r2 = r2 - r1
            float r3 = r2 * r4
            goto L57
        L56:
            r3 = r2
        L57:
            int r0 = (int) r3
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.wallpaperblur.WallpaperInfoManager.getWallpaperStartOffsetX():int");
    }

    /* JADX INFO: renamed from: com.lge.launcher3.wallpaperblur.WallpaperInfoManager$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$wallpaperblur$WallpaperInfoManager$SizeState;

        static {
            int[] iArr = new int[SizeState.values().length];
            $SwitchMap$com$lge$launcher3$wallpaperblur$WallpaperInfoManager$SizeState = iArr;
            try {
                iArr[SizeState.SM_WIDTH_SM_HEIGHT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$WallpaperInfoManager$SizeState[SizeState.SM_WIDTH_EQ_HEIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wallpaperblur$WallpaperInfoManager$SizeState[SizeState.SM_WIDTH_LA_HEIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public int getWallpaperStartOffsetY() {
        return (int) ((this.mRealWallpaperSize.y - (WindowUtils.getDisplayRealSize(this.mContext).y * getWallpaperScaledRatio())) * 0.5f);
    }

    public int getRealWallpaperMaxOffsetX() {
        int i = WindowUtils.getDisplayRealSize(this.mContext).x;
        if (this.mRealWallpaperSize.x >= i) {
            return this.mRealWallpaperSize.x - i;
        }
        return 0;
    }

    private SizeState getWallpaperSizeState() {
        int i = this.mRealWallpaperSize.x;
        int i2 = this.mRealWallpaperSize.y;
        Point displayRealSize = WindowUtils.getDisplayRealSize(this.mContext);
        int i3 = displayRealSize.x;
        int i4 = displayRealSize.y;
        if (i < i3) {
            if (i2 < i4) {
                return SizeState.SM_WIDTH_SM_HEIGHT;
            }
            if (i2 == i4) {
                return SizeState.SM_WIDTH_EQ_HEIGHT;
            }
            if (i2 > i4) {
                return SizeState.SM_WIDTH_LA_HEIGHT;
            }
        } else if (i == i3) {
            if (i2 < i4) {
                return SizeState.EQ_WIDTH_SM_HEIGHT;
            }
            if (i2 == i4) {
                return SizeState.EQ_WIDTH_EQ_HEIGHT;
            }
            if (i2 > i4) {
                return SizeState.EQ_WIDTH_LA_HEIGHT;
            }
        } else if (i > i3) {
            if (i2 < i4) {
                return SizeState.LA_WIDTH_SM_HEIGHT;
            }
            if (i2 == i4) {
                return SizeState.LA_WIDTH_EQ_HEIGHT;
            }
            if (i2 > i4) {
                return SizeState.LA_WIDTH_LA_HEIGHT;
            }
        }
        return null;
    }

    public float getWallpaperScaledRatio() {
        return Math.min(1.0f, Math.min(getWallpaperWidthScaledRatio(), getWallpaperHeightScaledRatio()));
    }

    private float getWallpaperWidthScaledRatio() {
        return this.mRealWallpaperSize.x / WindowUtils.getDisplayRealSize(this.mContext).x;
    }

    private float getWallpaperHeightScaledRatio() {
        return this.mRealWallpaperSize.y / WindowUtils.getDisplayRealSize(this.mContext).y;
    }

    public Point getRealWallpaperSize() {
        return this.mRealWallpaperSize;
    }

    public Point getWallpaperStartOffset() {
        return this.mWallpaperStartOffset;
    }
}
