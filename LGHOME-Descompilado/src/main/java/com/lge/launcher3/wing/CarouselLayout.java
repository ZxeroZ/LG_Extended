package com.lge.launcher3.wing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherState;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.lge.launcher3.LauncherExtension;
import com.lge.launcher3.R;
import com.lge.launcher3.knockoff.LGHomeGestureDetector;
import com.lge.launcher3.knockoff.LGKnockOnListener;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.CPUBoostService;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.VibratorManager;
import com.lge.launcher3.wing.SwivelItemTouchHelper;
import com.lge.launcher3.wing.carousel.transformer.CoverFlowViewTransformer;
import com.lge.launcher3.wing.carousel.widget.CarouselView;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class CarouselLayout extends FrameLayout implements DragController.DragListener, DropTarget, StateManager.StateHandler<LauncherState>, Insettable {
    public static final String TAG = "CarouselLayout";
    private static boolean isFirst = true;
    private int carouselStartHeight;
    private int fadeTextDuration;
    private float invisibleIconAlpha;
    private float invisibleTextAlpha;
    private boolean isDragging;
    private boolean isShowingAppShortcutPopupInAllapps;
    private boolean keepCancelDrag;
    private AppListAdapter mAdapter;
    private PopupContainerWithArrow mAppShortcutPopup;
    private AppShortcutPopupListener mAppShortcutPopupListener;
    private Canvas mCanvas;
    private CarouselView mCarouselView;
    private View mCarouselWorkspace;
    int mDistanceSinceScroll;
    private DragView mDragAllAppView;
    private DragController mDragController;
    private Rect mDragLayerRect;
    private ShortcutInfo mDragShortcutInfo;
    private int[] mDragXY;
    private View mFindView;
    private int mFindViewLocationX;
    int mFirstDistanceSinceScroll;
    private LGHomeGestureDetector mGestures;
    private SwivelItemTouchHelper mHelper;
    private TextView mIconTextView;
    private int mInsertItemByNull;
    private boolean mIsBlockToAdd;
    private boolean mIsEntryScroll;
    private int mItemSelectedPosition;
    int[] mLastTouch;
    Launcher mLauncher;
    public OnScrollCallback mOnScrollCallback;
    private float mStartDragThreshold;
    private SwivelWeatherView mSwivelWeatherView;
    private int[] mTempXY;
    private int[] mTmpPoint;
    private int[] mWeatherIconList;
    private SwivelWeatherInformation mWeatherInfo;
    private float textScaleDelta;
    private float visibleIconAlpha;
    private float visibleTextAlpha;

    public interface OnScrollCallback {
        void onScrollEnd();

        void onScrollStart();

        void setSelectedIconText(CharSequence charSequence);
    }

    @Override // com.android.launcher3.DropTarget
    public boolean acceptDrop(DropTarget.DragObject dragObject) {
        return true;
    }

    @Override // com.android.launcher3.DropTarget
    public boolean isDropEnabled() {
        return true;
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragEnter(DropTarget.DragObject dragObject) {
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragExit(DropTarget.DragObject dragObject) {
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragOver(DropTarget.DragObject dragObject) {
    }

    @Override // com.android.launcher3.DropTarget
    public void onFlingToDelete(DropTarget.DragObject dragObject, PointF vec) {
    }

    @Override // com.android.launcher3.DropTarget
    public void prepareAccessibilityDrop() {
    }

    public CarouselLayout(Context context) {
        super(context);
        this.mCanvas = new Canvas();
        this.mTempXY = new int[2];
        this.mDragXY = new int[2];
        this.mDragAllAppView = null;
        this.mFindView = null;
        this.mFindViewLocationX = -1;
        this.mDragShortcutInfo = null;
        this.visibleIconAlpha = 1.0f;
        this.invisibleIconAlpha = 0.3f;
        this.carouselStartHeight = -1;
        this.fadeTextDuration = 150;
        this.visibleTextAlpha = 1.0f;
        this.invisibleTextAlpha = 0.5f;
        this.textScaleDelta = 0.0f;
        this.isDragging = false;
        this.mAppShortcutPopup = null;
        this.mAppShortcutPopupListener = null;
        this.mLastTouch = new int[2];
        this.mDistanceSinceScroll = 0;
        this.mFirstDistanceSinceScroll = 0;
        this.mTmpPoint = new int[2];
        this.mDragLayerRect = new Rect();
        this.isShowingAppShortcutPopupInAllapps = false;
        this.keepCancelDrag = false;
        this.mInsertItemByNull = 0;
        this.mWeatherIconList = null;
        this.mGestures = null;
        this.mLauncher = (Launcher) context;
        initView();
    }

    public CarouselLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCanvas = new Canvas();
        this.mTempXY = new int[2];
        this.mDragXY = new int[2];
        this.mDragAllAppView = null;
        this.mFindView = null;
        this.mFindViewLocationX = -1;
        this.mDragShortcutInfo = null;
        this.visibleIconAlpha = 1.0f;
        this.invisibleIconAlpha = 0.3f;
        this.carouselStartHeight = -1;
        this.fadeTextDuration = 150;
        this.visibleTextAlpha = 1.0f;
        this.invisibleTextAlpha = 0.5f;
        this.textScaleDelta = 0.0f;
        this.isDragging = false;
        this.mAppShortcutPopup = null;
        this.mAppShortcutPopupListener = null;
        this.mLastTouch = new int[2];
        this.mDistanceSinceScroll = 0;
        this.mFirstDistanceSinceScroll = 0;
        this.mTmpPoint = new int[2];
        this.mDragLayerRect = new Rect();
        this.isShowingAppShortcutPopupInAllapps = false;
        this.keepCancelDrag = false;
        this.mInsertItemByNull = 0;
        this.mWeatherIconList = null;
        this.mGestures = null;
        this.mLauncher = (Launcher) context;
        initView();
    }

    public CarouselLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCanvas = new Canvas();
        this.mTempXY = new int[2];
        this.mDragXY = new int[2];
        this.mDragAllAppView = null;
        this.mFindView = null;
        this.mFindViewLocationX = -1;
        this.mDragShortcutInfo = null;
        this.visibleIconAlpha = 1.0f;
        this.invisibleIconAlpha = 0.3f;
        this.carouselStartHeight = -1;
        this.fadeTextDuration = 150;
        this.visibleTextAlpha = 1.0f;
        this.invisibleTextAlpha = 0.5f;
        this.textScaleDelta = 0.0f;
        this.isDragging = false;
        this.mAppShortcutPopup = null;
        this.mAppShortcutPopupListener = null;
        this.mLastTouch = new int[2];
        this.mDistanceSinceScroll = 0;
        this.mFirstDistanceSinceScroll = 0;
        this.mTmpPoint = new int[2];
        this.mDragLayerRect = new Rect();
        this.isShowingAppShortcutPopupInAllapps = false;
        this.keepCancelDrag = false;
        this.mInsertItemByNull = 0;
        this.mWeatherIconList = null;
        this.mGestures = null;
        this.mLauncher = (Launcher) context;
        initView();
    }

    private void initView() {
        addView(((LayoutInflater) this.mLauncher.getSystemService("layout_inflater")).inflate(R.layout.carousel, (ViewGroup) this, false));
        if (this.mGestures == null && LGHomeFeature.Config.FEATURE_USE_KNOCK_OFF.getValue()) {
            this.mGestures = new LGHomeGestureDetector(getContext(), new LGKnockOnListener(getContext()));
        }
        View viewFindViewById = findViewById(R.id.carousel_workspace);
        this.mCarouselWorkspace = viewFindViewById;
        viewFindViewById.setSoundEffectsEnabled(false);
        this.mCarouselWorkspace.setOnTouchListener(new View.OnTouchListener() { // from class: com.lge.launcher3.wing.CarouselLayout.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                return CarouselLayout.this.mGestures != null && CarouselLayout.this.mGestures.onTouchEvent(event);
            }
        });
        this.mCarouselWorkspace.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.lge.launcher3.wing.CarouselLayout.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                DeviceProfile deviceProfile;
                if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(CarouselLayout.this.getContext())) {
                    Toast.makeText(CarouselLayout.this.getContext(), HomeSettingsSharedPreferences.getHomeLockDisableGuideText(CarouselLayout.this.getContext()), 0).show();
                    return true;
                }
                if (CarouselLayout.this.mLauncher != null && (deviceProfile = CarouselLayout.this.mLauncher.getDeviceProfile()) != null && deviceProfile.isMultiWindowMode) {
                    Toast.makeText(CarouselLayout.this.getContext(), CarouselLayout.this.getResources().getString(R.string.home_screen_lock_in_multiwindow), 0).show();
                    return true;
                }
                if (CarouselLayout.this.mLauncher != null && !CarouselLayout.this.mLauncher.isCleanViewState() && !UninstallModeManager.getInstance(CarouselLayout.this.mLauncher).isInUninstallMode()) {
                    VibratorManager.performHapticFeedback(CarouselLayout.this.mLauncher, 0);
                    CarouselLayout.this.mSwivelWeatherView.setVisibility(4);
                    ((LauncherExtension) CarouselLayout.this.mLauncher).showOverviewMode();
                }
                return true;
            }
        });
        this.mCarouselWorkspace.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.wing.CarouselLayout.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (UninstallModeManager.getInstance(CarouselLayout.this.mLauncher).isInUninstallMode()) {
                    CarouselLayout.this.mLauncher.showWorkspace(true);
                    CarouselLayout.this.mLauncher.getWorkspace().setVisibility(8);
                }
            }
        });
        this.mCarouselView = (CarouselView) findViewById(R.id.list_horizontal);
        if (Build.VERSION.SDK_INT >= 31) {
            this.mCarouselView.setOverScrollMode(2);
        }
        this.mStartDragThreshold = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.deep_shortcuts_start_drag_threshold);
        SwivelWeatherView swivelWeatherView = (SwivelWeatherView) findViewById(R.id.swivel_weather_view);
        this.mSwivelWeatherView = swivelWeatherView;
        swivelWeatherView.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.wing.CarouselLayout.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (CarouselLayout.this.mLauncher.getWorkspace().isSwitchingState()) {
                    return;
                }
                SwivelWeatherUtils.startWeatherApp(CarouselLayout.this.getContext(), CarouselLayout.this.mWeatherInfo != null ? CarouselLayout.this.mWeatherInfo.getCityIndex() : 0);
            }
        });
        TextView textView = (TextView) findViewById(R.id.icon_text);
        this.mIconTextView = textView;
        textView.setAlpha(this.visibleTextAlpha);
        if (this.mOnScrollCallback == null) {
            OnScrollCallback onScrollCallback = new OnScrollCallback() { // from class: com.lge.launcher3.wing.CarouselLayout.5
                @Override // com.lge.launcher3.wing.CarouselLayout.OnScrollCallback
                public void onScrollStart() {
                    CarouselLayout carouselLayout = CarouselLayout.this;
                    carouselLayout.fadeOutAnimation(carouselLayout.mIconTextView, CarouselLayout.this.fadeTextDuration);
                }

                @Override // com.lge.launcher3.wing.CarouselLayout.OnScrollCallback
                public void onScrollEnd() {
                    CarouselLayout carouselLayout = CarouselLayout.this;
                    carouselLayout.fadeInAnimation(carouselLayout.mIconTextView, CarouselLayout.this.fadeTextDuration);
                }

                @Override // com.lge.launcher3.wing.CarouselLayout.OnScrollCallback
                public void setSelectedIconText(CharSequence text) {
                    if (CarouselLayout.this.mIconTextView != null) {
                        if (text != null) {
                            CarouselLayout.this.mIconTextView.setText(text);
                        } else {
                            CarouselLayout.this.mIconTextView.setAlpha(0.0f);
                        }
                    }
                }
            };
            this.mOnScrollCallback = onScrollCallback;
            this.mCarouselView.setOnScrollCallback(onScrollCallback);
        }
        this.mWeatherIconList = SwivelWeatherUtils.createIconArrayList();
        if (this.mLauncher.getOrientation() == 2) {
            this.carouselStartHeight = (int) this.mLauncher.getResources().getDimension(R.dimen.swivel_carousel_view_start_height_land);
        } else {
            this.carouselStartHeight = (int) this.mLauncher.getResources().getDimension(R.dimen.swivel_carousel_view_start_height_port);
        }
    }

    public void updateWeatheInformation() {
        String str = TAG;
        LGLog.i(str, "Update weather information");
        this.mSwivelWeatherView.setDate();
        SwivelWeatherInformation weatherInfo = SwivelWeatherUtils.getWeatherInfo(getContext());
        this.mWeatherInfo = weatherInfo;
        if (weatherInfo != null) {
            this.mSwivelWeatherView.setTemperatureText(weatherInfo.getTemperature());
            this.mSwivelWeatherView.setWeatherImageView(this.mWeatherIconList[SwivelWeatherUtils.getIconImage(this.mWeatherInfo)]);
        } else {
            LGLog.i(str, "Weather information is not existing yet");
        }
        if (SwivelWeatherUtils.isWeatherInstalled(getContext())) {
            this.mSwivelWeatherView.setSoundEffectsEnabled(true);
        } else {
            this.mSwivelWeatherView.setSoundEffectsEnabled(false);
        }
    }

    public void registerDragController(DragController dragController) {
        if (this.mDragController == null) {
            this.mDragController = dragController;
            dragController.addDragListener(this);
            this.mDragController.addDropTarget(this);
        }
    }

    public void unregisterDragController() {
        DragController dragController = this.mDragController;
        if (dragController != null) {
            dragController.removeDragListener(this);
            this.mDragController.removeDropTarget(this);
            this.mDragController = null;
        }
    }

    private void initCarousel() {
        AppListAdapter appListAdapter = new AppListAdapter(this.mLauncher);
        this.mAdapter = appListAdapter;
        this.mCarouselView.setAdapter(appListAdapter);
        this.mCarouselView.setClipChildren(false);
        ((ViewGroup) this.mCarouselView.getParent()).setClipChildren(false);
        ((ViewGroup) this.mCarouselView.getParent()).setClipToPadding(false);
        ((SimpleItemAnimator) this.mCarouselView.getItemAnimator()).setSupportsChangeAnimations(false);
        this.mCarouselView.setInfinite(false);
        this.mCarouselView.setExtraVisibleChilds(1);
        this.mCarouselView.getLayoutManager().setDrawOrder(CarouselView.DrawOrder.CenterFront);
        this.mCarouselView.setScrollingAlignToViews(true);
        this.mCarouselView.setOnItemSelectedListener(new CarouselView.OnItemSelectedListener() { // from class: com.lge.launcher3.wing.CarouselLayout.6
            @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.OnItemSelectedListener
            public void onItemDeselected(CarouselView carouselView, int position, int adapterPosition, RecyclerView.Adapter adapter) {
            }

            @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.OnItemSelectedListener
            public void onItemSelected(CarouselView carouselView, int position, int adapterPosition, RecyclerView.Adapter adapter) {
                if (!CarouselLayout.isFirst) {
                    CarouselLayout.this.mItemSelectedPosition = position;
                } else {
                    CarouselLayout.isFirst = false;
                }
            }
        });
        SwivelItemTouchHelper swivelItemTouchHelper = new SwivelItemTouchHelper(new SwivelItemTouchHelper.Callback() { // from class: com.lge.launcher3.wing.CarouselLayout.7
            @Override // com.lge.launcher3.wing.SwivelItemTouchHelper.Callback
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override // com.lge.launcher3.wing.SwivelItemTouchHelper.Callback
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(48, 0);
            }

            @Override // com.lge.launcher3.wing.SwivelItemTouchHelper.Callback
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                CarouselLayout.this.mAdapter.onItemMove(dragged.getAdapterPosition(), target.getAdapterPosition());
                return false;
            }

            /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: androidx.recyclerview.widget.RecyclerView$ViewHolder */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.lge.launcher3.wing.SwivelItemTouchHelper.Callback
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != 0) {
                    if (viewHolder instanceof IItemTouchHelperViewHolder) {
                        ((IItemTouchHelperViewHolder) viewHolder).onItemSelected();
                    }
                } else {
                    CarouselLayout.this.mCarouselView.adjustPosition();
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: androidx.recyclerview.widget.RecyclerView$ViewHolder */
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.lge.launcher3.wing.SwivelItemTouchHelper.Callback
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (viewHolder instanceof IItemTouchHelperViewHolder) {
                    ((IItemTouchHelperViewHolder) viewHolder).onItemClear();
                }
            }

            @Override // com.lge.launcher3.wing.SwivelItemTouchHelper.Callback
            public void onLongPress(MotionEvent e, View v) {
                DeviceProfile deviceProfile;
                LGLog.d(CarouselLayout.TAG, "onLongPress()");
                if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(CarouselLayout.this.mLauncher)) {
                    Toast.makeText(CarouselLayout.this.mLauncher, HomeSettingsSharedPreferences.getHomeLockDisableGuideText(CarouselLayout.this.mLauncher), 0).show();
                    return;
                }
                if (CarouselLayout.this.mLauncher != null && (deviceProfile = CarouselLayout.this.mLauncher.getDeviceProfile()) != null && deviceProfile.isMultiWindowMode) {
                    Toast.makeText(CarouselLayout.this.getContext(), CarouselLayout.this.getResources().getString(R.string.home_screen_lock_in_multiwindow), 0).show();
                    return;
                }
                if (CarouselLayout.this.mIsBlockToAdd) {
                    CarouselLayout.this.mIsBlockToAdd = false;
                    return;
                }
                if (CarouselLayout.this.isShowingAppShortcutPopupInAllapps || CarouselLayout.this.mLauncher.getAllAppsHost().isInArrangeMode() || UninstallModeManager.getInstance(CarouselLayout.this.mLauncher).isInUninstallMode()) {
                    LGLog.i(CarouselLayout.TAG, "onLongPress() Don't show deep shortcut. isShowingAppShortcutPopupInAllapps = " + CarouselLayout.this.isShowingAppShortcutPopupInAllapps + ", mLauncher.getAllAppsHost().isInArrangeMode() = " + CarouselLayout.this.mLauncher.getAllAppsHost().isInArrangeMode());
                    return;
                }
                CarouselLayout.this.showDeepShortcut(v, e.getX(), e.getY());
                CarouselLayout.this.mCarouselView.setLongTouchEvent(true);
            }

            @Override // com.lge.launcher3.wing.SwivelItemTouchHelper.Callback
            public void onTouchMove(MotionEvent event) {
                int actionMasked = event.getActionMasked();
                if (actionMasked == 1) {
                    CarouselLayout.this.mLauncher.unblockAndFlushInstallQueueSwivel();
                    return;
                }
                if (actionMasked != 2) {
                    return;
                }
                float x = event.getX();
                float y = event.getY();
                if (CarouselLayout.this.isShowingAppShortcutPopupInAllapps || CarouselLayout.this.mAppShortcutPopup == null) {
                    return;
                }
                CarouselLayout.this.calculateTouchDistance(x, y);
                if (CarouselLayout.this.shouldStartDrag(r3.mDistanceSinceScroll)) {
                    CarouselLayout.this.mLauncher.blockInstallQueueSwivel();
                    CarouselLayout.this.removeDeepShortcut();
                }
            }
        });
        this.mHelper = swivelItemTouchHelper;
        swivelItemTouchHelper.attachToRecyclerView(this.mCarouselView);
        this.mCarouselView.setTransformer(new CoverFlowViewTransformer());
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        initCarousel();
    }

    public void initIndex() {
        CPUBoostService.boostUp(this.mLauncher);
        this.mCarouselView.getLayoutManager().setScrollInterpolator(Interpolators.ACCEL_DEACCEL);
        this.mCarouselView.setOnScrollListener(new CarouselView.OnScrollListener() { // from class: com.lge.launcher3.wing.CarouselLayout.8
            @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.OnScrollListener
            public void onScrollEnd(CarouselView carouselView) {
                super.onScrollEnd(carouselView);
                if (CarouselLayout.this.mIsEntryScroll) {
                    CarouselLayout.this.mIsEntryScroll = false;
                    CarouselLayout.this.updateLiveIcons();
                }
            }
        });
        new Handler().postDelayed(new Runnable() { // from class: com.lge.launcher3.wing.CarouselLayout.9
            @Override // java.lang.Runnable
            public void run() {
                int itemCount = CarouselLayout.this.mAdapter.getItemCount() / 2;
                if (CarouselLayout.this.mAdapter.getItemCount() % 2 == 0 && itemCount > 0) {
                    itemCount--;
                }
                CarouselLayout.this.mCarouselView.smoothScrollToPosition(itemCount);
            }
        }, 850L);
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x00ce  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void beginDragSharedDeepShortcut(android.view.View r17, com.android.launcher3.DragSource r18, com.android.launcher3.dragndrop.DragOptions r19) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            java.lang.Object r2 = r17.getTag()
            boolean r3 = r2 instanceof com.android.launcher3.model.data.ItemInfo
            if (r3 != 0) goto Ld
            return
        Ld:
            java.lang.Object r3 = r17.getTag()
            r9 = r3
            com.android.launcher3.model.data.ItemInfo r9 = (com.android.launcher3.model.data.ItemInfo) r9
            boolean r3 = r2 instanceof com.android.launcher3.ShortcutInfo
            r4 = 0
            if (r3 == 0) goto L24
            com.android.launcher3.ShortcutInfo r3 = new com.android.launcher3.ShortcutInfo
            r3.<init>()
            com.android.launcher3.ShortcutInfo r2 = (com.android.launcher3.ShortcutInfo) r2
            r3.copyFrom(r2)
            goto L30
        L24:
            boolean r3 = r2 instanceof com.android.launcher3.model.data.AppInfo
            if (r3 == 0) goto L2f
            com.android.launcher3.model.data.AppInfo r2 = (com.android.launcher3.model.data.AppInfo) r2
            com.android.launcher3.ShortcutInfo r3 = r2.makeShortcut()
            goto L30
        L2f:
            r3 = r4
        L30:
            com.android.launcher3.graphics.DragPreviewProvider r2 = new com.android.launcher3.graphics.DragPreviewProvider
            r2.<init>(r1)
            android.graphics.Canvas r5 = r0.mCanvas
            android.graphics.Bitmap r5 = r2.createDragBitmap(r5)
            int r6 = r2.previewPadding
            int r6 = r6 / 2
            int[] r7 = r0.mTempXY
            float r12 = r2.getScaleAndPosition(r5, r7)
            int[] r2 = r0.mTempXY
            r7 = 0
            r8 = r2[r7]
            r10 = 1
            r2 = r2[r10]
            com.android.launcher3.Launcher r11 = r0.mLauncher
            com.android.launcher3.DeviceProfile r11 = r11.getDeviceProfile()
            boolean r13 = r1 instanceof com.android.launcher3.BubbleTextView
            if (r13 == 0) goto L84
            int r4 = r11.iconSizePx
            int r7 = r17.getPaddingTop()
            boolean r14 = r11.isLandscape
            if (r14 == 0) goto L6a
            boolean r11 = r11.allowRotation
            if (r11 == 0) goto L6a
            int r11 = r17.getPaddingStart()
            goto L71
        L6a:
            int r11 = r5.getWidth()
            int r11 = r11 - r4
            int r11 = r11 / 2
        L71:
            int r14 = r11 + r4
            int r4 = r4 + r7
            int r2 = r2 + r7
            android.graphics.Point r15 = new android.graphics.Point
            int r10 = -r6
            r15.<init>(r10, r6)
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>(r11, r7, r14, r4)
            r7 = r2
            r11 = r6
            r10 = r15
            goto L9f
        L84:
            boolean r10 = r1 instanceof com.android.launcher3.folder.FolderIcon
            if (r10 == 0) goto L9c
            int r4 = r11.folderIconSizePx
            android.graphics.Point r10 = new android.graphics.Point
            int r11 = -r6
            r10.<init>(r11, r6)
            android.graphics.Rect r6 = new android.graphics.Rect
            int r11 = r17.getPaddingTop()
            r6.<init>(r7, r11, r4, r4)
            r7 = r2
            r11 = r6
            goto L9f
        L9c:
            r7 = r2
            r10 = r4
            r11 = r10
        L9f:
            if (r13 == 0) goto Lce
            com.android.launcher3.Launcher r2 = r0.mLauncher
            android.view.View r2 = r2.getAllAppsButton()
            if (r1 == r2) goto Lce
            com.android.launcher3.Launcher r2 = r0.mLauncher
            com.lge.launcher3.allapps.AllAppsHost r2 = r2.getAllAppsHost()
            boolean r2 = r2.isInArrangeMode()
            if (r2 != 0) goto Lce
            java.lang.String r2 = com.lge.launcher3.wing.CarouselLayout.TAG
            java.lang.String r4 = "beginDragSharedDeepShortcut()  show DeepShortcut"
            com.lge.launcher3.util.LGLog.d(r2, r4)
            com.android.launcher3.popup.PopupContainerWithArrow r1 = r16.showDeepShortcut(r17)
            if (r1 == 0) goto Lce
            com.android.launcher3.dragndrop.DragOptions$PreDragCondition r1 = r1.createPreDragCondition()
            r2 = r19
            r2.preDragCondition = r1
            r1 = 1
            r0.isShowingAppShortcutPopupInAllapps = r1
            goto Ld0
        Lce:
            r2 = r19
        Ld0:
            com.android.launcher3.dragndrop.DragController r4 = r0.mDragController
            r6 = r8
            r8 = r18
            r13 = r19
            com.android.launcher3.dragndrop.DragView r1 = r4.startDragForDeepShortcut(r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r0.mDragAllAppView = r1
            boolean r2 = r0.isShowingAppShortcutPopupInAllapps
            if (r2 != 0) goto Le7
            if (r1 == 0) goto Le7
            r2 = 0
            r1.setAlpha(r2)
        Le7:
            r0.mDragShortcutInfo = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.wing.CarouselLayout.beginDragSharedDeepShortcut(android.view.View, com.android.launcher3.DragSource, com.android.launcher3.dragndrop.DragOptions):void");
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragStart(DragSource source, Object info, int dragAction) {
        this.isDragging = true;
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragEnd() {
        View view = this.mFindView;
        if (view != null && view.getAlpha() != this.visibleIconAlpha && this.isDragging) {
            int position = this.mCarouselView.getLayoutManager().getPosition(this.mFindView);
            if (position >= 0) {
                this.mAdapter.onItemDismiss(position);
                this.mFindView.setVisibility(8);
            }
        } else {
            TextView textView = this.mIconTextView;
            if (textView != null) {
                float alpha = textView.getAlpha();
                float f = this.visibleTextAlpha;
                if (alpha != f) {
                    this.mIconTextView.setAlpha(f);
                }
            }
        }
        int itemCount = this.mCarouselView.getLayoutManager().getItemCount();
        for (int i = 0; i < itemCount; i++) {
            View childAt = this.mCarouselView.getLayoutManager().getChildAt(i);
            if (this.mFindView != childAt && childAt != null && childAt.getAlpha() != this.visibleTextAlpha) {
                this.mCarouselView.getLayoutManager().getChildAt(i).setAlpha(this.visibleTextAlpha);
            }
        }
        this.isDragging = false;
        this.mDragAllAppView = null;
        this.mDragShortcutInfo = null;
    }

    public boolean getIsDragging() {
        return this.isDragging && this.mDragShortcutInfo != null;
    }

    public void setDragXY(int dragLayerX, int dragLayerY) {
        int[] iArr = this.mDragXY;
        iArr[0] = dragLayerX;
        iArr[1] = dragLayerY;
        View findView = setFindView(iArr[0], this.mCarouselView.getHeight() / 2);
        this.mFindView = findView;
        if (findView != null && this.mDragShortcutInfo != null) {
            int position = this.mCarouselView.getLayoutManager().getPosition(this.mFindView);
            int i = this.mInsertItemByNull;
            if (i != 1 && i != 2) {
                if (checkAddItemToSwiveAvailable()) {
                    showToastForUnableToAddForSwivel();
                    return;
                } else {
                    this.mAdapter.onItemInsert(this.mDragShortcutInfo, position);
                    this.mAdapter.notifyDataSetChanged();
                }
            }
            this.mInsertItemByNull = 0;
            return;
        }
        if (findView == null) {
            int itemCount = this.mCarouselView.getLayoutManager().getItemCount();
            if (this.mDragXY[0] < this.mCarouselView.getWidth() / 2) {
                if (checkAddItemToSwiveAvailable()) {
                    showToastForUnableToAddForSwivel();
                    return;
                }
                this.mAdapter.onItemInsert(this.mDragShortcutInfo, 0);
                this.mAdapter.notifyDataSetChanged();
                for (int i2 = 1; i2 <= this.mCarouselView.getWidth() / 10; i2++) {
                    int i3 = this.mDragXY[0] + (i2 * 10);
                    this.mFindViewLocationX = i3;
                    View findView2 = setFindView(i3, this.mCarouselView.getHeight() / 2);
                    this.mFindView = findView2;
                    if (findView2 != null) {
                        this.mInsertItemByNull = 1;
                        return;
                    }
                }
                return;
            }
            if (this.mDragXY[0] > this.mCarouselView.getWidth() / 2) {
                if (checkAddItemToSwiveAvailable()) {
                    showToastForUnableToAddForSwivel();
                    return;
                }
                this.mAdapter.onItemInsert(this.mDragShortcutInfo, itemCount);
                this.mAdapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() { // from class: com.lge.launcher3.wing.CarouselLayout.10
                    @Override // java.lang.Runnable
                    public void run() {
                        for (int i4 = 1; i4 <= CarouselLayout.this.mCarouselView.getWidth() / 10; i4++) {
                            CarouselLayout carouselLayout = CarouselLayout.this;
                            carouselLayout.mFindViewLocationX = carouselLayout.mDragXY[0] - (i4 * 10);
                            CarouselLayout carouselLayout2 = CarouselLayout.this;
                            carouselLayout2.mFindView = carouselLayout2.setFindView(carouselLayout2.mFindViewLocationX, CarouselLayout.this.mCarouselView.getHeight() / 2);
                            if (CarouselLayout.this.mFindView != null) {
                                CarouselLayout.this.mInsertItemByNull = 2;
                                return;
                            }
                        }
                    }
                }, 150L);
            }
        }
    }

    public View setFindView(int dragLayerX, int dragLayerY) {
        this.mInsertItemByNull = 0;
        View viewFindChildViewUnder = this.mCarouselView.findChildViewUnder(dragLayerX, r3.getHeight() / 2);
        this.mFindView = viewFindChildViewUnder;
        return viewFindChildViewUnder;
    }

    public int getFindViewLocationX() {
        return this.mFindViewLocationX;
    }

    public int getInsertItemByNull() {
        return this.mInsertItemByNull;
    }

    public void setAppList(ArrayList<ShortcutInfo> list) {
        this.mAdapter.setData(list);
        this.mAdapter.notifyDataSetChanged();
    }

    public void setFindViewAlpha(MotionEvent ev) {
        if (this.mFindView != null) {
            if (ev.getY() < this.carouselStartHeight) {
                this.mFindView.setAlpha(this.invisibleIconAlpha);
            } else {
                this.mFindView.setAlpha(this.visibleIconAlpha);
            }
        }
    }

    public void setItemClickListener(CarouselView.OnItemClickListener listener) {
        this.mCarouselView.setOnItemClickListener(listener);
    }

    public AppListAdapter getAdapter() {
        return this.mAdapter;
    }

    public CarouselView getCarouselView() {
        return this.mCarouselView;
    }

    public SwivelItemTouchHelper getHelper() {
        return this.mHelper;
    }

    public SwivelWeatherView getSwivelWeatherView() {
        return this.mSwivelWeatherView;
    }

    @Override // com.android.launcher3.DropTarget
    public void onDrop(DropTarget.DragObject dragObject) {
        if (this.isShowingAppShortcutPopupInAllapps) {
            LGLog.i(TAG, "onDrop() keep CancelDrag");
            this.keepCancelDrag = true;
            return;
        }
        LGLog.d(TAG, "onDrop() call cancelDrag");
        DragController dragController = this.mDragController;
        if (dragController != null) {
            dragController.cancelDrag();
        }
        this.mIsBlockToAdd = false;
        this.keepCancelDrag = false;
    }

    @Override // com.android.launcher3.DropTarget
    public void getHitRectRelativeToDragLayer(Rect outRect) {
        this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(this, outRect);
    }

    @Override // com.android.launcher3.DropTarget
    public void getLocationInDragLayer(int[] loc) {
        this.mLauncher.getDragLayer().getLocationInDragLayer(this, loc);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void calculateTouchDistance(float x, float y) {
        int[] clampedDragLayerPos = getClampedDragLayerPos(x, y);
        double d = this.mDistanceSinceScroll;
        int[] iArr = this.mLastTouch;
        this.mDistanceSinceScroll = (int) (d + Math.hypot(iArr[0] - clampedDragLayerPos[0], iArr[1] - clampedDragLayerPos[1]));
        int[] iArr2 = this.mLastTouch;
        iArr2[0] = clampedDragLayerPos[0];
        iArr2[1] = clampedDragLayerPos[1];
    }

    private AppInfo findAppInfoFromItemInfo(ItemInfo itemInfo) {
        for (LauncherActivityInfo launcherActivityInfo : LauncherAppsCompat.getInstance(this.mLauncher).getActivityList(itemInfo.getTargetComponent().getPackageName(), itemInfo.user)) {
            if (itemInfo.getTargetComponent().equals(launcherActivityInfo.getComponentName())) {
                return new AppInfo(this.mLauncher, launcherActivityInfo, itemInfo.user, LauncherAppState.getInstance(this.mLauncher).getIconCache());
            }
        }
        return null;
    }

    private int[] getClampedDragLayerPos(float x, float y) {
        this.mLauncher.getDragLayer().getLocalVisibleRect(this.mDragLayerRect);
        this.mTmpPoint[0] = (int) Math.max(this.mDragLayerRect.left, Math.min(x, this.mDragLayerRect.right - 1));
        this.mTmpPoint[1] = (int) Math.max(this.mDragLayerRect.top, Math.min(y, this.mDragLayerRect.bottom - 1));
        return this.mTmpPoint;
    }

    public boolean shouldStartDrag(double distanceDragged) {
        return distanceDragged > ((double) (this.mStartDragThreshold + ((float) this.mFirstDistanceSinceScroll)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearTouchDistance() {
        this.mDistanceSinceScroll = 0;
        this.mFirstDistanceSinceScroll = 0;
        int[] iArr = this.mLastTouch;
        iArr[0] = 0;
        iArr[1] = 0;
    }

    public PopupContainerWithArrow showDeepShortcut(View child, float x, float y) {
        String str = TAG;
        boolean z = child instanceof BubbleTextView;
        LGLog.i(str, "showDeepShortcut()  child = " + child + ", x = " + x + ", y = " + y + ", (child instanceof BubbleTextView) = " + z);
        if (z) {
            removeDeepShortcut();
            PopupContainerWithArrow popupContainerWithArrowShowForIcon = PopupContainerWithArrow.showForIcon((BubbleTextView) child);
            this.mAppShortcutPopup = popupContainerWithArrowShowForIcon;
            if (popupContainerWithArrowShowForIcon == null) {
                LGLog.i(str, "showDeepShortcut()  mAppShortcutPopup = " + popupContainerWithArrowShowForIcon);
                return null;
            }
            AppShortcutPopupListener appShortcutPopupListener = new AppShortcutPopupListener();
            this.mAppShortcutPopupListener = appShortcutPopupListener;
            this.mAppShortcutPopup.setListener(appShortcutPopupListener);
            calculateTouchDistance(x, y);
            this.mFirstDistanceSinceScroll = this.mDistanceSinceScroll;
        }
        return this.mAppShortcutPopup;
    }

    public PopupContainerWithArrow showDeepShortcut(View child) {
        String str = TAG;
        boolean z = child instanceof BubbleTextView;
        LGLog.i(str, "showDeepShortcut()  child = " + child + ", (child instanceof BubbleTextView) = " + z);
        if (z) {
            removeDeepShortcut();
            PopupContainerWithArrow popupContainerWithArrowShowForIcon = PopupContainerWithArrow.showForIcon((BubbleTextView) child);
            this.mAppShortcutPopup = popupContainerWithArrowShowForIcon;
            if (popupContainerWithArrowShowForIcon == null) {
                LGLog.i(str, "showDeepShortcut()  mAppShortcutPopup = " + popupContainerWithArrowShowForIcon);
                return null;
            }
            AppShortcutPopupListener appShortcutPopupListener = new AppShortcutPopupListener();
            this.mAppShortcutPopupListener = appShortcutPopupListener;
            this.mAppShortcutPopup.setListener(appShortcutPopupListener);
        }
        return this.mAppShortcutPopup;
    }

    public void removeDeepShortcut() {
        AppShortcutPopupListener appShortcutPopupListener;
        LGLog.i(TAG, "removeDeepShortcut() mAppShortcutPopup = " + this.mAppShortcutPopup);
        PopupContainerWithArrow popupContainerWithArrow = this.mAppShortcutPopup;
        if (popupContainerWithArrow != null) {
            popupContainerWithArrow.onDragStart(null, null, 0);
        }
        PopupContainerWithArrow popupContainerWithArrow2 = this.mAppShortcutPopup;
        if (popupContainerWithArrow2 != null && (appShortcutPopupListener = this.mAppShortcutPopupListener) != null) {
            popupContainerWithArrow2.removeListener(appShortcutPopupListener);
        }
        this.mAppShortcutPopup = null;
        this.mAppShortcutPopupListener = null;
        clearTouchDistance();
    }

    /* JADX DEBUG: Method merged with bridge method: setState(Ljava/lang/Object;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setState(LauncherState state) {
        PropertySetter.NO_ANIM_PROPERTY_SETTER.setFloat(this, LauncherAnimUtils.VIEW_ALPHA, (state.overviewUi || state.useBlur) ? 0.0f : 1.0f, Interpolators.LINEAR);
    }

    /* JADX DEBUG: Method merged with bridge method: setStateWithAnimation(Ljava/lang/Object;Lcom/android/launcher3/states/StateAnimationConfig;Lcom/android/launcher3/anim/PendingAnimation;)V */
    @Override // com.android.launcher3.statemanager.StateManager.StateHandler
    public void setStateWithAnimation(LauncherState toState, StateAnimationConfig config, PendingAnimation animation) {
        animation.setFloat(this, LauncherAnimUtils.VIEW_ALPHA, (toState.overviewUi || toState.useBlur) ? 0.0f : 1.0f, Interpolators.LINEAR);
    }

    @Override // com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        if (getResources().getConfiguration().orientation == 1) {
            setPadding(insets.left, insets.top, insets.right, insets.bottom);
        } else {
            setPadding(insets.left, 0, insets.right, 0);
        }
    }

    class AppShortcutPopupListener implements PopupContainerWithArrow.PopupContainerListener {
        AppShortcutPopupListener() {
        }

        @Override // com.android.launcher3.popup.PopupContainerWithArrow.PopupContainerListener
        public void OnClose(PopupContainerWithArrow popupContainer) {
            LGLog.i(CarouselLayout.TAG, "OnClose() keepCancelDrag = " + CarouselLayout.this.keepCancelDrag + ", popupContainer = " + popupContainer + ", mDragAllAppView = " + CarouselLayout.this.mDragAllAppView);
            LGLog.i(CarouselLayout.TAG, "OnClose() (popupContainer == mAppShortcutPopup) = " + (popupContainer == CarouselLayout.this.mAppShortcutPopup));
            if (popupContainer == CarouselLayout.this.mAppShortcutPopup) {
                CarouselLayout.this.mAppShortcutPopup.removeListener(CarouselLayout.this.mAppShortcutPopupListener);
                CarouselLayout.this.mAppShortcutPopup = null;
                CarouselLayout.this.mAppShortcutPopupListener = null;
                CarouselLayout.this.clearTouchDistance();
                if (CarouselLayout.this.isShowingAppShortcutPopupInAllapps && CarouselLayout.this.mDragAllAppView != null) {
                    CarouselLayout.this.mDragAllAppView.setAlpha(0.0f);
                }
                CarouselLayout.this.isShowingAppShortcutPopupInAllapps = false;
                if (CarouselLayout.this.keepCancelDrag) {
                    CarouselLayout.this.onDrop(null);
                    return;
                }
                return;
            }
            LGLog.i(CarouselLayout.TAG, "OnClose() mAppShortcutPopup = " + CarouselLayout.this.mAppShortcutPopup);
            if (CarouselLayout.this.mAppShortcutPopup == null) {
                if (CarouselLayout.this.mAppShortcutPopupListener != null) {
                    popupContainer.removeListener(CarouselLayout.this.mAppShortcutPopupListener);
                    CarouselLayout.this.mAppShortcutPopupListener = null;
                }
                CarouselLayout.this.clearTouchDistance();
                CarouselLayout.this.isShowingAppShortcutPopupInAllapps = false;
                if (CarouselLayout.this.keepCancelDrag) {
                    CarouselLayout.this.onDrop(null);
                }
            }
        }
    }

    public void resetLayoutPosition() {
        this.mCarouselView.getLayoutManager().scrollToPosition(0);
        setY(getTop());
        this.mIsEntryScroll = true;
    }

    public void fadeInAnimation(final TextView viewToFadeIn, int animationTime) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(viewToFadeIn, "alpha", this.invisibleTextAlpha, this.visibleTextAlpha), ObjectAnimator.ofFloat(viewToFadeIn, (Property<TextView, Float>) View.SCALE_X, viewToFadeIn.getScaleX(), viewToFadeIn.getScaleX() + this.textScaleDelta), ObjectAnimator.ofFloat(viewToFadeIn, (Property<TextView, Float>) View.SCALE_Y, viewToFadeIn.getScaleY(), viewToFadeIn.getScaleY() + this.textScaleDelta));
        animatorSet.setDuration(animationTime);
        animatorSet.setInterpolator(new PathInterpolator(0.11f, 0.0f, 0.5f, 0.0f));
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.wing.CarouselLayout.11
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                viewToFadeIn.setAlpha(CarouselLayout.this.invisibleTextAlpha);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                viewToFadeIn.setAlpha(CarouselLayout.this.visibleTextAlpha);
            }
        });
        animatorSet.start();
    }

    public void fadeOutAnimation(final TextView viewToFadeOut, int animationTime) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(viewToFadeOut, "alpha", this.visibleTextAlpha, this.invisibleTextAlpha), ObjectAnimator.ofFloat(viewToFadeOut, (Property<TextView, Float>) View.SCALE_X, viewToFadeOut.getScaleX(), viewToFadeOut.getScaleX() - this.textScaleDelta), ObjectAnimator.ofFloat(viewToFadeOut, (Property<TextView, Float>) View.SCALE_Y, viewToFadeOut.getScaleY(), viewToFadeOut.getScaleY() - this.textScaleDelta));
        animatorSet.setDuration(animationTime);
        animatorSet.setInterpolator(new PathInterpolator(0.5f, 1.0f, 0.89f, 1.0f));
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.wing.CarouselLayout.12
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                viewToFadeOut.setAlpha(CarouselLayout.this.visibleTextAlpha);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                viewToFadeOut.setAlpha(CarouselLayout.this.invisibleTextAlpha);
            }
        });
        animatorSet.start();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void updateLiveIcon(final ComponentName componentName) {
        if (this.mAdapter != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.lge.launcher3.wing.-$$Lambda$CarouselLayout$43c5YdWFO95Sdpnn0yHaDNtGpJk
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$updateLiveIcon$0$CarouselLayout(componentName);
                }
            });
        }
    }

    public /* synthetic */ void lambda$updateLiveIcon$0$CarouselLayout(ComponentName componentName) {
        this.mAdapter.onItemUpdate(componentName);
    }

    public void updateLiveIcons() {
        AppListAdapter appListAdapter = this.mAdapter;
        if (appListAdapter == null || appListAdapter.getData() == null) {
            return;
        }
        for (ShortcutInfo shortcutInfo : this.mAdapter.getData()) {
            if (LiveIconManager.getInstance(getContext()).hasLiveIcon(shortcutInfo.getTargetComponent())) {
                updateLiveIcon(shortcutInfo.getTargetComponent());
            }
        }
    }

    public void addAppsAutomatically(ArrayList<ItemInfo> apps) {
        int lastPostionFromSwivelDB;
        ShortcutInfo shortcutInfo;
        if (apps == null || apps.isEmpty()) {
            LGLog.i(TAG, "addAppsAutomatically() app list is empty. apps = " + apps);
            return;
        }
        boolean zIsLoadedAppList = this.mAdapter.isLoadedAppList();
        if (zIsLoadedAppList) {
            lastPostionFromSwivelDB = this.mCarouselView.getLayoutManager().getItemCount();
        } else {
            lastPostionFromSwivelDB = LauncherModel.getLastPostionFromSwivelDB(this.mLauncher);
        }
        LGLog.i(TAG, "addAppsAutomatically() apps.size() = " + apps.size());
        for (ItemInfo itemInfo : apps) {
            if (itemInfo instanceof ShortcutInfo) {
                shortcutInfo = new ShortcutInfo();
                shortcutInfo.copyFrom((ShortcutInfo) itemInfo);
            } else if (itemInfo instanceof AppInfo) {
                shortcutInfo = ((AppInfo) itemInfo).makeShortcut();
            } else {
                LGLog.i(TAG, "addAppsAutomatically() invalid type. item = " + itemInfo);
            }
            shortcutInfo.swivelPosition = lastPostionFromSwivelDB;
            if (zIsLoadedAppList) {
                this.mAdapter.onItemInsert(shortcutInfo, lastPostionFromSwivelDB);
                this.mAdapter.notifyDataSetChanged();
            } else {
                LauncherModel.addItemToDatabaseSwivel(this.mLauncher, shortcutInfo);
                this.mAdapter.appList.add(shortcutInfo);
            }
            lastPostionFromSwivelDB++;
        }
    }

    private boolean checkAddItemToSwiveAvailable() {
        return this.mAdapter.isCarouselItemMax();
    }

    private void showToastForUnableToAddForSwivel() {
        Launcher launcher = this.mLauncher;
        Toast.makeText(launcher, launcher.getString(R.string.swivel_home_maximum_items) + " " + this.mLauncher.getString(R.string.swivel_home_maximum_items_noti), 0).show();
        this.isDragging = false;
        this.mDragAllAppView = null;
        this.mDragShortcutInfo = null;
        clearTouchDistance();
        this.mIsBlockToAdd = true;
    }

    public void setCarouselStartHeight(int orientation) {
        if (orientation == 2) {
            this.carouselStartHeight = (int) this.mLauncher.getResources().getDimension(R.dimen.swivel_carousel_view_start_height_land);
        } else {
            this.carouselStartHeight = (int) this.mLauncher.getResources().getDimension(R.dimen.swivel_carousel_view_start_height_port);
        }
    }
}
