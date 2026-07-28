package com.lge.launcher3.badge;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.BadgeUtils;
import com.lge.launcher3.badge.uninstall.IUninstallBadgeView;
import com.lge.launcher3.badge.uninstall.UninstallBadgeUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.lgewidgetlib.LgeAppWidgetHostView;

/* JADX INFO: loaded from: classes.dex */
public class BadgeAppWidgetHostView extends LgeAppWidgetHostView implements IUninstallBadgeView {
    public static final String TAG = "BadgeAppWidgetHostView";
    private boolean mEnableAni;
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

    public BadgeAppWidgetHostView(Context context) {
        super(context);
        this.mUninstallBadgeDrawable = null;
        this.mUninstallType = null;
        this.mUninstallBadgeTouched = false;
        this.mUninstallBadgeRect = new Rect();
        this.mNewBound = new Rect();
        this.mIsSetUnInstallBadgeDesc = false;
    }

    @Override // android.appwidget.AppWidgetHostView, android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawUninstallBadge(canvas);
    }

    private void setBadgeLocationRect(BitmapDrawable badgeIcon, BadgeUtils.LocationType locationType) {
        int badgeLocationX = getBadgeLocationX(badgeIcon, locationType);
        int badgeLocationY = getBadgeLocationY(badgeIcon);
        badgeIcon.setBounds(badgeLocationX, badgeLocationY, badgeIcon.getIntrinsicWidth() + badgeLocationX, badgeIcon.getIntrinsicHeight() + badgeLocationY);
    }

    private int getBadgeLocationX(BitmapDrawable badgeIcon, BadgeUtils.LocationType locationType) {
        int intrinsicWidth = badgeIcon.getIntrinsicWidth();
        int i = (int) (intrinsicWidth / 3.0f);
        int i2 = AnonymousClass1.$SwitchMap$com$lge$launcher3$badge$BadgeUtils$LocationType[locationType.ordinal()];
        if (i2 == 1) {
            return i;
        }
        if (i2 != 2) {
            return 0;
        }
        return getWidth() - (intrinsicWidth + i);
    }

    /* JADX INFO: renamed from: com.lge.launcher3.badge.BadgeAppWidgetHostView$1, reason: invalid class name */
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

    public int getBadgeLocationY(BitmapDrawable badgeIcon) {
        return (int) (badgeIcon.getIntrinsicWidth() / 3.0f);
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
    public boolean isUninstallableAllApps() {
        return UninstallBadgeUtils.UninstallType.DISABLE.equals(this.mUninstallType) || UninstallBadgeUtils.UninstallType.UNINSTALL.equals(this.mUninstallType);
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public boolean hasUnistallBadge() {
        LGLog.d(TAG, "BadgeAppWidgetHostView ");
        return this.mUninstallBadgeDrawable != null;
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public void setVisibilityForUninstallBadge(boolean visible, int delay) {
        if (BadgeUtils.isChanged(this.mUninstallBadgeDrawable, visible)) {
            this.mUninstallBadgeDrawable = visible ? UninstallBadgeUtils.createUninstallBadgeDrawable(getContext()) : null;
            setClickable(visible);
            invalidate();
            setContentDescriptionForUninstallMode(visible);
        }
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public boolean isTouchedUninstallBadge() {
        return this.mUninstallBadgeTouched;
    }

    private void setContentDescriptionForUninstallMode(boolean visible) {
        AppWidgetHostView hostView = ((LauncherAppWidgetInfo) getTag()).getHostView();
        if (hostView == null) {
            return;
        }
        for (int i = 0; i <= hostView.getChildCount(); i++) {
            View childAt = hostView.getChildAt(i);
            if (childAt != null) {
                childAt.setImportantForAccessibility(visible ? 4 : 0);
            }
        }
        CharSequence contentDescription = hostView.getContentDescription();
        StringBuilder sb = new StringBuilder();
        if (contentDescription != null) {
            if (visible && !this.mIsSetUnInstallBadgeDesc) {
                sb.append(contentDescription);
                sb.append("," + getResources().getString(R.string.talkback_remove_message));
                setContentDescription(sb);
                this.mIsSetUnInstallBadgeDesc = true;
                return;
            }
            setContentDescription(contentDescription.toString().split(",")[0]);
            this.mIsSetUnInstallBadgeDesc = false;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        this.mUninstallBadgeTouched = UninstallBadgeUtils.getUninstallBadgeTouched(event, getContext(), this.mUninstallBadgeDrawable, this.mUninstallBadgeRect);
        return super.dispatchTouchEvent(event);
    }

    @Override // com.lge.launcher3.badge.uninstall.IUninstallBadgeView
    public void invalidateUninstallBadge(boolean visible, boolean enableAni) {
        if (!isAttachedToWindow()) {
            this.mEnableAni = false;
            return;
        }
        this.mEnableAni = enableAni;
        if (visible) {
            if (this.mUninstallBadgeDrawable == null) {
                setVisibilityForUninstallBadge(visible, 0);
            }
            if (!this.mIsSetUnInstallBadgeDesc) {
                setContentDescriptionForUninstallMode(visible);
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

    private void setUninstallBadgeLocationRect() {
        if (this.mUninstallBadgeDrawable == null) {
            this.mUninstallBadgeDrawable = UninstallBadgeUtils.createUninstallBadgeDrawable(getContext());
        }
        Rect rect = this.mUninstallBadgeRect;
        if (rect == null || rect.width() == 0) {
            int badgeLocationX = getBadgeLocationX(this.mUninstallBadgeDrawable, BadgeUtils.LocationType.TOP_LEFT);
            int badgeLocationY = getBadgeLocationY(this.mUninstallBadgeDrawable);
            this.mUninstallBadgeRect = new Rect(badgeLocationX, badgeLocationY, this.mUninstallBadgeDrawable.getIntrinsicWidth() + badgeLocationX, this.mUninstallBadgeDrawable.getIntrinsicHeight() + badgeLocationY);
        }
        UninstallBadgeUtils.setUninstallBadgeRect(this.mUninstallBadgeRect);
        this.mUninstallBadgeDrawable.setBounds(this.mUninstallBadgeRect.left, this.mUninstallBadgeRect.top, this.mUninstallBadgeRect.right, this.mUninstallBadgeRect.bottom);
    }

    private void drawUninstallBadge(Canvas canvas) {
        if (this.mUninstallBadgeDrawable != null) {
            setUninstallBadgeLocationRect();
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
    public void getGlobalVisibleRectForBadge(Rect r) {
        getGlobalVisibleRect(r);
    }
}
