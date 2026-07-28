package com.lge.mrg.service.lib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import com.lge.mrg.service.IActionManagerService;
import com.lge.mrg.service.IBackupInfoReporter;

/* JADX INFO: loaded from: classes2.dex */
public class ActionManager {
    public static final String ACTION_APPEND_USER_LOG = "com.lge.mrg.service.intent.action.APPEND_USER_LOG";
    public static final String ACTION_BOARD_STATE_CHANGED = "com.lge.mrg.service.BOARD_STATE_CHANGED";
    public static final String ACTION_CARD_COMMAND = "com.lge.mrg.service.action.CARD_COMMAND";
    public static final String ACTION_FEATURE_USER_LOG = "com.lge.mrg.service.FEATURE_USER_LOG";
    private static final ComponentName ACTION_MANAGER_COMP_NAME = new ComponentName("com.lge.mrg.service", "com.lge.mrg.service.internal.ActionManagerService");
    public static final String ACTION_NOTIFY_CARD_ACTION = "com.lge.mrg.service.action.NOTIFY_CARD_ACTION";
    public static final String ACTION_REGISTER_LISTENER = "com.lge.mrg.service.intent.action.REG_LISTENER";
    public static final String ACTION_REVIVE_SERVICE = "com.lge.mrg.service.REVIVE_SERVICE";
    public static final String BUNDLE_CARD_ACTION_URI = "bundle_card_action_uri";
    public static final int CARD_CMD_ADD = 0;
    public static final int CARD_CMD_REMOVE = 1;
    public static final int CARD_CMD_UPDATE = 2;
    public static final String EXTRA_ACTION_ID_MASK = "extra_action_id_mask";
    public static final String EXTRA_BOARD_CATEGORY = "category";
    public static final String EXTRA_BOARD_STATE = "state";
    public static final String EXTRA_CARD_ACTION_URI_PREFIX = "extra_card_action_prefix";
    public static final String EXTRA_CARD_CATEGORY = "extra_card_category";
    public static final String EXTRA_CARD_INFO = "extra_card_info";
    public static final String EXTRA_CARD_URI = "extra_card_uri";
    public static final String EXTRA_CLASS_NAME = "extra_cls_name";
    public static final String EXTRA_COMMAND = "extra_cmd";
    public static final String EXTRA_LISTENER = "extra_listener";
    public static final String EXTRA_MULTI_USER_LOGS = "extra_multi_user_logs";
    public static final String EXTRA_PACKAGE_NAME = "extra_pkg_name";
    private static final String LOG_TAG = "ActionManager";
    public static final int MSG_CARD_ACTION = 1001;
    public static final int MSG_SERVICE_CONNECTED = 1002;
    public static final int MSG_SERVICE_DISCONNECTED = 1003;
    public static final int MSG_USER_LOG = 1000;
    private ServiceConnection connection;
    private long mActionIdMask;
    private BackupInfoCollectorImpl mBIC;
    private Messenger mCallback;
    private String[] mCardActionURIPrefix;
    private String mClassName;
    private Context mContext;
    private String mPackageName;
    private IActionManagerService mService;

    public interface BackupSizeReporter {
        long getBackupSize();
    }

    public ActionManager(Context context) {
        this(context, null, 0L);
    }

    public ActionManager(Context context, Messenger messenger, long j) {
        this(context, messenger, j, null);
    }

