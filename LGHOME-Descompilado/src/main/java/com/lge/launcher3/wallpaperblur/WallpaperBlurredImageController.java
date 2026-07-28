package com.lge.launcher3.wallpaperblur;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.util.DisplayController;
import com.lge.launcher3.R;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.OrientationUtils;
import com.lge.launcher3.util.TalkBackUtils;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.util.ViewPosition;
import com.lge.launcher3.util.WindowUtils;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.AdaptiveColor;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.AdaptiveColorEngine;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine;
import com.lge.launcher3.wallpapermotion.WallpaperMotionManager;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperBlurredImageController implements AdaptiveColorEngine.IAdaptiveColorEngineListener {
    public static final boolean DEBUG = false;
    public static final String TAG = "WallpaperBlurredImageController";
    private static WallpaperBlurredImageController sInstance;
    private AdaptiveColorEngine mAdaptiveColorEngine;
    private Context mContext;
    private ArrayList<OnWallpaperChangeListener> mOnWallpaperChangeListeners;
    private final BroadcastReceiver mWallpaperChangeReceiver;
    private WallpaperInfoManager mWallpaperInfoManager;
    private Launcher mLauncher = null;
    private WallpaperMotionManager mWallpaperMotionManager = null;
    private int mStaticWallpaperCommonColor = 0;
    private int mLiveWallpaperCommonColor = 0;
    private final Object mLock = new Object();

    public interface OnWallpaperChangeListener {
        void onWallpaperBlurredImageChanged(int adaptiveColor);

        void onWallpaperChanged();
    }

    public static WallpaperBlurredImageController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WallpaperBlurredImageController(context.getApplicationContext());
        }
        return sInstance;
    }

    public void setLauncher(Launcher launcher) {
        if (launcher == null) {
            return;
        }
        this.mLauncher = launcher;
    }

    private WallpaperBlurredImageController(Context context) {
        this.mContext = null;
        this.mAdaptiveColorEngine = null;
        this.mWallpaperInfoManager = null;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (WallpaperBlurredImageController.this.isDisabled()) {
                    return;
                }
                if (Utilities.ACTION_WALLPAPER_CHANGED.equals(intent.getAction())) {
                    WallpaperBlurredImageController.this.notifyWallpaperChange();
                    WallpaperBlurredImageController.this.loadWallpaperBlurredImage(true);
                }
            }
        };
        this.mWallpaperChangeReceiver = broadcastReceiver;
        LGLog.i(TAG, "Create a new WallpaperBlurredImageController instance.");
        this.mContext = context;
        this.mWallpaperInfoManager = new WallpaperInfoManager(this.mContext);
        setupCommonColor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Utilities.ACTION_WALLPAPER_CHANGED);
        this.mContext.registerReceiver(broadcastReceiver, intentFilter);
        if (this.mAdaptiveColorEngine == null) {
            AdaptiveColorEngine adaptiveColorEngine = AdaptiveColorEngine.getInstance();
            this.mAdaptiveColorEngine = adaptiveColorEngine;
            adaptiveColorEngine.init(this.mContext);
            this.mAdaptiveColorEngine.addListener(this);
        }
    }

    public void loadWallpaperBlurredImage(boolean changed) {
        AdaptiveColorEngine adaptiveColorEngine;
        if (this.mContext == null) {
            LGLog.i(TAG, "Context is null");
            return;
        }
        if (!changed && (adaptiveColorEngine = this.mAdaptiveColorEngine) != null && adaptiveColorEngine.getBlurEngine().hasBlurredImage()) {
            Point displayRealSize = WindowUtils.getDisplayRealSize(this.mContext);
            Point blurreImageSize = this.mAdaptiveColorEngine.getBlurEngine().getBlurreImageSize();
            if (displayRealSize != null) {
                displayRealSize.x >>= 2;
                displayRealSize.y >>= 2;
                if (blurreImageSize.x >= displayRealSize.x || blurreImageSize.y >= displayRealSize.y) {
                    return;
                }
            }
        }
        if (isDisabled()) {
            return;
        }
        new AsyncTask<Void, Void, Void>() { // from class: com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController.1
            /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Void... params) {
                synchronized (WallpaperBlurredImageController.this.mLock) {
                    LGLog.i(WallpaperBlurredImageController.TAG, "loadWallpaperBlurredImage()");
                    if (WallpaperBlurredImageController.this.mWallpaperInfoManager == null) {
                        LGLog.i(WallpaperBlurredImageController.TAG, "loadWallpaperBlurredImage() : mWallpaperInfoManager is null.");
                        return null;
                    }
                    Drawable wallpaperDrawable = WallpaperBlurredImageController.this.mWallpaperInfoManager.getWallpaperDrawable();
                    if (wallpaperDrawable != null && (wallpaperDrawable instanceof BitmapDrawable)) {
                        Bitmap bitmap = ((BitmapDrawable) wallpaperDrawable).getBitmap();
                        if (bitmap == null) {
                            LGLog.e(WallpaperBlurredImageController.TAG, "loadWallpaperBlurredImage() : Wallpaper drawable haven't Bitmap image.");
                            return null;
                        }
                        if (WallpaperBlurredImageController.this.mAdaptiveColorEngine == null) {
                            LGLog.e(WallpaperBlurredImageController.TAG, "loadWallpaperBlurredImage() : mAdaptiveColorEngine is null.");
                            return null;
                        }
                        WallpaperBlurredImageController.this.mAdaptiveColorEngine.setImage(bitmap);
                        return null;
                    }
                    LGLog.e(WallpaperBlurredImageController.TAG, "loadWallpaperBlurredImage() : Wallpaper drawable is null or not BitmapDrawable.");
                    return null;
                }
            }
        }.execute(new Void[0]);
    }

    public Bitmap getBlurredImageForChildOfWorkspace(View child) {
        if (isDisabled() || !hasBlurredImage()) {
            return null;
        }
        Rect rect = new Rect();
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        ViewPosition.getViewRectRelativeToSelf(dragLayer, child, rect);
        if (isValidSize(dragLayer, rect)) {
            return getBlurredImage(getWallpaperOffsetForChildOfWorkspace(child) + rect.left, rect.top, rect.width(), rect.height());
        }
        return null;
    }

    private int getWallpaperOffsetForChildOfWorkspace(View child) {
        Workspace.WallpaperOffsetInterpolator wallpaperOffsetInterpolator = getWallpaperOffsetInterpolator();
        if (wallpaperOffsetInterpolator == null) {
            return 0;
        }
        float maxOffset = wallpaperOffsetInterpolator.getMaxOffset();
        Launcher launcher = this.mLauncher;
        Workspace workspace = launcher != null ? launcher.getWorkspace() : null;
        if (workspace == null) {
            return 0;
        }
        int iIndexOfChildExcludingEmptyAndCustom = workspace.indexOfChildExcludingEmptyAndCustom(workspace.getParentCellLayoutForView((View) child.getParent()));
        int numScreensExcludingEmptyAndCustom = workspace.getNumScreensExcludingEmptyAndCustom() - 1;
        if (com.android.launcher3.Utilities.isRtl(this.mLauncher.getResources())) {
            iIndexOfChildExcludingEmptyAndCustom = numScreensExcludingEmptyAndCustom - iIndexOfChildExcludingEmptyAndCustom;
        }
        return (int) (this.mWallpaperInfoManager.getRealWallpaperMaxOffsetX() * maxOffset * (iIndexOfChildExcludingEmptyAndCustom / numScreensExcludingEmptyAndCustom));
    }

    public void setWallpaperMotionManager(WallpaperMotionManager wallpaperMotionManager) {
        this.mWallpaperMotionManager = wallpaperMotionManager;
    }

    public Bitmap getWallpaperImageForCurrentWorkspace() {
        if (isDisabled() || !hasBlurredImage()) {
            return null;
        }
        int i = WindowUtils.getDisplayRealSize(this.mContext).x;
        int i2 = WindowUtils.getDisplayRealSize(this.mContext).y;
        int wallpaperOffsetForCurrentWorkspace = getWallpaperOffsetForCurrentWorkspace();
        if (TalkBackUtils.isEnabled(this.mContext)) {
            int navigationBarHeight = WindowUtils.getNavigationBarHeight(this.mContext);
            if (OrientationUtils.isPortrait(this.mContext)) {
                i2 -= navigationBarHeight;
            } else {
                i -= navigationBarHeight;
            }
        }
        return getWallpaperImage(wallpaperOffsetForCurrentWorkspace, 0, i, i2);
    }

    public Bitmap getBlurredImageForCurrentWorkspace(boolean screenshot) {
        if (isDisabled() || !hasBlurredImage()) {
            return null;
        }
        Point displayRealSize = WindowUtils.getDisplayRealSize(this.mLauncher.getWindow().getDecorView());
        if (displayRealSize == null && (displayRealSize = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(this.mLauncher).getInfo(0).currentSize) == null) {
            LGLog.w(TAG, "Display real size is null.", new int[0]);
            return null;
        }
        int i = displayRealSize.x;
        int i2 = displayRealSize.y;
        if (screenshot) {
            return getScreenshotBlurredImage(i, i2);
        }
        int wallpaperOffsetForCurrentWorkspace = getWallpaperOffsetForCurrentWorkspace();
        if (TalkBackUtils.isEnabled(this.mContext)) {
            int navigationBarHeight = WindowUtils.getNavigationBarHeight(this.mContext);
            if (OrientationUtils.isPortrait(this.mContext)) {
                i2 -= navigationBarHeight;
            } else {
                i -= navigationBarHeight;
            }
        }
        return getBlurredImage(wallpaperOffsetForCurrentWorkspace, 0, i, i2);
    }

    public Bitmap getBlurredImageForCurrentWorkspace(int radius) {
        if (isDisabled() || !hasBlurredImage()) {
            return null;
        }
        if (!StaticBlurEngine.getInstance().isRealtimeBlurStarted()) {
            int wallpaperOffsetForCurrentWorkspace = getWallpaperOffsetForCurrentWorkspace();
            int i = WindowUtils.getDisplayRealSize(this.mContext).x;
            int i2 = WindowUtils.getDisplayRealSize(this.mContext).y;
            if (TalkBackUtils.isEnabled(this.mContext)) {
                int navigationBarHeight = WindowUtils.getNavigationBarHeight(this.mContext);
                if (OrientationUtils.isPortrait(this.mContext)) {
                    i2 -= navigationBarHeight;
                } else {
                    i -= navigationBarHeight;
                }
            }
            startRealtimeBlur(wallpaperOffsetForCurrentWorkspace, 0, i, i2);
        }
        return getRealtimeBlurImage(radius);
    }

    public int getWallpaperOffsetForCurrentWorkspace() {
        Workspace.WallpaperOffsetInterpolator wallpaperOffsetInterpolator;
        if (isDisabled() || isLiveWallpaperMode() || (wallpaperOffsetInterpolator = getWallpaperOffsetInterpolator()) == null) {
            return 0;
        }
        return (int) (this.mWallpaperInfoManager.getRealWallpaperMaxOffsetX() * wallpaperOffsetInterpolator.getCurrX());
    }

    public Bitmap getBlurredImageForFullscreenInCenter() {
        if (isDisabled() || !hasBlurredImage()) {
            return null;
        }
        Point displayRealSize = WindowUtils.getDisplayRealSize(this.mContext);
        return getBlurredImage(getWallpaperOffsetInCenter(), 0, displayRealSize.x, displayRealSize.y);
    }

    private int getWallpaperOffsetInCenter() {
        return (int) (this.mWallpaperInfoManager.getRealWallpaperMaxOffsetX() * 0.5f);
    }

    private boolean isValidSize(View root, Rect childRect) {
        return childRect.left + childRect.width() <= root.getLeft() + root.getWidth() && childRect.top + childRect.height() <= root.getTop() + root.getHeight();
    }

    public Bitmap getBlurredImage(int x, int y, int width, int height) {
        Bitmap blurImage;
        int wallpaperStartOffsetX;
        if (isDisabled()) {
            return null;
        }
        if (!isLiveWallpaperMode()) {
            WallpaperMotionManager wallpaperMotionManager = this.mWallpaperMotionManager;
            if (wallpaperMotionManager != null && wallpaperMotionManager.isRunning()) {
                Point offset = this.mWallpaperMotionManager.getOffset();
                String str = TAG;
                LGLog.i(str, String.format("sensor x=%d y=%d", Integer.valueOf(offset.x), Integer.valueOf(offset.y)));
                Point wallpaperStartOffset = this.mWallpaperInfoManager.getWallpaperStartOffset();
                LGLog.i(str, String.format("startOffset x=%d y=%d", Integer.valueOf(wallpaperStartOffset.x), Integer.valueOf(wallpaperStartOffset.y)));
                int i = (int) ((offset.x / 100.0f) * wallpaperStartOffset.x * 2);
                wallpaperStartOffsetX = wallpaperStartOffset.x + i;
                y = wallpaperStartOffset.y + ((int) ((offset.y / 100.0f) * wallpaperStartOffset.y * 2));
            } else {
                wallpaperStartOffsetX = this.mWallpaperInfoManager.getWallpaperStartOffsetX() + toRealSize(x);
                y = this.mWallpaperInfoManager.getWallpaperStartOffsetY() + toRealSize(y);
            }
            x = wallpaperStartOffsetX;
            width = toRealSize(width);
            height = toRealSize(height);
        }
        try {
            if (isLiveWallpaperMode() && !this.mLauncher.getDeviceProfile().isLandscape) {
                blurImage = StaticBlurEngine.getInstance().getBlurImage(x, y, Math.min(width, height), Math.max(width, height));
            } else {
                blurImage = StaticBlurEngine.getInstance().getBlurImage(x, y, width, height);
            }
            return blurImage;
        } catch (IllegalArgumentException e) {
            LGLog.e(TAG, "IllegalArgumentException on getBlurredImage ", e);
            return null;
        } catch (RuntimeException e2) {
            LGLog.e(TAG, "RuntimeException on getBlurredImage ", e2);
            return null;
        }
    }

    public Bitmap getWallpaperImage(int x, int y, int width, int height) {
        int wallpaperStartOffsetX;
        int realSize;
        if (isDisabled()) {
            return null;
        }
        WallpaperMotionManager wallpaperMotionManager = this.mWallpaperMotionManager;
        if (wallpaperMotionManager != null && wallpaperMotionManager.isRunning()) {
            Point pointPause = this.mWallpaperMotionManager.pause();
            String str = TAG;
            LGLog.i(str, String.format("sensor x=%d y=%d", Integer.valueOf(pointPause.x), Integer.valueOf(pointPause.y)));
            Point wallpaperStartOffset = this.mWallpaperInfoManager.getWallpaperStartOffset();
            LGLog.i(str, String.format("startOffset x=%d y=%d", Integer.valueOf(wallpaperStartOffset.x), Integer.valueOf(wallpaperStartOffset.y)));
            int i = (int) ((pointPause.x / 100.0f) * wallpaperStartOffset.x * 2);
            wallpaperStartOffsetX = wallpaperStartOffset.x + i;
            realSize = wallpaperStartOffset.y + ((int) ((pointPause.y / 100.0f) * wallpaperStartOffset.y * 2));
        } else {
            wallpaperStartOffsetX = this.mWallpaperInfoManager.getWallpaperStartOffsetX() + toRealSize(x);
            realSize = toRealSize(y) + this.mWallpaperInfoManager.getWallpaperStartOffsetY();
        }
        try {
            return StaticBlurEngine.getInstance().getWallpaperImage(wallpaperStartOffsetX, realSize, toRealSize(width), toRealSize(height));
        } catch (IllegalArgumentException e) {
            LGLog.e(TAG, "IllegalArgumentException on getWallpaperImage ", e);
            return null;
        } catch (RuntimeException e2) {
            LGLog.e(TAG, "RuntimeException on getWallpaperImage ", e2);
            return null;
        }
    }

    public Bitmap getScreenshotBlurredImage(int width, int height) {
        if (isDisabled()) {
            return null;
        }
        try {
            return StaticBlurEngine.getInstance().getScreenshotBlurImage(this.mContext, width, height);
        } catch (IllegalArgumentException e) {
            LGLog.e(TAG, "IllegalArgumentException on getScreenshotBlurredImage ", e);
            return null;
        } catch (RuntimeException e2) {
            LGLog.e(TAG, "RuntimeException on getScreenshotBlurredImage ", e2);
            return null;
        }
    }

    public void startRealtimeBlur(int x, int y, int width, int height) {
        int wallpaperStartOffsetX;
        int realSize;
        if (isDisabled()) {
            return;
        }
        WallpaperMotionManager wallpaperMotionManager = this.mWallpaperMotionManager;
        if (wallpaperMotionManager != null && wallpaperMotionManager.isRunning()) {
            Point pointPause = this.mWallpaperMotionManager.pause();
            String str = TAG;
            LGLog.i(str, String.format("sensor x=%d y=%d", Integer.valueOf(pointPause.x), Integer.valueOf(pointPause.y)));
            Point wallpaperStartOffset = this.mWallpaperInfoManager.getWallpaperStartOffset();
            LGLog.i(str, String.format("startOffset x=%d y=%d", Integer.valueOf(wallpaperStartOffset.x), Integer.valueOf(wallpaperStartOffset.y)));
            int i = (int) ((pointPause.x / 100.0f) * wallpaperStartOffset.x * 2);
            wallpaperStartOffsetX = wallpaperStartOffset.x + i;
            realSize = wallpaperStartOffset.y + ((int) ((pointPause.y / 100.0f) * wallpaperStartOffset.y * 2));
        } else {
            wallpaperStartOffsetX = this.mWallpaperInfoManager.getWallpaperStartOffsetX() + toRealSize(x);
            realSize = toRealSize(y) + this.mWallpaperInfoManager.getWallpaperStartOffsetY();
        }
        StaticBlurEngine.getInstance().startRealtimeBlur(this.mContext, wallpaperStartOffsetX, realSize, toRealSize(width), toRealSize(height));
    }

    public void stopRealTimeBlur() {
        StaticBlurEngine.getInstance().stopRealTimeBlur();
        WallpaperMotionManager wallpaperMotionManager = this.mWallpaperMotionManager;
        if (wallpaperMotionManager != null) {
            wallpaperMotionManager.resume();
        }
    }

    public Bitmap getRealtimeBlurImage(int radius) {
        if (isDisabled()) {
            return null;
        }
        try {
            return StaticBlurEngine.getInstance().getRealtimeBlurImage(radius);
        } catch (IllegalArgumentException e) {
            LGLog.e(TAG, "IllegalArgumentException on getRealtimeBlurImage ", e);
            return null;
        } catch (RuntimeException e2) {
            LGLog.e(TAG, "RuntimeException on getRealtimeBlurImage ", e2);
            return null;
        }
    }

    private int toRealSize(int value) {
        return (int) (value * this.mWallpaperInfoManager.getWallpaperScaledRatio());
    }

    private void setupCommonColor() {
        String itemInSharingContentTable = LauncherModel.getItemInSharingContentTable(this.mContext, LauncherSettings.SharingContents.WALLPAPER_COMMON_COLOR);
        this.mStaticWallpaperCommonColor = itemInSharingContentTable != null ? (int) Long.parseLong(itemInSharingContentTable, 16) : 0;
        this.mLiveWallpaperCommonColor = ColorUtils.setAlphaComponent(-7829368, (int) ((this.mContext.getResources().getInteger(R.integer.config_widget_bg_alpha_in_live_wallpaper_mode) / 100.0f) * 255.0f));
    }

    public int getCommonColor() {
        return !isLiveWallpaperMode() ? this.mStaticWallpaperCommonColor : this.mLiveWallpaperCommonColor;
    }

    private void updateStaticWallpaperCommonColor(int color) {
        if (this.mStaticWallpaperCommonColor == color) {
            return;
        }
        if (!MemoryUtils.hasAvailableFileSystemMemory(null, false)) {
            LGLog.i(TAG, "Memory is full. so updateStaticWallpaperCommonColor is canceled.");
        } else {
            LauncherModel.updateItemInSharingContentTable(this.mContext, LauncherSettings.SharingContents.WALLPAPER_COMMON_COLOR, Integer.toHexString(color));
            this.mStaticWallpaperCommonColor = color;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDisabled() {
        return !((WidgetBlurManager.getInstance(this.mContext).isDisabled() && HomescreenBlurManager.getInstance(this.mContext).isDisabled()) ? false : true);
    }

    public boolean isLiveWallpaperMode() {
        return this.mWallpaperInfoManager.isLiveWallpaperMode();
    }

    public boolean hasBlurredImage() {
        return StaticBlurEngine.getInstance().hasBlurredImage();
    }

    public void addOnWallpaperChangeListener(OnWallpaperChangeListener listener) {
        if (this.mOnWallpaperChangeListeners == null) {
            this.mOnWallpaperChangeListeners = new ArrayList<>();
        }
        if (this.mOnWallpaperChangeListeners.contains(listener)) {
            return;
        }
        this.mOnWallpaperChangeListeners.add(listener);
    }

    public void removeOnWallpaperChangeListener(OnWallpaperChangeListener listener) {
        ArrayList<OnWallpaperChangeListener> arrayList = this.mOnWallpaperChangeListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(listener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyWallpaperChange() {
        ArrayList<OnWallpaperChangeListener> arrayList = this.mOnWallpaperChangeListeners;
        if (arrayList == null) {
            return;
        }
        for (OnWallpaperChangeListener onWallpaperChangeListener : arrayList) {
            if (onWallpaperChangeListener != null) {
                onWallpaperChangeListener.onWallpaperChanged();
            }
        }
    }

    private void notifyWallpaperBlurredImageChange(int adaptiveColor) {
        ArrayList<OnWallpaperChangeListener> arrayList = this.mOnWallpaperChangeListeners;
        if (arrayList == null) {
            return;
        }
        for (OnWallpaperChangeListener onWallpaperChangeListener : arrayList) {
            if (onWallpaperChangeListener != null) {
                onWallpaperChangeListener.onWallpaperBlurredImageChanged(adaptiveColor);
            }
        }
    }

    @Override // com.lge.launcher3.wallpaperblur.adaptivecolorengine.AdaptiveColorEngine.IAdaptiveColorEngineListener
    public void onAdaptiveColorChanged(AdaptiveColor adaptiveColor) {
        if (isDisabled()) {
            return;
        }
        int color = adaptiveColor.getColor();
        LGLog.i(TAG, String.format("onAdaptiveColorChanged(adpativeColor = %s(%d))", Integer.toHexString(color), Integer.valueOf(color)));
        updateStaticWallpaperCommonColor(color);
        notifyWallpaperBlurredImageChange(color);
    }

    private Workspace.WallpaperOffsetInterpolator getWallpaperOffsetInterpolator() {
        Launcher launcher = this.mLauncher;
        Workspace workspace = launcher != null ? launcher.getWorkspace() : null;
        if (workspace == null) {
            return null;
        }
        return workspace.getWallpaperOffset();
    }

    public void destroy() {
        ArrayList<OnWallpaperChangeListener> arrayList = this.mOnWallpaperChangeListeners;
        if (arrayList != null && arrayList.size() > 0) {
            LGLog.i(TAG, "Cancel to destroy WallpaperBlurredImageController instance.because there is some activity to use it.");
            return;
        }
        LGLog.i(TAG, "Destroy WallpaperBlurredImageController instance.");
        this.mContext.unregisterReceiver(this.mWallpaperChangeReceiver);
        ArrayList<OnWallpaperChangeListener> arrayList2 = this.mOnWallpaperChangeListeners;
        if (arrayList2 != null) {
            arrayList2.clear();
            this.mOnWallpaperChangeListeners = null;
        }
        synchronized (this.mLock) {
            AdaptiveColorEngine adaptiveColorEngine = this.mAdaptiveColorEngine;
            if (adaptiveColorEngine != null) {
                adaptiveColorEngine.clear();
                this.mAdaptiveColorEngine = null;
            }
            this.mWallpaperInfoManager = null;
        }
        this.mLauncher = null;
        this.mContext = null;
        sInstance = null;
    }

    public WallpaperInfoManager getWallpaperInfoManager() {
        return this.mWallpaperInfoManager;
    }

    public Rect getWidgetBlurBackgroundRect(View view, Bitmap bitmap) {
        Rect rect = new Rect();
        ViewPosition.getViewRectRelativeToSelf(this.mLauncher.getDragLayer(), view, rect);
        int i = rect.left;
        int i2 = rect.top;
        int iWidth = rect.width();
        int iHeight = rect.height();
        float fMin = Math.min(bitmap.getHeight() / WindowUtils.getDisplayRealSize(this.mContext).y, 1.0f);
        Rect rect2 = new Rect();
        rect2.left = (int) (i * fMin);
        rect2.top = (int) (i2 * fMin);
        rect2.right = (int) ((i + iWidth) * fMin);
        rect2.bottom = (int) ((i2 + iHeight) * fMin);
        return rect2;
    }

    public int getWidgetPageIndex(View view) {
        Launcher launcher = this.mLauncher;
        Workspace workspace = launcher != null ? launcher.getWorkspace() : null;
        if (workspace == null) {
            return 0;
        }
        int iIndexOfChildExcludingEmptyAndCustom = workspace.indexOfChildExcludingEmptyAndCustom(workspace.getParentCellLayoutForView((View) view.getParent()));
        return com.android.launcher3.Utilities.isRtl(this.mLauncher.getResources()) ? (workspace.getNumScreensExcludingEmptyAndCustom() - 1) - iIndexOfChildExcludingEmptyAndCustom : iIndexOfChildExcludingEmptyAndCustom;
    }

    public View getCellLayout(View view) {
        Launcher launcher = this.mLauncher;
        Workspace workspace = launcher != null ? launcher.getWorkspace() : null;
        if (workspace == null) {
            return null;
        }
        return workspace.getParentCellLayoutForView((View) view.getParent());
    }
}
