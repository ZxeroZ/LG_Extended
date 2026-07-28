package com.lge.launcher3.homesettings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroupAdapter;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.lge.launcher3.R;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SearchUtil {
    private static final int DELAY_HIGHLIGHT_DURATION_MILLIS = 600;
    private static final int DELAY_SELECTION_DURATION_MILLIS = 50;

    private static List<String> setPreferenceKeyList(PreferenceScreen preferenceScreen) {
        ArrayList arrayList = new ArrayList();
        int preferenceCount = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            arrayList.add(preference.getKey());
            if (preference instanceof PreferenceCategory) {
                PreferenceCategory preferenceCategory = (PreferenceCategory) preference;
                for (int i2 = 0; i2 < preferenceCategory.getPreferenceCount(); i2++) {
                    arrayList.add(preferenceCategory.getPreference(i2).getKey());
                }
            }
        }
        return arrayList;
    }

    private static int canUseListViewForHighLighting(String key, ListView listView, List<String> keyList) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null || !(adapter instanceof PreferenceGroupAdapter)) {
            return -1;
        }
        return findListPositionFromKey(adapter, key, keyList);
    }

    private static int findListPositionFromKey(ListAdapter adapter, String key, List<String> keyList) {
        String key2;
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            Object item = adapter.getItem(i);
            if ((item instanceof Preference) && (key2 = ((Preference) item).getKey()) != null && key2.equals(key)) {
                return keyList.indexOf(key2);
            }
        }
        return -1;
    }

    public static void highlightPreference(Context context, String key, final ListView _listView, PreferenceScreen preferenceScreen) {
        final Drawable drawable = context.getDrawable(R.drawable.preference_highlight);
        final int iCanUseListViewForHighLighting = canUseListViewForHighLighting(key, _listView, setPreferenceKeyList(preferenceScreen));
        if (iCanUseListViewForHighLighting >= 0) {
            PreferenceGroupAdapter adapter = _listView.getAdapter();
            PreferenceGroupAdapter preferenceGroupAdapter = adapter;
            preferenceGroupAdapter.setHighlightedDrawable(drawable);
            preferenceGroupAdapter.setHighlighted(iCanUseListViewForHighLighting);
            if (iCanUseListViewForHighLighting == adapter.getCount() - 2) {
                _listView.setTranscriptMode(2);
            }
            _listView.setSelection(iCanUseListViewForHighLighting);
            _listView.postDelayed(new Runnable() { // from class: com.lge.launcher3.homesettings.SearchUtil.1
                @Override // java.lang.Runnable
                public void run() {
                    final View childAt;
                    int firstVisiblePosition = iCanUseListViewForHighLighting - _listView.getFirstVisiblePosition();
                    if (firstVisiblePosition < 0 || firstVisiblePosition >= _listView.getChildCount() || (childAt = _listView.getChildAt(firstVisiblePosition)) == null) {
                        return;
                    }
                    drawable.setHotspot(childAt.getWidth() / 2, childAt.getHeight() / 2);
                    childAt.setSelected(true);
                    childAt.setPressed(true);
                    childAt.postDelayed(new Runnable() { // from class: com.lge.launcher3.homesettings.SearchUtil.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            childAt.setPressed(false);
                            childAt.setSelected(false);
                        }
                    }, 50L);
                }
            }, 600L);
        }
    }

    public static void highlightPreference(Context context, final ListView listView, final int position) {
        final Drawable drawable = context.getDrawable(R.drawable.preference_highlight);
        if (position >= 0) {
            listView.setSelection(position);
            listView.postDelayed(new Runnable() { // from class: com.lge.launcher3.homesettings.SearchUtil.2
                @Override // java.lang.Runnable
                public void run() {
                    final View childAt = listView.getChildAt(position);
                    if (childAt != null) {
                        drawable.setHotspot(childAt.getWidth() / 2, childAt.getHeight() / 2);
                        childAt.setSelected(true);
                        childAt.setPressed(true);
                        childAt.setBackground(drawable);
                        childAt.postDelayed(new Runnable() { // from class: com.lge.launcher3.homesettings.SearchUtil.2.1
                            @Override // java.lang.Runnable
                            public void run() {
                                childAt.setPressed(false);
                                childAt.setSelected(false);
                            }
                        }, 50L);
                    }
                }
            }, 600L);
        }
    }
}
