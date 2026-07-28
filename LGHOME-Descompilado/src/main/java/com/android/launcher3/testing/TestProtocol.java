package com.android.launcher3.testing;

/* JADX INFO: loaded from: classes.dex */
public final class TestProtocol {
    public static final int ALL_APPS_STATE_ORDINAL = 6;
    public static final int APPS_SPRING_LOADED_ORDINAL = 12;
    public static final int BACKGROUND_APP_STATE_ORDINAL = 7;
    public static final int CLEAN_VIEW_ORDINAL = 11;
    public static final String DISMISS_ANIMATION_ENDS_MESSAGE = "TAPL_DISMISS_ANIMATION_ENDS";
    public static final int DYNAMIC_GRID_OVERVIEW_ORDINAL = 14;
    public static final String GET_SCROLL_MESSAGE = "TAPL_GET_SCROLL";
    public static final int HINT_STATE_ORDINAL = 8;
    public static final int INAPPS_ORDINAL = 15;
    public static final int NORMAL_NOACTION_STATE_ORDINAL = 16;
    public static final int NORMAL_STATE_ORDINAL = 0;
    public static final String NO_ALLAPPS_EVENT_TAG = "b/133867119";
    public static final String NO_DRAG_TAG = "b/133009122";
    public static final String NO_OVERVIEW_EVENT_TAG = "b/134532571";
    public static final String NO_START_TAG = "b/132900132";
    public static final String NO_START_TASK_TAG = "b/133765434";
    public static final String NO_SWIPE_TO_HOME = "b/158017601";
    public static final String OVERIEW_NOT_ALLAPPS = "b/156095088";
    public static final int OVERVIEW_MODAL_TASK_STATE_ORDINAL = 4;
    public static final int OVERVIEW_PEEK_STATE_ORDINAL = 3;
    public static final int OVERVIEW_SPLIT_SELECT_ORDINAL = 9;
    public static final int OVERVIEW_STATE_ORDINAL = 2;
    public static final String PAUSE_DETECTED_MESSAGE = "TAPL_PAUSE_DETECTED";
    public static final String PAUSE_NOT_DETECTED = "b/139891609";
    public static final String PERMANENT_DIAG_TAG = "TaplTarget";
    public static final int QUICK_SWITCH_STATE_ORDINAL = 5;
    public static final String REQUEST_ALL_APPS_TO_OVERVIEW_SWIPE_HEIGHT = "all-apps-to-overview-swipe-height";
    public static final String REQUEST_APP_LIST_FREEZE_FLAGS = "app-list-freeze-flags";
    public static final String REQUEST_BACKGROUND_TO_OVERVIEW_SWIPE_HEIGHT = "background-to-overview-swipe-height";
    public static final String REQUEST_DISABLE_DEBUG_TRACING = "disable-debug-tracing";
    public static final String REQUEST_ENABLE_DEBUG_TRACING = "enable-debug-tracing";
    public static final String REQUEST_FREEZE_APP_LIST = "freeze-app-list";
    public static final String REQUEST_HOME_TO_ALL_APPS_SWIPE_HEIGHT = "home-to-all-apps-swipe-height";
    public static final String REQUEST_HOME_TO_OVERVIEW_SWIPE_HEIGHT = "home-to-overview-swipe-height";
    public static final String REQUEST_HOTSEAT_TOP = "hotseat-top";
    public static final String REQUEST_MOCK_SENSOR_ROTATION = "mock-sensor-rotation";
    public static final String REQUEST_OVERVIEW_ACTIONS_ENABLED = "overview-actions-enabled";
    public static final String REQUEST_UNFREEZE_APP_LIST = "unfreeze-app-list";
    public static final String RESPONSE_MESSAGE_POSTFIX = "_RESPONSE";
    public static final String SCROLL_FINISHED_MESSAGE = "TAPL_SCROLL_FINISHED";
    public static final String SCROLL_Y_FIELD = "scrollY";
    public static final String SEQUENCE_MAIN = "Main";
    public static final String SEQUENCE_PILFER = "Pilfer";
    public static final String SEQUENCE_TIS = "TIS";
    public static final int SPRING_LOADED_STATE_ORDINAL = 1;
    public static final String STATE_FIELD = "state";
    public static final String SWITCHED_TO_STATE_MESSAGE = "TAPL_SWITCHED_TO_STATE";
    public static final String TAPL_EVENTS_TAG = "TaplEvents";
    public static final String TEST_INFO_RESPONSE_FIELD = "response";
    public static final int WIDGETS_ORDINAL = 10;
    public static final int WIDGETS_SPRING_LOADED_ORDINAL = 13;
    public static boolean sDebugTracing;
    public static boolean sDisableSensorRotation;

    public static String stateOrdinalToString(int ordinal) {
        switch (ordinal) {
            case 0:
                return "Normal";
            case 1:
                return "SpringLoaded";
            case 2:
                return "Overview";
            case 3:
                return "OverviewPeek";
            case 4:
            case 9:
            default:
                return null;
            case 5:
                return "QuickSwitch";
            case 6:
                return "AllApps";
            case 7:
                return "Background";
            case 8:
                return "Hint";
            case 10:
                return "Widgets";
            case 11:
                return "CleanView";
            case 12:
                return "AppsSpringLoaded";
            case 13:
                return "WidgetsSpringLoaded";
            case 14:
                return "DynamicGridOverview";
            case 15:
                return "Inapps";
        }
    }
}
