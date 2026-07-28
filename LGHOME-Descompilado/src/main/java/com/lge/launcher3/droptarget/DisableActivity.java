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
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherModel.PackageUpdatedTask;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.recentuninstall.service.UninstallPackageUtil;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.TalkBackUtils;
import com.lge.launcher3.util.UserUtils;
import com.lge.mrg.service.lib.ActionManagerConstants;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class DisableActivity extends Activity {
    private static final String DIALOG_ARGS_APP_NAME = "app_name";
    private static final String TAG = "DisableActivity";
    DisableAlertDialogFragment mDialog;
    private DialogInfo mDialogInfo;
    private PackageManager mPackageManager;
    private String mPackageName;

    public static class DisableAlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton(R.string.droptarget_popup_disable, this);
            builder.setNegativeButton(R.string.droptarget_cancel, this);
            setMessage(builder);
            return builder.create();
        }

        private String getTitleMessageForDualApp() {
            String encodedSchemeSpecificPart = getActivity().getIntent().getData().getEncodedSchemeSpecificPart();
            UserHandle userHandleMyUserHandle = (UserHandle) getActivity().getIntent().getParcelableExtra("android.intent.extra.USER");
            if (userHandleMyUserHandle == null) {
                userHandleMyUserHandle = Process.myUserHandle();
            }
            int identifier = userHandleMyUserHandle.getIdentifier();
            UserUtils.isOriginalApplication(getContext(), encodedSchemeSpecificPart);
            boolean zIsSecondApplication = UserUtils.isSecondApplication(getContext(), identifier);
            String string = getArguments().getString(DisableActivity.DIALOG_ARGS_APP_NAME);
            if (zIsSecondApplication) {
                return String.format(getResources().getString(R.string.sp_disable_second_app_title), string);
            }
            return null;
        }

        private String getMessage() {
            return getResources().getString(R.string.ask_disable_app_message_mod_1);
        }

        private String getMessageForDualApp() {
            String encodedSchemeSpecificPart = getActivity().getIntent().getData().getEncodedSchemeSpecificPart();
            UserHandle userHandleMyUserHandle = (UserHandle) getActivity().getIntent().getParcelableExtra("android.intent.extra.USER");
            if (userHandleMyUserHandle == null) {
                userHandleMyUserHandle = Process.myUserHandle();
            }
            int identifier = userHandleMyUserHandle.getIdentifier();
            UserUtils.isOriginalApplication(getContext(), encodedSchemeSpecificPart);
            if (UserUtils.isSecondApplication(getContext(), identifier)) {
                String string = getArguments().getString(DisableActivity.DIALOG_ARGS_APP_NAME);
                return String.format(getResources().getString(R.string.sp_disable_second_app_desc), string, string);
            }
            return getResources().getString(R.string.ask_disable_app_message_mod_1);
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

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            Activity activity = getActivity();
            if (activity == null || !(activity instanceof DisableActivity)) {
                return;
            }
            DisableActivity disableActivity = (DisableActivity) activity;
            if (which == -1) {
                disableActivity.startDisableProgress();
                TalkBackUtils.sendAccessibilityEvent((Context) activity, R.string.disabled, true);
            } else {
                disableActivity.dispatchAborted();
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

    static class DialogInfo {
        ActivityInfo activityInfo;
        boolean allUsers;
        ApplicationInfo appInfo;
        String appName;
        IBinder callback;
        UserHandle user;

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
        String fragment = data.getFragment();
        if (fragment != null) {
            try {
                this.mDialogInfo.activityInfo = iPackageManagerAsInterface.getActivityInfo(new ComponentName(this.mPackageName, fragment), 0L, this.mDialogInfo.user.getIdentifier());
            } catch (RemoteException unused2) {
                Log.e(TAG, "Unable to get className. Package manager is dead?");
            }
        }
        this.mPackageManager = getBaseContext().getPackageManager();
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_ARGS_APP_NAME, this.mDialogInfo.appName);
        DisableAlertDialogFragment disableAlertDialogFragment = new DisableAlertDialogFragment();
        this.mDialog = disableAlertDialogFragment;
        disableAlertDialogFragment.setArguments(bundle);
        showDialogFragment(this.mDialog);
    }

    private void showDialogFragment(DialogFragment fragment) {
        FragmentTransaction fragmentTransactionBeginTransaction = getFragmentManager().beginTransaction();
        Fragment fragmentFindFragmentByTag = getFragmentManager().findFragmentByTag("dialog");
        if (fragmentFindFragmentByTag != null) {
            fragmentTransactionBeginTransaction.remove(fragmentFindFragmentByTag);
        }
        fragment.show(fragmentTransactionBeginTransaction, "dialog");
    }

    void startDisableProgress() {
        disable();
    }

    private void disable() {
        LGLog.i(TAG, "Disable Package: " + this.mPackageName);
        if (LGHomeFeature.Config.FEATURE_USE_DUAL_APP.getValue()) {
            int dualUserId = UserUtils.getUserManagerCompat(getBaseContext()).getDualUserId();
            boolean zIsOriginalApplication = UserUtils.isOriginalApplication(getBaseContext(), this.mPackageName);
            if (UserUtils.isSecondApplication(getBaseContext(), this.mDialogInfo.user.getIdentifier())) {
                UninstallPackageUtil.disablePackage(this.mPackageName, this.mDialogInfo.user.getIdentifier());
            } else {
                if (zIsOriginalApplication) {
                    UninstallPackageUtil.disablePackage(this.mPackageName, dualUserId);
                }
                this.mPackageManager.setApplicationEnabledSetting(this.mPackageName, 3, 0);
            }
        } else {
            this.mPackageManager.setApplicationEnabledSetting(this.mPackageName, 3, 0);
        }
        String[] strArr = {this.mPackageName};
        Handler handler = new Handler(LauncherModel.getWorkerLooper());
        LauncherModel model = LauncherAppState.getInstance(getBaseContext()).getModel();
        Objects.requireNonNull(model);
        handler.post(model.new PackageUpdatedTask(2, strArr, Process.myUserHandle()));
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

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        finish();
    }
}
