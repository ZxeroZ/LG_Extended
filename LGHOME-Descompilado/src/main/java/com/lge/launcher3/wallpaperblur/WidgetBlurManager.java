package com.lge.launcher3.wallpaperblur;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Workspace;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.lge.launcher3.screeneffect.WorkspaceStateTransitionWatcher;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class WidgetBlurManager implements WallpaperBlurredImageController.OnWallpaperChangeListener {
    public static final boolean DEBUG = true;
    public static final String TAG = "WidgetBlurManager";
    public static final String TRANSITION_NAME = "widget_blur_bg";
    private static Context sContext;
    private static WidgetBlurManager sInstance;
    private Launcher mLauncher = null;
    private boolean mIsResizedFrameEnabled = false;
    private boolean mIsAlreadyBlurViewRemoved = false;
    private Set<WidgetBlurListener> mListeners = new HashSet();

    public interface WidgetBlurListener {
        void onStart(boolean updateLiveWallpaper);

        void onStop(boolean updateLiveWallpaper);

        void onWallpaperBlurredImageChanged();
    }

    public static WidgetBlurManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WidgetBlurManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private WidgetBlurManager(Context context) {
        LGLog.i(TAG, "Create a new WidgetBlurManager instance.");
        sContext = context;
    }

    public void setLauncher(Launcher launcher) {
        if (launcher == null) {
            return;
        }
        this.mLauncher = launcher;
        WallpaperBlurredImageController.getInstance(sContext).setLauncher(this.mLauncher);
        WallpaperBlurredImageController.getInstance(sContext).addOnWallpaperChangeListener(this);
    }

    public void determineToRemovBlurView() {
        if (isDisabled() || !isLiveWallpaperMode() || this.mIsAlreadyBlurViewRemoved) {
            return;
        }
        removeBlurViewAll();
        updateColorViewAll(WallpaperBlurredImageController.getInstance(sContext).getCommonColor());
    }

    private void addBlurViewAndColorViewAll() {
        this.mIsAlreadyBlurViewRemoved = false;
        for (WidgetBlurLayout widgetBlurLayout : getAllBlurLayoutList(null)) {
            if (widgetBlurLayout != null) {
                widgetBlurLayout.addBlurView();
                widgetBlurLayout.addColorView();
            }
        }
    }

    private void removeBlurViewAndColorViewAll() {
        for (WidgetBlurLayout widgetBlurLayout : getAllBlurLayoutList(null)) {
            if (widgetBlurLayout != null) {
                widgetBlurLayout.removeBlurView();
                widgetBlurLayout.removeColorView();
            }
        }
    }

    private void removeBlurViewAll() {
        this.mIsAlreadyBlurViewRemoved = true;
        for (WidgetBlurLayout widgetBlurLayout : getAllBlurLayoutList(null)) {
            if (widgetBlurLayout != null) {
                widgetBlurLayout.removeBlurView();
            }
        }
    }

    public void enableBlurView(View view, boolean enable) {
        enableBlurView(view, enable, true);
    }

    public void enableBlurView(View view, boolean enable, boolean animate) {
        if (isDisabled() || isLiveWallpaperMode()) {
            return;
        }
        LGLog.i(TAG, String.format("enableBlurView(View, %s)", Boolean.valueOf(enable)));
        if (view == null || (view instanceof LauncherAppWidgetHostView)) {
            LauncherAppWidgetHostView launcherAppWidgetHostView = (LauncherAppWidgetHostView) view;
            if (launcherAppWidgetHostView.hasWidgetBlurLayout()) {
                launcherAppWidgetHostView.getWidgetBlurLayout().enableBlurView(enable, animate);
            }
        }
    }

    public void enableBlurViewInCurrentPage(boolean enable) {
        if (isDisabled() || isLiveWallpaperMode()) {
            return;
        }
        String str = TAG;
        LGLog.i(str, String.format("enableBlurViewInCurrentPage(%s)", Boolean.valueOf(enable)));
        if (enable && (isWorkspaceScrolling() || this.mIsResizedFrameEnabled)) {
            LGLog.i(str, String.format("Cancel it because workspace is scrolling or has resized frame.(%s, %s)", Boolean.valueOf(enable), Boolean.valueOf(this.mIsResizedFrameEnabled)));
            return;
        }
        int currentPage = getCurrentPage();
        if (currentPage == -1) {
            return;
        }
        for (WidgetBlurLayout widgetBlurLayout : getBlurLayoutList((ArrayList<WidgetBlurLayout>) null, currentPage)) {
            if (widgetBlurLayout != null) {
                widgetBlurLayout.enableBlurView(enable);
            }
        }
    }

    public void enableBlurViewIfAddedInCurrentPage(View view, boolean enable) {
        Object tag;
        Workspace workspace;
        if (isDisabled() || isLiveWallpaperMode()) {
            return;
        }
        LGLog.i(TAG, String.format("enableBlurViewIfAddedInCurrentPage(View, %s)", Boolean.valueOf(enable)));
        if (view == null || !(view instanceof LauncherAppWidgetHostView) || (tag = view.getTag()) == null || !(tag instanceof LauncherAppWidgetInfo)) {
            return;
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) tag;
        LauncherAppWidgetHostView launcherAppWidgetHostView = (LauncherAppWidgetHostView) view;
        if (launcherAppWidgetHostView.hasWidgetBlurLayout() && (workspace = getWorkspace()) != null && launcherAppWidgetInfo.screenId == workspace.getScreenIdForPageIndex(workspace.getCurrentPage())) {
            launcherAppWidgetHostView.getWidgetBlurLayout().enableBlurView(enable);
        }
    }

    public void enableBlurViewAll(boolean enable) {
        if (isDisabled() || isLiveWallpaperMode()) {
            return;
        }
        LGLog.i(TAG, String.format("enableBlurViewAll(%s)", Boolean.valueOf(enable)));
        for (WidgetBlurLayout widgetBlurLayout : getAllBlurLayoutList(null)) {
            if (widgetBlurLayout != null) {
                widgetBlurLayout.enableBlurView(enable);
            }
        }
    }

    public void updateBlurView(View view) {
        if (isDisabled() || isLiveWallpaperMode()) {
            return;
        }
        LGLog.i(TAG, String.format("updateBlurView(View)", new Object[0]));
        if (view == null || (view instanceof LauncherAppWidgetHostView)) {
            LauncherAppWidgetHostView launcherAppWidgetHostView = (LauncherAppWidgetHostView) view;
            if (launcherAppWidgetHostView.hasWidgetBlurLayout()) {
                launcherAppWidgetHostView.getWidgetBlurLayout().updateBlurView();
            }
        }
    }

    public void updateBlurViewInCurrentPage() {
        if (isDisabled() || isLiveWallpaperMode()) {
            return;
        }
        LGLog.i(TAG, String.format("updateBlurViewInCurrentPage()", new Object[0]));
        for (WidgetBlurLayout widgetBlurLayout : getBlurLayoutList((ArrayList<WidgetBlurLayout>) null, getCurrentPage())) {
            if (widgetBlurLayout != null) {
                widgetBlurLayout.updateBlurView();
            }
        }
    }

    public void updateBlurViewAll() {
        if (isDisabled() || isLiveWallpaperMode()) {
            return;
        }
        LGLog.i(TAG, String.format("updateBlurViewAll()", new Object[0]));
        for (WidgetBlurLayout widgetBlurLayout : getAllBlurLayoutList(null)) {
            if (widgetBlurLayout != null) {
                widgetBlurLayout.updateBlurView();
            }
        }
    }

    private void updateColorViewAll(int color) {
        for (WidgetBlurLayout widgetBlurLayout : getAllBlurLayoutList(null)) {
            if (widgetBlurLayout != null) {
                widgetBlurLayout.updateColorView(color);
            }
        }
    }

    private ArrayList<WidgetBlurLayout> getAllBlurLayoutList(ArrayList<WidgetBlurLayout> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        Workspace workspace = getWorkspace();
        if (workspace == null) {
            return list;
        }
        int childCount = workspace.getChildCount();
        for (int i = 0; i < childCount; i++) {
            getBlurLayoutList(list, i);
        }
        return list;
    }

    private ArrayList<WidgetBlurLayout> getBlurLayoutList(ArrayList<WidgetBlurLayout> list, int pageIndex) {
        CellLayout cellLayout;
        if (list == null) {
            list = new ArrayList<>();
        }
        Workspace workspace = getWorkspace();
        return (workspace == null || (cellLayout = (CellLayout) workspace.getChildAt(pageIndex)) == null) ? list : getBlurLayoutList(list, cellLayout);
    }

    private ArrayList<WidgetBlurLayout> getBlurLayoutList(ArrayList<WidgetBlurLayout> list, CellLayout cellLayout) {
        ShortcutAndWidgetContainer shortcutsAndWidgets;
        LauncherAppWidgetHostView launcherAppWidgetHostView;
        if (list == null) {
            list = new ArrayList<>();
        }
        if (cellLayout == null || (shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets()) == null) {
            return list;
        }
        int childCount = shortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Object tag = shortcutsAndWidgets.getChildAt(i).getTag();
            if (tag != null && (tag instanceof LauncherAppWidgetInfo) && (launcherAppWidgetHostView = (LauncherAppWidgetHostView) ((LauncherAppWidgetInfo) tag).getHostView()) != null && launcherAppWidgetHostView.hasWidgetBlurLayout()) {
                list.add(launcherAppWidgetHostView.getWidgetBlurLayout());
            }
        }
        return list;
    }

    private boolean isWorkspaceScrolling() {
        Workspace workspace = getWorkspace();
        if (workspace == null) {
            return false;
        }
        return workspace.isScrolling();
    }

    public Animator.AnimatorListener getWorkspaceStateAnimationListener(Workspace.State fromState, Workspace.State toState) {
        final boolean z = toState == Workspace.State.NORMAL;
        final boolean z2 = fromState == Workspace.State.NORMAL;
        LGLog.i(TAG, String.format("getWorkspaceStateAnimationListener() : fromNormal(%s), toNormal(%s)", Boolean.valueOf(z2), Boolean.valueOf(z)));
        return new AnimatorListenerAdapter() { // from class: com.lge.launcher3.wallpaperblur.WidgetBlurManager.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                if (z2) {
                    WidgetBlurManager.this.enableBlurViewAll(false);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Workspace workspace;
                LayoutTransition layoutTransition;
                if (!z || (workspace = WidgetBlurManager.this.getWorkspace()) == null || (layoutTransition = workspace.getLayoutTransition()) == null) {
                    return;
                }
                if (!layoutTransition.isRunning()) {
                    LGLog.i(WidgetBlurManager.TAG, String.format("LayoutTransition Is NOT Running.", new Object[0]));
                    WidgetBlurManager.this.enableBlurViewInCurrentPageAndUpdateAll();
                } else {
                    layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() { // from class: com.lge.launcher3.wallpaperblur.WidgetBlurManager.1.1
                        @Override // android.animation.LayoutTransition.TransitionListener
                        public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                        }

                        @Override // android.animation.LayoutTransition.TransitionListener
                        public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                            LGLog.i(WidgetBlurManager.TAG, String.format("LayoutTransition.endTransition() : %d", Integer.valueOf(transitionType)));
                            if (transition.isRunning()) {
                                return;
                            }
                            transition.removeTransitionListener(this);
                            WidgetBlurManager.this.enableBlurViewInCurrentPageAndUpdateAll();
                        }
                    });
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableBlurViewInCurrentPageAndUpdateAll() {
        LGLog.i(TAG, String.format("enableBlurViewInCurrentPageAndUpdateAll()", new Object[0]));
        if (this.mIsResizedFrameEnabled) {
            return;
        }
        enableBlurViewInCurrentPage(true);
        updateBlurViewAll();
    }

    public void enableResizedFrame(boolean enable) {
        LGLog.i(TAG, String.format("enableResizedFrame(%s)", Boolean.valueOf(enable)));
        this.mIsResizedFrameEnabled = enable;
        if (UninstallModeManager.getInstance(sContext).isInUninstallMode()) {
            return;
        }
        enableBlurViewInCurrentPage(!this.mIsResizedFrameEnabled);
        if (this.mIsResizedFrameEnabled) {
            return;
        }
        updateBlurViewInCurrentPage();
    }

    @Override // com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController.OnWallpaperChangeListener
    public void onWallpaperChanged() {
        LGLog.i(TAG, "Receive onWallpaperChanged()");
        if (isDisabled() || isLiveWallpaperMode()) {
            return;
        }
        removeBlurViewAndColorViewAll();
    }

    @Override // com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController.OnWallpaperChangeListener
    public void onWallpaperBlurredImageChanged(int adaptiveColor) {
        LGLog.i(TAG, String.format("onWallpaperBlurredImageChanged() : adpativeColor = %s(%d)", Integer.toHexString(adaptiveColor), Integer.valueOf(adaptiveColor)));
        Iterator<WidgetBlurListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onWallpaperBlurredImageChanged();
        }
        if (isDisabled() || isLiveWallpaperMode()) {
            return;
        }
        addBlurViewAndColorViewAll();
        if (WorkspaceStateTransitionWatcher.getInstance(sContext).getToState() == Workspace.State.NORMAL) {
            enableBlurViewInCurrentPage(true);
        } else {
            LGLog.i(TAG, String.format("onWallpaperBlurredImageChanged() : Skip to enable the current page.", new Object[0]));
        }
        updateColorViewAll(adaptiveColor);
    }

    public Workspace getWorkspace() {
        Launcher launcher = this.mLauncher;
        if (launcher != null) {
            return launcher.getWorkspace();
        }
        return null;
    }

    public int getCurrentPage() {
        Workspace workspace = getWorkspace();
        if (workspace != null) {
            return workspace.getCurrentPage();
        }
        return -1;
    }

    public boolean isDisabled() {
        return !LGHomeFeature.Config.FEATURE_USE_WIDGET_BLUR.getValue();
    }

    public boolean isLiveWallpaperMode() {
        return WallpaperBlurredImageController.getInstance(sContext).isLiveWallpaperMode();
    }

    public void destroy() {
        LGLog.i(TAG, "Destroy WidgetBlurManager instance.");
        WallpaperBlurredImageController.getInstance(sContext).removeOnWallpaperChangeListener(this);
        this.mIsAlreadyBlurViewRemoved = false;
        this.mIsResizedFrameEnabled = false;
        WidgetBlurAppList.getInstance(sContext).destroy();
        this.mLauncher = null;
        sInstance = null;
        sContext = null;
        this.mListeners.clear();
    }

    public void addListener(WidgetBlurListener l) {
        this.mListeners.add(l);
    }

    public void removeListener(WidgetBlurListener l) {
        this.mListeners.remove(l);
    }

    public void onStart(boolean updateLiveWallpaper) {
        Iterator<WidgetBlurListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onStart(updateLiveWallpaper);
        }
    }

    public void onStop(boolean updateLiveWallpaper) {
        Iterator<WidgetBlurListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onStop(updateLiveWallpaper);
        }
    }
}
