package com.android.launcher3.states;

import android.view.animation.Interpolator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public class StateAnimationConfig {
    public static final int ANIM_ALL_APPS_FADE = 10;
    public static final int ANIM_ALL_APPS_HEADER_FADE = 12;
    public static final int ANIM_ALL_APPS_SCALE = 15;
    public static final int ANIM_ALL_COMPONENTS = 7;
    public static final int ANIM_DEPTH = 14;
    public static final int ANIM_HOTSEAT_SCALE = 4;
    public static final int ANIM_HOTSEAT_TRANSLATE = 5;
    public static final int ANIM_OVERVIEW_FADE = 9;
    public static final int ANIM_OVERVIEW_MODAL = 13;
    public static final int ANIM_OVERVIEW_SCALE = 6;
    public static final int ANIM_OVERVIEW_SCRIM_FADE = 11;
    public static final int ANIM_OVERVIEW_TRANSLATE_X = 7;
    public static final int ANIM_OVERVIEW_TRANSLATE_Y = 8;
    private static final int ANIM_TYPES_COUNT = 16;
    public static final int ANIM_VERTICAL_PROGRESS = 0;
    public static final int ANIM_WORKSPACE_FADE = 3;
    public static final int ANIM_WORKSPACE_SCALE = 1;
    public static final int ANIM_WORKSPACE_TRANSLATE = 2;
    public static final int PLAY_ATOMIC_OVERVIEW_PEEK = 4;
    public static final int PLAY_ATOMIC_OVERVIEW_SCALE = 2;
    public static final int PLAY_NON_ATOMIC = 1;
    public static final int SKIP_DEPTH_CONTROLLER = 16;
    public static final int SKIP_OVERVIEW = 8;
    public long duration;
    public boolean userControlled;
    public int animFlags = 7;
    private final Interpolator[] mInterpolators = new Interpolator[16];

    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationFlags {
    }

    public void copyTo(StateAnimationConfig target) {
        target.duration = this.duration;
        target.animFlags = this.animFlags;
        target.userControlled = this.userControlled;
        for (int i = 0; i < 16; i++) {
            target.mInterpolators[i] = this.mInterpolators[i];
        }
    }

    public Interpolator getInterpolator(int animId, Interpolator fallback) {
        Interpolator[] interpolatorArr = this.mInterpolators;
        return interpolatorArr[animId] == null ? fallback : interpolatorArr[animId];
    }

    public void setInterpolator(int animId, Interpolator interpolator) {
        this.mInterpolators[animId] = interpolator;
    }

    public boolean playAtomicOverviewScaleComponent() {
        return hasAnimationFlag(2);
    }

    public boolean onlyPlayAtomicComponent() {
        return getAnimComponents() == 2 || getAnimComponents() == 4;
    }

    public boolean hasAnimationFlag(int a) {
        return (a & this.animFlags) != 0;
    }

    public int getAnimComponents() {
        return this.animFlags & 7;
    }
}
