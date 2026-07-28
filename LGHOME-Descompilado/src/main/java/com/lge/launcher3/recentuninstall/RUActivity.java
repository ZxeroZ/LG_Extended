package com.lge.launcher3.recentuninstall;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherModel.PackageUpdatedTask;
import com.lge.content.pm.PackageManagerEx;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.operator.GVNUtils;
import com.lge.launcher3.recentuninstall.RUProgressListManager;
import com.lge.launcher3.recentuninstall.RUReinstallAdapter;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.Utilities;
import com.lge.lgdynamicactionbar.AppBarLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class RUActivity extends FragmentActivity implements View.OnClickListener, RUReinstallAdapter.IRUReinstallCallback {
    public static final int MENU_RECENT_UNINSTALL_SELECT = 0;
    public static final int MENU_RECENT_UNINSTALL_SELECT_DONE = 1;
    private static final String TAG = "RUActivity";
    private MenuItem mActionBarSelect;
    ArrayAdapter<RUAppInfo> mAdapter;
    private TextView mAppBarTitle;
    RUDeleteAdapter mDeleteAdapter;
    TextView mEmptyTextView;
    ListView mListview;
    ArrayList<RUAppInfo> mRecentUninstallAppsList;
    private Button mUninstallAppBtnCancel;
    private Button mUninstallAppBtnOK;
    private TextView mUninstallAppCount;
    private String mUninstallAppCountFormat;
    private CheckBox mUninstallAppSelectAll;
    RECENT_UNINSTALL_MODE mCurState = RECENT_UNINSTALL_MODE.NORMAL;
    private AdapterView.OnItemClickListener mUninstallAppItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.lge.launcher3.recentuninstall.RUActivity.2
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (AnonymousClass7.$SwitchMap$com$lge$launcher3$recentuninstall$RUActivity$RECENT_UNINSTALL_MODE[RUActivity.this.mCurState.ordinal()] == 2 && (RUActivity.this.mAdapter instanceof RUDeleteAdapter)) {
                RUActivity rUActivity = RUActivity.this;
                rUActivity.mDeleteAdapter = (RUDeleteAdapter) rUActivity.mAdapter;
                RUActivity.this.mDeleteAdapter.setChecked(position);
                RUActivity.this.mDeleteAdapter.notifyDataSetChanged();
                RUActivity.this.mUninstallAppSelectAll.setChecked(RUActivity.this.mDeleteAdapter.getCount() == RUActivity.this.mDeleteAdapter.getSelectedCount());
                RUActivity.this.updateUninstallAppCount();
            }
        }
    };

    enum RECENT_UNINSTALL_MODE {
        NORMAL,
        SELECT_ALL
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        LGLog.d(TAG, "onCreate");
        setTheme(R.style.DynamicActionBarTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        initLayoutComponent();
        RUProgressListManager.getInstance(this).setPackageChangedCallback(new RUProgressListManager.PackageChangedCallback() { // from class: com.lge.launcher3.recentuninstall.RUActivity.1
            @Override // com.lge.launcher3.recentuninstall.RUProgressListManager.PackageChangedCallback
            public void onPackageRemoved(String packageName) {
                RUActivity.this.initLayoutComponent();
            }
        });
        LGUserLog.send(getBaseContext(), LGUserLog.FEATURENAME_SHOWRECENTLYUNINSTALLED);
        Utilities.adjustSystemBars(this);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setBackground();
        Utilities.adjustSystemBars(this);
    }

    private void refreshList() {
        String[] disabledByLGLauncherPackageList = new String[0];
        try {
            disabledByLGLauncherPackageList = PackageManagerEx.getDefault().getDisabledByLGLauncherPackageList(Process.myUserHandle().getIdentifier());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError unused) {
            LGLog.d(TAG, "Not implement PackageManagerEX in framework");
        }
        this.mRecentUninstallAppsList.clear();
        RUProgressListManager rUProgressListManager = RUProgressListManager.getInstance(this);
        PackageManager packageManager = getPackageManager();
        for (String str : disabledByLGLauncherPackageList) {
            if (!rUProgressListManager.contains(str)) {
                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
                    this.mAdapter.add(new RUAppInfo(applicationInfo.loadIcon(packageManager), applicationInfo.loadLabel(packageManager), str));
                } catch (PackageManager.NameNotFoundException e2) {
                    e2.printStackTrace();
                }
            }
        }
        setOptionMenuEnable(this.mAdapter.getCount() != 0);
        this.mEmptyTextView.setVisibility(this.mAdapter.getCount() == 0 ? 0 : 8);
        String str2 = TAG;
        LGLog.i(str2, String.format("refreshList() : packageNameList(%d %s)", Integer.valueOf(disabledByLGLauncherPackageList.length), Arrays.toString(disabledByLGLauncherPackageList)));
        LGLog.i(str2, String.format("refreshList() : packageNameList(%d %s)", Integer.valueOf(rUProgressListManager.getList().size()), rUProgressListManager.getList()));
    }

    public void customzingDAB() {
        ActionBar actionBar;
        Toolbar toolbar = (Toolbar) findViewById(R.id.launcher_toolbar);
        if (toolbar != null) {
            setActionBar(toolbar);
            actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
            }
        } else {
            actionBar = null;
        }
        AppBarLayout appBarLayoutFindViewById = findViewById(R.id.launcher_app_bar);
        if (appBarLayoutFindViewById != null) {
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
            int i = AnonymousClass7.$SwitchMap$com$lge$launcher3$recentuninstall$RUActivity$RECENT_UNINSTALL_MODE[this.mCurState.ordinal()];
            if (i == 1) {
                appBarLayoutFindViewById.setAppBarExpandEnable(true);
                appBarLayoutFindViewById.setExpanded(true);
                appBarLayoutFindViewById.setAppBarTitle(getResources().getString(R.string.app_trash_title));
                return;
            }
            if (i != 2) {
                return;
            }
            appBarLayoutFindViewById.setAppBarExpandEnable(false);
            appBarLayoutFindViewById.setExpanded(false);
            appBarLayoutFindViewById.setAppBarTitle(getResources().getString(R.string.iconchange_select_all));
            this.mUninstallAppCount = (TextView) findViewById(R.id.launcher_toolbar_title_ex);
            this.mUninstallAppSelectAll = (CheckBox) findViewById(R.id.launcher_toolbar_title_checkbox);
            this.mAppBarTitle = (TextView) findViewById(android.R.id.text1);
            try {
                this.mUninstallAppSelectAll.setTextSize(0, getResources().getDimension(34341166));
            } catch (NoClassDefFoundError unused) {
                LGLog.d(TAG, "Not implement lgapi.jar in framework");
            }
            this.mUninstallAppSelectAll.setOnClickListener(this);
            TextView textView = this.mAppBarTitle;
            if (textView != null) {
                textView.setOnClickListener(this);
            }
            initMultiSelectCmdBtn();
            this.mUninstallAppCount.setText(String.format(this.mUninstallAppCountFormat, 0));
            this.mUninstallAppBtnOK.setEnabled(false);
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.recentuninstall.RUActivity$7, reason: invalid class name */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$recentuninstall$RUActivity$RECENT_UNINSTALL_MODE;

        static {
            int[] iArr = new int[RECENT_UNINSTALL_MODE.values().length];
            $SwitchMap$com$lge$launcher3$recentuninstall$RUActivity$RECENT_UNINSTALL_MODE = iArr;
            try {
                iArr[RECENT_UNINSTALL_MODE.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$recentuninstall$RUActivity$RECENT_UNINSTALL_MODE[RECENT_UNINSTALL_MODE.SELECT_ALL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public void initLayoutComponent() {
        this.mRecentUninstallAppsList = new ArrayList<>();
        this.mUninstallAppCountFormat = getResources().getString(R.string.recently_uninstalled_selected_count);
        int i = AnonymousClass7.$SwitchMap$com$lge$launcher3$recentuninstall$RUActivity$RECENT_UNINSTALL_MODE[this.mCurState.ordinal()];
        if (i == 1) {
            setContentView(R.layout.recent_uninstall_list_view);
            this.mListview = (ListView) findViewById(R.id.recent_unisntall_list);
            if (getResources().getConfiguration().getLayoutDirection() == 1) {
                this.mListview.setDivider(getResources().getDrawable(R.drawable.listview_divider_background_with_icon_rtl, getTheme()));
            }
            this.mEmptyTextView = (TextView) findViewById(R.id.recent_uninstall_no_apps_message);
            this.mListview.setFocusable(false);
            this.mAdapter = new RUReinstallAdapter(this, this, R.layout.recent_uninstall_list_item, this.mRecentUninstallAppsList);
            refreshList();
        } else if (i == 2) {
            setContentView(R.layout.recent_uninstall_list_select_view);
            this.mListview = (ListView) findViewById(R.id.recent_unisntall_list);
            if (getResources().getConfiguration().getLayoutDirection() == 1) {
                this.mListview.setDivider(getResources().getDrawable(R.drawable.listview_divider_background_with_icon_rtl, getTheme()));
            }
            this.mListview.setFocusable(true);
            this.mListview.setOnItemClickListener(this.mUninstallAppItemClickListener);
            this.mAdapter = new RUDeleteAdapter(this, this, R.layout.recent_uninstall_list_select_item, this.mRecentUninstallAppsList);
            refreshList();
        }
        customzingDAB();
        setBackground();
        this.mListview.setAdapter((ListAdapter) this.mAdapter);
    }

    public void refreshItems() {
        this.mAdapter.notifyDataSetChanged();
    }

    public void initMultiSelectActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.recent_uninstall_multi_select_action_bar);
        actionBar.setDisplayOptions(16, 16);
        this.mUninstallAppSelectAll = (CheckBox) actionBar.getCustomView().findViewById(R.id.action_bar_select_checkBox);
        this.mUninstallAppCount = (TextView) actionBar.getCustomView().findViewById(R.id.action_bar_select_item_num);
        try {
            this.mUninstallAppSelectAll.setTextSize(0, getResources().getDimension(34341166));
        } catch (NoClassDefFoundError unused) {
            LGLog.d(TAG, "Not implement lgapi.jar in framework");
        }
        this.mUninstallAppSelectAll.setOnClickListener(this);
        initMultiSelectCmdBtn();
        this.mUninstallAppCount.setText(String.format(this.mUninstallAppCountFormat, 0));
        this.mUninstallAppBtnOK.setEnabled(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUninstallAppCount() {
        ArrayAdapter<RUAppInfo> arrayAdapter = this.mAdapter;
        if (arrayAdapter instanceof RUDeleteAdapter) {
            RUDeleteAdapter rUDeleteAdapter = (RUDeleteAdapter) arrayAdapter;
            this.mDeleteAdapter = rUDeleteAdapter;
            int selectedCount = rUDeleteAdapter.getSelectedCount();
            this.mUninstallAppCount.setText(String.format(this.mUninstallAppCountFormat, Integer.valueOf(selectedCount)));
            this.mUninstallAppBtnOK.setEnabled(selectedCount > 0);
        }
    }

    public void updateMultiSelectActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.recent_uninstall_multi_select_action_bar);
        actionBar.setDisplayOptions(16, 16);
        TextView textView = (TextView) actionBar.getCustomView().findViewById(R.id.action_bar_select_item_num);
        Iterator<RUAppInfo> it = this.mRecentUninstallAppsList.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().isSelected()) {
                i++;
            }
        }
        textView.setText(i);
        CheckBox checkBox = (CheckBox) actionBar.getCustomView().findViewById(R.id.action_bar_select_checkBox);
        if (this.mRecentUninstallAppsList.size() == i) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }

    public void initMultiSelectCmdBtn() {
        setOptionMenuEnable(false);
        Button button = (Button) findViewById(R.id.cancel_btn);
        this.mUninstallAppBtnCancel = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.recentuninstall.RUActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                RUActivity.this.mCurState = RECENT_UNINSTALL_MODE.NORMAL;
                RUActivity.this.mActionBarSelect.setVisible(true);
                RUActivity.this.initLayoutComponent();
            }
        });
        Button button2 = (Button) findViewById(R.id.uninstall_btn);
        this.mUninstallAppBtnOK = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.recentuninstall.RUActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                RUActivity.this.showDeleteConfirmDialog();
            }
        });
    }

    void startUninstallProgress() {
        UserHandle userHandleMyUserHandle = Process.myUserHandle();
        IPackageManager iPackageManagerAsInterface = IPackageManager.Stub.asInterface(ServiceManager.getService(AppNotifierManager.ExtraSpec.USAGE_PACKAGE));
        for (RUAppInfo rUAppInfo : this.mRecentUninstallAppsList) {
            if (rUAppInfo.isSelected()) {
                String packageName = rUAppInfo.getPackageName();
                LGLog.i(TAG, String.format("startUninstallProgress() : packageName(%s)", packageName));
                RUProgressListManager.getInstance(this).addUninstallProgress(packageName);
                try {
                    iPackageManagerAsInterface.deletePackageAsUser(packageName, -1, (IPackageDeleteObserver) null, userHandleMyUserHandle.getIdentifier(), 0);
                } catch (RemoteException e) {
                    LGLog.e(TAG, "Failed to talk to package manager", e);
                }
            }
        }
        if (Utilities.isLGUI10_0()) {
            return;
        }
        Toast.makeText(getBaseContext(), R.string.app_trash_delete_done_text, 0).show();
    }

    @Override // com.lge.launcher3.recentuninstall.RUReinstallAdapter.IRUReinstallCallback
    public void startEnableProgress(String packageName) {
        LGLog.i(TAG, "Enable Package: " + packageName);
        try {
            getBaseContext().getPackageManager().setApplicationEnabledSetting(packageName, 1, 0);
            String[] strArr = {packageName};
            Handler handler = new Handler(LauncherModel.getWorkerLooper());
            if (LauncherAppState.getInstanceNoCreate() != null) {
                LauncherModel model = LauncherAppState.getInstance(getApplicationContext()).getModel();
                Objects.requireNonNull(model);
                handler.post(model.new PackageUpdatedTask(2, strArr, Process.myUserHandle()));
            }
            if (Utilities.isLGUI10_0()) {
                return;
            }
            Toast.makeText(getBaseContext(), R.string.app_trash_restore_done_text, 0).show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItemAdd = menu.add(0, 0, 0, R.string.iconchange_option_menu_delete_button);
        this.mActionBarSelect = menuItemAdd;
        menuItemAdd.setShowAsAction(2);
        setOptionMenuEnable(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.mActionBarSelect.setIcon(R.drawable.btn_trash_tint);
        if (this.mCurState == RECENT_UNINSTALL_MODE.NORMAL && this.mAdapter.getCount() > 0) {
            setOptionMenuEnable(true);
        } else {
            setOptionMenuEnable(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 0) {
            if (this.mRecentUninstallAppsList.size() == 1) {
                showDeleteConfirmDialog();
            } else {
                this.mCurState = RECENT_UNINSTALL_MODE.SELECT_ALL;
                this.mActionBarSelect.setVisible(false);
                initLayoutComponent();
            }
            return true;
        }
        if (itemId == 16908332) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // com.lge.launcher3.recentuninstall.RUReinstallAdapter.IRUReinstallCallback
    public void setOptionMenuEnable(boolean enable) {
        LGLog.i(TAG, "enable = " + enable);
        MenuItem menuItem = this.mActionBarSelect;
        if (menuItem != null) {
            menuItem.setVisible(enable);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (AnonymousClass7.$SwitchMap$com$lge$launcher3$recentuninstall$RUActivity$RECENT_UNINSTALL_MODE[this.mCurState.ordinal()] != 2) {
            return;
        }
        if (v.equals(this.mUninstallAppSelectAll) || v.equals(this.mAppBarTitle)) {
            ArrayAdapter<RUAppInfo> arrayAdapter = this.mAdapter;
            if (arrayAdapter instanceof RUDeleteAdapter) {
                RUDeleteAdapter rUDeleteAdapter = (RUDeleteAdapter) arrayAdapter;
                this.mDeleteAdapter = rUDeleteAdapter;
                boolean z = rUDeleteAdapter.getCount() != this.mDeleteAdapter.getSelectedCount();
                this.mUninstallAppSelectAll.setChecked(z);
                this.mDeleteAdapter.setSelectAll(z);
                this.mDeleteAdapter.notifyDataSetChanged();
                updateUninstallAppCount();
            }
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        int i = AnonymousClass7.$SwitchMap$com$lge$launcher3$recentuninstall$RUActivity$RECENT_UNINSTALL_MODE[this.mCurState.ordinal()];
        if (i == 1) {
            super.onBackPressed();
            return;
        }
        if (i != 2) {
            return;
        }
        this.mCurState = RECENT_UNINSTALL_MODE.NORMAL;
        initLayoutComponent();
        MenuItem menuItem = this.mActionBarSelect;
        if (menuItem != null) {
            menuItem.setVisible(true);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        LGLog.d(TAG, "onDestroy");
        super.onDestroy();
        RUProgressListManager.getInstance(this).setPackageChangedCallback(null);
        this.mUninstallAppSelectAll = null;
        this.mUninstallAppCount = null;
        this.mAppBarTitle = null;
        this.mUninstallAppCountFormat = null;
        this.mUninstallAppBtnCancel = null;
        this.mUninstallAppBtnOK = null;
        this.mListview = null;
        ArrayAdapter<RUAppInfo> arrayAdapter = this.mAdapter;
        if (arrayAdapter != null) {
            arrayAdapter.clear();
            this.mAdapter = null;
        }
        RUDeleteAdapter rUDeleteAdapter = this.mDeleteAdapter;
        if (rUDeleteAdapter != null) {
            rUDeleteAdapter.clear();
            this.mDeleteAdapter = null;
        }
        ArrayList<RUAppInfo> arrayList = this.mRecentUninstallAppsList;
        if (arrayList != null) {
            arrayList.clear();
            this.mRecentUninstallAppsList = null;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.mCurState == RECENT_UNINSTALL_MODE.NORMAL) {
            refreshList();
        }
        updateUninstallAppCount();
    }

    @Override // com.lge.launcher3.recentuninstall.RUReinstallAdapter.IRUReinstallCallback
    public TextView getEmptyText() {
        return this.mEmptyTextView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this).setMessage(this.mRecentUninstallAppsList.size() == 1 ? R.string.app_trash_ask_delete_app_confirm_message : R.string.app_trash_ask_delete_apps_confirm_message).setPositiveButton(R.string.app_trash_delete_btn_text, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.recentuninstall.RUActivity.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int id) {
                if (RUActivity.this.mRecentUninstallAppsList.size() == 1) {
                    RUActivity.this.mRecentUninstallAppsList.get(0).setSelected(true);
                }
                RUActivity.this.startUninstallProgress();
                RUActivity.this.mCurState = RECENT_UNINSTALL_MODE.NORMAL;
                RUActivity.this.mActionBarSelect.setVisible(true);
                RUActivity.this.initLayoutComponent();
            }
        }).setNegativeButton(R.string.app_trash_cancel_btn_text, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.recentuninstall.RUActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int id) {
            }
        }).create().show();
    }

    private void setBackground() {
        if (GVNUtils.isGiovanna(getBaseContext())) {
            findViewById(android.R.id.content).setBackground(getDrawable(R.drawable.gvn_recently_uninstalled_apps_bg));
            return;
        }
        Window window = getWindow();
        Drawable drawable = obtainStyledAttributes(new int[]{android.R.attr.windowBackground}).getDrawable(0);
        if (window == null || drawable == null) {
            return;
        }
        window.setBackgroundDrawable(drawable);
    }
}
