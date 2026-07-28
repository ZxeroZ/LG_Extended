package com.lge.launcher3.wallpaperlist;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lge.launcher3.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperListAdapter extends ArrayAdapter<ResolveInfo> {
    private Context mContext;
    private int mLayout;
    private LayoutInflater mLayoutInflater;
    private PackageManager mPackage;
    private ArrayList<ResolveInfo> mResolveInfo;
    private ViewHolder mViewHolder;

    public WallpaperListAdapter(Context context, int wallpaperListItemLayout, ArrayList<ResolveInfo> info) {
        super(context, wallpaperListItemLayout, info);
        this.mLayoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mContext = context;
        this.mLayout = wallpaperListItemLayout;
        this.mResolveInfo = info;
        this.mPackage = context.getPackageManager();
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.mLayoutInflater.inflate(this.mLayout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            this.mViewHolder = viewHolder;
            viewHolder.appImage = (ImageView) convertView.findViewById(R.id.wallpaper_icon);
            this.mViewHolder.appName = (TextView) convertView.findViewById(R.id.wallpaper_title);
            convertView.setTag(this.mViewHolder);
        } else {
            this.mViewHolder = (ViewHolder) convertView.getTag();
        }
        this.mViewHolder.appImage.setImageDrawable(this.mResolveInfo.get(position).loadIcon(this.mPackage));
        this.mViewHolder.appName.setText(this.mResolveInfo.get(position).loadLabel(this.mPackage));
        return convertView;
    }

    class ViewHolder {
        private ImageView appImage;
        private TextView appName;

        ViewHolder() {
        }
    }
}
