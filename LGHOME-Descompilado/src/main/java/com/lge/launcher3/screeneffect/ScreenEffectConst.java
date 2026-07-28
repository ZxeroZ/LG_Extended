package com.lge.launcher3.screeneffect;

import android.content.Context;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectConst {
    public static final int LAST_PAGE_OVERSCROLL_SNAP_ANIMATION_DURATION = 500;
    public static final int MIN_SNAP_DURATION = 300;
    public static final int MIN_SNAP_VELOCITY = 1000;
    public static final float OVERSCROLL_DAMP_FACTOR = 0.07f;
    public static final float OVERSCROLL_DAMP_FACTOR_FOR_SLIDE_ALPHA_EFFECT = 0.21f;
    public static final int PAGE_OVERSCROLL_SNAP_ANIMATION_DURATION = 800;

    public enum DrawState {
        NORMAL_SCREEN_EFFECT,
        OVERSCROLL_SCREEN_EFFECT,
        VIEW_SELF,
        SKIP
    }

    public enum FixedOverscrollState {
        INNER,
        OUTER,
        NONE
    }

    public enum OverscrollState {
        OVERSCROLL_LEFT,
        NONE,
        OVERSCROLL_RIGHT
    }

    public enum ScrollDirection {
        TO_LEFT,
        TO_RIGHT,
        NONE
    }

    public enum WhichPageToDraw {
        FIXED_OVERSCROLL_LEFT,
        NORMAL_LEFT,
        NORMAL_RIGHT,
        FIXED_OVERSCROLL_RIGHT,
        NONE
    }

    public enum ScreenEffectType {
        SLIDE(R.string.menu_screen_effect_basic),
        BREEZE(R.string.menu_screen_effect_breeze),
        PANORAMA(R.string.menu_screen_effect_panorama),
        CAROUSEL(R.string.menu_screen_effect_carousel);

        private int mResId;

        ScreenEffectType(int resId) {
            this.mResId = -1;
            this.mResId = resId;
        }

        public boolean equals(Context context, String other) {
            return context.getResources().getString(this.mResId).equals(other);
        }
    }
}
