package com.lge.launcher3.homesettings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toolbar;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.folder.FolderIcon;
import com.lge.launcher3.R;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.widgettray.LGWidgetPreviewLoader;
import com.lge.lgdynamicactionbar.AppBarLayout;

/* JADX INFO: loaded from: classes.dex */
public class IconFramesPrefActivity extends PreferenceActivity {
    private static final String EXTRA_FROM_SETTING_SEARCH = "from_setting_search";
    private static final String EXTRA_SEARCH_ITEM = "search_item";
    public static final String SETTINGS_ICON_FRAMES = "icon_frames";
    private static final String TAG = "IconFramesPrefActivity";

    @Override // android.app.Activity
    public Intent getIntent() {
        Intent intent = super.getIntent();
        intent.putExtra(":android:show_fragment", getFragmentClass().getName());
        intent.putExtra(":android:no_headers", true);
        return intent;
    }

    @Override // android.preference.PreferenceActivity
    protected boolean isValidFragment(String fragmentName) {
        return IconFramesPrefFragment.class.getName().equals(fragmentName);
    }

    Class<? extends Fragment> getFragmentClass() {
        return IconFramesPrefFragment.class;
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DynamicActionBarTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setTitle(getText(R.string.icon_shapes));
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class IconFramesPrefFragment extends PreferenceFragment {
        private static final String SELECTED_ITEM_POS = "selected_item_pos";
        private ArrayAdapter<String> mAdapter;
        private int mCurrentIndex;
        private String[] mItemNames;
        private ListView mList;
        private ImageView mPreviewImage;
        private ArrayMap<String, Integer> mPreviewResMap = new ArrayMap<>();
        private ArrayMap<String, String> mSearchItemsMap = new ArrayMap<>();

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.icon_frames_setting, container, false);
        }

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(SELECTED_ITEM_POS, this.mList.getCheckedItemPosition());
        }

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            initPreviewDrawableMap();
            initSearchItemMap();
            int i = Settings.System.getInt(getActivity().getContentResolver(), IconFramesPrefActivity.SETTINGS_ICON_FRAMES, 0);
            this.mCurrentIndex = i;
            if (savedInstanceState != null) {
                i = savedInstanceState.getInt(SELECTED_ITEM_POS, i);
            }
            if (i < 0 || i >= this.mItemNames.length) {
                return;
            }
            this.mList.setItemChecked(i, true);
            this.mList.smoothScrollToPosition(i);
            this.mPreviewImage.setImageResource(this.mPreviewResMap.get(this.mItemNames[i]).intValue());
        }

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            customzingDAB(view);
            Resources resources = getResources();
            this.mPreviewImage = (ImageView) view.findViewById(R.id.icon_frames_setting_preview);
            this.mItemNames = resources.getStringArray(LGHomeFeature.Config.FEATURE_NEW_ICON_SHAPE_LIST.getValue() ? R.array.icon_shapes_new : R.array.icon_shapes);
            this.mAdapter = new ArrayAdapter<>(getActivity(), R.layout.icon_frames_list_item, this.mItemNames);
            this.mList = (ListView) view.findViewById(R.id.icon_frames_setting_list);
            if (getResources().getConfiguration().getLayoutDirection() == 1) {
                this.mList.setDivider(getResources().getDrawable(R.drawable.listview_divider_background_with_checkbox_rtl, getContext().getTheme()));
            }
            this.mList.setAdapter((ListAdapter) this.mAdapter);
            this.mList.setItemsCanFocus(false);
            this.mList.setChoiceMode(1);
            this.mList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lge.launcher3.homesettings.IconFramesPrefActivity.IconFramesPrefFragment.1
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                    if (position < 0 || position >= IconFramesPrefFragment.this.mItemNames.length) {
                        return;
                    }
                    IconFramesPrefFragment.this.mPreviewImage.setImageResource(((Integer) IconFramesPrefFragment.this.mPreviewResMap.get(IconFramesPrefFragment.this.mItemNames[position])).intValue());
                }
            });
            setListViewHeightBasedOnChildren(this.mList);
            ((Button) view.findViewById(R.id.footer_ok)).setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.homesettings.IconFramesPrefActivity.IconFramesPrefFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    IconFramesPrefFragment.this.handleOkButtonPressed();
                }
            });
        }

        public void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter adapter = listView.getAdapter();
            if (adapter == null) {
                return;
            }
            int count = adapter.getCount();
            View view = adapter.getView(0, null, listView);
            view.measure(0, 0);
            int measuredHeight = (view.getMeasuredHeight() * count) + (listView.getDividerHeight() * (count - 1)) + 0;
            ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
            layoutParams.height = measuredHeight;
            listView.setLayoutParams(layoutParams);
            listView.requestLayout();
        }

        private void initPreviewDrawableMap() {
            this.mPreviewResMap.clear();
            this.mPreviewResMap.put(getResources().getString(R.string.icon_shape_original), Integer.valueOf(R.drawable.lg_homescreen_preview_original));
            this.mPreviewResMap.put(getResources().getString(R.string.icon_shape_rounded_square), Integer.valueOf(R.drawable.lg_homescreen_preview_rounded_square));
            this.mPreviewResMap.put(getResources().getString(R.string.icon_shape_round), Integer.valueOf(R.drawable.lg_homescreen_preview_round));
            this.mPreviewResMap.put(getResources().getString(R.string.icon_shape_cylinder), Integer.valueOf(R.drawable.lg_homescreen_preview_cylinder));
            this.mPreviewResMap.put(getResources().getString(R.string.icon_shape_square), Integer.valueOf(R.drawable.lg_homescreen_preview_square));
            this.mPreviewResMap.put(getResources().getString(R.string.icon_shape_circle), Integer.valueOf(R.drawable.lg_homescreen_preview_circle));
        }

        private void initSearchItemMap() {
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ORIGINAL, getString(R.string.icon_shape_original));
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ROUNDED_SQUARE, getString(R.string.icon_shape_rounded_square));
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_ROUND, getString(R.string.icon_shape_round));
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_ICON_FRAME_TYPE_CYLINDER, getString(R.string.icon_shape_cylinder));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleOkButtonPressed() {
            boolean z;
            LGUserLog.send(getContext(), LGUserLog.FEATURENAME_CHANGEICONSHAPE);
            int checkedItemPosition = this.mList.getCheckedItemPosition();
            if (checkedItemPosition < 0 || checkedItemPosition >= this.mItemNames.length || checkedItemPosition == this.mCurrentIndex) {
                z = false;
            } else {
                LGLog.i(IconFramesPrefActivity.TAG, "icon_frames : " + checkedItemPosition);
                z = true;
            }
            if (z) {
                if (!SettingsSearchUtils.hasOOSNewSettingSearchFeature(getActivity())) {
                    Settings.System.putIntForUser(getContext().getContentResolver(), IconFramesPrefActivity.SETTINGS_ICON_FRAMES, checkedItemPosition, -2);
                }
                LauncherAppState launcherAppState = LauncherAppState.getInstance(getContext());
                if (launcherAppState != null) {
                    launcherAppState.getIconCache().clearIconDB();
                    ((LGWidgetPreviewLoader) launcherAppState.getWidgetCache()).clearCacheDB();
                    FolderIcon.clearFolderCache();
                    LiveIconManager.getInstance(getContext()).setForceUpdate();
                    launcherAppState.getModel().forceReload();
                }
                if (!SettingsSearchUtils.hasOOSNewSettingSearchFeature(getActivity())) {
                    getActivity().setResult(-1);
                } else {
                    getActivity().setResult(-1);
                    Settings.System.putIntForUser(getContext().getContentResolver(), IconFramesPrefActivity.SETTINGS_ICON_FRAMES, checkedItemPosition, -2);
                }
            } else {
                getActivity().setResult(0);
            }
            getActivity().finish();
        }

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onStart() {
            super.onStart();
            if (DDTUtils.isAdditionalThemeApplied(getContext()) || DDTUtils.isAdditionalIconThemeApplied(getContext())) {
                LGLog.i(IconFramesPrefActivity.TAG, "Not supported to the additional theme");
                getActivity().finish();
            }
        }

        @Override // android.app.Fragment
        public void onResume() {
            super.onResume();
            onCheckFromSettingsSearch(getActivity().getIntent());
        }

        private void onCheckFromSettingsSearch(Intent intent) {
            String stringExtra = intent.getStringExtra(IconFramesPrefActivity.EXTRA_SEARCH_ITEM);
            String str = this.mSearchItemsMap.get(stringExtra);
            boolean booleanExtra = intent.getBooleanExtra(IconFramesPrefActivity.EXTRA_FROM_SETTING_SEARCH, false);
            if (stringExtra == null || stringExtra.equals("") || str == null) {
                return;
            }
            if (booleanExtra) {
                SearchUtil.highlightPreference(getActivity(), this.mList, getItemPosition(this.mList, str));
            }
            intent.removeExtra(IconFramesPrefActivity.EXTRA_SEARCH_ITEM);
            intent.removeExtra(IconFramesPrefActivity.EXTRA_FROM_SETTING_SEARCH);
        }

        private int getItemPosition(final ListView listView, final String strItem) {
            for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                String str = (String) listView.getAdapter().getItem(i);
                if (str != null && strItem.equals(str)) {
                    return i;
                }
            }
            return -1;
        }

        public void customzingDAB(View view) {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.launcher_toolbar);
            if (toolbar != null) {
                Activity activity = getActivity();
                activity.setActionBar(toolbar);
                activity.getActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getActionBar().setDisplayShowTitleEnabled(false);
            }
            AppBarLayout appBarLayoutFindViewById = view.findViewById(R.id.launcher_app_bar);
            if (appBarLayoutFindViewById != null) {
                appBarLayoutFindViewById.setExpanded(false);
                appBarLayoutFindViewById.setAppBarTitle(getResources().getString(R.string.icon_shapes));
            }
        }
    }
}
