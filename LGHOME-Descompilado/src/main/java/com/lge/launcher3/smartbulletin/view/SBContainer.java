package com.lge.launcher3.smartbulletin.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.testing.TestProtocol;
import com.lge.launcher3.R;
import com.lge.launcher3.adaptive.AdaptiveTextUtil;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.homesettings.ActionManagerUserLog;
import com.lge.launcher3.homesettings.SmartBulletinAction;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.smartbulletin.info.SBAppWidgetProviderInfo;
import com.lge.launcher3.smartbulletin.log.SBLog;
import com.lge.launcher3.smartbulletin.view.SBNotiManager;
import com.lge.launcher3.smartbulletin.view.SBStateManager;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SBContainer extends FrameLayout implements Launcher.CustomContentCallbacks, SBCollapsableView, SBNotiManager.ISBNotiPanel {
    private static final String ACTION_VISION_SEARCH_CAMERA = "com.lge.ellievision.action.VISION_SEARCH_CAMERA";
    private static final int LARGE_SHADOW_COLOR = -14277082;
    private static final float SCROLL_THRESHOLD = 0.001f;
    private static final float SHADOW_LARGE_RADIUS = 2.0f;
    private static final float SHADOW_LARGE_X_OFFSET = 1.0f;
    private static final float SHADOW_LARGE_Y_OFFSET = 1.0f;
    protected static final String TAG = "SBContainer";
    private AnimatorSet mAnimatorSet;
    private SBCategoryLayout mCategoryLayout;
    private int mCollapseAniDuration;
    private float mCurRatio;
    private GestureDetector mGestures;
    private boolean mHasFocus;
    private int mHeaderActionbarHeight;
    private TextView mHeaderTitle;
    private long mLastUpdateTime;
    private TextView mNoProviderView;
    private SBNotiManager mNotiManager;
    private SBProviderObserver mObserver;
    private boolean mPostUpdateByNoti;
    private int mProviderTitleHeight;
    private SBRequestReceiver mRequestReceiver;
    private SBOverScrollView mScrollView;
    private boolean mSendLogFlag;
    private Runnable mSendLogPauseRunable;
    private Runnable mSendLogResumeRunable;
    private ImageButton mSettingButton;
    private int mStatusbarHeight;

    private void sendRandomNotiIntent() {
    }

    @Override // com.android.launcher3.Launcher.CustomContentCallbacks
    public boolean isScrollingAllowed() {
        return true;
    }

    @Override // com.android.launcher3.Launcher.CustomContentCallbacks
    public void onHide() {
    }

    public SBContainer(Context context) {
        super(context);
        this.mObserver = null;
        this.mScrollView = null;
        this.mCategoryLayout = null;
        this.mHeaderActionbarHeight = 0;
        this.mStatusbarHeight = 0;
        this.mCollapseAniDuration = 300;
        this.mRequestReceiver = null;
        this.mAnimatorSet = null;
        this.mNoProviderView = null;
        this.mNotiManager = null;
        this.mProviderTitleHeight = 0;
        this.mCurRatio = 0.0f;
        this.mPostUpdateByNoti = false;
        this.mLastUpdateTime = 0L;
        this.mHasFocus = true;
        this.mSendLogFlag = true;
        this.mSendLogPauseRunable = new Runnable() { // from class: com.lge.launcher3.smartbulletin.view.SBContainer.5
            @Override // java.lang.Runnable
            public void run() {
                ActionManagerUserLog.sendBoardPauseAll(SBContainer.this.getContext());
                SmartBulletinAction.sendPaused(SBContainer.this.getContext());
            }
        };
        this.mSendLogResumeRunable = new Runnable() { // from class: com.lge.launcher3.smartbulletin.view.SBContainer.6
            @Override // java.lang.Runnable
            public void run() {
                ActionManagerUserLog.sendBoardResumeAll(SBContainer.this.getContext());
                SmartBulletinAction.sendResumed(SBContainer.this.getContext());
            }
        };
    }

    public SBContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SBContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SBContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mObserver = null;
        this.mScrollView = null;
        this.mCategoryLayout = null;
        this.mHeaderActionbarHeight = 0;
        this.mStatusbarHeight = 0;
        this.mCollapseAniDuration = 300;
        this.mRequestReceiver = null;
        this.mAnimatorSet = null;
        this.mNoProviderView = null;
        this.mNotiManager = null;
        this.mProviderTitleHeight = 0;
        this.mCurRatio = 0.0f;
        this.mPostUpdateByNoti = false;
        this.mLastUpdateTime = 0L;
        this.mHasFocus = true;
        this.mSendLogFlag = true;
        this.mSendLogPauseRunable = new Runnable() { // from class: com.lge.launcher3.smartbulletin.view.SBContainer.5
            @Override // java.lang.Runnable
            public void run() {
                ActionManagerUserLog.sendBoardPauseAll(SBContainer.this.getContext());
                SmartBulletinAction.sendPaused(SBContainer.this.getContext());
            }
        };
        this.mSendLogResumeRunable = new Runnable() { // from class: com.lge.launcher3.smartbulletin.view.SBContainer.6
            @Override // java.lang.Runnable
            public void run() {
                ActionManagerUserLog.sendBoardResumeAll(SBContainer.this.getContext());
                SmartBulletinAction.sendResumed(SBContainer.this.getContext());
            }
        };
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        SBLog.d(TAG, "onFinishInflate() ");
        super.onFinishInflate();
        this.mCategoryLayout = (SBCategoryLayout) findViewById(R.id.category_layout);
        SBOverScrollView sBOverScrollView = (SBOverScrollView) findViewById(R.id.scroll_view);
        this.mScrollView = sBOverScrollView;
        sBOverScrollView.setOverScrollMode(1);
        this.mScrollView.setVerticalScrollBarEnabled(false);
        this.mNoProviderView = (TextView) findViewById(R.id.no_provider_view);
        this.mHeaderTitle = (TextView) findViewById(R.id.header_title);
        this.mHeaderActionbarHeight = (int) getContext().getResources().getDimension(R.dimen.smartbulletin_actionbar_height);
        this.mStatusbarHeight = (int) getContext().getResources().getDimension(R.dimen.smartbulletin_statusbar_height);
        this.mObserver = new SBProviderObserver(getContext(), this);
        this.mRequestReceiver = new SBRequestReceiver(this);
        this.mProviderTitleHeight = (int) getContext().getResources().getDimension(R.dimen.smartbulletin_provider_title_height);
        addListener();
        if (getChildCountInCategoryLayout() == 1) {
            ((ImageButton) ((LinearLayout) this.mCategoryLayout.getChildAt(0)).findViewById(R.id.provider_collapse_button)).setAlpha(0.0f);
        }
        SBNotiManager sBNotiManager = SBNotiManager.getInstance(getContext());
        this.mNotiManager = sBNotiManager;
        sBNotiManager.setSBNotiInterface(this);
        updateNotiCountView();
        SmartBulletinAction.sendEnabled(getContext());
        setAdaptiveHeaderForSB(getContext());
    }

    private void addListener() {
        ImageButton imageButton = (ImageButton) findViewById(R.id.setting_button);
        this.mSettingButton = imageButton;
        imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.smartbulletin.view.SBContainer.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SBContainer.this.startSettingActivity(v, v.getContext());
            }
        });
        if (!Utilities.isLGUI7_0()) {
            setSettingsButtonColor(this.mSettingButton);
        }
        this.mSettingButton.setOnKeyListener(new View.OnKeyListener() { // from class: com.lge.launcher3.smartbulletin.view.SBContainer.2
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0 || keyCode != 20) {
                    return false;
                }
                if (SBContainer.this.getChildCountInCategoryLayout() == 0) {
                    SBContainer.this.mNoProviderView.requestFocus();
                    return true;
                }
                SBContainer.this.mCategoryLayout.requestFocus();
                return true;
            }
        });
        this.mNoProviderView.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.smartbulletin.view.SBContainer.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SBContainer.this.startSettingActivity(v, v.getContext());
            }
        });
    }

    public void startSettingActivity(View v, Context context) {
        Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_SBSETTING.getValue(context));
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setFlags(276824064);
        intent.putExtra("startedBy", "SmartBulletin");
        ActivityOptions activityOptionsMakeScaleUpAnimation = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        activityOptionsMakeScaleUpAnimation.setSplashScreenStyle(0);
        ((Activity) context).startActivity(intent, activityOptionsMakeScaleUpAnimation.toBundle());
        this.mPostUpdateByNoti = true;
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        this.mHasFocus = hasWindowFocus;
        if (hasWindowFocus) {
            if (getGlobalVisibleRect(new Rect())) {
                SmartBulletinAction.sendResumed(getContext());
                return;
            }
            return;
        }
        SmartBulletinAction.sendPaused(getContext());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        SBLog.d(TAG, "onAttachedToWindow() ");
        super.onAttachedToWindow();
        SBRequestReceiver sBRequestReceiver = this.mRequestReceiver;
        if (sBRequestReceiver != null) {
            getContext().registerReceiver(this.mRequestReceiver, sBRequestReceiver.getIntentFilter());
        }
        SBStateManager.onChangeState(SBStateManager.SBState.OPEN);
        SBNotiManager sBNotiManager = this.mNotiManager;
        if (sBNotiManager != null) {
            sBNotiManager.registerNotiReceiver(getContext());
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        SBLog.d(TAG, "onDetachedFromWindow() ");
        super.onDetachedFromWindow();
        this.mObserver.onDestroy(getContext());
        if (this.mRequestReceiver != null) {
            getContext().unregisterReceiver(this.mRequestReceiver);
        }
        SBStateManager.onChangeState(SBStateManager.SBState.OPEN);
        SBNotiManager sBNotiManager = this.mNotiManager;
        if (sBNotiManager != null) {
            sBNotiManager.unregisterNotiReceiver(getContext());
        }
    }

    @Override // com.lge.launcher3.smartbulletin.view.SBCollapsableView
    public void expandProvider(boolean isAnimation) {
        ArrayList<Animator> arrayListProcessExpand = this.mCategoryLayout.processExpand();
        arrayListProcessExpand.add(getAnimatorScrollY(0));
        AnimatorSet animatorSet = new AnimatorSet();
        this.mAnimatorSet = animatorSet;
        animatorSet.playTogether(arrayListProcessExpand);
        this.mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        if (!isAnimation) {
            this.mAnimatorSet.setStartDelay(100L);
            postDelayed(new Runnable() { // from class: com.lge.launcher3.smartbulletin.view.SBContainer.4
                @Override // java.lang.Runnable
                public void run() {
                    SBContainer.this.mAnimatorSet.end();
                }
            }, 100L);
        }
        this.mAnimatorSet.start();
        this.mCategoryLayout.resetScrollAnimation();
        SBStateManager.onChangeState(SBStateManager.SBState.OPEN);
    }

    @Override // com.lge.launcher3.smartbulletin.view.SBCollapsableView
    public void collapseProvider() {
        ArrayList<Animator> arrayListProcessCollapse = this.mCategoryLayout.processCollapse();
        arrayListProcessCollapse.add(getAnimatorScrollY(0));
        AnimatorSet animatorSet = new AnimatorSet();
        this.mAnimatorSet = animatorSet;
        animatorSet.playTogether(arrayListProcessCollapse);
        this.mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        this.mAnimatorSet.start();
        this.mCategoryLayout.resetScrollAnimation();
        SBStateManager.onChangeState(SBStateManager.SBState.COLLAPSE);
    }

    class SBOnGestureListener implements GestureDetector.OnGestureListener {
        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent e) {
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public void onShowPress(MotionEvent e) {
        }

        SBOnGestureListener() {
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            SBAppWidgetProviderInfo sBAppWidgetProviderInfo;
            View childRect = SBContainer.this.getChildRect((int) e.getX(), (int) e.getY());
            if (childRect != null) {
                ((SBProviderLayout) childRect).removeOnceBadge();
                sBAppWidgetProviderInfo = (SBAppWidgetProviderInfo) childRect.getTag();
            } else {
                sBAppWidgetProviderInfo = null;
            }
            if (SBStateManager.getState() != SBStateManager.SBState.COLLAPSE) {
                return false;
            }
            SBStateManager.onChangeState(SBStateManager.SBState.OPEN);
            SBContainer.this.mCategoryLayout.resetScrollAnimation();
            ArrayList<Animator> arrayListProcessExpand = SBContainer.this.mCategoryLayout.processExpand();
            if (sBAppWidgetProviderInfo != null) {
                arrayListProcessExpand.add(SBContainer.this.getAnimatorscrollToComponent(sBAppWidgetProviderInfo.getComponentName().flattenToString()));
            }
            SBContainer.this.mAnimatorSet = new AnimatorSet();
            SBContainer.this.mAnimatorSet.setInterpolator(new DecelerateInterpolator());
            SBContainer.this.mAnimatorSet.playTogether(arrayListProcessExpand);
            SBContainer.this.mAnimatorSet.start();
            return true;
        }

        @Override // android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (((View) SBContainer.this.mParent).getLayerType() == 2) {
                ((View) SBContainer.this.mParent).setLayerType(0, null);
            }
            return false;
        }
    }

    enum VelocityLevel {
        INIT(-1),
        LOW(0),
        MID(1),
        HIGH(2);

        private int mLevel;

        VelocityLevel(int level) {
            this.mLevel = level;
        }

        public int getLevel() {
            return this.mLevel;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.mGestures == null) {
            this.mGestures = new GestureDetector(getContext(), new SBOnGestureListener());
        }
        if (this.mGestures != null) {
            int action = ev.getAction() & 255;
            if (SBStateManager.getState() == SBStateManager.SBState.COLLAPSE) {
                if (new Rect(0, 0, getRight(), this.mHeaderActionbarHeight + this.mStatusbarHeight).contains((int) ev.getX(), (int) ev.getY())) {
                    return super.dispatchTouchEvent(ev);
                }
                if (this.mGestures.onTouchEvent(ev) || action == 0 || action == 2) {
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }
            if (this.mGestures.onTouchEvent(ev)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getChildRect(int xPos, int yPos) {
        int childCountInCategoryLayout = getChildCountInCategoryLayout();
        View view = null;
        for (int i = 0; i < childCountInCategoryLayout; i++) {
            View childAt = this.mCategoryLayout.getChildAt(i);
            Rect rect = new Rect();
            childAt.getGlobalVisibleRect(rect);
            if (SBStateManager.getState() == SBStateManager.SBState.OPEN) {
                rect.top += this.mProviderTitleHeight;
            }
            if (rect.contains(xPos, yPos)) {
                view = childAt;
            }
        }
        return view;
    }

    @Override // com.lge.launcher3.smartbulletin.view.SBCollapsableView
    public Animator getAnimatorscrollToComponent(String providerName) {
        int i;
        if (this.mCategoryLayout == null || providerName == null) {
            return ObjectAnimator.ofInt(0);
        }
        int childCountInCategoryLayout = getChildCountInCategoryLayout();
        if (this.mCategoryLayout.getChildAt(0) != null) {
            i = 0;
            while (i < childCountInCategoryLayout) {
                if (providerName.equals(((SBAppWidgetProviderInfo) this.mCategoryLayout.getChildAt(i).getTag()).getComponentName().flattenToString())) {
                    break;
                }
                i++;
            }
            i = -1;
        } else {
            i = -1;
        }
        ObjectAnimator objectAnimatorOfInt = ObjectAnimator.ofInt(this.mScrollView, TestProtocol.SCROLL_Y_FIELD, Math.min(i != -1 ? this.mCategoryLayout.getChildAt(i).getTop() : 0, this.mCategoryLayout.getLastRealBottom() - this.mScrollView.getHeight()));
        objectAnimatorOfInt.setDuration(this.mCollapseAniDuration);
        return objectAnimatorOfInt;
    }

    @Override // com.android.launcher3.Launcher.CustomContentCallbacks
    public void onScrollProgressChanged(float ratio) {
        this.mCurRatio = ratio;
        if (ratio < SCROLL_THRESHOLD) {
            if (this.mSendLogFlag) {
                this.mSendLogFlag = false;
                post(this.mSendLogPauseRunable);
            }
            orderProviders();
            return;
        }
        if (ratio <= 0.999f || this.mSendLogFlag) {
            return;
        }
        this.mSendLogFlag = true;
        post(this.mSendLogResumeRunable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getChildCountInCategoryLayout() {
        SBCategoryLayout sBCategoryLayout = this.mCategoryLayout;
        if (sBCategoryLayout == null) {
            return 0;
        }
        return sBCategoryLayout.getChildCount();
    }

    private Animator getAnimatorScrollY(int scollY) {
        ObjectAnimator objectAnimatorOfInt = ObjectAnimator.ofInt(this.mScrollView, TestProtocol.SCROLL_Y_FIELD, 0);
        objectAnimatorOfInt.setDuration(this.mCollapseAniDuration);
        return objectAnimatorOfInt;
    }

    @Override // com.lge.launcher3.smartbulletin.view.SBNotiManager.ISBNotiPanel
    public void updateNotiCountView() {
        this.mNotiManager.updateProviderLayout(this.mCategoryLayout);
        this.mPostUpdateByNoti = true;
        if (this.mCurRatio > 0.0f) {
            return;
        }
        orderProviders();
    }

    private void orderProviders() {
        if (this.mCategoryLayout == null || !this.mPostUpdateByNoti) {
            return;
        }
        if (SBStateManager.getState() == SBStateManager.SBState.COLLAPSE) {
            expandProvider(false);
        }
        if (this.mCategoryLayout.orderProvidersByNoti(this.mLastUpdateTime)) {
            this.mScrollView.setScrollY(0);
        }
        this.mLastUpdateTime = System.currentTimeMillis();
        this.mPostUpdateByNoti = false;
    }

    @Override // com.android.launcher3.Launcher.CustomContentCallbacks
    public void onShow(boolean fromResume) {
        orderProviders();
    }

    private void setSettingsButtonColor(View settingButton) {
        Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(getResources(), R.drawable.smartbulletin_ic_menu_settings);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmapDecodeResource.getWidth(), bitmapDecodeResource.getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCreateBitmap.eraseColor(Utilities.sWhite);
        Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap(bitmapDecodeResource.getWidth(), bitmapDecodeResource.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap2);
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bitmapCreateBitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(bitmapDecodeResource, 0.0f, 0.0f, paint);
        paint.setXfermode(null);
        ((ImageView) settingButton).setImageBitmap(bitmapCreateBitmap2);
    }

    private static void sendIntentByBroadcast(Context context, String action) {
        context.sendBroadcast(new Intent(action));
    }

    private static Intent getVisionSearchCameraIntent() {
        return new Intent(ACTION_VISION_SEARCH_CAMERA);
    }

    private boolean isSupportVisionSearchCamera(Context context) {
        List<ResolveInfo> listQueryIntentActivities = context.getPackageManager().queryIntentActivities(getVisionSearchCameraIntent(), 0);
        return (listQueryIntentActivities == null || listQueryIntentActivities.size() == 0) ? false : true;
    }

    private void setAdaptiveHeaderForSB(Context context) {
        int defaultAdaptiveColor = SharedPreferencesManager.getInt(context, 0, SharedPreferencesConst.AdaptiveTextKey.TEXT_COLOR, 0);
        if (defaultAdaptiveColor == 0) {
            AdaptiveTextUtil.runAdaptiveColor(context);
            defaultAdaptiveColor = AdaptiveTextUtil.getDefaultAdaptiveColor(context);
        }
        setAdaptiveHeaderForSB(defaultAdaptiveColor);
    }

    public void setAdaptiveHeaderForSB(int color) {
        if (this.mHeaderTitle == null || this.mSettingButton == null) {
            return;
        }
        LGLog.d(TAG, "set adaptive feature to Smart bulletin header view : " + color);
        this.mHeaderTitle.setTextColor(color);
        this.mSettingButton.setImageResource(AdaptiveTextUtil.isDarkColor(color) ? R.drawable.btn_setting : R.drawable.smartbulletin_ic_menu_settings);
    }
}
