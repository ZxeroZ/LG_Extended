package com.android.launcher3.notification;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.notification.SwipeHelper;
import com.android.launcher3.util.Themes;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class NotificationMainView extends FrameLayout implements SwipeHelper.Callback {
    private int mBackgroundColor;
    private NotificationInfo mNotificationInfo;
    private ViewGroup mTextAndBackground;
    private TextView mTextView;
    private TextView mTitleView;

    @Override // com.android.launcher3.notification.SwipeHelper.Callback
    public View getChildAtPosition(MotionEvent ev) {
        return this;
    }

    @Override // com.android.launcher3.notification.SwipeHelper.Callback
    public float getFalsingThresholdFactor() {
        return 1.0f;
    }

    @Override // com.android.launcher3.notification.SwipeHelper.Callback
    public boolean isAntiFalsingNeeded() {
        return false;
    }

    @Override // com.android.launcher3.notification.SwipeHelper.Callback
    public void onBeginDrag(View v) {
    }

    @Override // com.android.launcher3.notification.SwipeHelper.Callback
    public void onChildSnappedBack(View animView, float targetLeft) {
    }

    @Override // com.android.launcher3.notification.SwipeHelper.Callback
    public void onDragCancelled(View v) {
    }

    @Override // com.android.launcher3.notification.SwipeHelper.Callback
    public boolean updateSwipeProgress(View animView, boolean dismissable, float swipeProgress) {
        return true;
    }

    public NotificationMainView(Context context) {
        this(context, null, 0);
    }

    public NotificationMainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.text_and_background);
        this.mTextAndBackground = viewGroup;
        ColorDrawable colorDrawable = (ColorDrawable) viewGroup.getBackground();
        this.mBackgroundColor = colorDrawable.getColor();
        this.mTextAndBackground.setBackground(new RippleDrawable(ColorStateList.valueOf(Themes.getAttrColor(getContext(), android.R.attr.colorControlHighlight)), colorDrawable, null));
        this.mTitleView = (TextView) this.mTextAndBackground.findViewById(R.id.title);
        this.mTextView = (TextView) this.mTextAndBackground.findViewById(R.id.text);
    }

    public void applyNotificationInfo(NotificationInfo mainNotification, View iconView) {
        applyNotificationInfo(mainNotification, iconView, false);
    }

    public void applyNotificationInfo(NotificationInfo mainNotification, View iconView, boolean animate) {
        this.mNotificationInfo = mainNotification;
        CharSequence charSequence = mainNotification.title;
        CharSequence charSequence2 = this.mNotificationInfo.text;
        if (!TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(charSequence2)) {
            this.mTitleView.setText(charSequence);
            this.mTextView.setText(charSequence2);
        } else {
            this.mTitleView.setMaxLines(2);
            TextView textView = this.mTitleView;
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = charSequence2;
            }
            textView.setText(charSequence);
            this.mTextView.setVisibility(8);
        }
        iconView.setBackground(this.mNotificationInfo.getIconForBackground(getContext(), this.mBackgroundColor));
        if (this.mNotificationInfo.intent != null) {
            setOnClickListener(this.mNotificationInfo);
        }
        setTranslationX(0.0f);
        setTag(new ItemInfo());
        if (animate) {
            ObjectAnimator.ofFloat(this.mTextAndBackground, (Property<ViewGroup, Float>) ALPHA, 0.0f, 1.0f).setDuration(150L).start();
        }
    }

    public NotificationInfo getNotificationInfo() {
        return this.mNotificationInfo;
    }

    @Override // com.android.launcher3.notification.SwipeHelper.Callback
    public boolean canChildBeDismissed(View v) {
        NotificationInfo notificationInfo = this.mNotificationInfo;
        return notificationInfo != null && notificationInfo.dismissable;
    }

    @Override // com.android.launcher3.notification.SwipeHelper.Callback
    public void onChildDismissed(View v) {
        Launcher.getLauncher(getContext()).getPopupDataProvider().cancelNotification(this.mNotificationInfo.notificationKey);
    }
}
