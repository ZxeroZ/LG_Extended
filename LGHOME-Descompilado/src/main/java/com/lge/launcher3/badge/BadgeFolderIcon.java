package com.lge.launcher3.badge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.BadgeUtils;
import com.lge.launcher3.badge.appnotifier.AppNotifierData;
import com.lge.launcher3.badge.appnotifier.AppNotifierDrawer;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.badge.appnotifier.IAppNotifierGroup;
import com.lge.launcher3.badge.uninstall.IUninstallBadgeView;
import com.lge.launcher3.badge.uninstall.UninstallBadgeUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class BadgeFolderIcon extends FrameLayout implements IUninstallBadgeView {
    private static final String TAG = "BadgeFolderIcon";
    private BitmapDrawable mAppNotifierDrawable;
    protected AppNotifierDrawer mAppNotifierDrawer;
    private int mBadgeCount;
    private boolean mDrawAppNotifier;
    private boolean mEnableAni;
    private boolean mForceSetRect;
    private boolean mIsSetUnInstallBadgeDesc;
    private Rect mNewBound;
    private BitmapDrawable mUninstallBadgeDrawable;
    private Rect mUninstallBadgeRect;
    private boolean mUninstallBadgeTouched;
    private UninstallBadgeUtils.UninstallType mUninstallType;

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public boolean isInFolder() {
        return false;
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public boolean isUninstallableAllApps() {
        return false;
    }

    public BadgeFolderIcon(Context context) {
        this(context, null);
    }

    public BadgeFolderIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAppNotifierDrawer = AppNotifierDrawer.NULL;
        this.mAppNotifierDrawable = null;
        this.mBadgeCount = 0;
        this.mDrawAppNotifier = true;
        this.mUninstallBadgeDrawable = null;
        this.mUninstallType = null;
        this.mUninstallBadgeTouched = false;
        this.mUninstallBadgeRect = null;
        this.mNewBound = new Rect();
        this.mForceSetRect = false;
        this.mIsSetUnInstallBadgeDesc = false;
    }

    public BadgeFolderIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAppNotifierDrawer = AppNotifierDrawer.NULL;
        this.mAppNotifierDrawable = null;
        this.mBadgeCount = 0;
        this.mDrawAppNotifier = true;
        this.mUninstallBadgeDrawable = null;
        this.mUninstallType = null;
        this.mUninstallBadgeTouched = false;
        this.mUninstallBadgeRect = null;
        this.mNewBound = new Rect();
        this.mForceSetRect = false;
        this.mIsSetUnInstallBadgeDesc = false;
    }

    protected ArrayList<String> getContentsByString(FolderInfo info) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (ShortcutInfo shortcutInfo : info.contents) {
            if (shortcutInfo.getTargetComponent() != null && shortcutInfo.getTargetComponent().getClassName() != null) {
                String className = shortcutInfo.getTargetComponent().getClassName();
                Intent intent = shortcutInfo.getIntent();
                if (intent != null) {
                    if (intent.hasExtra("AccountId")) {
                        className = className + intent.getExtras().getInt("AccountId");
                    } else if (shortcutInfo.itemType == 6 && intent.hasExtra("shortcut_id")) {
                        String string = intent.getExtras().getString("shortcut_id");
                        if (!string.equals(BadgeTextView.DEFAULT_EMAIL_ACCOUT_ID)) {
                            className = className + string;
                        }
                    }
                }
                if (shortcutInfo.user != null && shortcutInfo.user != null && !ManagedProfileUtils.hasProfileOwnerAsUser(getContext(), shortcutInfo.user.getIdentifier())) {
                    arrayList.add(className);
                }
            }
        }
        return arrayList;
    }

    protected ArrayList<AppNotifierData> getAppNotifierDatas(FolderInfo info) {
        ArrayList<AppNotifierData> arrayList = new ArrayList<>();
        for (ShortcutInfo shortcutInfo : info.contents) {
            if (shortcutInfo.getTargetComponent() != null && shortcutInfo.getTargetComponent().getClassName() != null) {
                String className = shortcutInfo.getTargetComponent().getClassName();
                Intent intent = shortcutInfo.getIntent();
                if (intent != null) {
                    if (intent.hasExtra("AccountId")) {
                        className = className + intent.getExtras().getInt("AccountId");
                    } else if (shortcutInfo.itemType == 6 && intent.hasExtra("shortcut_id")) {
                        String string = intent.getExtras().getString("shortcut_id");
                        if (!string.equals(BadgeTextView.DEFAULT_EMAIL_ACCOUT_ID)) {
                            className = className + string;
                        }
                    }
                }
                arrayList.add(new AppNotifierData(shortcutInfo.getTargetComponent().getPackageName(), className, shortcutInfo.user));
            }
        }
        return arrayList;
    }

    @Override // android.view.View
    public void setContentDescription(CharSequence contentDesc) {
        StringBuilder sb = new StringBuilder(contentDesc);
        String countDescription = AppNotifierManager.getInstance(this.mContext).getCountDescription(this.mBadgeCount);
        if (countDescription != null) {
            sb.append(", " + countDescription);
        }
        super.setContentDescription(sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AppNotifierDrawer registerAppNotifier(IAppNotifierGroup view, ArrayList<AppNotifierData> components) {
        return AppNotifierManager.getInstance(getContext()).registerAppNotifierGroup(view, components);
    }

    protected void onUpdateAppNotifier(int count, TextView folderName) {
        this.mBadgeCount = count;
        this.mAppNotifierDrawable = this.mAppNotifierDrawer.createBadgeDrawable(this.mContext, count);
        if (folderName.length() > 0) {
            CharSequence text = folderName.getText();
            setContentDescription(((Object) text) + this.mContext.getString(R.string.folder_name));
        } else {
            setContentDescription(this.mContext.getString(R.string.folder_hint_text));
        }
        invalidate();
    }

    public void setDrawAppNotifier(boolean draw) {
        this.mDrawAppNotifier = draw;
    }

    public void drawBadge(Canvas canvas, ImageView previewBackground) {
        BitmapDrawable bitmapDrawable = this.mAppNotifierDrawable;
        if (bitmapDrawable != null && this.mDrawAppNotifier) {
            setNotifierRect(bitmapDrawable, previewBackground);
            this.mAppNotifierDrawable.draw(canvas);
            LGLog.i(TAG, "drawBadge() : " + this.mAppNotifierDrawable.getBounds() + ": " + getTag());
        }
        if (LGHomeFeature.isEnableDefaultHome()) {
            return;
        }
        drawUninstallBadge(canvas, previewBackground);
    }

    private void setNotifierRect(BitmapDrawable badgeIcon, ImageView previewBackground) {
        int badgeLocationX = getBadgeLocationX(badgeIcon, previewBackground, BadgeUtils.LocationType.TOP_RIGHT);
        int badgeLocationY = getBadgeLocationY(badgeIcon, previewBackground);
        badgeIcon.setBounds(badgeLocationX, badgeLocationY, badgeIcon.getIntrinsicWidth() + badgeLocationX, badgeIcon.getIntrinsicHeight() + badgeLocationY);
    }

    private int getBadgeLocationX(Drawable badgeIcon, ImageView previewBackground, BadgeUtils.LocationType locationType) {
        float f;
        float f2;
        int dimensionPixelSize;
        int scrollX = getScrollX();
        int width = getWidth();
        DeviceProfile deviceProfile = ((Launcher) this.mContext).getDeviceProfile();
        int i = LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() ? deviceProfile.allAppsIconSizePx : deviceProfile.iconSizePx;
        boolean z = deviceProfile.isLandscape && deviceProfile.allowRotation && deviceProfile.cellLayoutHorizontal && !LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue();
        int i2 = z ? 0 : (width - i) / 2;
        int intrinsicWidth = badgeIcon.getIntrinsicWidth();
        if (z) {
            f = intrinsicWidth;
            f2 = 0.35f;
        } else {
            f = intrinsicWidth;
            f2 = 0.25f;
        }
        int i3 = (int) (f * f2);
        int i4 = AnonymousClass1.$SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType[locationType.ordinal()];
        if (i4 == 1) {
            return z ? scrollX : (scrollX + i2) - i3;
        }
        if (i4 != 2) {
            return 0;
        }
        if (!z) {
            dimensionPixelSize = ((scrollX + width) - (i2 + intrinsicWidth)) + i3;
        } else if (Utilities.isRtl(getResources())) {
            dimensionPixelSize = ((scrollX + width) - getResources().getDimensionPixelSize(R.dimen.workspace_icon_margin_start_land)) - (intrinsicWidth / 2);
        } else {
            dimensionPixelSize = ((i + scrollX) + getResources().getDimensionPixelSize(R.dimen.workspace_icon_margin_start_land)) - (intrinsicWidth / 2);
        }
        int i5 = scrollX + width;
        return dimensionPixelSize + intrinsicWidth >= i5 ? i5 - intrinsicWidth : dimensionPixelSize;
    }

    /* JADX INFO: renamed from: com.lge.launcher3.badge.BadgeFolderIcon$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType;

        static {
            int[] iArr = new int[BadgeUtils.LocationType.values().length];
            $SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType = iArr;
            try {
                iArr[BadgeUtils.LocationType.TOP_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType[BadgeUtils.LocationType.TOP_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private int getBadgeLocationY(Drawable badgeIcon, ImageView previewBackground) {
        if (previewBackground == null) {
            return getScrollY();
        }
        Object tag = getTag();
        if (tag == null || !(tag instanceof ItemInfo)) {
            return getScrollY();
        }
        int scrollY = (getScrollY() + getPaddingTop()) - (badgeIcon.getIntrinsicHeight() / 4);
        return scrollY >= getScrollY() ? scrollY : getScrollY();
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public void setUninstallType(UninstallBadgeUtils.UninstallType uninstallType) {
        if (LGHomeFeature.isEnableDefaultHome()) {
            this.mUninstallType = null;
        } else {
            this.mUninstallType = uninstallType;
        }
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
        LGLog.d(TAG, "BadgeFolderIcon ");
        return this.mUninstallBadgeDrawable != null;
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public void setVisibilityForUninstallBadge(boolean visible, int delay) {
        if (!BadgeUtils.isChanged(this.mUninstallBadgeDrawable, visible) || LGHomeFeature.isEnableDefaultHome()) {
            return;
        }
        this.mUninstallBadgeDrawable = visible ? UninstallBadgeUtils.createUninstallBadgeDrawable(getContext()) : null;
        invalidate();
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public void invalidateUninstallBadge(boolean visible, boolean enableAni) {
        if (!isAttachedToWindow() || LGHomeFeature.isEnableDefaultHome()) {
            this.mEnableAni = false;
            return;
        }
        this.mEnableAni = enableAni;
        if (visible) {
            if (this.mUninstallBadgeDrawable == null) {
                setVisibilityForUninstallBadge(visible, 0);
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

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public boolean isTouchedUninstallBadge() {
        return this.mUninstallBadgeTouched;
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public void getGlobalVisibleRectForBadge(Rect r) {
        getGlobalVisibleRect(r);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        this.mUninstallBadgeTouched = UninstallBadgeUtils.getUninstallBadgeTouched(event, getContext(), this.mUninstallBadgeDrawable, this.mUninstallBadgeRect);
        return super.dispatchTouchEvent(event);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mForceSetRect = true;
    }

    private void drawUninstallBadge(Canvas canvas, ImageView previewBackground) {
        if (this.mUninstallBadgeDrawable != null) {
            setUninstallBadgeLocationRect(this.mForceSetRect, previewBackground);
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

    private void setUninstallBadgeLocationRect(boolean forceSetRect, ImageView previewBackground) {
        Rect rect;
        if (this.mUninstallBadgeDrawable == null) {
            BitmapDrawable bitmapDrawableCreateUninstallBadgeDrawable = UninstallBadgeUtils.createUninstallBadgeDrawable(getContext());
            this.mUninstallBadgeDrawable = bitmapDrawableCreateUninstallBadgeDrawable;
            if (bitmapDrawableCreateUninstallBadgeDrawable == null) {
                LGLog.d(TAG, "fail setUninstallBadgeLocationRect because mUninstallBadgeDrawable is null");
                return;
            }
        }
        if (forceSetRect || (rect = this.mUninstallBadgeRect) == null || rect.width() == 0) {
            int badgeLocationX = getBadgeLocationX(this.mUninstallBadgeDrawable, previewBackground, BadgeUtils.LocationType.TOP_LEFT) + ((int) getResources().getDimension(R.dimen.uninstall_badge_left_margin));
            int badgeLocationY = getBadgeLocationY(this.mUninstallBadgeDrawable, previewBackground);
            this.mUninstallBadgeRect = new Rect(badgeLocationX, badgeLocationY, this.mUninstallBadgeDrawable.getIntrinsicWidth() + badgeLocationX, this.mUninstallBadgeDrawable.getIntrinsicHeight() + badgeLocationY);
            this.mForceSetRect = false;
        }
        this.mUninstallBadgeDrawable.setBounds(this.mUninstallBadgeRect.left, this.mUninstallBadgeRect.top, this.mUninstallBadgeRect.right, this.mUninstallBadgeRect.bottom);
    }
}
