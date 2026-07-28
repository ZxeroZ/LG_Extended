package com.lge.launcher3.debug;

import android.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Process;
import android.widget.Toast;

/* JADX INFO: loaded from: classes.dex */
public class LGHiddenMenuActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(2);
        actionBar.setDisplayOptions(0, 8);
        String[] strArr = {"Feature List", "Res check", "Explorer", "DBViewer"};
        Class[] clsArr = {LGHiddenMenuFeatureList.class, LGHiddenMenuResCheck.class, HiddenMenuExplorerListFragment.class, HiddenMenuDBViewer.class};
        for (int i = 0; i < 4; i++) {
            actionBar.addTab(actionBar.newTab().setText(strArr[i]).setTabListener(new TabListener(this, strArr[i], clsArr[i])));
        }
        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        if (LGHiddenMenuFeatureList.sIschangeRestart) {
            LGHiddenMenuFeatureList.sIschangeRestart = false;
            Process.killProcess(Process.myPid());
        }
    }

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final Activity mActivity;
        private final Bundle mArgs;
        private final Class<T> mClass;
        private Fragment mFragment;
        private final String mTag;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
            this.mActivity = activity;
            this.mTag = tag;
            this.mClass = clz;
            this.mArgs = args;
            Fragment fragmentFindFragmentByTag = activity.getFragmentManager().findFragmentByTag(tag);
            this.mFragment = fragmentFindFragmentByTag;
            if (fragmentFindFragmentByTag == null || fragmentFindFragmentByTag.isDetached()) {
                return;
            }
            FragmentTransaction fragmentTransactionBeginTransaction = activity.getFragmentManager().beginTransaction();
            fragmentTransactionBeginTransaction.detach(this.mFragment);
            fragmentTransactionBeginTransaction.commit();
        }

        @Override // android.app.ActionBar.TabListener
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            Fragment fragment = this.mFragment;
            if (fragment == null) {
                Fragment fragmentInstantiate = Fragment.instantiate(this.mActivity, this.mClass.getName(), this.mArgs);
                this.mFragment = fragmentInstantiate;
                ft.add(R.id.content, fragmentInstantiate, this.mTag);
                return;
            }
            ft.attach(fragment);
        }

        @Override // android.app.ActionBar.TabListener
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            Fragment fragment = this.mFragment;
            if (fragment != null) {
                ft.detach(fragment);
            }
        }

        @Override // android.app.ActionBar.TabListener
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            Toast.makeText(this.mActivity, "Reselected!", 0).show();
        }
    }
}
