package com.android.launcher3;

import android.accounts.AccountManager;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityManager;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
class LauncherClings implements View.OnClickListener {
    private static final boolean DISABLE_CLINGS = false;
    private static final int DISMISS_CLING_DURATION = 200;
    private static final String MIGRATION_CLING_DISMISSED_KEY = "cling_gel.migration.dismissed";
    private static final int SHOW_CLING_DURATION = 250;
    private static final String SKIP_FIRST_USE_HINTS = "skip_first_use_hints";
    private static final String TAG_CROP_TOP_AND_SIDES = "crop_bg_top_and_sides";
    private static final String WORKSPACE_CLING_DISMISSED_KEY = "cling_gel.workspace.dismissed";
    private LayoutInflater mInflater;
    Launcher mLauncher;

    public LauncherClings(Launcher launcher) {
        this.mLauncher = launcher;
        this.mInflater = LayoutInflater.from(launcher);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cling_dismiss_migration_use_default) {
            dismissMigrationCling();
            return;
        }
        if (id != R.id.cling_dismiss_migration_copy_apps) {
            if (id == R.id.cling_dismiss_longpress_info) {
                dismissLongPressCling();
                return;
            }
            return;
        }
        LauncherModel model = this.mLauncher.getModel();
        model.resetLoadedState(false, true);
        model.startLoader(com.lge.launcher3.PagedView.INVALID_RESTORE_PAGE, 3);
        SharedPreferences.Editor editorEdit = this.mLauncher.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).edit();
        editorEdit.putBoolean(Launcher.USER_HAS_MIGRATED, true);
        editorEdit.apply();
        dismissMigrationCling();
    }

    public void showMigrationCling() {
        this.mLauncher.hideWorkspaceSearchAndHotseat();
        View viewInflate = this.mInflater.inflate(R.layout.migration_cling, (ViewGroup) this.mLauncher.findViewById(R.id.launcher));
        viewInflate.findViewById(R.id.cling_dismiss_migration_copy_apps).setOnClickListener(this);
        viewInflate.findViewById(R.id.cling_dismiss_migration_use_default).setOnClickListener(this);
    }

    private void dismissMigrationCling() {
        this.mLauncher.showWorkspaceSearchAndHotseat();
        this.mLauncher.getWorkspace().post(new Runnable() { // from class: com.android.launcher3.LauncherClings.1
            @Override // java.lang.Runnable
            public void run() {
                Runnable runnable = new Runnable() { // from class: com.android.launcher3.LauncherClings.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        LauncherClings.this.showLongPressCling(false);
                    }
                };
                LauncherClings launcherClings = LauncherClings.this;
                launcherClings.dismissCling(launcherClings.mLauncher.findViewById(R.id.migration_cling), runnable, LauncherClings.MIGRATION_CLING_DISMISSED_KEY, 200);
            }
        });
    }

    public void showLongPressCling(boolean showWelcome) {
        ViewGroup viewGroup = (ViewGroup) this.mLauncher.findViewById(R.id.launcher);
        View viewInflate = this.mInflater.inflate(R.layout.longpress_cling, viewGroup, false);
        viewInflate.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.launcher3.LauncherClings.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                LauncherClings.this.mLauncher.showOverviewMode(true);
                LauncherClings.this.dismissLongPressCling();
                return true;
            }
        });
        final ViewGroup viewGroup2 = (ViewGroup) viewInflate.findViewById(R.id.cling_content);
        this.mInflater.inflate(showWelcome ? R.layout.longpress_cling_welcome_content : R.layout.longpress_cling_content, viewGroup2);
        viewGroup2.findViewById(R.id.cling_dismiss_longpress_info).setOnClickListener(this);
        if (TAG_CROP_TOP_AND_SIDES.equals(viewGroup2.getTag())) {
            viewGroup2.setBackground(new BorderCropDrawable(this.mLauncher.getResources().getDrawable(R.drawable.cling_bg), true, true, true, false));
        }
        viewGroup.addView(viewInflate);
        if (showWelcome) {
            return;
        }
        viewGroup2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.android.launcher3.LauncherClings.3
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                ObjectAnimator objectAnimatorOfPropertyValuesHolder;
                viewGroup2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (LauncherClings.TAG_CROP_TOP_AND_SIDES.equals(viewGroup2.getTag())) {
                    viewGroup2.setTranslationY(-r0.getMeasuredHeight());
                    objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofFloat(viewGroup2, "translationY", 0.0f);
                } else {
                    viewGroup2.setScaleX(0.0f);
                    viewGroup2.setScaleY(0.0f);
                    objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(viewGroup2, PropertyValuesHolder.ofFloat("scaleX", 1.0f), PropertyValuesHolder.ofFloat("scaleY", 1.0f));
                }
                objectAnimatorOfPropertyValuesHolder.setDuration(250L);
                objectAnimatorOfPropertyValuesHolder.setInterpolator(new LogDecelerateInterpolator(100, 0));
                objectAnimatorOfPropertyValuesHolder.start();
            }
        });
    }

    void dismissLongPressCling() {
        this.mLauncher.getWorkspace().post(new Runnable() { // from class: com.android.launcher3.LauncherClings.4
            @Override // java.lang.Runnable
            public void run() {
                LauncherClings launcherClings = LauncherClings.this;
                launcherClings.dismissCling(launcherClings.mLauncher.findViewById(R.id.longpress_cling), null, LauncherClings.WORKSPACE_CLING_DISMISSED_KEY, 200);
            }
        });
    }

    void dismissCling(final View cling, final Runnable postAnimationCb, final String flag, int duration) {
        if (cling == null || cling.getVisibility() == 8) {
            return;
        }
        Runnable runnable = new Runnable() { // from class: com.android.launcher3.LauncherClings.5
            @Override // java.lang.Runnable
            public void run() {
                cling.setVisibility(8);
                LauncherClings.this.mLauncher.getSharedPrefs().edit().putBoolean(flag, true).apply();
                Runnable runnable2 = postAnimationCb;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        };
        if (duration <= 0) {
            runnable.run();
        } else {
            cling.animate().alpha(0.0f).setDuration(duration).withEndAction(runnable);
        }
    }

    private boolean areClingsEnabled() {
        if (ActivityManager.isRunningInTestHarness() || ((AccessibilityManager) this.mLauncher.getSystemService("accessibility")).isTouchExplorationEnabled()) {
            return false;
        }
        return (((Build.VERSION.SDK_INT >= 18) && AccountManager.get(this.mLauncher).getAccounts().length == 0 && ((UserManager) this.mLauncher.getSystemService("user")).getUserRestrictions().getBoolean("no_modify_accounts", false)) || Settings.Secure.getInt(this.mLauncher.getContentResolver(), SKIP_FIRST_USE_HINTS, 0) == 1) ? false : true;
    }

    public boolean shouldShowFirstRunOrMigrationClings() {
        SharedPreferences sharedPrefs = this.mLauncher.getSharedPrefs();
        return (!areClingsEnabled() || sharedPrefs.getBoolean(WORKSPACE_CLING_DISMISSED_KEY, false) || sharedPrefs.getBoolean(MIGRATION_CLING_DISMISSED_KEY, false)) ? false : true;
    }

    public static void synchonouslyMarkFirstRunClingDismissed(Context ctx) {
        SharedPreferences.Editor editorEdit = ctx.getSharedPreferences(LauncherAppState.getSharedPreferencesKey(), 0).edit();
        editorEdit.putBoolean(WORKSPACE_CLING_DISMISSED_KEY, true);
        editorEdit.commit();
    }
}
