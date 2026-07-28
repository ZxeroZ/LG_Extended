package com.lge.launcher3.concierge;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.dragndrop.DragLayer;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.wallpaperblur.HomescreenBlurManager;
import com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController;
import com.lge.launcher3.wallpaperblur.WidgetBlurLayout;
import com.lge.lgewidgetlib.extview.IExtViewHostAdapter;
import com.lge.lgewidgetlib.extview.IWidgetExtHandler;

/* JADX INFO: loaded from: classes.dex */
public class ConciergeBoardMngr implements IExtViewHostAdapter {
    private static final String TAG = "ConciergeBoardMngr";
    private static ConciergeBoardMngr sInstance = null;
    private static boolean sIsExtViewEnabled = true;
    ViewGroup mDragLayer;
    IWidgetExtHandler mExtViewHandlerImpl;
    Activity mLauncher;

    private ConciergeBoardMngr(Activity context) {
        this.mLauncher = context;
        sIsExtViewEnabled = true;
    }

    public static ConciergeBoardMngr getInstance() {
        return sInstance;
    }

    public static void init(Activity context) {
        sInstance = new ConciergeBoardMngr(context);
        LGLog.i(TAG, "init()");
    }

    public void setContext(Context Context) {
        this.mLauncher = null;
        this.mExtViewHandlerImpl = null;
    }

    public static void setupExtLayerForAttach(ViewGroup view) {
        ConciergeBoardMngr conciergeBoardMngr = sInstance;
        if (conciergeBoardMngr != null) {
            conciergeBoardMngr.setupExtLayer(view);
        } else {
            LGLog.i(TAG, "sSetupExtLayerForAttach , sInstance is null");
        }
    }

    public void setupExtLayer(ViewGroup view) {
        this.mDragLayer = view;
    }

    public static void onStartBiding() {
        ConciergeBoardMngr conciergeBoardMngr = sInstance;
        if (conciergeBoardMngr != null) {
            conciergeBoardMngr.notifyBindingStarted();
        }
    }

    public void notifyBindingStarted() {
        IWidgetExtHandler iWidgetExtHandler = this.mExtViewHandlerImpl;
        if (iWidgetExtHandler != null) {
            iWidgetExtHandler.notifyBindingStarted();
        }
    }

    public static void onDestroyHost() {
        sInstance.notifyWidgetHostDestroyed();
        ConciergeBoardMngr conciergeBoardMngr = sInstance;
        if (conciergeBoardMngr != null) {
            conciergeBoardMngr.setContext(null);
        }
        sInstance = null;
    }

    private void notifyWidgetHostDestroyed() {
        IWidgetExtHandler iWidgetExtHandler = this.mExtViewHandlerImpl;
        if (iWidgetExtHandler != null) {
            iWidgetExtHandler.notifyWidgetHostDestroyed();
        } else {
            LGLog.e(TAG, "destroyWidgetHost, mExtViewHandlerImpl is null");
        }
    }

    public static boolean isExtViewMode() {
        ConciergeBoardMngr conciergeBoardMngr = sInstance;
        if (conciergeBoardMngr != null) {
            return conciergeBoardMngr.isExtView();
        }
        LGLog.i(TAG, "sIsExtViewMode , sInstance is null");
        return false;
    }

    private boolean isExtView() {
        IWidgetExtHandler iWidgetExtHandler = this.mExtViewHandlerImpl;
        if (iWidgetExtHandler != null) {
            return iWidgetExtHandler.isExtViewMode();
        }
        LGLog.i(TAG, "isExtViewMode , mExtViewHandlerImpl is null");
        return false;
    }

    public static void cancelExtViewMode() {
        ConciergeBoardMngr conciergeBoardMngr = sInstance;
        if (conciergeBoardMngr != null) {
            conciergeBoardMngr.cancelExtView();
        } else {
            LGLog.i(TAG, "sCancelExtViewMode , sInstance is null");
        }
    }

    private void cancelExtView() {
        IWidgetExtHandler iWidgetExtHandler = this.mExtViewHandlerImpl;
        if (iWidgetExtHandler != null) {
            iWidgetExtHandler.cancelExtViewMode();
        } else {
            LGLog.i(TAG, "cancelExtViewMode , mExtViewHandlerImpl is null");
        }
    }

