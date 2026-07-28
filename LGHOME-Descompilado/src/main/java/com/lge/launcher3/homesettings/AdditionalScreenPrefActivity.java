package com.lge.launcher3.homesettings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
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
import android.widget.Toast;
import android.widget.Toolbar;
import com.android.internal.util.ArrayUtils;
import com.lge.launcher3.R;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.operator.GoogleNowManager;
import com.lge.launcher3.operator.VZWSideScreenManager;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.lgdynamicactionbar.AppBarLayout;

/* JADX INFO: loaded from: classes.dex */
public class AdditionalScreenPrefActivity extends PreferenceActivity {
    private static final String EXTRA_ADDITIONAL_SCREEN_VALUE_CHANGE = "additional_screen_value_change";
    private static final String EXTRA_FROM_SETTING_SEARCH = "from_setting_search";
    private static final String EXTRA_SEARCH_ITEM = "search_item";
    private static final String TAG = "AdditionalScreenPrefActivity";

    @Override // android.app.Activity
    public Intent getIntent() {
        Intent intent = super.getIntent();
        intent.putExtra(":android:show_fragment", getFragmentClass().getName());
        intent.putExtra(":android:no_headers", true);
        return intent;
    }

    @Override // android.preference.PreferenceActivity
    protected boolean isValidFragment(String fragmentName) {
        return AdditionalScreenPrefFragment.class.getName().equals(fragmentName);
    }

