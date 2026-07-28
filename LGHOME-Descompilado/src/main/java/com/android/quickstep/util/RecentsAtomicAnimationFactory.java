package com.android.quickstep.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import com.android.launcher3.anim.SpringAnimationBuilder;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.quickstep.views.RecentsView;

/* JADX INFO: loaded from: classes.dex */
public class RecentsAtomicAnimationFactory<ACTIVITY_TYPE extends StatefulActivity, STATE_TYPE> extends StateManager.AtomicAnimationFactory<STATE_TYPE> {
    public static final int INDEX_RECENTS_FADE_ANIM = 0;
    public static final int INDEX_RECENTS_TRANSLATE_X_ANIM = 1;
    private static final int MY_ANIM_COUNT = 2;
    protected static final int NEXT_INDEX = 2;
    protected final ACTIVITY_TYPE mActivity;

    public RecentsAtomicAnimationFactory(ACTIVITY_TYPE activity, int extraAnims) {
        super(extraAnims + 2);
        this.mActivity = activity;
    }

    @Override // com.android.launcher3.statemanager.StateManager.AtomicAnimationFactory
    public Animator createStateElementAnimation(int index, float... values) {
        if (index == 0) {
            return ObjectAnimator.ofFloat((RecentsView) this.mActivity.getOverviewPanel(), RecentsView.CONTENT_ALPHA, values);
        }
        if (index == 1) {
            RecentsView recentsView = (RecentsView) this.mActivity.getOverviewPanel();
            return new SpringAnimationBuilder(this.mActivity).setMinimumVisibleChange(1.0f / recentsView.getPageOffsetScale()).setDampingRatio(0.8f).setStiffness(250.0f).setValues(values).build(recentsView, RecentsView.ADJACENT_PAGE_OFFSET);
        }
        return super.createStateElementAnimation(index, values);
    }
}
