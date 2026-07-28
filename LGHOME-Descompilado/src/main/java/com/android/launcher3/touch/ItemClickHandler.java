package com.android.launcher3.touch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.PendingAppWidgetHostView;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.model.AppLaunchTracker;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.PromiseAppInfo;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.widget.WidgetAddFlowHandler;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class ItemClickHandler {
    public static final View.OnClickListener INSTANCE = getInstance(null);

    public static final View.OnClickListener getInstance(final String sourceContainer) {
        return new View.OnClickListener() { // from class: com.android.launcher3.touch.-$$Lambda$ItemClickHandler$fH7QPPHeXou9_kNG2htSj_s2sMo
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ItemClickHandler.onClick(view, sourceContainer);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void onClick(View v, String sourceContainer) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_DRAG_TAG, "onClick() called with: v = [" + v.getClass().getSimpleName() + "], sourceContainer = [" + (sourceContainer != null ? sourceContainer.getClass().getSimpleName() : "null") + "]");
        }
        if (v.getWindowToken() == null) {
            return;
        }
        Launcher launcher = Launcher.getLauncher(v.getContext());
        if (launcher.getWorkspace().isFinishedSwitchingState()) {
            Object tag = v.getTag();
            if (tag instanceof ShortcutInfo) {
                onClickAppShortcut(v, (ShortcutInfo) tag, launcher, sourceContainer);
                return;
            }
            if (tag instanceof FolderInfo) {
                if (v instanceof FolderIcon) {
                    onClickFolderIcon(v);
                }
            } else {
                if (tag instanceof AppInfo) {
                    AppInfo appInfo = (AppInfo) tag;
                    if (sourceContainer == null) {
                        sourceContainer = AppLaunchTracker.CONTAINER_ALL_APPS;
                    }
                    startAppShortcutOrInfoActivity(v, appInfo, launcher, sourceContainer);
                    return;
                }
                if ((tag instanceof LauncherAppWidgetInfo) && (v instanceof PendingAppWidgetHostView)) {
                    onClickPendingWidget((PendingAppWidgetHostView) v, launcher);
                }
            }
        }
    }

    private static void onClickFolderIcon(View v) {
        Folder folder = ((FolderIcon) v).getFolder();
        if (folder.isOpen() || folder.isDestroyed()) {
            return;
        }
        folder.animateOpen();
    }

    private static void onClickPendingWidget(PendingAppWidgetHostView v, Launcher launcher) {
        if (launcher.getPackageManager().isSafeMode()) {
            Toast.makeText(launcher, R.string.safemode_widget_error, 0).show();
            return;
        }
        LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) v.getTag();
        if (v.isReadyForClickSetup()) {
            LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfoFindProvider = AppWidgetManagerCompat.getInstance(launcher).findProvider(launcherAppWidgetInfo.providerName, launcherAppWidgetInfo.user);
            if (launcherAppWidgetProviderInfoFindProvider == null) {
                return;
            }
            WidgetAddFlowHandler widgetAddFlowHandler = new WidgetAddFlowHandler(launcherAppWidgetProviderInfoFindProvider);
            if (launcherAppWidgetInfo.hasRestoreFlag(1)) {
                if (launcherAppWidgetInfo.hasRestoreFlag(16)) {
                    widgetAddFlowHandler.startBindFlow(launcher, launcherAppWidgetInfo.appWidgetId, launcherAppWidgetInfo, 14);
                    return;
                }
                return;
            }
            widgetAddFlowHandler.startConfigActivity(launcher, launcherAppWidgetInfo, 12);
            return;
        }
        onClickPendingAppItem(v, launcher, launcherAppWidgetInfo.providerName.getPackageName(), launcherAppWidgetInfo.installProgress >= 0);
    }

    private static void onClickPendingAppItem(final View v, final Launcher launcher, final String packageName, boolean downloadStarted) {
        if (downloadStarted) {
            startMarketIntentForPackage(v, launcher, packageName);
        } else {
            new AlertDialog.Builder(launcher).setTitle(R.string.abandoned_promises_title).setMessage(R.string.abandoned_promise_explanation).setPositiveButton(R.string.abandoned_search, new DialogInterface.OnClickListener() { // from class: com.android.launcher3.touch.-$$Lambda$ItemClickHandler$GmEsrUBu_kqLQNv6MMxITevbLfA
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ItemClickHandler.startMarketIntentForPackage(v, launcher, packageName);
                }
            }).setNeutralButton(R.string.abandoned_clean_this, new DialogInterface.OnClickListener() { // from class: com.android.launcher3.touch.-$$Lambda$ItemClickHandler$KkD92gA8w5g18vcoCH2SoZg3gv0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    launcher.getWorkspace().removeAbandonedPromise(packageName, Process.myUserHandle());
                }
            }).create().show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void startMarketIntentForPackage(View v, Launcher launcher, String packageName) {
        launcher.startActivitySafely(v, new PackageManagerHelper(launcher).getMarketIntent(packageName), (ItemInfo) v.getTag(), 0);
    }

    public static void onClickAppShortcut(View v, ShortcutInfo shortcut, Launcher launcher, String sourceContainer) {
        if (shortcut.isDisabled() && (shortcut.runtimeStatusFlags & 63 & (-5) & (-9)) != 0) {
            if (!TextUtils.isEmpty(shortcut.disabledMessage)) {
                Toast.makeText(launcher, shortcut.disabledMessage, 0).show();
                return;
            }
            int i = R.string.activity_not_available;
            if ((shortcut.runtimeStatusFlags & 1) != 0) {
                i = R.string.safemode_shortcut_error;
            } else if ((shortcut.runtimeStatusFlags & 16) != 0 || (shortcut.runtimeStatusFlags & 32) != 0) {
                i = R.string.shortcut_not_available;
            }
            Toast.makeText(launcher, i, 0).show();
            return;
        }
        if ((v instanceof BubbleTextView) && shortcut.hasPromiseIconUi()) {
            String packageName = shortcut.intent.getComponent() != null ? shortcut.intent.getComponent().getPackageName() : shortcut.intent.getPackage();
            if (!TextUtils.isEmpty(packageName)) {
                onClickPendingAppItem(v, launcher, packageName, shortcut.hasStatusFlag(4));
                return;
            }
        }
        startAppShortcutOrInfoActivity(v, shortcut, launcher, sourceContainer);
    }

    private static void startAppShortcutOrInfoActivity(View v, ItemInfo item, Launcher launcher, String sourceContainer) {
        Intent intent;
        if (item instanceof PromiseAppInfo) {
            intent = ((PromiseAppInfo) item).getMarketIntent(launcher);
        } else {
            intent = item.getIntent();
        }
        if (intent == null) {
            throw new IllegalArgumentException("Input must have a valid intent");
        }
        if ((item instanceof ShortcutInfo) && ((ShortcutInfo) item).hasStatusFlag(16) && "android.intent.action.VIEW".equals(intent.getAction())) {
            Intent intent2 = new Intent(intent);
            intent2.setPackage(null);
            intent = intent2;
        }
        launcher.startActivitySafely(v, intent, item, 0);
    }
}
