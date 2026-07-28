package com.android.launcher3.allapps;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.InsetDrawable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.BaseContainerView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherTransitionable;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsSearchBarController;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.MultiValueAlpha;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsContainerView extends BaseContainerView implements DragSource, LauncherTransitionable, View.OnTouchListener, View.OnLongClickListener, AllAppsSearchBarController.Callbacks {
    private static final int ALPHA_CHANNEL_COUNT = 2;
    private static final int MAX_NUM_MERGES_PHONE = 2;
    private static final int MIN_ROWS_IN_MERGED_SECTION_PHONE = 3;
    private AllAppsGridAdapter mAdapter;
    AlphabeticalAppsList mApps;
    AllAppsRecyclerView mAppsRecyclerView;
    private final Point mBoundsCheckLastTouchDownPos;
    View mContainerView;
    View mContent;
    private final Point mIconLastTouchPos;
    private RecyclerView.ItemDecoration mItemDecoration;
    Launcher mLauncher;
    private RecyclerView.LayoutManager mLayoutManager;
    private final MultiValueAlpha mMultiValueAlpha;
    private int mNumAppsPerRow;
    private int mNumPredictedAppsPerRow;
    private int mRecyclerViewTopBottomPadding;
    View mRevealView;
    private ViewGroup mSearchBarContainerView;
    AllAppsSearchBarController mSearchBarController;
    private View mSearchBarView;
    private SpannableStringBuilder mSearchQueryBuilder;
    private int mSectionNamesMargin;

    public void addSpringFromFlingUpdateListener(ValueAnimator animator, float velocity) {
    }

    public View getSearchView() {
        return null;
    }

    @Override // com.android.launcher3.LauncherTransitionable
    public void onLauncherTransitionPrepare(Launcher l, boolean animated, boolean toWorkspace) {
    }

    @Override // com.android.launcher3.LauncherTransitionable
    public void onLauncherTransitionStart(Launcher l, boolean animated, boolean toWorkspace) {
    }

    @Override // com.android.launcher3.LauncherTransitionable
    public void onLauncherTransitionStep(Launcher l, float t) {
    }

    public void reset(boolean animate) {
    }

    public boolean shouldContainerScroll(MotionEvent ev) {
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsDeleteDropTarget() {
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsFlingToDelete() {
        return true;
    }

    public AllAppsContainerView(Context context) {
        this(context, null);
    }

    public AllAppsContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBoundsCheckLastTouchDownPos = new Point(-1, -1);
        this.mIconLastTouchPos = new Point();
        this.mSearchQueryBuilder = null;
        Resources resources = context.getResources();
        this.mLauncher = (Launcher) context;
        this.mSectionNamesMargin = resources.getDimensionPixelSize(R.dimen.all_apps_grid_view_start_margin);
        this.mApps = new AlphabeticalAppsList(context);
        AllAppsGridAdapter allAppsGridAdapter = new AllAppsGridAdapter(context, this.mApps, this, this.mLauncher, this);
        this.mAdapter = allAppsGridAdapter;
        allAppsGridAdapter.setEmptySearchText(resources.getString(R.string.all_apps_loading_message));
        this.mApps.setAdapter(this.mAdapter);
        this.mLayoutManager = this.mAdapter.getLayoutManager();
        this.mItemDecoration = this.mAdapter.getItemDecoration();
        this.mRecyclerViewTopBottomPadding = resources.getDimensionPixelSize(R.dimen.all_apps_list_top_bottom_padding);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        this.mSearchQueryBuilder = spannableStringBuilder;
        Selection.setSelection(spannableStringBuilder, 0);
        this.mMultiValueAlpha = new MultiValueAlpha(this, 2);
    }

    public void setPredictedApps(List<ComponentKey> apps) {
        this.mApps.setPredictedApps(apps);
    }

    public void setApps(List<AppInfo> apps) {
        if (LGHomeFeature.isEnableDefaultHome()) {
            return;
        }
        this.mApps.setApps(apps);
    }

    public void addApps(List<AppInfo> apps) {
        if (LGHomeFeature.isEnableDefaultHome()) {
            return;
        }
        this.mApps.addApps(apps);
    }

    public void updateApps(List<AppInfo> apps) {
        if (LGHomeFeature.isEnableDefaultHome()) {
            return;
        }
        this.mApps.updateApps(apps);
    }

    public void removeApps(List<AppInfo> apps) {
        if (LGHomeFeature.isEnableDefaultHome()) {
            return;
        }
        this.mApps.removeApps(apps);
    }

    public MultiValueAlpha.AlphaProperty getAlphaProperty(int index) {
        return this.mMultiValueAlpha.getProperty(index);
    }

    public void setSearchBarController(AllAppsSearchBarController searchController) {
        if (this.mSearchBarController != null) {
            throw new RuntimeException("Expected search bar controller to only be set once");
        }
        this.mSearchBarController = searchController;
        searchController.initialize(this.mApps, this);
        View view = searchController.getView(this.mSearchBarContainerView);
        this.mSearchBarContainerView.addView(view);
        this.mSearchBarContainerView.setVisibility(0);
        this.mSearchBarView = view;
        setHasSearchBar();
        updateBackgroundAndPaddings();
    }

    public void scrollToTop() {
        this.mAppsRecyclerView.scrollToTop();
    }

    public View getContentView() {
        return this.mContainerView;
    }

    public View getSearchBarView() {
        return this.mSearchBarView;
    }

    public View getRevealView() {
        return this.mRevealView;
    }

    public AllAppsSearchBarController newDefaultAppSearchController() {
        return new DefaultAppSearchController(getContext(), this, this.mAppsRecyclerView);
    }

    public void startAppsSearch() {
        AllAppsSearchBarController allAppsSearchBarController = this.mSearchBarController;
        if (allAppsSearchBarController != null) {
            allAppsSearchBarController.focusSearchField();
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mAdapter.setRtl(Utilities.isRtl(getResources()));
        this.mContent = findViewById(R.id.content);
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() { // from class: com.android.launcher3.allapps.AllAppsContainerView.1
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    AllAppsContainerView.this.mAppsRecyclerView.requestFocus();
                }
            }
        };
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.search_box_container);
        this.mSearchBarContainerView = viewGroup;
        viewGroup.setOnFocusChangeListener(onFocusChangeListener);
        View viewFindViewById = findViewById(R.id.all_apps_container);
        this.mContainerView = viewFindViewById;
        viewFindViewById.setOnFocusChangeListener(onFocusChangeListener);
        this.mRevealView = findViewById(R.id.all_apps_reveal);
        AllAppsRecyclerView allAppsRecyclerView = (AllAppsRecyclerView) findViewById(R.id.apps_list_view);
        this.mAppsRecyclerView = allAppsRecyclerView;
        allAppsRecyclerView.setApps(this.mApps);
        this.mAppsRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mAppsRecyclerView.setAdapter(this.mAdapter);
        this.mAppsRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = this.mItemDecoration;
        if (itemDecoration != null) {
            this.mAppsRecyclerView.addItemDecoration(itemDecoration);
        }
        updateBackgroundAndPaddings();
    }

    @Override // com.android.launcher3.allapps.AllAppsSearchBarController.Callbacks
    public void onBoundsChanged(Rect newBounds) {
        this.mLauncher.updateOverlayBounds(newBounds);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        AlphabeticalAppsList.MergeAlgorithm simpleSectionMergeAlgorithm;
        int iWidth = !this.mContentBounds.isEmpty() ? this.mContentBounds.width() : View.MeasureSpec.getSize(widthMeasureSpec);
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        deviceProfile.updateAppsViewNumCols(getResources(), iWidth);
        if (this.mNumAppsPerRow != deviceProfile.allAppsNumCols || this.mNumPredictedAppsPerRow != deviceProfile.allAppsNumPredictiveCols) {
            this.mNumAppsPerRow = deviceProfile.allAppsNumCols;
            this.mNumPredictedAppsPerRow = deviceProfile.allAppsNumPredictiveCols;
            if (this.mSectionNamesMargin == 0 || !deviceProfile.isPhone) {
                simpleSectionMergeAlgorithm = new FullMergeAlgorithm();
            } else {
                simpleSectionMergeAlgorithm = new SimpleSectionMergeAlgorithm((int) Math.ceil(this.mNumAppsPerRow / 2.0f), 3, 2);
            }
            this.mAppsRecyclerView.setNumAppsPerRow(deviceProfile, this.mNumAppsPerRow);
            this.mAdapter.setNumAppsPerRow(this.mNumAppsPerRow);
            this.mApps.setNumAppsPerRow(this.mNumAppsPerRow, this.mNumPredictedAppsPerRow, simpleSectionMergeAlgorithm);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // com.android.launcher3.BaseContainerView
    protected void onUpdateBackgroundAndPaddings(Rect searchBarBounds, Rect padding) {
        boolean zIsRtl = Utilities.isRtl(getResources());
        InsetDrawable insetDrawable = new InsetDrawable(getResources().getDrawable(R.drawable.quantum_panel_shape), padding.left, 0, padding.right, 0);
        Rect rect = new Rect();
        insetDrawable.getPadding(rect);
        this.mContainerView.setBackground(insetDrawable);
        this.mRevealView.setBackground(insetDrawable.getConstantState().newDrawable());
        this.mAppsRecyclerView.updateBackgroundPadding(rect);
        this.mAdapter.updateBackgroundPadding(rect);
        this.mContent.setPadding(0, padding.top, 0, padding.bottom);
        this.mContainerView.setPadding(0, 0, 0, 0);
        int iMax = Math.max(this.mSectionNamesMargin, this.mAppsRecyclerView.getMaxScrollbarWidth());
        int i = this.mRecyclerViewTopBottomPadding;
        if (zIsRtl) {
            this.mAppsRecyclerView.setPadding(padding.left + this.mAppsRecyclerView.getMaxScrollbarWidth(), i, padding.right + iMax, i);
        } else {
            this.mAppsRecyclerView.setPadding(padding.left + iMax, i, padding.right + this.mAppsRecyclerView.getMaxScrollbarWidth(), i);
        }
        if (this.mSearchBarView != null) {
            Rect rect2 = new Rect();
            if (this.mSearchBarView.getBackground() != null) {
                this.mSearchBarView.getBackground().getPadding(rect2);
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mSearchBarContainerView.getLayoutParams();
            layoutParams.leftMargin = searchBarBounds.left - rect2.left;
            layoutParams.topMargin = searchBarBounds.top - rect2.top;
            layoutParams.rightMargin = (getMeasuredWidth() - searchBarBounds.right) - rect2.right;
            this.mSearchBarContainerView.requestLayout();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!this.mSearchBarController.isSearchFieldFocused() && event.getAction() == 0) {
            int unicodeChar = event.getUnicodeChar();
            if (((unicodeChar <= 0 || Character.isWhitespace(unicodeChar) || Character.isSpaceChar(unicodeChar)) ? false : true) && TextKeyListener.getInstance().onKeyDown(this, this.mSearchQueryBuilder, event.getKeyCode(), event) && this.mSearchQueryBuilder.length() > 0) {
                this.mSearchBarController.focusSearchField();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return handleTouchEvent(ev);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        return handleTouchEvent(ev);
    }

    public String getDescription() {
        return getContext().getString(R.string.all_apps_button_label);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent ev) {
        int action = ev.getAction();
        if (action != 0 && action != 2) {
            return false;
        }
        this.mIconLastTouchPos.set((int) ev.getX(), (int) ev.getY());
        return false;
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        if (!this.mLauncher.isAppsViewVisible() || this.mLauncher.getWorkspace().isSwitchingState() || !this.mLauncher.isDraggingEnabled()) {
            return false;
        }
        this.mLauncher.getWorkspace().beginDragShared(v, this.mIconLastTouchPos, this, false);
        this.mLauncher.enterSpringLoadedDragMode();
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsAppInfoDropTarget() {
        return !LGHomeFeature.isEnableDefaultHome();
    }

    @Override // com.android.launcher3.DragSource
    public float getIntrinsicIconScaleFactor() {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        return deviceProfile.allAppsIconSizePx / deviceProfile.iconSizePx;
    }

    @Override // com.android.launcher3.DragSource
    public void onFlingToDeleteCompleted() {
        this.mLauncher.exitSpringLoadedDragModeDelayed(true, 300, null);
        this.mLauncher.unlockScreenOrientation(false);
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x004a  */
    @Override // com.android.launcher3.DragSource
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onDropCompleted(android.view.View r4, com.android.launcher3.DropTarget.DragObject r5, boolean r6, boolean r7) {
        /*
            r3 = this;
            r0 = 0
            r1 = 1
            if (r6 != 0) goto L16
            if (r7 == 0) goto L16
            com.android.launcher3.Launcher r6 = r3.mLauncher
            com.android.launcher3.Workspace r6 = r6.getWorkspace()
            if (r4 == r6) goto L1d
            boolean r6 = r4 instanceof com.android.launcher3.DeleteDropTarget
            if (r6 != 0) goto L1d
            boolean r6 = r4 instanceof com.android.launcher3.folder.Folder
            if (r6 != 0) goto L1d
        L16:
            com.android.launcher3.Launcher r6 = r3.mLauncher
            r2 = 300(0x12c, float:4.2E-43)
            r6.exitSpringLoadedDragModeDelayed(r1, r2, r0)
        L1d:
            com.android.launcher3.Launcher r6 = r3.mLauncher
            r2 = 0
            r6.unlockScreenOrientation(r2)
            if (r7 != 0) goto L52
            boolean r6 = r4 instanceof com.android.launcher3.Workspace
            if (r6 == 0) goto L4a
            com.android.launcher3.Launcher r6 = r3.mLauncher
            int r6 = r6.getCurrentWorkspaceScreen()
            com.android.launcher3.Workspace r4 = (com.android.launcher3.Workspace) r4
            android.view.View r4 = r4.getChildAt(r6)
            com.android.launcher3.CellLayout r4 = (com.android.launcher3.CellLayout) r4
            java.lang.Object r5 = r5.dragInfo
            com.android.launcher3.model.data.ItemInfo r5 = (com.android.launcher3.model.data.ItemInfo) r5
            if (r4 == 0) goto L4a
            r4.calculateSpans(r5)
            int r6 = r5.spanX
            int r5 = r5.spanY
            boolean r4 = r4.findCellForSpan(r0, r6, r5)
            r4 = r4 ^ r1
            goto L4b
        L4a:
            r4 = r2
        L4b:
            if (r4 == 0) goto L52
            com.android.launcher3.Launcher r4 = r3.mLauncher
            r4.showOutOfSpaceMessage(r2)
        L52:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.AllAppsContainerView.onDropCompleted(android.view.View, com.android.launcher3.DropTarget$DragObject, boolean, boolean):void");
    }

    @Override // com.android.launcher3.LauncherTransitionable
    public void onLauncherTransitionEnd(Launcher l, boolean animated, boolean toWorkspace) {
        if (toWorkspace) {
            this.mSearchBarController.reset();
        }
    }

    @Override // com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        targetParent.containerType = this.mAppsRecyclerView.getContainerType(v);
    }

    private boolean handleTouchEvent(MotionEvent ev) {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int action = ev.getAction();
        if (action == 0) {
            if (!this.mContentBounds.isEmpty()) {
                new Rect(this.mContentBounds).inset((-deviceProfile.allAppsIconSizePx) / 2, 0);
                if (ev.getX() < r3.left || ev.getX() > r3.right) {
                    this.mBoundsCheckLastTouchDownPos.set(x, y);
                    return true;
                }
            } else if (ev.getX() < getPaddingLeft() || ev.getX() > getWidth() - getPaddingRight()) {
                this.mBoundsCheckLastTouchDownPos.set(x, y);
                return true;
            }
        } else {
            if (action != 1) {
                if (action == 3) {
                }
            } else if (this.mBoundsCheckLastTouchDownPos.x > -1) {
                ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
                if (((float) Math.hypot(ev.getX() - this.mBoundsCheckLastTouchDownPos.x, ev.getY() - this.mBoundsCheckLastTouchDownPos.y)) < viewConfiguration.getScaledTouchSlop()) {
                    ((Launcher) getContext()).showWorkspace(true);
                    return true;
                }
            }
            this.mBoundsCheckLastTouchDownPos.set(-1, -1);
        }
        return false;
    }

    @Override // com.android.launcher3.allapps.AllAppsSearchBarController.Callbacks
    public void onSearchResult(String query, ArrayList<ComponentKey> apps) {
        if (apps != null) {
            if (apps.isEmpty()) {
                this.mAdapter.setEmptySearchText(String.format(getResources().getString(R.string.all_apps_no_search_results), query));
            } else {
                this.mAppsRecyclerView.scrollToTop();
            }
            this.mApps.setOrderedFilter(apps);
        }
    }

    @Override // com.android.launcher3.allapps.AllAppsSearchBarController.Callbacks
    public void clearSearchResult() {
        this.mApps.setOrderedFilter(null);
        this.mSearchQueryBuilder.clear();
        this.mSearchQueryBuilder.clearSpans();
        Selection.setSelection(this.mSearchQueryBuilder, 0);
    }
}
