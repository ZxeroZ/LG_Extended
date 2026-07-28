package com.lge.launcher3;

import android.content.Context;
import android.graphics.PorterDuff;
import com.android.launcher3.AppWidgetResizeFrame;
import com.android.launcher3.CellLayout;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.dragndrop.DragLayer;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class LGAppWidgetResizeFrame extends AppWidgetResizeFrame {
    private static final String BLOCK_TAG = "widget_resize_frame";
    public static final String TAG = "LGAppWidgetResizeFrame";
    private int cellX;
    private int cellXInc;
    private int cellY;
    private int cellYInc;
    private int countX;
    private int countY;
    private int hSpanDelta;
    private int hSpanInc;
    protected int mMaxHSpan;
    protected int mMaxVSpan;
    private int spanX;
    private int spanY;
    private int vSpanDelta;
    private int vSpanInc;

    public LGAppWidgetResizeFrame(Context context, LauncherAppWidgetHostView widgetView, CellLayout cellLayout, DragLayer dragLayer) {
        super(context, widgetView, cellLayout, dragLayer);
        this.hSpanInc = 0;
        this.vSpanInc = 0;
        this.cellXInc = 0;
        this.cellYInc = 0;
        this.countX = 0;
        this.countY = 0;
        this.spanX = 0;
        this.spanY = 0;
        this.cellX = 0;
        this.cellY = 0;
        this.hSpanDelta = 0;
        this.vSpanDelta = 0;
        this.mMaxHSpan = Integer.MAX_VALUE;
        this.mMaxVSpan = Integer.MAX_VALUE;
        setContentDescription(getContext().getString(R.string.lg_workspace_talkback_resizeframe));
        applyOriginalColor();
        if (LGHomeFeature.Config.FEATURE_USE_WIDGET_MAX_SPAN.getValue() || Utilities.isLGUI10_0()) {
            LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) widgetView.getAppWidgetInfo();
            this.mMaxHSpan = launcherAppWidgetProviderInfo.getMaxSpanX();
            this.mMaxVSpan = launcherAppWidgetProviderInfo.getMaxSpanY();
        }
    }

    @Override // com.android.launcher3.AppWidgetResizeFrame
    public void onTouchUp() {
        super.onTouchUp();
        applyOriginalColor();
    }

    private void clear() {
        this.hSpanInc = 0;
        this.vSpanInc = 0;
        this.cellXInc = 0;
        this.cellYInc = 0;
        this.countX = 0;
        this.countY = 0;
        this.spanX = 0;
        this.spanY = 0;
        this.cellX = 0;
        this.cellY = 0;
        this.hSpanDelta = 0;
        this.vSpanDelta = 0;
        this.mDirectionVector[0] = 0;
        this.mDirectionVector[1] = 0;
        applyOriginalColor();
    }

    @Override // com.android.launcher3.AppWidgetResizeFrame, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mLauncher.setOneHandOperation(false, BLOCK_TAG);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLauncher.setOneHandOperation(true, BLOCK_TAG);
    }

    @Override // com.android.launcher3.AppWidgetResizeFrame
    protected void resizeWidgetIfNeeded(boolean onDismiss) {
        clear();
        float shirinkFactor = UninstallModeManager.getInstance(this.mContext).getShirinkFactor(this.mLauncher.getWorkspace());
        int cellWidth = (int) ((this.mCellLayout.getCellWidth() + this.mCellLayout.getWidthGap()) * shirinkFactor);
        int i = this.mDeltaX + this.mDeltaXAddOn;
        float f = ((i * 1.0f) / cellWidth) - this.mRunningHInc;
        float cellHeight = (((this.mDeltaY + this.mDeltaYAddOn) * 1.0f) / ((int) ((this.mCellLayout.getCellHeight() + this.mCellLayout.getHeightGap()) * shirinkFactor))) - this.mRunningVInc;
        this.hSpanInc = getSpanInc(f);
        this.vSpanInc = getSpanInc(cellHeight);
        this.countX = this.mCellLayout.getCountX();
        this.countY = this.mCellLayout.getCountY();
        if (!onDismiss && this.hSpanInc == 0 && this.vSpanInc == 0) {
            return;
        }
        CellLayout.LayoutParams layoutParamsCalculateExpandableBorder = calculateExpandableBorder();
        updateDimensions();
        if (!onDismiss && this.vSpanDelta == 0 && this.hSpanDelta == 0) {
            switchAbnormalColor();
            return;
        }
        sendTalkbackAccessibilityEvent();
        boolean z = true;
        if (onDismiss) {
            this.mDirectionVector[0] = this.mLastDirectionVector[0];
            this.mDirectionVector[1] = this.mLastDirectionVector[1];
        } else {
            this.mLastDirectionVector[0] = this.mDirectionVector[0];
            this.mLastDirectionVector[1] = this.mDirectionVector[1];
            LGUserLog.send(getContext(), LGUserLog.FEATURENAME_RESIZEWIDGET);
        }
        if (LGHomeFeature.Config.FEATURE_USE_WIDGET_MAX_SPAN.getValue() || Utilities.isLGUI10_0()) {
            int i2 = this.spanX;
            int i3 = this.mMaxHSpan;
            boolean z2 = i2 > i3;
            int i4 = this.spanY;
            int i5 = this.mMaxVSpan;
            boolean z3 = i4 > i5;
            if (z2) {
                this.hSpanDelta = 0;
                this.spanX = i3;
            }
            if (z3) {
                this.vSpanDelta = 0;
                this.spanY = i5;
            }
            if (!onDismiss && layoutParamsCalculateExpandableBorder.cellHSpan == this.spanX && layoutParamsCalculateExpandableBorder.cellVSpan == this.spanY) {
                z = false;
            }
        }
        if (z && this.mCellLayout.createAreaForResize(this.cellX, this.cellY, this.spanX, this.spanY, this.mWidgetView, this.mDirectionVector, onDismiss)) {
            layoutParamsCalculateExpandableBorder.tmpCellX = this.cellX;
            layoutParamsCalculateExpandableBorder.tmpCellY = this.cellY;
            layoutParamsCalculateExpandableBorder.cellHSpan = this.spanX;
            layoutParamsCalculateExpandableBorder.cellVSpan = this.spanY;
            this.mRunningVInc += this.vSpanDelta;
            this.mRunningHInc += this.hSpanDelta;
            if (!onDismiss) {
                updateWidgetSizeRanges(this.mWidgetView, this.mLauncher, this.spanX, this.spanY, this.cellX, this.cellY);
            }
        } else {
            switchAbnormalColor();
        }
        this.mWidgetView.requestLayout();
    }

    private void updateDimensions() {
        if (this.mLeftBorderActive || this.mRightBorderActive) {
            this.spanX += this.hSpanInc;
            this.cellX += this.cellXInc;
            if (this.hSpanDelta != 0) {
                this.mDirectionVector[0] = this.mLeftBorderActive ? -1 : 1;
            }
        }
        if (this.mTopBorderActive || this.mBottomBorderActive) {
            this.spanY += this.vSpanInc;
            this.cellY += this.cellYInc;
            if (this.vSpanDelta != 0) {
                this.mDirectionVector[1] = this.mTopBorderActive ? -1 : 1;
            }
        }
    }

    private CellLayout.LayoutParams calculateExpandableBorder() {
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) this.mWidgetView.getLayoutParams();
        this.spanX = layoutParams.cellHSpan;
        this.spanY = layoutParams.cellVSpan;
        this.cellX = layoutParams.useTmpCoords ? layoutParams.tmpCellX : layoutParams.cellX;
        this.cellY = layoutParams.useTmpCoords ? layoutParams.tmpCellY : layoutParams.cellY;
        if (this.mLeftBorderActive) {
            this.cellXInc = Math.max(-this.cellX, this.hSpanInc);
            this.cellXInc = Math.min(layoutParams.cellHSpan - this.mMinHSpan, this.cellXInc);
            if ((LGHomeFeature.Config.FEATURE_USE_WIDGET_MAX_SPAN.getValue() || Utilities.isLGUI10_0()) && this.mMaxHSpan != Integer.MAX_VALUE) {
                this.cellXInc = Math.max(layoutParams.cellHSpan - this.mMaxHSpan, this.cellXInc);
            }
            int i = this.hSpanInc * (-1);
            this.hSpanInc = i;
            this.hSpanInc = Math.min(this.cellX, i);
            int iMax = Math.max(-(layoutParams.cellHSpan - this.mMinHSpan), this.hSpanInc);
            this.hSpanInc = iMax;
            this.hSpanDelta = -iMax;
        } else if (this.mRightBorderActive) {
            this.hSpanInc = Math.min(this.countX - (this.cellX + this.spanX), this.hSpanInc);
            int iMax2 = Math.max(-(layoutParams.cellHSpan - this.mMinHSpan), this.hSpanInc);
            this.hSpanInc = iMax2;
            this.hSpanDelta = iMax2;
        }
        if (this.mTopBorderActive) {
            this.cellYInc = Math.max(-this.cellY, this.vSpanInc);
            this.cellYInc = Math.min(layoutParams.cellVSpan - this.mMinVSpan, this.cellYInc);
            if ((LGHomeFeature.Config.FEATURE_USE_WIDGET_MAX_SPAN.getValue() || Utilities.isLGUI10_0()) && this.mMaxHSpan != Integer.MAX_VALUE) {
                this.cellYInc = Math.max(layoutParams.cellVSpan - this.mMaxVSpan, this.cellYInc);
            }
            int i2 = this.vSpanInc * (-1);
            this.vSpanInc = i2;
            this.vSpanInc = Math.min(this.cellY, i2);
            int iMax3 = Math.max(-(layoutParams.cellVSpan - this.mMinVSpan), this.vSpanInc);
            this.vSpanInc = iMax3;
            this.vSpanDelta = -iMax3;
        } else if (this.mBottomBorderActive) {
            this.vSpanInc = Math.min(this.countY - (this.cellY + this.spanY), this.vSpanInc);
            int iMax4 = Math.max(-(layoutParams.cellVSpan - this.mMinVSpan), this.vSpanInc);
            this.vSpanInc = iMax4;
            this.vSpanDelta = iMax4;
        }
        return layoutParams;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0036  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x003d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void sendTalkbackAccessibilityEvent() {
        /*
            r10 = this;
            int r0 = r10.spanX
            int r1 = r10.countX
            r2 = 0
            r3 = 1
            if (r0 < r1) goto La
            r1 = r3
            goto Lb
        La:
            r1 = r2
        Lb:
            int r4 = r10.mMinHSpan
            if (r0 > r4) goto L11
            r0 = r3
            goto L12
        L11:
            r0 = r2
        L12:
            int r4 = r10.spanY
            int r5 = r10.countY
            if (r4 < r5) goto L1a
            r5 = r3
            goto L1b
        L1a:
            r5 = r2
        L1b:
            int r6 = r10.mMinVSpan
            if (r4 > r6) goto L20
            r2 = r3
        L20:
            int r4 = r10.mResizeMode
            r6 = 2131820944(0x7f110190, float:1.9274617E38)
            r7 = 2131820966(0x7f1101a6, float:1.9274662E38)
            r8 = -1
            if (r4 == r3) goto L45
            r9 = 2
            if (r4 == r9) goto L3f
            r9 = 3
            if (r4 == r9) goto L32
            return
        L32:
            if (r0 == 0) goto L38
            if (r2 == 0) goto L38
        L36:
            r6 = r7
            goto L4a
        L38:
            if (r1 == 0) goto L3d
            if (r5 == 0) goto L3d
            goto L4a
        L3d:
            r6 = r8
            goto L4a
        L3f:
            if (r2 == 0) goto L42
            goto L36
        L42:
            if (r5 == 0) goto L3d
            goto L4a
        L45:
            if (r0 == 0) goto L48
            goto L36
        L48:
            if (r1 == 0) goto L3d
        L4a:
            if (r6 == r8) goto L51
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.lge.launcher3.util.TalkBackUtils.sendAccessibilityEvent(r0, r6, r3)
        L51:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.LGAppWidgetResizeFrame.sendTalkbackAccessibilityEvent():void");
    }

    private int getSpanInc(float spanIncF) {
        if (Math.abs(spanIncF) > 0.66f) {
            return Math.round(spanIncF);
        }
        return 0;
    }

    private void switchAbnormalColor() {
        int color = getResources().getColor(R.color.widget_resize_abnormal, null);
        getForeground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        this.mLeftHandle.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        this.mTopHandle.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        this.mRightHandle.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        this.mBottomHandle.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void applyOriginalColor() {
        int lGEColor = DDTUtils.getLGEColor(this.mContext, "color_accent_ui");
        if (lGEColor == 0) {
            return;
        }
        getForeground().setColorFilter(lGEColor, PorterDuff.Mode.SRC_ATOP);
        this.mLeftHandle.setColorFilter(lGEColor, PorterDuff.Mode.SRC_ATOP);
        this.mTopHandle.setColorFilter(lGEColor, PorterDuff.Mode.SRC_ATOP);
        this.mRightHandle.setColorFilter(lGEColor, PorterDuff.Mode.SRC_ATOP);
        this.mBottomHandle.setColorFilter(lGEColor, PorterDuff.Mode.SRC_ATOP);
    }

    public int getTouchTargetWidth() {
        return this.mTouchTargetWidth;
    }
}
