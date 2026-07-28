package com.lge.launcher3.droptarget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherModel.PackageUpdatedTask;
import com.lge.launcher3.LauncherExtension;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.operator.GVNUtils;
import com.lge.launcher3.recentuninstall.service.UninstallPackageUtil;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.TalkBackUtils;
import com.lge.launcher3.util.UserUtils;
import com.lge.launcher3.util.VplApps;
import com.lge.mrg.service.lib.ActionManagerConstants;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class UninstallerActivity extends Activity {
    private static final boolean DEBUG = false;
    private static final String DIALOG_ARGS_APP_NAME = "app_name";
    private static final String TAG = "UninstallerActivity";
    private String mClassName;
    private DialogInfo mDialogInfo;
    private String mPackageName;

    public static class UninstallAlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton(R.string.app_trash_delete_btn_text, this);
            builder.setNegativeButton(R.string.app_trash_cancel_btn_text, this);
            setupMessage(builder);
            return builder.create();
        }

        private void setupMessage(AlertDialog.Builder dialogBuilder) {
            if (GVNUtils.isGiovanna(getContext())) {
                setImageMessage(dialogBuilder);
            } else {
                setMessage(dialogBuilder);
            }
        }

        private String getTitleMessageForDualApp() {
            String encodedSchemeSpecificPart = getActivity().getIntent().getData().getEncodedSchemeSpecificPart();
            UserHandle userHandleMyUserHandle = (UserHandle) getActivity().getIntent().getParcelableExtra("android.intent.extra.USER");
            if (userHandleMyUserHandle == null) {
                userHandleMyUserHandle = Process.myUserHandle();
            }
            int identifier = userHandleMyUserHandle.getIdentifier();
            boolean zIsOriginalApplication = UserUtils.isOriginalApplication(getContext(), encodedSchemeSpecificPart);
            boolean zIsSecondApplication = UserUtils.isSecondApplication(getContext(), identifier);
            String string = getArguments().getString(UninstallerActivity.DIALOG_ARGS_APP_NAME);
            if (zIsSecondApplication) {
                return String.format(getResources().getString(R.string.ask_delete_dual_app_title), string);
            }
            if (zIsOriginalApplication) {
                return String.format(getResources().getString(R.string.ask_delete_original_app_title), string);
            }
            return null;
        }

        private String getMessage() {
            String string = getResources().getString(R.string.ask_delete_app_message);
            if (!"Widgets".equals(getTag())) {
                return string;
            }
            return String.format(getResources().getString(R.string.ask_delete_widget_message), getArguments().getString(UninstallerActivity.DIALOG_ARGS_APP_NAME));
        }

        private String getMessageForDualApp() {
            String string;
            String encodedSchemeSpecificPart = getActivity().getIntent().getData().getEncodedSchemeSpecificPart();
            UserHandle userHandleMyUserHandle = (UserHandle) getActivity().getIntent().getParcelableExtra("android.intent.extra.USER");
            if (userHandleMyUserHandle == null) {
                userHandleMyUserHandle = Process.myUserHandle();
            }
            int identifier = userHandleMyUserHandle.getIdentifier();
            boolean zIsOriginalApplication = UserUtils.isOriginalApplication(getContext(), encodedSchemeSpecificPart);
            if (UserUtils.isSecondApplication(getContext(), identifier)) {
                String string2 = getArguments().getString(UninstallerActivity.DIALOG_ARGS_APP_NAME);
                return String.format(getResources().getString(R.string.ask_delete_dual_app_desc), string2, string2);
            }
            if (zIsOriginalApplication) {
                string = getResources().getString(R.string.ask_delete_original_app_desc);
            } else {
                string = getResources().getString(R.string.ask_delete_app_message);
            }
            if (!"Widgets".equals(getTag())) {
                return string;
            }
            return String.format(getResources().getString(R.string.ask_delete_widget_message), getArguments().getString(UninstallerActivity.DIALOG_ARGS_APP_NAME));
        }

        private void setMessage(AlertDialog.Builder dialogBuilder) {
            if (LGHomeFeature.Config.FEATURE_USE_DUAL_APP.getValue()) {
                String titleMessageForDualApp = getTitleMessageForDualApp();
                if (titleMessageForDualApp != null) {
                    dialogBuilder.setTitle(titleMessageForDualApp);
                }
                dialogBuilder.setMessage(getMessageForDualApp());
                return;
            }
            dialogBuilder.setMessage(getMessage());
        }

        private void setImageMessage(AlertDialog.Builder dialogBuilder) {
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            View viewInflate = layoutInflater.inflate(33751061, (ViewGroup) null);
            LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(android.R.id.content);
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.gvn_dialog_uninstall));
            View viewInflate2 = layoutInflater.inflate(33751045, (ViewGroup) linearLayout, false);
            ((TextView) viewInflate2.findViewById(android.R.id.text1)).setText(getMessage());
            linearLayout.addView(imageView);
            linearLayout.addView(viewInflate2);
            dialogBuilder.setView(viewInflate);
        }

        @Override // android.app.Fragment, android.content.ComponentCallbacks
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            dismiss();
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            boolean zIsSecondApplication;
            Activity activity = getActivity();
            if (activity == null || !(activity instanceof UninstallerActivity)) {
                return;
            }
            UninstallerActivity uninstallerActivity = (UninstallerActivity) activity;
            if (which == -1) {
                if (LGHomeFeature.Config.FEATURE_USE_DUAL_APP.getValue()) {
                    UserHandle userHandleMyUserHandle = (UserHandle) activity.getIntent().getParcelableExtra("android.intent.extra.USER");
                    if (userHandleMyUserHandle == null) {
                        userHandleMyUserHandle = Process.myUserHandle();
                    }
                    zIsSecondApplication = UserUtils.isSecondApplication(getContext(), userHandleMyUserHandle.getIdentifier());
                } else {
                    zIsSecondApplication = false;
                }
                if (LGHomeFeature.Config.FEATURE_USE_RECENT_UNINSTALL_APP.getValue()) {
                    uninstallerActivity.disable();
                    if (("Widgets".equals(getTag()) || "Workspace".equals(getTag())) && (!LGHomeFeature.Config.FEATURE_USE_DUAL_APP.getValue() || !zIsSecondApplication)) {
                        uninstallerActivity.startReinstallerActivity();
                    }
                } else {
                    uninstallerActivity.startUninstallProgress();
                }
                if (LGHomeFeature.Config.FEATURE_USE_DUAL_APP.getValue() && zIsSecondApplication) {
                    uninstallerActivity.setResult(0);
                } else {
                    uninstallerActivity.setResult(-1);
                }
                TalkBackUtils.sendAccessibilityEvent((Context) activity, R.string.droptarget_popup_delete, true);
                return;
            }
            uninstallerActivity.dispatchAborted();
            uninstallerActivity.setResult(0);
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

    static class DialogInfo {
        ActivityInfo activityInfo;
        boolean allUsers;
        ApplicationInfo appInfo;
        String appName;
        IBinder callback;
        String startedBy;
        UserHandle user;
        boolean vpl;

        DialogInfo() {
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data == null) {
            LGLog.e(TAG, "No package URI in intent");
            return;
        }
        String encodedSchemeSpecificPart = data.getEncodedSchemeSpecificPart();
        this.mPackageName = encodedSchemeSpecificPart;
        if (encodedSchemeSpecificPart == null) {
            LGLog.e(TAG, "Invalid package name in URI: " + data);
            return;
        }
        IPackageManager iPackageManagerAsInterface = IPackageManager.Stub.asInterface(ServiceManager.getService(AppNotifierManager.ExtraSpec.USAGE_PACKAGE));
        DialogInfo dialogInfo = new DialogInfo();
        this.mDialogInfo = dialogInfo;
        dialogInfo.user = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
        if (this.mDialogInfo.user == null) {
            this.mDialogInfo.user = Process.myUserHandle();
        }
        this.mDialogInfo.allUsers = intent.getBooleanExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
        this.mDialogInfo.callback = intent.getIBinderExtra("android.content.pm.extra.CALLBACK");
        try {
            DialogInfo dialogInfo2 = this.mDialogInfo;
            dialogInfo2.appInfo = iPackageManagerAsInterface.getApplicationInfo(this.mPackageName, ActionManagerConstants.ACTION_CATEGORY_MMS, dialogInfo2.user.getIdentifier());
        } catch (RemoteException unused) {
            LGLog.e(TAG, "Unable to get packageName. Package manager is dead?");
        }
        if (this.mDialogInfo.appInfo == null) {
            LGLog.e(TAG, "Invalid packageName: " + this.mPackageName);
            return;
        }
        DialogInfo dialogInfo3 = this.mDialogInfo;
        dialogInfo3.appName = dialogInfo3.appInfo.loadLabel(getPackageManager()).toString();
        this.mDialogInfo.vpl = VplApps.contains(this.mPackageName);
        this.mDialogInfo.startedBy = intent.getStringExtra("startedBy");
        String fragment = data.getFragment();
        this.mClassName = fragment;
        if (fragment != null) {
            try {
                this.mDialogInfo.activityInfo = iPackageManagerAsInterface.getActivityInfo(new ComponentName(this.mPackageName, this.mClassName), 0L, this.mDialogInfo.user.getIdentifier());
            } catch (RemoteException unused2) {
                Log.e(TAG, "Unable to get className. Package manager is dead?");
            }
        }
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_ARGS_APP_NAME, this.mDialogInfo.appName);
        UninstallAlertDialogFragment uninstallAlertDialogFragment = new UninstallAlertDialogFragment();
        uninstallAlertDialogFragment.setArguments(bundle);
        showDialogFragment(uninstallAlertDialogFragment);
    }

    private void showDialogFragment(DialogFragment fragment) {
        FragmentTransaction fragmentTransactionBeginTransaction = getFragmentManager().beginTransaction();
        Fragment fragmentFindFragmentByTag = getFragmentManager().findFragmentByTag(this.mDialogInfo.startedBy);
        if (fragmentFindFragmentByTag != null) {
            fragmentTransactionBeginTransaction.remove(fragmentFindFragmentByTag);
        }
        fragment.show(fragmentTransactionBeginTransaction, this.mDialogInfo.startedBy);
    }

    void startUninstallProgress() {
        LGLog.i(TAG, "Delete Package: " + this.mPackageName);
        try {
            IPackageManager.Stub.asInterface(ServiceManager.getService(AppNotifierManager.ExtraSpec.USAGE_PACKAGE)).deletePackageAsUser(this.mPackageName, -1, (IPackageDeleteObserver) null, this.mDialogInfo.user.getIdentifier(), this.mDialogInfo.allUsers ? 2 : 0);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to talk to package manager", e);
        }
        if (this.mDialogInfo.vpl) {
            disable();
        }
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            LauncherExtension.onItemRemove(this.mPackageName, this.mClassName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disable() {
        LGLog.i(TAG, "Disable Package: " + this.mPackageName);
        PackageManager packageManager = getBaseContext().getPackageManager();
        if (LGHomeFeature.Config.FEATURE_USE_DUAL_APP.getValue()) {
            IPackageManager.Stub.asInterface(ServiceManager.getService(AppNotifierManager.ExtraSpec.USAGE_PACKAGE));
            int dualUserId = UserUtils.getUserManagerCompat(getBaseContext()).getDualUserId();
            boolean zIsOriginalApplication = UserUtils.isOriginalApplication(getBaseContext(), this.mPackageName);
            if (UserUtils.isSecondApplication(getBaseContext(), this.mDialogInfo.user.getIdentifier())) {
                UninstallPackageUtil.uninstallPackage(this.mPackageName, this.mDialogInfo.user.getIdentifier());
            } else {
                if (zIsOriginalApplication) {
                    UninstallPackageUtil.uninstallPackage(this.mPackageName, dualUserId);
                }
                packageManager.setApplicationEnabledSetting(this.mPackageName, 3, 0);
            }
        } else {
            packageManager.setApplicationEnabledSetting(this.mPackageName, 3, 0);
        }
        String[] strArr = {this.mPackageName};
        Handler handler = new Handler(LauncherModel.getWorkerLooper());
        LauncherModel model = LauncherAppState.getInstance(getBaseContext()).getModel();
        Objects.requireNonNull(model);
        handler.post(model.new PackageUpdatedTask(2, strArr, Process.myUserHandle()));
        if (LGHomeFeature.Config.FEATURE_USE_DUAL_APP.getValue()) {
            if (UserUtils.isSecondApplication(getBaseContext(), this.mDialogInfo.user.getIdentifier())) {
                return;
            }
            UninstallPackageUtil.sendSchedulingIntent(this);
            return;
        }
        UninstallPackageUtil.sendSchedulingIntent(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startReinstallerActivity() {
        if (isCheckedReintallerDoNotShowAgain()) {
            return;
        }
        Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_REINSTALL_DIALOG.getValue(getBaseContext()));
        DialogInfo dialogInfo = this.mDialogInfo;
        if (dialogInfo != null) {
            intent.putExtra(LauncherConst.EXTRA_APP_NAME, dialogInfo.appName);
            intent.putExtra(LauncherConst.EXTRA_PACKAGE_NAME, this.mPackageName);
            intent.putExtra(LauncherConst.EXTRA_CLASS_NAME, this.mClassName);
        }
        startActivity(intent);
    }

    private boolean isCheckedReintallerDoNotShowAgain() {
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences(ReinstallerActivity.PREF_DO_NOT_SHOW_AGAIN, 0);
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(ReinstallerActivity.PREF_KEY_REINSTALL, false);
        }
        return false;
    }

    void dispatchAborted() {
        DialogInfo dialogInfo = this.mDialogInfo;
        if (dialogInfo == null || dialogInfo.callback == null) {
            return;
        }
        try {
            IPackageDeleteObserver2.Stub.asInterface(this.mDialogInfo.callback).onPackageDeleted(this.mPackageName, -5, "Cancelled by user");
        } catch (RemoteException e) {
            Log.w(TAG, "dispatchAborted", e);
        }
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
