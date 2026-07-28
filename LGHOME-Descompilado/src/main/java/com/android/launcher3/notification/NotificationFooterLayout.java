package com.android.launcher3.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.anim.PropertyResetListener;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class NotificationFooterLayout extends FrameLayout {
    private static final int MAX_FOOTER_NOTIFICATIONS = 5;
    private static final String TAG = "NotificationFooterLayout";
    private static final Rect sTempRect = new Rect();
    private int mBackgroundColor;
    FrameLayout.LayoutParams mIconLayoutParams;
    private LinearLayout mIconRow;
    private final List<NotificationInfo> mNotifications;
    private View mOverflowEllipsis;
    private final List<NotificationInfo> mOverflowNotifications;
    private final boolean mRtl;

    public interface IconAnimationEndListener {
        void onIconAnimationEnd(NotificationInfo animatedNotification);
    }

    public NotificationFooterLayout(Context context) {
        this(context, null, 0);
    }

    public NotificationFooterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationFooterLayout(Context context, AttributeSet attrs, int defStyle) {
        int dimensionPixelSize;
        int dimensionPixelSize2;
        super(context, attrs, defStyle);
        this.mNotifications = new ArrayList();
        this.mOverflowNotifications = new ArrayList();
        Resources resources = getResources();
        this.mRtl = Utilities.isRtl(resources);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.notification_footer_icon_size_swivel);
        } else if (com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.notification_footer_icon_size_ux10_0);
        } else {
            dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.notification_footer_icon_size);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize);
        this.mIconLayoutParams = layoutParams;
        layoutParams.gravity = 16;
        int dimensionPixelSize3 = resources.getDimensionPixelSize(R.dimen.notification_footer_icon_row_padding);
        int dimensionPixelSize4 = resources.getDimensionPixelSize(R.dimen.horizontal_ellipsis_offset) + resources.getDimensionPixelSize(R.dimen.horizontal_ellipsis_size);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.bg_popup_item_width_swivel);
        } else if (com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.bg_popup_item_width_ux10_0);
        } else {
            dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.bg_popup_item_width);
        }
        int i = ((dimensionPixelSize2 - dimensionPixelSize3) - dimensionPixelSize4) - (dimensionPixelSize * 5);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            this.mIconLayoutParams.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.notification_footer_icon_margin_end));
        } else {
            this.mIconLayoutParams.setMarginStart(i / 5);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mOverflowEllipsis = findViewById(R.id.overflow);
        this.mIconRow = (LinearLayout) findViewById(R.id.icon_row);
        this.mBackgroundColor = ((ColorDrawable) getBackground()).getColor();
    }

    public void addNotificationInfo(final NotificationInfo notificationInfo) {
        if (this.mNotifications.size() < 5) {
            this.mNotifications.add(notificationInfo);
        } else {
            this.mOverflowNotifications.add(notificationInfo);
        }
    }

    public void commitNotificationInfos() {
        this.mIconRow.removeAllViews();
        for (int i = 0; i < this.mNotifications.size(); i++) {
            addNotificationIconForInfo(this.mNotifications.get(i));
        }
        updateOverflowEllipsisVisibility();
    }

    private void updateOverflowEllipsisVisibility() {
        this.mOverflowEllipsis.setVisibility(this.mOverflowNotifications.isEmpty() ? 8 : 0);
    }

    private View addNotificationIconForInfo(NotificationInfo info) {
        View view = new View(getContext());
        view.setBackground(info.getIconForBackground(getContext(), this.mBackgroundColor));
        view.setOnClickListener(info);
        view.setTag(info);
        view.setImportantForAccessibility(2);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            this.mIconRow.addView(view, this.mIconLayoutParams);
        } else {
            this.mIconRow.addView(view, 0, this.mIconLayoutParams);
        }
        return view;
    }

    public void animateFirstNotificationTo(Rect toBounds, final IconAnimationEndListener callback) {
        final View childAt;
        int marginStart;
        AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            childAt = this.mIconRow.getChildAt(0);
        } else {
            LinearLayout linearLayout = this.mIconRow;
            childAt = linearLayout.getChildAt(linearLayout.getChildCount() - 1);
        }
        Rect rect = sTempRect;
        if (childAt == null) {
            LGLog.i(TAG, "firstNotification is null");
            AbstractFloatingView.getTopOpenView(Launcher.getLauncher(getContext())).close(true);
            return;
        }
        childAt.getGlobalVisibleRect(rect);
        float fHeight = toBounds.height() / rect.height();
        ObjectAnimator objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(childAt, new PropertyListBuilder().scale(fHeight).translationY((toBounds.top - rect.top) + (((rect.height() * fHeight) - rect.height()) / 2.0f)).build());
        objectAnimatorOfPropertyValuesHolder.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.notification.NotificationFooterLayout.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                callback.onIconAnimationEnd((NotificationInfo) childAt.getTag());
                NotificationFooterLayout.this.removeViewFromIconRow(childAt);
            }
        });
        animatorSetCreateAnimatorSet.play(objectAnimatorOfPropertyValuesHolder);
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            marginStart = -this.mIconLayoutParams.width;
        } else {
            marginStart = this.mIconLayoutParams.width + this.mIconLayoutParams.getMarginStart();
        }
        if (this.mRtl) {
            marginStart = -marginStart;
        }
        if (!this.mOverflowNotifications.isEmpty()) {
            NotificationInfo notificationInfoRemove = this.mOverflowNotifications.remove(0);
            this.mNotifications.add(notificationInfoRemove);
            animatorSetCreateAnimatorSet.play(ObjectAnimator.ofFloat(addNotificationIconForInfo(notificationInfoRemove), (Property<View, Float>) ALPHA, 0.0f, 1.0f));
        }
        int childCount = (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || com.lge.launcher3.util.Utilities.isLGUI10_0()) ? this.mIconRow.getChildCount() : this.mIconRow.getChildCount() - 1;
        PropertyResetListener propertyResetListener = new PropertyResetListener(TRANSLATION_X, Float.valueOf(0.0f));
        for (int i = (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || com.lge.launcher3.util.Utilities.isLGUI10_0()) ? 1 : 0; i < childCount; i++) {
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this.mIconRow.getChildAt(i), (Property<View, Float>) TRANSLATION_X, marginStart);
            objectAnimatorOfFloat.addListener(propertyResetListener);
            animatorSetCreateAnimatorSet.play(objectAnimatorOfFloat);
        }
        animatorSetCreateAnimatorSet.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeViewFromIconRow(View child) {
        PopupContainerWithArrow open;
        this.mIconRow.removeView(child);
        this.mNotifications.remove((NotificationInfo) child.getTag());
        updateOverflowEllipsisVisibility();
        if (this.mIconRow.getChildCount() != 0 || (open = PopupContainerWithArrow.getOpen(Launcher.getLauncher(getContext()))) == null) {
            return;
        }
        Animator animatorReduceNotificationViewHeight = open.reduceNotificationViewHeight(getHeight(), getResources().getInteger(R.integer.config_removeNotificationViewDuration));
        animatorReduceNotificationViewHeight.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.notification.NotificationFooterLayout.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (NotificationFooterLayout.this.getParent() != null) {
                    ((ViewGroup) NotificationFooterLayout.this.getParent()).removeView(NotificationFooterLayout.this);
                }
            }
        });
        animatorReduceNotificationViewHeight.start();
    }

    public void trimNotifications(List<String> notifications) {
        if (!isAttachedToWindow() || this.mIconRow.getChildCount() == 0) {
            return;
        }
        Iterator<NotificationInfo> it = this.mOverflowNotifications.iterator();
        while (it.hasNext()) {
            if (!notifications.contains(it.next().notificationKey)) {
                it.remove();
            }
        }
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            for (int i = 0; i < this.mIconRow.getChildCount(); i++) {
                View childAt = this.mIconRow.getChildAt(i);
                if (!notifications.contains(((NotificationInfo) childAt.getTag()).notificationKey)) {
                    removeViewFromIconRow(childAt);
                }
            }
            return;
        }
        for (int childCount = this.mIconRow.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt2 = this.mIconRow.getChildAt(childCount);
            if (!notifications.contains(((NotificationInfo) childAt2.getTag()).notificationKey)) {
                removeViewFromIconRow(childAt2);
            }
        }
    }
}
