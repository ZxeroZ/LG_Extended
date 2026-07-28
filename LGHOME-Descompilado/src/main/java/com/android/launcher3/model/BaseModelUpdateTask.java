package com.android.launcher3.model;

import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.allapps.AllAppsList;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseModelUpdateTask implements LauncherModel.ModelUpdateTask {
    private static final boolean DEBUG_TASKS = false;
    private static final String TAG = "BaseModelUpdateTask";
    private AllAppsList mAllAppsList;
    private LauncherAppState mApp;
    private BgDataModel mDataModel;
    private LauncherModel mModel;
    private Executor mUiExecutor;

    public abstract void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps);

    public final void scheduleCallbackTask(final LauncherModel.CallbackTask task) {
    }

    @Override // com.android.launcher3.LauncherModel.ModelUpdateTask
    public void init(LauncherAppState app, LauncherModel model, BgDataModel dataModel, AllAppsList allAppsList, Executor uiExecutor) {
        this.mApp = app;
        this.mModel = model;
        this.mDataModel = dataModel;
        this.mAllAppsList = allAppsList;
        this.mUiExecutor = uiExecutor;
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (this.mModel.isModelLoaded()) {
            execute(this.mApp, this.mDataModel, this.mAllAppsList);
        }
    }

    public ModelWriter getModelWriter() {
        return this.mModel.getWriter(false);
    }
}
