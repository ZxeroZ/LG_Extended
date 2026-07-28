package com.lge.launcher3.allapps;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.FocusIndicatorView;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherState;
import com.android.launcher3.LauncherTransitionable;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Workspace;
import com.android.launcher3.allapps.PersonalWorkSlidingTabStrip;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.views.WorkEduView;
import com.lge.launcher3.R;
import com.lge.launcher3.hideapps.HideAppsSettingActivity;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.uioverrides.InAppsState;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.util.WindowUtils;
import java.util.ArrayList;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsHost extends FrameLayout implements LauncherTransitionable, IAllAppsHostListener, IAllAppsSearchListener, IAllAppsFolderListener, Insettable {
    private static final int ALPHA_CHANNEL_COUNT = 2;
    private static final FloatPropertyCompat<AllAppsHost> DAMPED_SCROLL = new FloatPropertyCompat<AllAppsHost>("value") { // from class: com.lge.launcher3.allapps.AllAppsHost.1
        /* JADX DEBUG: Method merged with bridge method: getValue(Ljava/lang/Object;)F */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(AllAppsHost object) {
            return object.mDampedScrollShift;
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(AllAppsHost object, float value) {
            object.setDampedScrollShift(value);
        }
    };
    private static final float DAMPING_RATIO = 0.5f;
    private static final int DUAL_APP_PROFILE = 97;
    private static final float FLING_ANIMATION_THRESHOLD = 0.55f;
    private static final float FLING_VELOCITY_MULTIPLIER = 135.0f;
    static final String LOG_TAG = "AllAppsHost";
    private static final float STIFFNESS = 850.0f;
    private AccessibilityManager mAccessibilityManager;
    private int mActionBarHeight;
    private Activity mActivity;
    private Runnable mBindAllAppsRunnable;
    private float mDampedScrollShift;
    private Button mFabWorkProfileSwitch;
    protected FocusIndicatorView mFocusIndicatorView;
    private ViewGroup mHeadContainer;
    private ViewGroup mHeadContainerLand;
    private boolean mInTransition;
    private boolean mIsWorkTab;
    private AllAppsPagedView mLGAllAppsPagedView;
    private Launcher mLauncher;
    private FrameLayout mMenuContent;
    private MenuItem mMenuLayout;
    private int mMenuLayoutOrderIdx;
    private AllAppsSearch mMenuSearch;
    private final MultiValueAlpha mMultiValueAlpha;
    private View mNoSearchResult;
    private AllAppsOptionsMenu mOptionsMenu;
    private ImageButton mOptionsMenuButton;
    private ImageButton mOptionsMenuButtonLand;
    protected PersonalWorkSlidingTabStrip mPersonalWorkTabStrip;
    private ImageButton mSearchButton;
    private ImageButton mSearchButtonLand;
    private ViewGroup mSearch_edit;
    private final SpringAnimation mSpring;
    private final SparseBooleanArray mSpringViews;
    private ViewGroup mTopMenuLayout;
    private LinearLayout mWorkOffContentLayout;
    private StateManager.StateListener<LauncherState> mWorkTabListener;
    public View.OnClickListener onClickListener;

    public View getSearchBarView() {
        return null;
    }

    @Override // com.android.launcher3.LauncherTransitionable
    public void onLauncherTransitionStep(Launcher l, float t) {
    }

    public void startAppsSearch() {
    }

    public AllAppsPagedView getLGAllAppsPagedView() {
        return this.mLGAllAppsPagedView;
    }

    public AllAppsHost(Context context) {
        this(context, null);
    }

    public AllAppsHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mActivity = null;
        this.mTopMenuLayout = null;
        this.mHeadContainer = null;
        this.mHeadContainerLand = null;
        this.mSearch_edit = null;
        this.mOptionsMenuButton = null;
        this.mSearchButton = null;
        this.mOptionsMenuButtonLand = null;
        this.mSearchButtonLand = null;
        this.mFabWorkProfileSwitch = null;
        this.mWorkOffContentLayout = null;
        this.mMenuSearch = null;
        this.mMenuContent = null;
        this.mNoSearchResult = null;
        this.mMenuLayout = null;
        this.mMenuLayoutOrderIdx = 0;
        this.mOptionsMenu = null;
        this.mAccessibilityManager = null;
        this.mDampedScrollShift = 0.0f;
        this.mSpringViews = new SparseBooleanArray();
        this.onClickListener = new View.OnClickListener() { // from class: com.lge.launcher3.allapps.AllAppsHost.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int id = v.getId();
                if (id != R.id.fab_work_profile_switch) {
                    switch (id) {
                        case R.id.lg_page_menu_search_btn /* 2131296629 */:
                            if (!AllAppsHost.this.isInArrangeMode() && AllAppsHost.this.getTranslationY() == 0.0f) {
                                AllAppsHost.this.mMenuSearch.showAppsSearchBar("");
                                AllAppsHost.this.onSearch("");
                                if (ManagedProfileUtils.isAFW(AllAppsHost.this.getContext())) {
                                    AllAppsHost.this.mPersonalWorkTabStrip.setVisibility(4);
                                }
                                break;
                            }
                            break;
                        case R.id.lg_page_menu_search_btn_land /* 2131296630 */:
                            if (Utilities.checkActionAvailable(AllAppsHost.this.getContext(), "com.lge.abba.action.INTEGRATED_SEARCH")) {
                                InAppsState.enterABBASearch(AllAppsHost.this.mLauncher, null);
                            }
                            break;
                        case R.id.lg_pagemenu_optionsmenu_button /* 2131296631 */:
                        case R.id.lg_pagemenu_optionsmenu_button_land /* 2131296632 */:
                            if (AllAppsHost.this.getTranslationY() == 0.0f && AllAppsHost.this.mMenuSearch.getVisibility() != 0) {
                                if (AllAppsHost.this.isInArrangeMode()) {
                                    AllAppsHost.this.setArrangeMode(false, true);
                                } else {
                                    AllAppsHost.this.showOptionsMenu();
                                }
                            }
                            break;
                    }
                    return;
                }
                AllAppsHost allAppsHost = AllAppsHost.this;
                allAppsHost.setWorkProfileEnabled(allAppsHost.isWokProfileQuiet());
            }
        };
        this.mIsWorkTab = false;
        this.mLauncher = (Launcher) context;
        setBackground(null);
        Resources resources = getResources();
        if (resources != null) {
            this.mActionBarHeight = resources.getDimensionPixelSize(R.dimen.hig_actionbar_height);
        } else {
            this.mActionBarHeight = 0;
        }
        this.mMultiValueAlpha = new MultiValueAlpha(this, 2);
        addSpringView(R.id.topmenu_layout);
        SpringAnimation springAnimation = new SpringAnimation(this, DAMPED_SCROLL, 0.0f);
        this.mSpring = springAnimation;
        if (LGHomeFeature.Config.FEATURE_USE_NEW_ALLAPPS_ANIMATION.getValue()) {
            springAnimation.setSpring(new SpringForce(0.0f).setStiffness(10000.0f).setDampingRatio(1.0f));
        } else {
            springAnimation.setSpring(new SpringForce(0.0f).setStiffness(STIFFNESS).setDampingRatio(0.5f));
        }
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
    }

    public View getContentView() {
        return findViewById(R.id.topmenu_layout);
    }

    public View getAppContainerView() {
        return findViewById(R.id.apps_container);
    }

    public View getSearchView() {
        return findViewById(R.id.head_container);
    }

    public View getRevealView() {
        return findViewById(R.id.allapps_reveal_view);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        AllAppsSearch allAppsSearch = (AllAppsSearch) findViewById(R.id.all_apps_search_bar);
        this.mMenuSearch = allAppsSearch;
        allAppsSearch.setOnSearchListener(this);
        this.mTopMenuLayout = (ViewGroup) findViewById(R.id.topmenu_layout);
        this.mHeadContainer = (ViewGroup) findViewById(R.id.head_container);
        this.mHeadContainerLand = (ViewGroup) findViewById(R.id.head_container_land);
        this.mSearch_edit = (ViewGroup) findViewById(R.id.search_edit);
        this.mMenuContent = (FrameLayout) findViewById(R.id.apps_container);
        this.mFocusIndicatorView = (FocusIndicatorView) findViewById(R.id.focus_indicator);
        AllAppsPagedView allAppsPagedView = (AllAppsPagedView) findViewById(R.id.lg_allapps_pagedview);
        this.mLGAllAppsPagedView = allAppsPagedView;
        allAppsPagedView.setHostListener(this);
        this.mLGAllAppsPagedView.setFocusIndicatorView(this.mFocusIndicatorView);
        if (this.mLGAllAppsPagedView == null || this.mMenuContent == null) {
            throw new Resources.NotFoundException();
        }
        this.mOptionsMenuButton = (ImageButton) findViewById(R.id.lg_pagemenu_optionsmenu_button);
        this.mOptionsMenuButtonLand = (ImageButton) findViewById(R.id.lg_pagemenu_optionsmenu_button_land);
        this.mSearchButton = (ImageButton) findViewById(R.id.lg_page_menu_search_btn);
        this.mSearchButtonLand = (ImageButton) findViewById(R.id.lg_page_menu_search_btn_land);
        AllAppsCustomizeTabKeyEventListener allAppsCustomizeTabKeyEventListener = new AllAppsCustomizeTabKeyEventListener();
        this.mSearchButton.setOnKeyListener(allAppsCustomizeTabKeyEventListener);
        this.mSearchButtonLand.setOnKeyListener(allAppsCustomizeTabKeyEventListener);
        updateButtons();
        setExtendedButtonListener();
        this.mOptionsMenuButton.setEnabled(true);
        this.mOptionsMenuButton.setClickable(true);
        this.mOptionsMenuButton.setOnKeyListener(allAppsCustomizeTabKeyEventListener);
        this.mOptionsMenuButtonLand.setEnabled(true);
        this.mOptionsMenuButtonLand.setClickable(true);
        this.mOptionsMenuButtonLand.setOnKeyListener(allAppsCustomizeTabKeyEventListener);
        this.mLGAllAppsPagedView.setVisibility(8);
        View viewFindViewById = findViewById(R.id.no_search_result);
        this.mNoSearchResult = viewFindViewById;
        viewFindViewById.setVisibility(8);
        AllAppsItemFactory.getInstance().setFolderListener(this);
        this.mPersonalWorkTabStrip = (PersonalWorkSlidingTabStrip) findViewById(R.id.tabs);
        if (ManagedProfileUtils.isAFW(getContext())) {
            this.mPersonalWorkTabStrip.setVisibility(0);
            this.mPersonalWorkTabStrip.setAllAppsHost(this);
        }
        findViewById(R.id.tab_personal).setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsHost$6YFxGmC0q9HGdGWff33RH1Z09Ok
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onFinishInflate$0$AllAppsHost(view);
            }
        });
        findViewById(R.id.tab_work).setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsHost$y-7hPypOVFlAYqZ6KmOPbTwWKrs
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$onFinishInflate$1$AllAppsHost(view);
            }
        });
        this.mWorkOffContentLayout = (LinearLayout) findViewById(R.id.work_off_content_layout);
        setDeviceManagementResources();
        setFabWorkProfileSwitch();
    }

    public /* synthetic */ void lambda$onFinishInflate$0$AllAppsHost(View view) {
        this.mLGAllAppsPagedView.snapToPage(0);
    }

    public /* synthetic */ void lambda$onFinishInflate$1$AllAppsHost(View view) {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        allAppsPagedView.snapToPage(allAppsPagedView.getManagedProfileStartPage());
    }

    private void setFabWorkProfileSwitch() {
        Button button = (Button) findViewById(R.id.fab_work_profile_switch);
        this.mFabWorkProfileSwitch = button;
        button.setOnClickListener(this.onClickListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isWokProfileQuiet() {
        UserManagerCompat userManagerCompat = UserManagerCompat.getInstance(this.mLauncher);
        for (UserHandle userHandle : userManagerCompat.getUserProfiles()) {
            if (userHandle.getIdentifier() != 0) {
                return userManagerCompat.isQuietModeEnabled(userHandle);
            }
        }
        return false;
    }

    private void setDeviceManagementResources() {
        if (this.mLauncher.getStringCache() != null) {
            ((Button) findViewById(R.id.tab_personal)).setText(this.mLauncher.getStringCache().allAppsPersonalTab);
            ((Button) findViewById(R.id.tab_work)).setText(this.mLauncher.getStringCache().allAppsWorkTab);
            ((TextView) findViewById(R.id.work_off_content_title)).setText(this.mLauncher.getStringCache().workProfilePausedTitle);
            ((TextView) findViewById(R.id.work_off_content_body)).setText(this.mLauncher.getStringCache().workProfilePausedDescription);
        }
    }

    public void setExtendedButtonListener() {
        this.mOptionsMenuButton.setOnClickListener(this.onClickListener);
        this.mOptionsMenuButtonLand.setOnClickListener(this.onClickListener);
        this.mSearchButton.setOnClickListener(this.onClickListener);
        this.mSearchButtonLand.setOnClickListener(this.onClickListener);
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() { // from class: com.lge.launcher3.allapps.AllAppsHost.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                AllAppsHost.this.showTitleButtonGuideToast(v.getContentDescription());
                return true;
            }
        };
        this.mSearchButton.setOnLongClickListener(onLongClickListener);
        this.mSearchButtonLand.setOnLongClickListener(onLongClickListener);
    }

    public void layout() {
        ViewGroup.LayoutParams layoutParams = this.mSearchButton.getLayoutParams();
        ViewGroup.LayoutParams layoutParams2 = this.mOptionsMenuButton.getLayoutParams();
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            this.mSearch_edit.setPaddingRelative(0, 0, getResources().getDimensionPixelSize(R.dimen.all_apps_more_right_padding_swivel_land), 0);
            layoutParams.width = getResources().getDimensionPixelSize(R.dimen.all_apps_search_button_height_swivel_land);
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.all_apps_search_button_height_swivel_land);
            layoutParams2.height = getResources().getDimensionPixelSize(R.dimen.all_apps_search_button_height_swivel_land);
        } else {
            this.mSearch_edit.setPaddingRelative(0, 0, getResources().getDimensionPixelSize(R.dimen.all_apps_more_right_padding), 0);
            layoutParams.width = getResources().getDimensionPixelSize(R.dimen.hig_actionbar_height);
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.hig_actionbar_height);
            layoutParams2.height = getResources().getDimensionPixelSize(R.dimen.hig_actionbar_height);
        }
        this.mSearchButton.setLayoutParams(layoutParams);
        this.mOptionsMenuButton.setLayoutParams(layoutParams2);
        this.mHeadContainer.setVisibility(0);
        this.mHeadContainerLand.setVisibility(8);
        this.mOptionsMenu = new AllAppsOptionsMenu(this.mContext, this.mOptionsMenuButton, new OptionsMenuClickListener());
        if (ManagedProfileUtils.isAFW(getContext())) {
            this.mOptionsMenuButton.setVisibility(8);
        } else {
            this.mOptionsMenuButton.setVisibility(0);
        }
    }

    public void setChildVisible() {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.setVisibility(0);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mLGAllAppsPagedView == null || event.getY() >= getBottom()) {
            return super.onTouchEvent(event);
        }
        return true;
    }

    @Override // android.view.ViewGroup
    public int getDescendantFocusability() {
        if (getVisibility() != 0) {
            return 393216;
        }
        return super.getDescendantFocusability();
    }

    public void reset(Bundle savedState) {
        AllAppsPagedView allAppsPagedView;
        if (!this.mInTransition && (allAppsPagedView = this.mLGAllAppsPagedView) != null) {
            allAppsPagedView.setIsPageMoving(false);
            setArrangeMode(false, true);
        }
        if (savedState == null || !savedState.getBoolean("apps_customize_editmde")) {
            return;
        }
        savedState.putBoolean("apps_customize_editmde", false);
    }

    public boolean isInTransition() {
        return this.mInTransition;
    }

    @Override // com.lge.launcher3.allapps.IAllAppsHostListener
    public boolean setArrangeMode(boolean enableMode, boolean useAnimation) {
        Activity activity = this.mActivity;
        if (activity != null) {
            activity.closeOptionsMenu();
        }
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView == null) {
            return false;
        }
        if (enableMode) {
            allAppsPagedView.startArrangeMode(useAnimation);
        } else {
            allAppsPagedView.endArrangeMode(useAnimation);
        }
        changeEditButtonState();
        closeFolder(true, true);
        return true;
    }

    private void updateButtons() {
        if (isInArrangeMode()) {
            this.mOptionsMenuButton.setImageResource(R.drawable.btn_homescreen_app_tray_done_normal);
            this.mOptionsMenuButton.setFocusable(true);
            this.mOptionsMenuButton.setContentDescription(getContext().getResources().getString(R.string.sp_menu_done_lable_NORMAL));
            this.mSearchButton.setVisibility(4);
            return;
        }
        this.mOptionsMenuButton.setImageResource(R.drawable.ic_t_more_tint);
        this.mOptionsMenuButton.setFocusable(true);
        this.mOptionsMenuButton.setContentDescription(getContext().getResources().getString(R.string.str_moreoptions));
        this.mSearchButton.setVisibility(0);
    }

    @Override // com.lge.launcher3.allapps.IAllAppsHostListener
    public void changeEditButtonState() {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            this.mSearchButton.setEnabled(allAppsPagedView.getAllAppsCount() > 0);
            this.mSearchButton.setFocusable(this.mLGAllAppsPagedView.getAllAppsCount() > 0);
        } else {
            this.mSearchButton.setEnabled(false);
            this.mSearchButton.setFocusable(false);
        }
        updateButtons();
    }

    public boolean onBackPressed() {
        if (this.mOptionsMenu.isShowing()) {
            this.mOptionsMenu.dismiss();
            return true;
        }
        if (this.mMenuSearch.onBackPressed()) {
            return true;
        }
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView == null || !allAppsPagedView.onBackPressed()) {
            return false;
        }
        updateButtons();
        return true;
    }

    private void callSelectSortAppsBy() {
        AllAppsSortDialog.showSelectSortTypeDialog(getContext(), this.mLGAllAppsPagedView);
    }

    public void showOptionsMenu() {
        AllAppsOptionsMenu allAppsOptionsMenu;
        if (isInArrangeMode() || (allAppsOptionsMenu = this.mOptionsMenu) == null) {
            return;
        }
        allAppsOptionsMenu.clear();
        onCreateOptionsMenu(this.mOptionsMenu.getMenu());
        onPrepareOptionsMenu(this.mOptionsMenu.getMenu());
        this.mOptionsMenu.show();
    }

    public void clearOptionMenu() {
        AllAppsOptionsMenu allAppsOptionsMenu = this.mOptionsMenu;
        if (allAppsOptionsMenu != null) {
            allAppsOptionsMenu.dismiss();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        new Intent("android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS").setFlags(276824064);
        this.mMenuLayoutOrderIdx = 0;
        menu.add(0, 2, 0, R.string.allapps_sortappsby_title).setIcon(android.R.drawable.ic_menu_manage).setAlphabeticShortcut('O');
        int i = this.mMenuLayoutOrderIdx + 1;
        this.mMenuLayoutOrderIdx = i;
        menu.add(0, 3, i, R.string.hide_apps_setting_title).setIcon(android.R.drawable.ic_menu_manage);
        int i2 = this.mMenuLayoutOrderIdx + 1;
        this.mMenuLayoutOrderIdx = i2;
        menu.add(0, 4, i2, R.string.arrange_apps).setIcon(android.R.drawable.ic_menu_manage);
        this.mMenuLayoutOrderIdx++;
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.mMenuLayoutOrderIdx == 0) {
            onCreateOptionsMenu(menu);
        }
        menu.setGroupVisible(0, false);
        if (getVisibility() != 0) {
            return true;
        }
        if (this.mMenuSearch.getVisibility() == 0) {
            return false;
        }
        menu.setGroupVisible(0, false);
        if (this.mLGAllAppsPagedView == null || isInArrangeMode()) {
            return false;
        }
        MenuItem menuItem = this.mMenuLayout;
        if (menuItem != null) {
            menu.removeItem(menuItem.getItemId());
        }
        menu.setGroupVisible(0, true);
        if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(getContext()) || this.mLauncher.isInMultiWindowMode() || ManagedProfileUtils.isAFW(getContext())) {
            menu.setGroupEnabled(0, false);
            return true;
        }
        boolean z = this.mLGAllAppsPagedView.getAllAppsCount() > 0;
        MenuItem menuItemFindItem = menu.findItem(2);
        if (menuItemFindItem != null) {
            menuItemFindItem.setEnabled(z);
        }
        MenuItem menuItemFindItem2 = menu.findItem(4);
        if (menuItemFindItem2 != null) {
            menuItemFindItem2.setEnabled(z);
        }
        return true;
    }

    @Override // com.android.launcher3.Insettable
    public void setInsets(Rect newInsets) {
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            newInsets.bottom = getResources().getDimensionPixelSize(R.dimen.all_apps_pageview_padding_bottom_swivel_land);
        }
        setPadding(newInsets.left, newInsets.top, newInsets.right, newInsets.bottom);
    }

    @Override // com.lge.launcher3.allapps.IAllAppsFolderListener
    public void addItemsInAllApps(ArrayList<ShortcutInfo> shortcutInfos) {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.addItemsInAllApps(shortcutInfos);
        }
    }

    @Override // com.lge.launcher3.allapps.IAllAppsFolderListener
    public void bindAppsMoved(ArrayList<ShortcutInfo> items, FolderInfo target) {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.removeItemsByList(items, target);
        }
    }

    @Override // com.lge.launcher3.allapps.IAllAppsFolderListener
    public void removeVacantPage() {
        if (this.mLGAllAppsPagedView == null || isInArrangeMode()) {
            return;
        }
        this.mLGAllAppsPagedView.removeVacantPage();
    }

    private class OptionsMenuClickListener implements PopupMenu.OnMenuItemClickListener {
        private OptionsMenuClickListener() {
        }

        @Override // android.widget.PopupMenu.OnMenuItemClickListener
        public boolean onMenuItemClick(MenuItem item) {
            return AllAppsHost.this.onOptionsItemSelected(item);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (isInTransition()) {
            return false;
        }
        if (this.mLGAllAppsPagedView != null && isInArrangeMode()) {
            this.mLGAllAppsPagedView.endArrangeMode(false);
            updateButtons();
        }
        this.mMenuSearch.hideAppsSearchBar();
        int itemId = item.getItemId();
        if (itemId == 2) {
            callSelectSortAppsBy();
            return true;
        }
        if (itemId == 3) {
            callHideAppsSettingActivity();
            return true;
        }
        if (itemId != 4) {
            return false;
        }
        LGUserLog.send(getContext(), LGUserLog.FEATURENAME_APPDRAWER_REARRANGE);
        return setArrangeMode(!isInArrangeMode(), true);
    }

    public void closeMenuDialog() {
        try {
            AllAppsSortDialog.closeDialog();
        } catch (Exception unused) {
        }
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.closeMenuDialog();
        }
    }

    public void onDestroy() {
        closeMenuDialog();
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.onDestroy();
            this.mLGAllAppsPagedView = null;
        }
        this.mActivity = null;
        removeCallbacks(this.mBindAllAppsRunnable);
        this.mLGAllAppsPagedView = null;
        this.mOptionsMenu = null;
    }

    public void closeFolder(boolean animated, boolean bOrientation) {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.closeFolder(animated, bOrientation);
        }
    }

    public void endArrangeMode(boolean animated) {
        if (isInArrangeMode()) {
            this.mLGAllAppsPagedView.endArrangeMode(animated);
        }
    }

    public void setApps(ArrayList<AppInfo> list) {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.setApps(list);
        }
        changeEditButtonState();
    }

    public void addApps(ArrayList<AppInfo> apps, int op) {
        this.mMenuSearch.hideAppsSearchBar();
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.addApps(apps, op);
        }
        changeEditButtonState();
    }

    public void updateApps(ArrayList<AppInfo> apps) {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.updateApps(apps);
        }
        changeEditButtonState();
    }

    public void removeApps(ArrayList<AppInfo> apps) {
        this.mMenuSearch.hideAppsSearchBar();
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.removeApps(apps);
        }
        changeEditButtonState();
    }

    public void setup(Activity activity, DragController dragController) {
        this.mActivity = activity;
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.setup(dragController);
        }
    }

    public void setReloadState(ArrayList<AppInfo> apps, boolean needreload) {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.setReloadState(apps, needreload);
        }
        changeEditButtonState();
    }

    public void updateAppList() {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.appUpdateStart();
        }
    }

    public boolean getAppsSearchBarStatus() {
        return this.mMenuSearch.getVisibility() == 0;
    }

    @Override // com.lge.launcher3.allapps.IAllAppsHostListener
    public void setSearchComplete(boolean hasResults) {
        showNoSearchResult(!hasResults);
    }

    @Override // com.lge.launcher3.allapps.IAllAppsHostListener
    public void updateTabIndicator(int activePAge) {
        this.mPersonalWorkTabStrip.setActiveMarker(activePAge);
        onTabChanged(activePAge);
    }

    private void setHeadContainerVisibility(int visible) {
        ViewGroup viewGroup = this.mHeadContainer;
        if (viewGroup != null) {
            viewGroup.setVisibility(visible);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == 0 && this.mMenuSearch.getVisibility() == 0 && event.getY() > this.mActionBarHeight + WindowUtils.getStatusBarHeight(this.mActivity)) {
            this.mMenuSearch.clearFocus();
        }
        return super.dispatchTouchEvent(event);
    }

    public void onResume(LauncherState state) {
        LGLog.d(LOG_TAG, "onResume, isMenuState" + state);
        if (state == LauncherState.ALL_APPS) {
            changeEditButtonState();
            updateAppList();
            AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
            if (allAppsPagedView != null) {
                allAppsPagedView.invalidate();
            }
        } else {
            updateAppList();
            if (this.mLauncher.getWorkspace().getCheckSwipeUpAppDrawer().booleanValue()) {
                this.mLauncher.getWorkspace().setCheckSwipeUpAppDrawer(false);
                this.mLauncher.getWorkspace().backToWorkspaceFromSwipeUpAppDrawer(false);
            }
        }
        if (this.mLGAllAppsPagedView != null) {
            if (!LGHomeFeature.isEnableDefaultHome()) {
                this.mLGAllAppsPagedView.resetForLoop();
            }
            this.mLGAllAppsPagedView.updateFeatureEnabled();
        }
        this.mPersonalWorkTabStrip.setVisibility(ManagedProfileUtils.isAFW(getContext()) ? 0 : 8);
        if (ManagedProfileUtils.isAFW(getContext())) {
            updateWorkProfileComponent();
        } else {
            onTabChanged(this.mLGAllAppsPagedView.getNextPage());
        }
    }

    public void restoreState(Bundle savedState) {
        if (savedState == null) {
            return;
        }
        int i = savedState.getInt("apps_customize_currentIndex");
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.restorePageForIndex(i);
        }
        if (savedState.getBoolean("apps_customize_searchstatus")) {
            String strRestoreSearchKeyword = this.mMenuSearch.restoreSearchKeyword();
            AllAppsPagedView allAppsPagedView2 = this.mLGAllAppsPagedView;
            if (allAppsPagedView2 != null) {
                allAppsPagedView2.setSearchKeyword(strRestoreSearchKeyword);
            }
            this.mMenuSearch.showAppsSearchBar(strRestoreSearchKeyword);
            return;
        }
        if (this.mLGAllAppsPagedView != null) {
            this.mLGAllAppsPagedView.restoreArrangeMode(savedState.getBoolean("apps_customize_editmde"));
        }
    }

    public void saveInstanceState(Bundle outState, boolean isMenuState) {
        int currentPage = getLGAllAppsPagedView().getCurrentPage();
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.getDefaultPage();
        }
        outState.putInt("apps_customize_currentIndex", currentPage);
        if (isMenuState) {
            outState.putBoolean("apps_customize_editmde", isInArrangeMode());
        }
        outState.putBoolean("apps_customize_searchstatus", getAppsSearchBarStatus());
    }

    @Override // com.lge.launcher3.allapps.IAllAppsHostListener
    public void setMenuHostVisibility(int visibility) {
        setVisibility(visibility);
    }

    public void onPause() {
        AllAppsOptionsMenu allAppsOptionsMenu = this.mOptionsMenu;
        if (allAppsOptionsMenu != null && allAppsOptionsMenu.isShowing()) {
            this.mOptionsMenu.dismiss();
        }
        this.mMenuSearch.hideAppsSearchBar();
        hideWorkViews();
    }

    @Override // com.android.launcher3.LauncherTransitionable
    public void onLauncherTransitionPrepare(Launcher l, boolean animated, boolean toWorkspace) {
        this.mInTransition = true;
    }

    private void setBackgroundDimming(boolean toWorkspace) {
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        if (this.mMenuContent == null || dragLayer == null) {
            return;
        }
        if (toWorkspace && this.mLauncher.getWorkspace().getState() != Workspace.State.SPRING_LOADED) {
            dragLayer.setBackgroundAlpha(0.0f);
        } else {
            dragLayer.setBackgroundAlpha(this.mContext.getResources().getInteger(R.integer.config_workspaceScrimAlpha) / 100.0f);
        }
    }

    @Override // com.android.launcher3.LauncherTransitionable
    public void onLauncherTransitionStart(Launcher l, boolean animated, boolean toWorkspace) {
        if (toWorkspace) {
            closeMenuDialog();
            closeFolder(true, true);
            this.mMenuSearch.clearFocus();
            this.mMenuSearch.hideAppsSearchBar();
        } else {
            setChildVisible();
        }
        setBackgroundDimming(toWorkspace);
        this.mInTransition = true;
    }

    @Override // com.android.launcher3.LauncherTransitionable
    public void onLauncherTransitionEnd(Launcher l, boolean animated, boolean toWorkspace) {
        LGLog.d(LOG_TAG, "onLauncherTransitionEnd");
        this.mInTransition = false;
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView == null) {
            return;
        }
        if (!toWorkspace) {
            allAppsPagedView.appLoadStart(Boolean.valueOf(!animated));
            this.mLGAllAppsPagedView.postHardwareLayerOn();
            if (ManagedProfileUtils.isAFW(getContext())) {
                this.mLGAllAppsPagedView.setAfwTabPositon();
            }
            changeEditButtonState();
            setChildFocus();
            this.mMenuSearch.requestFocusForSearch();
            if (this.mLauncher.isInState(LauncherState.ALL_APPS)) {
                if (SharedPreferencesManager.getBoolean(getContext(), 0, SharedPreferencesConst.OverviewGuideKey.IS_ENABLED, true)) {
                    SharedPreferencesManager.putInt(getContext(), 0, SharedPreferencesConst.OverviewGuideKey.COUNT, getContext().getResources().getInteger(R.integer.config_swipe_up_count));
                    SharedPreferencesManager.putBoolean(getContext(), 0, SharedPreferencesConst.OverviewGuideKey.IS_ENABLED, false);
                }
                if (getShowWorkTabIfNeeded()) {
                    Executors.MAIN_EXECUTOR.postDelayed(new Runnable() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsHost$kz8PkNh6wLz74NHKIHX4dM62Z6U
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onLauncherTransitionEnd$2$AllAppsHost();
                        }
                    }, 1000);
                    return;
                }
                return;
            }
            return;
        }
        allAppsPagedView.destroyHardwareLayerCreationExceptCurpage();
        this.mLGAllAppsPagedView.setIsPageMoving(false);
        if (isInArrangeMode()) {
            setArrangeMode(false, true);
        }
    }

    public void setShowWorkTabIfNeeded() {
        SharedPreferencesManager.putBoolean(getContext(), 0, SharedPreferencesConst.NEED_TO_GO_WORK_TAB.GO_WORK_TAB, true);
    }

    public boolean getShowWorkTabIfNeeded() {
        return SharedPreferencesManager.getBoolean(getContext(), 0, SharedPreferencesConst.NEED_TO_GO_WORK_TAB.GO_WORK_TAB, false);
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$onLauncherTransitionEnd$2$AllAppsHost()V */
    /* JADX DEBUG: Method merged with bridge method: lambda$showWorkTab$3$AllAppsHost()V */
    /* JADX INFO: renamed from: showWorkTab, reason: merged with bridge method [inline-methods] and merged with bridge method [inline-methods] */
    public void lambda$showWorkTab$3$AllAppsHost() {
        AllAppsPagedView lGAllAppsPagedView = getLGAllAppsPagedView();
        if (lGAllAppsPagedView == null) {
            LGLog.d(LOG_TAG, "showWorkTab : return cause by pagedview is null");
            return;
        }
        if (lGAllAppsPagedView.getManagedProfileStartPage() < 1) {
            LGLog.d(LOG_TAG, "showWorkTab : return cause by set tab not yet");
            Executors.MAIN_EXECUTOR.postDelayed(new Runnable() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsHost$WNLqdRcyKuIUQlwENoPj43mhSek
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showWorkTab$3$AllAppsHost();
                }
            }, 1000);
        } else {
            lGAllAppsPagedView.setAfwTabPositon();
            lGAllAppsPagedView.snapToPage(lGAllAppsPagedView.getManagedProfileStartPage());
            SharedPreferencesManager.putBoolean(getContext(), 0, SharedPreferencesConst.NEED_TO_GO_WORK_TAB.GO_WORK_TAB, false);
        }
    }

    private String getHeadTitle() {
        return getResources().getString(R.string.all_apps_button_label);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mInTransition) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override // com.lge.launcher3.allapps.IAllAppsSearchListener
    public void onSearch(CharSequence key) {
        if (key != null) {
            String lowerCase = key.toString().toLowerCase(Locale.getDefault());
            AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
            if (allAppsPagedView != null) {
                allAppsPagedView.searchApps(lowerCase);
            }
        }
    }

    @Override // com.lge.launcher3.allapps.IAllAppsSearchListener
    public void prepareSearchViewHide() {
        setHeadContainerVisibility(0);
        if (ManagedProfileUtils.isAFW(getContext())) {
            this.mPersonalWorkTabStrip.setVisibility(0);
            this.mLGAllAppsPagedView.snapToPage(0);
        }
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.setShowSearchBar(false);
        }
        changeEditButtonState();
        dispatchWindowFocusChanged(true);
        setChildFocus();
    }

    @Override // com.lge.launcher3.allapps.IAllAppsSearchListener
    public void prepareSearchViewShow() {
        setHeadContainerVisibility(4);
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.setShowSearchBar(true);
        }
        hideWorkViews();
    }

    public void onStop() {
        this.mMenuSearch.saveSearchKeyword();
    }

    @Override // com.lge.launcher3.allapps.IAllAppsHostListener
    public void sendTalkBackDescription(String description) {
        if (this.mAccessibilityManager.isEnabled()) {
            AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(32);
            accessibilityEventObtain.getText().add(getHeadTitle() + " " + description);
            this.mAccessibilityManager.sendAccessibilityEvent(accessibilityEventObtain);
        }
    }

    public void setCustomFocus(boolean hasFocus) {
        this.mMenuSearch.setFocus(hasFocus);
    }

    public void bindAllApplications(final ArrayList<AppInfo> apps) {
        closeFolder(false, false);
        endArrangeMode(false);
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            allAppsPagedView.resetAllAppsLoadedBySearchBar();
            onPause();
        }
        setApps(apps);
        layout();
        LauncherAppState launcherAppState = LauncherAppState.getInstance(getContext());
        int hideAppsCount = launcherAppState.getHideAppsCount();
        if (hideAppsCount != -1) {
            String string = getContext().getString(R.string.hide_apps_saved_message);
            launcherAppState.setHideAppsCount(-1);
            Toast.makeText(getContext(), String.format(string, Integer.valueOf(hideAppsCount)), 0).show();
        }
    }

    public void homeKeyPressed(boolean alreadyOnHome) {
        if (alreadyOnHome) {
            return;
        }
        reset(null);
    }

    private void setChildFocus() {
        AllAppsPagedView allAppsPagedView;
        if (hasVisibleItems() && (allAppsPagedView = this.mLGAllAppsPagedView) != null) {
            allAppsPagedView.requestFocus(2);
            this.mLGAllAppsPagedView.setChildFocus();
        }
    }

    private boolean hasVisibleItems() {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        return allAppsPagedView != null && allAppsPagedView.getAllAppsCount() > 0;
    }

    private void showNoSearchResult(boolean visible) {
        if (this.mNoSearchResult == null) {
            return;
        }
        hideWorkViews();
        if (visible) {
            this.mNoSearchResult.setVisibility(0);
        } else {
            this.mNoSearchResult.setVisibility(8);
        }
        invalidate();
    }

    public void showTitleButtonGuideToast(CharSequence message) {
        getResources();
        Toast.makeText(getContext(), message, 0).show();
    }

    public boolean isInArrangeMode() {
        AllAppsPagedView allAppsPagedView = this.mLGAllAppsPagedView;
        if (allAppsPagedView != null) {
            return allAppsPagedView.isInArrangeMode();
        }
        return false;
    }

    private void callHideAppsSettingActivity() {
        LGUserLog.send(getContext(), LGUserLog.FEATURENAME_SHOWHIDEAPPS);
        try {
            Intent intent = new Intent(getContext(), (Class<?>) HideAppsSettingActivity.class);
            intent.setFlags(268468224);
            this.mActivity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LGLog.e(LOG_TAG, "ActivityNotFoundException - ", e);
        }
    }

    public void addSpringFromFlingUpdateListener(ValueAnimator animator, final float velocity) {
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.allapps.AllAppsHost.3
            boolean shouldSpring = true;

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (!this.shouldSpring || valueAnimator.getAnimatedFraction() < AllAppsHost.FLING_ANIMATION_THRESHOLD) {
                    return;
                }
                AllAppsHost.this.finishWithShiftAndVelocity(1.0f, velocity * AllAppsHost.FLING_VELOCITY_MULTIPLIER, new DynamicAnimation.OnAnimationEndListener() { // from class: com.lge.launcher3.allapps.AllAppsHost.3.1
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity2) {
                        AllAppsHost.this.invalidate();
                    }
                });
                this.shouldSpring = false;
            }
        });
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (this.mDampedScrollShift != 0.0f && this.mSpringViews.get(child.getId())) {
            int iSave = canvas.save();
            canvas.clipRect(0, 0, getWidth(), getHeight());
            canvas.translate(0.0f, this.mDampedScrollShift);
            boolean zDrawChild = super.drawChild(canvas, child, drawingTime);
            canvas.restoreToCount(iSave);
            return zDrawChild;
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    private void finishScrollWithVelocity(float velocity) {
        this.mSpring.setStartVelocity(velocity);
        this.mSpring.setStartValue(this.mDampedScrollShift);
        this.mSpring.start();
    }

    public MultiValueAlpha.AlphaProperty getAlphaProperty(int index) {
        return this.mMultiValueAlpha.getProperty(index);
    }

    protected void setDampedScrollShift(float shift) {
        if (shift != this.mDampedScrollShift) {
            this.mDampedScrollShift = shift;
            invalidate();
        }
    }

    protected void finishWithShiftAndVelocity(float shift, float velocity, DynamicAnimation.OnAnimationEndListener listener) {
        setDampedScrollShift(shift);
        this.mSpring.addEndListener(listener);
        finishScrollWithVelocity(velocity);
    }

    public void addSpringView(int id) {
        this.mSpringViews.put(id, true);
    }

    public void removeSpringView(int id) {
        this.mSpringViews.delete(id);
        invalidate();
    }

    public void updateWorkProfileComponent() {
        Executors.MAIN_EXECUTOR.postDelayed(new Runnable() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsHost$Kpw12g4l7dN_blhYz1dU4rD_jcw
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updateWorkProfileComponent$4$AllAppsHost();
            }
        }, 500);
    }

    public /* synthetic */ void lambda$updateWorkProfileComponent$4$AllAppsHost() {
        AllAppsPagedView allAppsPagedView;
        if (!ManagedProfileUtils.isAFW(getContext()) || (allAppsPagedView = this.mLGAllAppsPagedView) == null) {
            return;
        }
        allAppsPagedView.setAfwTabPositon();
    }

    public void setWorkProfileEnabled(final boolean enabled) {
        final UserManagerCompat userManagerCompat = UserManagerCompat.getInstance(this.mLauncher);
        Executors.UI_HELPER_EXECUTOR.post(new Runnable() { // from class: com.lge.launcher3.allapps.-$$Lambda$AllAppsHost$ywsT1Bj1Pln-VKF291SOjWSnCj8
            @Override // java.lang.Runnable
            public final void run() {
                AllAppsHost.lambda$setWorkProfileEnabled$5(userManagerCompat, enabled);
            }
        });
    }

    static /* synthetic */ void lambda$setWorkProfileEnabled$5(UserManagerCompat userManagerCompat, boolean z) {
        for (UserHandle userHandle : userManagerCompat.getUserProfiles()) {
            if (!Process.myUserHandle().equals(userHandle) && userHandle.getIdentifier() != 97) {
                userManagerCompat.requestQuietModeEnabled(!z, userHandle);
            }
        }
    }

    private void setWorkProfileButtonText() {
        String str = isWokProfileQuiet() ? this.mLauncher.getStringCache().workProfileEnableButton : this.mLauncher.getStringCache().workProfilePauseButton;
        Button button = this.mFabWorkProfileSwitch;
        if (button != null) {
            button.setText(str);
        }
    }

    private void showWorkOffContentLayout() {
        if (this.mWorkOffContentLayout != null) {
            this.mWorkOffContentLayout.setVisibility(isWokProfileQuiet() && this.mIsWorkTab ? 0 : 8);
        }
    }

    private void hideWorkViews() {
        this.mFabWorkProfileSwitch.setVisibility(8);
        this.mWorkOffContentLayout.setVisibility(8);
    }

    @Override // android.view.View
    protected boolean fitSystemWindows(Rect insets) {
        if (!this.mLauncher.getDeviceProfile().isMultiWindowMode) {
            WindowUtils.modifyInsetsForHideNav(getContext(), insets);
        }
        return super.fitSystemWindows(insets);
    }

    public void setOptionMenuVisibility(int visibility) {
        LGLog.d("AllAppsHost", "setOptionMenuVisibility: " + visibility);
        this.mOptionsMenuButton.setVisibility(visibility);
    }

    public void onTabChanged(int pos) {
        if (ManagedProfileUtils.isAFW(getContext()) && pos == 1) {
            WorkEduView.showWorkEduIfNeeded(this.mLauncher);
            this.mIsWorkTab = true;
        } else {
            this.mIsWorkTab = false;
        }
        if (this.mFabWorkProfileSwitch != null) {
            setWorkProfileButtonText();
            showWorkOffContentLayout();
            this.mFabWorkProfileSwitch.setVisibility(this.mIsWorkTab ? 0 : 8);
        }
    }
}
