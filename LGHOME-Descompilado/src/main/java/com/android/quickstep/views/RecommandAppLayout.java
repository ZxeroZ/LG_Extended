package com.android.quickstep.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherActivityInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Process;
import android.os.StrictMode;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.quickstep.FrequentAppManager;
import com.lge.contextenginelibrary.model.FrequentAppsInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.CPUBoostService;
import com.lge.launcher3.util.LGLog;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class RecommandAppLayout extends LinearLayout {
    private static final String PADDING_RES_NAME_FORMAT = "horizontal_padding_for_%d_shortcuts";
    private Context mContext;
    private int mIconSize;
    private Launcher mLauncher;
    private BroadcastReceiver mPackageRemoveReceiver;
    private AnimatorSet mRotationAniSet;

    public RecommandAppLayout(Context context) {
        super(context);
        this.mIconSize = 0;
        this.mPackageRemoveReceiver = new BroadcastReceiver() { // from class: com.android.quickstep.views.RecommandAppLayout.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String strReplace = intent.getData().toString().replace("package:", "");
                FrequentAppManager frequentAppManager = FrequentAppManager.getInstance(RecommandAppLayout.this.getContext());
                if (frequentAppManager.existFrequentApps(strReplace)) {
                    LGLog.d("RecommendAppLayout", strReplace + " is removed.");
                    frequentAppManager.checkUpdateFrequentApps(RecommandAppLayout.this.getContext());
                }
            }
        };
        BaseActivity baseActivityFromContext = BaseActivity.fromContext(context);
        if (baseActivityFromContext instanceof Launcher) {
            this.mLauncher = (Launcher) baseActivityFromContext;
        } else {
            this.mContext = context;
        }
        setLayoutTransition(null);
    }

    public RecommandAppLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIconSize = 0;
        this.mPackageRemoveReceiver = new BroadcastReceiver() { // from class: com.android.quickstep.views.RecommandAppLayout.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String strReplace = intent.getData().toString().replace("package:", "");
                FrequentAppManager frequentAppManager = FrequentAppManager.getInstance(RecommandAppLayout.this.getContext());
                if (frequentAppManager.existFrequentApps(strReplace)) {
                    LGLog.d("RecommendAppLayout", strReplace + " is removed.");
                    frequentAppManager.checkUpdateFrequentApps(RecommandAppLayout.this.getContext());
                }
            }
        };
        BaseActivity baseActivityFromContext = BaseActivity.fromContext(context);
        if (baseActivityFromContext instanceof Launcher) {
            this.mLauncher = (Launcher) baseActivityFromContext;
        } else {
            this.mContext = context;
        }
        setLayoutTransition(null);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int gap = getGap();
        int childCount = getChildCount();
        int centerLeft = childCount == 1 ? getCenterLeft() : getHorizontalPadding(childCount);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.recommand_app_item_top_margin);
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                int measuredWidth = childAt.getMeasuredWidth() + centerLeft;
                childAt.layout(centerLeft, dimensionPixelSize, measuredWidth, getMeasuredHeight());
                centerLeft = measuredWidth + gap;
            }
        }
    }

    private int getCenterLeft() {
        View childAt = getChildAt(0);
        if (childAt == null) {
            return 0;
        }
        return (getMeasuredWidth() - childAt.getMeasuredWidth()) / 2;
    }

    private int getGap() {
        int horizontalPadding = getHorizontalPadding(getChildCount()) * 2;
        int childCount = getChildCount();
        int measuredWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                measuredWidth += childAt.getMeasuredWidth();
            }
        }
        int measuredWidth2 = (getMeasuredWidth() - measuredWidth) - horizontalPadding;
        int i2 = childCount - 1;
        if (childCount < 2) {
            return 0;
        }
        return measuredWidth2 / i2;
    }

    protected int getHorizontalPadding(int numOfShortcut) {
        Resources resources = getResources();
        int identifier = resources.getIdentifier(String.format(Locale.ENGLISH, PADDING_RES_NAME_FORMAT, Integer.valueOf(numOfShortcut)), "dimen", getContext().getPackageName());
        if (identifier == 0) {
            return 0;
        }
        return resources.getDimensionPixelSize(identifier);
    }

    public void updateItems() {
        updateItems(false);
    }

    public void updateItems(boolean forceUpdate) {
        if (FrequentAppManager.isSupportRecommendApps()) {
            Context context = this.mLauncher;
            if (context == null) {
                context = this.mContext;
            }
            FrequentAppManager frequentAppManager = FrequentAppManager.getInstance(context);
            LGLog.d("FrequentAppManager", "updateItems : current count = " + getChildCount());
            if (frequentAppManager.needUpdate(forceUpdate) || (getChildCount() == 0 && frequentAppManager.getFrequentApps() != null)) {
                removeAllViews();
                if (frequentAppManager.getFrequentApps() != null) {
                    Iterator<FrequentAppsInfo.AppInfo> it = frequentAppManager.getFrequentApps().iterator();
                    while (it.hasNext()) {
                        addItem(it.next().packageName);
                    }
                    LGLog.d("FrequentAppManager", "updateItems : updated count = " + getChildCount());
                    return;
                }
                LGLog.d("FrequentAppManager", "getFrequentApps is null");
                return;
            }
            frequentAppManager.checkUpdateFrequentApps(getContext());
        }
    }

    public void addItem(String packageName) {
        Launcher launcher = this.mLauncher;
        ShortcutInfo shortcutInfoFromActivityInfo = null;
        if (launcher != null) {
            LauncherAppState.getInstance(launcher).getIconCache();
            List<LauncherActivityInfo> activityList = LauncherAppsCompat.getInstance(this.mLauncher).getActivityList(packageName, Process.myUserHandle());
            if (packageName.equals("com.android.contacts")) {
                Iterator<LauncherActivityInfo> it = activityList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    LauncherActivityInfo next = it.next();
                    if (next.getComponentName().flattenToShortString().contains("DialtactsActivity")) {
                        shortcutInfoFromActivityInfo = ShortcutInfo.fromActivityInfo(next, this.mLauncher);
                        break;
                    }
                }
            } else {
                shortcutInfoFromActivityInfo = ShortcutInfo.fromActivityInfo(activityList.get(0), this.mLauncher);
            }
            if (shortcutInfoFromActivityInfo != null) {
                BubbleTextView bubbleTextView = (BubbleTextView) this.mLauncher.createShortcut(this, shortcutInfoFromActivityInfo, R.layout.app_icon_recommand);
                shortcutInfoFromActivityInfo.usingLowResIcon = true;
                bubbleTextView.setTag(shortcutInfoFromActivityInfo);
                bubbleTextView.verifyHighRes();
                bubbleTextView.setTextVisibility(false);
                int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.recommand_app_item_badge_padding);
                this.mIconSize = bubbleTextView.getIconSize();
                int i = this.mIconSize;
                int i2 = dimensionPixelSize * 2;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(i + i2, i + i2);
                bubbleTextView.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
                bubbleTextView.setLayoutParams(layoutParams);
                addView(bubbleTextView);
                return;
            }
            return;
        }
        Context context = this.mContext;
        if (context != null) {
            LauncherAppState.getInstance(context).getIconCache();
            List<LauncherActivityInfo> activityList2 = LauncherAppsCompat.getInstance(this.mContext).getActivityList(packageName, Process.myUserHandle());
            if (packageName.equals("com.android.contacts")) {
                Iterator<LauncherActivityInfo> it2 = activityList2.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    LauncherActivityInfo next2 = it2.next();
                    if (next2.getComponentName().flattenToShortString().contains("DialtactsActivity")) {
                        shortcutInfoFromActivityInfo = ShortcutInfo.fromActivityInfo(next2, this.mContext);
                        break;
                    }
                }
            } else {
                shortcutInfoFromActivityInfo = ShortcutInfo.fromActivityInfo(activityList2.get(0), this.mContext);
            }
            if (shortcutInfoFromActivityInfo != null) {
                BubbleTextView bubbleTextView2 = (BubbleTextView) createShortcut(this.mContext, this, shortcutInfoFromActivityInfo);
                shortcutInfoFromActivityInfo.usingLowResIcon = true;
                bubbleTextView2.setTag(shortcutInfoFromActivityInfo);
                bubbleTextView2.verifyHighRes();
                bubbleTextView2.setTextVisibility(false);
                int dimensionPixelSize2 = getResources().getDimensionPixelSize(R.dimen.recommand_app_item_badge_padding);
                int i3 = dimensionPixelSize2 * 2;
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(bubbleTextView2.getIconSize() + i3, bubbleTextView2.getIconSize() + i3);
                bubbleTextView2.setPadding(dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize2, dimensionPixelSize2);
                bubbleTextView2.setLayoutParams(layoutParams2);
                addView(bubbleTextView2);
            }
        }
    }

    public View createShortcut(final Context context, ViewGroup parent, ShortcutInfo info) {
        BubbleTextView bubbleTextView = (BubbleTextView) ((Activity) context).getLayoutInflater().inflate((info.hasPhotoIcon() && info.hasLargeIcon()) ? R.layout.app_photo_icon : R.layout.app_icon_recommand, parent, false);
        bubbleTextView.setOnClickListener(new View.OnClickListener() { // from class: com.android.quickstep.views.RecommandAppLayout.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent;
                CPUBoostService.boostUp(v.getContext());
                Object tag = v.getTag();
                if (tag instanceof ShortcutInfo) {
                    intent = new Intent(((ShortcutInfo) tag).intent);
                    int[] iArr = new int[2];
                    v.getLocationOnScreen(iArr);
                    intent.setSourceBounds(new Rect(iArr[0], iArr[1], iArr[0] + v.getWidth(), iArr[1] + v.getHeight()));
                } else if (tag instanceof AppInfo) {
                    intent = ((AppInfo) tag).intent;
                } else {
                    throw new IllegalArgumentException("Input must be a Shortcut or AppInfo");
                }
                intent.addFlags(268435456);
                try {
                    LauncherAppsCompat launcherAppsCompat = LauncherAppsCompat.getInstance(context);
                    UserHandle userForSerialNumber = intent.hasExtra(ItemInfo.EXTRA_PROFILE) ? UserManagerCompat.getInstance(context).getUserForSerialNumber(intent.getLongExtra(ItemInfo.EXTRA_PROFILE, -1L)) : null;
                    if (userForSerialNumber != null && !userForSerialNumber.equals(Process.myUserHandle())) {
                        launcherAppsCompat.startActivityForProfile(intent.getComponent(), userForSerialNumber, intent.getSourceBounds(), null);
                        return;
                    }
                    StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
                    try {
                        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
                        context.startActivity(intent, null);
                        StrictMode.setVmPolicy(vmPolicy);
                    } catch (Throwable th) {
                        StrictMode.setVmPolicy(vmPolicy);
                        throw th;
                    }
                } catch (SecurityException unused) {
                    Toast.makeText(context, R.string.activity_not_found, 0).show();
                }
            }
        });
        UninstallModeManager.getInstance(context).setUninstallTypeForBadgeView(bubbleTextView);
        return bubbleTextView;
    }

    public int getIconSize() {
        return this.mIconSize;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerPackageRemoveReceiver(getContext());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterPackageRemoveReceiver(getContext());
    }

    public void registerPackageRemoveReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme(AppNotifierManager.ExtraSpec.USAGE_PACKAGE);
        context.registerReceiver(this.mPackageRemoveReceiver, intentFilter);
        LGLog.i(RecommandAppLayout.class.getSimpleName(), "registerPackageRemoveReceiver");
    }

    public void unregisterPackageRemoveReceiver(Context context) {
        LGLog.i(RecommandAppLayout.class.getSimpleName(), "unregisterPackageRemoveReceiver");
        context.unregisterReceiver(this.mPackageRemoveReceiver);
    }

    public void updateRotation(float degree, boolean animate) {
        if (degree == 180.0f) {
            degree = 0.0f;
        } else if (degree == 270.0f) {
            degree = -90.0f;
        }
        if (getChildCount() <= 1 || getChildAt(0) == null || getChildAt(0).getRotation() != degree) {
            AnimatorSet animatorSet = this.mRotationAniSet;
            if (animatorSet != null && animatorSet.isRunning()) {
                this.mRotationAniSet.pause();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.mRotationAniSet = animatorSet2;
            animatorSet2.setDuration(animate ? 300L : 0L);
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (childAt != null && childAt.getRotation() != degree) {
                    this.mRotationAniSet.play(ObjectAnimator.ofFloat(childAt, (Property<View, Float>) View.ROTATION, childAt.getRotation(), degree));
                }
            }
            this.mRotationAniSet.start();
        }
    }

    @Override // android.view.View
    public void setAlpha(float alpha) {
        if (getAlpha() != alpha && (alpha == 0.0f || alpha == 1.0f)) {
            Launcher launcher = this.mLauncher;
            if (launcher != null) {
                ((RecentsView) launcher.getOverviewPanel()).setCustomClipBound(alpha, "RecommandAppLayout");
            } else {
                Context context = this.mContext;
                if (context instanceof StatefulActivity) {
                    ((RecentsView) ((StatefulActivity) context).getOverviewPanel()).setCustomClipBound(alpha, "RecommandAppLayout");
                }
            }
        }
        super.setAlpha(alpha);
    }
}
