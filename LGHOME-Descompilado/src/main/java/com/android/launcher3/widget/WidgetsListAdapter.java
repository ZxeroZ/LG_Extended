package com.android.launcher3.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.WidgetsModel;
import com.android.launcher3.util.LabelComparator;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageUserKey;
import com.lge.launcher3.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsListAdapter extends RecyclerView.Adapter<WidgetsRowViewHolder> {
    private static final boolean DEBUG = false;
    private static final int PRESET_INDENT_SIZE_TABLET = 56;
    private static final String TAG = "WidgetsListAdapter";
    private final ArrayList<WidgetListRowEntry> mEntries = new ArrayList<>();
    private View.OnClickListener mIconClickListener;
    private View.OnLongClickListener mIconLongClickListener;
    private int mIndent;
    private final AlphabeticIndexCompat mIndexer;
    private Launcher mLauncher;
    private LayoutInflater mLayoutInflater;
    private WidgetPreviewLoader mWidgetPreviewLoader;
    private WidgetsModel mWidgetsModel;

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int pos) {
        return pos;
    }

    /* JADX DEBUG: Method merged with bridge method: onFailedToRecycleView(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;)Z */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public boolean onFailedToRecycleView(WidgetsRowViewHolder holder) {
        return true;
    }

    public WidgetsListAdapter(View.OnClickListener iconClickListener, View.OnLongClickListener iconLongClickListener, Context context) {
        this.mIndent = 0;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mWidgetPreviewLoader = LauncherAppState.getInstance(context).getWidgetCache();
        this.mIndexer = new AlphabeticIndexCompat(context);
        this.mIconClickListener = iconClickListener;
        this.mIconLongClickListener = iconLongClickListener;
        this.mIndent = context.getResources().getDimensionPixelSize(R.dimen.widget_section_indent);
        this.mLauncher = Launcher.getLauncher(context);
        setContainerHeight();
    }

    public void setWidgetsModel(WidgetsModel w) {
        this.mWidgetsModel = w;
    }

    public void setWidgets(MultiHashMap<PackageItemInfo, WidgetItem> widgets) {
        this.mEntries.clear();
        WidgetItemComparator widgetItemComparator = new WidgetItemComparator();
        for (Map.Entry<PackageItemInfo, WidgetItem> entry : widgets.entrySet()) {
            WidgetListRowEntry widgetListRowEntry = new WidgetListRowEntry(entry.getKey(), (ArrayList) entry.getValue());
            widgetListRowEntry.titleSectionName = this.mIndexer.computeSectionName(widgetListRowEntry.pkgItem.title);
            Collections.sort(widgetListRowEntry.widgets, widgetItemComparator);
            this.mEntries.add(widgetListRowEntry);
        }
        Collections.sort(this.mEntries, new WidgetListRowEntryComparator());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mWidgetsModel.getPackageSize();
    }

    /* JADX DEBUG: Method merged with bridge method: onBindViewHolder(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;I)V */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(WidgetsRowViewHolder holder, int pos) {
        List<Object> sortedWidgets = this.mWidgetsModel.getSortedWidgets(pos);
        ViewGroup viewGroup = (ViewGroup) holder.getContent().findViewById(R.id.widgets_cell_list);
        int size = sortedWidgets.size() - viewGroup.getChildCount();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                WidgetCell widgetCell = (WidgetCell) this.mLayoutInflater.inflate(R.layout.widget_cell, viewGroup, false);
                widgetCell.setOnClickListener(this.mIconClickListener);
                widgetCell.setOnLongClickListener(this.mIconLongClickListener);
                ViewGroup.LayoutParams layoutParams = widgetCell.getLayoutParams();
                layoutParams.height = widgetCell.mCellSize;
                layoutParams.width = widgetCell.mCellSize;
                widgetCell.setLayoutParams(layoutParams);
                viewGroup.addView(widgetCell);
            }
        } else if (size < 0) {
            for (int size2 = sortedWidgets.size(); size2 < viewGroup.getChildCount(); size2++) {
                viewGroup.getChildAt(size2).setVisibility(8);
            }
        }
        ((BubbleTextView) holder.getContent().findViewById(R.id.section)).applyFromPackageItemInfo(this.mWidgetsModel.getPackageItemInfo(pos));
        if (getWidgetPreviewLoader() == null) {
            return;
        }
        for (int i2 = 0; i2 < sortedWidgets.size(); i2++) {
            WidgetCell widgetCell2 = (WidgetCell) viewGroup.getChildAt(i2);
            if (sortedWidgets.get(i2) instanceof LauncherAppWidgetProviderInfo) {
                LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) sortedWidgets.get(i2);
                widgetCell2.setTag(new PendingAddWidgetInfo(this.mLauncher, launcherAppWidgetProviderInfo, null));
                widgetCell2.applyFromAppWidgetProviderInfo(launcherAppWidgetProviderInfo, this.mWidgetPreviewLoader);
            } else if (sortedWidgets.get(i2) instanceof ResolveInfo) {
                ResolveInfo resolveInfo = (ResolveInfo) sortedWidgets.get(i2);
                widgetCell2.setTag(new PendingAddShortcutInfo(resolveInfo.activityInfo, this.mLauncher));
                widgetCell2.applyFromResolveInfo(this.mLauncher.getPackageManager(), resolveInfo, this.mWidgetPreviewLoader);
            }
            widgetCell2.ensurePreview();
            widgetCell2.setVisibility(0);
        }
    }

    /* JADX DEBUG: Method merged with bridge method: onCreateViewHolder(Landroid/view/ViewGroup;I)Landroidx/recyclerview/widget/RecyclerView$ViewHolder; */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public WidgetsRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) this.mLayoutInflater.inflate(R.layout.widgets_list_row_view, parent, false);
        LinearLayout linearLayout = (LinearLayout) viewGroup.findViewById(R.id.widgets_cell_list);
        if (Build.VERSION.SDK_INT >= 17) {
            linearLayout.setPaddingRelative(this.mIndent, 0, 1, 0);
        } else {
            linearLayout.setPadding(this.mIndent, 0, 1, 0);
        }
        return new WidgetsRowViewHolder(viewGroup);
    }

    /* JADX DEBUG: Method merged with bridge method: onViewRecycled(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;)V */
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewRecycled(WidgetsRowViewHolder holder) {
        ViewGroup viewGroup = (ViewGroup) holder.getContent().findViewById(R.id.widgets_cell_list);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            ((WidgetCell) viewGroup.getChildAt(i)).clear();
        }
    }

    private WidgetPreviewLoader getWidgetPreviewLoader() {
        if (this.mWidgetPreviewLoader == null) {
            this.mWidgetPreviewLoader = LauncherAppState.getInstance(this.mLauncher).getWidgetCache();
        }
        return this.mWidgetPreviewLoader;
    }

    private void setContainerHeight() {
        Resources resources = this.mLauncher.getResources();
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        if (deviceProfile.isLargeTablet || deviceProfile.isTablet) {
            this.mIndent = Utilities.pxFromDp(56.0f, resources.getDisplayMetrics());
        }
    }

    public List<WidgetItem> copyWidgetsForPackageUser(PackageUserKey packageUserKey) {
        WidgetsModel bgWidgetsModel = this.mLauncher.getModel().getBgWidgetsModel();
        this.mWidgetsModel = bgWidgetsModel;
        return convertObjectToWidgetItem(this.mWidgetsModel.mWidgetsList.get(bgWidgetsModel.mTmpPackageItemInfos.get(packageUserKey.mPackageName)), packageUserKey);
    }

    private List<WidgetItem> convertObjectToWidgetItem(ArrayList<Object> widgetsShortcutsList, PackageUserKey packageUserKey) {
        if (widgetsShortcutsList == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        LauncherAppState launcherAppState = LauncherAppState.getInstance(this.mLauncher.getApplicationContext());
        final PackageManager packageManager = this.mLauncher.getPackageManager();
        for (Object obj : widgetsShortcutsList) {
            if (obj instanceof LauncherAppWidgetProviderInfo) {
                arrayList.add(new WidgetItem((LauncherAppWidgetProviderInfo) obj, packageManager, launcherAppState.getInvariantDeviceProfile(), this.mLauncher));
            } else if (obj instanceof ResolveInfo) {
                final ResolveInfo resolveInfo = (ResolveInfo) obj;
                arrayList.add(new WidgetItem(new ShortcutConfigActivityInfo(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name), Process.myUserHandle()) { // from class: com.android.launcher3.widget.WidgetsListAdapter.1
                    @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
                    public CharSequence getLabel() {
                        return resolveInfo.loadLabel(packageManager);
                    }

                    @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
                    public Drawable getFullResIcon(IconCache cache) {
                        return cache.getFullResIcon(resolveInfo.activityInfo);
                    }
                }));
            }
        }
        return arrayList;
    }

    public static class WidgetListRowEntryComparator implements Comparator<WidgetListRowEntry> {
        private final LabelComparator mComparator = new LabelComparator();

        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(WidgetListRowEntry a, WidgetListRowEntry b) {
            return this.mComparator.compare(a.pkgItem.title.toString(), b.pkgItem.title.toString());
        }
    }
}