    public ActionManager(Context context, Messenger messenger, long j, String[] strArr) {
        this.mBIC = null;
        this.connection = new ServiceConnection() { // from class: com.lge.mrg.service.lib.ActionManager.1
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(ActionManager.LOG_TAG, "disconnected from ActionManagerService.");
                ActionManager.this.mService = null;
                if (ActionManager.this.mCallback != null) {
                    Message messageObtain = Message.obtain();
                    messageObtain.what = 1003;
                    try {
                        ActionManager.this.mCallback.send(messageObtain);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                if (iBinder == null) {
                    return;
                }
                ActionManager.this.mService = IActionManagerService.Stub.asInterface(iBinder);
                Log.d(ActionManager.LOG_TAG, "connected to ActionManagerService.");
                if (ActionManager.this.mBIC != null) {
                    try {
                        ActionManager.this.mService.registerBackupInfoCollector(ActionManager.this.mBIC);
                    } catch (RemoteException e) {
                        Log.e(ActionManager.LOG_TAG, "failed to register BackupInfoCollector!!");
                        e.printStackTrace();
                    }
                }
                if (ActionManager.this.mCallback != null) {
                    Message messageObtain = Message.obtain();
                    messageObtain.what = 1002;
                    try {
                        ActionManager.this.mCallback.send(messageObtain);
                    } catch (RemoteException e2) {
                        e2.printStackTrace();
                    }
                    Intent intent = new Intent(ActionManager.ACTION_REGISTER_LISTENER);
                    intent.setComponent(ActionManager.ACTION_MANAGER_COMP_NAME);
                    intent.putExtra(ActionManager.EXTRA_LISTENER, ActionManager.this.mCallback);
                    intent.putExtra(ActionManager.EXTRA_ACTION_ID_MASK, ActionManager.this.mActionIdMask);
                    intent.putExtra(ActionManager.EXTRA_CARD_ACTION_URI_PREFIX, ActionManager.this.mCardActionURIPrefix);
                    intent.putExtra(ActionManager.EXTRA_PACKAGE_NAME, ActionManager.this.mPackageName);
                    intent.putExtra(ActionManager.EXTRA_CLASS_NAME, ActionManager.this.mClassName);
                    ActionManager.this.mContext.startService(intent);
                }
            }
        };
        this.mContext = context;
        this.mCallback = messenger;
        this.mActionIdMask = j;
        this.mCardActionURIPrefix = strArr;
        this.mPackageName = context.getPackageName();
        this.mClassName = context.getClass().getName();
        Intent intent = new Intent();
        intent.setComponent(ACTION_MANAGER_COMP_NAME);
        if (!context.bindService(intent, this.connection, 1)) {
            Log.e(LOG_TAG, "failed to connect to ActionManagerService");
        } else {
            Log.d(LOG_TAG, "connecting to ActionManagerService...");
        }
    }

    public void setBackupSizeReporter(BackupSizeReporter backupSizeReporter) throws RemoteException {
        BackupInfoCollectorImpl backupInfoCollectorImpl = new BackupInfoCollectorImpl(backupSizeReporter);
        this.mBIC = backupInfoCollectorImpl;
        IActionManagerService iActionManagerService = this.mService;
        if (iActionManagerService != null) {
            iActionManagerService.registerBackupInfoCollector(backupInfoCollectorImpl);
        }
    }

    private void checkServiceConnection() throws RemoteException {
        if (this.mService == null) {
            throw new RemoteException("ActionManager is not Ready");
        }
    }

    public boolean isConnected() {
        return this.mService != null;
    }

    public void appendUserLog(Bundle bundle) throws RemoteException {
        checkServiceConnection();
        this.mService.appendUserLog(bundle);
    }

    public static void appendUserLog(Context context, Bundle bundle) {
        Intent intent = new Intent(ACTION_APPEND_USER_LOG);
        intent.setComponent(ACTION_MANAGER_COMP_NAME);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    public static void appendUserLog(Context context, Bundle[] bundleArr) {
        Intent intent = new Intent(ACTION_APPEND_USER_LOG);
        intent.setComponent(ACTION_MANAGER_COMP_NAME);
        intent.putExtra(EXTRA_MULTI_USER_LOGS, bundleArr);
        context.startService(intent);
    }

    public void executeCardCommand(int i, String str, String str2) throws RemoteException {
        executeCardCommand(i, str, str2, null);
    }

    public void executeCardCommand(int i, String str, String str2, String str3) throws RemoteException {
        checkServiceConnection();
        this.mService.executeCardCommand(i, str, str2, str3);
    }

    public static void executeCardCommand(Context context, int i, String str, String str2, String str3) {
        Intent intent = new Intent(ACTION_CARD_COMMAND);
        intent.setComponent(ACTION_MANAGER_COMP_NAME);
        intent.putExtra(EXTRA_COMMAND, i);
        intent.putExtra(EXTRA_CARD_URI, str);
        intent.putExtra(EXTRA_CARD_CATEGORY, str2);
        if (str3 != null) {
            intent.putExtra(EXTRA_CARD_INFO, str3);
        }
        context.startService(intent);
    }

    public void finalize() {
        Log.d(LOG_TAG, "finalize() - " + this.connection);
        this.mContext.unbindService(this.connection);
        this.connection = null;
        this.mService = null;
        BackupInfoCollectorImpl backupInfoCollectorImpl = this.mBIC;
        if (backupInfoCollectorImpl != null) {
            backupInfoCollectorImpl.mReporter = null;
            this.mBIC = null;
        }
    }

    public static Intent makeIntentForCardAction(String str) {
        Intent intent = new Intent(ACTION_NOTIFY_CARD_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_CARD_ACTION_URI, str);
        intent.setComponent(ACTION_MANAGER_COMP_NAME);
        intent.putExtras(bundle);
        return intent;
    }

    public static boolean isActionManagerAvailable(Context context) {
        Intent intent = new Intent();
        intent.setComponent(ACTION_MANAGER_COMP_NAME);
        return context.getPackageManager().queryIntentServices(intent, 0).size() > 0;
    }

    private class BackupInfoCollectorImpl extends IBackupInfoReporter.Stub {
        private BackupSizeReporter mReporter;

        public BackupInfoCollectorImpl(BackupSizeReporter backupSizeReporter) {
            this.mReporter = backupSizeReporter;
        }

        public String getPackageName() throws RemoteException {
            if (this.mReporter == null) {
                return null;
            }
            return ActionManager.this.mPackageName;
        }

        public long getBackupSize() throws RemoteException {
            BackupSizeReporter backupSizeReporter = this.mReporter;
            if (backupSizeReporter == null) {
                return 0L;
            }
            return backupSizeReporter.getBackupSize();
        }
    }
}
