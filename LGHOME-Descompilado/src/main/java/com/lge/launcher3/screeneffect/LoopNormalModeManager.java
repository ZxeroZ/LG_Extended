package com.lge.launcher3.screeneffect;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.lge.launcher3.PagedView;
import com.lge.launcher3.allapps.AllAppsPagedView;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class LoopNormalModeManager {
    public static final boolean DEBUG = false;
    public static final String TAG = "LoopNormalModeManager";
    private static LoopNormalModeManager sInstance;
    private Context mContext;
    private boolean mIsRtl;
    private Launcher mLauncher;
    private boolean mIsFeatureEnabled = true;
    private boolean mIsFeatureEnabledAllApps = true;
    private boolean mIsEnabled = true;

    public enum PageShiftDirection {
        SHIFT_TO_TAIL,
        SHIFT_TO_HEAD,
        SHIFT_NONE
    }

    public static LoopNormalModeManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LoopNormalModeManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private LoopNormalModeManager(Context context) {
        this.mContext = null;
        this.mIsRtl = false;
        LGLog.i(TAG, "Create a new LoopScreenManager instance.");
        this.mContext = context;
        updateFeatureEnabled();
        this.mIsRtl = Utilities.isRtl(this.mContext.getResources());
    }

    public void updateFeatureEnabled() {
        this.mIsFeatureEnabled = HomeSettingsSharedPreferences.getContinuousLoopEnabled(this.mContext);
        this.mIsFeatureEnabledAllApps = true;
        if (LGFeatureConfig.FEATURE_OPERATOR.equals("VZW")) {
            this.mIsFeatureEnabledAllApps = HomeSettingsSharedPreferences.getVZWAppDrawerLoopEnabled(this.mContext);
        }
    }

    public void setEnabled(boolean enabled) {
        this.mIsEnabled = enabled;
    }

    public boolean isEnabled(PagedView pagedView) {
        boolean z = pagedView instanceof Workspace;
        boolean zIsAllAppsView = isAllAppsView(pagedView);
        if (pagedView == null || !this.mIsEnabled) {
            return false;
        }
        if (zIsAllAppsView && !this.mIsFeatureEnabledAllApps) {
            return false;
        }
        if (z && !this.mIsFeatureEnabled) {
            return false;
        }
        boolean zIsWorkspaceNormalState = isWorkspaceNormalState();
        boolean zIsWorkspaceSpringLoadedState = isWorkspaceSpringLoadedState();
        boolean z2 = pagedView.getChildCount() <= 1;
        return (z && ((zIsWorkspaceNormalState || zIsWorkspaceSpringLoadedState) && !z2)) || (zIsAllAppsView && !z2);
    }

    public boolean isAllAppsView(PagedView pagedView) {
        if (pagedView == null) {
            return false;
        }
        return pagedView instanceof AllAppsPagedView;
    }

    public void setLauncher(Launcher launcher) {
        if (launcher == null) {
            return;
        }
        this.mLauncher = launcher;
    }

    public boolean isWorkspaceNormalState() {
        WorkspaceStateTransitionWatcher workspaceStateTransitionWatcher = WorkspaceStateTransitionWatcher.getInstance(this.mContext);
        return !workspaceStateTransitionWatcher.isStateTransitioning() && workspaceStateTransitionWatcher.getToState() == Workspace.State.NORMAL;
    }

    public boolean isWorkspaceSpringLoadedState() {
        WorkspaceStateTransitionWatcher workspaceStateTransitionWatcher = WorkspaceStateTransitionWatcher.getInstance(this.mContext);
        return !workspaceStateTransitionWatcher.isStateTransitioning() && workspaceStateTransitionWatcher.getToState() == Workspace.State.SPRING_LOADED;
    }

    public boolean isOverviewState() {
        WorkspaceStateTransitionWatcher workspaceStateTransitionWatcher = WorkspaceStateTransitionWatcher.getInstance(this.mContext);
        return !workspaceStateTransitionWatcher.isStateTransitioning() && workspaceStateTransitionWatcher.getToState() == Workspace.State.OVERVIEW;
    }

    public int getPageNearestToCenterOfScreenForLoop(PagedView pagedView) {
        int iIndexOfTail;
        int viewportOffsetX = pagedView.getViewportOffsetX();
        int scrollX = pagedView.getScrollX() + viewportOffsetX + (pagedView.getViewportWidth() / 2);
        int childCount = pagedView.getChildCount();
        int i = Integer.MAX_VALUE;
        int i2 = -1;
        for (int i3 = 0; i3 < childCount; i3++) {
            View pageAt = pagedView.getPageAt(i3);
            ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
            boolean zIsHeadPage = screenEffectTargetManager.isHeadPage(i3);
            boolean zIsTailPage = screenEffectTargetManager.isTailPage(i3);
            int measuredWidth = pageAt.getMeasuredWidth() / 2;
            int childOffset = pagedView.getChildOffset(i3) + viewportOffsetX + measuredWidth;
            if (screenEffectTargetManager.isOverscrollLeft() && (this.mIsRtl ? zIsHeadPage : zIsTailPage)) {
                childOffset = viewportOffsetX - measuredWidth;
            } else if (screenEffectTargetManager.isOverscrollRight() && (this.mIsRtl ? zIsTailPage : zIsHeadPage)) {
                childOffset = screenEffectTargetManager.getMaxScrollForLoop() + viewportOffsetX + measuredWidth;
            }
            int iAbs = Math.abs(childOffset - scrollX);
            if (iAbs < i) {
                if (screenEffectTargetManager.isHeadToTail() && zIsTailPage) {
                    iIndexOfTail = screenEffectTargetManager.indexOfHead() - 1;
                } else if (screenEffectTargetManager.isTailToHead() && zIsHeadPage) {
                    iIndexOfTail = screenEffectTargetManager.indexOfTail() + 1;
                } else {
                    i2 = i3;
                    i = iAbs;
                }
                i2 = iIndexOfTail;
                i = iAbs;
            }
        }
        return i2;
    }

    public boolean forceToDrawChildForLoop(PagedView pagedView, int leftScreen, int rightScreen) {
        boolean zIsOverscrollRight;
        boolean zIsOverscrollLeft;
        Launcher launcher;
        if (isAllAppsView(pagedView) && ((launcher = this.mLauncher) == null || launcher.getAllAppsHost() == null || this.mLauncher.getAllAppsHost().getLGAllAppsPagedView() == null)) {
            return false;
        }
        if (isAllAppsView(pagedView)) {
            zIsOverscrollLeft = this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().isOverscrollLeft();
            zIsOverscrollRight = this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().isOverscrollRight();
        } else {
            ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
            boolean zIsOverscrollLeft2 = screenEffectTargetManager.isOverscrollLeft();
            zIsOverscrollRight = screenEffectTargetManager.isOverscrollRight();
            zIsOverscrollLeft = zIsOverscrollLeft2;
        }
        if (isAllAppsView(pagedView) && ((leftScreen == -1 || rightScreen == -1) && (zIsOverscrollLeft || zIsOverscrollRight))) {
            return true;
        }
        return (isWorkspaceNormalState() && ((leftScreen == -1 || rightScreen == -1) && (zIsOverscrollLeft || zIsOverscrollRight))) || isWorkspaceSpringLoadedState();
    }

    public boolean drawChildForLoop(Canvas canvas, PagedView pagedView, View child) {
        View childAt;
        View childAt2;
        Launcher launcher;
        if (isAllAppsView(pagedView) && (launcher = this.mLauncher) != null) {
            boolean z = launcher.getAllAppsHost().getLGAllAppsPagedView().isHeadToTail() && this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().isHeadPage(child);
            boolean z2 = this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().isTailToHead() && this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().isTailPage(child);
            if (!z && !z2) {
                return false;
            }
            childAt = pagedView.getChildAt(this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().indexOfHead());
            childAt2 = pagedView.getChildAt(this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().indexOfTail(pagedView));
        } else {
            ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
            boolean z3 = screenEffectTargetManager.isHeadToTail() && screenEffectTargetManager.isHeadPage(child);
            boolean z4 = screenEffectTargetManager.isTailToHead() && screenEffectTargetManager.isTailPage(child);
            if (!z3 && !z4) {
                return false;
            }
            childAt = pagedView.getChildAt(screenEffectTargetManager.indexOfHead());
            childAt2 = pagedView.getChildAt(screenEffectTargetManager.indexOfTail(pagedView));
        }
        if (childAt == null || childAt2 == null) {
            return false;
        }
        long drawingTime = pagedView.getDrawingTime();
        pagedView.drawChild(canvas, childAt, drawingTime);
        pagedView.drawChild(canvas, childAt2, drawingTime);
        return true;
    }

    public int getTranslationAmount(PagedView pagedView, View headView) {
        int maxScrollX;
        int paddingRight;
        View childAt = pagedView.getChildAt(ScreenEffectTargetManager.getInstance(this.mContext).indexOfTail(pagedView));
        PagedView.LayoutParams layoutParams = (PagedView.LayoutParams) headView.getLayoutParams();
        if (layoutParams.isVerticalLayout) {
            if (layoutParams.isFullScreenPage) {
                if (this.mIsRtl) {
                    return ((pagedView.getMaxScrollX() + childAt.getMeasuredWidth()) - (headView.getMeasuredWidth() - (childAt.getMeasuredWidth() + pagedView.getPageSpacing()))) + pagedView.getPageSpacing();
                }
                return ((((pagedView.getMaxScrollX() + childAt.getMeasuredWidth()) + pagedView.getPageSpacing()) + pagedView.getPaddingLeft()) + Math.round(headView.getScrollX() * (childAt.getWidth() / headView.getWidth()))) - ((headView.getMeasuredWidth() - childAt.getMeasuredWidth()) / 2);
            }
            maxScrollX = pagedView.getMaxScrollX() + childAt.getMeasuredWidth();
            paddingRight = pagedView.getPageSpacing();
        } else {
            maxScrollX = pagedView.getMaxScrollX() + childAt.getMeasuredWidth() + pagedView.getPaddingLeft();
            paddingRight = pagedView.getPaddingRight();
        }
        return maxScrollX + paddingRight;
    }

    public void pageShiftForLoopOnSpringLoaded(PagedView pagedView, PageShiftDirection direction, boolean needNextChild) {
        if (!(pagedView instanceof Workspace) || pagedView.getChildCount() < 2 || isAllAppsView(pagedView) || ((Workspace) pagedView).getOverlayTranslation() != 0.0f) {
            return;
        }
        ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
        View childAt = pagedView.getChildAt(screenEffectTargetManager.indexOfHead());
        View childAt2 = pagedView.getChildAt(screenEffectTargetManager.indexOfTail(pagedView));
        View childAt3 = pagedView.getChildAt(screenEffectTargetManager.indexOfTail(pagedView) - 1);
        View childAt4 = pagedView.getChildAt(screenEffectTargetManager.indexOfHead() + 1);
        int translationAmount = (this.mIsRtl ? -1 : 1) * getTranslationAmount(pagedView, childAt);
        if (pagedView.getChildCount() == 2) {
            childAt.setTranslationX(0.0f);
            childAt2.setTranslationX(0.0f);
            return;
        }
        if (direction == PageShiftDirection.SHIFT_TO_HEAD) {
            float f = translationAmount * (-1);
            childAt2.setTranslationX(f);
            childAt.setTranslationX(0.0f);
            childAt4.setTranslationX(0.0f);
            if (needNextChild) {
                childAt3.setTranslationX(f);
                return;
            }
            return;
        }
        if (direction == PageShiftDirection.SHIFT_TO_TAIL) {
            float f2 = translationAmount;
            childAt.setTranslationX(f2);
            childAt2.setTranslationX(0.0f);
            childAt3.setTranslationX(0.0f);
            if (needNextChild) {
                childAt4.setTranslationX(f2);
                return;
            }
            return;
        }
        childAt.setTranslationX(0.0f);
        childAt4.setTranslationX(0.0f);
        childAt2.setTranslationX(0.0f);
        childAt3.setTranslationX(0.0f);
    }

    public void translateCanvasForLoop(Canvas canvas, View child) {
        ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
        boolean zIsHeadPage = screenEffectTargetManager.isHeadPage(child);
        boolean zIsTailPage = screenEffectTargetManager.isTailPage(child);
        if (screenEffectTargetManager.isOverscrollLeft() && (this.mIsRtl ? zIsHeadPage : zIsTailPage)) {
            canvas.translate(-screenEffectTargetManager.getMaxScrollForLoop(), 0.0f);
            return;
        }
        if (screenEffectTargetManager.isOverscrollRight()) {
            if (this.mIsRtl) {
                if (!zIsTailPage) {
                    return;
                }
            } else if (!zIsHeadPage) {
                return;
            }
            canvas.translate(screenEffectTargetManager.getMaxScrollForLoop(), 0.0f);
        }
    }

    public int[] computeScrollToForLoop(PagedView pagedView, int currentPage, int scrollX) {
        int iMin;
        ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
        View pageAt = pagedView.getPageAt(screenEffectTargetManager.indexOfHead());
        PagedView.LayoutParams layoutParams = (PagedView.LayoutParams) pageAt.getLayoutParams();
        if (scrollX == 0 && currentPage == 0) {
            return new int[]{currentPage, scrollX};
        }
        if (screenEffectTargetManager.isHeadToTailScrollOver(scrollX)) {
            currentPage = screenEffectTargetManager.indexOfTail();
            scrollX += (this.mIsRtl ? -1 : 1) * screenEffectTargetManager.getMaxScrollForLoop();
            if (layoutParams.isFullScreenPage && layoutParams.isOverviewMode && layoutParams.isVerticalLayout) {
                int scrollForPage = screenEffectTargetManager.getScrollForPage(currentPage);
                if (this.mIsRtl) {
                    iMin = Math.max(scrollX, 0);
                } else {
                    iMin = Math.min(scrollX, scrollForPage);
                }
                scrollX = iMin;
            }
        } else if (screenEffectTargetManager.isTailToHeadScrollOver(scrollX)) {
            currentPage = screenEffectTargetManager.indexOfHead();
            scrollX += (this.mIsRtl ? 1 : -1) * screenEffectTargetManager.getMaxScrollForLoop();
            if (layoutParams.isFullScreenPage && layoutParams.isOverviewMode && layoutParams.isVerticalLayout) {
                int scrollForPage2 = screenEffectTargetManager.getScrollForPage(currentPage);
                int measuredWidth = pageAt.getMeasuredWidth() - (pagedView.getPageAt(screenEffectTargetManager.indexOfTail()).getMeasuredWidth() + pagedView.getPageSpacing());
                if (this.mIsRtl) {
                    scrollX = Math.min(scrollX, scrollForPage2 - measuredWidth);
                } else {
                    scrollX = Math.max(scrollX, measuredWidth);
                }
            }
        }
        return new int[]{currentPage, scrollX};
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x003e  */
    /* JADX WARN: Removed duplicated region for block: B:24:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void computeScrollHelperForLoop(com.lge.launcher3.PagedView r6, int r7) {
        /*
            r5 = this;
            android.content.Context r0 = r5.mContext
            com.lge.launcher3.screeneffect.ScreenEffectTargetManager r0 = com.lge.launcher3.screeneffect.ScreenEffectTargetManager.getInstance(r0)
            int r1 = r6.getScrollX()
            boolean r2 = r0.isHeadToTail()
            r3 = 1
            r4 = -1
            if (r2 == 0) goto L25
            boolean r2 = r0.isTailPage(r7)
            if (r2 == 0) goto L25
            boolean r7 = r5.mIsRtl
            if (r7 != 0) goto L1d
            goto L1e
        L1d:
            r3 = r4
        L1e:
            int r7 = r0.getMaxScrollForLoop()
        L22:
            int r3 = r3 * r7
            int r3 = r3 + r1
            goto L3c
        L25:
            boolean r2 = r0.isTailToHead()
            if (r2 == 0) goto L3b
            boolean r7 = r0.isHeadPage(r7)
            if (r7 == 0) goto L3b
            boolean r7 = r5.mIsRtl
            if (r7 != 0) goto L36
            r3 = r4
        L36:
            int r7 = r0.getMaxScrollForLoop()
            goto L22
        L3b:
            r3 = r1
        L3c:
            if (r3 == r1) goto L45
            int r7 = r6.getScrollY()
            r6.scrollTo(r3, r7)
        L45:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.screeneffect.LoopNormalModeManager.computeScrollHelperForLoop(com.lge.launcher3.PagedView, int):void");
    }

    public int validateNewPageForLoop(int whichPage, ViewGroup workspace) {
        ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
        if (screenEffectTargetManager.getParent() != workspace) {
            LGLog.d(TAG, "[validateNewPageForLoop] update workspace.");
            screenEffectTargetManager.setParent(workspace);
        }
        int iIndexOfHead = screenEffectTargetManager.indexOfHead();
        int iIndexOfTail = screenEffectTargetManager.indexOfTail();
        return whichPage < iIndexOfHead ? iIndexOfTail : iIndexOfTail < whichPage ? iIndexOfHead : whichPage;
    }

    public float[] updateStateForCustomContentForLoop(int index) {
        float fMax;
        ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
        int[] scrollDeltaAndRange = screenEffectTargetManager.getScrollDeltaAndRange(screenEffectTargetManager.validatePageIndexForLoop(index));
        int i = scrollDeltaAndRange[0];
        int i2 = scrollDeltaAndRange[1];
        float f = 0.0f;
        if (i2 == 0 || Math.abs(i) < 0 || Math.abs(i) > Math.abs(i2)) {
            fMax = 0.0f;
        } else {
            float f2 = i2 - i;
            fMax = Math.max(0.0f, f2 / i2);
            f = f2;
        }
        return new float[]{f, fMax};
    }

    public float getScrollProgressForLoop(int index) {
        ScreenEffectTargetManager screenEffectTargetManager = ScreenEffectTargetManager.getInstance(this.mContext);
        int[] scrollDeltaAndRange = screenEffectTargetManager.getScrollDeltaAndRange(screenEffectTargetManager.validatePageIndexForLoop(index));
        int i = scrollDeltaAndRange[0];
        int iAbs = Math.abs(scrollDeltaAndRange[1]);
        if (iAbs != 0) {
            return Math.max(Math.min(i / iAbs, 1.0f), -1.0f);
        }
        return 0.0f;
    }
}
