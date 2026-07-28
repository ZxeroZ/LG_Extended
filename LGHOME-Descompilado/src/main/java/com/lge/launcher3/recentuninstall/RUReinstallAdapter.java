package com.lge.launcher3.recentuninstall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.lge.launcher3.R;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class RUReinstallAdapter extends ArrayAdapter<RUAppInfo> {
    private IRUReinstallCallback mCallback;
    private final LayoutInflater mLayoutInflater;
    private List<RUAppInfo> mList;
    int mResource;
    private ReinstallViewHolder mViewHolder;

    public interface IRUReinstallCallback {
        TextView getEmptyText();

        void setOptionMenuEnable(boolean enable);

        void startEnableProgress(String packageName);
    }

    public RUReinstallAdapter(Context context, IRUReinstallCallback callback, int resource, List<RUAppInfo> list) {
        super(context, resource, list);
        this.mResource = resource;
        this.mCallback = callback;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final RUAppInfo item = getItem(position);
        if (convertView == null) {
            this.mViewHolder = new ReinstallViewHolder();
            convertView = this.mLayoutInflater.inflate(this.mResource, parent, false);
            this.mViewHolder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            this.mViewHolder.appName = (TextView) convertView.findViewById(R.id.app_name);
            this.mViewHolder.reinstall = (Button) convertView.findViewById(R.id.reinstall_button);
            convertView.setTag(this.mViewHolder);
        } else {
            this.mViewHolder = (ReinstallViewHolder) convertView.getTag();
        }
        this.mViewHolder.appIcon.setImageDrawable(item.getIcon());
        this.mViewHolder.appName.setText(item.getTitle());
        Button button = this.mViewHolder.reinstall;
        CharSequence title = item.getTitle();
        button.setContentDescription(((Object) title) + "," + getContext().getResources().getString(R.string.app_trash_restore_btn_text));
        this.mViewHolder.reinstall.setClickable(true);
        this.mViewHolder.reinstall.setFocusable(true);
        this.mViewHolder.reinstall.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.recentuninstall.RUReinstallAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (RUReinstallAdapter.this.mList.size() == 0) {
                    return;
                }
                RUReinstallAdapter.this.mCallback.startEnableProgress(item.getPackageName());
                parent.postDelayed(new Runnable() { // from class: com.lge.launcher3.recentuninstall.RUReinstallAdapter.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        RUReinstallAdapter.this.mList.remove(item);
                        RUReinstallAdapter.this.notifyDataSetChanged();
                        if (RUReinstallAdapter.this.mList.size() == 0) {
                            RUReinstallAdapter.this.mCallback.setOptionMenuEnable(false);
                            RUReinstallAdapter.this.mCallback.getEmptyText().setVisibility(0);
                        }
                    }
                }, 300L);
            }
        });
        this.mViewHolder.reinstall.setTag(Integer.valueOf(position));
        return convertView;
    }

    class ReinstallViewHolder {
        private ImageView appIcon;
        private TextView appName;
        private Button reinstall;

        ReinstallViewHolder() {
        }
    }
}
