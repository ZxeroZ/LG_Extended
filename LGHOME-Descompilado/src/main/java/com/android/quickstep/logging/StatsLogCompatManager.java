package com.android.quickstep.logging;

import android.content.Context;
import android.util.Log;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsList;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.InstanceIdSequence;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.BaseModelUpdateTask;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.LooperExecutor;
import com.android.systemui.shared.system.SysUiStatsLog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes.dex */
public class StatsLogCompatManager extends StatsLogManager {
    private static final int DEFAULT_PAGE_INDEX = -2;
    private static final int FOLDER_HIERARCHY_OFFSET = 100;
    private static final int SEARCH_RESULT_HIERARCHY_OFFSET = 200;
    private static final String TAG = "StatsLog";
    private static Context sContext;
    private static final boolean IS_VERBOSE = Utilities.isPropertyEnabled("StatsLog");
    private static final InstanceId DEFAULT_INSTANCE_ID = InstanceId.fakeInstanceId(0);

    @Override // com.android.launcher3.logging.StatsLogManager
    public void logSnapshot() {
    }

    public StatsLogCompatManager(Context context) {
        sContext = context;
    }

    @Override // com.android.launcher3.logging.StatsLogManager
    public StatsLogManager.StatsLogger logger() {
        return new StatsCompatLogger();
    }

    @Override // com.android.launcher3.logging.StatsLogManager
    public void log(StatsLogManager.EventEnum rankingEvent, InstanceId instanceId, String packageName, int position) {
        SysUiStatsLog.write(SysUiStatsLog.RANKING_SELECTED, rankingEvent.getId(), packageName, instanceId.getId(), position);
    }

    /* JADX INFO: Access modifiers changed from: private */
    class SnapshotWorker extends BaseModelUpdateTask {
        private final InstanceId mInstanceId = new InstanceIdSequence(1048576).newInstanceId();

        SnapshotWorker() {
        }

        @Override // com.android.launcher3.model.BaseModelUpdateTask
        public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
            LongArrayMap<FolderInfo> longArrayMapClone = dataModel.folders.clone();
            ArrayList arrayList = (ArrayList) dataModel.workspaceItems.clone();
            ArrayList arrayList2 = (ArrayList) dataModel.appWidgets.clone();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                StatsLogCompatManager.writeSnapshot(((ItemInfo) it.next()).buildProto(null), this.mInstanceId);
            }
            for (FolderInfo folderInfo : longArrayMapClone) {
                try {
                    LooperExecutor looperExecutor = Executors.MAIN_EXECUTOR;
                    final ArrayList<ShortcutInfo> arrayList3 = folderInfo.contents;
                    Objects.requireNonNull(arrayList3);
                    Iterator it2 = ((ArrayList) looperExecutor.submit(new Callable() { // from class: com.android.quickstep.logging.-$$Lambda$StatsLogCompatManager$SnapshotWorker$MPKLXSz6a3rlxx10myuxmYOQtT8
                        @Override // java.util.concurrent.Callable
                        public final Object call() {
                            return arrayList3.clone();
                        }
                    }).get()).iterator();
                    while (it2.hasNext()) {
                        StatsLogCompatManager.writeSnapshot(((ItemInfo) it2.next()).buildProto(folderInfo), this.mInstanceId);
                    }
                } catch (Exception unused) {
                }
            }
            Iterator it3 = arrayList2.iterator();
            while (it3.hasNext()) {
                StatsLogCompatManager.writeSnapshot(((ItemInfo) it3.next()).buildProto(null), this.mInstanceId);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void writeSnapshot(LauncherAtom.ItemInfo info, InstanceId instanceId) {
        if (IS_VERBOSE) {
            Log.d("StatsLog", String.format("\nwriteSnapshot(%d):\n%s", Integer.valueOf(instanceId.getId()), info));
        }
        if (!Utilities.ATLEAST_R) {
        }
    }

    private static class StatsCompatLogger implements StatsLogManager.StatsLogger {
        private static final ItemInfo DEFAULT_ITEM_INFO = new ItemInfo();
        private Optional<LauncherAtom.ContainerInfo> mContainerInfo;
        private int mDstState;
        private Optional<String> mEditText;
        private Optional<LauncherAtom.FromState> mFromState;
        private InstanceId mInstanceId;
        private ItemInfo mItemInfo;
        private OptionalInt mRank;
        private int mSrcState;
        private Optional<LauncherAtom.ToState> mToState;

        private StatsCompatLogger() {
            this.mItemInfo = DEFAULT_ITEM_INFO;
            this.mInstanceId = StatsLogCompatManager.DEFAULT_INSTANCE_ID;
            this.mRank = OptionalInt.empty();
            this.mContainerInfo = Optional.empty();
            this.mSrcState = 0;
            this.mDstState = 0;
            this.mFromState = Optional.empty();
            this.mToState = Optional.empty();
            this.mEditText = Optional.empty();
        }

        @Override // com.android.launcher3.logging.StatsLogManager.StatsLogger
        public StatsLogManager.StatsLogger withItemInfo(ItemInfo itemInfo) {
            if (this.mContainerInfo.isPresent()) {
                throw new IllegalArgumentException("ItemInfo and ContainerInfo are mutual exclusive; cannot log both.");
            }
            this.mItemInfo = itemInfo;
            return this;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.StatsLogger
        public StatsLogManager.StatsLogger withInstanceId(InstanceId instanceId) {
            this.mInstanceId = instanceId;
            return this;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.StatsLogger
        public StatsLogManager.StatsLogger withRank(int rank) {
            this.mRank = OptionalInt.of(rank);
            return this;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.StatsLogger
        public StatsLogManager.StatsLogger withSrcState(int srcState) {
            this.mSrcState = srcState;
            return this;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.StatsLogger
        public StatsLogManager.StatsLogger withDstState(int dstState) {
            this.mDstState = dstState;
            return this;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.StatsLogger
        public StatsLogManager.StatsLogger withContainerInfo(LauncherAtom.ContainerInfo containerInfo) {
            if (this.mItemInfo != DEFAULT_ITEM_INFO) {
                throw new IllegalArgumentException("ItemInfo and ContainerInfo are mutual exclusive; cannot log both.");
            }
            this.mContainerInfo = Optional.of(containerInfo);
            return this;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.StatsLogger
        public StatsLogManager.StatsLogger withFromState(LauncherAtom.FromState fromState) {
            this.mFromState = Optional.of(fromState);
            return this;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.StatsLogger
        public StatsLogManager.StatsLogger withToState(LauncherAtom.ToState toState) {
            this.mToState = Optional.of(toState);
            return this;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.StatsLogger
        public StatsLogManager.StatsLogger withEditText(String editText) {
            this.mEditText = Optional.of(editText);
            return this;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.StatsLogger
        public void log(StatsLogManager.EventEnum event) {
            if (Utilities.ATLEAST_R) {
                long j = this.mItemInfo.container;
            }
        }
    }
}
