package com.android.quickstep.interaction;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import androidx.fragment.app.FragmentActivity;
import com.android.quickstep.interaction.TutorialController;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class GestureSandboxActivity extends FragmentActivity {
    private static final String LOG_TAG = "GestureSandboxActivity";
    private TutorialFragment mFragment;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.gesture_tutorial_activity);
        this.mFragment = TutorialFragment.newInstance(getTutorialType(getIntent().getExtras()));
        getSupportFragmentManager().beginTransaction().add(R.id.gesture_tutorial_fragment_container, this.mFragment).commit();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        disableSystemGestures();
        this.mFragment.onAttachedToWindow();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFragment.onDetachedFromWindow();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private TutorialController.TutorialType getTutorialType(Bundle extras) {
        TutorialController.TutorialType tutorialType = TutorialController.TutorialType.RIGHT_EDGE_BACK_NAVIGATION;
        if (extras != null && extras.containsKey("tutorial_type")) {
            try {
                return TutorialController.TutorialType.valueOf(extras.getString("tutorial_type", ""));
            } catch (IllegalArgumentException unused) {
            }
        }
        return tutorialType;
    }

    private void hideSystemUI() {
        getWindow().setDecorFitsSystemWindows(false);
        WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.statusBars());
            insetsController.setSystemBarsBehavior(2);
        }
        getWindow().setNavigationBarColor(0);
    }

    private void disableSystemGestures() {
        Display display = getDisplay();
        if (display != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            getWindow().setSystemGestureExclusionRects(GestureSandboxActivity$$ExternalSynthetic1.m0(new Object[]{new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)}));
        }
    }
}
