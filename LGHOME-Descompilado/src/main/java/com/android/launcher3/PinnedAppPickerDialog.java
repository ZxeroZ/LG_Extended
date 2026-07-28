package com.android.launcher3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import androidx.fragment.app.DialogFragment;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class PinnedAppPickerDialog extends DialogFragment {
    private AppListAdapter mAppListAdapter;
    private AppPickedCallback mAppPickerCallback;

    public PinnedAppPickerDialog() {
        setStyle(0, R.style.PinnedAppPickerDialogTheme);
    }

    public static PinnedAppPickerDialog newInstance(AppListAdapter appListAdapter, AppPickedCallback callback) {
        PinnedAppPickerDialog pinnedAppPickerDialog = new PinnedAppPickerDialog();
        pinnedAppPickerDialog.mAppListAdapter = appListAdapter;
        pinnedAppPickerDialog.mAppPickerCallback = callback;
        return pinnedAppPickerDialog;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_picker_dialog, container);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gridView = (GridView) view.findViewById(R.id.picker_app_grid);
        gridView.setAdapter((ListAdapter) this.mAppListAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.launcher3.-$$Lambda$PinnedAppPickerDialog$LY5bhVuNCoIJp2eViaFzWvigR5E
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view2, int i, long j) {
                this.f$0.lambda$onViewCreated$0$PinnedAppPickerDialog(adapterView, view2, i, j);
            }
        });
    }

    public /* synthetic */ void lambda$onViewCreated$0$PinnedAppPickerDialog(AdapterView adapterView, View view, int i, long j) {
        this.mAppPickerCallback.onAppPicked(this.mAppListAdapter.getItem(i));
        dismiss();
    }
}
