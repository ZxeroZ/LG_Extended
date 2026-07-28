package com.android.launcher3;

import android.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AppListAdapter extends ArrayAdapter<AppEntry> {
    private static final String TAG = "SecondaryDisplayLauncher : AppListAdapter";
    private boolean IsAppDrawer;
    private final LayoutInflater mInflater;
    private View mPrevView;

    AppListAdapter(Context context) {
        super(context, R.layout.simple_list_item_2);
        this.mPrevView = null;
        this.IsAppDrawer = false;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mInflater = layoutInflater;
        Log.i(TAG, "create AppListAdapter. mInflater : " + layoutInflater);
    }

    void setData(List<AppEntry> data) {
        Log.i(TAG, "setData - data : " + data);
        clear();
        if (data != null) {
            Log.i(TAG, "setData - data.size() : " + data.size());
            addAll(data);
        }
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "getView - position : " + position + ", convertView : " + convertView + ", parent : " + parent);
        boolean z = this.IsAppDrawer;
        StringBuilder sb = new StringBuilder();
        sb.append("getView - IsAppDrawer : ");
        sb.append(z);
        Log.i(TAG, sb.toString());
        if (convertView == null) {
            convertView = this.mInflater.inflate(com.lge.launcher3.R.layout.app_grid_item, parent, false);
        } else if (this.IsAppDrawer) {
            convertView = this.mInflater.inflate(com.lge.launcher3.R.layout.app_drawer_grid_item, parent, false);
        }
        AppEntry item = getItem(position);
        Log.i(TAG, "getView - item : " + item);
        ((ImageView) convertView.findViewById(com.lge.launcher3.R.id.app_icon)).setImageDrawable(item.getIcon());
        ((TextView) convertView.findViewById(com.lge.launcher3.R.id.app_name)).setText(item.getLabel());
        if (parent != null) {
            if (parent.getId() == com.lge.launcher3.R.id.pinned_app_grid) {
                convertView.setOnGenericMotionListener(PinnedAppListUtils.getOnGenericMotionListener(convertView, position, this));
                setDoubleClickEvent(convertView, position);
            } else if (parent.getId() == com.lge.launcher3.R.id.app_drawer_grid) {
                convertView.setOnGenericMotionListener(getOnGenericMotionListenerInAppDrawer(convertView, position, this));
            }
        }
        Log.i(TAG, "getView - view : " + convertView + ", Visibility :" + convertView.getVisibility());
        return convertView;
    }

    private void setDoubleClickEvent(final View view, final int position) {
        view.setFocusable(true);
        view.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.launcher3.AppListAdapter.1
            private GestureDetector gestureDetector;

            {
                this.gestureDetector = new GestureDetector(AppListAdapter.this.getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: com.android.launcher3.AppListAdapter.1.1
                    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                    public boolean onDoubleTap(MotionEvent e) {
                        AppEntry item = AppListAdapter.this.getItem(position);
                        Log.d(AppListAdapter.TAG, "onDoubleTap : " + item);
                        AppListUtils.launch(AppListAdapter.this.getContext(), item.getLaunchIntent());
                        return super.onDoubleTap(e);
                    }

                    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        return super.onSingleTapConfirmed(e);
                    }

                    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                    public boolean onSingleTapUp(MotionEvent e) {
                        if (AppListAdapter.this.mPrevView != null && AppListAdapter.this.mPrevView != view) {
                            AppListAdapter.this.mPrevView.setSelected(false);
                            AppListAdapter.this.mPrevView.clearFocus();
                        }
                        AppListAdapter.this.mPrevView = view;
                        AppListAdapter.this.mPrevView.requestFocus();
                        AppListAdapter.this.mPrevView.setSelected(true);
                        return super.onSingleTapUp(e);
                    }
                });
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                int toolType = event.getToolType(0);
                Log.d(AppListAdapter.TAG, "onTouch:: toolType = " + toolType + ", Action = " + event.getAction() + ", ButtonState = " + event.getButtonState() + ", Source = " + event.getSource());
                if (toolType == 0 || toolType == 2) {
                    if (event.getAction() == 1) {
                        Log.i(AppListAdapter.TAG, "onTouch:: TOOL_TYPE_UNKNOWN or TOOL_TYPE_STYLUS : launch app");
                        AppListUtils.launch(AppListAdapter.this.getContext(), AppListAdapter.this.getItem(position).getLaunchIntent());
                        return true;
                    }
                } else if (toolType == 1 && event.getAction() == 1 && event.getButtonState() <= 1) {
                    Log.i(AppListAdapter.TAG, "onTouch:: TOOL_TYPE_FINGER : launch app");
                    AppListUtils.launch(AppListAdapter.this.getContext(), AppListAdapter.this.getItem(position).getLaunchIntent());
                    return true;
                }
                this.gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    public View.OnGenericMotionListener getOnGenericMotionListenerInAppDrawer(final View v, final int position, final ArrayAdapter<AppEntry> adapter) {
        return new View.OnGenericMotionListener() { // from class: com.android.launcher3.-$$Lambda$AppListAdapter$8eKvERLBYgKWDQ1aP0P1UY5jxWg
            @Override // android.view.View.OnGenericMotionListener
            public final boolean onGenericMotion(View view, MotionEvent motionEvent) {
                return this.f$0.lambda$getOnGenericMotionListenerInAppDrawer$1$AppListAdapter(adapter, position, v, view, motionEvent);
            }
        };
    }

    public /* synthetic */ boolean lambda$getOnGenericMotionListenerInAppDrawer$1$AppListAdapter(final ArrayAdapter arrayAdapter, final int i, View view, View view2, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 11 && motionEvent.getActionButton() == 2) {
            view2.setFocusable(true);
            view2.requestFocus();
            view2.setSelected(true);
            PinnedAppListUtils.showMenu(view2, arrayAdapter, com.lge.launcher3.R.menu.context_menu, 48, new PopupMenu.OnMenuItemClickListener() { // from class: com.android.launcher3.-$$Lambda$AppListAdapter$huYmkUPBG-XbcND4OUYwlvHcUI0
                @Override // android.widget.PopupMenu.OnMenuItemClickListener
                public final boolean onMenuItemClick(MenuItem menuItem) {
                    return this.f$0.lambda$getOnGenericMotionListenerInAppDrawer$0$AppListAdapter(arrayAdapter, i, menuItem);
                }
            });
            return true;
        }
        if (motionEvent.getAction() != 11 || motionEvent.getActionButton() != 1) {
            return false;
        }
        AppListUtils.launch(view.getContext(), ((AppEntry) arrayAdapter.getItem(i)).getLaunchIntent());
        return true;
    }

    public /* synthetic */ boolean lambda$getOnGenericMotionListenerInAppDrawer$0$AppListAdapter(ArrayAdapter arrayAdapter, int i, MenuItem menuItem) {
        if (menuItem.getItemId() != com.lge.launcher3.R.id.add_app_shortcut) {
            return false;
        }
        addAppShortcut((AppEntry) arrayAdapter.getItem(i));
        AppDrawerDialog.getInstance(this).dismiss();
        return true;
    }

    /* JADX WARN: Not initialized variable reg: 3, insn: 0x0023: INVOKE (r2v7 ?? I:java.lang.StringBuilder), (r3 I:java.lang.String) VIRTUAL call: java.lang.StringBuilder.append(java.lang.String):java.lang.StringBuilder A[MD:(java.lang.String):java.lang.StringBuilder (c)], block:B:4:0x0018 */
    public void addAppShortcut(AppEntry appEntry) {
        String strAppend;
        Log.i(TAG, "addAppShortcut - appEntry : " + appEntry);
        if (appEntry != null) {
            appEntry.getLabel();
            Log.i(TAG, strAppend + "");
        }
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pinned_apps", 0);
        List arrayList = (List) new Gson().fromJson(sharedPreferences.getString("pinned_apps", null), new TypeToken<List<String>>() { // from class: com.android.launcher3.AppListAdapter.2
        }.getType());
        if (arrayList == null) {
            arrayList = new ArrayList();
        }
        String strFlattenToString = appEntry.getComponentName().flattenToString();
        Log.i(TAG, "addAppShortcut - pinnedApps.size() = " + arrayList.size());
        Log.i(TAG, "addAppShortcut - newEntry = " + strFlattenToString);
        if (arrayList.contains(strFlattenToString)) {
            Log.i(TAG, "addAppShortcut - pinnedApps contains " + strFlattenToString);
            return;
        }
        arrayList.add(appEntry.getComponentName().flattenToString());
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        sharedPreferences.edit().putString("pinned_apps", new Gson().toJson(arrayList)).apply();
        editorEdit.apply();
    }

    public void setAppDrawer(boolean AppDrawer) {
        this.IsAppDrawer = AppDrawer;
    }
}
