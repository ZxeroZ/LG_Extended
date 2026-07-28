package com.android.launcher3.notification;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.anim.PillHeightRevealOutlineProvider;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.notification.NotificationFooterLayout;
import com.android.launcher3.popup.PopupItemView;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierDrawer;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class NotificationItemView extends PopupItemView {
    private static final Rect sTempRect = new Rect();
    private boolean mAnimatingNextIcon;
    private NotificationFooterLayout mFooter;
    private TextView mHeaderCount;
    private NotificationMainView mMainView;
    private int mNotificationHeaderTextColor;
    private SwipeHelper mSwipeHelper;

    public NotificationItemView(Context context) {
        this(context, null, 0);
    }

    public NotificationItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mNotificationHeaderTextColor = 0;
    }

    @Override // com.android.launcher3.popup.PopupItemView, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mHeaderCount = (TextView) findViewById(R.id.notification_count);
        this.mMainView = (NotificationMainView) findViewById(R.id.main_view);
        this.mFooter = (NotificationFooterLayout) findViewById(R.id.footer);
        SwipeHelper swipeHelper = new SwipeHelper(0, this.mMainView, getContext());
        this.mSwipeHelper = swipeHelper;
        swipeHelper.setDisableHardwareLayers(true);
    }

    public NotificationMainView getMainView() {
        return this.mMainView;
    }

    public int getHeightMinusFooter() {
        return getHeight() - (this.mFooter.getParent() == null ? 0 : this.mFooter.getHeight());
    }

    public Animator animateHeightRemoval(int heightToRemove) {
        return new PillHeightRevealOutlineProvider(this.mPillRect, getBackgroundRadius(), getHeight() - heightToRemove).createRevealAnimator(this, true);
    }

    public void updateHeader(int notificationCount, IconPalette palette) {
        this.mHeaderCount.setText(AppNotifierDrawer.getAppNotifierMaxCountString(notificationCount));
        if (palette != null) {
            if (this.mNotificationHeaderTextColor == 0) {
                this.mNotificationHeaderTextColor = IconPalette.resolveContrastColor(getContext(), palette.dominantColor, getResources().getColor(R.color.popup_header_background_color));
            }
            this.mHeaderCount.setTextColor(this.mNotificationHeaderTextColor);
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mMainView.getNotificationInfo() == null) {
            return false;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        return this.mSwipeHelper.onInterceptTouchEvent(ev);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.mMainView.getNotificationInfo() == null) {
            return false;
        }
        return this.mSwipeHelper.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    public void applyNotificationInfos(final List<NotificationInfo> notificationInfos) {
        if (notificationInfos.isEmpty()) {
            return;
        }
        this.mMainView.applyNotificationInfo(notificationInfos.get(0), this.mIconView);
        for (int i = 1; i < notificationInfos.size(); i++) {
            this.mFooter.addNotificationInfo(notificationInfos.get(i));
        }
        this.mFooter.commitNotificationInfos();
    }

    public void trimNotifications(final List<String> notificationKeys) {
        if ((!notificationKeys.contains(this.mMainView.getNotificationInfo().notificationKey)) && !this.mAnimatingNextIcon) {
            this.mAnimatingNextIcon = true;
            this.mMainView.setVisibility(4);
            this.mMainView.setTranslationX(0.0f);
            View view = this.mIconView;
            Rect rect = sTempRect;
            view.getGlobalVisibleRect(rect);
            this.mFooter.animateFirstNotificationTo(rect, new NotificationFooterLayout.IconAnimationEndListener() { // from class: com.android.launcher3.notification.NotificationItemView.1
                @Override // com.android.launcher3.notification.NotificationFooterLayout.IconAnimationEndListener
                public void onIconAnimationEnd(NotificationInfo newMainNotification) {
                    if (newMainNotification != null) {
                        NotificationItemView.this.mMainView.applyNotificationInfo(newMainNotification, NotificationItemView.this.mIconView, true);
                        NotificationItemView.this.mMainView.setVisibility(0);
                    }
                    NotificationItemView.this.mAnimatingNextIcon = false;
                }
            });
            return;
        }
        this.mFooter.trimNotifications(notificationKeys);
    }
}
