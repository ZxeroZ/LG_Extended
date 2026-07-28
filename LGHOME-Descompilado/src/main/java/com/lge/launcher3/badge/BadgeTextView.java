package com.lge.launcher3.badge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.BadgeUtils;
import com.lge.launcher3.badge.appnotifier.AppNotifierData;
import com.lge.launcher3.badge.appnotifier.AppNotifierDrawer;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.badge.appnotifier.IAppNotifierView;
import com.lge.launcher3.badge.uninstall.IUninstallBadgeView;
import com.lge.launcher3.badge.uninstall.UninstallBadgeUtils;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.debug.DuplicatedApplicationChecker;
import com.lge.launcher3.droptarget.ButtonDropTargetUtils;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.GiftBoxManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class BadgeTextView extends TextView implements IAppNotifierView, IUninstallBadgeView {
    public static final String DEFAULT_EMAIL_ACCOUT_ID = "-1";
    private static final String EXTRA_EMAIL_ACCOUNT_ID = "AccountId";
    public static final String TAG = "BadgeTextView";
    private BitmapDrawable mAppNotifierBadgeDrawable;
    private int mAppNotifierCount;
    private AppNotifierDrawer mAppNotifierDrawer;
    private ComponentName mComponentName;
    private BitmapDrawable mDataFreeDrawable;
    private int mDataFreeDrawableTopMargin;
    private boolean mEnableAni;
    private boolean mForceSetRect;
    private BitmapDrawable mIconChangeBadgeDrawable;
    private boolean mIsSetUnInstallBadgeDesc;
    protected boolean mLayoutHorizontal;
    private Rect mNewBound;
    private String mRegisteredComponentName;
    private BitmapDrawable mShortcutBadgeDrawable;
    private BitmapDrawable mUninstallBadgeDrawable;
    private Rect mUninstallBadgeRect;
    private boolean mUninstallBadgeTouched;
    private UninstallBadgeUtils.UninstallType mUninstallType;
    private UserHandle mUser;

    public BadgeTextView(Context context) {
        super(context);
        this.mAppNotifierDrawer = AppNotifierDrawer.NULL;
        this.mAppNotifierCount = 0;
        this.mIconChangeBadgeDrawable = null;
        this.mUninstallBadgeDrawable = null;
        this.mUninstallType = null;
        this.mUninstallBadgeTouched = false;
        this.mUninstallBadgeRect = null;
        this.mNewBound = new Rect();
        this.mForceSetRect = false;
        this.mIsSetUnInstallBadgeDesc = false;
        this.mShortcutBadgeDrawable = null;
        this.mDataFreeDrawable = null;
        this.mLayoutHorizontal = false;
    }

    public BadgeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAppNotifierDrawer = AppNotifierDrawer.NULL;
        this.mAppNotifierCount = 0;
        this.mIconChangeBadgeDrawable = null;
        this.mUninstallBadgeDrawable = null;
        this.mUninstallType = null;
        this.mUninstallBadgeTouched = false;
        this.mUninstallBadgeRect = null;
        this.mNewBound = new Rect();
        this.mForceSetRect = false;
        this.mIsSetUnInstallBadgeDesc = false;
        this.mShortcutBadgeDrawable = null;
        this.mDataFreeDrawable = null;
        this.mLayoutHorizontal = false;
    }

    public BadgeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mAppNotifierDrawer = AppNotifierDrawer.NULL;
        this.mAppNotifierCount = 0;
        this.mIconChangeBadgeDrawable = null;
        this.mUninstallBadgeDrawable = null;
        this.mUninstallType = null;
        this.mUninstallBadgeTouched = false;
        this.mUninstallBadgeRect = null;
        this.mNewBound = new Rect();
        this.mForceSetRect = false;
        this.mIsSetUnInstallBadgeDesc = false;
        this.mShortcutBadgeDrawable = null;
        this.mDataFreeDrawable = null;
        this.mLayoutHorizontal = false;
    }

    @Override // android.view.View
    public void setTag(Object tag) {
        super.setTag(tag);
        if (tag == null || !(tag instanceof ItemInfo)) {
            return;
        }
        ItemInfo itemInfo = (ItemInfo) tag;
        try {
            Intent intent = itemInfo.getIntent();
            ComponentName component = intent.getComponent();
            if (component != null) {
                this.mRegisteredComponentName = component.getClassName();
                this.mUser = itemInfo.user;
                this.mComponentName = component;
                if (intent.hasExtra(EXTRA_EMAIL_ACCOUNT_ID)) {
                    int i = intent.getExtras().getInt(EXTRA_EMAIL_ACCOUNT_ID);
                    this.mRegisteredComponentName = this.mRegisteredComponentName + i;
                } else if (itemInfo.itemType == 6 && intent.hasExtra("shortcut_id")) {
                    String string = intent.getExtras().getString("shortcut_id");
                    if (!string.equals(DEFAULT_EMAIL_ACCOUT_ID)) {
                        this.mRegisteredComponentName = this.mRegisteredComponentName + string;
                    }
                }
                AppNotifierDrawer appNotifierDrawerRegisterAppNotifier = registerAppNotifier(this, new AppNotifierData(this.mComponentName.getPackageName(), this.mRegisteredComponentName, this.mUser));
                this.mAppNotifierDrawer = appNotifierDrawerRegisterAppNotifier;
                if (!appNotifierDrawerRegisterAppNotifier.isRegistered()) {
                    this.mAppNotifierDrawer = AppNotifierDrawer.NULL;
                }
            } else {
                this.mAppNotifierDrawer = AppNotifierDrawer.NULL;
            }
        } catch (RuntimeException e) {
            LGLog.i(TAG, String.format("setTag() : RuntimeException(%s), tag(%s)", e.toString(), itemInfo.toString()));
        }
        updateVisibilityForShortcutBadge(itemInfo);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        ComponentName componentName;
        super.onAttachedToWindow();
        if (Utilities.isSupportDataFreeApps() && (componentName = this.mComponentName) != null && Utilities.isDataFreeApp(componentName.getPackageName()) && GiftBoxManager.isProperToSupportDataFree(this.mContext)) {
            ComponentName componentName2 = this.mComponentName;
            if (componentName2 != null) {
                LGLog.i(TAG, "GiftBox DataFree ComponentName " + componentName2);
            }
            this.mDataFreeDrawable = Utilities.createDataFreeDrawable(this.mContext);
            this.mDataFreeDrawableTopMargin = this.mContext.getResources().getDimensionPixelSize(R.dimen.datafreeIcon_top_margin);
        } else {
            this.mDataFreeDrawable = null;
        }
        if (this.mRegisteredComponentName != null) {
            registerAppNotifier(this, new AppNotifierData(this.mComponentName.getPackageName(), this.mRegisteredComponentName, this.mUser));
        }
        DuplicatedApplicationChecker.addView(this);
        this.mForceSetRect = true;
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AppNotifierManager.getInstance(this.mContext).unregisterAppNotifier(this);
        DuplicatedApplicationChecker.removeView(this);
        this.mForceSetRect = false;
    }

    @Override // android.view.View
    public void setContentDescription(CharSequence contentDesc) {
        StringBuilder sb = new StringBuilder(contentDesc);
        String countDescription = AppNotifierManager.getInstance(this.mContext).getCountDescription(this.mAppNotifierCount);
        if (countDescription != null) {
            sb.append(", " + countDescription);
        }
        if (this.mUninstallBadgeDrawable != null) {
            sb.append(", " + getTalkbackStringForUninstallMode());
            super.setContentDescription(sb.toString());
            this.mIsSetUnInstallBadgeDesc = true;
        } else {
            this.mIsSetUnInstallBadgeDesc = false;
        }
        super.setContentDescription(sb.toString());
    }

    protected void drawBadge(Canvas canvas) {
        BitmapDrawable bitmapDrawable = this.mDataFreeDrawable;
        if (bitmapDrawable != null) {
            setBadgeLocationRect(bitmapDrawable, BadgeUtils.LocationType.TOP);
            this.mDataFreeDrawable.draw(canvas);
        }
        BitmapDrawable bitmapDrawable2 = this.mAppNotifierBadgeDrawable;
        if (bitmapDrawable2 != null) {
            setBadgeLocationRect(bitmapDrawable2, BadgeUtils.LocationType.TOP_RIGHT);
            this.mAppNotifierBadgeDrawable.draw(canvas);
        }
        drawUninstallBadge(canvas);
        BitmapDrawable bitmapDrawable3 = this.mIconChangeBadgeDrawable;
        if (bitmapDrawable3 != null) {
            setBadgeLocationRect(bitmapDrawable3, BadgeUtils.LocationType.TOP_RIGHT);
            this.mIconChangeBadgeDrawable.draw(canvas);
        }
        BitmapDrawable bitmapDrawable4 = this.mShortcutBadgeDrawable;
        if (bitmapDrawable4 != null) {
            setBadgeLocationRect(bitmapDrawable4, BadgeUtils.LocationType.BOTTOM_LEFT);
            this.mShortcutBadgeDrawable.draw(canvas);
        }
    }

    private void setBadgeLocationRect(BitmapDrawable badgeIcon, BadgeUtils.LocationType locationType) {
        int badgeLocationX = getBadgeLocationX(badgeIcon, locationType);
        int badgeLocationY = getBadgeLocationY(badgeIcon, locationType);
        badgeIcon.setBounds(badgeLocationX, badgeLocationY, badgeIcon.getIntrinsicWidth() + badgeLocationX, badgeIcon.getIntrinsicHeight() + badgeLocationY);
    }

    private int getBadgeLocationX(BitmapDrawable bitmapDrawable, BadgeUtils.LocationType locationType) {
        float f;
        float f2;
        int i;
        int i2;
        int iWidth;
        int i3;
        int scrollX = getScrollX();
        int width = getWidth();
        Drawable[] compoundDrawablesRelative = getCompoundDrawablesRelative();
        boolean z = this.mLayoutHorizontal;
        Drawable drawable = compoundDrawablesRelative[!z ? 1 : 0];
        int iWidth2 = (drawable == null || z) ? 0 : (width - drawable.getBounds().width()) / 2;
        int intrinsicWidth = bitmapDrawable.getIntrinsicWidth();
        if (this.mLayoutHorizontal) {
            f = intrinsicWidth;
            f2 = 0.35f;
        } else {
            f = intrinsicWidth;
            f2 = 0.25f;
        }
        int i4 = (int) (f * f2);
        int i5 = AnonymousClass1.$SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType[locationType.ordinal()];
        if (i5 != 1) {
            if (i5 == 2 || i5 == 3) {
                return this.mLayoutHorizontal ? scrollX : (scrollX + iWidth2) - i4;
            }
            if (i5 != 4) {
                return 0;
            }
            if (this.mLayoutHorizontal) {
                if (com.android.launcher3.Utilities.isRtl(getResources())) {
                    iWidth = (scrollX + width) - getResources().getDimensionPixelSize(R.dimen.workspace_icon_margin_start_land);
                    i3 = intrinsicWidth / 2;
                } else {
                    iWidth = drawable.getBounds().width() + scrollX + getResources().getDimensionPixelSize(R.dimen.workspace_icon_margin_start_land);
                    i3 = intrinsicWidth / 2;
                }
                i2 = iWidth - i3;
            } else {
                i2 = ((scrollX + width) - (iWidth2 + intrinsicWidth)) + i4;
            }
            i = scrollX + width;
            if (i2 + intrinsicWidth < i) {
                return i2;
            }
        } else {
            if (this.mLayoutHorizontal) {
                return scrollX + ((drawable.getBounds().width() - intrinsicWidth) / 2);
            }
            i = scrollX + (width / 2);
            intrinsicWidth /= 2;
        }
        return i - intrinsicWidth;
    }

    /* JADX INFO: renamed from: com.lge.launcher3.badge.BadgeTextView$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType;

        static {
            int[] iArr = new int[BadgeUtils.LocationType.values().length];
            $SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType = iArr;
            try {
                iArr[BadgeUtils.LocationType.TOP.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType[BadgeUtils.LocationType.TOP_LEFT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType[BadgeUtils.LocationType.BOTTOM_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType[BadgeUtils.LocationType.TOP_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:30:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int getBadgeLocationY(android.graphics.drawable.Drawable r8, com.lge.launcher3.badge.BadgeUtils.LocationType r9) {
        /*
            r7 = this;
            int r0 = r7.getScrollY()
            int r1 = r7.getPaddingTop()
            android.graphics.drawable.Drawable[] r2 = r7.getCompoundDrawablesRelative()
            boolean r3 = r7.mLayoutHorizontal
            r4 = 1
            r3 = r3 ^ r4
            r2 = r2[r3]
            r3 = 0
            if (r2 == 0) goto L1e
            android.graphics.Rect r2 = r2.getBounds()
            int r2 = r2.height()
            goto L1f
        L1e:
            r2 = r3
        L1f:
            int r8 = r8.getIntrinsicHeight()
            float r5 = (float) r8
            r6 = 1048576000(0x3e800000, float:0.25)
            float r5 = r5 * r6
            int r5 = (int) r5
            int[] r6 = com.lge.launcher3.badge.BadgeTextView.AnonymousClass1.$SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType
            int r9 = r9.ordinal()
            r9 = r6[r9]
            if (r9 == r4) goto L57
            r4 = 2
            if (r9 == r4) goto L50
            r4 = 3
            if (r9 == r4) goto L3c
            r8 = 4
            if (r9 == r8) goto L50
            goto L5c
        L3c:
            int r1 = r1 + r0
            int r1 = r1 + r2
            int r1 = r1 - r8
            int r3 = r1 + r5
            boolean r9 = r7.isTextTransparent()
            if (r9 != 0) goto L5c
            int r9 = r7.getExtendedPaddingTop()
            int r0 = r0 + r9
            int r0 = r0 - r8
            if (r3 <= r0) goto L5c
            goto L55
        L50:
            int r0 = r0 + r1
            int r0 = r0 - r5
            if (r0 >= 0) goto L55
            goto L5c
        L55:
            r3 = r0
            goto L5c
        L57:
            int r0 = r0 + r1
            int r8 = r7.mDataFreeDrawableTopMargin
            int r3 = r0 + r8
        L5c:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.badge.BadgeTextView.getBadgeLocationY(android.graphics.drawable.Drawable, com.lge.launcher3.badge.BadgeUtils$LocationType):int");
    }

    public boolean isTextTransparent() {
        return getCurrentTextColor() == getResources().getColor(android.R.color.transparent);
    }

    public AppNotifierDrawer registerAppNotifier(IAppNotifierView view, AppNotifierData appData) {
        return AppNotifierManager.getInstance(this.mContext).registerAppNotifier(view, new AppNotifierData(this.mComponentName.getPackageName(), this.mRegisteredComponentName, this.mUser));
    }

    @Override // com.lge.launcher3.badge.appnotifier.IAppNotifierView
    public void onUpdateAppNotifier(int count) {
        this.mAppNotifierCount = count;
        this.mAppNotifierBadgeDrawable = this.mAppNotifierDrawer.createBadgeDrawable(getContext(), count);
        setContentDescription(getText());
        invalidate();
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public void setUninstallType(UninstallBadgeUtils.UninstallType uninstallType) {
        this.mUninstallType = uninstallType;
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public UninstallBadgeUtils.UninstallType getUninstallType() {
        return this.mUninstallType;
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public boolean isUninstallable() {
        return this.mUninstallType != null;
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public boolean hasUnistallBadge() {
        LGLog.d(TAG, "BadgeTextView ");
        return this.mUninstallBadgeDrawable != null;
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public boolean isUninstallableAllApps() {
        return UninstallBadgeUtils.UninstallType.DISABLE.equals(this.mUninstallType) || UninstallBadgeUtils.UninstallType.UNINSTALL.equals(this.mUninstallType);
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public void setVisibilityForUninstallBadge(boolean visible, int delay) {
        if ((!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || Launcher.getLauncher(getContext()).isAllAppsVisible()) && !BadgeUtils.isChanged(this.mUninstallBadgeDrawable, visible)) {
            return;
        }
        this.mUninstallBadgeDrawable = visible ? UninstallBadgeUtils.createUninstallBadgeDrawable(getContext()) : null;
        invalidate();
        if (visible) {
            return;
        }
        ItemInfo itemInfo = (ItemInfo) getTag();
        if (itemInfo.contentDescription != null) {
            setContentDescription(itemInfo.contentDescription);
        }
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public boolean isTouchedUninstallBadge() {
        return this.mUninstallBadgeTouched;
    }

    private String getTalkbackStringForUninstallMode() {
        ItemInfo itemInfo = (ItemInfo) getTag();
        return getResources().getString((itemInfo.itemType != 0 || ButtonDropTargetUtils.isShortcutWithApplicationType(this.mContext, itemInfo)) ? R.string.talkback_remove_message : R.string.talkback_uninstall_message);
    }

    private void updateVisibilityForShortcutBadge(ItemInfo itemInfo) {
        Context context = getContext();
        boolean value = LGHomeFeature.Config.FEATURE_SHORTCUT_BADGE_ENABLE.getValue();
        boolean zEquals = LGFeatureConfig.FEATURE_OPERATOR.equals("VZW");
        boolean z = true;
        boolean z2 = itemInfo.user != null && itemInfo.user.getIdentifier() == UserHandle.myUserId();
        if (itemInfo.itemType != 1 && !ButtonDropTargetUtils.isShortcutWithApplicationType(context, itemInfo)) {
            z = false;
        }
        if (value && !zEquals && z2 && z && LGHomeFeature.isEnableDefaultHome() && this.mShortcutBadgeDrawable == null) {
            this.mShortcutBadgeDrawable = BadgeUtils.createShortcutBadgeDrawable(context);
        }
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        this.mUninstallBadgeTouched = UninstallBadgeUtils.getUninstallBadgeTouched(event, getContext(), this.mUninstallBadgeDrawable, this.mUninstallBadgeRect, getScrollX());
        return super.dispatchTouchEvent(event);
    }

    public void invalidateDataFreeBadge() {
        ComponentName componentName;
        if (Utilities.isSupportDataFreeApps() && (componentName = this.mComponentName) != null && Utilities.isDataFreeApp(componentName.getPackageName()) && GiftBoxManager.isProperToSupportDataFree(this.mContext)) {
            this.mDataFreeDrawable = Utilities.createDataFreeDrawable(this.mContext);
            this.mDataFreeDrawableTopMargin = this.mContext.getResources().getDimensionPixelSize(R.dimen.datafreeIcon_top_margin);
        } else {
            this.mDataFreeDrawable = null;
        }
        invalidate();
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public void invalidateUninstallBadge(boolean visible, boolean enableAni) {
        if (!isAttachedToWindow() && !UninstallModeManager.getInstance(getContext()).isPowerSaveMode()) {
            this.mEnableAni = false;
            return;
        }
        this.mEnableAni = enableAni;
        if (visible) {
            if (this.mUninstallBadgeDrawable == null) {
                setVisibilityForUninstallBadge(visible, 0);
            }
            if (!this.mIsSetUnInstallBadgeDesc) {
                setContentDescription(getText());
            }
            if (enableAni && getVisibility() == 0) {
                Rect rect = this.mUninstallBadgeRect;
                if (rect != null) {
                    invalidate(rect.left, this.mUninstallBadgeRect.top, this.mUninstallBadgeRect.right, this.mUninstallBadgeRect.bottom);
                } else {
                    invalidate();
                }
            }
        }
    }

    private void setUninstallBadgeLocationRect(boolean forceSetRect) {
        Rect rect;
        if (this.mUninstallBadgeDrawable == null) {
            this.mUninstallBadgeDrawable = UninstallBadgeUtils.createUninstallBadgeDrawable(getContext());
        }
        if (forceSetRect || (rect = this.mUninstallBadgeRect) == null || rect.width() == 0) {
            int badgeLocationX = getBadgeLocationX(this.mUninstallBadgeDrawable, BadgeUtils.LocationType.TOP_LEFT) + ((int) getResources().getDimension(R.dimen.uninstall_badge_left_margin));
            int badgeLocationY = getBadgeLocationY(this.mUninstallBadgeDrawable, BadgeUtils.LocationType.TOP_LEFT);
            if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && !Launcher.getLauncher(getContext()).isAllAppsVisible()) {
                badgeLocationY = getBadgeLocationY(this.mUninstallBadgeDrawable, BadgeUtils.LocationType.TOP_LEFT) + ((int) getResources().getDimension(R.dimen.swivel_uninstall_badge_top_margin));
            }
            this.mUninstallBadgeRect = new Rect(badgeLocationX, badgeLocationY, this.mUninstallBadgeDrawable.getIntrinsicWidth() + badgeLocationX, this.mUninstallBadgeDrawable.getIntrinsicHeight() + badgeLocationY);
            this.mForceSetRect = false;
        }
        this.mUninstallBadgeDrawable.setBounds(this.mUninstallBadgeRect.left, this.mUninstallBadgeRect.top, this.mUninstallBadgeRect.right, this.mUninstallBadgeRect.bottom);
    }

    private void drawUninstallBadge(Canvas canvas) {
        if (this.mUninstallBadgeDrawable != null) {
            setUninstallBadgeLocationRect(this.mForceSetRect);
            if (this.mEnableAni) {
                int rangeOfUninstallBadge = UninstallBadgeUtils.getRangeOfUninstallBadge();
                Rect bounds = this.mUninstallBadgeDrawable.getBounds();
                this.mNewBound = bounds;
                bounds.set(bounds.left + rangeOfUninstallBadge, this.mNewBound.top + rangeOfUninstallBadge, this.mNewBound.right - rangeOfUninstallBadge, this.mNewBound.bottom - rangeOfUninstallBadge);
                this.mUninstallBadgeDrawable.setBounds(this.mNewBound);
            }
            this.mUninstallBadgeDrawable.draw(canvas);
        }
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public boolean isInFolder() {
        return ((ItemInfo) getTag()).container > 0;
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public void getGlobalVisibleRectForBadge(Rect r) {
        getGlobalVisibleRect(r);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mForceSetRect = true;
    }
}
