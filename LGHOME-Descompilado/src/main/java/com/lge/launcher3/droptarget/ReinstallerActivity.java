package com.lge.launcher3.droptarget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lge.launcher3.LauncherExtension;
import com.lge.launcher3.R;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class ReinstallerActivity extends Activity {
    public static final String PREF_DO_NOT_SHOW_AGAIN = "showmoredlgreinstall";
    public static final String PREF_KEY_REINSTALL = "ISCHECKINREINSTALL";
    private static final String TAG = "ReinstallerActivity";
    private String mClassName;
    private String mPackageName;

    void dispatchAborted() {
    }

    public static class ReinstallAlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.app_trash_title);
            builder.setPositiveButton(R.string.app_trash_title, this);
            builder.setNegativeButton(R.string.app_trash_close, this);
            setMessageView(builder);
            return builder.create();
        }

        private void setMessageView(AlertDialog.Builder dialogBuilder) {
            try {
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View viewInflate = layoutInflater.inflate(33751061, (ViewGroup) null);
                LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(android.R.id.content);
                View viewInflate2 = layoutInflater.inflate(33751045, (ViewGroup) linearLayout, false);
                ((TextView) viewInflate2.findViewById(android.R.id.text1)).setText(String.format(getContext().getString(R.string.app_trash_ask_move_restore_message_ux9), getActivity().getIntent().getStringExtra(LauncherConst.EXTRA_APP_NAME)));
                View viewInflate3 = layoutInflater.inflate(33751052, (ViewGroup) linearLayout, false);
                CheckBox checkBox = (CheckBox) viewInflate3.findViewById(android.R.id.checkbox);
                checkBox.setText(R.string.sp_dont_show_again_NORMAL);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.lge.launcher3.droptarget.ReinstallerActivity.ReinstallAlertDialogFragment.1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ReinstallAlertDialogFragment.this.setDoNotShowAgain(ReinstallerActivity.PREF_KEY_REINSTALL, isChecked);
                    }
                });
                linearLayout.addView(viewInflate2);
                linearLayout.addView(viewInflate3);
                dialogBuilder.setView(viewInflate);
            } catch (NoClassDefFoundError unused) {
                LGLog.w(ReinstallerActivity.TAG, "WhiteTheme  unsupported ", new int[0]);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDoNotShowAgain(String key, boolean doNotShowAgain) {
            getActivity().getSharedPreferences(ReinstallerActivity.PREF_DO_NOT_SHOW_AGAIN, 0).edit().putBoolean(key, doNotShowAgain).commit();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            Activity activity = getActivity();
            if (activity == null || !(activity instanceof ReinstallerActivity)) {
                return;
            }
            ReinstallerActivity reinstallerActivity = (ReinstallerActivity) activity;
            if (which == -1) {
                reinstallerActivity.startRUActivity();
            } else {
                reinstallerActivity.dispatchAborted();
            }
        }

        @Override // android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            Activity activity = getActivity();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mPackageName = getIntent().getStringExtra(LauncherConst.EXTRA_PACKAGE_NAME);
        this.mClassName = getIntent().getStringExtra(LauncherConst.EXTRA_CLASS_NAME);
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        showDialogFragment(new ReinstallAlertDialogFragment());
    }

    private void showDialogFragment(DialogFragment fragment) {
        FragmentTransaction fragmentTransactionBeginTransaction = getFragmentManager().beginTransaction();
        Fragment fragmentFindFragmentByTag = getFragmentManager().findFragmentByTag(null);
        if (fragmentFindFragmentByTag != null) {
            fragmentTransactionBeginTransaction.remove(fragmentFindFragmentByTag);
        }
        fragment.show(fragmentTransactionBeginTransaction, (String) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startRUActivity() {
        Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_RECENTUNINSTALL.getValue(getBaseContext()));
        intent.setFlags(335544320);
        startActivity(intent);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        finish();
    }

    @Override // android.app.Activity
    public void finish() {
        String str;
        super.finish();
        overridePendingTransition(0, 0);
        if (!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() || (str = this.mPackageName) == null) {
            return;
        }
        LauncherExtension.onItemRemove(str, this.mClassName);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        finish();
    }
}
