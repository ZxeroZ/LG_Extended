package com.android.launcher3.dragndrop;

import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemProperties;
import android.util.Log;
import android.util.MathUtils;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DragScroller;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragDriver;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.BaseDragLayer;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.dragndrop.ConeShortcut;
import com.lge.launcher3.initialguide.InitialGuideManager;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.VibratorManager;
import com.lge.launcher3.util.WindowUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class DragController implements DragDriver.EventListener, TouchController {
    private static final String ACTION_SET_CURRENT_CONTENT = "com.lge.signboard.content.intent.action.SET_CURRENT_CONTENT";
    private static final String CONE_PACKAGE_NAME = "com.lge.coneshortcut";
    public static int DRAG_ACTION_COPY = 1;
    public static int DRAG_ACTION_MOVE = 0;
    private static final String EXTRA_CONE_COMPONENT_NAME = "com.lge.coneshortcut/.ConeShortcuts";
    private static final String EXTRA_FLATTENED_COMPONENT_NAME = "com.lge.signboard.content.intent.extra.FLATTENED_COMPONENT_NAME";
    private static final float MAX_FLING_DEGREES = 35.0f;
    private static final boolean PROFILE_DRAWING_DURING_DRAG = false;
    public static final String PROPERTY_RO_LGE_QUICK_TASK_TOOLS;
    public static final int RESCROLL_DELAY = 750;
    public static final int SCROLL_DELAY = 500;
    public static final int SCROLL_LEFT = 0;
    public static final int SCROLL_NONE = -1;
    private static final int SCROLL_OUTSIDE_ZONE = 0;
    public static final int SCROLL_RIGHT = 1;
    private static final int SCROLL_WAITING_IN_ZONE = 1;
    private static final String TAG = "Launcher.DragController";
    private DropTarget.DragObject mDragObject;
    DragScroller mDragScroller;
    private boolean mDragging;
    private DropTarget mFlingToDeleteDropTarget;
    protected int mFlingToDeleteThresholdVelocity;
    private Handler mHandler;
    private InputMethodManager mInputMethodManager;
    private boolean mIsAccessibleDrag;
    private boolean mIsInPreDrag;
    private final boolean mIsRtl;
    private DropTarget mLastDropTarget;
    Launcher mLauncher;
    private int mMotionDownX;
    private int mMotionDownY;
    private View mMoveTarget;
    private DragOptions mOptions;
    private View mScrollView;
    private int mScrollZone;
    private VelocityTracker mVelocityTracker;
    private IBinder mWindowToken;
    private DragDriver mDragDriver = null;
    private Rect mRectTemp = new Rect();
    private final int[] mCoordinatesTemp = new int[2];
    private ArrayList<DropTarget> mDropTargets = new ArrayList<>();
    private ArrayList<DragListener> mListeners = new ArrayList<>();
    int mScrollState = 0;
    private ScrollRunnable mScrollRunnable = new ScrollRunnable();
    int[] mLastTouch = new int[2];
    long mLastTouchUpTime = -1;
    int mDistanceSinceScroll = 0;
    private int[] mTmpPoint = new int[2];
    private Rect mDragLayerRect = new Rect();
    private boolean mIsFirstMove = true;
    private boolean mIsFirstMoveDone = false;
    private boolean mIsTouching = false;
    public Boolean mConeUIFlag = null;
    public int mDragingDiff = 0;
    public boolean mPreventBroadcastSetCurrentContent = false;
    public int mDragStatus = -1;
    public final String[] mHomeDragingSource = {"LGHome", "NODrag", "addItem"};
    public final String[] mExternalDragingSouce = {"coneshortcut"};

    public interface DragListener {
        void onDragEnd();

        void onDragStart(DragSource source, Object info, int dragAction);
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x00e7  */
    @Override // com.android.launcher3.util.TouchController
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onControllerTouchEvent(final android.view.MotionEvent r8) {
        /*
            r7 = this;
            int r0 = r8.getAction()
            r1 = 1
            if (r0 != 0) goto L10
            com.android.launcher3.Launcher r0 = r7.mLauncher
            boolean r0 = r7.blockTouchDown(r0)
            if (r0 == 0) goto L10
            return r1
        L10:
            com.android.launcher3.DropTarget$DragObject r0 = r7.mDragObject
            int r2 = r8.getAction()
            r3 = 3
            r4 = 0
            if (r2 != r3) goto L2d
            if (r0 == 0) goto L2d
            com.android.launcher3.dragndrop.DragView r2 = r0.dragView
            if (r2 == 0) goto L2d
            com.android.launcher3.dragndrop.DragView r2 = r0.dragView
            float r2 = r2.getAlpha()
            r5 = 1065353216(0x3f800000, float:1.0)
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 >= 0) goto L2d
            return r4
        L2d:
            java.lang.Boolean r2 = r7.mConeUIFlag
            boolean r2 = r2.booleanValue()
            if (r2 == 0) goto L3e
            if (r0 == 0) goto L3e
            com.android.launcher3.DragSource r0 = r0.dragSource
            boolean r0 = r0 instanceof com.lge.launcher3.dragndrop.ConeShortcut
            if (r0 == 0) goto L3e
            return r4
        L3e:
            com.android.launcher3.dragndrop.DragDriver r0 = r7.mDragDriver
            if (r0 == 0) goto L126
            com.android.launcher3.dragndrop.DragOptions r0 = r7.mOptions
            if (r0 == 0) goto L126
            boolean r0 = r0.isAccessibleDrag
            if (r0 == 0) goto L4c
            goto L126
        L4c:
            int r0 = r8.getAction()
            float r2 = r8.getX()
            float r5 = r8.getY()
            int[] r2 = r7.getClampedDragLayerPos(r2, r5)
            r5 = r2[r4]
            r2 = r2[r1]
            if (r0 == 0) goto L11b
            if (r0 == r1) goto Le7
            r6 = 2
            if (r0 == r6) goto L6b
            if (r0 == r3) goto Le7
            goto L11f
        L6b:
            com.lge.launcher3.util.LGHomeFeature$Config r0 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r0 = r0.getValue()
            if (r0 == 0) goto L11f
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.lge.launcher3.wing.CarouselLayout r0 = r0.getCarouselLayout()
            if (r0 == 0) goto L11f
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.lge.launcher3.wing.CarouselLayout r0 = r0.getCarouselLayout()
            boolean r0 = r0.getIsDragging()
            if (r0 == 0) goto L11f
            boolean r0 = r7.mIsFirstMove
            if (r0 == 0) goto Lb0
            r7.mIsTouching = r1
            r7.mMotionDownX = r5
            r7.mMotionDownY = r2
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.lge.launcher3.wing.CarouselLayout r0 = r0.getCarouselLayout()
            int r1 = r7.mMotionDownX
            int r2 = r7.mMotionDownY
            r0.setDragXY(r1, r2)
            android.os.Handler r0 = new android.os.Handler
            r0.<init>()
            com.android.launcher3.dragndrop.DragController$1 r1 = new com.android.launcher3.dragndrop.DragController$1
            r1.<init>()
            r2 = 700(0x2bc, double:3.46E-321)
            r0.postDelayed(r1, r2)
            r7.mIsFirstMove = r4
            goto L11f
        Lb0:
            boolean r0 = r7.mIsFirstMoveDone
            if (r0 == 0) goto L11f
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.lge.launcher3.wing.CarouselLayout r0 = r0.getCarouselLayout()
            if (r0 == 0) goto L11f
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.lge.launcher3.wing.CarouselLayout r0 = r0.getCarouselLayout()
            com.lge.launcher3.wing.SwivelItemTouchHelper r0 = r0.getHelper()
            androidx.recyclerview.widget.RecyclerView$OnItemTouchListener r0 = r0.mOnItemTouchListener
            com.android.launcher3.Launcher r1 = r7.mLauncher
            com.lge.launcher3.wing.CarouselLayout r1 = r1.getCarouselLayout()
            com.lge.launcher3.wing.carousel.widget.CarouselView r1 = r1.getCarouselView()
            android.view.MotionEvent r2 = android.view.MotionEvent.obtain(r8)
            r0.onTouchEvent(r1, r2)
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.lge.launcher3.wing.CarouselLayout r0 = r0.getCarouselLayout()
            android.view.MotionEvent r1 = android.view.MotionEvent.obtain(r8)
            r0.setFindViewAlpha(r1)
            goto L11f
        Le7:
            com.lge.launcher3.util.LGHomeFeature$Config r0 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r0 = r0.getValue()
            if (r0 == 0) goto L11f
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.lge.launcher3.wing.CarouselLayout r0 = r0.getCarouselLayout()
            if (r0 == 0) goto L11f
            r7.mIsFirstMove = r1
            r7.mIsFirstMoveDone = r4
            r7.mIsTouching = r4
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.lge.launcher3.wing.CarouselLayout r0 = r0.getCarouselLayout()
            com.lge.launcher3.wing.SwivelItemTouchHelper r0 = r0.getHelper()
            androidx.recyclerview.widget.RecyclerView$OnItemTouchListener r0 = r0.mOnItemTouchListener
            com.android.launcher3.Launcher r1 = r7.mLauncher
            com.lge.launcher3.wing.CarouselLayout r1 = r1.getCarouselLayout()
            com.lge.launcher3.wing.carousel.widget.CarouselView r1 = r1.getCarouselView()
            android.view.MotionEvent r2 = android.view.MotionEvent.obtain(r8)
            r0.onTouchEvent(r1, r2)
            goto L11f
        L11b:
            r7.mMotionDownX = r5
            r7.mMotionDownY = r2
        L11f:
            com.android.launcher3.dragndrop.DragDriver r0 = r7.mDragDriver
            boolean r8 = r0.onTouchEvent(r8)
            return r8
        L126:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.dragndrop.DragController.onControllerTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && blockTouchDown(this.mLauncher)) {
            return true;
        }
        DropTarget.DragObject dragObject = this.mDragObject;
        if (ev.getAction() == 3 && dragObject != null && dragObject.dragView != null && dragObject.dragView.getAlpha() < 1.0f) {
            return false;
        }
        DragOptions dragOptions = this.mOptions;
        if (dragOptions != null && dragOptions.isAccessibleDrag) {
            return false;
        }
        int action = ev.getAction();
        int[] clampedDragLayerPos = getClampedDragLayerPos(ev.getX(), ev.getY());
        int i = clampedDragLayerPos[0];
        int i2 = clampedDragLayerPos[1];
        if (action == 0) {
            this.mMotionDownX = i;
            this.mMotionDownY = i2;
        } else if (action == 1) {
            this.mLastTouchUpTime = System.currentTimeMillis();
        }
        DragDriver dragDriver = this.mDragDriver;
        return dragDriver != null && dragDriver.onInterceptTouchEvent(ev);
    }

    @Override // com.android.launcher3.dragndrop.DragDriver.EventListener
    public void onDriverDragMove(float x, float y) {
        int[] clampedDragLayerPos = getClampedDragLayerPos(x, y);
        handleMoveEvent(clampedDragLayerPos[0], clampedDragLayerPos[1]);
    }

    @Override // com.android.launcher3.dragndrop.DragDriver.EventListener
    public void onDriverDragExitWindow() {
        DropTarget dropTarget = this.mLastDropTarget;
        if (dropTarget != null) {
            dropTarget.onDragExit(this.mDragObject);
            this.mLastDropTarget = null;
        }
    }

    @Override // com.android.launcher3.dragndrop.DragDriver.EventListener
    public void onDriverDragEnd(float x, float y) {
        drop(x, y);
        endDrag();
    }

    @Override // com.android.launcher3.dragndrop.DragDriver.EventListener
    public void onDriverDragCancel() {
        cancelDrag();
    }

    public DragController(Launcher launcher) {
        Resources resources = launcher.getResources();
        this.mLauncher = launcher;
        this.mHandler = new Handler();
        this.mScrollZone = resources.getDimensionPixelSize(R.dimen.scroll_zone);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mFlingToDeleteThresholdVelocity = (int) (resources.getInteger(R.integer.config_flingToDeleteMinVelocity) * resources.getDisplayMetrics().density);
        this.mIsRtl = Utilities.isRtl(resources);
        checkConeShortcut();
    }

    public boolean dragging() {
        return this.mDragging;
    }

    public void startDrag(View v, Bitmap bmp, DragSource source, Object dragInfo, Rect viewImageBounds, int dragAction, float initialDragViewScale) {
        int[] iArr = this.mCoordinatesTemp;
        this.mLauncher.getDragLayer().getLocationInDragLayer(v, iArr);
        startDragForDeepShortcut(bmp, iArr[0] + viewImageBounds.left + ((int) (((bmp.getWidth() * initialDragViewScale) - bmp.getWidth()) / 2.0f)), iArr[1] + viewImageBounds.top + ((int) (((bmp.getHeight() * initialDragViewScale) - bmp.getHeight()) / 2.0f)), source, (ItemInfo) dragInfo, null, null, initialDragViewScale, new DragOptions());
        if (dragAction == DRAG_ACTION_MOVE) {
            v.setVisibility(8);
        }
    }

    public DragView startDrag(Bitmap b, int dragLayerX, int dragLayerY, DragSource source, Object dragInfo, int dragAction, Point dragOffset, Rect dragRegion, float initialDragViewScale, boolean accessible) {
        if (this.mInputMethodManager == null) {
            this.mInputMethodManager = (InputMethodManager) this.mLauncher.getApplicationContext().getSystemService("input_method");
        }
        boolean z = false;
        this.mInputMethodManager.hideSoftInputFromWindow(this.mWindowToken, 0);
        Iterator<DragListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onDragStart(source, dragInfo, dragAction);
        }
        int i = this.mMotionDownX - dragLayerX;
        int i2 = this.mMotionDownY - dragLayerY;
        int i3 = dragRegion == null ? 0 : dragRegion.left;
        int i4 = dragRegion == null ? 0 : dragRegion.top;
        this.mDragging = true;
        this.mIsAccessibleDrag = accessible;
        DropTarget.DragObject dragObject = new DropTarget.DragObject();
        this.mDragObject = dragObject;
        dragObject.dragComplete = false;
        if (this.mIsAccessibleDrag) {
            this.mDragObject.xOffset = b.getWidth() / 2;
            this.mDragObject.yOffset = b.getHeight() / 2;
            this.mDragObject.accessibleDrag = true;
        } else {
            this.mDragObject.xOffset = this.mMotionDownX - (dragLayerX + i3);
            this.mDragObject.yOffset = this.mMotionDownY - (dragLayerY + i4);
        }
        this.mDragObject.dragSource = source;
        this.mDragObject.dragInfo = dragInfo;
        DropTarget.DragObject dragObject2 = this.mDragObject;
        DragView dragView = new DragView(this.mLauncher, b, i, i2, 0, 0, b.getWidth(), b.getHeight(), initialDragViewScale);
        dragObject2.dragView = dragView;
        if (dragOffset != null) {
            dragView.setDragVisualizeOffset(new Point(dragOffset));
        }
        if (dragRegion != null) {
            dragView.setDragRegion(new Rect(dragRegion));
        }
        VibratorManager.performHapticFeedback(this.mLauncher, 0);
        dragView.show(this.mMotionDownX, this.mMotionDownY);
        handleMoveEvent(this.mMotionDownX, this.mMotionDownY);
        if (this.mConeUIFlag.booleanValue() && (dragInfo instanceof ItemInfo) && !checkQuickTaskTools()) {
            z = true;
        }
        if (!z) {
            return dragView;
        }
        startDragShadow(b, source, dragInfo, this.mMotionDownX - dragLayerX, this.mMotionDownY - dragLayerY, dragView);
        return dragView;
    }

    Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean zWillNotCacheDrawing = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int drawingCacheBackgroundColor = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        float alpha = v.getAlpha();
        v.setAlpha(1.0f);
        if (drawingCacheBackgroundColor != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap drawingCache = v.getDrawingCache();
        if (drawingCache == null) {
            Log.e(TAG, "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(drawingCache);
        v.destroyDrawingCache();
        v.setAlpha(alpha);
        v.setWillNotCacheDrawing(zWillNotCacheDrawing);
        v.setDrawingCacheBackgroundColor(drawingCacheBackgroundColor);
        return bitmapCreateBitmap;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.mDragging;
    }

    public boolean isDragging() {
        return this.mDragging;
    }

    public void cancelDrag() {
        onDragCancel();
        if (this.mDragging) {
            DropTarget dropTarget = this.mLastDropTarget;
            if (dropTarget != null) {
                dropTarget.onDragExit(this.mDragObject);
            }
            this.mDragObject.deferDragViewCleanupPostAnimation = false;
            this.mDragObject.cancelled = true;
            this.mDragObject.dragComplete = true;
            this.mDragObject.dragSource.onDropCompleted(null, this.mDragObject, false, false);
            AbstractFloatingView.closeAllOpenViews(this.mLauncher);
        }
        endDrag();
    }

    public void onAppsRemoved(ItemInfoMatcher matcher) {
        ShortcutInfo shortcutInfo;
        ComponentName targetComponent;
        DropTarget.DragObject dragObject = this.mDragObject;
        if (dragObject != null) {
            Object obj = dragObject.dragInfo;
            if ((obj instanceof ShortcutInfo) && (targetComponent = (shortcutInfo = (ShortcutInfo) obj).getTargetComponent()) != null && matcher.matches(shortcutInfo, targetComponent)) {
                cancelDrag();
            }
        }
    }

    public void onAppsRemoved(final HashSet<String> packageNames, HashSet<ComponentName> cns) {
        DropTarget.DragObject dragObject = this.mDragObject;
        if (dragObject != null) {
            Object obj = dragObject.dragInfo;
            if (obj instanceof ShortcutInfo) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) obj;
                for (ComponentName componentName : cns) {
                    if (shortcutInfo != null && shortcutInfo.intent != null) {
                        ComponentName component = shortcutInfo.intent.getComponent();
                        if (component != null && (component.equals(componentName) || packageNames.contains(component.getPackageName()))) {
                            cancelDrag();
                            return;
                        }
                    }
                }
            }
        }
    }

    private void endDrag() {
        if (this.mDragging) {
            this.mDragDriver = null;
            boolean z = false;
            this.mDragging = false;
            this.mIsAccessibleDrag = false;
            clearScrollRunnable();
            if (this.mDragObject.dragView != null) {
                z = this.mDragObject.deferDragViewCleanupPostAnimation;
                if (!z) {
                    this.mDragObject.dragView.remove();
                } else if (this.mIsInPreDrag) {
                    animateDragViewToOriginalPosition(null, null, -1);
                }
                this.mDragObject.dragView = null;
            }
            if (!z) {
                callOnDragEnd();
            }
        }
        if (this.mLauncher.isInState(LauncherState.ALL_APPS) && this.mLauncher.getAllAppsHost() != null && !this.mLauncher.getAllAppsHost().isInArrangeMode()) {
            this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().cancelLongPressHandler();
        }
        releaseVelocityTracker();
    }

    void onDeferredEndDrag(DragView dragView) {
        dragView.remove();
        if (this.mDragObject.deferDragViewCleanupPostAnimation) {
            callOnDragEnd();
        }
    }

    public void onDeferredEndFling(DropTarget.DragObject d) {
        d.dragSource.onFlingToDeleteCompleted();
    }

    private int[] getClampedDragLayerPos(float x, float y) {
        this.mLauncher.getDragLayer().getLocalVisibleRect(this.mDragLayerRect);
        this.mTmpPoint[0] = (int) Math.max(this.mDragLayerRect.left, Math.min(x, this.mDragLayerRect.right - 1));
        this.mTmpPoint[1] = (int) Math.max(this.mDragLayerRect.top, Math.min(y, this.mDragLayerRect.bottom - 1));
        return this.mTmpPoint;
    }

    public long getLastGestureUpTime() {
        if (this.mDragging) {
            return System.currentTimeMillis();
        }
        return this.mLastTouchUpTime;
    }

    public void resetLastGestureUpTime() {
        this.mLastTouchUpTime = -1L;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        DropTarget.DragObject dragObject = this.mDragObject;
        if ((ev.getAction() == 3 && dragObject != null && dragObject.dragView != null && dragObject.dragView.getAlpha() < 1.0f) || this.mIsAccessibleDrag) {
            return false;
        }
        acquireVelocityTrackerAndAddMovement(ev);
        int action = ev.getAction();
        int[] clampedDragLayerPos = getClampedDragLayerPos(ev.getX(), ev.getY());
        int i = clampedDragLayerPos[0];
        int i2 = clampedDragLayerPos[1];
        if (action == 0) {
            this.mMotionDownX = i;
            this.mMotionDownY = i2;
            this.mLastDropTarget = null;
            int[] iArr = this.mLastTouch;
            iArr[0] = i;
            iArr[1] = i2;
        } else if (action == 1) {
            this.mLastTouchUpTime = System.currentTimeMillis();
            if (this.mDragging) {
                PointF pointFIsFlingingToDelete = DeleteDropTarget.supportsDrop(this.mDragObject.dragInfo) ? isFlingingToDelete(this.mDragObject.dragSource) : null;
                if (pointFIsFlingingToDelete != null) {
                    dropOnFlingToDeleteTarget(i, i2, pointFIsFlingingToDelete);
                } else {
                    drop(i, i2);
                }
            }
            endDrag();
        } else if (action == 3) {
            cancelDrag();
        }
        return this.mDragging;
    }

    public void setMoveTarget(View view) {
        this.mMoveTarget = view;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        View view = this.mMoveTarget;
        return view != null && view.dispatchUnhandledMove(focused, direction);
    }

    private void clearScrollRunnable() {
        this.mHandler.removeCallbacks(this.mScrollRunnable);
        if (this.mScrollState == 1) {
            this.mScrollState = 0;
            this.mScrollRunnable.setDirection(1);
            this.mDragScroller.onExitScrollArea();
            this.mLauncher.getDragLayer().onExitScrollArea();
        }
    }

    private void handleMoveEvent(int x, int y) {
        DropTarget.DragObject dragObject = this.mDragObject;
        if (dragObject == null || dragObject.dragView == null) {
            Log.d(TAG, "handleMoveEvent() mDragObject:" + this.mDragObject + " return");
            return;
        }
        this.mDragObject.dragView.move(x, y);
        int[] iArr = this.mCoordinatesTemp;
        DropTarget dropTargetFindDropTarget = findDropTarget(x, y, iArr);
        Folder openFolder = this.mLauncher.mWorkspace != null ? this.mLauncher.mWorkspace.getOpenFolder() : null;
        if (openFolder != null && openFolder != dropTargetFindDropTarget) {
            dropTargetFindDropTarget = null;
        }
        this.mDragObject.x = iArr[0];
        this.mDragObject.y = iArr[1];
        checkTouchMove(dropTargetFindDropTarget);
        double d = this.mDistanceSinceScroll;
        int[] iArr2 = this.mLastTouch;
        this.mDistanceSinceScroll = (int) (d + Math.hypot(iArr2[0] - x, iArr2[1] - y));
        int[] iArr3 = this.mLastTouch;
        iArr3[0] = x;
        iArr3[1] = y;
        checkScrollState(x, y);
        if (this.mIsInPreDrag && this.mOptions.preDragCondition != null && this.mOptions.preDragCondition.shouldStartDrag(this.mDistanceSinceScroll)) {
            callOnDragStart();
        }
    }

    public void forceTouchMove() {
        int[] iArr = this.mCoordinatesTemp;
        int[] iArr2 = this.mLastTouch;
        DropTarget dropTargetFindDropTarget = findDropTarget(iArr2[0], iArr2[1], iArr);
        this.mDragObject.x = iArr[0];
        this.mDragObject.y = iArr[1];
        checkTouchMove(dropTargetFindDropTarget);
    }

    private void checkTouchMove(DropTarget dropTarget) {
        if (dropTarget != null) {
            DropTarget dropTarget2 = this.mLastDropTarget;
            if (dropTarget2 != dropTarget) {
                if (dropTarget2 != null) {
                    dropTarget2.onDragExit(this.mDragObject);
                }
                dropTarget.onDragEnter(this.mDragObject);
            }
            dropTarget.onDragOver(this.mDragObject);
        } else {
            DropTarget dropTarget3 = this.mLastDropTarget;
            if (dropTarget3 != null) {
                dropTarget3.onDragExit(this.mDragObject);
            }
        }
        this.mLastDropTarget = dropTarget;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    void checkScrollState(int i, int i2) {
        int i3 = this.mDistanceSinceScroll < ViewConfiguration.get(this.mLauncher).getScaledWindowTouchSlop() ? 750 : 500;
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        boolean z = this.mIsRtl;
        int i4 = !z ? 1 : 0;
        if (i < this.mScrollZone) {
            if (this.mScrollState == 0) {
                this.mScrollState = 1;
                if (this.mDragScroller.onEnterScrollArea(i, i2, z ? 1 : 0)) {
                    dragLayer.onEnterScrollArea(z ? 1 : 0);
                    this.mScrollRunnable.setDirection(z ? 1 : 0);
                    this.mHandler.postDelayed(this.mScrollRunnable, i3);
                    return;
                }
                return;
            }
            return;
        }
        if (i > this.mScrollView.getWidth() - this.mScrollZone) {
            if (this.mScrollState == 0) {
                this.mScrollState = 1;
                if (this.mDragScroller.onEnterScrollArea(i, i2, i4)) {
                    dragLayer.onEnterScrollArea(i4);
                    this.mScrollRunnable.setDirection(i4);
                    this.mHandler.postDelayed(this.mScrollRunnable, i3);
                    return;
                }
                return;
            }
            return;
        }
        clearScrollRunnable();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        DropTarget.DragObject dragObject = this.mDragObject;
        if (ev.getAction() == 3 && dragObject != null && dragObject.dragView != null && dragObject.dragView.getAlpha() < 1.0f) {
            return false;
        }
        if ((this.mConeUIFlag.booleanValue() && dragObject != null && (dragObject.dragSource instanceof ConeShortcut)) || !this.mDragging || this.mIsAccessibleDrag) {
            return false;
        }
        acquireVelocityTrackerAndAddMovement(ev);
        int action = ev.getAction();
        int[] clampedDragLayerPos = getClampedDragLayerPos(ev.getX(), ev.getY());
        int i = clampedDragLayerPos[0];
        int i2 = clampedDragLayerPos[1];
        if (action == 0) {
            this.mMotionDownX = i;
            this.mMotionDownY = i2;
            if (i < this.mScrollZone || i > this.mScrollView.getWidth() - this.mScrollZone) {
                this.mScrollState = 1;
                this.mHandler.postDelayed(this.mScrollRunnable, 500L);
            } else {
                this.mScrollState = 0;
            }
            handleMoveEvent(i, i2);
        } else if (action == 1) {
            handleMoveEvent(i, i2);
            this.mHandler.removeCallbacks(this.mScrollRunnable);
            if (this.mDragging) {
                PointF pointFIsFlingingToDelete = isFlingingToDelete(this.mDragObject.dragSource);
                if (!DeleteDropTarget.supportsDrop(this.mDragObject.dragInfo)) {
                    pointFIsFlingingToDelete = null;
                }
                if (pointFIsFlingingToDelete != null) {
                    dropOnFlingToDeleteTarget(i, i2, pointFIsFlingingToDelete);
                } else {
                    drop(i, i2);
                }
            }
            endDrag();
        } else if (action == 2) {
            handleMoveEvent(i, i2);
        } else if (action == 3) {
            this.mHandler.removeCallbacks(this.mScrollRunnable);
            cancelDrag();
        }
        return true;
    }

    public void prepareAccessibleDrag(int x, int y) {
        this.mMotionDownX = x;
        this.mMotionDownY = y;
        this.mLastDropTarget = null;
    }

    public void completeAccessibleDrag(int[] location) {
        int[] iArr = this.mCoordinatesTemp;
        DropTarget dropTargetFindDropTarget = findDropTarget(location[0], location[1], iArr);
        this.mDragObject.x = iArr[0];
        this.mDragObject.y = iArr[1];
        checkTouchMove(dropTargetFindDropTarget);
        dropTargetFindDropTarget.prepareAccessibilityDrop();
        drop(location[0], location[1]);
        endDrag();
    }

    private PointF isFlingingToDelete(DragSource source) {
        if (this.mFlingToDeleteDropTarget == null || !source.supportsFlingToDelete()) {
            return null;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, ViewConfiguration.get(this.mLauncher).getScaledMaximumFlingVelocity());
        if (this.mVelocityTracker.getYVelocity() < this.mFlingToDeleteThresholdVelocity) {
            PointF pointF = new PointF(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
            PointF pointF2 = new PointF(0.0f, -1.0f);
            if (((float) Math.acos(((pointF.x * pointF2.x) + (pointF.y * pointF2.y)) / (pointF.length() * pointF2.length()))) <= Math.toRadians(35.0d)) {
                return pointF;
            }
        }
        return null;
    }

    private void dropOnFlingToDeleteTarget(float x, float y, PointF vel) {
        int[] iArr = this.mCoordinatesTemp;
        boolean z = false;
        this.mDragObject.x = iArr[0];
        this.mDragObject.y = iArr[1];
        DropTarget dropTarget = this.mLastDropTarget;
        if (dropTarget != null && this.mFlingToDeleteDropTarget != dropTarget) {
            dropTarget.onDragExit(this.mDragObject);
        }
        this.mFlingToDeleteDropTarget.onDragEnter(this.mDragObject);
        this.mDragObject.dragComplete = true;
        this.mFlingToDeleteDropTarget.onDragExit(this.mDragObject);
        if (this.mFlingToDeleteDropTarget.acceptDrop(this.mDragObject)) {
            this.mFlingToDeleteDropTarget.onFlingToDelete(this.mDragObject, vel);
            z = true;
        }
        this.mDragObject.dragSource.onDropCompleted((View) this.mFlingToDeleteDropTarget, this.mDragObject, true, z);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v2, resolved type: com.android.launcher3.DropTarget */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:7:0x002f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void drop(float r6, float r7) {
        /*
            r5 = this;
            int[] r0 = r5.mCoordinatesTemp
            int r6 = (int) r6
            int r7 = (int) r7
            com.android.launcher3.DropTarget r6 = r5.findDropTarget(r6, r7, r0)
            com.android.launcher3.DropTarget$DragObject r7 = r5.mDragObject
            r1 = 0
            r2 = r0[r1]
            r7.x = r2
            com.android.launcher3.DropTarget$DragObject r7 = r5.mDragObject
            r2 = 1
            r0 = r0[r2]
            r7.y = r0
            if (r6 == 0) goto L2f
            com.android.launcher3.DropTarget$DragObject r7 = r5.mDragObject
            r7.dragComplete = r2
            com.android.launcher3.DropTarget$DragObject r7 = r5.mDragObject
            r6.onDragExit(r7)
            com.android.launcher3.DropTarget$DragObject r7 = r5.mDragObject
            boolean r7 = r6.acceptDrop(r7)
            if (r7 == 0) goto L2f
            com.android.launcher3.DropTarget$DragObject r7 = r5.mDragObject
            r6.onDrop(r7)
            goto L30
        L2f:
            r2 = r1
        L30:
            boolean r7 = r6 instanceof android.view.View
            if (r7 == 0) goto L38
            r7 = r6
            android.view.View r7 = (android.view.View) r7
            goto L39
        L38:
            r7 = 0
        L39:
            com.android.launcher3.DropTarget$DragObject r0 = r5.mDragObject
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "drop : accepted = "
            r3.append(r4)
            r3.append(r2)
            java.lang.String r4 = ", dropTarget = "
            r3.append(r4)
            r3.append(r6)
            java.lang.String r6 = ", mDragObject = "
            r3.append(r6)
            r3.append(r0)
            java.lang.String r6 = r3.toString()
            java.lang.String r0 = "Launcher.DragController"
            com.lge.launcher3.util.LGLog.d(r0, r6)
            if (r2 != 0) goto L67
            com.android.launcher3.DropTarget$DragObject r6 = r5.mDragObject
            r6.deferDragViewCleanupPostAnimation = r1
        L67:
            com.android.launcher3.DropTarget$DragObject r6 = r5.mDragObject
            com.android.launcher3.DragSource r6 = r6.dragSource
            com.android.launcher3.DropTarget$DragObject r0 = r5.mDragObject
            r6.onDropCompleted(r7, r0, r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.dragndrop.DragController.drop(float, float):void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: com.android.launcher3.DropTarget */
    /* JADX WARN: Multi-variable type inference failed */
    private DropTarget findDropTarget(int x, int y, int[] dropCoordinates) {
        Rect rect = this.mRectTemp;
        ArrayList<DropTarget> arrayList = this.mDropTargets;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            DropTarget dropTarget = arrayList.get(size);
            if (dropTarget.isDropEnabled()) {
                dropTarget.getHitRectRelativeToDragLayer(rect);
                this.mDragObject.x = x;
                this.mDragObject.y = y;
                if (rect.contains(x, y)) {
                    dropCoordinates[0] = x;
                    dropCoordinates[1] = y;
                    this.mLauncher.getDragLayer().mapCoordInSelfToDescendent((View) dropTarget, dropCoordinates);
                    return dropTarget;
                }
            }
        }
        return null;
    }

    public void setDragScoller(DragScroller scroller) {
        this.mDragScroller = scroller;
    }

    public void setWindowToken(IBinder token) {
        this.mWindowToken = token;
    }

    public void addDragListener(DragListener l) {
        this.mListeners.add(l);
    }

    public void removeDragListener(DragListener l) {
        this.mListeners.remove(l);
    }

    public ArrayList<DragListener> getDragListeners() {
        return this.mListeners;
    }

    public void addDropTarget(DropTarget target) {
        this.mDropTargets.add(target);
    }

    public void removeDropTarget(DropTarget target) {
        this.mDropTargets.remove(target);
    }

    public void setFlingToDeleteDropTarget(DropTarget target) {
        this.mFlingToDeleteDropTarget = target;
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
    }

    private void releaseVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public void setScrollView(View v) {
        this.mScrollView = v;
    }

    public DragView getDragView() {
        return this.mDragObject.dragView;
    }

    private class ScrollRunnable implements Runnable {
        private int mDirection;

        ScrollRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (DragController.this.mDragScroller != null) {
                if (this.mDirection == 0) {
                    DragController.this.mDragScroller.scrollLeft();
                } else {
                    DragController.this.mDragScroller.scrollRight();
                }
                DragController.this.mScrollState = 0;
                DragController.this.mDistanceSinceScroll = 0;
                DragController.this.mDragScroller.onExitScrollArea();
                DragController.this.mLauncher.getDragLayer().onExitScrollArea();
                if (DragController.this.isDragging()) {
                    DragController dragController = DragController.this;
                    dragController.checkScrollState(dragController.mLastTouch[0], DragController.this.mLastTouch[1]);
                }
            }
        }

        void setDirection(int direction) {
            this.mDirection = direction;
        }
    }

    public float getDragDistance() {
        float f = this.mMotionDownX;
        float f2 = this.mMotionDownY;
        int[] iArr = this.mLastTouch;
        return MathUtils.dist(f, f2, iArr[0], iArr[1]);
    }

    static {
        PROPERTY_RO_LGE_QUICK_TASK_TOOLS = Build.VERSION.SDK_INT < 28 ? "ro.lge.quick_task_tools" : "ro.product.lge.quick_task_tools";
    }

    private boolean checkQuickTaskTools() {
        return SystemProperties.getBoolean(PROPERTY_RO_LGE_QUICK_TASK_TOOLS, false);
    }

    private void checkConeShortcut() {
        try {
            if (this.mConeUIFlag == null) {
                this.mConeUIFlag = Boolean.valueOf(this.mLauncher.getPackageManager().getPackageInfo(CONE_PACKAGE_NAME, 0) != null);
            }
            this.mDragingDiff = 0;
        } catch (PackageManager.NameNotFoundException unused) {
            this.mConeUIFlag = false;
        }
    }

    public boolean onDragEvent(DragEvent ev) {
        int action = ev.getAction();
        int[] clampedDragLayerPos = getClampedDragLayerPos(ev.getX(), ev.getY());
        int i = clampedDragLayerPos[0];
        if (action == 6 && clampedDragLayerPos[1] < this.mDragingDiff) {
            clampedDragLayerPos[1] = 0;
        }
        int i2 = clampedDragLayerPos[1];
        if (action != 5 && action != 1 && i2 > 0) {
            i2 += this.mDragingDiff;
        }
        if (action == 1) {
            Workspace workspace = this.mLauncher.getWorkspace();
            this.mMotionDownX = i;
            this.mMotionDownY = i2;
            LGLog.i("DragNDrop", i + " " + i2);
            if (isDragingConeShortCut(ev) && workspace.getScreenIdForPageIndex(workspace.getCurrentPage()) == -301) {
                return false;
            }
            if (isDragingConeShortCut(ev)) {
                creatDummyShortView(ev);
                this.mLauncher.closeFolder(new boolean[0]);
                this.mLauncher.enterSpringLoadedDragMode();
            }
            hideFloatingApp();
        } else if (action == 2) {
            handleMoveEvent(i, i2);
        } else if (action == 3) {
            DropTarget.DragObject dragObject = this.mDragObject;
            if (dragObject == null || dragObject.dragView == null) {
                return false;
            }
            handleMoveEvent(i, i2);
            this.mHandler.removeCallbacks(this.mScrollRunnable);
            if (isDragingSource(ev)) {
                if (isDragingConeShortCut(ev) && !(findDropTarget(i, i2, this.mCoordinatesTemp) instanceof ButtonDropTarget)) {
                    cancelDrag();
                    return false;
                }
                if (this.mConeUIFlag.booleanValue() && !checkQuickTaskTools()) {
                    this.mDragObject.dragView.setVisibility(0);
                    this.mDragObject.dragView.setAlpha(1.0f);
                }
                drop(i, i2);
                endDrag();
                return true;
            }
        } else if (action == 4) {
            handledragEndEvent(ev);
            showFloatApp();
        } else if (action == 5) {
            this.mMotionDownX = i;
            this.mMotionDownY = i2;
        }
        this.mDragStatus = action;
        return true;
    }

    private void showFloatApp() {
        Intent intent = new Intent("com.lge.intent.action.FLOATING_WINDOW_EXIT_LOWPROFILE");
        intent.putExtra(AppNotifierManager.ExtraSpec.USAGE_PACKAGE, this.mLauncher.getPackageName());
        this.mLauncher.sendBroadcast(intent);
    }

    private void hideFloatingApp() {
        Intent intent = new Intent("com.lge.intent.action.FLOATING_WINDOW_ENTER_LOWPROFILE");
        intent.putExtra("hide", false);
        intent.putExtra(AppNotifierManager.ExtraSpec.USAGE_PACKAGE, this.mLauncher.getPackageName());
        this.mLauncher.sendBroadcast(intent);
    }

    private void handledragEndEvent(DragEvent ev) {
        int i = this.mDragStatus;
        if (i == 1 || i == 6) {
            this.mLauncher.exitSpringLoadedDragModeDelayed(true, 0, null);
            cancelDrag();
        }
    }

    private void replaceDragView(DragEvent ev) {
        if (ev.getClipData().getItemCount() > 0) {
            Bundle extras = ev.getClipData().getItemAt(0).getIntent().getExtras();
            ShortcutInfo shortcutInfo = new ShortcutInfo();
            if (extras != null) {
                shortcutInfo.onAddFromClipData(extras);
            }
            this.mDragObject.dragInfo = shortcutInfo;
        }
    }

    private void creatDummyShortView(DragEvent ev) {
        Workspace workspace = this.mLauncher.getWorkspace();
        BubbleTextView bubbleTextView = (BubbleTextView) LayoutInflater.from(this.mLauncher).inflate(R.layout.app_icon, (ViewGroup) workspace.getChildAt(workspace.getCurrentPage()), false);
        Drawable drawable = this.mLauncher.getResources().getDrawable(R.mipmap.lg_iconframe_home);
        bubbleTextView.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, drawable, (Drawable) null, (Drawable) null);
        BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(0, 0);
        layoutParams.width = drawable.getIntrinsicWidth();
        layoutParams.height = drawable.getIntrinsicHeight();
        bubbleTextView.setLayoutParams(layoutParams);
        bubbleTextView.setTranslationX((int) ev.getX());
        bubbleTextView.setTranslationY((int) ev.getY());
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.spanX = 10;
        shortcutInfo.spanY = 10;
        shortcutInfo.minSpanX = 10;
        shortcutInfo.minSpanY = 10;
        shortcutInfo.itemType = 1;
        shortcutInfo.user = Process.myUserHandle();
        shortcutInfo.title = "";
        shortcutInfo.intent = new Intent();
        bubbleTextView.setTag(shortcutInfo);
        workspace.beginDragShared(bubbleTextView, new ConeShortcut(this.mLauncher), false);
    }

    private boolean isDragingConeShortCut(DragEvent ev) {
        try {
            String str = (String) ev.getClipDescription().getLabel();
            for (String str2 : this.mExternalDragingSouce) {
                if (str2.equals(str)) {
                    return true;
                }
            }
        } catch (NullPointerException unused) {
        }
        return false;
    }

    private boolean isDragingSource(DragEvent ev) {
        try {
            String str = (String) ev.getClipDescription().getLabel();
            for (String str2 : this.mHomeDragingSource) {
                if (str2.equals(str)) {
                    return true;
                }
            }
            for (String str3 : this.mExternalDragingSouce) {
                if (str3.equals(str)) {
                    return true;
                }
            }
        } catch (NullPointerException unused) {
        }
        return false;
    }

    public void startDragShadow(Bitmap b, DragSource source, Object dragInfo, final int registrationX, final int registrationY, DragView dragView) {
        String str;
        final float f;
        final int width;
        ItemInfo itemInfo = (ItemInfo) dragInfo;
        if ((itemInfo.itemType == 0 || (itemInfo.itemType == 1 && itemInfo.id != -1)) && itemInfo.spanX == 1 && itemInfo.spanY == 1 && !this.mPreventBroadcastSetCurrentContent) {
            Intent intent = new Intent(ACTION_SET_CURRENT_CONTENT);
            intent.putExtra(EXTRA_FLATTENED_COMPONENT_NAME, EXTRA_CONE_COMPONENT_NAME);
            this.mLauncher.sendBroadcast(intent);
            str = "LGHome";
        } else {
            str = "NODrag";
        }
        this.mPreventBroadcastSetCurrentContent = false;
        Intent intent2 = new Intent();
        Bundle bundle = new Bundle();
        itemInfo.onAddToClipData(bundle);
        intent2.putExtras(bundle);
        ClipData clipDataNewIntent = ClipData.newIntent(str, intent2);
        if (itemInfo.spanX == 1 && itemInfo.spanY == 1) {
            f = 1.2f;
            width = (int) (b.getWidth() * 0.1f);
        } else {
            f = 1.0f;
            width = 0;
        }
        final Point point = new Point((int) (b.getWidth() * f), (int) (b.getHeight() * f));
        dragView.startDrag(clipDataNewIntent, new View.DragShadowBuilder(dragView) { // from class: com.android.launcher3.dragndrop.DragController.2
            @Override // android.view.View.DragShadowBuilder
            public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
                shadowSize.set(point.x, point.y);
                shadowTouchPoint.set(Math.max(0, registrationX) + width, Math.max(0, registrationY) + width);
            }

            @Override // android.view.View.DragShadowBuilder
            public void onDrawShadow(Canvas canvas) {
                float f2 = f;
                canvas.scale(f2, f2);
                getView().draw(canvas);
            }
        }, dragView, 768);
        dragView.setAlpha(0.0f);
        dragView.setVisibility(4);
    }

    public void onDragCancel() {
        if (this.mDragging) {
            DropTarget dropTarget = this.mLastDropTarget;
            if (dropTarget instanceof Workspace) {
                ((Workspace) dropTarget).onDragCancel();
            }
        }
    }

    public DragView startDragForDeepShortcut(Bitmap b, int dragLayerX, int dragLayerY, DragSource source, ItemInfo dragInfo, Point dragOffset, Rect dragRegion, float initialDragViewScale, DragOptions options) {
        ((InputMethodManager) this.mLauncher.getApplicationContext().getSystemService(InputMethodManager.class)).hideSoftInputFromWindow(this.mWindowToken, 0);
        this.mOptions = options;
        if (options.systemDndStartPoint != null) {
            this.mMotionDownX = this.mOptions.systemDndStartPoint.x;
            this.mMotionDownY = this.mOptions.systemDndStartPoint.y;
        }
        int i = this.mMotionDownX - dragLayerX;
        int i2 = this.mMotionDownY - dragLayerY;
        int i3 = dragRegion == null ? 0 : dragRegion.left;
        int i4 = dragRegion == null ? 0 : dragRegion.top;
        this.mLastDropTarget = null;
        this.mDragging = true;
        this.mDragObject = new DropTarget.DragObject();
        this.mIsInPreDrag = (this.mOptions.preDragCondition == null || this.mOptions.preDragCondition.shouldStartDrag(0.0d)) ? false : true;
        Resources resources = this.mLauncher.getResources();
        if (this.mIsInPreDrag) {
            resources.getDimensionPixelSize(R.dimen.pre_drag_view_scale);
        }
        DropTarget.DragObject dragObject = this.mDragObject;
        DragView dragView = new DragView(this.mLauncher, b, i, i2, 0, 0, b.getWidth(), b.getHeight(), initialDragViewScale);
        dragObject.dragView = dragView;
        dragView.setItemInfo(dragInfo);
        this.mDragObject.dragComplete = false;
        if (this.mOptions.isAccessibleDrag) {
            this.mDragObject.xOffset = b.getWidth() / 2;
            this.mDragObject.yOffset = b.getHeight() / 2;
            this.mDragObject.accessibleDrag = true;
        } else {
            this.mDragObject.xOffset = this.mMotionDownX - (dragLayerX + i3);
            this.mDragObject.yOffset = this.mMotionDownY - (dragLayerY + i4);
            this.mDragDriver = DragDriver.create(this.mLauncher, this, this.mDragObject, this.mOptions);
        }
        this.mDragObject.dragSource = source;
        this.mDragObject.dragInfo = dragInfo;
        if (dragOffset != null) {
            dragView.setDragVisualizeOffset(new Point(dragOffset));
        }
        if (dragRegion != null) {
            dragView.setDragRegion(new Rect(dragRegion));
        }
        VibratorManager.performHapticFeedback(this.mLauncher, 65579);
        dragView.show(this.mMotionDownX, this.mMotionDownY);
        this.mDistanceSinceScroll = 0;
        this.mLauncher.getWorkspace().onDragStartForHotseat(this.mDragObject.dragSource);
        if (!this.mIsInPreDrag) {
            callOnDragStart();
        } else if (this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragStart(this.mDragObject);
        }
        int[] iArr = this.mLastTouch;
        int i5 = this.mMotionDownX;
        iArr[0] = i5;
        int i6 = this.mMotionDownY;
        iArr[1] = i6;
        handleMoveEvent(i5, i6);
        if (!(this.mConeUIFlag.booleanValue() && (dragInfo instanceof ItemInfo) && !checkQuickTaskTools() && this.mOptions.systemDndStartPoint == null)) {
            return dragView;
        }
        startDragShadow(b, source, dragInfo, this.mMotionDownX - dragLayerX, this.mMotionDownY - dragLayerY, dragView);
        return dragView;
    }

    public void animateDragViewToOriginalPosition(final Runnable onComplete, final View originalIcon, int duration) {
        this.mDragObject.dragView.animateTo(this.mMotionDownX, this.mMotionDownY, new Runnable() { // from class: com.android.launcher3.dragndrop.DragController.3
            @Override // java.lang.Runnable
            public void run() {
                View view = originalIcon;
                if (view != null) {
                    view.setVisibility(0);
                }
                Runnable runnable = onComplete;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }, duration);
    }

    public void callOnDragStart() {
        int statusBarHeight;
        int dimensionPixelSize;
        if (this.mLauncher.isInState(LauncherState.ALL_APPS) && !this.mLauncher.getAllAppsHost().isInArrangeMode()) {
            this.mLauncher.getWorkspace().setCheckSwipeUpAppDrawer(false);
            this.mLauncher.getWorkspace().backToWorkspaceFromSwipeUpAppDrawer(false);
            this.mLauncher.getAllAppsHost().getLGAllAppsPagedView().cancelLongPressHandler();
        }
        DragOptions dragOptions = this.mOptions;
        if (dragOptions != null && dragOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragEnd(this.mDragObject, true);
        }
        this.mIsInPreDrag = false;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mLauncher.getSearchBar().getLayoutParams();
        if (this.mLauncher.isInMultiWindowMode()) {
            statusBarHeight = this.mLauncher.getDeviceProfile().getInsets().top;
            dimensionPixelSize = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.device_profile_droptarget_top_margin);
        } else {
            statusBarHeight = WindowUtils.getStatusBarHeight(this.mLauncher);
            dimensionPixelSize = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.device_profile_droptarget_top_margin);
        }
        layoutParams.topMargin = statusBarHeight + dimensionPixelSize;
        LGLog.d(TAG, "callOnDragStart LGSearchDropTargetBar topMargin : " + layoutParams.topMargin);
        this.mLauncher.getSearchBar().setLayoutParams(layoutParams);
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((DragListener) it.next()).onDragStart(this.mDragObject.dragSource, this.mDragObject.dragInfo, DRAG_ACTION_MOVE);
        }
    }

    private void callOnDragEnd() {
        DragOptions dragOptions = this.mOptions;
        if (dragOptions != null && ((this.mIsInPreDrag || dragOptions.isDragFromAllAps) && this.mOptions.preDragCondition != null)) {
            this.mOptions.preDragCondition.onPreDragEnd(this.mDragObject, false);
        }
        this.mIsInPreDrag = false;
        this.mOptions = null;
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((DragListener) it.next()).onDragEnd();
        }
    }

    private boolean blockTouchDown(Launcher launcher) {
        InitialGuideManager initialGuideManager = InitialGuideManager.getInstance(launcher);
        if (initialGuideManager.isAlreadyShown()) {
            return false;
        }
        if (initialGuideManager.isReadyToShow()) {
            if (!initialGuideManager.showInitialGuide(launcher)) {
                return false;
            }
            LGLog.i("DragLayer", "blockTouchDown() : Skip touch event until InitialGuide will be shown.");
            return true;
        }
        LGLog.i("DragLayer", "blockTouchDown() : Skip touch event until time to be ready to show InitialGuide.");
        return true;
    }
}
