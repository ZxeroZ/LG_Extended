package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.view.MotionEvent;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.util.MotionPauseDetector;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class ScreenPinnedInputConsumer implements InputConsumer {
    private static final String TAG = "ScreenPinnedConsumer";
    private final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMinDisplacement;
    private float mTouchDownY;

    @Override // com.android.quickstep.InputConsumer
    public int getType() {
        return 64;
    }

    public ScreenPinnedInputConsumer(final Context context, final GestureState gestureState) {
        this.mMotionPauseMinDisplacement = context.getResources().getDimension(R.dimen.motion_pause_detector_min_displacement_from_app);
        MotionPauseDetector motionPauseDetector = new MotionPauseDetector(context, true);
        this.mMotionPauseDetector = motionPauseDetector;
        motionPauseDetector.setOnMotionPauseListener(new MotionPauseDetector.OnMotionPauseListener() { // from class: com.android.quickstep.inputconsumers.-$$Lambda$ScreenPinnedInputConsumer$VTIt9Vs3NhQ0GXDj_JLaJArmg5k
            @Override // com.android.quickstep.util.MotionPauseDetector.OnMotionPauseListener
            public final void onMotionPauseChanged(boolean z) {
                this.f$0.lambda$new$0$ScreenPinnedInputConsumer(context, gestureState, z);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$ScreenPinnedInputConsumer(Context context, GestureState gestureState, boolean z) {
        if (z) {
            SystemUiProxy.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).stopScreenPinning();
            StatefulActivity createdActivity = gestureState.getActivityInterface().getCreatedActivity();
            if (createdActivity != null) {
                createdActivity.getRootView().performHapticFeedback(0, 1);
            }
            this.mMotionPauseDetector.clear();
        }
    }

    @Override // com.android.quickstep.InputConsumer
    public void onMotionEvent(MotionEvent ev) {
        float y = ev.getY();
        int action = ev.getAction();
        if (action == 0) {
            this.mTouchDownY = y;
            return;
        }
        if (action != 1) {
            if (action == 2) {
                this.mMotionPauseDetector.setDisallowPause(this.mTouchDownY - y < this.mMotionPauseMinDisplacement);
                this.mMotionPauseDetector.addPosition(ev);
                return;
            } else if (action != 3) {
                return;
            }
        }
        this.mMotionPauseDetector.clear();
    }
}
