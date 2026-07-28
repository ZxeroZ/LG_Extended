package com.lge.launcher3.homesettings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toolbar;
import androidx.preference.PreferenceFragment;
import com.lge.launcher3.R;
import com.lge.launcher3.receiver.PendingIntentObjectList;
import com.lge.launcher3.screeneffect.ScreenEffectManager;
import com.lge.launcher3.screeneffect.ScreenEffectPreviewManager;
import com.lge.launcher3.screeneffect.ScreenEffectPreviewTargetManager;
import com.lge.launcher3.screeneffect.ScreenEffectUtils;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.Utilities;
import com.lge.lgdynamicactionbar.AppBarLayout;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectPrefActivity extends PreferenceActivity {
    private static final String EXTRA_FROM_SETTING_SEARCH = "from_setting_search";
    private static final String EXTRA_SEARCH_ITEM = "search_item";
    private static final String TAG = "com.lge.launcher3.homesettings.ScreenEffectPrefActivity";

    @Override // android.app.Activity
    public Intent getIntent() {
        Intent intent = super.getIntent();
        intent.putExtra(":android:show_fragment", getFragmentClass().getName());
        intent.putExtra(":android:no_headers", true);
        return intent;
    }

    @Override // android.preference.PreferenceActivity
    protected boolean isValidFragment(String fragmentName) {
        return ScreenEffectPrefFragment.class.getName().equals(fragmentName);
    }

    Class<? extends Fragment> getFragmentClass() {
        return ScreenEffectPrefFragment.class;
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DynamicActionBarTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setTitle(getText(R.string.menu_screen_effect));
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ScreenEffectPrefFragment extends PreferenceFragment {
        private static final String SELECTED_ITEM = "selected_item";
        private int mCurrentIndex;
        private ArrayMap<String, String> mSearchItemsMap = new ArrayMap<>();
        private ScreenEffectListLayout mScreenEffectListLayout = null;
        private Context mContext = null;

        @Override // androidx.preference.PreferenceFragment
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        }

        @Override // androidx.preference.PreferenceFragment, android.app.Fragment
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            ScreenEffectListLayout screenEffectListLayout = this.mScreenEffectListLayout;
            if (screenEffectListLayout != null) {
                outState.putInt(SELECTED_ITEM, screenEffectListLayout.getCheckedItemPosition());
            }
        }

        @Override // androidx.preference.PreferenceFragment, android.app.Fragment
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.screen_effect_setting, container, false);
        }

        @Override // android.app.Fragment
        public void onActivityCreated(Bundle savedInstanceState) {
            int i;
            ScreenEffectListLayout screenEffectListLayout;
            super.onActivityCreated(savedInstanceState);
            initSearchItemMap();
            if (savedInstanceState == null || this.mCurrentIndex == (i = savedInstanceState.getInt(SELECTED_ITEM, this.mCurrentIndex)) || (screenEffectListLayout = this.mScreenEffectListLayout) == null) {
                return;
            }
            screenEffectListLayout.setItemCheckedAndSelection(i);
        }

        @Override // androidx.preference.PreferenceFragment, android.app.Fragment
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            customzingDAB(view);
            this.mCurrentIndex = HomeSettingsSharedPreferences.getSelectedScreenEffect(getActivity());
            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.screen_effect_layout);
            ScreenEffectListLayout screenEffectListLayout = new ScreenEffectListLayout(getActivity());
            this.mScreenEffectListLayout = screenEffectListLayout;
            screenEffectListLayout.setItemCheckedAndSelection(this.mCurrentIndex);
            frameLayout.addView(this.mScreenEffectListLayout);
            ((Button) view.findViewById(R.id.footer_ok)).setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.homesettings.ScreenEffectPrefActivity.ScreenEffectPrefFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    ScreenEffectPrefFragment.this.handleOkButtonPressed();
                }
            });
        }

        @Override // android.app.Fragment
        public void onResume() {
            super.onResume();
            onCheckFromSettingsSearch(getActivity().getIntent());
        }

        @Override // android.app.Fragment
        public void onPause() {
            super.onPause();
            ScreenEffectListLayout screenEffectListLayout = this.mScreenEffectListLayout;
            if (screenEffectListLayout != null) {
                screenEffectListLayout.reset();
            }
        }

        @Override // android.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            ScreenEffectListLayout screenEffectListLayout = this.mScreenEffectListLayout;
            if (screenEffectListLayout != null) {
                screenEffectListLayout.destroy();
                this.mScreenEffectListLayout = null;
            }
            this.mContext = null;
        }

        private void initSearchItemMap() {
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_SLIDE, getString(R.string.menu_screen_effect_basic));
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_BREEZE, getString(R.string.menu_screen_effect_breeze));
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_PANORAMA, getString(R.string.menu_screen_effect_panorama));
            this.mSearchItemsMap.put(HomeSettingsConstant.KEY_HOMESETTINGS_SCREEN_EFFECT_CAROUSEL, getString(R.string.menu_screen_effect_carousel));
        }

        private void onCheckFromSettingsSearch(Intent intent) {
            ScreenEffectListLayout screenEffectListLayout;
            String stringExtra = intent.getStringExtra(ScreenEffectPrefActivity.EXTRA_SEARCH_ITEM);
            String str = this.mSearchItemsMap.get(stringExtra);
            boolean booleanExtra = intent.getBooleanExtra(ScreenEffectPrefActivity.EXTRA_FROM_SETTING_SEARCH, false);
            if (stringExtra == null || stringExtra.equals("") || str == null) {
                return;
            }
            if (booleanExtra && (screenEffectListLayout = this.mScreenEffectListLayout) != null) {
                SearchUtil.highlightPreference(getActivity(), this.mScreenEffectListLayout.getListView(), screenEffectListLayout.getItemPosition(str));
            }
            intent.removeExtra(ScreenEffectPrefActivity.EXTRA_SEARCH_ITEM);
            intent.removeExtra(ScreenEffectPrefActivity.EXTRA_FROM_SETTING_SEARCH);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleOkButtonPressed() {
            Activity activity = getActivity();
            LGUserLog.send(activity, LGUserLog.FEATURENAME_SHOWEFFECT);
            int checkedItemPosition = this.mScreenEffectListLayout.getCheckedItemPosition();
            if (checkedItemPosition != this.mCurrentIndex) {
                LGLog.i(ScreenEffectPrefActivity.TAG, "screen_effect : " + checkedItemPosition);
                HomeSettingsSharedPreferences.putSelectedScreenEffect(activity, checkedItemPosition);
                if (Utilities.getCoverDisplayState() == 2 || Utilities.getCoverDisplayState() == 3 || Utilities.getSwivelDisplayState(activity) == 2) {
                    Intent intent = new Intent(PendingIntentObjectList.KillProcessHandler.KILL_PROCESS_INTENT);
                    intent.setPackage("com.lge.secondlauncher");
                    activity.sendBroadcast(intent);
                }
                ScreenEffectManager.getInstance(activity).updateSelectedScreenEffectType();
                getActivity().setResult(-1);
            } else {
                getActivity().setResult(0);
            }
            getActivity().finish();
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
                appBarLayoutFindViewById.setAppBarTitle(getResources().getString(R.string.menu_screen_effect));
            }
        }
    }

    public static class ScreenEffectListLayout extends FrameLayout implements AdapterView.OnItemClickListener {
        public final String TAG;
        private ListView mListView;
        private ScreenEffectPreviewManager mScreenEffectPreviewManager;

        public ScreenEffectListLayout(Context context) {
            super(context);
            this.TAG = ScreenEffectListLayout.class.getSimpleName();
            this.mListView = null;
            this.mScreenEffectPreviewManager = null;
            setupListView(context);
            this.mScreenEffectPreviewManager = new ScreenEffectPreviewManager(context);
            ScreenEffectPreviewTargetManager.getInstance(context).setParent(this);
        }

        private void setupListView(Context context) {
            ListView listView = new ListView(context);
            this.mListView = listView;
            listView.setAdapter((ListAdapter) getAdapter(context));
            this.mListView.setChoiceMode(1);
            this.mListView.setClipChildren(false);
            this.mListView.setClipToPadding(false);
            this.mListView.setOnItemClickListener(this);
            if (getResources().getConfiguration().getLayoutDirection() == 1) {
                this.mListView.setDivider(getResources().getDrawable(R.drawable.listview_divider_background_with_checkbox_rtl, getContext().getTheme()));
            } else {
                this.mListView.setDivider(getResources().getDrawable(R.drawable.listview_divider_background_with_checkbox, getContext().getTheme()));
            }
            addView(this.mListView);
        }

        private ArrayAdapter<String> getAdapter(Context context) {
            return new ArrayAdapter<>(context, R.layout.icon_frames_list_item, ScreenEffectUtils.getScreenEffectList(context));
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (view != null) {
                this.mListView.setSelectionFromTop(position, view.getTop());
            } else {
                this.mListView.setSelection(position);
            }
            this.mScreenEffectPreviewManager.startPreviewAnimation(position);
        }

        public void setItemCheckedAndSelection(int position) {
            ListView listView = this.mListView;
            if (listView != null) {
                listView.setItemChecked(position, true);
                this.mListView.smoothScrollToPosition(position);
                this.mListView.setSelection(position);
            }
        }

        public int getCheckedItemPosition() {
            return this.mListView.getCheckedItemPosition();
        }

        public int getItemPosition(final String strIem) {
            for (int i = 0; i < this.mListView.getAdapter().getCount(); i++) {
                String str = (String) this.mListView.getAdapter().getItem(i);
                if (str != null && strIem.equals(str)) {
                    return i;
                }
            }
            return -1;
        }

        public ListView getListView() {
            return this.mListView;
        }

        @Override // android.view.ViewGroup
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            boolean zDrawChild = this.mScreenEffectPreviewManager.drawChild(canvas, child);
            return !zDrawChild ? super.drawChild(canvas, child, drawingTime) : zDrawChild;
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (this.mScreenEffectPreviewManager.isPreviewAnimationStarted()) {
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }

        public void reset() {
            this.mScreenEffectPreviewManager.cancelPreviewAnimation();
        }

        public void destroy() {
            ScreenEffectPreviewManager screenEffectPreviewManager = this.mScreenEffectPreviewManager;
            if (screenEffectPreviewManager != null) {
                screenEffectPreviewManager.destroy();
                this.mScreenEffectPreviewManager = null;
            }
            ListView listView = this.mListView;
            if (listView != null) {
                listView.setLayerType(0, null);
                this.mListView = null;
            }
            removeAllViews();
        }
    }
}
