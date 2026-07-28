package com.android.launcher3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class AppDrawerDialog extends DialogFragment {
    private static final String TAG = "AppDrawerDialog";
    private static AppDrawerDialog sInstance;
    private AppListAdapter mAppListAdapter;
    private AppPickedCallback mAppPickerCallback;

    public AppDrawerDialog() {
        setStyle(0, R.style.PinnedAppPickerDialogTheme);
    }

    public static AppDrawerDialog getInstance(AppListAdapter appListAdapter) {
        if (sInstance == null) {
            sInstance = new AppDrawerDialog();
        }
        sInstance.mAppListAdapter = appListAdapter;
        appListAdapter.setAppDrawer(true);
        return sInstance;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_drawer_dialog, container);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gridView = (GridView) view.findViewById(R.id.app_drawer_grid);
        gridView.setAdapter((ListAdapter) this.mAppListAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.launcher3.-$$Lambda$AppDrawerDialog$NXxbfPmjswM0fmQECLprgegfT0Q
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view2, int i, long j) {
                this.f$0.lambda$onViewCreated$0$AppDrawerDialog(adapterView, view2, i, j);
            }
        });
    }

    public /* synthetic */ void lambda$onViewCreated$0$AppDrawerDialog(AdapterView adapterView, View view, int i, long j) {
        AppListUtils.launch(getContext(), this.mAppListAdapter.getItem(i).getLaunchIntent());
        dismiss();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void show(FragmentManager manager, String tag) {
        LGLog.i(TAG, "show");
        super.show(manager, tag);
    }

    @Override // androidx.fragment.app.DialogFragment
    public void dismiss() {
        LGLog.i(TAG, "dismiss");
        super.dismiss();
    }
}
