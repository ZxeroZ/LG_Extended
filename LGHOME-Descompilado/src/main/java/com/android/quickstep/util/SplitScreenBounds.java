package com.android.quickstep.util;

import android.content.Context;
import android.graphics.Insets;
import android.graphics.Rect;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.WindowBounds;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class SplitScreenBounds {
    public static final SplitScreenBounds INSTANCE = new SplitScreenBounds();
    private static final String TAG = "SplitScreenBounds";
    private WindowBounds mBounds;
    private final ArrayList<OnChangeListener> mListeners = new ArrayList<>();

    public interface OnChangeListener {
        void onSecondaryWindowBoundsChanged();
    }

    private SplitScreenBounds() {
    }

    public void setSecondaryWindowBounds(WindowBounds bounds, Context context) {
        if (bounds.equals(this.mBounds)) {
            return;
        }
        if (bounds != null && this.mBounds != null) {
            LGLog.i(TAG, "setSecondaryWindowBounds: new(" + bounds.bounds + ", " + bounds.availableSize.x + ", " + bounds.availableSize.y + ", " + bounds.insets + ", " + bounds.rotation + "), old(" + this.mBounds.bounds + ", " + this.mBounds.availableSize.x + ", " + this.mBounds.availableSize.y + ", " + this.mBounds.insets + ", " + this.mBounds.rotation + ")");
        }
        this.mBounds = bounds;
        Iterator<OnChangeListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onSecondaryWindowBoundsChanged();
        }
    }

    public WindowBounds getSecondaryWindowBounds(Context context) {
        int i = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getInfo(0).rotation;
        WindowBounds windowBounds = this.mBounds;
        if (windowBounds == null || i != windowBounds.rotation) {
            WindowBounds windowBoundsCreateDefaultWindowBounds = createDefaultWindowBounds(context);
            this.mBounds = windowBoundsCreateDefaultWindowBounds;
            LGLog.d(TAG, "getSecondaryWindowBounds: " + windowBoundsCreateDefaultWindowBounds.bounds + ", " + this.mBounds.availableSize.x + ", " + this.mBounds.availableSize.y + ", " + this.mBounds.insets + ", " + this.mBounds.rotation);
        }
        return this.mBounds;
    }

    private static WindowBounds createDefaultWindowBounds(Context context) {
        WindowMetrics maximumWindowMetrics = ((WindowManager) context.getSystemService(WindowManager.class)).getMaximumWindowMetrics();
        Insets insets = maximumWindowMetrics.getWindowInsets().getInsets(WindowInsets.Type.systemBars());
        WindowBounds windowBounds = new WindowBounds(maximumWindowMetrics.getBounds(), new Rect(insets.left, insets.top, insets.right, insets.bottom), DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getInfo(0).rotation);
        int i = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getInfo(0).rotation;
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.multi_window_task_divider_size) / 2;
        if (i == 0 || i == 2) {
            windowBounds.bounds.top = windowBounds.insets.top + (windowBounds.availableSize.y / 2) + dimensionPixelSize;
            windowBounds.insets.top = 0;
        } else {
            windowBounds.bounds.left = windowBounds.insets.left + (windowBounds.availableSize.x / 2) + dimensionPixelSize;
            windowBounds.insets.left = 0;
        }
        return new WindowBounds(windowBounds.bounds, windowBounds.insets, windowBounds.rotation);
    }

    public void addOnChangeListener(OnChangeListener listener) {
        this.mListeners.add(listener);
    }

    public void removeOnChangeListener(OnChangeListener listener) {
        this.mListeners.remove(listener);
    }
}
