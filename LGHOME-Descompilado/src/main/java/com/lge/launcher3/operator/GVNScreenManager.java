package com.lge.launcher3.operator;

import android.content.Context;
import com.lge.launcher3.util.LGLog;
import java.security.SecureRandom;

/* JADX INFO: loaded from: classes.dex */
public class GVNScreenManager {
    private static final int DRAW_TURN_TRANSI_EFFECT = 30;
    private static String TAG = "com.lge.launcher3.operator.GVNScreenManager";
    public static final int TRANSI_EFFECT_RANDOM_NUM = 3;
    private static GVNScreenManager sInstance;
    private Context mContext;
    int mTransiAniEffectImgIdx;
    int mBeginMovingPageNum = 0;
    int mStopMovingPageNum = 0;
    boolean mEnableTransiAniEffect = false;
    int mMovedPageCnt = 1;

    public void destroy() {
    }

    private GVNScreenManager(Context context) {
        this.mContext = context;
    }

    public static GVNScreenManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new GVNScreenManager(context);
        }
        return sInstance;
    }

    public void setBeginMovingPageNum(int screenNum) {
        this.mBeginMovingPageNum = screenNum;
    }

    public void setStopMovingPageNum(int screenNum) {
        this.mStopMovingPageNum = screenNum;
    }

    private boolean isPageChanged() {
        return this.mBeginMovingPageNum != this.mStopMovingPageNum;
    }

    public void addMovedPageCnt() {
        if (isPageChanged()) {
            int i = this.mMovedPageCnt + 1;
            this.mMovedPageCnt = i;
            if (i == 30) {
                LGLog.d(TAG, "[GVN] Moved Page Cnt [" + i + "]");
                turnOnTransiAniEffect();
                return;
            }
            turnOffTransiAniEffect();
        }
    }

    private void turnOnTransiAniEffect() {
        LGLog.i(TAG, "[GVN] TransiAnimationEffect [" + this.mEnableTransiAniEffect + "]");
        this.mTransiAniEffectImgIdx = new SecureRandom().nextInt(3);
        this.mEnableTransiAniEffect = true;
        this.mMovedPageCnt = 0;
    }

    private void turnOffTransiAniEffect() {
        this.mEnableTransiAniEffect = false;
    }

    public boolean isEnableTransiAniEffect() {
        return GVNUtils.isGVNScreenEffectOn(this.mContext) && this.mEnableTransiAniEffect;
    }

    public boolean isEnableSoundImgEffect() {
        return GVNUtils.isGVNScreenEffectOn(this.mContext) && GVNUtils.isDisclosureEffectsEnabled(this.mContext) && isPageChanged();
    }

    public int getTransiAniEffectImgIdx() {
        return this.mTransiAniEffectImgIdx;
    }
}
