package com.lge.launcher3.smartbulletin.setting;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;
import com.lge.content.pm.PackageManagerEx;
import com.lge.launcher3.R;
import com.lge.launcher3.homesettings.SBHomeDataBaseUtil;
import com.lge.launcher3.homesettings.SettingsSearchUtils;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.smartbulletin.lib.Action;
import com.lge.launcher3.smartbulletin.util.WidgetHelper;
import com.lge.launcher3.smartbulletin.view.SBStateManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.lgdynamicactionbar.AppBarLayout;
import com.mobeta.android.dslv.DragSortListView;

/* JADX INFO: loaded from: classes.dex */
public class SBSettingListActivity extends Activity {
    private static final String DIALOG_SHOWING = "dialog_showing";
    private static final String EXTRA_NEW_VALUE = "newValue";
    private static final String EXTRA_PERFORM = "perform";
    private static final String EXTRA_SEARCH_ITEM = "search_item";
    private static final String TAG = "SBSettingListActivity";
    private DragSortListView mDragSortListView = null;
    private SBSettingListAdapter mListAdapter = null;
    private WidgetHelper mWidgetHelper = null;
    private ViewGroup mSwitchView = null;
    private Switch mSwitch = null;
    private TextView mSwitchTextView = null;
    private Dialog mCurrentDialog = null;
    private Menu mCurrentMenu = null;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar;
        setTheme(R.style.DynamicActionBarTheme_NoActionBar);
        if (this.mWidgetHelper == null) {
            WidgetHelper widgetHelper = WidgetHelper.getInstance(this);
            this.mWidgetHelper = widgetHelper;
            widgetHelper.updatedSmartBulletinProvider(this);
        }
        if (Build.VERSION.SDK_INT > 15 && !LGHomeFeature.Config.FEATURE_USE_VZW_SIDESCREEN.getValue() && (actionBar = getActionBar()) != null) {
            actionBar.setDisplayOptions(9);
        }
        setContentView(R.layout.smartbulletin_setting_list);
        customzingDAB();
        super.onCreate(savedInstanceState);
        this.mDragSortListView = (DragSortListView) findViewById(R.id.smartbulletin_setting_list);
        if (getResources().getConfiguration().getLayoutDirection() == 1) {
            this.mDragSortListView.setDivider(getResources().getDrawable(R.drawable.listview_divider_background_with_checkbox_rtl, getTheme()));
        }
        SBSettingListAdapter sBSettingListAdapter = new SBSettingListAdapter(this);
        this.mListAdapter = sBSettingListAdapter;
        this.mDragSortListView.setAdapter((ListAdapter) sBSettingListAdapter);
        this.mDragSortListView.setDropListener(this.mListAdapter);
        this.mDragSortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lge.launcher3.smartbulletin.setting.SBSettingListActivity.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SBSettingListActivity.this.mListAdapter.onClick((Switch) view.findViewById(R.id.providerName));
            }
        });
        this.mSwitch = (Switch) findViewById(R.id.switch_widget);
        this.mSwitchTextView = (TextView) findViewById(R.id.switch_text);
        initSwitch(this.mSwitch);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.smartbulletin_switch);
        this.mSwitchView = viewGroup;
        if (viewGroup != null) {
            viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.smartbulletin.setting.SBSettingListActivity.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SBSettingListActivity.this.mSwitch.toggle();
                }
            });
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Dialog dialog = this.mCurrentDialog;
        outState.putBoolean(DIALOG_SHOWING, dialog != null && dialog.isShowing());
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(DIALOG_SHOWING)) {
                this.mSwitch.setChecked(false);
                dismissDialog();
                Dialog dialogCreateSBRemoveDialog = createSBRemoveDialog(this.mSwitch);
                this.mCurrentDialog = dialogCreateSBRemoveDialog;
                dialogCreateSBRemoveDialog.show();
                return;
            }
            this.mSwitch.setChecked(SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(getBaseContext()));
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (SBStateManager.getState() == SBStateManager.SBState.COLLAPSE) {
            getBaseContext().sendBroadcast(new Intent(Action.SMARTBULLETIN_ACTION_REQUEST_EXPAND));
        }
        if (this.mSwitch != null) {
            this.mSwitch.setChecked(SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(getBaseContext()));
        }
        Dialog dialog = this.mCurrentDialog;
        if (dialog == null || !dialog.isShowing()) {
            dismissDialog();
            Switch r0 = this.mSwitch;
            if (r0 != null) {
                initSwitch(r0);
            }
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        if (SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(getBaseContext())) {
            if (MemoryUtils.hasAvailableFileSystemMemory(this, false)) {
                this.mListAdapter.updateData(this);
            } else {
                LGLog.i(TAG, "SBSettingListActivity update failed");
            }
        }
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.mListAdapter.onDestroy(this);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        setResult(-1, new Intent());
        super.onBackPressed();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        switchFromSettingsSearch();
        this.mCurrentMenu = menu;
        getMenuInflater().inflate(R.menu.smartbulletin_setting_actions, menu);
        setButtonEnable(SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(getBaseContext()));
        return super.onCreateOptionsMenu(menu);
    }

    private int getEnabledItemNum() {
        return this.mListAdapter.getEnabledItemNum(getBaseContext());
    }

    private void initSwitch(Switch switchButton) {
        if (switchButton == null) {
            return;
        }
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lge.launcher3.smartbulletin.setting.SBSettingListActivity.3
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                SBSettingListActivity.this.setSwitchText(isChecked);
                if (SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(buttonView.getContext()) == isChecked) {
                    return;
                }
                if (isChecked) {
                    SBSettingListActivity.this.mSwitch.setChecked(true);
                    new Handler().postDelayed(new Runnable() { // from class: com.lge.launcher3.smartbulletin.setting.SBSettingListActivity.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            SBSettingListActivity.this.setButtonEnable(isChecked);
                            SBSettingListActivity.this.enableSmartBulletinInSBSetting(buttonView.getContext(), true);
                            SettingsSearchUtils.updateSmartBulletinOnOff(SBSettingListActivity.this.getApplicationContext(), true);
                        }
                    }, 300L);
                } else {
                    SBSettingListActivity.this.dismissDialog();
                    SBSettingListActivity sBSettingListActivity = SBSettingListActivity.this;
                    sBSettingListActivity.mCurrentDialog = sBSettingListActivity.createSBRemoveDialog(buttonView);
                    SBSettingListActivity.this.mCurrentDialog.show();
                }
            }
        });
        boolean zExistSmartBulletinItemInDataBase = SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(getBaseContext());
        switchButton.setChecked(zExistSmartBulletinItemInDataBase);
        setSwitchText(zExistSmartBulletinItemInDataBase);
        setButtonEnable(zExistSmartBulletinItemInDataBase);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSwitchText(boolean enable) {
        if (enable) {
            this.mSwitchTextView.setText(R.string.switch_on_text);
            this.mSwitchTextView.setTypeface(Typeface.DEFAULT, 1);
            this.mSwitchTextView.setTextColor(getResources().getColor(R.color.componet_text_color, getTheme()));
        } else {
            this.mSwitchTextView.setText(R.string.switch_off_text);
            this.mSwitchTextView.setTypeface(Typeface.DEFAULT, 0);
            this.mSwitchTextView.setTextColor(getResources().getColor(R.color.primary_text_default_material_light, getTheme()));
        }
    }

    private boolean isAvailablePackage(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 1);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setButtonEnable(boolean isChecked) {
        SBSettingListAdapter sBSettingListAdapter = this.mListAdapter;
        if (sBSettingListAdapter != null) {
            sBSettingListAdapter.setEnabled(isChecked);
        }
        DragSortListView dragSortListView = this.mDragSortListView;
        if (dragSortListView != null) {
            dragSortListView.setDragEnabled(isChecked);
        }
        if (this.mCurrentMenu == null) {
            return;
        }
        setDownloadableButtonEnable(isChecked);
    }

    private void setDownloadableButtonEnable(boolean isChecked) {
        MenuItem menuItemFindItem = this.mCurrentMenu.findItem(R.id.settings_downloadable);
        if (LGHomeFeature.Config.FEATURE_SUPPORT_SMARTBULLETIN_DOWNLOADABLE_PROVIDER.getValue() && isAvailablePackage("com.lge.lgworld")) {
            menuItemFindItem.setVisible(true);
            menuItemFindItem.setEnabled(isChecked);
            if (isChecked) {
                menuItemFindItem.getIcon().setAlpha(255);
                return;
            } else {
                menuItemFindItem.getIcon().setAlpha(128);
                return;
            }
        }
        menuItemFindItem.setVisible(false);
    }

    private boolean isInTrash() {
        try {
            String[] disabledByLGLauncherPackageList = PackageManagerEx.getDefault().getDisabledByLGLauncherPackageList(Process.myUserHandle().getIdentifier());
            if (disabledByLGLauncherPackageList != null) {
                for (String str : disabledByLGLauncherPackageList) {
                    if ("com.lge.lgworld".equals(str)) {
                        return true;
                    }
                }
            }
        } catch (RemoteException | NoClassDefFoundError e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WidgetHelper widgetHelper = this.mWidgetHelper;
        if (widgetHelper != null) {
            widgetHelper.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    private Dialog createInTrashDialog() {
        return new AlertDialog.Builder(this).setTitle(R.string.sp_etc_lgsmartworld_SHORT).setMessage(R.string.lg_market_deleted_temporary).setNegativeButton(R.string.smartbulletin_cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.smartbulletin_settings, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.smartbulletin.setting.SBSettingListActivity.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                try {
                    SBSettingListActivity.this.startActivity(new Intent("com.lge.launcher3.intent.action.SHOW_RECENTLY_UNINSTALLED_LIST"));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).create();
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_downloadable) {
            if (isInTrash()) {
                createInTrashDialog().show();
                return true;
            }
            Intent intent = new Intent("com.lge.lgworld.intent.action.VIEW");
            intent.putExtra("lgworld.receiver", "LGSW_INVOKE_COLLECTION");
            intent.putExtra("URL", "/mobile/APIs/LGWC/Business/getThemeList?idx=1&code=" + getResources().getString(R.string.smartbulletin_apps_collection_code));
            intent.putExtra("TITLE", "");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Dialog createSBRemoveDialog(final CompoundButton buttonView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.smartbulletin_remove_dialog, null);
        String string = getString(R.string.smartbulletin_remove);
        String string2 = getString(R.string.smartbulletin_cancel);
        builder.setView(linearLayout);
        builder.setPositiveButton(string, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.smartbulletin.setting.SBSettingListActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SBSettingListActivity.this.dismissDialog();
                if (!MemoryUtils.hasAvailableFileSystemMemory(SBSettingListActivity.this, true)) {
                    buttonView.setChecked(true);
                    return;
                }
                SBSettingListActivity.this.mListAdapter.updateData(SBSettingListActivity.this.getBaseContext());
                SBSettingListActivity.this.setButtonEnable(false);
                SBSettingListActivity.this.enableSmartBulletinInSBSetting(buttonView.getContext(), false);
                if (SBSettingListActivity.this.mSwitch != null) {
                    SBSettingListActivity.this.mSwitch.setChecked(false);
                }
                SettingsSearchUtils.updateSmartBulletinOnOff(SBSettingListActivity.this.getApplicationContext(), false);
            }
        });
        builder.setNegativeButton(string2, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.smartbulletin.setting.SBSettingListActivity.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SBSettingListActivity.this.dismissDialog();
                buttonView.setChecked(true);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.lge.launcher3.smartbulletin.setting.SBSettingListActivity.7
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                SBSettingListActivity.this.dismissDialog();
                buttonView.setChecked(true);
            }
        });
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissDialog() {
        Dialog dialog = this.mCurrentDialog;
        if (dialog != null) {
            dialog.dismiss();
            this.mCurrentDialog = null;
        }
    }

    private void switchFromSettingsSearch() {
        Intent intent = getIntent();
        if (intent.getStringExtra(EXTRA_SEARCH_ITEM) != null) {
            boolean booleanExtra = intent.getBooleanExtra(EXTRA_PERFORM, false);
            boolean booleanExtra2 = intent.getBooleanExtra(EXTRA_NEW_VALUE, false);
            if (booleanExtra) {
                setButtonEnable(booleanExtra2);
                enableSmartBulletinInSBSetting(getBaseContext(), booleanExtra2);
                SettingsSearchUtils.updateSmartBulletinOnOff(getApplicationContext(), booleanExtra2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableSmartBulletinInSBSetting(Context curContext, boolean enabled) {
        if (enabled) {
            SBHomeDataBaseUtil.turnOnSmartBulletin(curContext);
        } else {
            SBHomeDataBaseUtil.turnOffSmartBulletin(curContext);
        }
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
            appBarLayoutFindViewById.setAppBarTitle(getResources().getString(R.string.smartbulletin));
        }
    }
}