    public static void enableConciergeExtView(boolean enable) {
        sIsExtViewEnabled = enable;
        ConciergeBoardMngr conciergeBoardMngr = sInstance;
        if (conciergeBoardMngr == null || !enable) {
            return;
        }
        conciergeBoardMngr.notifyExtViewAvailable();
    }

    public void notifyExtViewAvailable() {
        this.mExtViewHandlerImpl.notifyExtViewAvailable();
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewHostAdapter
    public void setWidgetExtHandler(IWidgetExtHandler widgetExtHandler) {
        this.mExtViewHandlerImpl = widgetExtHandler;
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewHostAdapter
    public int getScreenHeight() {
        Rect rect = new Rect();
        this.mLauncher.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.bottom;
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewHostAdapter
    public void attachWidgetToExtLayer(View view, FrameLayout.LayoutParams lp) {
        ViewGroup viewGroup;
        if (view == null || (viewGroup = this.mDragLayer) == null) {
            LGLog.i(TAG, "attachWidgetToExtLayer , view = " + view + ", mDragLayer = " + this.mDragLayer);
            return;
        }
        ((DragLayer) viewGroup).setForceIgnoreInsets(true);
        this.mDragLayer.addView(view, lp);
        HomescreenBlurManager homescreenBlurManager = HomescreenBlurManager.getInstance(this.mLauncher);
        if (homescreenBlurManager == null) {
            LGLog.i(TAG, "attachWidgetToExtLayer , manager is null");
        } else {
            homescreenBlurManager.showBackground(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER, 0);
        }
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewHostAdapter
    public void detachWidgetFromExtLayer(View view) {
        if (view == null || this.mDragLayer == null) {
            LGLog.i(TAG, "detachWidgetFromExtLayer , view = " + view + ", mDragLayer = " + this.mDragLayer);
            return;
        }
        HomescreenBlurManager homescreenBlurManager = HomescreenBlurManager.getInstance(this.mLauncher);
        if (homescreenBlurManager == null) {
            LGLog.i(TAG, "detachWidgetFromExtLayer , manager is null");
            return;
        }
        homescreenBlurManager.hideBackground(HomescreenBlurManager.BackgroundType.TOP_DRAGLAYER, 0);
        this.mDragLayer.removeView(view);
        ((DragLayer) this.mDragLayer).setForceIgnoreInsets(false);
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewHostAdapter
    public boolean isExtViewAvailable() {
        LGLog.i(TAG, "isExtViewAvailable = " + sIsExtViewEnabled);
        return sIsExtViewEnabled;
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewHostAdapter
    public int getWorkSpaceWidth() {
        ViewGroup viewGroup = this.mDragLayer;
        if (viewGroup == null) {
            return -1;
        }
        return viewGroup.getWidth();
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewHostAdapter
    public View getHostViewBlurLayout(View hostView) {
        WidgetBlurLayout widgetBlurLayout = ((LauncherAppWidgetHostView) hostView).getWidgetBlurLayout();
        LGLog.i(TAG, "getHostViewBlurLayout = " + widgetBlurLayout);
        return widgetBlurLayout;
    }

    @Override // com.lge.lgewidgetlib.extview.IExtViewHostAdapter
    public int calcExtWidgetBg(float alphaFraction, boolean keepWidgetBg) {
        WallpaperBlurredImageController wallpaperBlurredImageController = WallpaperBlurredImageController.getInstance(this.mLauncher);
        if (wallpaperBlurredImageController == null) {
            LGLog.i(TAG, "getBlurCommonColor controller is null");
            return ColorUtils.setAlphaComponent(Utilities.sWhite, 51);
        }
        if (wallpaperBlurredImageController.isLiveWallpaperMode()) {
            return ColorUtils.setAlphaComponent(5329233, (int) (204 * alphaFraction));
        }
        int commonColor = wallpaperBlurredImageController.getCommonColor();
        if (keepWidgetBg) {
            return ColorUtils.compositeColors(ColorUtils.setAlphaComponent(Utilities.sWhite, (int) (51 * alphaFraction)), commonColor);
        }
        return ColorUtils.setAlphaComponent(ColorUtils.compositeColors(ColorUtils.setAlphaComponent(Utilities.sWhite, 51), commonColor), (int) (Color.alpha(r5) * alphaFraction));
    }
}
