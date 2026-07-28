package com.android.quickstep.interaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.quickstep.interaction.TutorialController;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class AssistantGestureTutorialFragment extends TutorialFragment {
    @Override // com.android.quickstep.interaction.TutorialFragment
    int getHandAnimationResId() {
        return R.drawable.assistant_gesture;
    }

    @Override // com.android.quickstep.interaction.TutorialFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // com.android.quickstep.interaction.TutorialFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // com.android.quickstep.interaction.TutorialFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onDestroy() {
        super.onDestroy();
    }

    @Override // com.android.quickstep.interaction.TutorialFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onPause() {
        super.onPause();
    }

    @Override // com.android.quickstep.interaction.TutorialFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onResume() {
        super.onResume();
    }

    @Override // com.android.quickstep.interaction.TutorialFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override // com.android.quickstep.interaction.TutorialFragment
    TutorialController createController(TutorialController.TutorialType type) {
        return new AssistantGestureTutorialController(this, type);
    }

    @Override // com.android.quickstep.interaction.TutorialFragment
    Class<? extends TutorialController> getControllerClass() {
        return AssistantGestureTutorialController.class;
    }

    @Override // com.android.quickstep.interaction.TutorialFragment, android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && this.mTutorialController != null) {
            this.mTutorialController.setRippleHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return super.onTouch(view, motionEvent);
    }
}
