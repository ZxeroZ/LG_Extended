package com.android.launcher3;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import com.android.launcher3.CellLayout;
import com.android.launcher3.folder.FolderIcon;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsPagedCellLayout;
import com.lge.launcher3.screeneffect.ScreenEffectManager;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutAndWidgetContainer extends ViewGroup {
    static final String TAG = "CellLayoutChildren";
    private int mCellHeight;
    private int mCellWidth;
    private boolean mCenterContent;
    private int mCountX;
    private int mCountY;
    private int mHeightGap;
    private boolean mInvertIfRtl;
    private boolean mIsHotseatLayout;
    private Launcher mLauncher;
    private final int[] mTmpCellXY;
    private final WallpaperManager mWallpaperManager;
    private int mWidthGap;

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public ShortcutAndWidgetContainer(Context context) {
        super(context);
        this.mTmpCellXY = new int[2];
        this.mInvertIfRtl = false;
        this.mLauncher = (Launcher) context;
        this.mWallpaperManager = WallpaperManager.getInstance(context);
    }

    public void setCellDimensions(int cellWidth, int cellHeight, int widthGap, int heightGap, int countX, int countY) {
        this.mCellWidth = cellWidth;
        this.mCellHeight = cellHeight;
        this.mWidthGap = widthGap;
        this.mHeightGap = heightGap;
        this.mCountX = countX;
        this.mCountY = countY;
    }

    public View getChildAt(int x, int y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) childAt.getLayoutParams();
            if (layoutParams.cellX <= x && x < layoutParams.cellX + layoutParams.cellHSpan && layoutParams.cellY <= y && y < layoutParams.cellY + layoutParams.cellVSpan) {
                return childAt;
            }
        }
        return null;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
            ScreenEffectManager.showChildBounds(canvas, this, SupportMenu.CATEGORY_MASK, 5, true);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() != 8) {
                measureChild(childAt);
            }
        }
    }

    public void setupLp(CellLayout.LayoutParams lp) {
        lp.setup(this.mCellWidth, this.mCellHeight, this.mWidthGap, this.mHeightGap, invertLayoutHorizontally(), this.mCountX);
    }

    public void setInvertIfRtl(boolean invert) {
        this.mInvertIfRtl = invert;
    }

    public void setIsHotseat(boolean isHotseat) {
        this.mIsHotseatLayout = isHotseat;
    }

    public int getCellContentWidth() {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        return Math.min(getMeasuredHeight(), this.mIsHotseatLayout ? deviceProfile.hotseatCellWidthPx : deviceProfile.cellWidthPx);
    }

    public int getCellContentHeight() {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        return Math.min(getMeasuredHeight(), this.mIsHotseatLayout ? deviceProfile.hotseatCellHeightPx : deviceProfile.cellHeightPx);
    }

    public void measureChild(View child) {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        int i = this.mCellWidth;
        int i2 = this.mCellHeight;
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) child.getLayoutParams();
        if (!layoutParams.isFullscreen) {
            layoutParams.setup(i, i2, this.mWidthGap, this.mHeightGap, invertLayoutHorizontally(), this.mCountX);
            if (!(child instanceof LauncherAppWidgetHostView)) {
                int iMax = (deviceProfile.isLandscape && deviceProfile.allowRotation && deviceProfile.cellLayoutHorizontal) ? 0 : (int) Math.max(0.0f, (layoutParams.height - getCellContentHeight()) / 2.0f);
                int dimensionPixelSize = (deviceProfile.isLandscape && deviceProfile.allowRotation && deviceProfile.cellLayoutHorizontal) ? getResources().getDimensionPixelSize(R.dimen.bubbleTextView_and_folderIcon_CellPaddingX) : (int) (deviceProfile.edgeMarginPx / 2.0f);
                child.setPadding(dimensionPixelSize, iMax, dimensionPixelSize, 0);
            }
        } else {
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.width = getMeasuredWidth();
            layoutParams.height = getMeasuredHeight();
        }
        child.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824));
    }

    public boolean invertLayoutHorizontally() {
        return this.mInvertIfRtl && Utilities.isRtl(getResources());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() != 8) {
                CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) childAt.getLayoutParams();
                int i2 = layoutParams.x;
                int i3 = layoutParams.y;
                if ((getParent() instanceof AllAppsPagedCellLayout) && ((layoutParams.isLockedToGrid && childAt.getTranslationX() != 0.0f) || childAt.getTranslationY() != 0.0f)) {
                    LGLog.d(TAG, "reset translation " + childAt + ", (" + childAt.getTranslationX() + ", " + childAt.getTranslationY() + ")");
                    childAt.setTranslationX(0.0f);
                    childAt.setTranslationY(0.0f);
                }
                childAt.layout(i2, i3, layoutParams.width + i2, layoutParams.height + i3);
                if (layoutParams.dropped) {
                    layoutParams.dropped = false;
                    int[] iArr = this.mTmpCellXY;
                    getLocationOnScreen(iArr);
                    this.mWallpaperManager.sendWallpaperCommand(getWindowToken(), "android.home.drop", iArr[0] + i2 + (layoutParams.width / 2), iArr[1] + i3 + (layoutParams.height / 2), 0, null);
                }
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (child != null) {
            Rect rect = new Rect();
            child.getDrawingRect(rect);
            requestRectangleOnScreen(rect);
        }
    }

    @Override // android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).cancelLongPress();
        }
    }

    @Override // android.view.ViewGroup
    protected void setChildrenDrawingCacheEnabled(boolean enabled) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            childAt.setDrawingCacheEnabled(enabled);
            if (!childAt.isHardwareAccelerated() && enabled) {
                childAt.buildDrawingCache(true);
            }
        }
    }

    @Override // android.view.ViewGroup
    protected void setChildrenDrawnWithCacheEnabled(boolean enabled) {
        super.setChildrenDrawnWithCacheEnabled(enabled);
    }

    public void enableCenteredContent(boolean enabled) {
        this.mCenterContent = enabled;
    }

    public boolean isLayoutHorizontal(int cellX, int cellY) {
        return isLayoutHorizontal(getChildAt(cellX, cellY));
    }

    public boolean isLayoutHorizontal(View view) {
        if (view instanceof BubbleTextView) {
            return ((BubbleTextView) view).isLayoutHorizontal();
        }
        if (view instanceof FolderIcon) {
            return ((FolderIcon) view).isLayoutHorizontal();
        }
        return (view instanceof TextView) && ((TextView) view).getCompoundDrawables()[0] != null;
    }
}
