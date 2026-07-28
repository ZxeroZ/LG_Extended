package com.lge.launcher3.hideapps;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.hideapps.HideAppsAdapter;
import com.lge.launcher3.liveicon.LiveIcon;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.liveicon.OnLiveIconUpdateListener;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.lgdynamicactionbar.AppBarLayout;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class HideAppsSettingActivity extends Activity implements HideAppsAdapter.OnCheckStateChangedListener {
    private static final String TAG = "HideApps";
    private Button mApplyButton;
    private TextView mCheckCountText;
    private TextView mDescription;
    private GridView mGridView;
    private HideAppsAdapter mGridViewAdapter;
    private LiveIconManager mLiveIconManager;
    private OnLiveIconUpdateListener mLiveIconUpdateListener = new OnLiveIconUpdateListener() { // from class: com.lge.launcher3.hideapps.HideAppsSettingActivity.1
        @Override // com.lge.launcher3.liveicon.OnLiveIconUpdateListener
        public void onLiveIconUpdate(final LiveIcon liveIcon) {
            new Handler().post(new Runnable() { // from class: com.lge.launcher3.hideapps.HideAppsSettingActivity.1.1
                @Override // java.lang.Runnable
                public void run() {
                    ComponentName componentName = liveIcon.getComponentName();
                    int childCount = HideAppsSettingActivity.this.mGridView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        CheckableAppIcon checkableAppIcon = (CheckableAppIcon) HideAppsSettingActivity.this.mGridView.getChildAt(i);
                        HideAppItem hideAppItem = (HideAppItem) checkableAppIcon.getTag();
                        if (componentName.equals(hideAppItem.activityInfo.getComponentName())) {
                            checkableAppIcon.setIcon(liveIcon.getBadgedIcon(hideAppItem.userHandle));
                            HideAppsSettingActivity.this.mGridViewAdapter.notifyDataSetInvalidated();
                        }
                    }
                }
            });
        }
    };
    private boolean mSaving;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(34210242);
        super.onCreate(savedInstanceState);
        if (isFreeformMode(this)) {
            Toast.makeText(this, R.string.toast_cannot_open_in_popup_window, 0).show();
            finish();
            return;
        }
        setTheme(R.style.DynamicActionBarTheme_NoActionBar);
        setContentView(R.layout.multiselect_appicon);
        customzingDAB();
        setupViews();
        LiveIconManager liveIconManager = LiveIconManager.getInstance(this);
        this.mLiveIconManager = liveIconManager;
        liveIconManager.registerOnLiveIconUpdateListener(this.mLiveIconUpdateListener);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mGridView.setNumColumns(getResources().getInteger(R.integer.hide_apps_setting_grid_columns));
        this.mGridViewAdapter.notifyDataSetChanged();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mLiveIconManager.start();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        LiveIconManager liveIconManager = this.mLiveIconManager;
        if (liveIconManager != null) {
            liveIconManager.unregisterOnLiveIconUpdateListener(this.mLiveIconUpdateListener);
            this.mLiveIconManager.stop();
        }
    }

    private void setupViews() {
        this.mDescription = (TextView) findViewById(R.id.multiselect_description);
        if (LGHomeFeature.isEnableDefaultHome()) {
            this.mDescription.setText(R.string.hide_apps_guide_text);
        } else if (LGFeatureConfig.FEATURE_OPERATOR.equals("VZW")) {
            this.mDescription.setText(R.string.allapps_hide_apps_guide_text_vzw);
        } else {
            this.mDescription.setText(R.string.allapps_hide_apps_guide_text);
        }
        this.mDescription.setVisibility(0);
        this.mGridView = (GridView) findViewById(R.id.multiselect_apps_grid_view);
        HideAppsAdapter hideAppsAdapter = new HideAppsAdapter(this, this.mGridView);
        this.mGridViewAdapter = hideAppsAdapter;
        hideAppsAdapter.setOnCheckStateChangedListener(this);
        this.mGridView.setAdapter((ListAdapter) this.mGridViewAdapter);
        this.mGridView.setOnItemClickListener(this.mGridViewAdapter);
        Button button = (Button) findViewById(R.id.footer_ok);
        this.mApplyButton = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.hideapps.HideAppsSettingActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                HideAppsSettingActivity.this.apply();
            }
        });
        this.mCheckCountText = (TextView) findViewById(R.id.launcher_toolbar_title_ex);
        updateCheckCountText();
    }

    protected void apply() {
        if (this.mSaving) {
            return;
        }
        LGUserLog.send(getApplicationContext(), LGUserLog.FEATURENAME_CHANGEHIDEAPPS);
        this.mSaving = true;
        new SaveTask().execute(new Void[0]);
        finish();
    }

    private class SaveTask extends AsyncTask<Void, Void, Integer> {
        private SaveTask() {
        }

        /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Integer doInBackground(Void... params) {
            LGLog.i(HideAppsSettingActivity.TAG, "Applying settings");
            Context applicationContext = HideAppsSettingActivity.this.getApplicationContext();
            List<HideAppItem> checkedItems = HideAppsSettingActivity.this.mGridViewAdapter.getCheckedItems();
            HideAppsStorage.deleteAll(applicationContext);
            HideAppsStorage.addItems(applicationContext, checkedItems);
            if (LGHomeFeature.isEnableDefaultHome()) {
                LauncherModel.deleteHideAppsFromDatabase(applicationContext, checkedItems);
            }
            AppFilterImpl.clearList();
            HideAppsSettingActivity.this.mSaving = false;
            return Integer.valueOf(checkedItems.size());
        }

        /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Integer savedCount) {
            Context applicationContext = HideAppsSettingActivity.this.getApplicationContext();
            super.onPostExecute(savedCount);
            if (!LGHomeFeature.isEnableDefaultHome()) {
                LauncherAppState.getInstance(applicationContext).setHideAppsCount(savedCount.intValue());
            } else {
                Toast.makeText(applicationContext, String.format(HideAppsSettingActivity.this.getResources().getString(R.string.hide_apps_saved_message), savedCount), 0).show();
            }
            LauncherAppState.getInstance(applicationContext).getModel().forceReload();
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCheckCountText() {
        this.mCheckCountText.setText(String.format(getResources().getString(R.string.hide_apps_selected_count), Integer.valueOf(this.mGridViewAdapter.getCheckedCount())));
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mGridViewAdapter.restoreState(savedInstanceState);
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.mGridViewAdapter.saveState(outState);
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
    }

    @Override // com.lge.launcher3.hideapps.HideAppsAdapter.OnCheckStateChangedListener
    public void onCheckStateChanged() {
        updateCheckCountText();
    }

    public void customzingDAB() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.launcher_toolbar);
        if (toolbar != null) {
            setActionBar(toolbar);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowTitleEnabled(false);
        }
        AppBarLayout appBarLayoutFindViewById = findViewById(R.id.launcher_app_bar);
        if (appBarLayoutFindViewById != null) {
            appBarLayoutFindViewById.setExpanded(false);
            appBarLayoutFindViewById.setAppBarTitle(getResources().getString(R.string.hide_apps_title));
        }
    }

    public boolean isFreeformMode(Activity activity) {
        int windowingMode;
        if (activity == null || !activity.isInMultiWindowMode()) {
            windowingMode = -1;
        } else {
            try {
                windowingMode = activity.getResources().getConfiguration().windowConfiguration.getWindowingMode();
            } catch (Exception e) {
                LGLog.e(TAG, "Exception occur while getting Window StackId.  e = " + e);
                return false;
            }
        }
        return windowingMode == 5;
    }
}
