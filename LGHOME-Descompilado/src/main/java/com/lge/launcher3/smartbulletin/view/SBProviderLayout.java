package com.lge.launcher3.smartbulletin.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lge.launcher3.R;
import com.lge.launcher3.smartbulletin.constant.SBConstant;
import com.lge.launcher3.smartbulletin.info.SBAppWidgetProviderInfo;
import com.lge.launcher3.smartbulletin.lib.Action;
import com.lge.launcher3.smartbulletin.lib.Notification;
import com.lge.launcher3.smartbulletin.log.SBLog;
import com.lge.launcher3.smartbulletin.view.SBStateManager;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class SBProviderLayout extends LinearLayout {
    protected static final String TAG = "SBProviderLayout";
    private LinearLayout mBadgeLayout;
    private boolean mCustomProviderTitleBgColor;
    private boolean mCustomProviderTitleTextColor;
    private SBAppWidgetProviderInfo mInfo;
    private Intent mIntent;
    private long mNotiTime;
    private ImageView mProviderIcon;
    private TextView mProviderTitle;
    private int mProviderTitleColor;
    private LinearLayout mProviderTitleLayout;
    private SBProviderScrollAnimation mSBScrollAnimation;

    public SBProviderLayout(Context context) {
        this(context, null);
    }

    public SBProviderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SBProviderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCustomProviderTitleTextColor = false;
        this.mCustomProviderTitleBgColor = false;
        this.mInfo = null;
        this.mSBScrollAnimation = new SBProviderScrollAnimation();
        this.mNotiTime = 0L;
    }

    public SBAppWidgetProviderInfo getProviderInfo() {
        return this.mInfo;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        this.mProviderIcon = (ImageView) findViewById(R.id.smartbulletin_provider_icon);
        this.mProviderTitle = (TextView) findViewById(R.id.smartbulletin_provider_title);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.smartbulletin_provider_title_layout);
        this.mProviderTitleLayout = linearLayout;
        linearLayout.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.smartbulletin.view.SBProviderLayout.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SBProviderLayout.this.removeOnceBadge();
                SBProviderLayout.this.runProviderActivity(v);
            }
        });
        setOnKeyListener(new View.OnKeyListener() { // from class: com.lge.launcher3.smartbulletin.view.SBProviderLayout.2
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == 1 && keyCode == 66) {
                    SBProviderLayout.this.removeOnceBadge();
                    SBProviderLayout.this.runProviderActivity(v);
                    return true;
                }
                if (event.getAction() != 1) {
                    return false;
                }
                if ((keyCode != 20 && keyCode != 19) || SBStateManager.getState() != SBStateManager.SBState.COLLAPSE) {
                    return false;
                }
                SBProviderLayout.sendIntentByBroadcast(SBProviderLayout.this.getContext(), Action.SMARTBULLETIN_ACTION_REQUEST_EXPAND);
                return false;
            }
        });
        this.mBadgeLayout = (LinearLayout) findViewById(R.id.provider_badgelayout);
        super.onFinishInflate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runProviderActivity(View v) {
        try {
            ActivityOptions activityOptionsMakeBasic = ActivityOptions.makeBasic();
            activityOptionsMakeBasic.setSplashScreenStyle(0);
            v.getContext().startActivity(this.mIntent, activityOptionsMakeBasic.toBundle());
        } catch (Exception e) {
            SBLog.e(TAG, "onClick() provider title:  Can not find default activity");
            e.printStackTrace();
        }
    }

    public void setProviderBgColor(int color) {
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.smartbulletin_bg_stoke_width);
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R.dimen.smartbulletin_bg_stoke_radius);
        int color2 = getResources().getColor(R.color.smartbulletin_bg_stroke, null);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(dimensionPixelSize2);
        gradientDrawable.setStroke(dimensionPixelSize, color2);
        setBackground(gradientDrawable);
        setElevation(getResources().getDimensionPixelSize(R.dimen.smartbulletin_provider_elevation));
    }

    public void setProviderTitleBgColor(int color) {
        if (this.mProviderTitleLayout != null) {
            int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.smartbulletin_bg_stoke_radius);
            GradientDrawable gradientDrawable = new GradientDrawable();
            float f = dimensionPixelSize;
            gradientDrawable.setCornerRadii(new float[]{f, f, f, f, 0.0f, 0.0f, 0.0f, 0.0f});
            gradientDrawable.setColor(color);
            this.mProviderTitleLayout.setBackground(gradientDrawable);
        }
    }

    public void setProviderTitleColor(int color) {
        this.mProviderTitleColor = color;
        TextView textView = this.mProviderTitle;
        if (textView != null) {
            textView.setTextColor(color);
        }
    }

    public void setCustomProviderTitleBgColor(int color, boolean value) {
        this.mCustomProviderTitleBgColor = value;
        setProviderTitleBgColor(color);
    }

    public void setCustomProviderTitleColor(int color, boolean value) {
        this.mCustomProviderTitleTextColor = value;
        setProviderTitleColor(color);
    }

    public boolean getCustomProviderTitleBgColor() {
        return this.mCustomProviderTitleBgColor;
    }

    public boolean getCustomProviderTitleTextColor() {
        return this.mCustomProviderTitleTextColor;
    }

    public void setProviderInfo(SBAppWidgetProviderInfo info) {
        this.mInfo = info;
        if (info != null) {
            Intent defaultActivity = getDefaultActivity(info.mAppWidgetProviderInfo.provider.getPackageName());
            this.mIntent = defaultActivity;
            if (defaultActivity == null) {
                this.mIntent = getContext().getPackageManager().getLaunchIntentForPackage(this.mInfo.mAppWidgetProviderInfo.provider.getPackageName());
            }
            AppWidgetProviderInfo appWidgetProviderInfo = this.mInfo.mAppWidgetProviderInfo;
            if (this.mProviderIcon != null) {
                this.mProviderIcon.setImageDrawable(appWidgetProviderInfo.loadIcon(getContext(), getResources().getDisplayMetrics().densityDpi));
            }
            if (this.mProviderTitle != null) {
                this.mProviderTitle.setText(appWidgetProviderInfo.loadLabel(getContext().getPackageManager()).toUpperCase(Locale.getDefault()));
            }
        }
    }

    private Intent getDefaultActivity(String pakcageName) {
        try {
            Resources resourcesForApplication = getContext().getPackageManager().getResourcesForApplication(pakcageName);
            int identifier = resourcesForApplication.getIdentifier(SBConstant.SMARTBULLETIN_PROVIDER_DEFAULT_ACTIVITY, "string", pakcageName);
            if (identifier == 0) {
                return null;
            }
            ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(resourcesForApplication.getString(identifier));
            Intent intent = new Intent();
            intent.setComponent(componentNameUnflattenFromString);
            intent.addFlags(268435456);
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SBProviderScrollAnimation getSBScrollAnimation() {
        return this.mSBScrollAnimation;
    }

    public class SBProviderScrollAnimation {
        private int mScrollDuration = 300;
        private int mScrollTransY = 300;
        private AnimatorSet mAnimatorSet = null;
        private ObjectAnimator mObjAnimator = null;

        public SBProviderScrollAnimation() {
        }

        public void initAnimator(int direction) {
            if (this.mAnimatorSet == null) {
                this.mAnimatorSet = new AnimatorSet();
                ObjectAnimator duration = ObjectAnimator.ofFloat(SBProviderLayout.this, View.TRANSLATION_Y.getName(), this.mScrollTransY * direction, 0.0f).setDuration(this.mScrollDuration);
                this.mObjAnimator = duration;
                duration.setInterpolator(new DecelerateInterpolator());
                this.mAnimatorSet.addListener(new Animator.AnimatorListener() { // from class: com.lge.launcher3.smartbulletin.view.SBProviderLayout.SBProviderScrollAnimation.1
                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationRepeat(Animator animation) {
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        SBProviderScrollAnimation.this.mAnimatorSet = null;
                    }

                    @Override // android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        SBProviderScrollAnimation.this.mAnimatorSet = null;
                    }
                });
            }
        }

        public void startAniamtion() {
            AnimatorSet animatorSet = this.mAnimatorSet;
            if (animatorSet == null || animatorSet.isStarted()) {
                return;
            }
            this.mAnimatorSet.playTogether(this.mObjAnimator);
            this.mAnimatorSet.start();
        }

        public void resetAniamtion() {
            this.mAnimatorSet = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendIntentByBroadcast(Context context, String action) {
        context.sendBroadcast(new Intent(action));
    }

    public void addNewBadge() {
        removeAllBadge();
        ImageView imageView = new ImageView(getContext());
        imageView.setId(R.id.newbadgeview_index);
        Drawable drawable = getResources().getDrawable(R.drawable.smartbulletin_ic_newbadge);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.smartbulletin_noti_size);
        int dimensionPixelSize2 = getResources().getDimensionPixelSize(R.dimen.smartbulletin_noti_padding);
        imageView.setImageDrawable(drawable);
        imageView.setColorFilter(this.mProviderTitleColor, PorterDuff.Mode.SRC_ATOP);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize2);
        this.mBadgeLayout.addView(imageView, dimensionPixelSize, dimensionPixelSize);
    }

    public void addOnGoing(SBNoti noti) {
        removeAllBadge();
        SBNotiView sBNotiView = (SBNotiView) View.inflate(getContext(), R.layout.smartbulletin_notiview, null);
        sBNotiView.setWidgetDrawableRes(this.mInfo, noti.mResUri);
        sBNotiView.setNoti(noti);
        this.mBadgeLayout.addView(sBNotiView);
    }

    public void addBadge(SBNoti noti) {
        if (noti.mComponentName.equals(this.mInfo.getComponentName().flattenToString())) {
            if (noti.mNotiType.equals(Notification.SBNOTI_TYPE_ONGOING)) {
                addOnGoing(noti);
            } else if (noti.mNotiType.equals(Notification.SBNOTI_TYPE_ONCE)) {
                addNewBadge();
            }
            this.mNotiTime = noti.mTime;
        }
    }

    public void removeAllBadge() {
        if (this.mBadgeLayout.getChildCount() > 0) {
            this.mBadgeLayout.removeAllViews();
            this.mNotiTime = 0L;
        }
    }

    public void removeOnceBadge() {
        if (this.mBadgeLayout.getChildCount() > 0) {
            View viewFindViewById = this.mBadgeLayout.findViewById(R.id.newbadgeview_index);
            if (viewFindViewById != null) {
                this.mBadgeLayout.removeView(viewFindViewById);
                SBNotiManager.getInstance(getContext()).removeOnceInDB(getContext(), this.mInfo);
            }
            this.mNotiTime = 0L;
        }
    }

    public boolean hasNotiView() {
        LinearLayout linearLayout = this.mBadgeLayout;
        return linearLayout != null && linearLayout.getChildCount() > 0;
    }

    public long getNotiTime() {
        return this.mNotiTime;
    }
}
