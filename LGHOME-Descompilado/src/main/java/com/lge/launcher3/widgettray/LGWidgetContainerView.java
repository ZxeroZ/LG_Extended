package com.lge.launcher3.widgettray;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.viewpager.widget.ViewPager;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DropTarget;
import com.android.launcher3.PageIndicator;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.model.WidgetsModel;
import com.android.launcher3.widget.WidgetCell;
import com.android.launcher3.widget.WidgetsContainerView;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsSearch;
import com.lge.launcher3.allapps.IAllAppsSearchListener;
import com.lge.launcher3.badge.uninstall.UninstallBadgeUtils;
import com.lge.launcher3.droptarget.DisableDropTarget;
import com.lge.launcher3.droptarget.LGUninstallDropTarget;
import com.lge.launcher3.pageindicator.PageIndicatorExtension;
import com.lge.launcher3.pageindicator.PageIndicatorListener;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.widgettray.SearchWidgetsAsyncTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class LGWidgetContainerView extends WidgetsContainerView implements IAllAppsSearchListener, SearchWidgetsAsyncTask.ISearchTaskPostExcute {
    private static final List<Rect> SYSTEM_GESTURE_EXCLUSION_RECT = Collections.singletonList(new Rect());
    private static final String TAG = "LGWidgetContainerView";
    private final int WIDGET_EDIT_ANIM_CLOSE_ALPHA_DURATION;
    private final int WIDGET_EDIT_ANIM_CLOSE_SEL_ALPHA_DURATION;
    private final int WIDGET_EDIT_ANIM_CLOSE_TOTAL_DURATION;
    private final int WIDGET_EDIT_ANIM_OPEN_ALPHA_START_DELAY;
    private final int WIDGET_EDIT_ANIM_OPEN_SEL_ALPHA_DURATION;
    private final int WIDGET_EDIT_ANIM_OPEN_TOTAL_DURATION;
    private AnimatorSet mAnimatorSet;
    private GroupWidgetItemAdapter mGroupWidgetItemAdapter;
    private GridView mGroupWidgetPopupGridview;
    private ViewGroup mGroupWidgetPopupLayout;
    private TextView mGroupWidgetPopupName;
    private AllAppsSearch mMenuSearch;
    private View mNoSearchResult;
    private Configuration mOldConfig;
    private PageIndicatorExtension mPageIndicator;
    private ImageButton mSearchButton;
    private boolean mSearchState;
    private SearchWidgetsAsyncTask mSearchWidgetsAsyncTask;
    private ViewGroup mSearch_edit;
    private LGWidgetCell mSelectedWidgetCell;
    private boolean mShowGroupWidgetPopup;
    private WidgetsUninstallButton mUninstallBtn;
    private ViewPager mViewPager;
    private WidgetsViewPagerAdapter mViewPagerAdapter;
    private ViewGroup mWidgetContainer;
    private TextView mWidgetTitle;
    private ViewGroup mWidgetTitleBar;
    private View.OnKeyListener mWidgetsKeyListener;

    @Override // com.android.launcher3.widget.WidgetsContainerView, com.android.launcher3.BaseContainerView
    protected void onUpdateBackgroundAndPaddings(Rect searchBarBounds, Rect padding) {
    }

    @Override // com.android.launcher3.widget.WidgetsContainerView
    public void scrollToTop() {
    }

    public LGWidgetContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.WIDGET_EDIT_ANIM_OPEN_TOTAL_DURATION = 500;
        this.WIDGET_EDIT_ANIM_OPEN_SEL_ALPHA_DURATION = 130;
        this.WIDGET_EDIT_ANIM_OPEN_ALPHA_START_DELAY = 100;
        this.WIDGET_EDIT_ANIM_CLOSE_TOTAL_DURATION = 300;
        this.WIDGET_EDIT_ANIM_CLOSE_SEL_ALPHA_DURATION = 180;
        this.WIDGET_EDIT_ANIM_CLOSE_ALPHA_DURATION = 150;
        this.mSelectedWidgetCell = null;
        this.mAnimatorSet = null;
        this.mShowGroupWidgetPopup = false;
        this.mSearchButton = null;
        this.mMenuSearch = null;
        this.mNoSearchResult = null;
        this.mSearchWidgetsAsyncTask = null;
        this.mSearchState = false;
        this.mWidgetsKeyListener = new View.OnKeyListener() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.7
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                LGLog.i(LGWidgetContainerView.TAG, "onKey v:" + v + " keyCode:" + keyCode + " event:" + event);
                if (keyCode == 4) {
                    if (LGHomeFeature.Config.FEATURE_USE_WIDGET_SEARCH.getValue() && LGWidgetContainerView.this.mMenuSearch != null && LGWidgetContainerView.this.mMenuSearch.onBackPressed()) {
                        return true;
                    }
                    if (LGWidgetContainerView.this.mUninstallBtn.isUninstallMode()) {
                        LGWidgetContainerView.this.mUninstallBtn.setUninstallMode(false);
                        return true;
                    }
                }
                return false;
            }
        };
        init();
    }

    public LGWidgetContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.WIDGET_EDIT_ANIM_OPEN_TOTAL_DURATION = 500;
        this.WIDGET_EDIT_ANIM_OPEN_SEL_ALPHA_DURATION = 130;
        this.WIDGET_EDIT_ANIM_OPEN_ALPHA_START_DELAY = 100;
        this.WIDGET_EDIT_ANIM_CLOSE_TOTAL_DURATION = 300;
        this.WIDGET_EDIT_ANIM_CLOSE_SEL_ALPHA_DURATION = 180;
        this.WIDGET_EDIT_ANIM_CLOSE_ALPHA_DURATION = 150;
        this.mSelectedWidgetCell = null;
        this.mAnimatorSet = null;
        this.mShowGroupWidgetPopup = false;
        this.mSearchButton = null;
        this.mMenuSearch = null;
        this.mNoSearchResult = null;
        this.mSearchWidgetsAsyncTask = null;
        this.mSearchState = false;
        this.mWidgetsKeyListener = new View.OnKeyListener() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.7
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                LGLog.i(LGWidgetContainerView.TAG, "onKey v:" + v + " keyCode:" + keyCode + " event:" + event);
                if (keyCode == 4) {
                    if (LGHomeFeature.Config.FEATURE_USE_WIDGET_SEARCH.getValue() && LGWidgetContainerView.this.mMenuSearch != null && LGWidgetContainerView.this.mMenuSearch.onBackPressed()) {
                        return true;
                    }
                    if (LGWidgetContainerView.this.mUninstallBtn.isUninstallMode()) {
                        LGWidgetContainerView.this.mUninstallBtn.setUninstallMode(false);
                        return true;
                    }
                }
                return false;
            }
        };
        init();
    }

    @Override // com.android.launcher3.BaseContainerView, com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        super.setInsets(insets);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            LGLog.d(TAG, "setInsets - " + insets);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.topMargin = insets.top;
            marginLayoutParams.bottomMargin = insets.bottom;
            marginLayoutParams.rightMargin = insets.right;
            marginLayoutParams.leftMargin = insets.left;
        }
    }

    private void init() {
        this.mViewPagerAdapter = new WidgetsViewPagerAdapter(getContext(), this, this, this.mLauncher);
        this.mOldConfig = new Configuration(getResources().getConfiguration());
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        if (this.mLauncher != null && this.mLauncher.getDeviceProfile() != null && this.mLauncher.getDeviceProfile().allowRotation && !this.mLauncher.getDeviceProfile().isLandscape) {
            if ((newConfig.diff(this.mOldConfig) & 128) != 0) {
                invalidatePageData();
                layout();
            }
            this.mOldConfig.setTo(newConfig);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override // com.android.launcher3.widget.WidgetsContainerView, android.view.View
    protected void onFinishInflate() {
        View contentView = getContentView();
        contentView.setOnTouchListener(new View.OnTouchListener() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    LGWidgetContainerView.this.closeGroupWidgetPopup();
                }
                return true;
            }
        });
        contentView.setImportantForAccessibility(2);
        this.mContent = findViewById(R.id.content);
        this.mWidgetTitle = (TextView) findViewById(R.id.widget_tray_title);
        WidgetsUninstallButton widgetsUninstallButton = (WidgetsUninstallButton) findViewById(R.id.widget_tray_uninstall);
        this.mUninstallBtn = widgetsUninstallButton;
        widgetsUninstallButton.init(this, this.mLauncher, this.mViewPagerAdapter);
        this.mViewPager = (ViewPager) findViewById(R.id.widgets_list_view);
        this.mWidgetTitleBar = (ViewGroup) findViewById(R.id.widget_title_bar);
        this.mWidgetContainer = (ViewGroup) findViewById(R.id.widget_container);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.widget_group_popup_view);
        this.mGroupWidgetPopupLayout = viewGroup;
        this.mGroupWidgetPopupName = (TextView) viewGroup.findViewById(R.id.widget_name);
        GridView gridView = (GridView) this.mGroupWidgetPopupLayout.findViewById(R.id.group_widget_grid);
        this.mGroupWidgetPopupGridview = gridView;
        gridView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.2
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (LGWidgetContainerView.this.mShowGroupWidgetPopup) {
                    LGWidgetContainerView.this.showGroupWidgetPopupView(true);
                    LGWidgetContainerView.this.mShowGroupWidgetPopup = false;
                }
            }
        });
        GroupWidgetItemAdapter groupWidgetItemAdapter = new GroupWidgetItemAdapter(this.mLauncher, null, this, this, (this.mLauncher == null || this.mLauncher.getDeviceProfile() == null) ? false : this.mLauncher.getDeviceProfile().isTablet);
        this.mGroupWidgetItemAdapter = groupWidgetItemAdapter;
        this.mGroupWidgetPopupGridview.setAdapter((ListAdapter) groupWidgetItemAdapter);
        showGroupWidgetPopupView(false);
        this.mViewPager.setOffscreenPageLimit(getContext().getResources().getInteger(R.integer.config_widget_tray_page_limite));
        this.mViewPager.setAdapter(this.mViewPagerAdapter);
        PageIndicatorExtension pageIndicatorExtension = (PageIndicatorExtension) findViewById(R.id.widgets_page_indicator);
        this.mPageIndicator = pageIndicatorExtension;
        if (pageIndicatorExtension != null) {
            pageIndicatorExtension.setListener(new PageIndicatorListener() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.3
                @Override // com.lge.launcher3.pageindicator.PageIndicatorListener
                public void onChangePage(int page) {
                    if (LGWidgetContainerView.this.mViewPager != null) {
                        LGWidgetContainerView.this.mViewPager.setCurrentItem(LGWidgetContainerView.this.mViewPagerAdapter.getPositionAsLayoutDirection(page));
                    }
                }
            });
        }
        if (LGHomeFeature.Config.FEATURE_USE_WIDGET_SEARCH.getValue()) {
            this.mUninstallBtn.setmWidgetContainerCallbacks(new WidgetContainerCallbacks() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.4
                @Override // com.lge.launcher3.widgettray.WidgetContainerCallbacks
                public void onChange(boolean isUninstallMode) {
                    int i = isUninstallMode ? 4 : 0;
                    if (LGWidgetContainerView.this.mSearchButton != null) {
                        LGWidgetContainerView.this.mSearchButton.setVisibility(i);
                    }
                }
            });
            this.mSearch_edit = (ViewGroup) findViewById(R.id.search_edit);
            AllAppsSearch allAppsSearch = (AllAppsSearch) findViewById(R.id.widget_search_bar);
            this.mMenuSearch = allAppsSearch;
            allAppsSearch.setOnSearchListener(this);
            ImageButton imageButton = (ImageButton) findViewById(R.id.lg_page_menu_search_btn);
            this.mSearchButton = imageButton;
            imageButton.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.5
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    LGWidgetContainerView.this.mMenuSearch.showAppsSearchBar("");
                    LGWidgetContainerView.this.onSearch("");
                }
            });
            this.mNoSearchResult = findViewById(R.id.no_search_result);
        } else {
            this.mUninstallBtn.setmWidgetContainerCallbacks(new WidgetContainerCallbacks() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.6
                @Override // com.lge.launcher3.widgettray.WidgetContainerCallbacks
                public void onChange(boolean isUninstallMode) {
                    if (isUninstallMode) {
                        LGWidgetContainerView.this.mWidgetTitle.setText(R.string.widget_delete);
                    } else {
                        LGWidgetContainerView.this.mWidgetTitle.setText(R.string.widget_button_text);
                    }
                }
            });
            ImageButton imageButton2 = (ImageButton) findViewById(R.id.lg_page_menu_search_btn);
            this.mSearchButton = imageButton2;
            if (imageButton2 != null) {
                imageButton2.setVisibility(8);
            }
        }
        layout();
        setOnKeyListener(this.mWidgetsKeyListener);
    }

    @Override // com.android.launcher3.widget.WidgetsContainerView
    public void addWidgets(WidgetsModel model) {
        boolean z = this.mViewPagerAdapter.getCount() == 0;
        this.mViewPagerAdapter.setWidgetsModel(model);
        this.mViewPagerAdapter.notifyDataSetChanged();
        if (z) {
            this.mViewPager.setCurrentItem(this.mViewPagerAdapter.getFirstPageIndex());
        }
        if (this.mPageIndicator.getChildCount() == 0) {
            initPageIndicator();
        } else {
            refreshPageIndicator();
        }
    }

    private void initPageIndicator() {
        this.mPageIndicator.setVisibility(0);
        refreshPageIndicator();
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.8
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int position) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                int positionAsLayoutDirection = LGWidgetContainerView.this.mViewPagerAdapter.getPositionAsLayoutDirection(position);
                if (LGWidgetContainerView.this.mPageIndicator != null) {
                    LGWidgetContainerView.this.mPageIndicator.setActiveMarker(positionAsLayoutDirection);
                }
                LGWidgetContainerView.this.announceAccessibility(positionAsLayoutDirection);
                LGWidgetContainerView.this.mLauncher.getRootView().setDisallowBackGesture(false);
            }
        });
    }

    public void refreshPageIndicator() {
        if (this.mPageIndicator.getChildCount() != this.mViewPagerAdapter.getCount()) {
            this.mPageIndicator.removeAllMarkers(true);
            ArrayList<PageIndicator.PageMarkerResources> arrayList = new ArrayList<>();
            for (int i = 0; i < this.mViewPagerAdapter.getCount(); i++) {
                arrayList.add(new PageIndicator.PageMarkerResources());
            }
            this.mPageIndicator.addMarkers(arrayList, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void announceAccessibility(int position) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled()) {
            announceForAccessibility(String.format(getContext().getString(R.string.widget_button_text) + getContext().getString(R.string.apps_customize_widgets_talkback_format), Integer.valueOf(position + 1), Integer.valueOf(this.mViewPagerAdapter.getCount())) + ", " + String.format(getContext().getString(R.string.talkback_gird_locate_lines_rows), Integer.valueOf(this.mLauncher.getResources().getInteger(R.integer.widget_tray_col_port)), Integer.valueOf(this.mLauncher.getResources().getInteger(R.integer.widget_tray_row_land))));
        }
    }

    @Override // com.android.launcher3.widget.WidgetsContainerView, android.view.View.OnClickListener
    public void onClick(View v) {
        PendingAddItemInfo pendingAddItemInfo = (PendingAddItemInfo) v.getTag();
        WidgetCell widgetCell = (WidgetCell) v;
        if (this.mUninstallBtn.isUninstallMode()) {
            WidgetsImageView widgetsImageView = (WidgetsImageView) widgetCell.findViewById(R.id.widget_preview);
            if (widgetsImageView.isAvailableUninstall()) {
                int i = AnonymousClass15.$SwitchMap$com$lge$launcher3$badge$uninstall$UninstallBadgeUtils$UninstallType[widgetsImageView.getUninstallType().ordinal()];
                if (i == 1) {
                    LGUninstallDropTarget.startUninstallActivity(this.mLauncher, pendingAddItemInfo);
                    return;
                } else {
                    if (i != 2) {
                        return;
                    }
                    DisableDropTarget.startDisableActivity(this.mLauncher, pendingAddItemInfo);
                    return;
                }
            }
            return;
        }
        if (v instanceof LGWidgetCell) {
            LGWidgetCell lGWidgetCell = (LGWidgetCell) v;
            Object info = lGWidgetCell.getInfo();
            if ((info instanceof GroupLauncherAppWidgetProviderInfo) || (info instanceof GroupResolveInfo)) {
                AnimatorSet animatorSet = this.mAnimatorSet;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.mAnimatorSet = null;
                }
                this.mShowGroupWidgetPopup = true;
                this.mSelectedWidgetCell = lGWidgetCell;
                updateWidgetPopupView(info);
                this.mGroupWidgetPopupLayout.setVisibility(0);
                this.mGroupWidgetPopupLayout.setAlpha(0.0f);
                return;
            }
        }
        super.onClick(v);
    }

    /* JADX INFO: renamed from: com.lge.launcher3.widgettray.LGWidgetContainerView$15, reason: invalid class name */
    static /* synthetic */ class AnonymousClass15 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$badge$uninstall$UninstallBadgeUtils$UninstallType;

        static {
            int[] iArr = new int[UninstallBadgeUtils.UninstallType.values().length];
            $SwitchMap$com$lge$launcher3$badge$uninstall$UninstallBadgeUtils$UninstallType = iArr;
            try {
                iArr[UninstallBadgeUtils.UninstallType.UNINSTALL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$badge$uninstall$UninstallBadgeUtils$UninstallType[UninstallBadgeUtils.UninstallType.DISABLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private void layout() {
        int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.widgettray_page_indiator_padding);
        int dimension = (int) getResources().getDimension(R.dimen.device_profile_pageIndicator_padding);
        PageIndicatorExtension pageIndicatorExtension = this.mPageIndicator;
        pageIndicatorExtension.setPadding(pageIndicatorExtension.getPaddingLeft(), dimensionPixelOffset, this.mPageIndicator.getPaddingRight(), dimensionPixelOffset);
        this.mPageIndicator.setTypePadding(dimension);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mPageIndicator.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.widget_page_indicator_height);
        if (!this.mLauncher.getDeviceProfile().allowRotation && !this.mLauncher.getDeviceProfile().isLandscape) {
            layoutParams.weight = 0.11f;
        }
        this.mPageIndicator.setLayoutParams(layoutParams);
        this.mWidgetTitle.setPaddingRelative(getResources().getDimensionPixelSize(R.dimen.widget_title_padding_start), 0, 0, 0);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mWidgetTitleBar.getLayoutParams();
        layoutParams2.height = getResources().getDimensionPixelSize(R.dimen.widget_title_bar_height);
        this.mWidgetTitleBar.setLayoutParams(layoutParams2);
    }

    public void setVisibilityGroupWidgetPopupView(boolean value) {
        ViewGroup viewGroup = this.mGroupWidgetPopupLayout;
        if (viewGroup != null) {
            if (value) {
                viewGroup.animate().setListener(null);
                this.mWidgetContainer.animate().alpha(0.0f).setDuration(50L).start();
                this.mGroupWidgetPopupLayout.setVisibility(0);
                this.mGroupWidgetPopupLayout.setAlpha(0.0f);
                this.mGroupWidgetPopupLayout.setScaleX(0.5f);
                this.mGroupWidgetPopupLayout.setScaleY(0.5f);
                this.mGroupWidgetPopupLayout.animate().setInterpolator(new DecelerateInterpolator());
                this.mGroupWidgetPopupLayout.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setStartDelay(50L).setDuration(180L).start();
                this.mWidgetContainer.animate().setListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.9
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        LGWidgetContainerView.this.mWidgetContainer.setVisibility(8);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (LGWidgetContainerView.this.isOpenGroupWidgetPopup()) {
                            LGWidgetContainerView.this.mWidgetContainer.setVisibility(8);
                        } else if (LGWidgetContainerView.this.mWidgetContainer.getAlpha() != 0.0f) {
                            LGWidgetContainerView.this.mWidgetContainer.setAlpha(0.0f);
                            LGWidgetContainerView.this.mWidgetContainer.setVisibility(8);
                        }
                    }
                });
                return;
            }
            this.mWidgetContainer.animate().setListener(null);
            this.mGroupWidgetPopupLayout.animate().alpha(0.0f).setDuration(120L).start();
            if (this.mWidgetContainer.getVisibility() != 0) {
                this.mWidgetContainer.setVisibility(0);
                this.mWidgetContainer.setAlpha(0.0f);
                this.mWidgetContainer.animate().alpha(1.0f).setDuration(50L).start();
            }
            this.mGroupWidgetPopupLayout.animate().setListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.widgettray.LGWidgetContainerView.10
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    LGWidgetContainerView.this.mGroupWidgetPopupLayout.setVisibility(8);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    LGWidgetContainerView.this.mGroupWidgetPopupLayout.setVisibility(8);
                    if (LGWidgetContainerView.this.isOpenGroupWidgetPopup()) {
                        LGWidgetContainerView.this.mGroupWidgetPopupLayout.setAlpha(0.0f);
                    } else if (LGWidgetContainerView.this.mWidgetContainer.getAlpha() != 1.0f) {
                        LGWidgetContainerView.this.mWidgetContainer.setAlpha(1.0f);
                    }
                }
            });
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x00aa  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void showGroupWidgetPopupView(boolean r19) {
        /*
            r18 = this;
            r12 = r18
            android.view.ViewGroup r0 = r12.mGroupWidgetPopupLayout
            if (r0 == 0) goto L261
            android.animation.AnimatorSet r0 = r12.mAnimatorSet
            r1 = 0
            if (r0 == 0) goto L10
            r0.cancel()
            r12.mAnimatorSet = r1
        L10:
            com.android.launcher3.LogDecelerateInterpolator r0 = new com.android.launcher3.LogDecelerateInterpolator
            r2 = 100
            r13 = 0
            r0.<init>(r2, r13)
            r2 = 2131296969(0x7f0902c9, float:1.821187E38)
            r3 = 3
            r4 = 300(0x12c, double:1.48E-321)
            r14 = 1
            r6 = 1073741824(0x40000000, float:2.0)
            r15 = 2
            if (r19 == 0) goto L153
            com.lge.launcher3.widgettray.LGWidgetCell r7 = r12.mSelectedWidgetCell
            if (r7 == 0) goto L153
            android.util.Property r1 = android.view.View.ALPHA
            float[] r7 = new float[r15]
            r7 = {x0262: FILL_ARRAY_DATA , data: [1065353216, 0} // fill-array
            android.animation.PropertyValuesHolder r1 = android.animation.PropertyValuesHolder.ofFloat(r1, r7)
            android.util.Property r7 = android.view.View.SCALE_X
            float[] r8 = new float[r15]
            r8 = {x026a: FILL_ARRAY_DATA , data: [1065353216, 1063675494} // fill-array
            android.animation.PropertyValuesHolder r7 = android.animation.PropertyValuesHolder.ofFloat(r7, r8)
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r9 = new float[r15]
            r9 = {x0272: FILL_ARRAY_DATA , data: [1065353216, 1063675494} // fill-array
            android.animation.PropertyValuesHolder r8 = android.animation.PropertyValuesHolder.ofFloat(r8, r9)
            android.view.ViewGroup r9 = r12.mWidgetContainer
            android.animation.PropertyValuesHolder[] r3 = new android.animation.PropertyValuesHolder[r3]
            r3[r13] = r1
            r3[r14] = r7
            r3[r15] = r8
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofPropertyValuesHolder(r9, r3)
            r11.setInterpolator(r0)
            r11.setDuration(r4)
            int[] r1 = new int[r15]
            com.lge.launcher3.widgettray.LGWidgetCell r3 = r12.mSelectedWidgetCell
            r3.getLocationOnScreen(r1)
            com.lge.launcher3.widgettray.LGWidgetCell r3 = r12.mSelectedWidgetCell
            int r3 = r3.getWidth()
            com.lge.launcher3.widgettray.LGWidgetCell r4 = r12.mSelectedWidgetCell
            int r4 = r4.getHeight()
            int[] r5 = new int[r15]
            android.view.ViewGroup r7 = r12.mGroupWidgetPopupLayout
            r7.getLocationOnScreen(r5)
            android.view.ViewGroup r7 = r12.mGroupWidgetPopupLayout
            int r10 = r7.getWidth()
            android.view.ViewGroup r7 = r12.mGroupWidgetPopupLayout
            int r9 = r7.getHeight()
            float r7 = (float) r10
            float r3 = (float) r3
            float r8 = r7 / r3
            float r4 = (float) r4
            float r16 = r4 * r8
            com.lge.launcher3.widgettray.LGWidgetCell r14 = r12.mSelectedWidgetCell
            java.lang.Object r14 = r14.getInfo()
            if (r14 == 0) goto Laa
            boolean r13 = r14 instanceof com.lge.launcher3.widgettray.GroupLauncherAppWidgetProviderInfo
            if (r13 == 0) goto L9d
            com.lge.launcher3.widgettray.GroupLauncherAppWidgetProviderInfo r14 = (com.lge.launcher3.widgettray.GroupLauncherAppWidgetProviderInfo) r14
            java.util.List r13 = r14.getGroupList()
            goto La3
        L9d:
            com.lge.launcher3.widgettray.GroupResolveInfo r14 = (com.lge.launcher3.widgettray.GroupResolveInfo) r14
            java.util.List r13 = r14.getGroupList()
        La3:
            if (r13 == 0) goto Laa
            int r13 = r13.size()
            goto Lab
        Laa:
            r13 = 0
        Lab:
            if (r13 > r15) goto Lc1
            float r13 = (float) r9
            com.lge.launcher3.widgettray.LGWidgetCell r14 = r12.mSelectedWidgetCell
            android.view.View r2 = r14.findViewById(r2)
            android.widget.LinearLayout r2 = (android.widget.LinearLayout) r2
            int r2 = r2.getHeight()
            float r2 = (float) r2
            float r2 = r2 / r6
            float r4 = r4 - r2
            r16 = r13
            r13 = 0
            goto Lcc
        Lc1:
            float r2 = (float) r9
            int r13 = (r16 > r2 ? 1 : (r16 == r2 ? 0 : -1))
            if (r13 >= 0) goto Lc8
            r2 = 1
            goto Lcb
        Lc8:
            r16 = r2
            r2 = 0
        Lcb:
            r13 = r2
        Lcc:
            float r14 = r16 / r4
            r2 = 0
            r15 = r1[r2]
            float r15 = (float) r15
            float r3 = r3 / r6
            float r15 = r15 + r3
            r3 = r5[r2]
            float r2 = (float) r3
            float r7 = r7 / r6
            float r2 = r2 + r7
            float r7 = r15 - r2
            r2 = 1
            r1 = r1[r2]
            float r1 = (float) r1
            float r3 = r4 / r6
            float r1 = r1 + r3
            r3 = r5[r2]
            float r2 = (float) r3
            float r3 = (float) r9
            float r3 = r3 / r6
            float r2 = r2 + r3
            float r15 = r1 - r2
            float r4 = r4 * r8
            float r4 = r4 - r16
            float r4 = r4 / r6
            float r5 = r15 - r4
            if (r13 == 0) goto L101
            android.view.ViewGroup r1 = r12.mGroupWidgetPopupLayout
            android.graphics.Rect r2 = new android.graphics.Rect
            int r3 = java.lang.Math.round(r16)
            r4 = 0
            r2.<init>(r4, r4, r10, r3)
            r1.setClipBounds(r2)
        L101:
            r1 = 2
            float[] r2 = new float[r1]
            r2 = {x027a: FILL_ARRAY_DATA , data: [0, 1065353216} // fill-array
            android.animation.ValueAnimator r6 = android.animation.ValueAnimator.ofFloat(r2)
            r6.setInterpolator(r0)
            r0 = 500(0x1f4, double:2.47E-321)
            r6.setDuration(r0)
            com.lge.launcher3.widgettray.LGWidgetContainerView$11 r4 = new com.lge.launcher3.widgettray.LGWidgetContainerView$11
            r0 = r4
            r1 = r18
            r2 = r8
            r3 = r8
            r8 = r4
            r4 = r7
            r7 = r6
            r6 = r14
            r14 = r7
            r7 = r13
            r13 = r8
            r8 = r16
            r16 = r10
            r10 = r15
            r15 = r11
            r11 = r16
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r14.addUpdateListener(r13)
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r12.mAnimatorSet = r0
            r1 = 2
            android.animation.Animator[] r1 = new android.animation.Animator[r1]
            r2 = 0
            r1[r2] = r15
            r2 = 1
            r1[r2] = r14
            r0.playTogether(r1)
            android.animation.AnimatorSet r0 = r12.mAnimatorSet
            com.lge.launcher3.widgettray.LGWidgetContainerView$12 r1 = new com.lge.launcher3.widgettray.LGWidgetContainerView$12
            r1.<init>()
            r0.addListener(r1)
            android.animation.AnimatorSet r0 = r12.mAnimatorSet
            r0.start()
            goto L261
        L153:
            android.util.Property r7 = android.view.View.ALPHA
            r8 = 2
            float[] r9 = new float[r8]
            r9 = {x0282: FILL_ARRAY_DATA , data: [0, 1065353216} // fill-array
            android.animation.PropertyValuesHolder r7 = android.animation.PropertyValuesHolder.ofFloat(r7, r9)
            android.util.Property r9 = android.view.View.SCALE_X
            float[] r10 = new float[r8]
            r10 = {x028a: FILL_ARRAY_DATA , data: [1063675494, 1065353216} // fill-array
            android.animation.PropertyValuesHolder r9 = android.animation.PropertyValuesHolder.ofFloat(r9, r10)
            android.util.Property r10 = android.view.View.SCALE_Y
            float[] r11 = new float[r8]
            r11 = {x0292: FILL_ARRAY_DATA , data: [1063675494, 1065353216} // fill-array
            android.animation.PropertyValuesHolder r10 = android.animation.PropertyValuesHolder.ofFloat(r10, r11)
            android.view.ViewGroup r11 = r12.mWidgetContainer
            android.animation.PropertyValuesHolder[] r3 = new android.animation.PropertyValuesHolder[r3]
            r13 = 0
            r3[r13] = r7
            r7 = 1
            r3[r7] = r9
            r3[r8] = r10
            android.animation.ObjectAnimator r13 = android.animation.ObjectAnimator.ofPropertyValuesHolder(r11, r3)
            r13.setInterpolator(r0)
            r13.setDuration(r4)
            com.lge.launcher3.widgettray.LGWidgetCell r3 = r12.mSelectedWidgetCell
            if (r3 == 0) goto L241
            int[] r1 = new int[r8]
            r3.getLocationOnScreen(r1)
            com.lge.launcher3.widgettray.LGWidgetCell r3 = r12.mSelectedWidgetCell
            int r3 = r3.getWidth()
            com.lge.launcher3.widgettray.LGWidgetCell r7 = r12.mSelectedWidgetCell
            int r7 = r7.getHeight()
            int[] r9 = new int[r8]
            android.view.ViewGroup r8 = r12.mGroupWidgetPopupLayout
            r8.getLocationOnScreen(r9)
            android.view.ViewGroup r8 = r12.mGroupWidgetPopupLayout
            int r11 = r8.getWidth()
            android.view.ViewGroup r8 = r12.mGroupWidgetPopupLayout
            int r8 = r8.getHeight()
            float r10 = (float) r11
            float r3 = (float) r3
            float r14 = r10 / r3
            float r7 = (float) r7
            float r15 = r7 * r14
            com.lge.launcher3.widgettray.LGWidgetCell r4 = r12.mSelectedWidgetCell
            java.lang.Object r4 = r4.getInfo()
            if (r4 == 0) goto L1d3
            boolean r5 = r4 instanceof com.lge.launcher3.widgettray.GroupLauncherAppWidgetProviderInfo
            if (r5 == 0) goto L1d3
            com.lge.launcher3.widgettray.GroupLauncherAppWidgetProviderInfo r4 = (com.lge.launcher3.widgettray.GroupLauncherAppWidgetProviderInfo) r4
            java.util.List r4 = r4.getGroupList()
            if (r4 == 0) goto L1d3
            int r4 = r4.size()
            goto L1d4
        L1d3:
            r4 = 0
        L1d4:
            r5 = 2
            if (r4 > r5) goto L1eb
            float r4 = (float) r8
            com.lge.launcher3.widgettray.LGWidgetCell r5 = r12.mSelectedWidgetCell
            android.view.View r2 = r5.findViewById(r2)
            android.widget.LinearLayout r2 = (android.widget.LinearLayout) r2
            int r2 = r2.getHeight()
            float r2 = (float) r2
            float r2 = r2 / r6
            float r7 = r7 - r2
            r16 = r4
            r15 = 0
            goto L1f7
        L1eb:
            float r2 = (float) r8
            int r4 = (r15 > r2 ? 1 : (r15 == r2 ? 0 : -1))
            if (r4 >= 0) goto L1f2
            r2 = 1
            goto L1f4
        L1f2:
            r15 = r2
            r2 = 0
        L1f4:
            r16 = r15
            r15 = r2
        L1f7:
            float r17 = r16 / r7
            r2 = 0
            r4 = r1[r2]
            float r4 = (float) r4
            float r3 = r3 / r6
            float r4 = r4 + r3
            r2 = r9[r2]
            float r2 = (float) r2
            float r10 = r10 / r6
            float r2 = r2 + r10
            float r4 = r4 - r2
            r2 = 1
            r1 = r1[r2]
            float r1 = (float) r1
            float r3 = r7 / r6
            float r1 = r1 + r3
            r2 = r9[r2]
            float r2 = (float) r2
            float r3 = (float) r8
            float r3 = r3 / r6
            float r2 = r2 + r3
            float r10 = r1 - r2
            float r7 = r7 * r14
            float r7 = r7 - r16
            float r7 = r7 / r6
            float r5 = r10 - r7
            r1 = 2
            float[] r1 = new float[r1]
            r1 = {x029a: FILL_ARRAY_DATA , data: [1065353216, 0} // fill-array
            android.animation.ValueAnimator r9 = android.animation.ValueAnimator.ofFloat(r1)
            r9.setInterpolator(r0)
            r0 = 300(0x12c, double:1.48E-321)
            r9.setDuration(r0)
            com.lge.launcher3.widgettray.LGWidgetContainerView$13 r7 = new com.lge.launcher3.widgettray.LGWidgetContainerView$13
            r0 = r7
            r1 = r18
            r2 = r14
            r3 = r14
            r6 = r17
            r14 = r7
            r7 = r15
            r15 = r9
            r9 = r16
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r15.addUpdateListener(r14)
            r1 = r15
        L241:
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r12.mAnimatorSet = r0
            if (r1 == 0) goto L24d
            r0.play(r1)
        L24d:
            android.animation.AnimatorSet r0 = r12.mAnimatorSet
            r0.play(r13)
            android.animation.AnimatorSet r0 = r12.mAnimatorSet
            com.lge.launcher3.widgettray.LGWidgetContainerView$14 r1 = new com.lge.launcher3.widgettray.LGWidgetContainerView$14
            r1.<init>()
            r0.addListener(r1)
            android.animation.AnimatorSet r0 = r12.mAnimatorSet
            r0.start()
        L261:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.widgettray.LGWidgetContainerView.showGroupWidgetPopupView(boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetWidgetContainer() {
        ViewGroup viewGroup = this.mWidgetContainer;
        if (viewGroup != null) {
            viewGroup.setScaleX(1.0f);
            this.mWidgetContainer.setScaleY(1.0f);
            this.mWidgetContainer.setAlpha(1.0f);
        }
        LGWidgetCell lGWidgetCell = this.mSelectedWidgetCell;
        if (lGWidgetCell != null) {
            lGWidgetCell.setScaleX(1.0f);
            this.mSelectedWidgetCell.setScaleY(1.0f);
            this.mSelectedWidgetCell.setTranslationX(0.0f);
            this.mSelectedWidgetCell.setTranslationY(0.0f);
            this.mSelectedWidgetCell.setAlpha(1.0f);
        }
        ViewGroup viewGroup2 = this.mGroupWidgetPopupLayout;
        if (viewGroup2 != null) {
            viewGroup2.setScaleX(1.0f);
            this.mGroupWidgetPopupLayout.setScaleY(1.0f);
            this.mGroupWidgetPopupLayout.setTranslationX(0.0f);
            this.mGroupWidgetPopupLayout.setTranslationY(0.0f);
            this.mGroupWidgetPopupLayout.setAlpha(1.0f);
        }
    }

    private void updateWidgetPopupView(Object widgetInfo) {
        List<Object> groupList;
        String applicationLabel;
        if (widgetInfo instanceof GroupLauncherAppWidgetProviderInfo) {
            GroupLauncherAppWidgetProviderInfo groupLauncherAppWidgetProviderInfo = (GroupLauncherAppWidgetProviderInfo) widgetInfo;
            groupList = groupLauncherAppWidgetProviderInfo.getGroupList();
            applicationLabel = PackageUtils.getApplicationLabel(this.mContext, groupLauncherAppWidgetProviderInfo.provider.getPackageName());
        } else {
            GroupResolveInfo groupResolveInfo = (GroupResolveInfo) widgetInfo;
            groupList = groupResolveInfo.getGroupList();
            applicationLabel = PackageUtils.getApplicationLabel(this.mContext, groupResolveInfo.activityInfo.packageName);
        }
        this.mGroupWidgetPopupName.setText(applicationLabel);
        this.mGroupWidgetItemAdapter.setItemList(groupList);
        this.mGroupWidgetItemAdapter.notifyDataSetChanged();
    }

    private void createWidgetPopupView(Object widgetInfo) {
        List<Object> groupList;
        String applicationLabel;
        TextView textView = (TextView) this.mGroupWidgetPopupLayout.findViewById(R.id.widget_name);
        GridView gridView = (GridView) this.mGroupWidgetPopupLayout.findViewById(R.id.group_widget_grid);
        if (widgetInfo instanceof GroupLauncherAppWidgetProviderInfo) {
            GroupLauncherAppWidgetProviderInfo groupLauncherAppWidgetProviderInfo = (GroupLauncherAppWidgetProviderInfo) widgetInfo;
            groupList = groupLauncherAppWidgetProviderInfo.getGroupList();
            applicationLabel = PackageUtils.getApplicationLabel(this.mContext, groupLauncherAppWidgetProviderInfo.provider.getPackageName());
        } else {
            GroupResolveInfo groupResolveInfo = (GroupResolveInfo) widgetInfo;
            groupList = groupResolveInfo.getGroupList();
            applicationLabel = PackageUtils.getApplicationLabel(this.mContext, groupResolveInfo.activityInfo.packageName);
        }
        List<Object> list = groupList;
        boolean z = false;
        if (this.mLauncher != null && this.mLauncher.getDeviceProfile() != null) {
            z = this.mLauncher.getDeviceProfile().isTablet;
        }
        textView.setText(applicationLabel);
        GroupWidgetItemAdapter groupWidgetItemAdapter = new GroupWidgetItemAdapter(this.mLauncher, list, this, this, z);
        this.mGroupWidgetItemAdapter = groupWidgetItemAdapter;
        gridView.setAdapter((ListAdapter) groupWidgetItemAdapter);
    }

    @Override // com.android.launcher3.widget.WidgetsContainerView, android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        if (this.mLauncher.isInMultiWindowMode()) {
            Toast.makeText(getContext(), getResources().getString(R.string.home_screen_lock_in_multiwindow), 0).show();
            return true;
        }
        if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(getContext())) {
            Toast.makeText(getContext(), HomeSettingsSharedPreferences.getHomeLockDisableGuideText(getContext()), 0).show();
            return true;
        }
        if (!this.mUninstallBtn.isUninstallMode()) {
            return super.onLongClick(v);
        }
        showGroupWidgetPopupView(false);
        return false;
    }

    @Override // com.android.launcher3.widget.WidgetsContainerView
    public View getContentView() {
        return findViewById(R.id.widgets_view);
    }

    public void resetMode() {
        this.mUninstallBtn.setUninstallMode(false);
    }

    @Override // com.android.launcher3.widget.WidgetsContainerView, com.android.launcher3.DragSource
    public void onDropCompleted(View target, DropTarget.DragObject d, boolean isFlingToDelete, boolean success) {
        if (isFlingToDelete || !success || (target != this.mLauncher.getWorkspace() && !(target instanceof DeleteDropTarget) && !(target instanceof Folder))) {
            this.mLauncher.exitSpringLoadedDragModeDelayed(true, 300, null);
            if (!success) {
                d.deferDragViewCleanupPostAnimation = false;
            }
        }
        this.mLauncher.unlockScreenOrientation(false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchVisibilityChanged(View changedView, int visibility) {
        LGLog.d(TAG, "dispatchVisibilityChanged - " + visibility);
        if (visibility != 0) {
            setSystemGestureExclusionRects(Collections.singletonList(new Rect()));
        }
        super.dispatchVisibilityChanged(changedView, visibility);
    }

    @Override // android.view.View
    public void bringToFront() {
        AllAppsSearch allAppsSearch;
        announceAccessibility(this.mViewPager.getCurrentItem());
        if (LGHomeFeature.Config.FEATURE_USE_WIDGET_SEARCH.getValue() && (allAppsSearch = this.mMenuSearch) != null) {
            allAppsSearch.hideAppsSearchBar();
        }
        this.mViewPagerAdapter.notifyDataSetChanged();
        showGroupWidgetPopupView(false);
        this.mLauncher.getRootView().setDisallowBackGesture(false);
        super.bringToFront();
    }

    public boolean closeGroupWidgetPopup() {
        if (!isOpenGroupWidgetPopup()) {
            return false;
        }
        showGroupWidgetPopupView(false);
        return true;
    }

    public boolean isOpenGroupWidgetPopup() {
        ViewGroup viewGroup = this.mGroupWidgetPopupLayout;
        return viewGroup != null && viewGroup.getVisibility() == 0;
    }

    public void clear() {
        ViewPager viewPager;
        WidgetsViewPagerAdapter widgetsViewPagerAdapter = this.mViewPagerAdapter;
        if (widgetsViewPagerAdapter == null || (viewPager = this.mViewPager) == null) {
            return;
        }
        widgetsViewPagerAdapter.clear(viewPager);
    }

    public void setFlagForRefreshPreView(boolean needRefresh) {
        WidgetsViewPagerAdapter widgetsViewPagerAdapter = this.mViewPagerAdapter;
        if (widgetsViewPagerAdapter != null) {
            widgetsViewPagerAdapter.mNeedRefresh = needRefresh;
        }
    }

    public void notifyDataSetChanged() {
        WidgetsViewPagerAdapter widgetsViewPagerAdapter = this.mViewPagerAdapter;
        if (widgetsViewPagerAdapter != null) {
            widgetsViewPagerAdapter.notifyDataSetChanged();
        }
        GroupWidgetItemAdapter groupWidgetItemAdapter = this.mGroupWidgetItemAdapter;
        if (groupWidgetItemAdapter != null) {
            groupWidgetItemAdapter.notifyDataSetChanged();
        }
    }

    @Override // com.lge.launcher3.allapps.IAllAppsSearchListener
    public void onSearch(CharSequence key) {
        if (!LGHomeFeature.Config.FEATURE_USE_WIDGET_SEARCH.getValue() || key == null) {
            return;
        }
        searchWidgets(key.toString().toLowerCase(Locale.getDefault()));
    }

    private void searchWidgets(String key) {
        WidgetsViewPagerAdapter widgetsViewPagerAdapter;
        WidgetsViewPagerAdapter widgetsViewPagerAdapter2;
        if (this.mSearchWidgetsAsyncTask == null && key.equals("")) {
            ViewPager viewPager = this.mViewPager;
            if (viewPager != null && (widgetsViewPagerAdapter2 = this.mViewPagerAdapter) != null) {
                viewPager.setCurrentItem(widgetsViewPagerAdapter2.getFirstPageIndex(), false);
            }
            showNoSearchResult(false);
        }
        SearchWidgetsAsyncTask searchWidgetsAsyncTask = this.mSearchWidgetsAsyncTask;
        if (searchWidgetsAsyncTask != null) {
            if (searchWidgetsAsyncTask.getSearchWord().equals(key)) {
                boolean z = this.mViewPagerAdapter.getCount() > 0;
                if (key.equals("")) {
                    z = true;
                }
                ViewPager viewPager2 = this.mViewPager;
                if (viewPager2 != null && (widgetsViewPagerAdapter = this.mViewPagerAdapter) != null) {
                    viewPager2.setCurrentItem(widgetsViewPagerAdapter.getFirstPageIndex(), false);
                }
                showNoSearchResult(!z);
                return;
            }
            if (this.mSearchWidgetsAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
                this.mSearchWidgetsAsyncTask.cancel(true);
            }
        }
        SearchWidgetsAsyncTask searchWidgetsAsyncTask2 = new SearchWidgetsAsyncTask(this.mContext, this.mLauncher.getModel().getBgWidgetsModel().getRawList(), this);
        this.mSearchWidgetsAsyncTask = searchWidgetsAsyncTask2;
        searchWidgetsAsyncTask2.setSearchWord(key);
        this.mSearchWidgetsAsyncTask.execute(key);
    }

    private void setVisibilityBySearchView(int visibility) {
        TextView textView = this.mWidgetTitle;
        if (textView != null) {
            textView.setVisibility(visibility);
        }
        ViewGroup viewGroup = this.mSearch_edit;
        if (viewGroup != null) {
            viewGroup.setVisibility(visibility);
        }
    }

    @Override // com.lge.launcher3.allapps.IAllAppsSearchListener
    public void prepareSearchViewHide() {
        setVisibilityBySearchView(0);
    }

    @Override // com.lge.launcher3.allapps.IAllAppsSearchListener
    public void prepareSearchViewShow() {
        setVisibilityBySearchView(4);
    }

    private void showNoSearchResult(boolean visible) {
        View view = this.mNoSearchResult;
        if (view == null) {
            return;
        }
        if (visible) {
            view.setVisibility(0);
        } else {
            view.setVisibility(8);
        }
        invalidate();
    }

    @Override // com.lge.launcher3.widgettray.SearchWidgetsAsyncTask.ISearchTaskPostExcute
    public void postExcute(ArrayList<LGSearchedWidgetsInfo> searchedWidgets, Boolean keyValue) {
        WidgetsViewPagerAdapter widgetsViewPagerAdapter;
        this.mSearchState = keyValue.booleanValue();
        WidgetsViewPagerAdapter widgetsViewPagerAdapter2 = this.mViewPagerAdapter;
        if (widgetsViewPagerAdapter2 != null) {
            widgetsViewPagerAdapter2.setSearchWidget(searchedWidgets);
        }
        invalidatePageData();
        ViewPager viewPager = this.mViewPager;
        if (viewPager != null && (widgetsViewPagerAdapter = this.mViewPagerAdapter) != null) {
            viewPager.setCurrentItem(widgetsViewPagerAdapter.getFirstPageIndex(), false);
        }
        showNoSearchResult(!(!this.mSearchState || searchedWidgets.size() >= 1));
    }

    private void invalidatePageData() {
        clear();
        setFlagForRefreshPreView(true);
        refreshPageIndicator();
        notifyDataSetChanged();
    }

    public void cancelSearchWidgetsAsyncTask() {
        if (LGHomeFeature.Config.FEATURE_USE_WIDGET_SEARCH.getValue()) {
            this.mSearchState = false;
            SearchWidgetsAsyncTask searchWidgetsAsyncTask = this.mSearchWidgetsAsyncTask;
            if (searchWidgetsAsyncTask != null) {
                searchWidgetsAsyncTask.cancel(true);
            }
        }
    }
}
