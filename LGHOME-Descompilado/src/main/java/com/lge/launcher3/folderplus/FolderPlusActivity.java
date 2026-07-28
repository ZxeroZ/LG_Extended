package com.lge.launcher3.folderplus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toolbar;
import com.android.launcher3.ShortcutInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.folderplus.FolderPlusAdapter;
import com.lge.launcher3.hideapps.CheckableAppIcon;
import com.lge.launcher3.liveicon.LiveIcon;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.liveicon.OnLiveIconUpdateListener;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.lgdynamicactionbar.AppBarLayout;

/* JADX INFO: loaded from: classes.dex */
public class FolderPlusActivity extends Activity implements FolderPlusAdapter.OnCheckStateChangedListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "FolderPlus";
    private Button mApplyButton;
    private TextView mCheckCountText;
    protected boolean mCheckedAll;
    private GridView mGridView;
    private FolderPlusAdapter mGridViewAdapter;
    private LiveIconManager mLiveIconManager;
    private boolean mSaving;
    private boolean mSuppressFinish = false;
    private int mOldRequestedOrientation = -1;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.folderplus.FolderPlusActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (IntentConst.Action.ACTION_FINISH_FOLDERPLUS.getValue(context).equals(intent.getAction())) {
                FolderPlusActivity.this.finish();
            }
        }
    };
    private OnLiveIconUpdateListener mLiveIconUpdateListener = new OnLiveIconUpdateListener() { // from class: com.lge.launcher3.folderplus.FolderPlusActivity.2
        @Override // com.lge.launcher3.liveicon.OnLiveIconUpdateListener
        public void onLiveIconUpdate(final LiveIcon liveIcon) {
            new Handler().post(new Runnable() { // from class: com.lge.launcher3.folderplus.FolderPlusActivity.2.1
                @Override // java.lang.Runnable
                public void run() {
                    ComponentName componentName = liveIcon.getComponentName();
                    int childCount = FolderPlusActivity.this.mGridView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        CheckableAppIcon checkableAppIcon = (CheckableAppIcon) FolderPlusActivity.this.mGridView.getChildAt(i);
                        ShortcutInfo shortcutInfo = (ShortcutInfo) checkableAppIcon.getTag();
                        if (componentName.equals(shortcutInfo.getTargetComponent())) {
                            checkableAppIcon.setIcon(liveIcon.getBadgedIcon(shortcutInfo.user));
                        }
                    }
                }
            });
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(34210242);
        super.onCreate(savedInstanceState);
        LGLog.i(TAG, "Start FolderPlus");
        setTheme(R.style.DynamicActionBarTheme_NoActionBar);
        setContentView(R.layout.multiselect_appicon_main);
        customzingDAB();
        registerFinishReceiver();
        this.mOldRequestedOrientation = getRequestedOrientation();
        setRequestedOrientation();
        setupViews();
        LiveIconManager liveIconManager = LiveIconManager.getInstance(this);
        this.mLiveIconManager = liveIconManager;
        liveIconManager.registerOnLiveIconUpdateListener(this.mLiveIconUpdateListener);
    }

    private void setRequestedOrientation() {
        this.mSuppressFinish = getIntent().getIntExtra("folderOrientation", 1) != getResources().getConfiguration().orientation;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if (rotation == 0) {
            setRequestedOrientation(1);
            Resources resources = getResources();
            Configuration configuration = new Configuration(resources.getConfiguration());
            if (configuration.orientation == 2) {
                configuration.orientation = 1;
                resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                return;
            }
            return;
        }
        if (rotation == 1) {
            setRequestedOrientation(0);
        } else if (rotation == 2) {
            setRequestedOrientation(9);
        } else {
            if (rotation != 3) {
                return;
            }
            setRequestedOrientation(8);
        }
    }

    private void setupViews() {
        Intent intent = getIntent();
        long longExtra = intent.getLongExtra("folderId", -1L);
        boolean booleanExtra = intent.getBooleanExtra("isAllApps", false);
        this.mGridView = (GridView) findViewById(R.id.multiselect_apps_grid_view);
        FolderPlusAdapter folderPlusAdapter = new FolderPlusAdapter(this, this.mGridView, longExtra, booleanExtra);
        this.mGridViewAdapter = folderPlusAdapter;
        folderPlusAdapter.setOnCheckStateChangedListener(this);
        this.mGridView.setAdapter((ListAdapter) this.mGridViewAdapter);
        this.mGridView.setOnItemClickListener(this.mGridViewAdapter);
        Button button = (Button) findViewById(R.id.footer_ok);
        this.mApplyButton = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.folderplus.FolderPlusActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                FolderPlusActivity.this.apply();
            }
        });
        this.mCheckCountText = (TextView) findViewById(R.id.launcher_toolbar_title_ex);
        updateCheckCountText();
        this.mCheckedAll = this.mGridViewAdapter.getCheckedCount() != this.mGridViewAdapter.getTotalCount();
        if (!LGHomeFeature.isEnableDefaultHome() && !booleanExtra) {
            LGLog.d(TAG, "The description of FolderPlus dont't use in Workspace");
            return;
        }
        TextView textView = (TextView) findViewById(R.id.multiselect_description);
        textView.setText(R.string.folderplus_guide_text);
        textView.setVisibility(0);
    }

    protected void apply() {
        if (this.mSaving) {
            return;
        }
        LGUserLog.send(getApplicationContext(), LGUserLog.FEATURENAME_ADDFOLDERITEMBYPLUS);
        this.mSaving = true;
        this.mGridViewAdapter.apply();
        this.mGridView.setOnItemClickListener(null);
        setResult(LauncherConst.RESULT_OK_FOLDER_ACTIVITY);
        finish();
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!this.mSuppressFinish) {
            setResult(LauncherConst.RESULT_ABNORMAL_CLOSE_FOLDER);
            finish();
        } else {
            this.mSuppressFinish = false;
        }
    }

    private void updateCheckCountText() {
        this.mCheckCountText.setText(String.format(getResources().getString(R.string.folderplus_selected_count), Integer.valueOf(this.mGridViewAdapter.getCheckedCount())));
    }

    @Override // com.lge.launcher3.folderplus.FolderPlusAdapter.OnCheckStateChangedListener
    public void onCheckStateChanged() {
        updateCheckCountText();
    }

    @Override // android.app.Activity
    protected void onPause() {
        setRequestedOrientation(this.mOldRequestedOrientation);
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        unregisterFinishReceiver();
        this.mLiveIconManager.unregisterOnLiveIconUpdateListener(this.mLiveIconUpdateListener);
        LGLog.i(TAG, "End");
    }

    private void registerFinishReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentConst.Action.ACTION_FINISH_FOLDERPLUS.getValue(getBaseContext()));
        registerReceiver(this.mReceiver, intentFilter);
    }

    private void unregisterFinishReceiver() {
        unregisterReceiver(this.mReceiver);
    }

    public void customzingDAB() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.launcher_toolbar);
        if (toolbar != null) {
            setActionBar(toolbar);
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(false);
        }
        AppBarLayout appBarLayoutFindViewById = findViewById(R.id.launcher_app_bar);
        if (appBarLayoutFindViewById != null) {
            appBarLayoutFindViewById.setExpanded(true);
            appBarLayoutFindViewById.setAppBarExpandEnable(true);
            appBarLayoutFindViewById.setAppBarTitle(getResources().getString(R.string.sp_folderplus_title));
        }
    }
}
