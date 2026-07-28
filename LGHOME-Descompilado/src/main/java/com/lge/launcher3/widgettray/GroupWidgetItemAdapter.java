package com.lge.launcher3.widgettray;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.WidgetCell;
import com.lge.launcher3.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class GroupWidgetItemAdapter extends BaseAdapter {
    private static final int VIEW_HOLDER = 2131296548;
    private Context mContext;
    private View.OnClickListener mIconClickListener;
    private View.OnLongClickListener mIconLongClickListener;
    private List<Object> mList;
    private List<WidgetCell> mWidgetCellList;
    private WidgetPreviewLoader mWidgetPreviewLoader;

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return 0L;
    }

    public GroupWidgetItemAdapter() {
    }

    public GroupWidgetItemAdapter(Context context, List<Object> arrayList, View.OnClickListener iconClickListener, View.OnLongClickListener iconLongClickListener, boolean isTablet) {
        this.mContext = context;
        this.mList = arrayList;
        this.mIconLongClickListener = iconLongClickListener;
        this.mIconClickListener = iconClickListener;
        this.mWidgetCellList = new ArrayList();
    }

    public void setItemList(List<Object> arrayList) {
        this.mList = arrayList;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        List<Object> list = this.mList;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        WidgetCell widgetCell = (WidgetCell) convertView;
        if (getWidgetPreviewLoader() == null) {
            return null;
        }
        LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        Object obj = this.mList.get(position);
        if (widgetCell == null) {
            widgetCell = (WidgetCell) layoutInflater.inflate(R.layout.widget_group_popup_view_item, (ViewGroup) null);
            viewHolder = new ViewHolder();
            LGWidgetCell lGWidgetCell = (LGWidgetCell) widgetCell;
            viewHolder.widgetPreview = (WidgetsImageView) lGWidgetCell.findViewById(R.id.widget_preview);
            viewHolder.widgetCell = lGWidgetCell;
            widgetCell.setTag(R.id.group_widget_view_holder, viewHolder);
        } else {
            viewHolder = (ViewHolder) widgetCell.getTag(R.id.group_widget_view_holder);
        }
        if (obj != null) {
            if (obj instanceof LauncherAppWidgetProviderInfo) {
                LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) obj;
                widgetCell.setTag(new PendingAddWidgetInfo((Launcher) this.mContext, launcherAppWidgetProviderInfo, null));
                widgetCell.applyFromAppWidgetProviderInfo(launcherAppWidgetProviderInfo, this.mWidgetPreviewLoader);
            } else if (obj instanceof ResolveInfo) {
                ResolveInfo resolveInfo = (ResolveInfo) obj;
                widgetCell.setTag(new PendingAddShortcutInfo(resolveInfo.activityInfo, Launcher.getLauncher(this.mContext)));
                widgetCell.applyFromResolveInfo(this.mContext.getPackageManager(), resolveInfo, this.mWidgetPreviewLoader);
            }
        }
        viewHolder.widgetCell.setOnLongClickListener(this.mIconLongClickListener);
        viewHolder.widgetCell.setOnClickListener(this.mIconClickListener);
        viewHolder.widgetCell.setBackgroundResource(R.drawable.btn_homescreen_set_wallpaper_normal);
        viewHolder.widgetPreview.setBitmap(null);
        makeWidgetPreview(obj, viewHolder, parent, widgetCell.getTag());
        LGWidgetCell lGWidgetCell2 = viewHolder.widgetCell;
        this.mWidgetCellList.add(viewHolder.widgetCell);
        return lGWidgetCell2;
    }

    /* JADX WARN: Type inference failed for: r4v1, types: [com.lge.launcher3.widgettray.GroupWidgetItemAdapter$1] */
    private void makeWidgetPreview(Object info, ViewHolder viewHolder, ViewGroup parent, final Object tag) {
        new AsyncTask<Object, Void, Bitmap>() { // from class: com.lge.launcher3.widgettray.GroupWidgetItemAdapter.1
            private ViewHolder viewHolder;

            /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.AsyncTask
            public Bitmap doInBackground(Object... params) {
                ViewHolder viewHolder2 = (ViewHolder) params[1];
                this.viewHolder = viewHolder2;
                return viewHolder2.widgetCell.getPreview(params[0]);
            }

            /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Bitmap oriBitmap) {
                if (this.viewHolder.widgetCell.getTag().equals(tag)) {
                    WidgetsImageView widgetsImageView = this.viewHolder.widgetPreview;
                    if (widgetsImageView != null) {
                        widgetsImageView.setBitmap(oriBitmap);
                    }
                    super.onPostExecute(oriBitmap);
                }
            }
        }.execute(info, viewHolder);
    }

    private WidgetPreviewLoader getWidgetPreviewLoader() {
        if (this.mWidgetPreviewLoader == null) {
            this.mWidgetPreviewLoader = LauncherAppState.getInstance(this.mContext).getWidgetCache();
        }
        return this.mWidgetPreviewLoader;
    }

    public boolean compareWidgetCell(View v) {
        List<WidgetCell> list = this.mWidgetCellList;
        return list != null && list.contains(v);
    }

    class ViewHolder {
        private LGWidgetCell widgetCell;
        private WidgetsImageView widgetPreview;

        ViewHolder() {
        }
    }
}
