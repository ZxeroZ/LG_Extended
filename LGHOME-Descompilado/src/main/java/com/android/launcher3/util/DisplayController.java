package com.android.launcher3.util;

import android.app.ActivityManagerEx;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.util.ArraySet;
import android.util.Log;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.WindowMetrics;
import com.android.launcher3.Utilities;
import com.android.launcher3.uioverrides.ApiWrapper;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.lge.display.DisplayManagerHelper;
import com.lge.launcher3.util.LGLog;
import defpackage.$$Nest$Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class DisplayController implements DisplayManager.DisplayListener, ComponentCallbacks {
    public static final int CHANGE_ACTIVE_SCREEN = 1;
    public static final int CHANGE_ALL = 63;
    public static final int CHANGE_DENSITY = 8;
    public static final int CHANGE_FRAME_DELAY = 4;
    public static final int CHANGE_MULTIWINDOW_MODE = 32;
    public static final int CHANGE_NEW_DISPLAY = 4;
    public static final int CHANGE_ROTATION = 2;
    public static final int CHANGE_SUPPORTED_BOUNDS = 16;
    private static final String TAG = "DisplayController";
    private final Context mContext;
    private final DisplayManager mDM;
    private DisplayManagerHelper mDisplayManagerHelper;
    private final int mId;
    private Info mInfo;
    private int mSubDisplayId;
    private final Context mWindowContext;
    public static final MainThreadInitializedObject<DisplayController> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.launcher3.util.-$$Lambda$DisplayController$QjdXHmv721WSoJW-MI-3oZlD7FY
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return DisplayController.m292lambda$QjdXHmv721WSoJWMI3oZlD7FY(context);
        }
    });
    public static int CHANGE_DISPLAY_MSG = 1;
    private final ArrayList<DisplayInfoChangeListener> mListeners = new ArrayList<>();
    private HashMap<Integer, Info> mInfos = new HashMap<>();

    public interface DisplayInfoChangeListener {
        void onDisplayInfoChanged(Context context, Info info, int flags);
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR (r1v0 android.content.Context) A[MD:(android.content.Context):void (m)] call: com.android.launcher3.util.DisplayController.<init>(android.content.Context):void type: CONSTRUCTOR */
    /* JADX INFO: renamed from: lambda$QjdXHmv721WSoJW-MI-3oZlD7FY, reason: not valid java name */
    public static /* synthetic */ DisplayController m292lambda$QjdXHmv721WSoJWMI3oZlD7FY(Context context) {
        return new DisplayController(context);
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public final void onDisplayAdded(int displayId) {
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public final void onDisplayRemoved(int displayId) {
    }

    @Override // android.content.ComponentCallbacks
    public final void onLowMemory() {
    }

    private DisplayController(Context context) {
        Display display;
        this.mContext = context;
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        this.mDM = displayManager;
        DisplayManagerHelper displayManagerHelper = new DisplayManagerHelper(context);
        this.mDisplayManagerHelper = displayManagerHelper;
        this.mSubDisplayId = displayManagerHelper.getMultiDisplayId();
        Display display2 = displayManager.getDisplay(0);
        $$Nest$Constructor __nest_constructor = null;
        if (Utilities.ATLEAST_S) {
            Context contextCreateWindowContext = context.createWindowContext(display2, 2, null);
            this.mWindowContext = contextCreateWindowContext;
            contextCreateWindowContext.registerComponentCallbacks(this);
        } else {
            this.mWindowContext = null;
            context.registerReceiver(new SimpleBroadcastReceiver(new Consumer() { // from class: com.android.launcher3.util.-$$Lambda$DisplayController$5DoMBITmdII83T8cfG2j5Px436E
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.onConfigChanged((Intent) obj);
                }
            }), new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
        }
        ArraySet arraySet = new ArraySet();
        for (Display display3 : displayManager.getDisplays()) {
            if (ApiWrapper.isInternalDisplay(display2) && display3.getDisplayId() != 0) {
                Point point = new Point();
                display3.getRealSize(point);
                arraySet.add(new PortraitSize(point.x, point.y));
            }
        }
        Info info = new Info(getDisplayInfoContext(display2), display2, arraySet, __nest_constructor);
        this.mInfo = info;
        this.mId = info.id;
        this.mInfos.put(Integer.valueOf(this.mInfo.id), this.mInfo);
        int i = this.mSubDisplayId;
        if (i != -1 && (display = this.mDM.getDisplay(i)) != null) {
            Info info2 = new Info(getDisplayInfoContext(display), display, arraySet, __nest_constructor);
            this.mInfos.put(Integer.valueOf(info2.id), info2);
        }
        this.mDM.registerDisplayListener(this, Executors.UI_HELPER_EXECUTOR.getHandler());
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public final void onDisplayChanged(int displayId) {
        int i;
        LGLog.d(TAG, "display changed: " + displayId);
        int i2 = this.mSubDisplayId;
        if (i2 == -1) {
            int i3 = this.mId;
            if (displayId != i3) {
                LGLog.d(TAG, "onDisplayChanged: skip. (" + i3 + ", " + i2 + ")(" + displayId + ")");
                return;
            }
        } else if (i2 != displayId && displayId != (i = this.mId)) {
            LGLog.d(TAG, "onDisplayChanged: skip. (" + i + ", " + i2 + ")(" + displayId + ")");
            return;
        }
        Display display = this.mDM.getDisplay(displayId);
        if (display == null) {
            return;
        }
        handleInfoChange(display);
    }

    public static int getSingleFrameMs(Context context) {
        return INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getInfo().singleFrameMs;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConfigChanged(Intent intent) {
        Configuration configuration = this.mContext.getResources().getConfiguration();
        if (this.mInfo.fontScale == configuration.fontScale && this.mInfo.densityDpi == configuration.densityDpi) {
            return;
        }
        Log.d(TAG, "Configuration changed, notifying listeners");
        Display display = this.mDM.getDisplay(0);
        if (display != null) {
            handleInfoChange(display);
        }
    }

    @Override // android.content.ComponentCallbacks
    public final void onConfigurationChanged(Configuration config) {
        Display display = this.mWindowContext.getDisplay();
        if (config.densityDpi == this.mInfo.densityDpi && config.fontScale == this.mInfo.fontScale && display.getRotation() == this.mInfo.rotation && this.mInfo.mScreenSizeDp.equals(new PortraitSize(config.screenHeightDp, config.screenWidthDp))) {
            return;
        }
        handleInfoChange(display);
    }

    public void addChangeListener(DisplayInfoChangeListener listener) {
        this.mListeners.add(listener);
    }

    public void removeChangeListener(DisplayInfoChangeListener listener) {
        this.mListeners.remove(listener);
    }

    public Info getInfo() {
        return this.mInfo;
    }

    private Context getDisplayInfoContext(Display display) {
        return Utilities.ATLEAST_S ? this.mWindowContext : this.mContext.createDisplayContext(display);
    }

    private void handleInfoChange(Display display) {
        Info info = this.mInfo;
        final int displayId = display.getDisplayId();
        if (info.mAllSizes.size() > 1) {
            Set unused = info.mAllSizes;
        } else {
            Collections.emptySet();
        }
        final Context displayInfoContext = getDisplayInfoContext(display);
        Info info2 = new Info(displayInfoContext, display);
        LGLog.d(TAG, "onDisplayChanged : old Info = " + info);
        LGLog.d(TAG, "onDisplayChanged : new Info = " + info2);
        final int i = 0;
        if (displayId != 0) {
            i = 4;
            info = info2;
        }
        if (!info2.mScreenSizeDp.equals(info.mScreenSizeDp)) {
            i |= 1;
        }
        if (info2.rotation != info.rotation) {
            i |= 2;
        }
        if (info2.singleFrameMs != info.singleFrameMs) {
            i |= 4;
        }
        if (info2.densityDpi != info.densityDpi || info2.fontScale != info.fontScale) {
            i |= 8;
        }
        if (!info2.supportedBounds.equals(info.supportedBounds)) {
            i |= 16;
        }
        if (isMultiWindowRunning()) {
            i |= 32;
        }
        if (i != 0) {
            this.mInfos.put(Integer.valueOf(info2.id), info2);
            if (displayId == 0) {
                this.mInfo = info2;
            }
            if ((i & 2) != 0) {
                Executors.MAIN_EXECUTOR.execute(new Runnable() { // from class: com.android.launcher3.util.-$$Lambda$DisplayController$bpq9Q7tw8CDrJ299eWG9ZrqKbNc
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$handleInfoChange$1$DisplayController(displayInfoContext, i, displayId);
                    }
                });
            } else {
                Executors.MAIN_EXECUTOR.postDelayed(new Runnable() { // from class: com.android.launcher3.util.-$$Lambda$DisplayController$dUMwDgAeKfp2M2lOCY5LeGR0kfU
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$handleInfoChange$2$DisplayController(displayInfoContext, i, displayId);
                    }
                }, 500);
            }
        }
    }

    private boolean isMultiWindowRunning() {
        return ((ActivityManagerEx) this.mContext.getSystemService("activity")).inMultiWindowMode();
    }

    public Info getInfo(int displayId) {
        if (this.mInfos.get(Integer.valueOf(displayId)) != null) {
            return this.mInfos.get(Integer.valueOf(displayId));
        }
        LGLog.d(TAG, "getInfo : info(" + displayId + ") is null");
        if (displayId != 0 && ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).getDisplay(displayId) != null) {
            onDisplayChanged(displayId);
        }
        if (this.mInfos.get(Integer.valueOf(displayId)) == null) {
            return this.mInfo;
        }
        return this.mInfos.get(Integer.valueOf(displayId));
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$handleInfoChange$1$DisplayController(Landroid/content/Context;II)V */
    /* JADX DEBUG: Method merged with bridge method: lambda$handleInfoChange$2$DisplayController(Landroid/content/Context;II)V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: notifyChange, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public void lambda$handleInfoChange$2$DisplayController(Context context, int flags, int displayId) {
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            this.mListeners.get(size).onDisplayInfoChanged(context, this.mInfos.get(Integer.valueOf(displayId)), flags);
        }
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$handleInfoChange$0(Landroid/content/Context;I)V */
    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: notifyChange, reason: merged with bridge method [inline-methods] */
    public void lambda$handleInfoChange$0(Context context, int flags) {
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            this.mListeners.get(size).onDisplayInfoChanged(context, this.mInfo, flags);
        }
    }

    public static class Info {
        public final Point currentSize;
        public final int densityDpi;
        public final float fontScale;
        public final int id;
        private final Set<PortraitSize> mAllSizes;
        private final PortraitSize mScreenSizeDp;
        public final int rotation;
        public final int singleFrameMs;
        public final Set<WindowBounds> supportedBounds;

        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR (r1v0 android.content.Context), (r2v0 android.content.Context), (r3v0 android.view.Display) A[MD:(android.content.Context, android.view.Display, java.util.Set<com.android.launcher3.util.DisplayController$PortraitSize>):void (m)] call: com.android.launcher3.util.DisplayController.Info.<init>(android.content.Context, android.view.Display, java.util.Set):void type: THIS */
        /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.content.Context */
        /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: android.view.Display */
        /* JADX WARN: Multi-variable type inference failed */
        /* synthetic */ Info(Context context, Context context2, Display display, Set<PortraitSize> set) {
            this(context, context2, display);
        }

        public Info(Context context, Display display) {
            this(context, display, Collections.emptySet());
        }

        private Info(Context context, Display display, Set<PortraitSize> extraDisplaysSizes) {
            ArraySet arraySet = new ArraySet();
            this.supportedBounds = arraySet;
            this.id = display.getDisplayId();
            DisplayInfo displayInfo = new DisplayInfo();
            display.getDisplayInfo(displayInfo);
            int i = displayInfo.rotation;
            this.rotation = i;
            Configuration configuration = context.getResources().getConfiguration();
            this.fontScale = configuration.fontScale;
            int i2 = configuration.densityDpi;
            this.densityDpi = i2;
            this.mScreenSizeDp = new PortraitSize(configuration.screenHeightDp, configuration.screenWidthDp);
            this.singleFrameMs = DisplayController.getSingleFrameMs(display);
            Point point = new Point();
            this.currentSize = point;
            point.x = displayInfo.logicalWidth;
            point.y = displayInfo.logicalHeight;
            if (extraDisplaysSizes.isEmpty() || !Utilities.ATLEAST_S) {
                Point point2 = new Point();
                Point point3 = new Point();
                display.getCurrentSizeRange(point2, point3);
                int iMin = Math.min(point.x, point.y);
                int iMax = Math.max(point.x, point.y);
                arraySet.add(new WindowBounds(iMin, iMax, point2.x, point3.y, i));
                arraySet.add(new WindowBounds(iMax, iMin, point3.x, point2.y, i));
                this.mAllSizes = Collections.singleton(new PortraitSize(point.x, point.y));
                return;
            }
            ArraySet arraySet2 = new ArraySet(extraDisplaysSizes);
            this.mAllSizes = arraySet2;
            arraySet2.add(new PortraitSize(point.x, point.y));
            WindowManagerCompat.getDisplayProfiles(context, arraySet2, i2, true).forEach(new Consumer() { // from class: com.android.launcher3.util.-$$Lambda$DisplayController$Info$dudgTN24DjQN6vIedNhTGkXCiiM
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$new$0$DisplayController$Info((WindowMetrics) obj);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$DisplayController$Info(WindowMetrics windowMetrics) {
            this.supportedBounds.add(WindowBounds.fromWindowMetrics(windowMetrics, this.rotation));
        }

        public boolean isTablet(WindowBounds bounds) {
            return Utilities.dpiFromPx((float) Math.min(bounds.bounds.width(), bounds.bounds.height()), this.densityDpi) >= 600.0f;
        }
    }

    public static class PortraitSize {
        public final int height;
        public final int width;

        public PortraitSize(int w, int h) {
            this.width = Math.min(w, h);
            this.height = Math.max(w, h);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PortraitSize portraitSize = (PortraitSize) o;
            return this.width == portraitSize.width && this.height == portraitSize.height;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.width), Integer.valueOf(this.height));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getSingleFrameMs(Display display) {
        float refreshRate = display.getRefreshRate();
        if (refreshRate > 0.0f) {
            return (int) (1000.0f / refreshRate);
        }
        return 16;
    }
}
