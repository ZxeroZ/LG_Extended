package com.android.launcher3;

import android.animation.Animator;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SecondaryDisplayLauncher extends FragmentActivity implements AppPickedCallback, PopupMenu.OnMenuItemClickListener, ISecondaryDisplayLauncherCallback {
    private static boolean FEATURE_BUTTON_APPDRAWER = false;
    private static final String TAG = "SecondaryDisplayLauncher";
    private AppDrawerDialog mAppDrawerDialogFragment;
    private boolean mAppDrawerShown;
    private AppListAdapter mAppListAdapter;
    private FloatingActionButton mFab;
    private AppListAdapter mPinnedAppListAdapter;
    private View mRootView;

    private void setupTransparentSystemBarsForLmp() {
        if (Utilities.isLmpOrAbove()) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | 256 | 1024 | 512);
            window.clearFlags(201326592);
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(0);
            window.setNavigationBarColor(0);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setupTransparentSystemBarsForLmp();
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        String str = TAG;
        LGLog.i(str, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondary_display_launcher);
        View viewFindViewById = findViewById(R.id.RootView);
        this.mRootView = viewFindViewById;
        viewFindViewById.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.launcher3.-$$Lambda$SecondaryDisplayLauncher$DBJW3S-dqVlUanQSLtegTTDLd7o
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return this.f$0.lambda$onCreate$0$SecondaryDisplayLauncher(view, motionEvent);
            }
        });
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.FloatingActionButton);
        this.mFab = floatingActionButton;
        if (floatingActionButton != null) {
            if (FEATURE_BUTTON_APPDRAWER) {
                floatingActionButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.-$$Lambda$SecondaryDisplayLauncher$E5XLrBvZFPT3PpSY9x7DSzUA2wI
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$onCreate$1$SecondaryDisplayLauncher(view);
                    }
                });
                this.mFab.setVisibility(0);
            } else {
                floatingActionButton.setVisibility(8);
            }
        }
        if (PinnedAppListUtils.getFirstRunValueFromPreference(this)) {
            PinnedAppListUtils.saveInitialLayoutToPreference(this, PinnedAppListUtils.loadSecondaryLayoutFromXML(this));
            PinnedAppListUtils.saveFirstRunValueFromPreference(this, false);
        }
        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(), new ViewModelProvider.AndroidViewModelFactory((Application) getApplicationContext()));
        AppListAdapter appListAdapter = new AppListAdapter(this);
        this.mPinnedAppListAdapter = appListAdapter;
        LGLog.i(str, "onCreate - mPinnedAppListAdapter : " + appListAdapter);
        final GridView gridView = (GridView) findViewById(R.id.pinned_app_grid);
        gridView.setAdapter((ListAdapter) this.mPinnedAppListAdapter);
        gridView.setOnKeyListener(new View.OnKeyListener() { // from class: com.android.launcher3.SecondaryDisplayLauncher.1
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() != 1 || i != 66) {
                    return false;
                }
                AppListUtils.launch(SecondaryDisplayLauncher.this.getApplicationContext(), SecondaryDisplayLauncher.this.mPinnedAppListAdapter.getItem(gridView.getSelectedItemPosition()).getLaunchIntent());
                return false;
            }
        });
        PinnedAppListViewModel pinnedAppListViewModel = (PinnedAppListViewModel) viewModelProvider.get(PinnedAppListViewModel.class);
        LGLog.i(str, "onCreate - pinnedAppListViewModel : " + pinnedAppListViewModel);
        if (pinnedAppListViewModel != null) {
            LGLog.i(str, "onCreate - pinnedAppListViewModel.getPinnedAppList() : " + pinnedAppListViewModel.getPinnedAppList());
            if (pinnedAppListViewModel.getPinnedAppList() != null) {
                LGLog.i(str, "onCreate - pinnedAppListViewModel.getPinnedAppList().getValue() : " + pinnedAppListViewModel.getPinnedAppList().getValue());
                if (pinnedAppListViewModel.getPinnedAppList().getValue() != null) {
                    LGLog.i(str, "onCreate - pinnedAppListViewModel isEmpty : " + pinnedAppListViewModel.getPinnedAppList().getValue().isEmpty());
                    LGLog.i(str, "onCreate - pinnedAppListViewModel size : " + pinnedAppListViewModel.getPinnedAppList().getValue().size());
                }
            }
        }
        pinnedAppListViewModel.getPinnedAppList().observe(this, new Observer() { // from class: com.android.launcher3.-$$Lambda$SecondaryDisplayLauncher$8ddYvXsrog8yngyQBV2jqjBCtUc
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                this.f$0.lambda$onCreate$2$SecondaryDisplayLauncher((List) obj);
            }
        });
        AppListAdapter appListAdapter2 = new AppListAdapter(this);
        this.mAppListAdapter = appListAdapter2;
        LGLog.i(str, "onCreate - mAppListAdapter : " + appListAdapter2);
        GridView gridView2 = (GridView) findViewById(R.id.app_grid);
        gridView2.setAdapter((ListAdapter) this.mAppListAdapter);
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.launcher3.-$$Lambda$SecondaryDisplayLauncher$wQ2rS_VfRwq48GhuyF0h0mzavYw
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                this.f$0.lambda$onCreate$3$SecondaryDisplayLauncher(adapterView, view, i, j);
            }
        });
        AppListViewModel appListViewModel = (AppListViewModel) viewModelProvider.get(AppListViewModel.class);
        LGLog.i(str, "onCreate - appListViewModel : " + appListViewModel);
        if (appListViewModel != null) {
            LGLog.i(str, "onCreate - appListViewModel.getAppList() : " + appListViewModel.getAppList());
            if (appListViewModel.getAppList() != null) {
                LGLog.i(str, "onCreate - appListViewModel.getAppList().getValue() : " + appListViewModel.getAppList().getValue());
                if (appListViewModel.getAppList().getValue() != null) {
                    LGLog.i(str, "onCreate - appListViewModel isEmpty : " + appListViewModel.getAppList().getValue().isEmpty());
                    LGLog.i(str, "onCreate - appListViewModel size : " + appListViewModel.getAppList().getValue().size());
                }
            }
        }
        appListViewModel.getAppList().observe(this, new Observer() { // from class: com.android.launcher3.-$$Lambda$SecondaryDisplayLauncher$r4gvEA8kDYasxSsU7eAJpHL7Th8
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                this.f$0.lambda$onCreate$4$SecondaryDisplayLauncher((List) obj);
            }
        });
        SecondaryDisplayLauncherManager.getInstance(this).setSecondaryDisplayLauncherCallback(this);
        LGLog.i(str, "[Icon_debug] SecondaryDisplayLauncher with activity context : " + getResources().getDisplayMetrics().toString());
        LGLog.i(str, "[Icon_debug] SecondaryDisplayLauncher with application context : " + getApplicationContext().getResources().getDisplayMetrics().toString());
    }

    public /* synthetic */ boolean lambda$onCreate$0$SecondaryDisplayLauncher(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            LGLog.i(TAG, "onCreate - mRootView.setOnTouchListener. showAppDrawer(false)");
            showAppDrawer(false);
        }
        return false;
    }

    public /* synthetic */ void lambda$onCreate$1$SecondaryDisplayLauncher(View view) {
        LGLog.i(TAG, "onCreate - FloatingActionButton.setOnClickListener. showAppDrawer(true)");
        showAppDrawer(true);
    }

    public /* synthetic */ void lambda$onCreate$2$SecondaryDisplayLauncher(List list) {
        LGLog.i(TAG, "onCreate - pinnedAppListViewModel.getPinnedAppList().observe ");
        this.mPinnedAppListAdapter.setData(list);
    }

    public /* synthetic */ void lambda$onCreate$3$SecondaryDisplayLauncher(AdapterView adapterView, View view, int i, long j) {
        AppListUtils.launch(this, this.mAppListAdapter.getItem(i).getLaunchIntent());
    }

    public /* synthetic */ void lambda$onCreate$4$SecondaryDisplayLauncher(List list) {
        LGLog.i(TAG, "onCreate - appListViewModel.getAppList().observe ");
        this.mAppListAdapter.setData(list);
    }

    @Override // android.widget.PopupMenu.OnMenuItemClickListener
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.add_app_shortcut) {
            PinnedAppPickerDialog.newInstance(this.mAppListAdapter, this).show(getSupportFragmentManager(), "fragment_app_picker");
        }
        return true;
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LGLog.i(TAG, "onConfigurationChanged - showAppDrawer(false)");
        showAppDrawer(false);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        LGLog.i(TAG, "onBackPressed - showAppDrawer(false)");
        showAppDrawer(false);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        View viewPeekDecorView;
        super.onNewIntent(intent);
        if (PackageUtils.ANDROID_INTENT_ACTION_MAIN.equals(intent.getAction()) && (viewPeekDecorView = getWindow().peekDecorView()) != null && viewPeekDecorView.getWindowToken() != null) {
            ((InputMethodManager) getSystemService(InputMethodManager.class)).hideSoftInputFromWindow(viewPeekDecorView.getWindowToken(), 0);
        }
        LGLog.i(TAG, "onNewIntent - showAppDrawer(false)");
        showAppDrawer(false);
    }

    @Override // com.android.launcher3.AppPickedCallback
    public void onAppPicked(AppEntry appEntry) {
        String str = TAG;
        LGLog.i(str, "onAppPicked - appEntry = " + appEntry);
        if (appEntry != null) {
            LGLog.i(str, "onAppPicked - appEntry.getLabel() = " + appEntry.getLabel());
        }
        SharedPreferences sharedPreferences = getSharedPreferences("pinned_apps", 0);
        List arrayList = (List) new Gson().fromJson(sharedPreferences.getString("pinned_apps", null), new TypeToken<List<String>>() { // from class: com.android.launcher3.SecondaryDisplayLauncher.2
        }.getType());
        LGLog.i(str, "onAppPicked - pinnedApps = " + arrayList);
        if (arrayList == null) {
            arrayList = new ArrayList();
        }
        String strFlattenToString = appEntry.getComponentName().flattenToString();
        LGLog.i(str, "onAppPicked - pinnedApps.size() = " + arrayList.size());
        LGLog.i(str, "onAppPicked - newEntry = " + strFlattenToString);
        if (arrayList.contains(strFlattenToString)) {
            LGLog.i(str, "onAppPicked - pinnedApps contains  " + strFlattenToString);
            return;
        }
        arrayList.add(appEntry.getComponentName().flattenToString());
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        sharedPreferences.edit().putString("pinned_apps", new Gson().toJson(arrayList)).apply();
        editorEdit.apply();
    }

    private void showAppDrawer(boolean show) {
        String str = TAG;
        LGLog.i(str, "showAppDrawer: show = " + show);
        AppDrawerDialog appDrawerDialog = this.mAppDrawerDialogFragment;
        if (appDrawerDialog != null) {
            LGLog.i(str, "showAppDrawer: mAppDrawerDialogFragment.isVisible() = " + appDrawerDialog.isVisible());
        }
        AppDrawerDialog appDrawerDialog2 = this.mAppDrawerDialogFragment;
        if (appDrawerDialog2 != null && show == appDrawerDialog2.isVisible()) {
            LGLog.i(str, "skip showAppDrawer = " + show);
            return;
        }
        if (show) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            AppDrawerDialog appDrawerDialog3 = AppDrawerDialog.getInstance(this.mAppListAdapter);
            this.mAppDrawerDialogFragment = appDrawerDialog3;
            if (!appDrawerDialog3.isAdded()) {
                this.mAppDrawerDialogFragment.show(supportFragmentManager, "fragment_app_drawer");
            }
            this.mAppDrawerShown = true;
            return;
        }
        AppDrawerDialog appDrawerDialog4 = this.mAppDrawerDialogFragment;
        if (appDrawerDialog4 != null) {
            appDrawerDialog4.dismiss();
        }
        this.mAppDrawerShown = false;
    }

    private Animator revealAnimator(View view, boolean open) {
        int iHypot = (int) Math.hypot(view.getWidth(), view.getHeight());
        return ViewAnimationUtils.createCircularReveal(view, view.getRight(), view.getBottom(), open ? 0.0f : iHypot, open ? iHypot : 0.0f);
    }

    @Override // com.android.launcher3.ISecondaryDisplayLauncherCallback
    public void executeAppDrawer(int mode) {
        boolean z = !this.mAppDrawerShown;
        AppDrawerDialog appDrawerDialog = this.mAppDrawerDialogFragment;
        if (appDrawerDialog != null) {
            z = !appDrawerDialog.isVisible();
            LGLog.i(TAG, "executeAppDrawer: mAppDrawerDialogFragment.isVisible() = " + this.mAppDrawerDialogFragment.isVisible());
        }
        String str = TAG;
        LGLog.i(str, "executeAppDrawer: mAppDrawerShown = " + this.mAppDrawerShown + ", mAppDrawerDialogFragment = " + this.mAppDrawerDialogFragment);
        LGLog.i(str, "executeAppDrawer: show = " + z + ", mode = " + mode);
        showAppDrawer(z);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        LGLog.i(TAG, "onResume");
        super.onResume();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        LGLog.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        LGLog.i(TAG, "onStop");
        super.onStop();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        LGLog.i(TAG, "onPause - mAppDrawerDialogFragment = " + this.mAppDrawerDialogFragment + ", mAppDrawerShown = " + this.mAppDrawerShown);
        AppDrawerDialog appDrawerDialog = this.mAppDrawerDialogFragment;
        if (appDrawerDialog != null) {
            appDrawerDialog.dismiss();
            this.mAppDrawerDialogFragment = null;
        }
        this.mAppDrawerShown = false;
        super.onPause();
    }
}
