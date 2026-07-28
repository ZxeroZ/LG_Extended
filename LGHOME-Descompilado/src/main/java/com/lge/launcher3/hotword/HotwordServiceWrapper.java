package com.lge.launcher3.hotword;

import android.view.ViewParent;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.google.android.libraries.gsa.launcherclient.LauncherClient;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class HotwordServiceWrapper {
    public static final String TAG = "HotwordServiceWrapper";
    public boolean mEnableHotwordService;
    public boolean mFolderOpened;
    private LauncherClient mHotwordServiceClient;
    private Launcher mLauncher;

    public HotwordServiceWrapper(Launcher launcher) {
        this.mLauncher = launcher;
    }

    public void setLauncherClient(LauncherClient launcherClient) {
        this.mHotwordServiceClient = launcherClient;
    }

    public void onPageSwitched(Workspace workspace) {
        int currentPage = workspace.getCurrentPage();
        CellLayout cellLayout = (CellLayout) workspace.getChildAt(currentPage);
        if (cellLayout == null) {
            LGLog.e(TAG, "CellLayout is NULL current page = " + currentPage);
            return;
        }
        this.mEnableHotwordService = cellLayout.mEnableHotwordService;
        requestHotwordDetectionIfNeeded();
    }

    public void updateHotwordDetection(CellLayout layout) {
        if (this.mHotwordServiceClient == null) {
            return;
        }
        ViewParent parent = layout.getParent();
        if (parent instanceof Workspace) {
            Workspace workspace = (Workspace) parent;
            layout.enableHotwordServiceIfNeeded();
            int currentPage = workspace.getCurrentPage();
            CellLayout cellLayout = (CellLayout) workspace.getChildAt(currentPage);
            if (cellLayout == null) {
                LGLog.e(TAG, "CellLayout is NULL current page = " + currentPage);
                return;
            }
            this.mEnableHotwordService = cellLayout.mEnableHotwordService;
            requestHotwordDetectionIfNeeded();
        }
    }

    public void openFolder() {
        this.mFolderOpened = true;
        requestHotwordDetectionIfNeeded();
    }

    public void closeFolder() {
        this.mFolderOpened = false;
        requestHotwordDetectionIfNeeded();
    }

    public void requestHotwordDetectionIfNeeded() {
        Launcher launcher;
        if (this.mHotwordServiceClient == null || (launcher = this.mLauncher) == null) {
            return;
        }
        Workspace workspace = launcher.getWorkspace();
        boolean z = false;
        if (workspace == null) {
            LGLog.w(TAG, "Workspace is null", new int[0]);
            return;
        }
        LauncherState state = this.mLauncher.getState();
        Workspace.State state2 = workspace.getState();
        if (state == LauncherState.NORMAL && state2 == Workspace.State.NORMAL && this.mEnableHotwordService && !this.mFolderOpened) {
            z = true;
        }
        this.mHotwordServiceClient.requestHotwordDetection(z);
    }
}
