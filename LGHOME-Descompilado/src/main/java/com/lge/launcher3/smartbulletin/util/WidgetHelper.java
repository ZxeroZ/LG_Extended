package com.lge.launcher3.smartbulletin.util;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.launcher3.LauncherSettings;
import com.lge.launcher3.R;
import com.lge.launcher3.homesettings.SmartBulletinAction;
import com.lge.launcher3.smartbulletin.info.SBAppWidgetProviderInfo;
import com.lge.launcher3.smartbulletin.info.SBDefaultProviderInfo;
import com.lge.launcher3.smartbulletin.info.SBExcludeProviderInfo;
import com.lge.launcher3.smartbulletin.lib.Action;
import com.lge.launcher3.smartbulletin.log.SBLog;
import com.lge.launcher3.smartbulletin.provider.SBContentObserver;
import com.lge.launcher3.smartbulletin.provider.SBContract;
import com.lge.launcher3.smartbulletin.view.SBProviderLayout;
import com.lge.launcher3.smartbulletin.view.SBStateManager;
import com.lge.launcher3.smartbulletin.widgetlibrary.MyAppWidgetHost;
import com.lge.launcher3.smartbulletin.widgetlibrary.MyAppWidgetHostView;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WidgetHelper extends SBContentObserver {
    public static final int REQUEST_BIND_APPWIDGET = 1003;
    public static final int REQUEST_CREATE_APPWIDGET = 1002;
    public static final int REQUEST_REMOVE_APPWIDGET = 1004;
    public static final int REQUEST_SELECT_APPWIDGET = 1001;
    private static final String TAG = "WidgetHelper";
    private static WidgetHelper sWidgetHelper;
    private MyAppWidgetHost mAppWidgetHost;
    private int mAppWidgetId;
    private AppWidgetManager mAppWidgetManager;
    private AppWidgetProviderInfo mAppWidgetProviderInfo;
    private Context mContext;
    private ArrayList<SBExcludeProviderInfo> mExcludeProviderInfos;
    private List<SBAppWidgetProviderInfo> mProviders;
    private int sCurOrder;
    private int sProviderBgColor;
    private int sProviderTitleBgColor;
    private int sProviderTitleColor;

    public static WidgetHelper getInstance(Context context) {
        if (sWidgetHelper == null) {
            sWidgetHelper = new WidgetHelper(context);
        }
        return sWidgetHelper;
    }

    public static void onDestroy() {
        WidgetHelper widgetHelper = sWidgetHelper;
        if (widgetHelper == null) {
            SBLog.i(TAG, "sWidgetHelper is null");
            return;
        }
        SBLog.i(TAG, "sWidgetHelper.mAppWidgetHost = " + widgetHelper.mAppWidgetHost);
        MyAppWidgetHost myAppWidgetHost = sWidgetHelper.mAppWidgetHost;
        if (myAppWidgetHost != null) {
            myAppWidgetHost.stopListening();
        }
        WidgetHelper widgetHelper2 = sWidgetHelper;
        widgetHelper2.mProviders = null;
        widgetHelper2.releaseContentObserver();
        sWidgetHelper = null;
    }

    private WidgetHelper(Context context) {
        super(new Handler());
        this.mProviders = null;
        this.mContext = null;
        this.sCurOrder = 0;
        this.mContext = context;
        if (this.mAppWidgetHost == null) {
            this.mAppWidgetManager = AppWidgetManager.getInstance(context);
            MyAppWidgetHost myAppWidgetHost = new MyAppWidgetHost(context, 1025);
            this.mAppWidgetHost = myAppWidgetHost;
            myAppWidgetHost.setInteractionHandler(new SBInteractionHandler(context));
            try {
                this.mAppWidgetHost.startListening();
                this.mAppWidgetHost.setOnProvidersChangedListener(new MyAppWidgetHost.OnProvidersChangedListener() { // from class: com.lge.launcher3.smartbulletin.util.WidgetHelper.1
                    @Override // com.lge.launcher3.smartbulletin.widgetlibrary.MyAppWidgetHost.OnProvidersChangedListener
                    public void onProvidersChanged() {
                        SBLog.i(WidgetHelper.TAG, "onProvidersChanged");
                        WidgetHelper widgetHelper = WidgetHelper.this;
                        widgetHelper.updatedSmartBulletinProvider(widgetHelper.mContext);
                    }
                });
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        registerObserver(context);
        this.mProviders = SBContract.SmartBulletin.getAllProvider(context);
        this.sProviderBgColor = context.getResources().getColor(R.color.smartbulletin_default_bg_color, null);
        this.sProviderTitleBgColor = context.getResources().getColor(R.color.smartbulletin_default_title_bg_color, null);
        this.sProviderTitleColor = context.getResources().getColor(R.color.smartbulletin_default_title_color, null);
        this.mExcludeProviderInfos = SBExcludeProviderList.loadExcludeProviderListFromXml(context);
        deleteExcludedWidgetIds();
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        int i;
        String str = TAG;
        SBLog.d(str, "onActivityResult() : resultCode:" + resultCode + " requestCode:" + requestCode);
        if (resultCode == 0) {
            if (requestCode != 1002 || (i = this.mAppWidgetId) == -1 || this.mAppWidgetProviderInfo == null) {
                return;
            }
            this.mAppWidgetHost.deleteAppWidgetId(i);
            initWidgetInfo();
        }
        switch (requestCode) {
            case 1001:
                AppWidgetProviderInfo appWidgetProviderInfo = (AppWidgetProviderInfo) data.getParcelableExtra("widget_info");
                if (contain(this.mProviders, appWidgetProviderInfo)) {
                    Toast.makeText(this.mContext, "Widget already exists", 0).show();
                } else {
                    int iBindWidget = bindWidget(context, appWidgetProviderInfo);
                    if (iBindWidget != -1) {
                        insertDatabase(appWidgetProviderInfo, iBindWidget, true, -1);
                        initWidgetInfo();
                    }
                }
                break;
            case 1002:
                if (data.getExtras().getInt("appWidgetId", -1) != -1) {
                    insertDatabase(this.mAppWidgetProviderInfo, this.mAppWidgetId, true, -1);
                    initWidgetInfo();
                }
                break;
            case 1003:
                insertDatabase(this.mAppWidgetProviderInfo, this.mAppWidgetId, true, -1);
                break;
            case 1004:
                removeWidget(data.getIntegerArrayListExtra("remove_widgetId_list"));
                break;
            default:
                SBLog.i(str, "onActivityResult() : default requestCode(" + requestCode);
                break;
        }
    }

    private void removeWidget(List<Integer> removeList) {
        Toast toastMakeText;
        try {
            synchronized (this.mProviders) {
                for (Integer num : removeList) {
                    for (SBAppWidgetProviderInfo sBAppWidgetProviderInfo : this.mProviders) {
                        if (num.intValue() == sBAppWidgetProviderInfo.mWidgetId) {
                            SBContract.SmartBulletin.deleteById(this.mContext, sBAppWidgetProviderInfo.mDatabaseId);
                            this.mAppWidgetHost.deleteAppWidgetId(sBAppWidgetProviderInfo.mWidgetId);
                        }
                    }
                }
            }
            if (removeList != null && removeList.size() > 0 && (toastMakeText = Toast.makeText(this.mContext, R.string.smartbulletin_removed, 0)) != null) {
                toastMakeText.show();
            }
            this.mProviders = SBContract.SmartBulletin.getAllProvider(this.mContext);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public int bindWidget(Context context, AppWidgetProviderInfo info) {
        int iAllocateAppWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        this.mAppWidgetId = iAllocateAppWidgetId;
        this.mAppWidgetProviderInfo = info;
        if (this.mAppWidgetManager.bindAppWidgetIdIfAllowed(iAllocateAppWidgetId, info.provider)) {
            if (this.mAppWidgetProviderInfo.configure == null) {
                return this.mAppWidgetId;
            }
            Intent intent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
            intent.setComponent(this.mAppWidgetProviderInfo.configure);
            intent.putExtra("appWidgetId", this.mAppWidgetId);
            ((Activity) context).startActivityForResult(intent, 1002, null);
            return -1;
        }
        Intent intent2 = new Intent("android.appwidget.action.APPWIDGET_BIND");
        intent2.putExtra("appWidgetId", this.mAppWidgetId);
        intent2.putExtra(LauncherSettings.Favorites.APPWIDGET_PROVIDER, this.mAppWidgetProviderInfo.provider);
        ((Activity) context).startActivityForResult(intent2, 1003, null);
        return -1;
    }

    public void insertDatabase(AppWidgetProviderInfo info, int appWidgetId, boolean isEnable, int positionY) {
        SBContract.SmartBulletin.insertDatabase(this.mContext, info, appWidgetId, isEnable, positionY);
        SmartBulletinAction.sendProviderEnabled(this.mContext, isEnable, info.provider);
        this.mProviders = SBContract.SmartBulletin.getAllProvider(this.mContext);
    }

    public AppWidgetManager getAppWidgetManager() {
        return this.mAppWidgetManager;
    }

    public AppWidgetHost getAppWidgetHost() {
        return this.mAppWidgetHost;
    }

    public void createHostView(Context context, SBAppWidgetProviderInfo info, ViewGroup parent) {
        int i = info.mWidgetId;
        AppWidgetProviderInfo appWidgetProviderInfo = info.mAppWidgetProviderInfo;
        MyAppWidgetHostView myAppWidgetHostView = (MyAppWidgetHostView) getAppWidgetHost().createView(context, i, appWidgetProviderInfo);
        if (myAppWidgetHostView != null) {
            int dimension = (int) this.mContext.getResources().getDimension(R.dimen.smartbulletin_provider_padding);
            myAppWidgetHostView.setPadding(dimension, dimension, dimension, dimension);
            if ((appWidgetProviderInfo.widgetCategory & 256) == 256) {
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -2);
                LinearLayout linearLayoutMakeChildView = makeChildView(context, info, parent);
                linearLayoutMakeChildView.addView(myAppWidgetHostView, layoutParams);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setId(R.id.dummyview_index);
                linearLayoutMakeChildView.addView(linearLayout);
                parent.addView(linearLayoutMakeChildView, -1, -2);
                linearLayoutMakeChildView.setTag(new SBAppWidgetProviderInfo(appWidgetProviderInfo));
                return;
            }
            if ((appWidgetProviderInfo.widgetCategory & 1) == 1) {
                SBLog.w(TAG, "We can't support this widget(widgetCategory: " + appWidgetProviderInfo.widgetCategory + ", provider: " + appWidgetProviderInfo.provider);
                return;
            }
            SBLog.w(TAG, "We can't handle this widget(info: " + info.mAppWidgetProviderInfo.provider);
        }
    }

    private LinearLayout makeChildView(Context context, SBAppWidgetProviderInfo info, ViewGroup parent) {
        SBProviderLayout sBProviderLayout;
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(context);
        if (WidgetUtil.getSmartBulletinType(context, info.mAppWidgetProviderInfo) == 1) {
            sBProviderLayout = (SBProviderLayout) layoutInflaterFrom.inflate(R.layout.smartbulletin_provider_layout_disney, parent, false);
        } else {
            sBProviderLayout = (SBProviderLayout) layoutInflaterFrom.inflate(R.layout.smartbulletin_provider_layout, parent, false);
        }
        sBProviderLayout.setProviderBgColor(this.sProviderBgColor);
        sBProviderLayout.setProviderTitleColor(this.sProviderTitleColor);
        sBProviderLayout.setProviderTitleBgColor(this.sProviderTitleBgColor);
        sBProviderLayout.setProviderInfo(info);
        return sBProviderLayout;
    }

    public void updatedSmartBulletinProvider(Context context) {
        try {
            synchronized (this.mProviders) {
                if (SBStateManager.getState() == SBStateManager.SBState.COLLAPSE) {
                    context.sendBroadcast(new Intent(Action.SMARTBULLETIN_ACTION_REQUEST_EXPAND));
                }
                removeInvalidProviders(context);
                loadProviderList(context);
                addInstalledSmartBulletinProviders(context);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void loadProviderList(Context context) {
        try {
            synchronized (this.mProviders) {
                if (this.mProviders.size() == 0) {
                    onAppWidgetReset();
                    loadDefaultProviderList();
                    this.mProviders = SBContract.SmartBulletin.getAllProvider(context);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void removeInvalidProviders(Context context) {
        SBContract.SmartBulletin.removeInvalidProviders(context);
        this.mProviders = SBContract.SmartBulletin.getAllProvider(context);
    }

    private void removeExcludeProviders(Context context) {
        try {
            synchronized (this.mProviders) {
                ArrayList<SBExcludeProviderInfo> arrayListLoadExcludeProviderListFromXml = SBExcludeProviderList.loadExcludeProviderListFromXml(context);
                if (arrayListLoadExcludeProviderListFromXml != null) {
                    for (int size = this.mProviders.size() - 1; size >= 0; size--) {
                        SBAppWidgetProviderInfo sBAppWidgetProviderInfo = this.mProviders.get(size);
                        if (isExcludeProviderList(arrayListLoadExcludeProviderListFromXml, sBAppWidgetProviderInfo)) {
                            SBContract.SmartBulletin.deleteById(this.mContext, sBAppWidgetProviderInfo.mDatabaseId);
                        }
                    }
                }
            }
            this.mProviders = SBContract.SmartBulletin.getAllProvider(context);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private List<AppWidgetProviderInfo> getSBListProviders() {
        ArrayList arrayList = new ArrayList();
        for (AppWidgetProviderInfo appWidgetProviderInfo : getAppWidgetManager().getInstalledProviders(256)) {
            int smartBulletinType = WidgetUtil.getSmartBulletinType(this.mContext, appWidgetProviderInfo);
            if (smartBulletinType != this.mContext.getResources().getInteger(R.integer.smartbulletin_type)) {
                SBLog.i(TAG, "skip provider: " + appWidgetProviderInfo.provider.getPackageName() + ", com.lge.smartbulletin.type = " + smartBulletinType);
            } else if (!isExcludeProvider(appWidgetProviderInfo)) {
                arrayList.add(appWidgetProviderInfo);
            }
        }
        return arrayList;
    }

    private void deleteExcludedWidgetIds() {
        new Thread(new Runnable() { // from class: com.lge.launcher3.smartbulletin.util.WidgetHelper.2
            @Override // java.lang.Runnable
            public void run() {
                LGLog.d(WidgetHelper.TAG, "deleteIdExcludedWidgets start");
                int i = 0;
                for (int i2 : WidgetHelper.this.getAppWidgetHost().getAppWidgetIds()) {
                    if (i2 != -1) {
                        if (WidgetHelper.this.isExcludeProvider(WidgetHelper.this.getAppWidgetManager().getAppWidgetInfo(i2))) {
                            WidgetHelper.this.mAppWidgetHost.deleteAppWidgetId(i2);
                            i++;
                        }
                    }
                }
                LGLog.d(WidgetHelper.TAG, "deleteIdExcludedWidgets count = " + i);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isExcludeProvider(AppWidgetProviderInfo installedProvider) {
        for (SBExcludeProviderInfo sBExcludeProviderInfo : this.mExcludeProviderInfos) {
            if (sBExcludeProviderInfo.mPakageName == null || sBExcludeProviderInfo.mClassName == null || installedProvider == null) {
                break;
            }
            if (installedProvider.provider.equals(new ComponentName(sBExcludeProviderInfo.mPakageName, sBExcludeProviderInfo.mClassName))) {
                return true;
            }
        }
        return false;
    }

    private boolean isExcludeProviderList(ArrayList<SBExcludeProviderInfo> providerList, SBAppWidgetProviderInfo sbInfo) {
        for (SBExcludeProviderInfo sBExcludeProviderInfo : providerList) {
            if (sBExcludeProviderInfo.mPakageName == null || sBExcludeProviderInfo.mClassName == null) {
                break;
            }
            if (sbInfo.getComponentName().equals(new ComponentName(sBExcludeProviderInfo.mPakageName, sBExcludeProviderInfo.mClassName))) {
                return true;
            }
        }
        return false;
    }

    private void addInstalledSmartBulletinProviders(Context context) {
        addInstalledSmartBulletinProvider(context);
        this.mProviders = SBContract.SmartBulletin.getAllProvider(context);
    }

    private void addInstalledSmartBulletinProvider(Context context) {
        List<AppWidgetProviderInfo> sBListProviders = getSBListProviders();
        for (AppWidgetProviderInfo appWidgetProviderInfo : sBListProviders) {
            if ((appWidgetProviderInfo.widgetCategory & 256) != 256) {
                SBLog.w(TAG, "awpInfo.widgetCategory = " + appWidgetProviderInfo.widgetCategory + ", awpInfo.provider = " + appWidgetProviderInfo.provider);
            }
        }
        int integer = context.getResources().getInteger(R.integer.smartbulletin_limited_number);
        for (AppWidgetProviderInfo appWidgetProviderInfo2 : sBListProviders) {
            if (!contain(this.mProviders, appWidgetProviderInfo2)) {
                if (getEnabledItemNum() >= integer) {
                    insertProvider(context, appWidgetProviderInfo2, false);
                } else {
                    insertProvider(context, appWidgetProviderInfo2, true);
                }
            }
        }
    }

    private int getEnabledItemNum() {
        int i = 0;
        try {
            Iterator<SBAppWidgetProviderInfo> it = this.mProviders.iterator();
            while (it.hasNext()) {
                if (it.next().mIsEnabled) {
                    i++;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return i;
    }

    public boolean contain(List<SBAppWidgetProviderInfo> providers, AppWidgetProviderInfo appWidgetInfo) {
        try {
            Iterator<SBAppWidgetProviderInfo> it = providers.iterator();
            while (it.hasNext()) {
                if (it.next().mAppWidgetProviderInfo.provider.equals(appWidgetInfo.provider)) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange) {
        try {
            synchronized (this.mProviders) {
                super.onChange(selfChange);
                loadProviderList(this.mContext);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void onAppWidgetReset() {
        MyAppWidgetHost myAppWidgetHost = this.mAppWidgetHost;
        if (myAppWidgetHost != null) {
            myAppWidgetHost.deleteHost();
            this.mAppWidgetHost.startListening();
        }
    }

    private void loadDefaultProviderList() {
        ArrayList<SBAppWidgetProviderInfo> defaultProviderInfo = getDefaultProviderInfo();
        int integer = this.mContext.getResources().getInteger(R.integer.smartbulletin_limited_number);
        int i = 0;
        for (SBAppWidgetProviderInfo sBAppWidgetProviderInfo : defaultProviderInfo) {
            if (i >= integer) {
                sBAppWidgetProviderInfo.mIsEnabled = false;
            }
            insertProvider(this.mContext, sBAppWidgetProviderInfo.mAppWidgetProviderInfo, sBAppWidgetProviderInfo.mIsEnabled);
            i++;
        }
    }

    private ArrayList<SBAppWidgetProviderInfo> getDefaultProviderInfo() {
        List<AppWidgetProviderInfo> sBListProviders = getSBListProviders();
        for (AppWidgetProviderInfo appWidgetProviderInfo : sBListProviders) {
            if ((appWidgetProviderInfo.widgetCategory & 256) != 256) {
                SBLog.w(TAG, "awpInfo.widgetCategory = " + appWidgetProviderInfo.widgetCategory + ", awpInfo.provider = " + appWidgetProviderInfo.provider);
            }
        }
        List<AppWidgetProviderInfo> installedProviders = getAppWidgetManager().getInstalledProviders();
        ArrayList<SBDefaultProviderInfo> arrayListLoadProviderListFromXml = SBDefaultProviderList.loadProviderListFromXml(this.mContext);
        ArrayList<SBAppWidgetProviderInfo> arrayList = new ArrayList<>();
        for (SBDefaultProviderInfo sBDefaultProviderInfo : arrayListLoadProviderListFromXml) {
            ComponentName componentName = new ComponentName(sBDefaultProviderInfo.mPakageName, sBDefaultProviderInfo.mClassName);
            AppWidgetProviderInfo providerInfo = getProviderInfo(sBListProviders, componentName);
            if (providerInfo == null) {
                providerInfo = getProviderInfo(installedProviders, componentName);
            }
            if (providerInfo != null) {
                SBAppWidgetProviderInfo sBAppWidgetProviderInfo = new SBAppWidgetProviderInfo(providerInfo);
                sBAppWidgetProviderInfo.mIsEnabled = sBDefaultProviderInfo.mEnable;
                arrayList.add(sBAppWidgetProviderInfo);
            }
        }
        return arrayList;
    }

    private AppWidgetProviderInfo getProviderInfo(List<AppWidgetProviderInfo> providers, ComponentName cn) {
        for (AppWidgetProviderInfo appWidgetProviderInfo : providers) {
            if (appWidgetProviderInfo.provider.equals(cn)) {
                return appWidgetProviderInfo;
            }
        }
        return null;
    }

    private void insertProvider(Context context, AppWidgetProviderInfo info, boolean isEnable) {
        int iBindWidget = bindWidget(context, info);
        if (iBindWidget != -1) {
            insertDatabase(info, iBindWidget, isEnable, -1);
        }
    }

    public void initWidgetInfo() {
        this.mAppWidgetId = -1;
        this.mAppWidgetProviderInfo = null;
    }
}
