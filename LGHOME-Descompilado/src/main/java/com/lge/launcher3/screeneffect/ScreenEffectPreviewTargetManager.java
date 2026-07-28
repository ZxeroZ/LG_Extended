package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.view.View;
import com.lge.launcher3.screeneffect.ScreenEffectConst;
import com.lge.launcher3.screeneffect.ScreenEffectTargetManager;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectPreviewTargetManager extends ScreenEffectTargetManager {
    public static final String TAG = "ScreenEffectPreviewTargetManager";
    private static ScreenEffectPreviewTargetManager sInstance;

    public static ScreenEffectPreviewTargetManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ScreenEffectPreviewTargetManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public ScreenEffectPreviewTargetManager(Context context) {
        super(context);
    }

    public void updateTargetInfo(ScreenEffectConst.WhichPageToDraw whichPageToDraw) {
        this.mTargetInfo.scrollDirection = ScreenEffectConst.ScrollDirection.TO_RIGHT;
        this.mTargetInfo.overscrollState = ScreenEffectConst.OverscrollState.NONE;
        this.mTargetInfo.whichPageToDraw = whichPageToDraw;
        this.mTargetInfo.fixedOverscrollState = ScreenEffectConst.FixedOverscrollState.NONE;
    }

    @Override // com.lge.launcher3.screeneffect.ScreenEffectTargetManager
    public ScreenEffectTargetManager.TargetInfo getTargetInfo(View child) {
        return this.mTargetInfo;
    }

    @Override // com.lge.launcher3.screeneffect.ScreenEffectTargetManager
    public void setScrollX(int scrollX) {
        this.mTargetInfo.scrollX = scrollX;
        int measuredWidth = getChild() != null ? getChild().getMeasuredWidth() : 0;
        if (measuredWidth <= 0) {
            this.mTargetInfo.scrollProgress = 0.0f;
        } else {
            this.mTargetInfo.scrollProgress = scrollX / measuredWidth;
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.screeneffect.ScreenEffectPreviewTargetManager$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw;

        static {
            int[] iArr = new int[ScreenEffectConst.WhichPageToDraw.values().length];
            $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw = iArr;
            try {
                iArr[ScreenEffectConst.WhichPageToDraw.NORMAL_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NORMAL_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.FIXED_OVERSCROLL_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[ScreenEffectConst.WhichPageToDraw.NONE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public int getScrollX() {
        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$screeneffect$ScreenEffectConst$WhichPageToDraw[this.mTargetInfo.whichPageToDraw.ordinal()];
        if (i == 1) {
            return this.mTargetInfo.scrollX * (-1);
        }
        if (i != 2) {
            return 0;
        }
        return (getChild() != null ? getChild().getMeasuredWidth() : 0) + (this.mTargetInfo.scrollX * (-1));
    }

    public View getChild() {
        if (this.mParent != null) {
            return this.mParent.getChildAt(0);
        }
        return null;
    }
}
