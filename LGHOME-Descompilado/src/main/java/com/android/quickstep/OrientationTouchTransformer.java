package com.android.quickstep;

import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.SysUINavigationMode;
import com.android.quickstep.util.RecentsOrientedState;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
class OrientationTouchTransformer {
    private static final boolean DEBUG = false;
    private static final int MAX_ORIENTATIONS = 4;
    private static final int QUICKSTEP_ROTATION_UNINITIALIZED = -1;
    private static final String TAG = "OrientationTouchTransformer";
    private int mActiveTouchRotation;
    private QuickStepContractInfo mContractInfo;
    private int mCurrentDisplayRotation;
    private int mDisplayId;
    private boolean mEnableMultipleRegions;
    private OrientationRectF mLastRectTouched;
    private SysUINavigationMode.Mode mMode;
    private int mNavBarGesturalHeight;
    private final int mNavBarLargerGesturalHeight;
    private Resources mResources;
    private final Matrix mTmpMatrix = new Matrix();
    private final float[] mTmpPoint = new float[2];
    private SparseArray<OrientationRectF> mSwipeTouchRegions = new SparseArray<>(4);
    private final RectF mAssistantLeftRegion = new RectF();
    private final RectF mAssistantRightRegion = new RectF();
    private final RectF mOneHandedModeRegion = new RectF();
    private int mQuickStepStartingRotation = -1;

    interface QuickStepContractInfo {
        float getWindowCornerRadius();
    }