    Class<? extends Fragment> getFragmentClass() {
        return AdditionalScreenPrefFragment.class;
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DynamicActionBarTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setTitle(getText(R.string.additional_screen_name));
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class AdditionalScreenPrefFragment extends PreferenceFragment {
        private static final String SELECTED_ITEM_TITLE = "selected_item_title";
        private ArrayAdapter<String> mAdapter;
        private String[] mItemNames;
        private ListView mList;
        private ImageView mPreviewImage;
        private ArrayMap<String, Integer> mPreviewResMap = new ArrayMap<>();
        private String mCurrentSettingTitle = "";
        private ArrayMap<String, String> mSearchItemsMap = new ArrayMap<>();

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.additional_screen_setting, container, false);
        }

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString(SELECTED_ITEM_TITLE, (String) this.mList.getItemAtPosition(this.mList.getCheckedItemPosition()));
        }

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Context applicationContext = getActivity().getApplicationContext();
            initPreviewDrawableMap();
            initSearchItemMap();
            if (HomeSettingsSharedPreferences.getVZWSideScreenEnabled(applicationContext) && VZWSideScreenManager.isAppEnabled()) {
                this.mCurrentSettingTitle = getString(R.string.vzw_sidescreen_name);
                SettingsSearchUtils.updateSmartBulletinOnOff(applicationContext, false);
            } else if (GoogleNowManager.isAvailable(getContext())) {
                this.mCurrentSettingTitle = getString(R.string.google_feed);
                SettingsSearchUtils.updateSmartBulletinOnOff(applicationContext, false);
            } else if (SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(applicationContext)) {
                this.mCurrentSettingTitle = getString(R.string.smartbulletin);
            } else {
                this.mCurrentSettingTitle = getString(R.string.sp_none_home_NORMAL);
            }
            String string = savedInstanceState != null ? savedInstanceState.getString(SELECTED_ITEM_TITLE, this.mCurrentSettingTitle) : this.mCurrentSettingTitle;
            for (int i = 0; i < this.mList.getAdapter().getCount(); i++) {
                if (string.equals(this.mList.getItemAtPosition(i))) {
                    this.mList.setItemChecked(i, true);
                    this.mList.smoothScrollToPosition(i);
                    this.mPreviewImage.setImageResource(this.mPreviewResMap.get(this.mItemNames[i]).intValue());
                }
            }
        }

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            customzingDAB(view);
            Activity activity = getActivity();
            Resources resources = getResources();
            this.mPreviewImage = (ImageView) view.findViewById(R.id.additional_screen_setting_preview);
            this.mItemNames = resources.getStringArray(R.array.additional_screen);
            if (!VZWSideScreenManager.isAppEnabled()) {
                this.mItemNames = ArrayUtils.removeString(this.mItemNames, getString(R.string.vzw_sidescreen_name));
            }
            if (!LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue() || !GoogleNowManager.isAppEnabled()) {
                this.mItemNames = ArrayUtils.removeString(this.mItemNames, getString(R.string.google_feed));
            }
            this.mAdapter = new ArrayAdapter<>(activity, R.layout.icon_frames_list_item, this.mItemNames);
            this.mList = (ListView) view.findViewById(R.id.additional_screen_setting_list);
            if (getResources().getConfiguration().getLayoutDirection() == 1) {
                this.mList.setDivider(getResources().getDrawable(R.drawable.listview_divider_background_with_checkbox_rtl, getContext().getTheme()));
            }
            this.mList.setAdapter((ListAdapter) this.mAdapter);
            this.mList.setItemsCanFocus(false);
            this.mList.setChoiceMode(1);
            this.mList.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lge.launcher3.homesettings.AdditionalScreenPrefActivity.AdditionalScreenPrefFragment.1
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                    Integer num;
                    if (position < 0 || position >= AdditionalScreenPrefFragment.this.mItemNames.length || (num = (Integer) AdditionalScreenPrefFragment.this.mPreviewResMap.get(AdditionalScreenPrefFragment.this.mItemNames[position])) == null) {
                        return;
                    }
                    AdditionalScreenPrefFragment.this.mPreviewImage.setImageResource(num.intValue());
                }
            });
            ((Button) view.findViewById(R.id.footer_ok)).setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.homesettings.AdditionalScreenPrefActivity.AdditionalScreenPrefFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    AdditionalScreenPrefFragment.this.handleOkButtonPressed();
                }
            });
        }

        private void initPreviewDrawableMap() {
            this.mPreviewResMap.clear();
            if (VZWSideScreenManager.isAppEnabled()) {
                this.mPreviewResMap.put(getString(R.string.vzw_sidescreen_name), Integer.valueOf(R.drawable.lg_homescreen_preview_appflash_vzw));
            }
            if (LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_NOW.getValue()) {
                this.mPreviewResMap.put(getString(R.string.google_feed), Integer.valueOf(R.drawable.lg_homescreen_preview_google_feed));
            }
            this.mPreviewResMap.put(getString(R.string.smartbulletin), Integer.valueOf(R.drawable.lg_homescreen_preview_smartbulletin_vzw));
            this.mPreviewResMap.put(getString(R.string.sp_none_home_NORMAL), Integer.valueOf(R.drawable.lg_homescreen_preview_nonehelp_vzw));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleOkButtonPressed() {
            Context applicationContext = getActivity().getApplicationContext();
            LGUserLog.send(applicationContext, LGUserLog.FEATURENAME_CHANGEADDITIONANSCREEN);
            Object itemAtPosition = this.mList.getItemAtPosition(this.mList.getCheckedItemPosition());
            if (!this.mCurrentSettingTitle.equals(itemAtPosition)) {
                LGLog.i(AdditionalScreenPrefActivity.TAG, "Additional screen : " + itemAtPosition);
                if (getString(R.string.vzw_sidescreen_name).equals(itemAtPosition)) {
                    turnOnVZWSideScreen(applicationContext);
                } else if (getString(R.string.google_feed).equals(itemAtPosition)) {
                    turnOnGoogleFeed(applicationContext);
                } else if (getString(R.string.smartbulletin).equals(itemAtPosition)) {
                    turnOnSmartBulletin(applicationContext);
                } else {
                    turnOffAll(applicationContext);
                }
                restartHome(applicationContext);
            }
            getActivity().finish();
        }

        private void turnOffOtherSetting(Context context) {
            SBHomeDataBaseUtil.turnOffSmartBulletin(context);
            SettingsSearchUtils.updateSmartBulletinOnOff(context, false);
            if (HomeSettingsSharedPreferences.getContinuousLoopEnabled(context)) {
                HomeSettingsSharedPreferences.setContinuousLoopEnabled(context, false);
                Toast.makeText(context, R.string.sp_loop_turned_off, 0).show();
            }
        }

        private void turnOnVZWSideScreen(Context context) {
            turnOffOtherSetting(context);
            Boolean bool = true;
            HomeSettingsSharedPreferences.setVZWSideScreenEnabled(context, bool.booleanValue());
        }

        private void turnOnGoogleFeed(Context context) {
            turnOffOtherSetting(context);
            HomeSettingsSharedPreferences.setGoogleNowEnabled(getActivity(), true);
        }

        private void turnOnSmartBulletin(Context context) {
            SBHomeDataBaseUtil.turnOnSmartBulletin(context);
            SettingsSearchUtils.updateSmartBulletinOnOff(context, true);
            Boolean bool = false;
            HomeSettingsSharedPreferences.setVZWSideScreenEnabled(context, bool.booleanValue());
            HomeSettingsSharedPreferences.setGoogleNowEnabled(getActivity(), false);
        }

        private void turnOffAll(Context context) {
            SBHomeDataBaseUtil.turnOffSmartBulletin(context);
            SettingsSearchUtils.updateSmartBulletinOnOff(context, false);
            Boolean bool = false;
            HomeSettingsSharedPreferences.setVZWSideScreenEnabled(context, bool.booleanValue());
            HomeSettingsSharedPreferences.setGoogleNowEnabled(getActivity(), false);
        }

        private void restartHome(Context context) {
            Intent intent = new Intent(IntentConst.Action.ACTION_KILL_PROCESS.getValue(context));
            intent.putExtra(AdditionalScreenPrefActivity.EXTRA_ADDITIONAL_SCREEN_VALUE_CHANGE, true);
            context.sendBroadcast(intent);
        }

        @Override // android.app.Fragment
        public void onResume() {
            super.onResume();
            onCheckFromSettingsSearch(getActivity().getIntent());
        }

        private void onCheckFromSettingsSearch(Intent intent) {
            String stringExtra = intent.getStringExtra(AdditionalScreenPrefActivity.EXTRA_SEARCH_ITEM);
            String str = this.mSearchItemsMap.get(stringExtra);
            boolean booleanExtra = intent.getBooleanExtra(AdditionalScreenPrefActivity.EXTRA_FROM_SETTING_SEARCH, false);
            if (stringExtra == null || stringExtra.equals("") || str == null) {
                return;
            }
            if (booleanExtra) {
                SearchUtil.highlightPreference(getActivity(), this.mList, getItemPosition(this.mList, str));
            }
            intent.removeExtra(AdditionalScreenPrefActivity.EXTRA_SEARCH_ITEM);
            intent.removeExtra(AdditionalScreenPrefActivity.EXTRA_FROM_SETTING_SEARCH);
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

        private void initSearchItemMap() {
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_APPFLASH, getString(R.string.vzw_sidescreen_name));
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_SMARTBULLETIN, getString(R.string.smartbulletin));
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_GOOGLE_FEED, getString(R.string.google_feed));
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_LEFT_HOME_SCREEN_NONE, getString(R.string.sp_none_home_NORMAL));
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
                appBarLayoutFindViewById.setAppBarTitle(getResources().getString(R.string.additional_screen_name));
            }
        }
    }
}
