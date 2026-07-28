package com.android.launcher3.shortcuts;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LogAccelerateInterpolator;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.util.PillRevealOutlineProvider;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class DeepShortcutView extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    private static final Point sTempPoint = new Point();
    private BubbleTextView mBubbleText;
    private ShortcutInfoCompat mDetail;
    private View mIconView;
    private ShortcutInfo mInfo;
    private float mOpenAnimationProgress;
    private final Rect mPillRect;

    public DeepShortcutView(Context context) {
        this(context, null, 0);
    }

    public DeepShortcutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeepShortcutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPillRect = new Rect();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mBubbleText = (BubbleTextView) findViewById(R.id.bubble_text);
        this.mIconView = findViewById(R.id.icon);
    }

    public BubbleTextView getBubbleText() {
        return this.mBubbleText;
    }

    public void setWillDrawIcon(boolean willDraw) {
        this.mIconView.setVisibility(willDraw ? 0 : 4);
    }

    public boolean willDrawIcon() {
        return this.mIconView.getVisibility() == 0;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mPillRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void applyShortcutInfo(ShortcutInfo info, ShortcutInfoCompat detail, PopupContainerWithArrow container) {
        this.mInfo = info;
        this.mDetail = detail;
        this.mBubbleText.applyFromShortcutInfo(info, LauncherAppState.getInstance(getContext()).getIconCache());
        this.mIconView.setBackground(this.mBubbleText.getIcon());
        CharSequence longLabel = this.mDetail.getLongLabel();
        boolean z = !TextUtils.isEmpty(longLabel) && this.mBubbleText.getPaint().measureText(longLabel.toString()) <= ((float) ((this.mBubbleText.getWidth() - this.mBubbleText.getTotalPaddingLeft()) - this.mBubbleText.getTotalPaddingRight()));
        BubbleTextView bubbleTextView = this.mBubbleText;
        if (!z) {
            longLabel = this.mDetail.getShortLabel();
        }
        bubbleTextView.setText(longLabel);
        this.mBubbleText.setTextColor(getContext().getResources().getColor(R.color.deep_shortcut_text_color));
        this.mBubbleText.setOnClickListener(container.getItemClickListener());
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            this.mBubbleText.setOnLongClickListener(container.getItemDragHandler());
        }
        this.mBubbleText.setOnTouchListener(container.getItemDragHandler());
    }

    public ShortcutInfo getFinalInfo() {
        ShortcutInfo shortcutInfo = new ShortcutInfo(this.mInfo);
        Launcher.getLauncher(getContext()).getModel().updateShortcutInfo(getContext(), this.mDetail, shortcutInfo);
        return shortcutInfo;
    }

    public View getIconView() {
        return this.mIconView;
    }

    public Animator createOpenAnimation(boolean isContainerAboveIcon, boolean pivotLeft) {
        Point iconCenter = getIconCenter();
        ValueAnimator valueAnimatorCreateRevealAnimator = new ZoomRevealOutlineProvider(iconCenter.x, iconCenter.y, this.mPillRect, this, this.mIconView, isContainerAboveIcon, pivotLeft).createRevealAnimator(this, false);
        this.mOpenAnimationProgress = 0.0f;
        valueAnimatorCreateRevealAnimator.addUpdateListener(this);
        return valueAnimatorCreateRevealAnimator;
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.mOpenAnimationProgress = valueAnimator.getAnimatedFraction();
    }

    public boolean isOpenOrOpening() {
        return this.mOpenAnimationProgress > 0.0f;
    }

    public Animator createCloseAnimation(boolean isContainerAboveIcon, boolean pivotLeft, long duration) {
        Point iconCenter = getIconCenter();
        ValueAnimator valueAnimatorCreateRevealAnimator = new ZoomRevealOutlineProvider(iconCenter.x, iconCenter.y, this.mPillRect, this, this.mIconView, isContainerAboveIcon, pivotLeft).createRevealAnimator(this, true);
        valueAnimatorCreateRevealAnimator.setDuration((long) (duration * this.mOpenAnimationProgress));
        valueAnimatorCreateRevealAnimator.setInterpolator(new CloseInterpolator(this.mOpenAnimationProgress));
        return valueAnimatorCreateRevealAnimator;
    }

    public Point getIconCenter() {
        Point point = sTempPoint;
        int measuredHeight = getMeasuredHeight() / 2;
        point.x = measuredHeight;
        point.y = measuredHeight;
        if (Utilities.isRtl(getResources())) {
            point.x = getMeasuredWidth() - point.x;
        }
        return point;
    }

    private static class ZoomRevealOutlineProvider extends PillRevealOutlineProvider {
        private final float mFullHeight;
        private final boolean mPivotLeft;
        private final View mTranslateView;
        private final float mTranslateX;
        private final float mTranslateYMultiplier;
        private final View mZoomView;

        public ZoomRevealOutlineProvider(int x, int y, Rect pillRect, View translateView, View zoomView, boolean isContainerAboveIcon, boolean pivotLeft) {
            super(x, y, pillRect, 0.0f);
            this.mTranslateView = translateView;
            this.mZoomView = zoomView;
            this.mFullHeight = pillRect.height();
            this.mTranslateYMultiplier = isContainerAboveIcon ? 0.5f : -0.5f;
            this.mPivotLeft = pivotLeft;
            this.mTranslateX = pivotLeft ? pillRect.height() / 2 : pillRect.right - (pillRect.height() / 2);
        }

        @Override // com.android.launcher3.util.PillRevealOutlineProvider
        public void setProgress(float progress) {
            super.setProgress(progress);
            this.mZoomView.setScaleX(progress);
            this.mZoomView.setScaleY(progress);
            float fHeight = this.mOutline.height();
            this.mTranslateView.setTranslationY(this.mTranslateYMultiplier * (this.mFullHeight - fHeight));
            this.mTranslateView.setTranslationX(this.mTranslateX - (this.mPivotLeft ? this.mOutline.left + (fHeight / 2.0f) : this.mOutline.right - (fHeight / 2.0f)));
        }
    }

    private static class CloseInterpolator extends LogAccelerateInterpolator {
        private float mRemainingProgress;
        private float mStartProgress;

        public CloseInterpolator(float openAnimationProgress) {
            super(100, 0);
            this.mStartProgress = 1.0f - openAnimationProgress;
            this.mRemainingProgress = openAnimationProgress;
        }

        @Override // com.android.launcher3.LogAccelerateInterpolator, android.animation.TimeInterpolator
        public float getInterpolation(float v) {
            return this.mStartProgress + (super.getInterpolation(v) * this.mRemainingProgress);
        }
    }
}
