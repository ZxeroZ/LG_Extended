package com.lge.launcher3.recentuninstall;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.lge.launcher3.R;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class RUDeleteAdapter extends ArrayAdapter<RUAppInfo> {
    private final LayoutInflater mLayoutInflater;
    private List<RUAppInfo> mRecentUninstallAppInfoList;
    int mResource;
    private DeleteViewHolder mViewHolder;

    public RUDeleteAdapter(Context context, Activity activity, int resource, List<RUAppInfo> list) {
        super(context, resource, list);
        this.mResource = resource;
        this.mRecentUninstallAppInfoList = list;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public int getSelectedCount() {
        Iterator<RUAppInfo> it = this.mRecentUninstallAppInfoList.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (it.next().isSelected()) {
                i++;
            }
        }
        return i;
    }

    public void setChecked(int position) {
        this.mRecentUninstallAppInfoList.get(position).setSelected(!r2.isSelected());
    }

    public void setSelectAll(boolean isSelectAll) {
        Iterator<RUAppInfo> it = this.mRecentUninstallAppInfoList.iterator();
        while (it.hasNext()) {
            it.next().setSelected(isSelectAll);
        }
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        RUAppInfo item = getItem(position);
        if (convertView == null) {
            convertView = this.mLayoutInflater.inflate(this.mResource, parent, false);
            DeleteViewHolder deleteViewHolder = new DeleteViewHolder();
            this.mViewHolder = deleteViewHolder;
            deleteViewHolder.appCheckBox = (CheckBox) convertView.findViewById(R.id.select_checkBox);
            this.mViewHolder.appName = (TextView) convertView.findViewById(R.id.app_name);
            convertView.setTag(this.mViewHolder);
        } else {
            this.mViewHolder = (DeleteViewHolder) convertView.getTag();
        }
        this.mViewHolder.appCheckBox.setClickable(false);
        this.mViewHolder.appCheckBox.setFocusable(false);
        this.mViewHolder.appCheckBox.setChecked(item.isSelected());
        this.mViewHolder.appName.setText(item.getTitle());
        return convertView;
    }

    class DeleteViewHolder {
        private CheckBox appCheckBox;
        private TextView appName;

        DeleteViewHolder() {
        }
    }
}
