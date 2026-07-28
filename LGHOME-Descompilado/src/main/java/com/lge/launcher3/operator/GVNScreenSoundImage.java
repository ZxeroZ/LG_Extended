package com.lge.launcher3.operator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.android.launcher3.Launcher;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class GVNScreenSoundImage {
    private static String TAG = "com.lge.launcher3.operator.GVNScreenSoundImage";
    private static GVNScreenSoundImage sInstance;
    private Context mContext;
    Animation.AnimationListener mPageMoveSoundImageAnimListener = new Animation.AnimationListener() { // from class: com.lge.launcher3.operator.GVNScreenSoundImage.1
        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
            int dimensionPixelOffset = GVNScreenSoundImage.this.mContext.getResources().getDimensionPixelOffset(R.dimen.gvn_screen_sound_translate_x);
            int dimensionPixelOffset2 = GVNScreenSoundImage.this.mContext.getResources().getDimensionPixelOffset(R.dimen.gvn_screen_sound_translate_y);
            GVNScreenSoundImage.this.mScreenSoundImageView.setTranslationX(dimensionPixelOffset);
            GVNScreenSoundImage.this.mScreenSoundImageView.setTranslationY(dimensionPixelOffset2);
            GVNScreenSoundImage.this.mScreenSoundImageView.setVisibility(0);
            GVNScreenSoundImage.this.mScreenSoundImageView.setAlpha(1.0f);
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            GVNScreenSoundImage.this.mScreenSoundImageView.setTranslationX(0.0f);
            GVNScreenSoundImage.this.mScreenSoundImageView.setTranslationY(0.0f);
            GVNScreenSoundImage.this.mScreenSoundImageView.setVisibility(8);
            GVNScreenSoundImage.this.mScreenSoundImageView.setAlpha(0.0f);
        }
    };
    Animation mScreenSoundImageAnim;
    FrameLayout mScreenSoundImageView;

    private GVNScreenSoundImage(Context context) {
        this.mContext = context;
        init();
    }

    public static GVNScreenSoundImage getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new GVNScreenSoundImage(context);
        }
        return sInstance;
    }

    private void init() {
        this.mScreenSoundImageView = (FrameLayout) LayoutInflater.from(this.mContext).inflate(R.layout.gvn_screen_sound_image_view, (ViewGroup) null);
        Animation animationLoadAnimation = AnimationUtils.loadAnimation(this.mContext, R.anim.gvn_show_screen_sound_image);
        this.mScreenSoundImageAnim = animationLoadAnimation;
        animationLoadAnimation.setAnimationListener(this.mPageMoveSoundImageAnimListener);
        addScreenSoundImageViewToDragLayer();
    }

    private void addScreenSoundImageViewToDragLayer() {
        Context context = this.mContext;
        if (context instanceof Launcher) {
            ((Launcher) context).getDragLayer().addView(this.mScreenSoundImageView);
        } else {
            LGLog.w(TAG, "Can not add the screen sound image view at DragLayer.", new int[0]);
        }
    }

    private void removeScreenSoundImageViewToDragLayer() {
        Context context = this.mContext;
        if ((context instanceof Launcher) && this.mScreenSoundImageView != null) {
            ((Launcher) context).getDragLayer().removeView(this.mScreenSoundImageView);
            this.mScreenSoundImageView = null;
        } else {
            LGLog.w(TAG, "Can not remove the screen sound image view at DragLayer.", new int[0]);
        }
    }

    public void startAnimation() {
        if (GVNScreenManager.getInstance(this.mContext).isEnableSoundImgEffect()) {
            this.mScreenSoundImageView.startAnimation(this.mScreenSoundImageAnim);
        }
    }

    public void destroy() {
        removeScreenSoundImageViewToDragLayer();
        sInstance = null;
    }
}
