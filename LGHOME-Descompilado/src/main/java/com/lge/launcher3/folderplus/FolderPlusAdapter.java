package com.lge.launcher3.folderplus;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsFolderInfo;
import com.lge.launcher3.allapps.AllAppsItemFactory;
import com.lge.launcher3.allapps.AllAppsItemInfo;
import com.lge.launcher3.hideapps.CheckableAppIcon;
import com.lge.launcher3.util.AppNameComparator;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class FolderPlusAdapter extends BaseAdapter implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final Comparator<ShortcutInfo> LABEL_COMPARATOR = new Comparator<ShortcutInfo>() { // from class: com.lge.launcher3.folderplus.FolderPlusAdapter.1
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public final int compare(ShortcutInfo a, ShortcutInfo b) {
            CharSequence charSequence = a.title;
            CharSequence charSequence2 = b.title;
            if (charSequence == null && charSequence2 == null) {
                return 0;
            }
            if (charSequence == null) {
                return -1;
            }
            if (charSequence2 == null) {
                return 1;
            }
            return AppNameComparator.compare(charSequence.toString(), charSequence2.toString());
        }
    };
    public static final Comparator<ShortcutInfo> RANK_COMPARATOR = new Comparator<ShortcutInfo>() { // from class: com.lge.launcher3.folderplus.FolderPlusAdapter.2
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public final int compare(ShortcutInfo a, ShortcutInfo b) {
            if (a == null && b == null) {
                return 0;
            }
            if (a == null) {
                return -1;
            }
            if (b == null) {
                return 1;
            }
            return a.rank - b.rank;
        }
    };
    private Context mContext;
    private long mFolderId;
    private FolderInfo mFolderInfo;
    private IconCache mIconCache;
    private boolean mIsAllApps;
    private LayoutInflater mLayoutInflater;
    private OnCheckStateChangedListener mListener;
    private ViewGroup mRootView;
    private List<ShortcutInfo> mAllItems = new ArrayList();
    private ArrayList<ShortcutInfo> mFolderItems = new ArrayList<>();
    private ArrayList<ShortcutInfo> mWillBeAdded = new ArrayList<>();
    private ArrayList<ShortcutInfo> mWillBeRemoved = new ArrayList<>();

    public interface OnCheckStateChangedListener {
        void onCheckStateChanged();
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    public FolderPlusAdapter(Context context, ViewGroup root, long folderId, boolean isAllApps) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRootView = root;
        this.mFolderId = folderId;
        this.mIsAllApps = isAllApps;
        this.mIconCache = LauncherAppState.getInstance(context).getIconCache();
        if (LGHomeFeature.isEnableDefaultHome()) {
            if (!this.mIsAllApps) {
                loadAllApps(context);
                return;
            } else {
                loadAllAppsForMenu(context);
                return;
            }
        }
        if (!this.mIsAllApps) {
            loadAllAppsForWorkSpace(context);
        } else {
            loadAllAppsForMenu(context);
        }
    }

    private void loadAllApps(Context context) {
        this.mAllItems.clear();
        this.mFolderItems.clear();
        for (ItemInfo itemInfo : LauncherAppState.getInstance(context).getModel().getItems()) {
            if (itemInfo.itemType == 0 || itemInfo.itemType == 1 || itemInfo.itemType == 6) {
                if (itemInfo.container == this.mFolderId) {
                    this.mFolderItems.add((ShortcutInfo) itemInfo);
                } else {
                    this.mAllItems.add((ShortcutInfo) itemInfo);
                }
            } else if (itemInfo.itemType == 2 && itemInfo.id == this.mFolderId) {
                this.mFolderInfo = (FolderInfo) itemInfo;
            }
        }
        Collections.sort(this.mFolderItems, RANK_COMPARATOR);
        Collections.sort(this.mAllItems, LABEL_COMPARATOR);
        this.mAllItems.addAll(0, this.mFolderItems);
    }

    private void loadAllAppsForWorkSpace(Context context) {
        this.mAllItems.clear();
        this.mFolderItems.clear();
        LauncherModel model = LauncherAppState.getInstance(context).getModel();
        for (ItemInfo itemInfo : model.getItems()) {
            if (itemInfo.itemType == 2 && itemInfo.id == this.mFolderId) {
                this.mFolderInfo = (FolderInfo) itemInfo;
            }
        }
        Iterator<AppInfo> it = model.getAllAppsList().iterator();
        while (it.hasNext()) {
            this.mAllItems.add(new ShortcutInfo(it.next()));
        }
        Collections.sort(this.mAllItems, LABEL_COMPARATOR);
    }

    private void loadAllAppsForMenu(Context context) {
        this.mAllItems.clear();
        this.mFolderItems.clear();
        for (AllAppsItemInfo allAppsItemInfo : AllAppsItemFactory.getInstance().getAllAppsItemInfoList()) {
            if (allAppsItemInfo.itemType != 2 || allAppsItemInfo.mFolderInfo == null) {
                ShortcutInfo shortcutInfo = new ShortcutInfo(allAppsItemInfo);
                if (!this.mFolderItems.contains(shortcutInfo)) {
                    this.mAllItems.add(shortcutInfo);
                }
            } else if (allAppsItemInfo.mFolderInfo.id == this.mFolderId) {
                AllAppsFolderInfo allAppsFolderInfo = allAppsItemInfo.mFolderInfo;
                this.mFolderInfo = allAppsFolderInfo;
                this.mFolderItems.addAll(allAppsFolderInfo.contents);
            } else {
                this.mAllItems.addAll(allAppsItemInfo.mFolderInfo.contents);
            }
        }
        Collections.sort(this.mFolderItems, RANK_COMPARATOR);
        Collections.sort(this.mAllItems, LABEL_COMPARATOR);
        this.mAllItems.addAll(0, this.mFolderItems);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mAllItems.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.mAllItems.get(position);
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Bitmap icon;
        List<ShortcutInfo> list = this.mAllItems;
        if (list == null || position < 0 || position >= list.size()) {
            return null;
        }
        CheckableAppIcon checkableAppIcon = (CheckableAppIcon) convertView;
        ShortcutInfo shortcutInfo = this.mAllItems.get(position);
        boolean z = false;
        if (checkableAppIcon == null) {
            checkableAppIcon = (CheckableAppIcon) this.mLayoutInflater.inflate(R.layout.checkable_app_icon, this.mRootView, false);
        }
        if (shortcutInfo.itemType == 0 && shortcutInfo.usingLowResIcon) {
            icon = this.mIconCache.getIcon(shortcutInfo.getIntent(), shortcutInfo.user);
        } else {
            icon = shortcutInfo.getIcon(this.mIconCache);
        }
        checkableAppIcon.setIcon(icon);
        checkableAppIcon.setText(shortcutInfo.title);
        checkableAppIcon.setTag(shortcutInfo);
        checkableAppIcon.setOnClickListener(this);
        if (this.mWillBeAdded.contains(shortcutInfo) || (this.mFolderItems.contains(shortcutInfo) && !this.mWillBeRemoved.contains(shortcutInfo))) {
            z = true;
        }
        checkableAppIcon.setChecked(z);
        return checkableAppIcon;
    }

    public int getCheckedCount() {
        return (this.mFolderItems.size() + this.mWillBeAdded.size()) - this.mWillBeRemoved.size();
    }

    public int getTotalCount() {
        return this.mAllItems.size();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        CheckableAppIcon checkableAppIcon = (CheckableAppIcon) v;
        ShortcutInfo shortcutInfo = (ShortcutInfo) checkableAppIcon.getTag();
        checkableAppIcon.toggle();
        onCheckStateChanged(shortcutInfo, checkableAppIcon.isChecked());
        checkableAppIcon.setContentDescription(checkableAppIcon.getCheckableAppTalkbackString(checkableAppIcon.isChecked()));
        notifyCheckStateChanged();
    }

    public void onCheckStateChanged(ShortcutInfo info, boolean checked) {
        if (checked) {
            if (!this.mFolderItems.contains(info)) {
                if (this.mWillBeAdded.contains(info)) {
                    return;
                }
                this.mWillBeAdded.add(info);
                return;
            } else {
                if (this.mWillBeRemoved.contains(info)) {
                    this.mWillBeRemoved.remove(info);
                    return;
                }
                return;
            }
        }
        if (this.mFolderItems.contains(info)) {
            if (this.mWillBeRemoved.contains(info)) {
                return;
            }
            this.mWillBeRemoved.add(info);
        } else if (this.mWillBeAdded.contains(info)) {
            this.mWillBeAdded.remove(info);
        }
    }

    public void setCheckedAll(boolean checked) {
        for (int i = 0; i < this.mRootView.getChildCount(); i++) {
            ((CheckableAppIcon) this.mRootView.getChildAt(i)).setChecked(checked);
        }
        Iterator<ShortcutInfo> it = this.mAllItems.iterator();
        while (it.hasNext()) {
            onCheckStateChanged(it.next(), checked);
        }
        notifyCheckStateChanged();
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

    public void apply() {
        verifyHighRes(this.mWillBeAdded);
        if (!this.mIsAllApps) {
            LauncherModel model = LauncherAppState.getInstance(this.mContext).getModel();
            model.moveItemsToFolder(this.mContext, this.mWillBeAdded, this.mFolderInfo);
            model.moveFolderItemsToWorkspace(this.mContext, this.mWillBeRemoved);
            model.removeWorkspaceEmptyScreenModel();
            return;
        }
        AllAppsItemFactory.getInstance().moveItemsToFolder(this.mWillBeAdded, this.mFolderInfo);
        AllAppsItemFactory.getInstance().moveFolderItemsToAllApps(this.mFolderInfo, this.mWillBeRemoved);
        AllAppsItemFactory.getInstance().removeAllAppsEmptyScreenModel();
    }

    private void verifyHighRes(ArrayList<ShortcutInfo> items) {
        for (ShortcutInfo shortcutInfo : items) {
            if (shortcutInfo.usingLowResIcon) {
                shortcutInfo.usingLowResIcon = false;
                shortcutInfo.updateIcon(this.mIconCache);
            }
        }
    }
}
