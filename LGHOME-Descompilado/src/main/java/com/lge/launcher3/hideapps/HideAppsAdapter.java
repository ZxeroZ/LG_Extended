package com.lge.launcher3.hideapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.icons.IconCache;
import com.lge.launcher3.R;
import com.lge.launcher3.hideapps.HiddenApps;
import com.lge.launcher3.util.AppNameComparator;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.UserUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class HideAppsAdapter extends BaseAdapter implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final Comparator<HideAppItem> COMPARATOR = new Comparator<HideAppItem>() { // from class: com.lge.launcher3.hideapps.HideAppsAdapter.1
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public final int compare(HideAppItem a, HideAppItem b) {
            CharSequence label = a.activityInfo.getLabel();
            CharSequence label2 = b.activityInfo.getLabel();
            if (label == null && label2 == null) {
                return 0;
            }
            if (label == null) {
                return -1;
            }
            if (label2 == null) {
                return 1;
            }
            return AppNameComparator.compare(label.toString(), label2.toString());
        }
    };
    private static final String STATE_CHECKED_APPS = "checked_apps";
    private static final String TAG = "HideApps.Apdater";
    private List<HideAppItem> mAllApps = new ArrayList();
    private HiddenApps mHiddenApps;
    private IconCache mIconCache;
    private LauncherAppsCompat mLauncherApps;
    private LayoutInflater mLayoutInflater;
    private OnCheckStateChangedListener mListener;
    private ViewGroup mRootView;

    public interface OnCheckStateChangedListener {
        void onCheckStateChanged();
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    public void selectAll(boolean check) {
        Iterator<HideAppItem> it = this.mAllApps.iterator();
        while (it.hasNext()) {
            it.next().checked = check;
            notifyCheckStateChanged();
        }
    }

    public HideAppsAdapter(Context context, ViewGroup root) {
        this.mLauncherApps = LauncherAppsCompat.getInstance(context);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRootView = root;
        this.mHiddenApps = HiddenApps.getInstance(context);
        this.mIconCache = LauncherAppState.getInstance(context).getIconCache();
        loadAllApps(context);
    }

    private void loadAllApps(Context context) {
        LGLog.d(TAG, "loadAllApps ");
        this.mAllApps.clear();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        List<UserHandle> userProfiles = UserUtils.getUserManagerCompat(context).getUserProfiles();
        int size = userProfiles.size();
        for (int i = 0; i < size; i++) {
            UserHandle userHandle = userProfiles.get(i);
            for (LauncherActivityInfo launcherActivityInfo : this.mLauncherApps.getActivityList(null, userHandle)) {
                if (!isExcludedApp(context, launcherActivityInfo)) {
                    HideAppItem hideAppItem = new HideAppItem();
                    hideAppItem.activityInfo = launcherActivityInfo;
                    hideAppItem.userHandle = userHandle;
                    hideAppItem.checked = this.mHiddenApps.contains(launcherActivityInfo.getComponentName(), userHandle);
                    hideAppItem.intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN);
                    hideAppItem.intent.addCategory("android.intent.category.LAUNCHER");
                    hideAppItem.intent.setComponent(launcherActivityInfo.getComponentName());
                    if (hideAppItem.checked) {
                        arrayList.add(hideAppItem);
                    } else {
                        arrayList2.add(hideAppItem);
                    }
                }
            }
        }
        Comparator<HideAppItem> comparator = COMPARATOR;
        Collections.sort(arrayList, comparator);
        Collections.sort(arrayList2, comparator);
        this.mAllApps.addAll(arrayList);
        this.mAllApps.addAll(arrayList2);
    }

    private boolean isExcludedApp(Context context, LauncherActivityInfo activityInfo) {
        for (String str : context.getResources().getStringArray(R.array.lg_exclude_apps)) {
            if (str.equals(activityInfo.getComponentName().flattenToString())) {
                return true;
            }
        }
        return false;
    }

    public List<LauncherActivityInfo> getActivityList(String packageName, UserHandle user) {
        List<LauncherActivityInfo> activityList = this.mLauncherApps.getActivityList(packageName, user);
        return activityList.size() == 0 ? Collections.emptyList() : activityList;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mAllApps.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.mAllApps.get(position);
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        List<HideAppItem> list = this.mAllApps;
        CheckableAppIcon checkableAppIcon = null;
        if (list == null) {
            return null;
        }
        if (position >= 0 && position < list.size()) {
            HideAppItem hideAppItem = this.mAllApps.get(position);
            LauncherActivityInfo launcherActivityInfo = hideAppItem.activityInfo;
            checkableAppIcon = (CheckableAppIcon) convertView;
            if (convertView == null) {
                checkableAppIcon = (CheckableAppIcon) this.mLayoutInflater.inflate(R.layout.checkable_app_icon, this.mRootView, false);
            }
            checkableAppIcon.setIcon(this.mIconCache.getIcon(hideAppItem.intent, hideAppItem.userHandle));
            checkableAppIcon.setText(launcherActivityInfo.getLabel());
            checkableAppIcon.setTag(hideAppItem);
            checkableAppIcon.setChecked(hideAppItem.checked);
            checkableAppIcon.setOnClickListener(this);
        }
        return checkableAppIcon;
    }

    public List<HideAppItem> getCheckedItems() {
        ArrayList arrayList = new ArrayList();
        for (HideAppItem hideAppItem : this.mAllApps) {
            if (hideAppItem.checked) {
                arrayList.add(hideAppItem);
            }
        }
        return arrayList;
    }

    public int getCheckedCount() {
        Iterator<HideAppItem> it = this.mAllApps.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().checked) {
                i++;
            }
        }
        return i;
    }

    public int getTotalCount() {
        return this.mAllApps.size();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        CheckableAppIcon checkableAppIcon = (CheckableAppIcon) v;
        checkableAppIcon.toggle();
        HideAppItem hideAppItem = (HideAppItem) checkableAppIcon.getTag();
        hideAppItem.checked = checkableAppIcon.isChecked();
        checkableAppIcon.setContentDescription(checkableAppIcon.getCheckableAppTalkbackString(hideAppItem.checked));
        notifyCheckStateChanged();
    }

    public void restoreState(Bundle savedInstanceState) {
        ArrayList parcelableArrayList = savedInstanceState.getParcelableArrayList(STATE_CHECKED_APPS);
        Iterator<HideAppItem> it = this.mAllApps.iterator();
        while (it.hasNext()) {
            it.next().checked = false;
        }
        if (parcelableArrayList != null) {
            Iterator it2 = parcelableArrayList.iterator();
            while (it2.hasNext()) {
                HideAppItem hideAppItemFindHideAppItem = findHideAppItem((HiddenApps.HiddenApp) it2.next());
                if (hideAppItemFindHideAppItem != null) {
                    hideAppItemFindHideAppItem.checked = true;
                }
            }
        }
        notifyCheckStateChanged();
    }

    private HideAppItem findHideAppItem(HiddenApps.HiddenApp app) {
        for (HideAppItem hideAppItem : this.mAllApps) {
            if (hideAppItem.activityInfo.getComponentName().equals(app.getComponentName()) && hideAppItem.userHandle.equals(app.getUserHandle())) {
                return hideAppItem;
            }
        }
        return null;
    }

    public void saveState(Bundle outState) {
        ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
        for (HideAppItem hideAppItem : this.mAllApps) {
            if (hideAppItem.checked) {
                arrayList.add(new HiddenApps.HiddenApp(hideAppItem.activityInfo.getComponentName(), hideAppItem.userHandle));
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        outState.putParcelableArrayList(STATE_CHECKED_APPS, arrayList);
    }

    public void setOnCheckStateChangedListener(OnCheckStateChangedListener listener) {
        this.mListener = listener;
    }

    public void notifyCheckStateChanged() {
        OnCheckStateChangedListener onCheckStateChangedListener = this.mListener;
        if (onCheckStateChangedListener != null) {
            onCheckStateChangedListener.onCheckStateChanged();
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onClick(view);
    }
}
