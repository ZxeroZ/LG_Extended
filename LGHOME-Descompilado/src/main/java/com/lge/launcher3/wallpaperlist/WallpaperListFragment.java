package com.lge.launcher3.wallpaperlist;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperListFragment extends Fragment {
    public static final int REQUEST_WALLPAPER_SELECT = 0;
    public static final String TAG = "WallpaperListFragment";

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        LGLog.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LGLog.i(TAG, "onCreateView()");
        View viewInflate = inflater.inflate(R.layout.wallpaper_activity_select_list, container, false);
        setAdatper(viewInflate);
        return viewInflate;
    }

    @Override // android.app.Fragment
    public void onResume() {
        LGLog.i(TAG, "onResume()");
        View view = getView();
        if (view != null) {
            setAdatper(view);
        }
        super.onResume();
    }

    private void setAdatper(View view) {
        final List<ResolveInfo> listQueryIntentActivities = getContext().getPackageManager().queryIntentActivities(new Intent("android.intent.action.SET_WALLPAPER"), 0);
        Collections.sort(listQueryIntentActivities, new Comparator<ResolveInfo>() { // from class: com.lge.launcher3.wallpaperlist.WallpaperListFragment.1
            /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
            @Override // java.util.Comparator
            public int compare(ResolveInfo first, ResolveInfo second) {
                int iFindWallpaperIndex = WallpaperListFragment.this.findWallpaperIndex(first.activityInfo.packageName);
                int iFindWallpaperIndex2 = WallpaperListFragment.this.findWallpaperIndex(second.activityInfo.packageName);
                if (iFindWallpaperIndex < iFindWallpaperIndex2) {
                    return -1;
                }
                return iFindWallpaperIndex > iFindWallpaperIndex2 ? 1 : 0;
            }
        });
        WallpaperListAdapter wallpaperListAdapter = new WallpaperListAdapter(getContext(), R.layout.wallpaper_activity_select_list_item, (ArrayList) listQueryIntentActivities);
        ListView listView = (ListView) view.findViewById(R.id.wallpaper_list_view);
        listView.setAdapter((ListAdapter) wallpaperListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lge.launcher3.wallpaperlist.WallpaperListFragment.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                Intent intent = new Intent();
                intent.setClassName(((ResolveInfo) listQueryIntentActivities.get(position)).activityInfo.packageName, ((ResolveInfo) listQueryIntentActivities.get(position)).activityInfo.name);
                try {
                    WallpaperListFragment.this.startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int findWallpaperIndex(String packageName) {
        String[] stringArray = getActivity().getResources().getStringArray(R.array.wallpaper_order_apps);
        int length = stringArray.length;
        for (int i = length - 1; i >= 0; i--) {
            if (packageName.equals(stringArray[i])) {
                return i;
            }
        }
        return length;
    }

    @Override // android.app.Fragment
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (requestCode == 0 && resultCode == -1) {
            Toast.makeText(getActivity(), R.string.sp_wallpaper_changed_NORMAL, 0).show();
            getActivity().finish();
        }
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        LGLog.i(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            getActivity().onBackPressed();
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
