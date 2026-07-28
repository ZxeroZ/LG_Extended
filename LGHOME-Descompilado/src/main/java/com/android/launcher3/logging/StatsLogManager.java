package com.android.launcher3.logging;

import android.content.Context;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.LauncherLogProto;
import com.android.launcher3.util.ResourceBasedOverride;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class StatsLogManager implements ResourceBasedOverride {
    public static final int LAUNCHER_STATE_ALLAPPS = 4;
    public static final int LAUNCHER_STATE_BACKGROUND = 1;
    public static final int LAUNCHER_STATE_HOME = 2;
    public static final int LAUNCHER_STATE_OVERVIEW = 3;
    public static final int LAUNCHER_STATE_UNCHANGED = 5;
    public static final int LAUNCHER_STATE_UNSPECIFIED = 0;

    public interface EventEnum {
        int getId();
    }

    public interface StatsLogger {
        default void log(EventEnum event) {
        }

        default StatsLogger withContainerInfo(LauncherAtom.ContainerInfo containerInfo) {
            return this;
        }

        default StatsLogger withDstState(int dstState) {
            return this;
        }

        default StatsLogger withEditText(String editText) {
            return this;
        }

        default StatsLogger withFromState(LauncherAtom.FromState fromState) {
            return this;
        }

        default StatsLogger withInstanceId(InstanceId instanceId) {
            return this;
        }

        default StatsLogger withItemInfo(ItemInfo itemInfo) {
            return this;
        }

        default StatsLogger withRank(int rank) {
            return this;
        }

        default StatsLogger withSrcState(int srcState) {
            return this;
        }

        default StatsLogger withToState(LauncherAtom.ToState toState) {
            return this;
        }
    }

    public static int containerTypeToAtomState(int containerType) {
        if (containerType == 1) {
            return 2;
        }
        if (containerType == 4) {
            return 4;
        }
        if (containerType != 6) {
            return containerType != 13 ? 0 : 1;
        }
        return 3;
    }

    public void log(EventEnum rankingEvent, InstanceId instanceId, String packageName, int position) {
    }

    public void logSnapshot() {
    }

    public static EventEnum getLauncherAtomEvent(int startContainerType, int targetContainerType, EventEnum fallbackEvent) {
        if (startContainerType == LauncherLogProto.ContainerType.WORKSPACE.getNumber() && targetContainerType == LauncherLogProto.ContainerType.WORKSPACE.getNumber()) {
            return LauncherEvent.LAUNCHER_HOME_GESTURE;
        }
        if (startContainerType != LauncherLogProto.ContainerType.TASKSWITCHER.getNumber() && targetContainerType == LauncherLogProto.ContainerType.TASKSWITCHER.getNumber()) {
            return LauncherEvent.LAUNCHER_OVERVIEW_GESTURE;
        }
        if (startContainerType == LauncherLogProto.ContainerType.ALLAPPS.getNumber() || targetContainerType != LauncherLogProto.ContainerType.ALLAPPS.getNumber()) {
            return (startContainerType != LauncherLogProto.ContainerType.ALLAPPS.getNumber() || targetContainerType == LauncherLogProto.ContainerType.ALLAPPS.getNumber()) ? fallbackEvent : LauncherEvent.LAUNCHER_ALLAPPS_CLOSE_DOWN;
        }
        return LauncherEvent.LAUNCHER_ALLAPPS_OPEN_UP;
    }

    public enum LauncherEvent implements EventEnum {
        IGNORE(-1),
        LAUNCHER_APP_LAUNCH_TAP(338),
        LAUNCHER_TASK_LAUNCH_TAP(339),
        LAUNCHER_NOTIFICATION_LAUNCH_TAP(516),
        LAUNCHER_TASK_LAUNCH_SWIPE_DOWN(340),
        LAUNCHER_TASK_DISMISS_SWIPE_UP(341),
        LAUNCHER_ITEM_DRAG_STARTED(383),
        LAUNCHER_ITEM_DROP_COMPLETED(385),
        LAUNCHER_ITEM_DROP_FOLDER_CREATED(386),
        LAUNCHER_FOLDER_AUTO_LABELED(591),
        LAUNCHER_FOLDER_AUTO_LABELING_SKIPPED_EMPTY_PRIMARY(592),
        LAUNCHER_FOLDER_AUTO_LABELING_SKIPPED_EMPTY_SUGGESTIONS(593),
        LAUNCHER_FOLDER_LABEL_UPDATED(460),
        LAUNCHER_WORKSPACE_LONGPRESS(461),
        LAUNCHER_WALLPAPER_BUTTON_TAP_OR_LONGPRESS(462),
        LAUNCHER_SETTINGS_BUTTON_TAP_OR_LONGPRESS(463),
        LAUNCHER_WIDGETSTRAY_BUTTON_TAP_OR_LONGPRESS(464),
        LAUNCHER_ITEM_DROPPED_ON_REMOVE(465),
        LAUNCHER_ITEM_DROPPED_ON_CANCEL(466),
        LAUNCHER_ITEM_DROPPED_ON_DONT_SUGGEST(467),
        LAUNCHER_ITEM_DROPPED_ON_UNINSTALL(468),
        LAUNCHER_ITEM_UNINSTALL_COMPLETED(469),
        LAUNCHER_ITEM_UNINSTALL_CANCELLED(470),
        LAUNCHER_TASK_ICON_TAP_OR_LONGPRESS(517),
        LAUNCHER_SYSTEM_SHORTCUT_WIDGETS_TAP(514),
        LAUNCHER_SYSTEM_SHORTCUT_APP_INFO_TAP(515),
        LAUNCHER_SYSTEM_SHORTCUT_SPLIT_SCREEN_TAP(518),
        LAUNCHER_SYSTEM_SHORTCUT_FREE_FORM_TAP(519),
        LAUNCHER_SYSTEM_SHORTCUT_PAUSE_TAP(521),
        LAUNCHER_SYSTEM_SHORTCUT_PIN_TAP(522),
        LAUNCHER_ALL_APPS_EDU_SHOWN(523),
        LAUNCHER_FOLDER_OPEN(551),
        LAUNCHER_HOTSEAT_EDU_SEEN(479),
        LAUNCHER_HOTSEAT_EDU_ACCEPT(480),
        LAUNCHER_HOTSEAT_EDU_DENY(481),
        LAUNCHER_HOTSEAT_EDU_ONLY_TIP(482),
        LAUNCHER_ALL_APPS_RANKED(552),
        LAUNCHER_HOTSEAT_RANKED(553),
        LAUNCHER_ONSTOP(562),
        LAUNCHER_ONRESUME(563),
        LAUNCHER_SWIPELEFT(564),
        LAUNCHER_SWIPERIGHT(565),
        LAUNCHER_UNKNOWN_SWIPEUP(566),
        LAUNCHER_UNKNOWN_SWIPEDOWN(567),
        LAUNCHER_ALLAPPS_OPEN_UP(568),
        LAUNCHER_ALLAPPS_CLOSE_DOWN(569),
        LAUNCHER_OVERVIEW_GESTURE(570),
        LAUNCHER_QUICKSWITCH_LEFT(571),
        LAUNCHER_QUICKSWITCH_RIGHT(572),
        LAUNCHER_SWIPEDOWN_NAVBAR(573),
        LAUNCHER_HOME_GESTURE(574),
        LAUNCHER_WORKSPACE_SNAPSHOT(579),
        LAUNCHER_OVERVIEW_ACTIONS_SCREENSHOT(580),
        LAUNCHER_OVERVIEW_ACTIONS_SELECT(581),
        LAUNCHER_OVERVIEW_ACTIONS_SHARE(582),
        LAUNCHER_SELECT_MODE_CLOSE(583),
        LAUNCHER_SELECT_MODE_ITEM(584);

        private final int mId;

        LauncherEvent(int id) {
            this.mId = id;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.EventEnum
        public int getId() {
            return this.mId;
        }
    }

    public enum LauncherRankingEvent implements EventEnum {
        UNKNOWN(0);

        private final int mId;

        LauncherRankingEvent(int id) {
            this.mId = id;
        }

        @Override // com.android.launcher3.logging.StatsLogManager.EventEnum
        public int getId() {
            return this.mId;
        }
    }

    public StatsLogger logger() {
        return new StatsLogger() { // from class: com.android.launcher3.logging.StatsLogManager.1
        };
    }

    public static StatsLogManager newInstance(Context context) {
        return (StatsLogManager) ResourceBasedOverride.Overrides.getObject(StatsLogManager.class, context.getApplicationContext(), R.string.stats_log_manager_class);
    }
}
