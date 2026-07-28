package com.lge.launcher3.wing;

import android.content.ComponentName;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public class AppListAdapter extends RecyclerView.Adapter<AppViewHolder> implements ItemTouchHelperAdapter, EditModeCallback {
    private static final int MAX_CAROUSEL_ITEM = 35;
    private static final String TAG = "AppListAdapter";
    ArrayList<ShortcutInfo> appList;
    Context mContext;

    public AppListAdapter(Context context) {
        this.mContext = context;
        setHasStableIds(true);
    }

    public void setData(ArrayList<ShortcutInfo> data) {
        this.appList = new ArrayList<>(data);
    }

    public ArrayList<ShortcutInfo> getData() {
        return this.appList;
    }

    @Override // com.lge.launcher3.wing.ItemTouchHelperAdapter
    public void onItemInsert(ShortcutInfo info, int pos) {
        ArrayList<ShortcutInfo> arrayList = this.appList;
        if (arrayList != null) {
            arrayList.add(pos, info);
            updateSwivelPosition();
            LauncherModel.addItemToDatabaseSwivel(this.mContext, info);
            notifyItemInserted(pos);
        }
    }

    @Override // com.lge.launcher3.wing.ItemTouchHelperAdapter
    public void onItemMove(int fromPos, int targetPos) {
        ArrayList<ShortcutInfo> arrayList = this.appList;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        if (fromPos < targetPos) {
            int i = fromPos;
            while (i < targetPos) {
                int i2 = i + 1;
                Collections.swap(this.appList, i, i2);
                i = i2;
            }
        } else {
            for (int i3 = fromPos; i3 > targetPos; i3--) {
                Collections.swap(this.appList, i3, i3 - 1);
            }
        }
        updateSwivelPosition();
        notifyItemMoved(fromPos, targetPos);
        LauncherModel.updateSwivelPosition(this.mContext, fromPos, targetPos);
    }

    @Override // com.lge.launcher3.wing.ItemTouchHelperAdapter
    public void onItemDismiss(int pos) {
        ArrayList<ShortcutInfo> arrayList = this.appList;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        ShortcutInfo shortcutInfoRemove = this.appList.remove(pos);
        updateSwivelPosition();
        notifyItemRemoved(pos);
        LauncherModel.deleteItemsFromDatabaseSwivel(this.mContext, shortcutInfoRemove);
    }

    @Override // com.lge.launcher3.wing.EditModeCallback
    public void onItemRemove(View view) {
        ArrayList<ShortcutInfo> arrayList;
        if (view == null || (arrayList = this.appList) == null || arrayList.isEmpty()) {
            return;
        }
        ItemInfo itemInfo = (ItemInfo) view.getTag();
        if (itemInfo.getTargetComponent() == null) {
            return;
        }
        ShortcutInfo shortcutInfoRemove = this.appList.remove(itemInfo.swivelPosition);
        updateSwivelPosition();
        notifyItemRemoved(itemInfo.swivelPosition);
        LauncherModel.deleteItemsFromDatabaseSwivel(this.mContext, shortcutInfoRemove);
    }

    public void onItemRemove(ItemInfo itemInfo) {
        ArrayList<ShortcutInfo> arrayList;
        if (itemInfo == null || (arrayList = this.appList) == null || arrayList.isEmpty() || itemInfo.getTargetComponent() == null) {
            return;
        }
        ShortcutInfo shortcutInfoRemove = this.appList.remove(itemInfo.swivelPosition);
        updateSwivelPosition();
        notifyItemRemoved(itemInfo.swivelPosition);
        LauncherModel.deleteItemsFromDatabaseSwivel(this.mContext, shortcutInfoRemove);
    }

    public void onItemRemove(String packageName, String className) {
        ArrayList<ShortcutInfo> arrayList;
        if (packageName == null || (arrayList = this.appList) == null || arrayList.isEmpty()) {
            return;
        }
        ArrayList arrayList2 = new ArrayList();
        for (ShortcutInfo shortcutInfo : this.appList) {
            if (packageName.equals(shortcutInfo.getTargetComponent().getPackageName())) {
                if (className != null) {
                    if (className.equals(shortcutInfo.getTargetComponent().getClassName())) {
                        arrayList2.add(shortcutInfo);
                    }
                } else {
                    arrayList2.add(shortcutInfo);
                }
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            onItemRemove((ShortcutInfo) it.next());
        }
    }

    public void onClearList() {
        ArrayList<ShortcutInfo> arrayList = this.appList;
        if (arrayList != null && !arrayList.isEmpty()) {
            LGLog.i(TAG, "onClearList() appList.size() = " + this.appList.size());
            this.appList.clear();
        } else {
            LGLog.i(TAG, "onClearList() only clear DB");
        }
        LauncherModel.clearDatabaseSwivel(this.mContext);
    }

    public class AppViewHolder extends RecyclerView.ViewHolder implements IItemTouchHelperViewHolder {
        @Override // com.lge.launcher3.wing.IItemTouchHelperViewHolder
        public void onItemClear() {
        }

        @Override // com.lge.launcher3.wing.IItemTouchHelperViewHolder
        public void onItemSelected() {
        }

        public AppViewHolder(View itemView) {
            super(itemView);
        }
    }

    /* JADX DEBUG: Method merged with bridge method: onCreateViewHolder(Landroid/view/ViewGroup;I)Landroidx/recyclerview/widget/RecyclerView$ViewHolder; */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppViewHolder((SwivelAppIconView) LayoutInflater.from(this.mContext).inflate(R.layout.swivel_app_icon, (ViewGroup) null));
    }

    /* JADX DEBUG: Method merged with bridge method: onBindViewHolder(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;I)V */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(AppViewHolder holder, int position) {
        ShortcutInfo shortcutInfo = this.appList.get(position % this.appList.size());
        SwivelAppIconView swivelAppIconView = (SwivelAppIconView) holder.itemView;
        swivelAppIconView.applyFromCarouselShortcutInfo(shortcutInfo);
        swivelAppIconView.setTextVisibility(false);
        swivelAppIconView.setOnKeyListener(new CarouselKeyEventListener());
        if (UninstallModeManager.getInstance(this.mContext).isInUninstallMode()) {
            UninstallModeManager.getInstance(this.mContext).setUninstallTypeForBadgeView(swivelAppIconView);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        ArrayList<ShortcutInfo> arrayList = this.appList;
        if (arrayList == null || arrayList.isEmpty()) {
            return 0;
        }
        return this.appList.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int position) {
        ArrayList<ShortcutInfo> arrayList = this.appList;
        if (arrayList == null || arrayList.get(position) == null) {
            return 0L;
        }
        return this.appList.get(position).hashCode();
    }

    private void updateSwivelPosition() {
        ArrayList<ShortcutInfo> arrayList = this.appList;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        int i = 0;
        Iterator<ShortcutInfo> it = this.appList.iterator();
        while (it.hasNext()) {
            it.next().swivelPosition = i;
            i++;
        }
    }

    public void onItemUpdate(ComponentName name) {
        ArrayList<ShortcutInfo> arrayList = this.appList;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.appList.size(); i++) {
            if (this.appList.get(i).getTargetComponent().equals(name)) {
                notifyItemChanged(i);
            }
        }
    }

    public void onItemUpdate(ArrayList<ShortcutInfo> list) {
        ArrayList<ShortcutInfo> arrayList = this.appList;
        if (arrayList == null || arrayList.isEmpty() || list == null || list.isEmpty()) {
            return;
        }
        ArrayList<ShortcutInfo> arrayList2 = new ArrayList();
        for (ShortcutInfo shortcutInfo : list) {
            if (shortcutInfo.itemType != 6) {
                arrayList2.add(shortcutInfo);
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.appList.size(); i++) {
            if (this.appList.get(i).itemType != 6) {
                for (ShortcutInfo shortcutInfo2 : arrayList2) {
                    ShortcutInfo shortcutInfo3 = this.appList.get(i);
                    if (shortcutInfo3.getTargetComponent().equals(shortcutInfo2.getTargetComponent()) && shortcutInfo3.getCategories().containsAll(shortcutInfo2.getCategories()) && shortcutInfo2.title != null && !shortcutInfo2.title.toString().isEmpty()) {
                        shortcutInfo3.title = shortcutInfo2.title;
                        shortcutInfo3.status = shortcutInfo2.status;
                        shortcutInfo2.swivelPosition = shortcutInfo3.swivelPosition;
                        String str = TAG;
                        CharSequence charSequence = shortcutInfo2.title;
                        LGLog.i(str, "onItemUpdate : " + ((Object) charSequence) + " / " + shortcutInfo2.isPromise() + " / " + shortcutInfo2.swivelPosition);
                        LauncherModel.updateItemToDatabaseSwivel(this.mContext, shortcutInfo2);
                        SwivelAppIconCache.getInstance(this.mContext).fillCache(shortcutInfo2);
                        notifyItemChanged(i);
                    }
                }
            }
        }
    }

    public boolean isLoadedAppList() {
        return this.appList != null;
    }

    public boolean isCarouselItemMax() {
        ArrayList<ShortcutInfo> arrayList = this.appList;
        return (arrayList == null || arrayList.isEmpty() || this.appList.size() < 35) ? false : true;
    }
}
