package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.util.SparseArray;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AnimatorSetBuilder {
    public static final int ANIM_ALL_APPS_FADE = 10;
    public static final int ANIM_HOTSEAT_SCALE = 4;
    public static final int ANIM_HOTSEAT_TRANSLATE = 5;
    public static final int ANIM_OVERVIEW_FADE = 9;
    public static final int ANIM_OVERVIEW_SCALE = 6;
    public static final int ANIM_OVERVIEW_TRANSLATE_X = 7;
    public static final int ANIM_OVERVIEW_TRANSLATE_Y = 8;
    public static final int ANIM_VERTICAL_PROGRESS = 0;
    public static final int ANIM_WORKSPACE_FADE = 3;
    public static final int ANIM_WORKSPACE_SCALE = 1;
    public static final int ANIM_WORKSPACE_TRANSLATE = 2;
    public static final int FLAG_DONT_ANIMATE_OVERVIEW = 1;
    protected final ArrayList<Animator> mAnims = new ArrayList<>();
    private final SparseArray<Interpolator> mInterpolators = new SparseArray<>();
    private List<Runnable> mOnFinishRunnables = new ArrayList();
    private int mFlags = 0;

    public void play(Animator anim) {
        this.mAnims.add(anim);
    }

    public void addOnFinishRunnable(Runnable onFinishRunnable) {
        this.mOnFinishRunnables.add(onFinishRunnable);
    }

    public AnimatorSet build() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(this.mAnims);
        if (!this.mOnFinishRunnables.isEmpty()) {
            animatorSet.addListener(new AnimationSuccessListener() { // from class: com.android.launcher3.anim.AnimatorSetBuilder.1
                @Override // com.android.launcher3.anim.AnimationSuccessListener
                public void onAnimationSuccess(Animator animation) {
                    Iterator it = AnimatorSetBuilder.this.mOnFinishRunnables.iterator();
                    while (it.hasNext()) {
                        ((Runnable) it.next()).run();
                    }
                    AnimatorSetBuilder.this.mOnFinishRunnables.clear();
                }
            });
        }
        return animatorSet;
    }

    public Interpolator getInterpolator(int animId, Interpolator fallback) {
        return this.mInterpolators.get(animId, fallback);
    }

    public void setInterpolator(int animId, Interpolator interpolator) {
        this.mInterpolators.put(animId, interpolator);
    }

    public void addFlag(int flag) {
        this.mFlags = flag | this.mFlags;
    }

    public boolean hasFlag(int flag) {
        return (flag & this.mFlags) != 0;
    }
}
