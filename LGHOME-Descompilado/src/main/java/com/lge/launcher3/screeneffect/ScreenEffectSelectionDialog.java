package com.lge.launcher3.screeneffect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import com.lge.launcher3.R;
import com.lge.launcher3.receiver.PendingIntentObjectList;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class ScreenEffectSelectionDialog {
    public static final String TAG = "ScreenEffectSelectionDialog";

    public static void show(Context contextActivity, Context contextPackage, DialogInterface.OnClickListener positiveListerner) {
        if (contextActivity instanceof Activity) {
            String simpleName = ScreenEffectSelectionDialogFragment.class.getSimpleName();
            FragmentManager fragmentManager = ((Activity) contextActivity).getFragmentManager();
            FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
            Fragment fragmentFindFragmentByTag = fragmentManager.findFragmentByTag(simpleName);
            if (fragmentFindFragmentByTag != null) {
                fragmentTransactionBeginTransaction.remove(fragmentFindFragmentByTag);
            }
            new ScreenEffectSelectionDialogFragment(contextPackage, positiveListerner).show(fragmentTransactionBeginTransaction, simpleName);
        }
    }

    public static class ScreenEffectSelectionDialogFragment extends DialogFragment {
        private Context mContext;
        private int mOldPosition;
        private int mOrientation;
        private DialogInterface.OnClickListener mPositiveListerner;
        private ScreenEffectListLayout mScreenEffectListLayout;

        public ScreenEffectSelectionDialogFragment() {
            this.mContext = null;
            this.mPositiveListerner = null;
            this.mOldPosition = -1;
            this.mScreenEffectListLayout = null;
            this.mOrientation = -1;
        }

        public ScreenEffectSelectionDialogFragment(Context context, DialogInterface.OnClickListener positiveListerner) {
            this.mContext = null;
            this.mPositiveListerner = null;
            this.mOldPosition = -1;
            this.mScreenEffectListLayout = null;
            this.mOrientation = -1;
            this.mContext = context;
            this.mPositiveListerner = positiveListerner;
            this.mOldPosition = HomeSettingsSharedPreferences.getSelectedScreenEffect(context);
            ScreenEffectListLayout screenEffectListLayout = new ScreenEffectListLayout(context);
            this.mScreenEffectListLayout = screenEffectListLayout;
            screenEffectListLayout.setItemCheckedAndSelection(this.mOldPosition);
            this.mOrientation = this.mContext.getResources().getConfiguration().orientation;
        }

        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (this.mContext == null) {
                return null;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setTitle(R.string.menu_screen_effect);
            builder.setView(this.mScreenEffectListLayout);
            builder.setPositiveButton(R.string.rename_action, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.screeneffect.ScreenEffectSelectionDialog.ScreenEffectSelectionDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    int checkedItemPosition;
                    if (ScreenEffectSelectionDialogFragment.this.mScreenEffectListLayout == null || ScreenEffectSelectionDialogFragment.this.mOldPosition == (checkedItemPosition = ScreenEffectSelectionDialogFragment.this.mScreenEffectListLayout.getCheckedItemPosition())) {
                        return;
                    }
                    HomeSettingsSharedPreferences.putSelectedScreenEffect(ScreenEffectSelectionDialogFragment.this.mContext, checkedItemPosition);
                    if (Utilities.getCoverDisplayState() != 2) {
                        Utilities.getCoverDisplayState();
                    }
                    Intent intent = new Intent(PendingIntentObjectList.KillProcessHandler.KILL_PROCESS_INTENT);
                    intent.setPackage("com.lge.secondlauncher");
                    ScreenEffectSelectionDialogFragment.this.mContext.sendBroadcast(intent);
                    if (ScreenEffectSelectionDialogFragment.this.mPositiveListerner != null) {
                        ScreenEffectSelectionDialogFragment.this.mPositiveListerner.onClick(dialog, which);
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel_action, (DialogInterface.OnClickListener) null);
            AlertDialog alertDialogCreate = builder.create();
            alertDialogCreate.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.lge.launcher3.screeneffect.ScreenEffectSelectionDialog.ScreenEffectSelectionDialogFragment.2
                @Override // android.content.DialogInterface.OnShowListener
                public void onShow(DialogInterface dialog) {
                }
            });
            return alertDialogCreate;
        }

        @Override // android.app.Fragment
        public void onPause() {
            super.onPause();
            ScreenEffectListLayout screenEffectListLayout = this.mScreenEffectListLayout;
            if (screenEffectListLayout != null) {
                screenEffectListLayout.reset();
            }
        }

        @Override // android.app.Fragment, android.content.ComponentCallbacks
        public void onConfigurationChanged(Configuration newConfig) {
            ScreenEffectListLayout screenEffectListLayout;
            super.onConfigurationChanged(newConfig);
            if (newConfig.orientation == this.mOrientation || (screenEffectListLayout = this.mScreenEffectListLayout) == null) {
                return;
            }
            screenEffectListLayout.reset();
            this.mOrientation = newConfig.orientation;
        }

        @Override // android.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            this.mOrientation = -1;
            ScreenEffectListLayout screenEffectListLayout = this.mScreenEffectListLayout;
            if (screenEffectListLayout != null) {
                screenEffectListLayout.destroy();
                this.mScreenEffectListLayout = null;
            }
            this.mPositiveListerner = null;
            this.mContext = null;
        }
    }
}
