package com.android.systemui.shared.rotation;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import androidx.core.view.OneShotPreDrawListener;
import com.android.systemui.shared.rotation.FloatingRotationButtonPositionCalculator;
import com.android.systemui.shared.rotation.RotationButton;

/* JADX INFO: loaded from: classes.dex */
public class FloatingRotationButton implements RotationButton {
    private static final int MARGIN_ANIMATION_DURATION_MILLIS = 300;
    private AnimatedVectorDrawable mAnimatedDrawable;
    private final int mButtonBottomMarginResource;
    private final int mButtonDiameterResource;
    private final int mButtonLeftMarginResource;
    private int mContainerXSize;
    private int mContainerYSize;
    private final int mContentDescriptionResource;
    private final Context mContext;
    private int mDisplayRotation;
    private boolean mIsShowing;
    private final ViewGroup mKeyButtonContainer;
    private final FloatingRotationButtonView mKeyButtonView;
    private FloatingRotationButtonPositionCalculator.Position mPosition;
    private FloatingRotationButtonPositionCalculator mPositionCalculator;
    private int mRotation;
    private RotationButtonController mRotationButtonController;
    private final int mRoundedContentPaddingResource;
    private final int mTaskbarBottomMarginResource;
    private final int mTaskbarLeftMarginResource;
    private RotationButton.RotationButtonUpdatesCallback mUpdatesCallback;
    private final WindowManager mWindowManager;
    private boolean mCanShow = true;
    private boolean mIsTaskbarVisible = false;
    private boolean mIsTaskbarStashed = false;

    @Override // com.android.systemui.shared.rotation.RotationButton
    public void updateIcon(int i, int i2) {
    }

    public FloatingRotationButton(Context context, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
        this.mWindowManager = (WindowManager) context.getSystemService(WindowManager.class);
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(i2, (ViewGroup) null);
        this.mKeyButtonContainer = viewGroup;
        FloatingRotationButtonView floatingRotationButtonView = (FloatingRotationButtonView) viewGroup.findViewById(i3);
        this.mKeyButtonView = floatingRotationButtonView;
        floatingRotationButtonView.setVisibility(0);
        floatingRotationButtonView.setContentDescription(context.getString(i));
        floatingRotationButtonView.setRipple(i10);
        this.mContext = context;
        this.mContentDescriptionResource = i;
        this.mButtonLeftMarginResource = i4;
        this.mButtonBottomMarginResource = i5;
        this.mRoundedContentPaddingResource = i6;
        this.mTaskbarLeftMarginResource = i7;
        this.mTaskbarBottomMarginResource = i8;
        this.mButtonDiameterResource = i9;
        updateDimensionResources();
    }

