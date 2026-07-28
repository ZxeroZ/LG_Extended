package com.lge.launcher3.smartbulletin.setting;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.lge.launcher3.R;
import com.lge.launcher3.homesettings.SmartBulletinAction;
import com.lge.launcher3.smartbulletin.info.SBAppWidgetProviderInfo;
import com.lge.launcher3.smartbulletin.log.SBLog;
import com.lge.launcher3.smartbulletin.provider.SBContentObserver;
import com.lge.launcher3.smartbulletin.provider.SBContract;
import com.mobeta.android.dslv.DragSortListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SBSettingListAdapter extends BaseAdapter implements DragSortListView.DropListener {
    private static final int COUNT_TYPES = 2;
    private static final int TYPE_CHECK = 0;
    private static final int TYPE_UNCHECK = 1;
    private int mLimitedNum;
    SBContentObserver mObserver;
    List<SBAppWidgetProviderInfo> mProviderList;
    private boolean mIsEnabled = true;
    private Toast mExceedLimitedToast = null;

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 2;
    }

    public SBSettingListAdapter(final Context context) {
        this.mProviderList = null;
        this.mObserver = null;
        this.mLimitedNum = 0;
        this.mProviderList = SBContract.SmartBulletin.getAllProvider(context);
        SBContentObserver sBContentObserver = new SBContentObserver(new Handler()) { // from class: com.lge.launcher3.smartbulletin.setting.SBSettingListAdapter.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange) {
                synchronized (SBSettingListAdapter.this.mProviderList) {
                    super.onChange(selfChange);
                    ArrayList<SBAppWidgetProviderInfo> allProvider = SBContract.SmartBulletin.getAllProvider(context);
                    if (allProvider.size() != SBSettingListAdapter.this.mProviderList.size()) {
                        SBSettingListAdapter.this.mProviderList = allProvider;
                    }
                    SBSettingListAdapter.this.notifyDataSetChanged();
                }
            }
        };
        this.mObserver = sBContentObserver;
        sBContentObserver.registerObserver(context);
        this.mLimitedNum = context.getResources().getInteger(R.integer.smartbulletin_limited_number);
    }

    public void onDestroy(Context context) {
        this.mObserver.unregisterObserver(context);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mProviderList.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        if (this.mProviderList.size() > 0) {
            return this.mProviderList.get(position);
        }
        return null;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        return !this.mProviderList.get(i).mIsEnabled ? 1 : 0;
    }

    private class ViewHolder {
        ImageView dragHandle;
        TextView dragTitle;
        Switch providerName;

        private ViewHolder() {
        }
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        SBLog.v("ConvertView", String.valueOf(position));
        if (convertView == null) {
            convertView = ViewGroup.inflate(parent.getContext(), R.layout.smartbulletin_setting_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.dragHandle = (ImageView) convertView.findViewById(R.id.drag_handle);
            viewHolder.dragHandle.setOnTouchListener(new View.OnTouchListener() { // from class: com.lge.launcher3.smartbulletin.setting.SBSettingListAdapter.2
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    v.performHapticFeedback(0);
                    return false;
                }
            });
            viewHolder.dragTitle = (TextView) convertView.findViewById(R.id.drag_title);
            viewHolder.providerName = (Switch) convertView.findViewById(R.id.providerName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SBAppWidgetProviderInfo sBAppWidgetProviderInfo = this.mProviderList.get(position);
        if (this.mIsEnabled) {
            viewHolder.dragHandle.setAlpha(1.0f);
        } else {
            viewHolder.dragHandle.setAlpha(0.35f);
        }
        convertView.setFocusable(!this.mIsEnabled);
        viewHolder.dragTitle.setText(sBAppWidgetProviderInfo.mAppWidgetProviderInfo.loadLabel(parent.getContext().getPackageManager()));
        viewHolder.dragTitle.setEnabled(this.mIsEnabled);
        viewHolder.providerName.setEnabled(this.mIsEnabled);
        viewHolder.providerName.setChecked(sBAppWidgetProviderInfo.mIsEnabled);
        viewHolder.providerName.setTag(sBAppWidgetProviderInfo.mAppWidgetProviderInfo.provider);
        return convertView;
    }

    public void onClick(Switch view) {
        SBAppWidgetProviderInfo sBAppWidgetProviderInfoFindInfoByComponentName = findInfoByComponentName(this.mProviderList, (ComponentName) view.getTag());
        if (sBAppWidgetProviderInfoFindInfoByComponentName == null) {
            return;
        }
        boolean z = false;
        if (!sBAppWidgetProviderInfoFindInfoByComponentName.mIsEnabled) {
            if (getEnabledItemNum() >= this.mLimitedNum) {
                showExceedLimitedToast(view.getContext());
            } else {
                z = true;
                view.toggle();
            }
        } else {
            view.toggle();
        }
        sBAppWidgetProviderInfoFindInfoByComponentName.mIsEnabled = z;
    }

    private SBAppWidgetProviderInfo findInfoByComponentName(List<SBAppWidgetProviderInfo> list, ComponentName componentName) {
        for (SBAppWidgetProviderInfo sBAppWidgetProviderInfo : list) {
            if (sBAppWidgetProviderInfo.isSameComponent(componentName)) {
                return sBAppWidgetProviderInfo;
            }
        }
        return null;
    }

    @Override // com.mobeta.android.dslv.DragSortListView.DropListener
    public void drop(int from, int to) {
        synchronized (this.mProviderList) {
            if (from == to) {
                return;
            }
            SBAppWidgetProviderInfo sBAppWidgetProviderInfo = this.mProviderList.get(from);
            this.mProviderList.remove(from);
            this.mProviderList.add(to, sBAppWidgetProviderInfo);
            notifyDataSetChanged();
        }
    }

    public void updateData(final Context context) {
        setPositionYByIndex(context);
    }

    private void setPositionYByIndex(Context context) {
        synchronized (this.mProviderList) {
            if (context == null) {
                return;
            }
            ArrayList<SBAppWidgetProviderInfo> allProvider = SBContract.SmartBulletin.getAllProvider(context);
            for (int i = 0; i < this.mProviderList.size(); i++) {
                SBAppWidgetProviderInfo sBAppWidgetProviderInfo = this.mProviderList.get(i);
                if (sBAppWidgetProviderInfo.mIsEnabled) {
                    sBAppWidgetProviderInfo.mPostionY = i;
                } else if (!sBAppWidgetProviderInfo.mIsEnabled) {
                    sBAppWidgetProviderInfo.mPostionY = i + 1000;
                }
                SBContract.SmartBulletin.updateByInfo(context, sBAppWidgetProviderInfo);
                if (isChangeState(allProvider, sBAppWidgetProviderInfo)) {
                    SmartBulletinAction.sendProviderEnabled(context, sBAppWidgetProviderInfo.mIsEnabled, sBAppWidgetProviderInfo.mAppWidgetProviderInfo.provider);
                }
            }
            this.mProviderList = SBContract.SmartBulletin.getAllProvider(context);
        }
    }

    private boolean isChangeState(List<SBAppWidgetProviderInfo> currentProviderList, SBAppWidgetProviderInfo info) {
        SBAppWidgetProviderInfo infoFromList = getInfoFromList(currentProviderList, info.mAppWidgetProviderInfo.provider);
        return (infoFromList == null || info == null || info.mIsEnabled == infoFromList.mIsEnabled) ? false : true;
    }

    private SBAppWidgetProviderInfo getInfoFromList(List<SBAppWidgetProviderInfo> providerList, ComponentName cn) {
        for (SBAppWidgetProviderInfo sBAppWidgetProviderInfo : providerList) {
            if (sBAppWidgetProviderInfo != null && sBAppWidgetProviderInfo.isSameComponent(cn)) {
                return sBAppWidgetProviderInfo;
            }
        }
        return null;
    }

    public int getEnabledItemNum(Context context) {
        ArrayList<SBAppWidgetProviderInfo> allProvider = SBContract.SmartBulletin.getAllProvider(context);
        if (allProvider.size() != this.mProviderList.size()) {
            this.mProviderList = allProvider;
        }
        return getEnabledItemNum();
    }

    public int getEnabledItemNum() {
        Iterator<SBAppWidgetProviderInfo> it = this.mProviderList.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().mIsEnabled) {
                i++;
            }
        }
        return i;
    }

    public void setEnabled(boolean isEnabled) {
        this.mIsEnabled = isEnabled;
        notifyDataSetChanged();
    }

    public int getWidgetCount() {
        return this.mProviderList.size() - getSmartBulletinProviderCount();
    }

    private int getSmartBulletinProviderCount() {
        Iterator<SBAppWidgetProviderInfo> it = this.mProviderList.iterator();
        int i = 0;
        while (it.hasNext()) {
            if ((it.next().mAppWidgetProviderInfo.widgetCategory & 256) == 256) {
                i++;
            }
        }
        return i;
    }

    private void showExceedLimitedToast(Context context) {
        if (this.mExceedLimitedToast == null) {
            this.mExceedLimitedToast = makeExceedLimitedToast(context);
        }
        this.mExceedLimitedToast.show();
    }

    private Toast makeExceedLimitedToast(Context context) {
        return Toast.makeText(context, String.format(context.getResources().getString(R.string.smartbulletin_exceed_limited_number), Integer.valueOf(this.mLimitedNum)), 0);
    }
}