    OrientationTouchTransformer(Resources resources, SysUINavigationMode.Mode mode, QuickStepContractInfo contractInfo, int displayId) {
        this.mDisplayId = 0;
        this.mResources = resources;
        this.mMode = mode;
        this.mContractInfo = contractInfo;
        this.mDisplayId = displayId;
        int navbarSize = getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE);
        this.mNavBarGesturalHeight = navbarSize;
        this.mNavBarLargerGesturalHeight = ResourceUtils.getDimenByName(ResourceUtils.NAVBAR_BOTTOM_GESTURE_LARGER_SIZE, resources, navbarSize);
    }

    private void refreshTouchRegion(DisplayController.Info info, Resources newRes) {
        this.mResources = newRes;
        this.mSwipeTouchRegions.clear();
        resetSwipeRegions(info);
    }

    void setNavigationMode(SysUINavigationMode.Mode newMode, DisplayController.Info info, Resources newRes) {
        if (this.mMode == newMode) {
            return;
        }
        this.mMode = newMode;
        refreshTouchRegion(info, newRes);
    }

    void setGesturalHeight(int newGesturalHeight, DisplayController.Info info, Resources newRes) {
        if (this.mNavBarGesturalHeight == newGesturalHeight) {
            return;
        }
        this.mNavBarGesturalHeight = newGesturalHeight;
        refreshTouchRegion(info, newRes);
    }

    void createOrAddTouchRegion(DisplayController.Info info) {
        int i = info.rotation;
        this.mCurrentDisplayRotation = i;
        int i2 = this.mQuickStepStartingRotation;
        if (i2 > -1 && i == i2) {
            resetSwipeRegions(info);
            return;
        }
        OrientationRectF orientationRectF = this.mSwipeTouchRegions.get(i);
        if (orientationRectF == null || !isSameRect(info, orientationRectF)) {
            if (this.mEnableMultipleRegions) {
                this.mSwipeTouchRegions.put(this.mCurrentDisplayRotation, createRegionForDisplay(info));
            } else {
                resetSwipeRegions(info);
            }
        }
    }

    void enableMultipleRegions(boolean enableMultipleRegions, DisplayController.Info info) {
        boolean z = enableMultipleRegions && this.mMode != SysUINavigationMode.Mode.TWO_BUTTONS;
        this.mEnableMultipleRegions = z;
        if (z) {
            this.mQuickStepStartingRotation = info.rotation;
        } else {
            this.mActiveTouchRotation = 0;
            this.mQuickStepStartingRotation = -1;
        }
        resetSwipeRegions(info);
    }

    void setSingleActiveRegion(DisplayController.Info displayInfo) {
        this.mActiveTouchRotation = displayInfo.rotation;
        resetSwipeRegions(displayInfo);
    }

    private void resetSwipeRegions(DisplayController.Info region) {
        int i = region.rotation;
        this.mCurrentDisplayRotation = i;
        OrientationRectF orientationRectFCreateRegionForDisplay = this.mSwipeTouchRegions.get(i);
        if (orientationRectFCreateRegionForDisplay == null || !isSameRect(region, orientationRectFCreateRegionForDisplay)) {
            orientationRectFCreateRegionForDisplay = createRegionForDisplay(region);
        }
        this.mSwipeTouchRegions.clear();
        this.mSwipeTouchRegions.put(this.mCurrentDisplayRotation, orientationRectFCreateRegionForDisplay);
        updateAssistantRegions(orientationRectFCreateRegionForDisplay);
        LGLog.d(TAG, "resetSwipeRegions : " + region.id + ", " + this.mDisplayId + ", mCurrentDisplayRotation = " + this.mCurrentDisplayRotation + ", mSwipeTouchRegions = " + this.mSwipeTouchRegions + ", regionToKeep = " + orientationRectFCreateRegionForDisplay);
    }

    private void resetSwipeRegions() {
        OrientationRectF orientationRectF = this.mSwipeTouchRegions.get(this.mCurrentDisplayRotation);
        this.mSwipeTouchRegions.clear();
        if (orientationRectF != null) {
            this.mSwipeTouchRegions.put(this.mCurrentDisplayRotation, orientationRectF);
            LGLog.d(TAG, "resetSwipeRegions : " + this.mDisplayId + ", mCurrentDisplayRotation = " + this.mCurrentDisplayRotation + ", mSwipeTouchRegions = " + this.mSwipeTouchRegions + ", regionToKeep = " + orientationRectF);
            updateAssistantRegions(orientationRectF);
        }
    }

    private OrientationRectF createRegionForDisplay(DisplayController.Info display) {
        Point point = new Point(display.currentSize);
        int i = display.rotation;
        OrientationRectF orientationRectF = new OrientationRectF(0.0f, 0.0f, point.x, point.y, i);
        if (this.mMode == SysUINavigationMode.Mode.NO_BUTTON) {
            orientationRectF.top = orientationRectF.bottom - getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE);
            updateAssistantRegions(orientationRectF);
        } else {
            this.mAssistantLeftRegion.setEmpty();
            this.mAssistantRightRegion.setEmpty();
            if (i == 1) {
                orientationRectF.left = orientationRectF.right - getNavbarSize(ResourceUtils.NAVBAR_LANDSCAPE_LEFT_RIGHT_SIZE);
            } else if (i == 3) {
                orientationRectF.right = orientationRectF.left + getNavbarSize(ResourceUtils.NAVBAR_LANDSCAPE_LEFT_RIGHT_SIZE);
            } else {
                orientationRectF.top = orientationRectF.bottom - getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE);
            }
        }
        this.mOneHandedModeRegion.set(0.0f, orientationRectF.bottom - this.mNavBarLargerGesturalHeight, point.x, point.y);
        return orientationRectF;
    }

    private void updateAssistantRegions(OrientationRectF orientationRectF) {
        int navbarSize = getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE);
        int dimensionPixelSize = this.mResources.getDimensionPixelSize(R.dimen.gestures_assistant_width);
        float fMax = Math.max(navbarSize, this.mContractInfo.getWindowCornerRadius());
        RectF rectF = this.mAssistantLeftRegion;
        RectF rectF2 = this.mAssistantRightRegion;
        float f = orientationRectF.bottom;
        rectF2.bottom = f;
        rectF.bottom = f;
        RectF rectF3 = this.mAssistantLeftRegion;
        RectF rectF4 = this.mAssistantRightRegion;
        float f2 = orientationRectF.bottom - fMax;
        rectF4.top = f2;
        rectF3.top = f2;
        this.mAssistantLeftRegion.left = 0.0f;
        float f3 = dimensionPixelSize;
        this.mAssistantLeftRegion.right = f3;
        this.mAssistantRightRegion.right = orientationRectF.right;
        this.mAssistantRightRegion.left = orientationRectF.right - f3;
    }

    boolean touchInAssistantRegion(MotionEvent ev) {
        return this.mAssistantLeftRegion.contains(ev.getX(), ev.getY()) || this.mAssistantRightRegion.contains(ev.getX(), ev.getY());
    }

    boolean touchInOneHandedModeRegion(MotionEvent ev) {
        return this.mOneHandedModeRegion.contains(ev.getX(), ev.getY());
    }

    private int getNavbarSize(String resName) {
        return ResourceUtils.getNavbarSize(resName, this.mResources);
    }

    boolean touchInValidSwipeRegions(float x, float y) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_SWIPE_TO_HOME, "touchInValidSwipeRegions " + x + "," + y + " in " + this.mLastRectTouched);
        }
        OrientationRectF orientationRectF = this.mLastRectTouched;
        if (orientationRectF != null) {
            return orientationRectF.contains(x, y);
        }
        return false;
    }

    int getCurrentActiveRotation() {
        return this.mActiveTouchRotation;
    }

    int getQuickStepStartingRotation() {
        return this.mQuickStepStartingRotation;
    }

    public void transform(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    OrientationRectF orientationRectF = this.mLastRectTouched;
                    if (orientationRectF == null) {
                        return;
                    }
                    orientationRectF.applyTransform(event, true);
                    return;
                }
                if (actionMasked != 3) {
                    if (actionMasked != 5) {
                        return;
                    }
                }
            }
            OrientationRectF orientationRectF2 = this.mLastRectTouched;
            if (orientationRectF2 == null) {
                return;
            }
            orientationRectF2.applyTransform(event, true);
            this.mLastRectTouched = null;
            return;
        }
        if (this.mLastRectTouched != null) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            OrientationRectF orientationRectF3 = this.mSwipeTouchRegions.get(i);
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.NO_SWIPE_TO_HOME, "transform:DOWN, rect=" + orientationRectF3);
            }
            if (orientationRectF3 != null && orientationRectF3.applyTransform(event, false)) {
                if (TestProtocol.sDebugTracing) {
                    Log.d(TestProtocol.NO_SWIPE_TO_HOME, "setting mLastRectTouched");
                }
                this.mLastRectTouched = orientationRectF3;
                int i2 = orientationRectF3.mRotation;
                this.mActiveTouchRotation = i2;
                if (this.mEnableMultipleRegions && this.mCurrentDisplayRotation == i2) {
                    this.mQuickStepStartingRotation = this.mLastRectTouched.mRotation;
                    resetSwipeRegions();
                    return;
                }
                return;
            }
        }
    }

    public void dump(PrintWriter pw) {
        pw.println("OrientationTouchTransformerState: ");
        pw.println("  currentActiveRotation=" + getCurrentActiveRotation());
        pw.println("  lastTouchedRegion=" + this.mLastRectTouched);
        pw.println("  multipleRegionsEnabled=" + this.mEnableMultipleRegions);
        StringBuilder sb = new StringBuilder("  currentTouchableRotations=");
        for (int i = 0; i < this.mSwipeTouchRegions.size(); i++) {
            SparseArray<OrientationRectF> sparseArray = this.mSwipeTouchRegions;
            sb.append(sparseArray.get(sparseArray.keyAt(i)).toString()).append(" ");
        }
        pw.println(sb.toString());
        pw.println("  mNavBarGesturalHeight=" + this.mNavBarGesturalHeight);
        pw.println("  mNavBarLargerGesturalHeight=" + this.mNavBarLargerGesturalHeight);
        pw.println("  mOneHandedModeRegion=" + this.mOneHandedModeRegion);
    }

    boolean isSameRect(DisplayController.Info info, OrientationRectF region) {
        return region != null && new OrientationRectF(0.0f, 0.0f, (float) info.currentSize.x, (float) info.currentSize.y, info.rotation).equals(region) && info.rotation == region.mRotation;
    }

    private class OrientationRectF extends RectF {
        private float mHeight;
        private int mRotation;
        private float mWidth;

        OrientationRectF(float left, float top, float right, float bottom, int rotation) {
            super(left, top, right, bottom);
            this.mRotation = rotation;
            this.mHeight = bottom;
            this.mWidth = right;
        }

        @Override // android.graphics.RectF
        public String toString() {
            return super.toString() + " rotation: " + this.mRotation;
        }

        @Override // android.graphics.RectF
        public boolean contains(float x, float y) {
            return this.left < this.right && this.top < this.bottom && x >= this.left && x <= this.right && y >= this.top && y <= this.bottom;
        }

        boolean applyTransform(MotionEvent event, boolean forceTransform) {
            OrientationTouchTransformer.this.mTmpMatrix.reset();
            RecentsOrientedState.postDisplayRotation(RotationHelper.deltaRotation(OrientationTouchTransformer.this.mCurrentDisplayRotation, this.mRotation), this.mHeight, this.mWidth, OrientationTouchTransformer.this.mTmpMatrix);
            if (forceTransform) {
                event.transform(OrientationTouchTransformer.this.mTmpMatrix);
                return true;
            }
            OrientationTouchTransformer.this.mTmpPoint[0] = event.getX();
            OrientationTouchTransformer.this.mTmpPoint[1] = event.getY();
            OrientationTouchTransformer.this.mTmpMatrix.mapPoints(OrientationTouchTransformer.this.mTmpPoint);
            if (!contains(OrientationTouchTransformer.this.mTmpPoint[0], OrientationTouchTransformer.this.mTmpPoint[1])) {
                return false;
            }
            event.transform(OrientationTouchTransformer.this.mTmpMatrix);
            return true;
        }
    }
}