    private void updateDimensionResources() {
        Resources resources = this.mContext.getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(this.mButtonLeftMarginResource);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(this.mButtonBottomMarginResource);
        int dimensionPixelSize3 = resources.getDimensionPixelSize(this.mTaskbarLeftMarginResource);
        int dimensionPixelSize4 = resources.getDimensionPixelSize(this.mTaskbarBottomMarginResource);
        this.mPositionCalculator = new FloatingRotationButtonPositionCalculator(dimensionPixelSize, dimensionPixelSize2, dimensionPixelSize3, dimensionPixelSize4);
        int dimensionPixelSize5 = resources.getDimensionPixelSize(this.mButtonDiameterResource);
        this.mContainerXSize = Math.max(dimensionPixelSize, Math.max(dimensionPixelSize3, dimensionPixelSize4)) + dimensionPixelSize5;
        this.mContainerYSize = dimensionPixelSize5 + Math.max(dimensionPixelSize2, Math.max(dimensionPixelSize3, dimensionPixelSize4));
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public void setRotationButtonController(RotationButtonController rotationButtonController) {
        this.mRotationButtonController = rotationButtonController;
        updateIcon(rotationButtonController.getLightIconColor(), this.mRotationButtonController.getDarkIconColor());
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public void setUpdatesCallback(RotationButton.RotationButtonUpdatesCallback rotationButtonUpdatesCallback) {
        this.mUpdatesCallback = rotationButtonUpdatesCallback;
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public View getCurrentView() {
        return this.mKeyButtonView;
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public boolean show() {
        if (!this.mCanShow || this.mIsShowing) {
            return false;
        }
        this.mIsShowing = true;
        this.mWindowManager.addView(this.mKeyButtonContainer, adjustViewPositionAndCreateLayoutParams());
        AnimatedVectorDrawable animatedVectorDrawable = this.mAnimatedDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.reset();
            this.mAnimatedDrawable.start();
        }
        OneShotPreDrawListener.add(this.mKeyButtonView, new Runnable() { // from class: com.android.systemui.shared.rotation.-$$Lambda$FloatingRotationButton$ru2bO1n_Sw-5453EhPqos_J4PT8
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$show$0$FloatingRotationButton();
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$show$0$FloatingRotationButton() {
        RotationButton.RotationButtonUpdatesCallback rotationButtonUpdatesCallback;
        if (!this.mIsShowing || (rotationButtonUpdatesCallback = this.mUpdatesCallback) == null) {
            return;
        }
        rotationButtonUpdatesCallback.onVisibilityChanged(true);
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public boolean hide() {
        if (!this.mIsShowing) {
            return false;
        }
        this.mWindowManager.removeViewImmediate(this.mKeyButtonContainer);
        this.mIsShowing = false;
        RotationButton.RotationButtonUpdatesCallback rotationButtonUpdatesCallback = this.mUpdatesCallback;
        if (rotationButtonUpdatesCallback == null) {
            return true;
        }
        rotationButtonUpdatesCallback.onVisibilityChanged(false);
        return true;
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public boolean isVisible() {
        return this.mIsShowing;
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mKeyButtonView.setOnClickListener(onClickListener);
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public void setOnHoverListener(View.OnHoverListener onHoverListener) {
        this.mKeyButtonView.setOnHoverListener(onHoverListener);
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public void setDarkIntensity(float f) {
        this.mKeyButtonView.setDarkIntensity(f);
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        this.mKeyButtonView.setOnTouchListener(onTouchListener);
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public void setCanShowRotationButton(boolean z) {
        this.mCanShow = z;
        if (z) {
            return;
        }
        hide();
    }

    @Override // com.android.systemui.shared.rotation.RotationButton
    public void onTaskbarStateChanged(boolean z, boolean z2) {
        this.mIsTaskbarVisible = z;
        this.mIsTaskbarStashed = z2;
        if (this.mIsShowing) {
            FloatingRotationButtonPositionCalculator.Position positionCalculatePosition = this.mPositionCalculator.calculatePosition(this.mRotation, this.mDisplayRotation, z, z2);
            if (positionCalculatePosition.getTranslationX() == this.mPosition.getTranslationX() && positionCalculatePosition.getTranslationY() == this.mPosition.getTranslationY()) {
                return;
            }
            updateTranslation(positionCalculatePosition, true);
            this.mPosition = positionCalculatePosition;
        }
    }

    public void onConfigurationChanged(int i) {
        if ((i & 4096) != 0 || (i & 1024) != 0) {
            updateDimensionResources();
            if (this.mIsShowing) {
                this.mWindowManager.updateViewLayout(this.mKeyButtonContainer, adjustViewPositionAndCreateLayoutParams());
            }
        }
        if ((i & 4) != 0) {
            this.mKeyButtonView.setContentDescription(this.mContext.getString(this.mContentDescriptionResource));
        }
    }

    private WindowManager.LayoutParams adjustViewPositionAndCreateLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(this.mContainerXSize, this.mContainerYSize, 0, this.mContext.getResources().getDimensionPixelSize(R.dimen.indeterminate_progress_alpha_25), 2024, 8, -3);
        layoutParams.privateFlags |= 16;
        layoutParams.setTitle("FloatingRotationButton");
        layoutParams.setFitInsetsTypes(0);
        int rotation = this.mWindowManager.getDefaultDisplay().getRotation();
        this.mDisplayRotation = rotation;
        FloatingRotationButtonPositionCalculator.Position positionCalculatePosition = this.mPositionCalculator.calculatePosition(this.mRotation, rotation, this.mIsTaskbarVisible, this.mIsTaskbarStashed);
        this.mPosition = positionCalculatePosition;
        layoutParams.gravity = positionCalculatePosition.getGravity();
        ((FrameLayout.LayoutParams) this.mKeyButtonView.getLayoutParams()).gravity = this.mPosition.getGravity();
        updateTranslation(this.mPosition, false);
        return layoutParams;
    }

    private void updateTranslation(FloatingRotationButtonPositionCalculator.Position position, boolean z) {
        int translationX = position.getTranslationX();
        int translationY = position.getTranslationY();
        if (z) {
            this.mKeyButtonView.animate().translationX(translationX).translationY(translationY).setDuration(300L).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() { // from class: com.android.systemui.shared.rotation.-$$Lambda$FloatingRotationButton$KlNtkQsfU8KBz67x5m2ktsr8jd0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$updateTranslation$1$FloatingRotationButton();
                }
            }).start();
        } else {
            this.mKeyButtonView.setTranslationX(translationX);
            this.mKeyButtonView.setTranslationY(translationY);
        }
    }

    public /* synthetic */ void lambda$updateTranslation$1$FloatingRotationButton() {
        RotationButton.RotationButtonUpdatesCallback rotationButtonUpdatesCallback = this.mUpdatesCallback;
        if (rotationButtonUpdatesCallback == null || !this.mIsShowing) {
            return;
        }
        rotationButtonUpdatesCallback.onPositionChanged();
    }

    public void onRotationProposal(int i, int i2) {
        this.mRotation = i;
        this.mDisplayRotation = i2;
        this.mPosition = this.mPositionCalculator.calculatePosition(i, i2, this.mIsTaskbarVisible, this.mIsTaskbarStashed);
    }
}
