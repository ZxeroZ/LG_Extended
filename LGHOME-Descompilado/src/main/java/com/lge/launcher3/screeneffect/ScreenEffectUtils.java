package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.content.pm.PackageManager;
import com.lge.launcher3.R;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.MathFunctionUtils;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectUtils {
    public static final ScreenEffectConst.ScreenEffectType getSelectedScreenEffectType(Context context) {
        return getScreenEffectType(context, getSelectedScreenEffectText(context));
    }

    public static final String getSelectedScreenEffectText(Context context) {
        return getScreenEffectList(context)[HomeSettingsSharedPreferences.getSelectedScreenEffect(context)];
    }

    public static final String getSelectedSecondScreenEffectText(Context context) {
        return getScreenEffectList(context)[HomeSettingsSharedPreferences.getSelectedSecondScreenEffect(context)];
    }

    public static final ScreenEffectConst.ScreenEffectType getScreenEffectType(Context context, int typeIndex) {
        return getScreenEffectType(context, getScreenEffectList(context)[typeIndex]);
    }

    public static final ScreenEffectConst.ScreenEffectType getScreenEffectType(Context context, String typeText) {
        for (ScreenEffectConst.ScreenEffectType screenEffectType : ScreenEffectConst.ScreenEffectType.values()) {
            if (screenEffectType.equals(context, typeText)) {
                return screenEffectType;
            }
        }
        return null;
    }

    public static final String[] getScreenEffectList(Context context) {
        Context contextCreatePackageContext;
        try {
            contextCreatePackageContext = context.createPackageContext("com.lge.launcher3", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            contextCreatePackageContext = null;
        }
        return contextCreatePackageContext.getResources().getStringArray(R.array.screen_effect);
    }

    public static final float getFixedChlidGap(ScreenEffectConst.WhichPageToDraw whichPageToDraw, float scrollProgress, float childWidth, float fixedWidthRatio) {
        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[whichPageToDraw.ordinal()];
        if (i == 1) {
            return childWidth * (-1.0f) * fixedWidthRatio * scrollProgress;
        }
        if (i != 2) {
            return 0.0f;
        }
        return (1.0f - scrollProgress) * childWidth * fixedWidthRatio;
    }

    public static final float getProgressiveChildGap(ScreenEffectConst.WhichPageToDraw whichPageToDraw, float scrollProgress, float childWidth, float fixedWidthRatio, float finalProgressiveWidthRatio, float exponent) {
        double dPow;
        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[whichPageToDraw.ordinal()];
        if (i != 1) {
            if (i != 2) {
                return 0.0f;
            }
            if (scrollProgress >= 0.5f) {
                dPow = Math.pow((1.0f - scrollProgress) * 2.0f, exponent);
            } else {
                dPow = Math.pow(2.0f * scrollProgress, exponent);
            }
        } else if (scrollProgress <= 0.5f) {
            dPow = Math.pow(2.0f * scrollProgress, exponent);
        } else {
            dPow = Math.pow((1.0f - scrollProgress) * 2.0f, exponent);
        }
        return getFixedChlidGap(whichPageToDraw, scrollProgress, childWidth, fixedWidthRatio + (finalProgressiveWidthRatio * ((float) dPow)));
    }

    /* JADX INFO: renamed from: com.lge.launcher3.screeneffect.ScreenEffectUtils$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScrollDirection;
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw;

        static {
            int[] iArr = new int[ScreenEffectConst.ScrollDirection.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScrollDirection = iArr;
            try {
                iArr[ScreenEffectConst.ScrollDirection.TO_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScrollDirection[ScreenEffectConst.ScrollDirection.TO_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScrollDirection[ScreenEffectConst.ScrollDirection.NONE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            int[] iArr2 = new int[ScreenEffectConst.WhichPageToDraw.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw = iArr2;
            try {
                iArr2[ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NONE.ordinal()] = 5;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    public static final int getProgressivePageAlpha(ScreenEffectConst.ScrollDirection scrollDirection, ScreenEffectConst.WhichPageToDraw whichPageToDraw, float scrollProgress, float startRatio, float finalRatio, int startAlpha, int finalAlpha) {
        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$ScrollDirection[scrollDirection.ordinal()];
        if (i != 1 && i != 2) {
            return startAlpha;
        }
        if (whichPageToDraw == ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT || whichPageToDraw == ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT) {
            scrollProgress = 1.0f - scrollProgress;
        }
        if (startRatio > finalRatio) {
            return startAlpha;
        }
        if (scrollProgress >= startRatio) {
            if (startRatio > scrollProgress || scrollProgress > finalRatio) {
                startAlpha = finalAlpha;
            } else {
                float fNormalize = MathFunctionUtils.normalize(scrollProgress, startRatio, finalRatio);
                startAlpha = ((int) (startAlpha * (1.0f - fNormalize))) + ((int) (finalAlpha * fNormalize));
            }
        }
        if (startAlpha > 0) {
            return startAlpha;
        }
        return 1;
    }
}
