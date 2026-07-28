package com.android.launcher3.userevent;

import com.android.launcher3.userevent.LauncherLogExtensions;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherLogProto {

    public interface ActionOrBuilder extends MessageLiteOrBuilder {
        Action.Command getCommand();

        Action.Direction getDir();

        boolean getIsOutside();

        boolean getIsStateChange();

        Action.Touch getTouch();

        Action.Type getType();

        boolean hasCommand();

        boolean hasDir();

        boolean hasIsOutside();

        boolean hasIsStateChange();

        boolean hasTouch();

        boolean hasType();
    }

    public interface LauncherEventOrBuilder extends MessageLiteOrBuilder {
        Action getAction();

        long getActionDurationMillis();

        Target getDestTarget(int i);

        int getDestTargetCount();

        List<Target> getDestTargetList();

        long getElapsedContainerMillis();

        long getElapsedSessionMillis();

        LauncherLogExtensions.LauncherEventExtension getExtension();

        @Deprecated
        boolean getIsInLandscapeMode();

        @Deprecated
        boolean getIsInMultiWindowMode();

        Target getSrcTarget(int i);

        int getSrcTargetCount();

        List<Target> getSrcTargetList();

        boolean hasAction();

        boolean hasActionDurationMillis();

        boolean hasElapsedContainerMillis();

        boolean hasElapsedSessionMillis();

        boolean hasExtension();

        @Deprecated
        boolean hasIsInLandscapeMode();

        @Deprecated
        boolean hasIsInMultiWindowMode();
    }

    public interface TargetOrBuilder extends MessageLiteOrBuilder {
        int getCardinality();

        int getComponentHash();

        ContainerType getContainerType();

        ControlType getControlType();

        LauncherLogExtensions.TargetExtension getExtension();

        Target.FromFolderLabelState getFromFolderLabelState();

        int getGridX();

        int getGridY();

        int getIntentHash();

        boolean getIsWorkApp();

        ItemType getItemType();

        int getPackageNameHash();

        int getPageIndex();

        int getPredictedRank();

        int getRank();

        int getSearchQueryLength();

        int getSpanX();

        int getSpanY();

        TipType getTipType();

        Target.ToFolderLabelState getToFolderLabelState();

        Target.Type getType();

        boolean hasCardinality();

        boolean hasComponentHash();

        boolean hasContainerType();

        boolean hasControlType();

        boolean hasExtension();

        boolean hasFromFolderLabelState();

        boolean hasGridX();

        boolean hasGridY();

        boolean hasIntentHash();

        boolean hasIsWorkApp();

        boolean hasItemType();

        boolean hasPackageNameHash();

        boolean hasPageIndex();

        boolean hasPredictedRank();

        boolean hasRank();

        boolean hasSearchQueryLength();

        boolean hasSpanX();

        boolean hasSpanY();

        boolean hasTipType();

        boolean hasToFolderLabelState();

        boolean hasType();
    }

    public static void registerAllExtensions(ExtensionRegistryLite extensionRegistryLite) {
    }

    private LauncherLogProto() {
    }

    public enum ItemType implements Internal.EnumLite {
        DEFAULT_ITEMTYPE(0),
        APP_ICON(1),
        SHORTCUT(2),
        WIDGET(3),
        FOLDER_ICON(4),
        DEEPSHORTCUT(5),
        SEARCHBOX(6),
        EDITTEXT(7),
        NOTIFICATION(8),
        TASK(9),
        WEB_APP(10),
        TASK_ICON(11);

        public static final int APP_ICON_VALUE = 1;
        public static final int DEEPSHORTCUT_VALUE = 5;
        public static final int DEFAULT_ITEMTYPE_VALUE = 0;
        public static final int EDITTEXT_VALUE = 7;
        public static final int FOLDER_ICON_VALUE = 4;
        public static final int NOTIFICATION_VALUE = 8;
        public static final int SEARCHBOX_VALUE = 6;
        public static final int SHORTCUT_VALUE = 2;
        public static final int TASK_ICON_VALUE = 11;
        public static final int TASK_VALUE = 9;
        public static final int WEB_APP_VALUE = 10;
        public static final int WIDGET_VALUE = 3;
        private static final Internal.EnumLiteMap<ItemType> internalValueMap = new Internal.EnumLiteMap<ItemType>() { // from class: com.android.launcher3.userevent.LauncherLogProto.ItemType.1
            /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
            @Override // com.google.protobuf.Internal.EnumLiteMap
            public ItemType findValueByNumber(int i) {
                return ItemType.forNumber(i);
            }
        };
        private final int value;

        @Override // com.google.protobuf.Internal.EnumLite
        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static ItemType valueOf(int i) {
            return forNumber(i);
        }

        public static ItemType forNumber(int i) {
            switch (i) {
                case 0:
                    return DEFAULT_ITEMTYPE;
                case 1:
                    return APP_ICON;
                case 2:
                    return SHORTCUT;
                case 3:
                    return WIDGET;
                case 4:
                    return FOLDER_ICON;
                case 5:
                    return DEEPSHORTCUT;
                case 6:
                    return SEARCHBOX;
                case 7:
                    return EDITTEXT;
                case 8:
                    return NOTIFICATION;
                case 9:
                    return TASK;
                case 10:
                    return WEB_APP;
                case 11:
                    return TASK_ICON;
                default:
                    return null;
            }
        }

        public static Internal.EnumLiteMap<ItemType> internalGetValueMap() {
            return internalValueMap;
        }

        public static Internal.EnumVerifier internalGetVerifier() {
            return ItemTypeVerifier.INSTANCE;
        }

        private static final class ItemTypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new ItemTypeVerifier();

            private ItemTypeVerifier() {
            }

            @Override // com.google.protobuf.Internal.EnumVerifier
            public boolean isInRange(int i) {
                return ItemType.forNumber(i) != null;
            }
        }

        ItemType(int i) {
            this.value = i;
        }
    }

    public enum ContainerType implements Internal.EnumLite {
        DEFAULT_CONTAINERTYPE(0),
        WORKSPACE(1),
        HOTSEAT(2),
        FOLDER(3),
        ALLAPPS(4),
        WIDGETS(5),
        OVERVIEW(6),
        PREDICTION(7),
        SEARCHRESULT(8),
        DEEPSHORTCUTS(9),
        PINITEM(10),
        NAVBAR(11),
        TASKSWITCHER(12),
        APP(13),
        TIP(14),
        OTHER_LAUNCHER_APP(15);

        public static final int ALLAPPS_VALUE = 4;
        public static final int APP_VALUE = 13;
        public static final int DEEPSHORTCUTS_VALUE = 9;
        public static final int DEFAULT_CONTAINERTYPE_VALUE = 0;
        public static final int FOLDER_VALUE = 3;
        public static final int HOTSEAT_VALUE = 2;
        public static final int NAVBAR_VALUE = 11;
        public static final int OTHER_LAUNCHER_APP_VALUE = 15;
        public static final int OVERVIEW_VALUE = 6;
        public static final int PINITEM_VALUE = 10;
        public static final int PREDICTION_VALUE = 7;
        public static final int SEARCHRESULT_VALUE = 8;
        public static final int TASKSWITCHER_VALUE = 12;
        public static final int TIP_VALUE = 14;
        public static final int WIDGETS_VALUE = 5;
        public static final int WORKSPACE_VALUE = 1;
        private static final Internal.EnumLiteMap<ContainerType> internalValueMap = new Internal.EnumLiteMap<ContainerType>() { // from class: com.android.launcher3.userevent.LauncherLogProto.ContainerType.1
            /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
            @Override // com.google.protobuf.Internal.EnumLiteMap
            public ContainerType findValueByNumber(int i) {
                return ContainerType.forNumber(i);
            }
        };
        private final int value;

        @Override // com.google.protobuf.Internal.EnumLite
        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static ContainerType valueOf(int i) {
            return forNumber(i);
        }

        public static ContainerType forNumber(int i) {
            switch (i) {
                case 0:
                    return DEFAULT_CONTAINERTYPE;
                case 1:
                    return WORKSPACE;
                case 2:
                    return HOTSEAT;
                case 3:
                    return FOLDER;
                case 4:
                    return ALLAPPS;
                case 5:
                    return WIDGETS;
                case 6:
                    return OVERVIEW;
                case 7:
                    return PREDICTION;
                case 8:
                    return SEARCHRESULT;
                case 9:
                    return DEEPSHORTCUTS;
                case 10:
                    return PINITEM;
                case 11:
                    return NAVBAR;
                case 12:
                    return TASKSWITCHER;
                case 13:
                    return APP;
                case 14:
                    return TIP;
                case 15:
                    return OTHER_LAUNCHER_APP;
                default:
                    return null;
            }
        }

        public static Internal.EnumLiteMap<ContainerType> internalGetValueMap() {
            return internalValueMap;
        }

        public static Internal.EnumVerifier internalGetVerifier() {
            return ContainerTypeVerifier.INSTANCE;
        }

        private static final class ContainerTypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new ContainerTypeVerifier();

            private ContainerTypeVerifier() {
            }

            @Override // com.google.protobuf.Internal.EnumVerifier
            public boolean isInRange(int i) {
                return ContainerType.forNumber(i) != null;
            }
        }

        ContainerType(int i) {
            this.value = i;
        }
    }

    public enum ControlType implements Internal.EnumLite {
        DEFAULT_CONTROLTYPE(0),
        ALL_APPS_BUTTON(1),
        WIDGETS_BUTTON(2),
        WALLPAPER_BUTTON(3),
        SETTINGS_BUTTON(4),
        REMOVE_TARGET(5),
        UNINSTALL_TARGET(6),
        APPINFO_TARGET(7),
        RESIZE_HANDLE(8),
        VERTICAL_SCROLL(9),
        HOME_INTENT(10),
        BACK_BUTTON(11),
        QUICK_SCRUB_BUTTON(12),
        CLEAR_ALL_BUTTON(13),
        CANCEL_TARGET(14),
        TASK_PREVIEW(15),
        SPLIT_SCREEN_TARGET(16),
        REMOTE_ACTION_SHORTCUT(17),
        APP_USAGE_SETTINGS(18),
        BACK_GESTURE(19),
        UNDO(20),
        DISMISS_PREDICTION(21),
        HYBRID_HOTSEAT_ACCEPTED(22),
        HYBRID_HOTSEAT_CANCELED(23),
        OVERVIEW_ACTIONS_SHARE_BUTTON(24),
        OVERVIEW_ACTIONS_SCREENSHOT_BUTTON(25),
        OVERVIEW_ACTIONS_SELECT_BUTTON(26),
        SELECT_MODE_CLOSE_BUTTON(27),
        SELECT_MODE_ITEM(28);

        public static final int ALL_APPS_BUTTON_VALUE = 1;
        public static final int APPINFO_TARGET_VALUE = 7;
        public static final int APP_USAGE_SETTINGS_VALUE = 18;
        public static final int BACK_BUTTON_VALUE = 11;
        public static final int BACK_GESTURE_VALUE = 19;
        public static final int CANCEL_TARGET_VALUE = 14;
        public static final int CLEAR_ALL_BUTTON_VALUE = 13;
        public static final int DEFAULT_CONTROLTYPE_VALUE = 0;
        public static final int DISMISS_PREDICTION_VALUE = 21;
        public static final int HOME_INTENT_VALUE = 10;
        public static final int HYBRID_HOTSEAT_ACCEPTED_VALUE = 22;
        public static final int HYBRID_HOTSEAT_CANCELED_VALUE = 23;
        public static final int OVERVIEW_ACTIONS_SCREENSHOT_BUTTON_VALUE = 25;
        public static final int OVERVIEW_ACTIONS_SELECT_BUTTON_VALUE = 26;
        public static final int OVERVIEW_ACTIONS_SHARE_BUTTON_VALUE = 24;
        public static final int QUICK_SCRUB_BUTTON_VALUE = 12;
        public static final int REMOTE_ACTION_SHORTCUT_VALUE = 17;
        public static final int REMOVE_TARGET_VALUE = 5;
        public static final int RESIZE_HANDLE_VALUE = 8;
        public static final int SELECT_MODE_CLOSE_BUTTON_VALUE = 27;
        public static final int SELECT_MODE_ITEM_VALUE = 28;
        public static final int SETTINGS_BUTTON_VALUE = 4;
        public static final int SPLIT_SCREEN_TARGET_VALUE = 16;
        public static final int TASK_PREVIEW_VALUE = 15;
        public static final int UNDO_VALUE = 20;
        public static final int UNINSTALL_TARGET_VALUE = 6;
        public static final int VERTICAL_SCROLL_VALUE = 9;
        public static final int WALLPAPER_BUTTON_VALUE = 3;
        public static final int WIDGETS_BUTTON_VALUE = 2;
        private static final Internal.EnumLiteMap<ControlType> internalValueMap = new Internal.EnumLiteMap<ControlType>() { // from class: com.android.launcher3.userevent.LauncherLogProto.ControlType.1
            /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
            @Override // com.google.protobuf.Internal.EnumLiteMap
            public ControlType findValueByNumber(int i) {
                return ControlType.forNumber(i);
            }
        };
        private final int value;

        @Override // com.google.protobuf.Internal.EnumLite
        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static ControlType valueOf(int i) {
            return forNumber(i);
        }

        public static ControlType forNumber(int i) {
            switch (i) {
                case 0:
                    return DEFAULT_CONTROLTYPE;
                case 1:
                    return ALL_APPS_BUTTON;
                case 2:
                    return WIDGETS_BUTTON;
                case 3:
                    return WALLPAPER_BUTTON;
                case 4:
                    return SETTINGS_BUTTON;
                case 5:
                    return REMOVE_TARGET;
                case 6:
                    return UNINSTALL_TARGET;
                case 7:
                    return APPINFO_TARGET;
                case 8:
                    return RESIZE_HANDLE;
                case 9:
                    return VERTICAL_SCROLL;
                case 10:
                    return HOME_INTENT;
                case 11:
                    return BACK_BUTTON;
                case 12:
                    return QUICK_SCRUB_BUTTON;
                case 13:
                    return CLEAR_ALL_BUTTON;
                case 14:
                    return CANCEL_TARGET;
                case 15:
                    return TASK_PREVIEW;
                case 16:
                    return SPLIT_SCREEN_TARGET;
                case 17:
                    return REMOTE_ACTION_SHORTCUT;
                case 18:
                    return APP_USAGE_SETTINGS;
                case 19:
                    return BACK_GESTURE;
                case 20:
                    return UNDO;
                case 21:
                    return DISMISS_PREDICTION;
                case 22:
                    return HYBRID_HOTSEAT_ACCEPTED;
                case 23:
                    return HYBRID_HOTSEAT_CANCELED;
                case 24:
                    return OVERVIEW_ACTIONS_SHARE_BUTTON;
                case 25:
                    return OVERVIEW_ACTIONS_SCREENSHOT_BUTTON;
                case 26:
                    return OVERVIEW_ACTIONS_SELECT_BUTTON;
                case 27:
                    return SELECT_MODE_CLOSE_BUTTON;
                case 28:
                    return SELECT_MODE_ITEM;
                default:
                    return null;
            }
        }

        public static Internal.EnumLiteMap<ControlType> internalGetValueMap() {
            return internalValueMap;
        }

        public static Internal.EnumVerifier internalGetVerifier() {
            return ControlTypeVerifier.INSTANCE;
        }

        private static final class ControlTypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new ControlTypeVerifier();

            private ControlTypeVerifier() {
            }

            @Override // com.google.protobuf.Internal.EnumVerifier
            public boolean isInRange(int i) {
                return ControlType.forNumber(i) != null;
            }
        }

        ControlType(int i) {
            this.value = i;
        }
    }

    public enum TipType implements Internal.EnumLite {
        DEFAULT_NONE(0),
        BOUNCE(1),
        SWIPE_UP_TEXT(2),
        QUICK_SCRUB_TEXT(3),
        PREDICTION_TEXT(4),
        DWB_TOAST(5),
        HYBRID_HOTSEAT(6);

        public static final int BOUNCE_VALUE = 1;
        public static final int DEFAULT_NONE_VALUE = 0;
        public static final int DWB_TOAST_VALUE = 5;
        public static final int HYBRID_HOTSEAT_VALUE = 6;
        public static final int PREDICTION_TEXT_VALUE = 4;
        public static final int QUICK_SCRUB_TEXT_VALUE = 3;
        public static final int SWIPE_UP_TEXT_VALUE = 2;
        private static final Internal.EnumLiteMap<TipType> internalValueMap = new Internal.EnumLiteMap<TipType>() { // from class: com.android.launcher3.userevent.LauncherLogProto.TipType.1
            /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
            @Override // com.google.protobuf.Internal.EnumLiteMap
            public TipType findValueByNumber(int i) {
                return TipType.forNumber(i);
            }
        };
        private final int value;

        @Override // com.google.protobuf.Internal.EnumLite
        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static TipType valueOf(int i) {
            return forNumber(i);
        }

        public static TipType forNumber(int i) {
            switch (i) {
                case 0:
                    return DEFAULT_NONE;
                case 1:
                    return BOUNCE;
                case 2:
                    return SWIPE_UP_TEXT;
                case 3:
                    return QUICK_SCRUB_TEXT;
                case 4:
                    return PREDICTION_TEXT;
                case 5:
                    return DWB_TOAST;
                case 6:
                    return HYBRID_HOTSEAT;
                default:
                    return null;
            }
        }

        public static Internal.EnumLiteMap<TipType> internalGetValueMap() {
            return internalValueMap;
        }

        public static Internal.EnumVerifier internalGetVerifier() {
            return TipTypeVerifier.INSTANCE;
        }

        private static final class TipTypeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new TipTypeVerifier();

            private TipTypeVerifier() {
            }

            @Override // com.google.protobuf.Internal.EnumVerifier
            public boolean isInRange(int i) {
                return TipType.forNumber(i) != null;
            }
        }

        TipType(int i) {
            this.value = i;
        }
    }

    public static final class Target extends GeneratedMessageLite<Target, Builder> implements TargetOrBuilder {
        public static final int CARDINALITY_FIELD_NUMBER = 7;
        public static final int COMPONENT_HASH_FIELD_NUMBER = 11;
        public static final int CONTAINER_TYPE_FIELD_NUMBER = 6;
        public static final int CONTROL_TYPE_FIELD_NUMBER = 8;
        private static final Target DEFAULT_INSTANCE;
        public static final int EXTENSION_FIELD_NUMBER = 16;
        public static final int FROM_FOLDER_LABEL_STATE_FIELD_NUMBER = 20;
        public static final int GRID_X_FIELD_NUMBER = 4;
        public static final int GRID_Y_FIELD_NUMBER = 5;
        public static final int INTENT_HASH_FIELD_NUMBER = 12;
        public static final int IS_WORK_APP_FIELD_NUMBER = 19;
        public static final int ITEM_TYPE_FIELD_NUMBER = 9;
        public static final int PACKAGE_NAME_HASH_FIELD_NUMBER = 10;
        public static final int PAGE_INDEX_FIELD_NUMBER = 2;
        private static volatile Parser<Target> PARSER = null;
        public static final int PREDICTEDRANK_FIELD_NUMBER = 15;
        public static final int RANK_FIELD_NUMBER = 3;
        public static final int SEARCH_QUERY_LENGTH_FIELD_NUMBER = 18;
        public static final int SPAN_X_FIELD_NUMBER = 13;
        public static final int SPAN_Y_FIELD_NUMBER = 14;
        public static final int TIP_TYPE_FIELD_NUMBER = 17;
        public static final int TO_FOLDER_LABEL_STATE_FIELD_NUMBER = 21;
        public static final int TYPE_FIELD_NUMBER = 1;
        private int bitField0_;
        private int cardinality_;
        private int componentHash_;
        private int containerType_;
        private int controlType_;
        private LauncherLogExtensions.TargetExtension extension_;
        private int fromFolderLabelState_;
        private int gridX_;
        private int gridY_;
        private int intentHash_;
        private boolean isWorkApp_;
        private int itemType_;
        private int packageNameHash_;
        private int pageIndex_;
        private int predictedRank_;
        private int rank_;
        private int searchQueryLength_;
        private int spanX_ = 1;
        private int spanY_ = 1;
        private int tipType_;
        private int toFolderLabelState_;
        private int type_;

        private Target() {
        }

        public enum Type implements Internal.EnumLite {
            NONE(0),
            ITEM(1),
            CONTROL(2),
            CONTAINER(3);

            public static final int CONTAINER_VALUE = 3;
            public static final int CONTROL_VALUE = 2;
            public static final int ITEM_VALUE = 1;
            public static final int NONE_VALUE = 0;
            private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { // from class: com.android.launcher3.userevent.LauncherLogProto.Target.Type.1
                /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
                @Override // com.google.protobuf.Internal.EnumLiteMap
                public Type findValueByNumber(int i) {
                    return Type.forNumber(i);
                }
            };
            private final int value;

            @Override // com.google.protobuf.Internal.EnumLite
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Type valueOf(int i) {
                return forNumber(i);
            }

            public static Type forNumber(int i) {
                if (i == 0) {
                    return NONE;
                }
                if (i == 1) {
                    return ITEM;
                }
                if (i == 2) {
                    return CONTROL;
                }
                if (i != 3) {
                    return null;
                }
                return CONTAINER;
            }

            public static Internal.EnumLiteMap<Type> internalGetValueMap() {
                return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
                return TypeVerifier.INSTANCE;
            }

            private static final class TypeVerifier implements Internal.EnumVerifier {
                static final Internal.EnumVerifier INSTANCE = new TypeVerifier();

                private TypeVerifier() {
                }

                @Override // com.google.protobuf.Internal.EnumVerifier
                public boolean isInRange(int i) {
                    return Type.forNumber(i) != null;
                }
            }

            Type(int i) {
                this.value = i;
            }
        }

        public enum FromFolderLabelState implements Internal.EnumLite {
            FROM_FOLDER_LABEL_STATE_UNSPECIFIED(0),
            FROM_EMPTY(1),
            FROM_CUSTOM(2),
            FROM_SUGGESTED(3);

            public static final int FROM_CUSTOM_VALUE = 2;
            public static final int FROM_EMPTY_VALUE = 1;
            public static final int FROM_FOLDER_LABEL_STATE_UNSPECIFIED_VALUE = 0;
            public static final int FROM_SUGGESTED_VALUE = 3;
            private static final Internal.EnumLiteMap<FromFolderLabelState> internalValueMap = new Internal.EnumLiteMap<FromFolderLabelState>() { // from class: com.android.launcher3.userevent.LauncherLogProto.Target.FromFolderLabelState.1
                /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
                @Override // com.google.protobuf.Internal.EnumLiteMap
                public FromFolderLabelState findValueByNumber(int i) {
                    return FromFolderLabelState.forNumber(i);
                }
            };
            private final int value;

            @Override // com.google.protobuf.Internal.EnumLite
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static FromFolderLabelState valueOf(int i) {
                return forNumber(i);
            }

            public static FromFolderLabelState forNumber(int i) {
                if (i == 0) {
                    return FROM_FOLDER_LABEL_STATE_UNSPECIFIED;
                }
                if (i == 1) {
                    return FROM_EMPTY;
                }
                if (i == 2) {
                    return FROM_CUSTOM;
                }
                if (i != 3) {
                    return null;
                }
                return FROM_SUGGESTED;
            }

            public static Internal.EnumLiteMap<FromFolderLabelState> internalGetValueMap() {
                return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
                return FromFolderLabelStateVerifier.INSTANCE;
            }

            private static final class FromFolderLabelStateVerifier implements Internal.EnumVerifier {
                static final Internal.EnumVerifier INSTANCE = new FromFolderLabelStateVerifier();

                private FromFolderLabelStateVerifier() {
                }

                @Override // com.google.protobuf.Internal.EnumVerifier
                public boolean isInRange(int i) {
                    return FromFolderLabelState.forNumber(i) != null;
                }
            }

            FromFolderLabelState(int i) {
                this.value = i;
            }
        }

        public enum ToFolderLabelState implements Internal.EnumLite {
            TO_FOLDER_LABEL_STATE_UNSPECIFIED(0),
            TO_SUGGESTION0_WITH_VALID_PRIMARY(1),
            TO_SUGGESTION1_WITH_VALID_PRIMARY(2),
            TO_SUGGESTION1_WITH_EMPTY_PRIMARY(3),
            TO_SUGGESTION2_WITH_VALID_PRIMARY(4),
            TO_SUGGESTION2_WITH_EMPTY_PRIMARY(5),
            TO_SUGGESTION3_WITH_VALID_PRIMARY(6),
            TO_SUGGESTION3_WITH_EMPTY_PRIMARY(7),
            TO_EMPTY_WITH_VALID_SUGGESTIONS(8),
            TO_EMPTY_WITH_VALID_PRIMARY(15),
            TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY(16),
            TO_EMPTY_WITH_EMPTY_SUGGESTIONS(9),
            TO_EMPTY_WITH_SUGGESTIONS_DISABLED(10),
            TO_CUSTOM_WITH_VALID_SUGGESTIONS(11),
            TO_CUSTOM_WITH_VALID_PRIMARY(17),
            TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY(18),
            TO_CUSTOM_WITH_EMPTY_SUGGESTIONS(12),
            TO_CUSTOM_WITH_SUGGESTIONS_DISABLED(13),
            UNCHANGED(14);

            public static final int TO_CUSTOM_WITH_EMPTY_SUGGESTIONS_VALUE = 12;
            public static final int TO_CUSTOM_WITH_SUGGESTIONS_DISABLED_VALUE = 13;
            public static final int TO_CUSTOM_WITH_VALID_PRIMARY_VALUE = 17;
            public static final int TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY_VALUE = 18;
            public static final int TO_CUSTOM_WITH_VALID_SUGGESTIONS_VALUE = 11;
            public static final int TO_EMPTY_WITH_EMPTY_SUGGESTIONS_VALUE = 9;
            public static final int TO_EMPTY_WITH_SUGGESTIONS_DISABLED_VALUE = 10;
            public static final int TO_EMPTY_WITH_VALID_PRIMARY_VALUE = 15;
            public static final int TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY_VALUE = 16;
            public static final int TO_EMPTY_WITH_VALID_SUGGESTIONS_VALUE = 8;
            public static final int TO_FOLDER_LABEL_STATE_UNSPECIFIED_VALUE = 0;
            public static final int TO_SUGGESTION0_WITH_VALID_PRIMARY_VALUE = 1;
            public static final int TO_SUGGESTION1_WITH_EMPTY_PRIMARY_VALUE = 3;
            public static final int TO_SUGGESTION1_WITH_VALID_PRIMARY_VALUE = 2;
            public static final int TO_SUGGESTION2_WITH_EMPTY_PRIMARY_VALUE = 5;
            public static final int TO_SUGGESTION2_WITH_VALID_PRIMARY_VALUE = 4;
            public static final int TO_SUGGESTION3_WITH_EMPTY_PRIMARY_VALUE = 7;
            public static final int TO_SUGGESTION3_WITH_VALID_PRIMARY_VALUE = 6;
            public static final int UNCHANGED_VALUE = 14;
            private static final Internal.EnumLiteMap<ToFolderLabelState> internalValueMap = new Internal.EnumLiteMap<ToFolderLabelState>() { // from class: com.android.launcher3.userevent.LauncherLogProto.Target.ToFolderLabelState.1
                /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
                @Override // com.google.protobuf.Internal.EnumLiteMap
                public ToFolderLabelState findValueByNumber(int i) {
                    return ToFolderLabelState.forNumber(i);
                }
            };
            private final int value;

            @Override // com.google.protobuf.Internal.EnumLite
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static ToFolderLabelState valueOf(int i) {
                return forNumber(i);
            }

            public static ToFolderLabelState forNumber(int i) {
                switch (i) {
                    case 0:
                        return TO_FOLDER_LABEL_STATE_UNSPECIFIED;
                    case 1:
                        return TO_SUGGESTION0_WITH_VALID_PRIMARY;
                    case 2:
                        return TO_SUGGESTION1_WITH_VALID_PRIMARY;
                    case 3:
                        return TO_SUGGESTION1_WITH_EMPTY_PRIMARY;
                    case 4:
                        return TO_SUGGESTION2_WITH_VALID_PRIMARY;
                    case 5:
                        return TO_SUGGESTION2_WITH_EMPTY_PRIMARY;
                    case 6:
                        return TO_SUGGESTION3_WITH_VALID_PRIMARY;
                    case 7:
                        return TO_SUGGESTION3_WITH_EMPTY_PRIMARY;
                    case 8:
                        return TO_EMPTY_WITH_VALID_SUGGESTIONS;
                    case 9:
                        return TO_EMPTY_WITH_EMPTY_SUGGESTIONS;
                    case 10:
                        return TO_EMPTY_WITH_SUGGESTIONS_DISABLED;
                    case 11:
                        return TO_CUSTOM_WITH_VALID_SUGGESTIONS;
                    case 12:
                        return TO_CUSTOM_WITH_EMPTY_SUGGESTIONS;
                    case 13:
                        return TO_CUSTOM_WITH_SUGGESTIONS_DISABLED;
                    case 14:
                        return UNCHANGED;
                    case 15:
                        return TO_EMPTY_WITH_VALID_PRIMARY;
                    case 16:
                        return TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY;
                    case 17:
                        return TO_CUSTOM_WITH_VALID_PRIMARY;
                    case 18:
                        return TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY;
                    default:
                        return null;
                }
            }

            public static Internal.EnumLiteMap<ToFolderLabelState> internalGetValueMap() {
                return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
                return ToFolderLabelStateVerifier.INSTANCE;
            }

            private static final class ToFolderLabelStateVerifier implements Internal.EnumVerifier {
                static final Internal.EnumVerifier INSTANCE = new ToFolderLabelStateVerifier();

                private ToFolderLabelStateVerifier() {
                }

                @Override // com.google.protobuf.Internal.EnumVerifier
                public boolean isInRange(int i) {
                    return ToFolderLabelState.forNumber(i) != null;
                }
            }

            ToFolderLabelState(int i) {
                this.value = i;
            }
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public Type getType() {
            Type typeForNumber = Type.forNumber(this.type_);
            return typeForNumber == null ? Type.NONE : typeForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setType(Type type) {
            Objects.requireNonNull(type);
            this.bitField0_ |= 1;
            this.type_ = type.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearType() {
            this.bitField0_ &= -2;
            this.type_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasPageIndex() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getPageIndex() {
            return this.pageIndex_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPageIndex(int i) {
            this.bitField0_ |= 2;
            this.pageIndex_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPageIndex() {
            this.bitField0_ &= -3;
            this.pageIndex_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasRank() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getRank() {
            return this.rank_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRank(int i) {
            this.bitField0_ |= 4;
            this.rank_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearRank() {
            this.bitField0_ &= -5;
            this.rank_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasGridX() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getGridX() {
            return this.gridX_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setGridX(int i) {
            this.bitField0_ |= 8;
            this.gridX_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearGridX() {
            this.bitField0_ &= -9;
            this.gridX_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasGridY() {
            return (this.bitField0_ & 16) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getGridY() {
            return this.gridY_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setGridY(int i) {
            this.bitField0_ |= 16;
            this.gridY_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearGridY() {
            this.bitField0_ &= -17;
            this.gridY_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasContainerType() {
            return (this.bitField0_ & 32) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public ContainerType getContainerType() {
            ContainerType containerTypeForNumber = ContainerType.forNumber(this.containerType_);
            return containerTypeForNumber == null ? ContainerType.DEFAULT_CONTAINERTYPE : containerTypeForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setContainerType(ContainerType containerType) {
            Objects.requireNonNull(containerType);
            this.bitField0_ |= 32;
            this.containerType_ = containerType.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearContainerType() {
            this.bitField0_ &= -33;
            this.containerType_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasCardinality() {
            return (this.bitField0_ & 64) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getCardinality() {
            return this.cardinality_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCardinality(int i) {
            this.bitField0_ |= 64;
            this.cardinality_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearCardinality() {
            this.bitField0_ &= -65;
            this.cardinality_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasControlType() {
            return (this.bitField0_ & 128) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public ControlType getControlType() {
            ControlType controlTypeForNumber = ControlType.forNumber(this.controlType_);
            return controlTypeForNumber == null ? ControlType.DEFAULT_CONTROLTYPE : controlTypeForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setControlType(ControlType controlType) {
            Objects.requireNonNull(controlType);
            this.bitField0_ |= 128;
            this.controlType_ = controlType.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearControlType() {
            this.bitField0_ &= -129;
            this.controlType_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasItemType() {
            return (this.bitField0_ & 256) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public ItemType getItemType() {
            ItemType itemTypeForNumber = ItemType.forNumber(this.itemType_);
            return itemTypeForNumber == null ? ItemType.DEFAULT_ITEMTYPE : itemTypeForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setItemType(ItemType itemType) {
            Objects.requireNonNull(itemType);
            this.bitField0_ |= 256;
            this.itemType_ = itemType.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearItemType() {
            this.bitField0_ &= -257;
            this.itemType_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasPackageNameHash() {
            return (this.bitField0_ & 512) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getPackageNameHash() {
            return this.packageNameHash_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPackageNameHash(int i) {
            this.bitField0_ |= 512;
            this.packageNameHash_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPackageNameHash() {
            this.bitField0_ &= -513;
            this.packageNameHash_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasComponentHash() {
            return (this.bitField0_ & 1024) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getComponentHash() {
            return this.componentHash_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setComponentHash(int i) {
            this.bitField0_ |= 1024;
            this.componentHash_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearComponentHash() {
            this.bitField0_ &= -1025;
            this.componentHash_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasIntentHash() {
            return (this.bitField0_ & 2048) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getIntentHash() {
            return this.intentHash_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIntentHash(int i) {
            this.bitField0_ |= 2048;
            this.intentHash_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIntentHash() {
            this.bitField0_ &= -2049;
            this.intentHash_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasSpanX() {
            return (this.bitField0_ & 4096) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getSpanX() {
            return this.spanX_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSpanX(int i) {
            this.bitField0_ |= 4096;
            this.spanX_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSpanX() {
            this.bitField0_ &= -4097;
            this.spanX_ = 1;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasSpanY() {
            return (this.bitField0_ & 8192) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getSpanY() {
            return this.spanY_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSpanY(int i) {
            this.bitField0_ |= 8192;
            this.spanY_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSpanY() {
            this.bitField0_ &= -8193;
            this.spanY_ = 1;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasPredictedRank() {
            return (this.bitField0_ & 16384) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getPredictedRank() {
            return this.predictedRank_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPredictedRank(int i) {
            this.bitField0_ |= 16384;
            this.predictedRank_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPredictedRank() {
            this.bitField0_ &= -16385;
            this.predictedRank_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasExtension() {
            return (this.bitField0_ & 32768) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public LauncherLogExtensions.TargetExtension getExtension() {
            LauncherLogExtensions.TargetExtension targetExtension = this.extension_;
            return targetExtension == null ? LauncherLogExtensions.TargetExtension.getDefaultInstance() : targetExtension;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setExtension(LauncherLogExtensions.TargetExtension targetExtension) {
            Objects.requireNonNull(targetExtension);
            this.extension_ = targetExtension;
            this.bitField0_ |= 32768;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setExtension(LauncherLogExtensions.TargetExtension.Builder builder) {
            this.extension_ = builder.build();
            this.bitField0_ |= 32768;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeExtension(LauncherLogExtensions.TargetExtension targetExtension) {
            Objects.requireNonNull(targetExtension);
            LauncherLogExtensions.TargetExtension targetExtension2 = this.extension_;
            if (targetExtension2 != null && targetExtension2 != LauncherLogExtensions.TargetExtension.getDefaultInstance()) {
                this.extension_ = LauncherLogExtensions.TargetExtension.newBuilder(this.extension_).mergeFrom(targetExtension).buildPartial();
            } else {
                this.extension_ = targetExtension;
            }
            this.bitField0_ |= 32768;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearExtension() {
            this.extension_ = null;
            this.bitField0_ &= -32769;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasTipType() {
            return (this.bitField0_ & 65536) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public TipType getTipType() {
            TipType tipTypeForNumber = TipType.forNumber(this.tipType_);
            return tipTypeForNumber == null ? TipType.DEFAULT_NONE : tipTypeForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setTipType(TipType tipType) {
            Objects.requireNonNull(tipType);
            this.bitField0_ |= 65536;
            this.tipType_ = tipType.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearTipType() {
            this.bitField0_ &= -65537;
            this.tipType_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasSearchQueryLength() {
            return (this.bitField0_ & 131072) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public int getSearchQueryLength() {
            return this.searchQueryLength_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSearchQueryLength(int i) {
            this.bitField0_ |= 131072;
            this.searchQueryLength_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSearchQueryLength() {
            this.bitField0_ &= -131073;
            this.searchQueryLength_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasIsWorkApp() {
            return (this.bitField0_ & 262144) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean getIsWorkApp() {
            return this.isWorkApp_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIsWorkApp(boolean z) {
            this.bitField0_ |= 262144;
            this.isWorkApp_ = z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIsWorkApp() {
            this.bitField0_ &= -262145;
            this.isWorkApp_ = false;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasFromFolderLabelState() {
            return (this.bitField0_ & 524288) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public FromFolderLabelState getFromFolderLabelState() {
            FromFolderLabelState fromFolderLabelStateForNumber = FromFolderLabelState.forNumber(this.fromFolderLabelState_);
            return fromFolderLabelStateForNumber == null ? FromFolderLabelState.FROM_FOLDER_LABEL_STATE_UNSPECIFIED : fromFolderLabelStateForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFromFolderLabelState(FromFolderLabelState fromFolderLabelState) {
            Objects.requireNonNull(fromFolderLabelState);
            this.bitField0_ |= 524288;
            this.fromFolderLabelState_ = fromFolderLabelState.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearFromFolderLabelState() {
            this.bitField0_ &= -524289;
            this.fromFolderLabelState_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public boolean hasToFolderLabelState() {
            return (this.bitField0_ & 1048576) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
        public ToFolderLabelState getToFolderLabelState() {
            ToFolderLabelState toFolderLabelStateForNumber = ToFolderLabelState.forNumber(this.toFolderLabelState_);
            return toFolderLabelStateForNumber == null ? ToFolderLabelState.TO_FOLDER_LABEL_STATE_UNSPECIFIED : toFolderLabelStateForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setToFolderLabelState(ToFolderLabelState toFolderLabelState) {
            Objects.requireNonNull(toFolderLabelState);
            this.bitField0_ |= 1048576;
            this.toFolderLabelState_ = toFolderLabelState.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearToFolderLabelState() {
            this.bitField0_ &= -1048577;
            this.toFolderLabelState_ = 0;
        }

        public static Target parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (Target) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Target parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Target) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static Target parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Target) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Target parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Target) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Target parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Target) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Target parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Target) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Target parseFrom(InputStream inputStream) throws IOException {
            return (Target) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Target parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Target) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Target parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Target) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Target parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Target) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Target parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Target) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Target parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Target) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Target target) {
            return DEFAULT_INSTANCE.createBuilder(target);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Target, Builder> implements TargetOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:2547) call: com.android.launcher3.userevent.LauncherLogProto.Target.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(Target.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasType() {
                return ((Target) this.instance).hasType();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public Type getType() {
                return ((Target) this.instance).getType();
            }

            public Builder setType(Type type) {
                copyOnWrite();
                ((Target) this.instance).setType(type);
                return this;
            }

            public Builder clearType() {
                copyOnWrite();
                ((Target) this.instance).clearType();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasPageIndex() {
                return ((Target) this.instance).hasPageIndex();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getPageIndex() {
                return ((Target) this.instance).getPageIndex();
            }

            public Builder setPageIndex(int i) {
                copyOnWrite();
                ((Target) this.instance).setPageIndex(i);
                return this;
            }

            public Builder clearPageIndex() {
                copyOnWrite();
                ((Target) this.instance).clearPageIndex();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasRank() {
                return ((Target) this.instance).hasRank();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getRank() {
                return ((Target) this.instance).getRank();
            }

            public Builder setRank(int i) {
                copyOnWrite();
                ((Target) this.instance).setRank(i);
                return this;
            }

            public Builder clearRank() {
                copyOnWrite();
                ((Target) this.instance).clearRank();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasGridX() {
                return ((Target) this.instance).hasGridX();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getGridX() {
                return ((Target) this.instance).getGridX();
            }

            public Builder setGridX(int i) {
                copyOnWrite();
                ((Target) this.instance).setGridX(i);
                return this;
            }

            public Builder clearGridX() {
                copyOnWrite();
                ((Target) this.instance).clearGridX();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasGridY() {
                return ((Target) this.instance).hasGridY();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getGridY() {
                return ((Target) this.instance).getGridY();
            }

            public Builder setGridY(int i) {
                copyOnWrite();
                ((Target) this.instance).setGridY(i);
                return this;
            }

            public Builder clearGridY() {
                copyOnWrite();
                ((Target) this.instance).clearGridY();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasContainerType() {
                return ((Target) this.instance).hasContainerType();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public ContainerType getContainerType() {
                return ((Target) this.instance).getContainerType();
            }

            public Builder setContainerType(ContainerType containerType) {
                copyOnWrite();
                ((Target) this.instance).setContainerType(containerType);
                return this;
            }

            public Builder clearContainerType() {
                copyOnWrite();
                ((Target) this.instance).clearContainerType();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasCardinality() {
                return ((Target) this.instance).hasCardinality();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getCardinality() {
                return ((Target) this.instance).getCardinality();
            }

            public Builder setCardinality(int i) {
                copyOnWrite();
                ((Target) this.instance).setCardinality(i);
                return this;
            }

            public Builder clearCardinality() {
                copyOnWrite();
                ((Target) this.instance).clearCardinality();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasControlType() {
                return ((Target) this.instance).hasControlType();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public ControlType getControlType() {
                return ((Target) this.instance).getControlType();
            }

            public Builder setControlType(ControlType controlType) {
                copyOnWrite();
                ((Target) this.instance).setControlType(controlType);
                return this;
            }

            public Builder clearControlType() {
                copyOnWrite();
                ((Target) this.instance).clearControlType();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasItemType() {
                return ((Target) this.instance).hasItemType();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public ItemType getItemType() {
                return ((Target) this.instance).getItemType();
            }

            public Builder setItemType(ItemType itemType) {
                copyOnWrite();
                ((Target) this.instance).setItemType(itemType);
                return this;
            }

            public Builder clearItemType() {
                copyOnWrite();
                ((Target) this.instance).clearItemType();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasPackageNameHash() {
                return ((Target) this.instance).hasPackageNameHash();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getPackageNameHash() {
                return ((Target) this.instance).getPackageNameHash();
            }

            public Builder setPackageNameHash(int i) {
                copyOnWrite();
                ((Target) this.instance).setPackageNameHash(i);
                return this;
            }

            public Builder clearPackageNameHash() {
                copyOnWrite();
                ((Target) this.instance).clearPackageNameHash();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasComponentHash() {
                return ((Target) this.instance).hasComponentHash();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getComponentHash() {
                return ((Target) this.instance).getComponentHash();
            }

            public Builder setComponentHash(int i) {
                copyOnWrite();
                ((Target) this.instance).setComponentHash(i);
                return this;
            }

            public Builder clearComponentHash() {
                copyOnWrite();
                ((Target) this.instance).clearComponentHash();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasIntentHash() {
                return ((Target) this.instance).hasIntentHash();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getIntentHash() {
                return ((Target) this.instance).getIntentHash();
            }

            public Builder setIntentHash(int i) {
                copyOnWrite();
                ((Target) this.instance).setIntentHash(i);
                return this;
            }

            public Builder clearIntentHash() {
                copyOnWrite();
                ((Target) this.instance).clearIntentHash();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasSpanX() {
                return ((Target) this.instance).hasSpanX();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getSpanX() {
                return ((Target) this.instance).getSpanX();
            }

            public Builder setSpanX(int i) {
                copyOnWrite();
                ((Target) this.instance).setSpanX(i);
                return this;
            }

            public Builder clearSpanX() {
                copyOnWrite();
                ((Target) this.instance).clearSpanX();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasSpanY() {
                return ((Target) this.instance).hasSpanY();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getSpanY() {
                return ((Target) this.instance).getSpanY();
            }

            public Builder setSpanY(int i) {
                copyOnWrite();
                ((Target) this.instance).setSpanY(i);
                return this;
            }

            public Builder clearSpanY() {
                copyOnWrite();
                ((Target) this.instance).clearSpanY();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasPredictedRank() {
                return ((Target) this.instance).hasPredictedRank();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getPredictedRank() {
                return ((Target) this.instance).getPredictedRank();
            }

            public Builder setPredictedRank(int i) {
                copyOnWrite();
                ((Target) this.instance).setPredictedRank(i);
                return this;
            }

            public Builder clearPredictedRank() {
                copyOnWrite();
                ((Target) this.instance).clearPredictedRank();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasExtension() {
                return ((Target) this.instance).hasExtension();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public LauncherLogExtensions.TargetExtension getExtension() {
                return ((Target) this.instance).getExtension();
            }

            public Builder setExtension(LauncherLogExtensions.TargetExtension targetExtension) {
                copyOnWrite();
                ((Target) this.instance).setExtension(targetExtension);
                return this;
            }

            public Builder setExtension(LauncherLogExtensions.TargetExtension.Builder builder) {
                copyOnWrite();
                ((Target) this.instance).setExtension(builder);
                return this;
            }

            public Builder mergeExtension(LauncherLogExtensions.TargetExtension targetExtension) {
                copyOnWrite();
                ((Target) this.instance).mergeExtension(targetExtension);
                return this;
            }

            public Builder clearExtension() {
                copyOnWrite();
                ((Target) this.instance).clearExtension();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasTipType() {
                return ((Target) this.instance).hasTipType();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public TipType getTipType() {
                return ((Target) this.instance).getTipType();
            }

            public Builder setTipType(TipType tipType) {
                copyOnWrite();
                ((Target) this.instance).setTipType(tipType);
                return this;
            }

            public Builder clearTipType() {
                copyOnWrite();
                ((Target) this.instance).clearTipType();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasSearchQueryLength() {
                return ((Target) this.instance).hasSearchQueryLength();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public int getSearchQueryLength() {
                return ((Target) this.instance).getSearchQueryLength();
            }

            public Builder setSearchQueryLength(int i) {
                copyOnWrite();
                ((Target) this.instance).setSearchQueryLength(i);
                return this;
            }

            public Builder clearSearchQueryLength() {
                copyOnWrite();
                ((Target) this.instance).clearSearchQueryLength();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasIsWorkApp() {
                return ((Target) this.instance).hasIsWorkApp();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean getIsWorkApp() {
                return ((Target) this.instance).getIsWorkApp();
            }

            public Builder setIsWorkApp(boolean z) {
                copyOnWrite();
                ((Target) this.instance).setIsWorkApp(z);
                return this;
            }

            public Builder clearIsWorkApp() {
                copyOnWrite();
                ((Target) this.instance).clearIsWorkApp();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasFromFolderLabelState() {
                return ((Target) this.instance).hasFromFolderLabelState();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public FromFolderLabelState getFromFolderLabelState() {
                return ((Target) this.instance).getFromFolderLabelState();
            }

            public Builder setFromFolderLabelState(FromFolderLabelState fromFolderLabelState) {
                copyOnWrite();
                ((Target) this.instance).setFromFolderLabelState(fromFolderLabelState);
                return this;
            }

            public Builder clearFromFolderLabelState() {
                copyOnWrite();
                ((Target) this.instance).clearFromFolderLabelState();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public boolean hasToFolderLabelState() {
                return ((Target) this.instance).hasToFolderLabelState();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.TargetOrBuilder
            public ToFolderLabelState getToFolderLabelState() {
                return ((Target) this.instance).getToFolderLabelState();
            }

            public Builder setToFolderLabelState(ToFolderLabelState toFolderLabelState) {
                copyOnWrite();
                ((Target) this.instance).setToFolderLabelState(toFolderLabelState);
                return this;
            }

            public Builder clearToFolderLabelState() {
                copyOnWrite();
                ((Target) this.instance).clearToFolderLabelState();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Target();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0015\u0000\u0001\u0001\u0015\u0015\u0000\u0000\u0000\u0001\f\u0000\u0002\u0004\u0001\u0003\u0004\u0002\u0004\u0004\u0003\u0005\u0004\u0004\u0006\f\u0005\u0007\u0004\u0006\b\f\u0007\t\f\b\n\u0004\t\u000b\u0004\n\f\u0004\u000b\r\u0004\f\u000e\u0004\r\u000f\u0004\u000e\u0010\t\u000f\u0011\f\u0010\u0012\u0004\u0011\u0013\u0007\u0012\u0014\f\u0013\u0015\f\u0014", new Object[]{"bitField0_", "type_", Type.internalGetVerifier(), "pageIndex_", "rank_", "gridX_", "gridY_", "containerType_", ContainerType.internalGetVerifier(), "cardinality_", "controlType_", ControlType.internalGetVerifier(), "itemType_", ItemType.internalGetVerifier(), "packageNameHash_", "componentHash_", "intentHash_", "spanX_", "spanY_", "predictedRank_", "extension_", "tipType_", TipType.internalGetVerifier(), "searchQueryLength_", "isWorkApp_", "fromFolderLabelState_", FromFolderLabelState.internalGetVerifier(), "toFolderLabelState_", ToFolderLabelState.internalGetVerifier()});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<Target> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (Target.class) {
                            defaultInstanceBasedParser = PARSER;
                            if (defaultInstanceBasedParser == null) {
                                defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                                PARSER = defaultInstanceBasedParser;
                            }
                            break;
                        }
                    }
                    return defaultInstanceBasedParser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            Target target = new Target();
            DEFAULT_INSTANCE = target;
            GeneratedMessageLite.registerDefaultInstance(Target.class, target);
        }

        public static Target getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Target> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* JADX INFO: renamed from: com.android.launcher3.userevent.LauncherLogProto$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke;

        static {
            int[] iArr = new int[GeneratedMessageLite.MethodToInvoke.values().length];
            $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke = iArr;
            try {
                iArr[GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.NEW_BUILDER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.BUILD_MESSAGE_INFO.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_PARSER.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_MEMOIZED_IS_INITIALIZED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.SET_MEMOIZED_IS_INITIALIZED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    public static final class Action extends GeneratedMessageLite<Action, Builder> implements ActionOrBuilder {
        public static final int COMMAND_FIELD_NUMBER = 4;
        private static final Action DEFAULT_INSTANCE;
        public static final int DIR_FIELD_NUMBER = 3;
        public static final int IS_OUTSIDE_FIELD_NUMBER = 5;
        public static final int IS_STATE_CHANGE_FIELD_NUMBER = 6;
        private static volatile Parser<Action> PARSER = null;
        public static final int TOUCH_FIELD_NUMBER = 2;
        public static final int TYPE_FIELD_NUMBER = 1;
        private int bitField0_;
        private int command_;
        private int dir_;
        private boolean isOutside_;
        private boolean isStateChange_;
        private int touch_;
        private int type_;

        private Action() {
        }

        public enum Type implements Internal.EnumLite {
            TOUCH(0),
            AUTOMATED(1),
            COMMAND(2),
            TIP(3),
            SOFT_KEYBOARD(4);

            public static final int AUTOMATED_VALUE = 1;
            public static final int COMMAND_VALUE = 2;
            public static final int SOFT_KEYBOARD_VALUE = 4;
            public static final int TIP_VALUE = 3;
            public static final int TOUCH_VALUE = 0;
            private static final Internal.EnumLiteMap<Type> internalValueMap = new Internal.EnumLiteMap<Type>() { // from class: com.android.launcher3.userevent.LauncherLogProto.Action.Type.1
                /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
                @Override // com.google.protobuf.Internal.EnumLiteMap
                public Type findValueByNumber(int i) {
                    return Type.forNumber(i);
                }
            };
            private final int value;

            @Override // com.google.protobuf.Internal.EnumLite
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Type valueOf(int i) {
                return forNumber(i);
            }

            public static Type forNumber(int i) {
                if (i == 0) {
                    return TOUCH;
                }
                if (i == 1) {
                    return AUTOMATED;
                }
                if (i == 2) {
                    return COMMAND;
                }
                if (i == 3) {
                    return TIP;
                }
                if (i != 4) {
                    return null;
                }
                return SOFT_KEYBOARD;
            }

            public static Internal.EnumLiteMap<Type> internalGetValueMap() {
                return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
                return TypeVerifier.INSTANCE;
            }

            private static final class TypeVerifier implements Internal.EnumVerifier {
                static final Internal.EnumVerifier INSTANCE = new TypeVerifier();

                private TypeVerifier() {
                }

                @Override // com.google.protobuf.Internal.EnumVerifier
                public boolean isInRange(int i) {
                    return Type.forNumber(i) != null;
                }
            }

            Type(int i) {
                this.value = i;
            }
        }

        public enum Touch implements Internal.EnumLite {
            TAP(0),
            LONGPRESS(1),
            DRAGDROP(2),
            SWIPE(3),
            FLING(4),
            PINCH(5),
            SWIPE_NOOP(6);

            public static final int DRAGDROP_VALUE = 2;
            public static final int FLING_VALUE = 4;
            public static final int LONGPRESS_VALUE = 1;
            public static final int PINCH_VALUE = 5;
            public static final int SWIPE_NOOP_VALUE = 6;
            public static final int SWIPE_VALUE = 3;
            public static final int TAP_VALUE = 0;
            private static final Internal.EnumLiteMap<Touch> internalValueMap = new Internal.EnumLiteMap<Touch>() { // from class: com.android.launcher3.userevent.LauncherLogProto.Action.Touch.1
                /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
                @Override // com.google.protobuf.Internal.EnumLiteMap
                public Touch findValueByNumber(int i) {
                    return Touch.forNumber(i);
                }
            };
            private final int value;

            @Override // com.google.protobuf.Internal.EnumLite
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Touch valueOf(int i) {
                return forNumber(i);
            }

            public static Touch forNumber(int i) {
                switch (i) {
                    case 0:
                        return TAP;
                    case 1:
                        return LONGPRESS;
                    case 2:
                        return DRAGDROP;
                    case 3:
                        return SWIPE;
                    case 4:
                        return FLING;
                    case 5:
                        return PINCH;
                    case 6:
                        return SWIPE_NOOP;
                    default:
                        return null;
                }
            }

            public static Internal.EnumLiteMap<Touch> internalGetValueMap() {
                return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
                return TouchVerifier.INSTANCE;
            }

            private static final class TouchVerifier implements Internal.EnumVerifier {
                static final Internal.EnumVerifier INSTANCE = new TouchVerifier();

                private TouchVerifier() {
                }

                @Override // com.google.protobuf.Internal.EnumVerifier
                public boolean isInRange(int i) {
                    return Touch.forNumber(i) != null;
                }
            }

            Touch(int i) {
                this.value = i;
            }
        }

        public enum Direction implements Internal.EnumLite {
            NONE(0),
            UP(1),
            DOWN(2),
            LEFT(3),
            RIGHT(4),
            UPRIGHT(5),
            UPLEFT(6);

            public static final int DOWN_VALUE = 2;
            public static final int LEFT_VALUE = 3;
            public static final int NONE_VALUE = 0;
            public static final int RIGHT_VALUE = 4;
            public static final int UPLEFT_VALUE = 6;
            public static final int UPRIGHT_VALUE = 5;
            public static final int UP_VALUE = 1;
            private static final Internal.EnumLiteMap<Direction> internalValueMap = new Internal.EnumLiteMap<Direction>() { // from class: com.android.launcher3.userevent.LauncherLogProto.Action.Direction.1
                /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
                @Override // com.google.protobuf.Internal.EnumLiteMap
                public Direction findValueByNumber(int i) {
                    return Direction.forNumber(i);
                }
            };
            private final int value;

            @Override // com.google.protobuf.Internal.EnumLite
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Direction valueOf(int i) {
                return forNumber(i);
            }

            public static Direction forNumber(int i) {
                switch (i) {
                    case 0:
                        return NONE;
                    case 1:
                        return UP;
                    case 2:
                        return DOWN;
                    case 3:
                        return LEFT;
                    case 4:
                        return RIGHT;
                    case 5:
                        return UPRIGHT;
                    case 6:
                        return UPLEFT;
                    default:
                        return null;
                }
            }

            public static Internal.EnumLiteMap<Direction> internalGetValueMap() {
                return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
                return DirectionVerifier.INSTANCE;
            }

            private static final class DirectionVerifier implements Internal.EnumVerifier {
                static final Internal.EnumVerifier INSTANCE = new DirectionVerifier();

                private DirectionVerifier() {
                }

                @Override // com.google.protobuf.Internal.EnumVerifier
                public boolean isInRange(int i) {
                    return Direction.forNumber(i) != null;
                }
            }

            Direction(int i) {
                this.value = i;
            }
        }

        public enum Command implements Internal.EnumLite {
            HOME_INTENT(0),
            BACK(1),
            ENTRY(2),
            CANCEL(3),
            CONFIRM(4),
            STOP(5),
            RECENTS_BUTTON(6),
            RESUME(7);

            public static final int BACK_VALUE = 1;
            public static final int CANCEL_VALUE = 3;
            public static final int CONFIRM_VALUE = 4;
            public static final int ENTRY_VALUE = 2;
            public static final int HOME_INTENT_VALUE = 0;
            public static final int RECENTS_BUTTON_VALUE = 6;
            public static final int RESUME_VALUE = 7;
            public static final int STOP_VALUE = 5;
            private static final Internal.EnumLiteMap<Command> internalValueMap = new Internal.EnumLiteMap<Command>() { // from class: com.android.launcher3.userevent.LauncherLogProto.Action.Command.1
                /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
                @Override // com.google.protobuf.Internal.EnumLiteMap
                public Command findValueByNumber(int i) {
                    return Command.forNumber(i);
                }
            };
            private final int value;

            @Override // com.google.protobuf.Internal.EnumLite
            public final int getNumber() {
                return this.value;
            }

            @Deprecated
            public static Command valueOf(int i) {
                return forNumber(i);
            }

            public static Command forNumber(int i) {
                switch (i) {
                    case 0:
                        return HOME_INTENT;
                    case 1:
                        return BACK;
                    case 2:
                        return ENTRY;
                    case 3:
                        return CANCEL;
                    case 4:
                        return CONFIRM;
                    case 5:
                        return STOP;
                    case 6:
                        return RECENTS_BUTTON;
                    case 7:
                        return RESUME;
                    default:
                        return null;
                }
            }

            public static Internal.EnumLiteMap<Command> internalGetValueMap() {
                return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
                return CommandVerifier.INSTANCE;
            }

            private static final class CommandVerifier implements Internal.EnumVerifier {
                static final Internal.EnumVerifier INSTANCE = new CommandVerifier();

                private CommandVerifier() {
                }

                @Override // com.google.protobuf.Internal.EnumVerifier
                public boolean isInRange(int i) {
                    return Command.forNumber(i) != null;
                }
            }

            Command(int i) {
                this.value = i;
            }
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public Type getType() {
            Type typeForNumber = Type.forNumber(this.type_);
            return typeForNumber == null ? Type.TOUCH : typeForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setType(Type type) {
            Objects.requireNonNull(type);
            this.bitField0_ |= 1;
            this.type_ = type.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearType() {
            this.bitField0_ &= -2;
            this.type_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public boolean hasTouch() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public Touch getTouch() {
            Touch touchForNumber = Touch.forNumber(this.touch_);
            return touchForNumber == null ? Touch.TAP : touchForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setTouch(Touch touch) {
            Objects.requireNonNull(touch);
            this.bitField0_ |= 2;
            this.touch_ = touch.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearTouch() {
            this.bitField0_ &= -3;
            this.touch_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public boolean hasDir() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public Direction getDir() {
            Direction directionForNumber = Direction.forNumber(this.dir_);
            return directionForNumber == null ? Direction.NONE : directionForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDir(Direction direction) {
            Objects.requireNonNull(direction);
            this.bitField0_ |= 4;
            this.dir_ = direction.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearDir() {
            this.bitField0_ &= -5;
            this.dir_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public boolean hasCommand() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public Command getCommand() {
            Command commandForNumber = Command.forNumber(this.command_);
            return commandForNumber == null ? Command.HOME_INTENT : commandForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCommand(Command command) {
            Objects.requireNonNull(command);
            this.bitField0_ |= 8;
            this.command_ = command.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearCommand() {
            this.bitField0_ &= -9;
            this.command_ = 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public boolean hasIsOutside() {
            return (this.bitField0_ & 16) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public boolean getIsOutside() {
            return this.isOutside_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIsOutside(boolean z) {
            this.bitField0_ |= 16;
            this.isOutside_ = z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIsOutside() {
            this.bitField0_ &= -17;
            this.isOutside_ = false;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public boolean hasIsStateChange() {
            return (this.bitField0_ & 32) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
        public boolean getIsStateChange() {
            return this.isStateChange_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIsStateChange(boolean z) {
            this.bitField0_ |= 32;
            this.isStateChange_ = z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIsStateChange() {
            this.bitField0_ &= -33;
            this.isStateChange_ = false;
        }

        public static Action parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (Action) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Action parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Action) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static Action parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Action) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Action parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Action) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Action parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Action) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Action parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Action) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Action parseFrom(InputStream inputStream) throws IOException {
            return (Action) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Action parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Action) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Action parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Action) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Action parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Action) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Action parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Action) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Action parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Action) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Action action) {
            return DEFAULT_INSTANCE.createBuilder(action);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Action, Builder> implements ActionOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:4405) call: com.android.launcher3.userevent.LauncherLogProto.Action.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(Action.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public boolean hasType() {
                return ((Action) this.instance).hasType();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public Type getType() {
                return ((Action) this.instance).getType();
            }

            public Builder setType(Type type) {
                copyOnWrite();
                ((Action) this.instance).setType(type);
                return this;
            }

            public Builder clearType() {
                copyOnWrite();
                ((Action) this.instance).clearType();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public boolean hasTouch() {
                return ((Action) this.instance).hasTouch();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public Touch getTouch() {
                return ((Action) this.instance).getTouch();
            }

            public Builder setTouch(Touch touch) {
                copyOnWrite();
                ((Action) this.instance).setTouch(touch);
                return this;
            }

            public Builder clearTouch() {
                copyOnWrite();
                ((Action) this.instance).clearTouch();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public boolean hasDir() {
                return ((Action) this.instance).hasDir();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public Direction getDir() {
                return ((Action) this.instance).getDir();
            }

            public Builder setDir(Direction direction) {
                copyOnWrite();
                ((Action) this.instance).setDir(direction);
                return this;
            }

            public Builder clearDir() {
                copyOnWrite();
                ((Action) this.instance).clearDir();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public boolean hasCommand() {
                return ((Action) this.instance).hasCommand();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public Command getCommand() {
                return ((Action) this.instance).getCommand();
            }

            public Builder setCommand(Command command) {
                copyOnWrite();
                ((Action) this.instance).setCommand(command);
                return this;
            }

            public Builder clearCommand() {
                copyOnWrite();
                ((Action) this.instance).clearCommand();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public boolean hasIsOutside() {
                return ((Action) this.instance).hasIsOutside();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public boolean getIsOutside() {
                return ((Action) this.instance).getIsOutside();
            }

            public Builder setIsOutside(boolean z) {
                copyOnWrite();
                ((Action) this.instance).setIsOutside(z);
                return this;
            }

            public Builder clearIsOutside() {
                copyOnWrite();
                ((Action) this.instance).clearIsOutside();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public boolean hasIsStateChange() {
                return ((Action) this.instance).hasIsStateChange();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.ActionOrBuilder
            public boolean getIsStateChange() {
                return ((Action) this.instance).getIsStateChange();
            }

            public Builder setIsStateChange(boolean z) {
                copyOnWrite();
                ((Action) this.instance).setIsStateChange(z);
                return this;
            }

            public Builder clearIsStateChange() {
                copyOnWrite();
                ((Action) this.instance).clearIsStateChange();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Action();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0006\u0000\u0001\u0001\u0006\u0006\u0000\u0000\u0000\u0001\f\u0000\u0002\f\u0001\u0003\f\u0002\u0004\f\u0003\u0005\u0007\u0004\u0006\u0007\u0005", new Object[]{"bitField0_", "type_", Type.internalGetVerifier(), "touch_", Touch.internalGetVerifier(), "dir_", Direction.internalGetVerifier(), "command_", Command.internalGetVerifier(), "isOutside_", "isStateChange_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<Action> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (Action.class) {
                            defaultInstanceBasedParser = PARSER;
                            if (defaultInstanceBasedParser == null) {
                                defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                                PARSER = defaultInstanceBasedParser;
                            }
                            break;
                        }
                    }
                    return defaultInstanceBasedParser;
                case 6:
                    return (byte) 1;
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            Action action = new Action();
            DEFAULT_INSTANCE = action;
            GeneratedMessageLite.registerDefaultInstance(Action.class, action);
        }

        public static Action getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Action> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class LauncherEvent extends GeneratedMessageLite<LauncherEvent, Builder> implements LauncherEventOrBuilder {
        public static final int ACTION_DURATION_MILLIS_FIELD_NUMBER = 4;
        public static final int ACTION_FIELD_NUMBER = 1;
        private static final LauncherEvent DEFAULT_INSTANCE;
        public static final int DEST_TARGET_FIELD_NUMBER = 3;
        public static final int ELAPSED_CONTAINER_MILLIS_FIELD_NUMBER = 5;
        public static final int ELAPSED_SESSION_MILLIS_FIELD_NUMBER = 6;
        public static final int EXTENSION_FIELD_NUMBER = 9;
        public static final int IS_IN_LANDSCAPE_MODE_FIELD_NUMBER = 8;
        public static final int IS_IN_MULTI_WINDOW_MODE_FIELD_NUMBER = 7;
        private static volatile Parser<LauncherEvent> PARSER = null;
        public static final int SRC_TARGET_FIELD_NUMBER = 2;
        private long actionDurationMillis_;
        private Action action_;
        private int bitField0_;
        private long elapsedContainerMillis_;
        private long elapsedSessionMillis_;
        private LauncherLogExtensions.LauncherEventExtension extension_;
        private boolean isInLandscapeMode_;
        private boolean isInMultiWindowMode_;
        private byte memoizedIsInitialized = 2;
        private Internal.ProtobufList<Target> srcTarget_ = emptyProtobufList();
        private Internal.ProtobufList<Target> destTarget_ = emptyProtobufList();

        private LauncherEvent() {
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public boolean hasAction() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public Action getAction() {
            Action action = this.action_;
            return action == null ? Action.getDefaultInstance() : action;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAction(Action action) {
            Objects.requireNonNull(action);
            this.action_ = action;
            this.bitField0_ |= 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAction(Action.Builder builder) {
            this.action_ = builder.build();
            this.bitField0_ |= 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeAction(Action action) {
            Objects.requireNonNull(action);
            Action action2 = this.action_;
            if (action2 != null && action2 != Action.getDefaultInstance()) {
                this.action_ = Action.newBuilder(this.action_).mergeFrom(action).buildPartial();
            } else {
                this.action_ = action;
            }
            this.bitField0_ |= 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearAction() {
            this.action_ = null;
            this.bitField0_ &= -2;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public List<Target> getSrcTargetList() {
            return this.srcTarget_;
        }

        public List<? extends TargetOrBuilder> getSrcTargetOrBuilderList() {
            return this.srcTarget_;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public int getSrcTargetCount() {
            return this.srcTarget_.size();
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public Target getSrcTarget(int i) {
            return this.srcTarget_.get(i);
        }

        public TargetOrBuilder getSrcTargetOrBuilder(int i) {
            return this.srcTarget_.get(i);
        }

        private void ensureSrcTargetIsMutable() {
            if (this.srcTarget_.isModifiable()) {
                return;
            }
            this.srcTarget_ = GeneratedMessageLite.mutableCopy(this.srcTarget_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSrcTarget(int i, Target target) {
            Objects.requireNonNull(target);
            ensureSrcTargetIsMutable();
            this.srcTarget_.set(i, target);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSrcTarget(int i, Target.Builder builder) {
            ensureSrcTargetIsMutable();
            this.srcTarget_.set(i, builder.build());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addSrcTarget(Target target) {
            Objects.requireNonNull(target);
            ensureSrcTargetIsMutable();
            this.srcTarget_.add(target);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addSrcTarget(int i, Target target) {
            Objects.requireNonNull(target);
            ensureSrcTargetIsMutable();
            this.srcTarget_.add(i, target);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addSrcTarget(Target.Builder builder) {
            ensureSrcTargetIsMutable();
            this.srcTarget_.add(builder.build());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addSrcTarget(int i, Target.Builder builder) {
            ensureSrcTargetIsMutable();
            this.srcTarget_.add(i, builder.build());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addAllSrcTarget(Iterable<? extends Target> iterable) {
            ensureSrcTargetIsMutable();
            AbstractMessageLite.addAll((Iterable) iterable, (List) this.srcTarget_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSrcTarget() {
            this.srcTarget_ = emptyProtobufList();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeSrcTarget(int i) {
            ensureSrcTargetIsMutable();
            this.srcTarget_.remove(i);
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public List<Target> getDestTargetList() {
            return this.destTarget_;
        }

        public List<? extends TargetOrBuilder> getDestTargetOrBuilderList() {
            return this.destTarget_;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public int getDestTargetCount() {
            return this.destTarget_.size();
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public Target getDestTarget(int i) {
            return this.destTarget_.get(i);
        }

        public TargetOrBuilder getDestTargetOrBuilder(int i) {
            return this.destTarget_.get(i);
        }

        private void ensureDestTargetIsMutable() {
            if (this.destTarget_.isModifiable()) {
                return;
            }
            this.destTarget_ = GeneratedMessageLite.mutableCopy(this.destTarget_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDestTarget(int i, Target target) {
            Objects.requireNonNull(target);
            ensureDestTargetIsMutable();
            this.destTarget_.set(i, target);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setDestTarget(int i, Target.Builder builder) {
            ensureDestTargetIsMutable();
            this.destTarget_.set(i, builder.build());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addDestTarget(Target target) {
            Objects.requireNonNull(target);
            ensureDestTargetIsMutable();
            this.destTarget_.add(target);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addDestTarget(int i, Target target) {
            Objects.requireNonNull(target);
            ensureDestTargetIsMutable();
            this.destTarget_.add(i, target);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addDestTarget(Target.Builder builder) {
            ensureDestTargetIsMutable();
            this.destTarget_.add(builder.build());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addDestTarget(int i, Target.Builder builder) {
            ensureDestTargetIsMutable();
            this.destTarget_.add(i, builder.build());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addAllDestTarget(Iterable<? extends Target> iterable) {
            ensureDestTargetIsMutable();
            AbstractMessageLite.addAll((Iterable) iterable, (List) this.destTarget_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearDestTarget() {
            this.destTarget_ = emptyProtobufList();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeDestTarget(int i) {
            ensureDestTargetIsMutable();
            this.destTarget_.remove(i);
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public boolean hasActionDurationMillis() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public long getActionDurationMillis() {
            return this.actionDurationMillis_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setActionDurationMillis(long j) {
            this.bitField0_ |= 2;
            this.actionDurationMillis_ = j;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearActionDurationMillis() {
            this.bitField0_ &= -3;
            this.actionDurationMillis_ = 0L;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public boolean hasElapsedContainerMillis() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public long getElapsedContainerMillis() {
            return this.elapsedContainerMillis_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setElapsedContainerMillis(long j) {
            this.bitField0_ |= 4;
            this.elapsedContainerMillis_ = j;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearElapsedContainerMillis() {
            this.bitField0_ &= -5;
            this.elapsedContainerMillis_ = 0L;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public boolean hasElapsedSessionMillis() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public long getElapsedSessionMillis() {
            return this.elapsedSessionMillis_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setElapsedSessionMillis(long j) {
            this.bitField0_ |= 8;
            this.elapsedSessionMillis_ = j;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearElapsedSessionMillis() {
            this.bitField0_ &= -9;
            this.elapsedSessionMillis_ = 0L;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        @Deprecated
        public boolean hasIsInMultiWindowMode() {
            return (this.bitField0_ & 16) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        @Deprecated
        public boolean getIsInMultiWindowMode() {
            return this.isInMultiWindowMode_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIsInMultiWindowMode(boolean z) {
            this.bitField0_ |= 16;
            this.isInMultiWindowMode_ = z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIsInMultiWindowMode() {
            this.bitField0_ &= -17;
            this.isInMultiWindowMode_ = false;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        @Deprecated
        public boolean hasIsInLandscapeMode() {
            return (this.bitField0_ & 32) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        @Deprecated
        public boolean getIsInLandscapeMode() {
            return this.isInLandscapeMode_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIsInLandscapeMode(boolean z) {
            this.bitField0_ |= 32;
            this.isInLandscapeMode_ = z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIsInLandscapeMode() {
            this.bitField0_ &= -33;
            this.isInLandscapeMode_ = false;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public boolean hasExtension() {
            return (this.bitField0_ & 64) != 0;
        }

        @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
        public LauncherLogExtensions.LauncherEventExtension getExtension() {
            LauncherLogExtensions.LauncherEventExtension launcherEventExtension = this.extension_;
            return launcherEventExtension == null ? LauncherLogExtensions.LauncherEventExtension.getDefaultInstance() : launcherEventExtension;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setExtension(LauncherLogExtensions.LauncherEventExtension launcherEventExtension) {
            Objects.requireNonNull(launcherEventExtension);
            this.extension_ = launcherEventExtension;
            this.bitField0_ |= 64;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setExtension(LauncherLogExtensions.LauncherEventExtension.Builder builder) {
            this.extension_ = builder.build();
            this.bitField0_ |= 64;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeExtension(LauncherLogExtensions.LauncherEventExtension launcherEventExtension) {
            Objects.requireNonNull(launcherEventExtension);
            LauncherLogExtensions.LauncherEventExtension launcherEventExtension2 = this.extension_;
            if (launcherEventExtension2 != null && launcherEventExtension2 != LauncherLogExtensions.LauncherEventExtension.getDefaultInstance()) {
                this.extension_ = LauncherLogExtensions.LauncherEventExtension.newBuilder(this.extension_).mergeFrom(launcherEventExtension).buildPartial();
            } else {
                this.extension_ = launcherEventExtension;
            }
            this.bitField0_ |= 64;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearExtension() {
            this.extension_ = null;
            this.bitField0_ &= -65;
        }

        public static LauncherEvent parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (LauncherEvent) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static LauncherEvent parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (LauncherEvent) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static LauncherEvent parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (LauncherEvent) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static LauncherEvent parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (LauncherEvent) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static LauncherEvent parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (LauncherEvent) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static LauncherEvent parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (LauncherEvent) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static LauncherEvent parseFrom(InputStream inputStream) throws IOException {
            return (LauncherEvent) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static LauncherEvent parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (LauncherEvent) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static LauncherEvent parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (LauncherEvent) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static LauncherEvent parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (LauncherEvent) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static LauncherEvent parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (LauncherEvent) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static LauncherEvent parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (LauncherEvent) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(LauncherEvent launcherEvent) {
            return DEFAULT_INSTANCE.createBuilder(launcherEvent);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<LauncherEvent, Builder> implements LauncherEventOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:5492) call: com.android.launcher3.userevent.LauncherLogProto.LauncherEvent.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(LauncherEvent.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public boolean hasAction() {
                return ((LauncherEvent) this.instance).hasAction();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public Action getAction() {
                return ((LauncherEvent) this.instance).getAction();
            }

            public Builder setAction(Action action) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setAction(action);
                return this;
            }

            public Builder setAction(Action.Builder builder) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setAction(builder);
                return this;
            }

            public Builder mergeAction(Action action) {
                copyOnWrite();
                ((LauncherEvent) this.instance).mergeAction(action);
                return this;
            }

            public Builder clearAction() {
                copyOnWrite();
                ((LauncherEvent) this.instance).clearAction();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public List<Target> getSrcTargetList() {
                return Collections.unmodifiableList(((LauncherEvent) this.instance).getSrcTargetList());
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public int getSrcTargetCount() {
                return ((LauncherEvent) this.instance).getSrcTargetCount();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public Target getSrcTarget(int i) {
                return ((LauncherEvent) this.instance).getSrcTarget(i);
            }

            public Builder setSrcTarget(int i, Target target) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setSrcTarget(i, target);
                return this;
            }

            public Builder setSrcTarget(int i, Target.Builder builder) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setSrcTarget(i, builder);
                return this;
            }

            public Builder addSrcTarget(Target target) {
                copyOnWrite();
                ((LauncherEvent) this.instance).addSrcTarget(target);
                return this;
            }

            public Builder addSrcTarget(int i, Target target) {
                copyOnWrite();
                ((LauncherEvent) this.instance).addSrcTarget(i, target);
                return this;
            }

            public Builder addSrcTarget(Target.Builder builder) {
                copyOnWrite();
                ((LauncherEvent) this.instance).addSrcTarget(builder);
                return this;
            }

            public Builder addSrcTarget(int i, Target.Builder builder) {
                copyOnWrite();
                ((LauncherEvent) this.instance).addSrcTarget(i, builder);
                return this;
            }

            public Builder addAllSrcTarget(Iterable<? extends Target> iterable) {
                copyOnWrite();
                ((LauncherEvent) this.instance).addAllSrcTarget(iterable);
                return this;
            }

            public Builder clearSrcTarget() {
                copyOnWrite();
                ((LauncherEvent) this.instance).clearSrcTarget();
                return this;
            }

            public Builder removeSrcTarget(int i) {
                copyOnWrite();
                ((LauncherEvent) this.instance).removeSrcTarget(i);
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public List<Target> getDestTargetList() {
                return Collections.unmodifiableList(((LauncherEvent) this.instance).getDestTargetList());
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public int getDestTargetCount() {
                return ((LauncherEvent) this.instance).getDestTargetCount();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public Target getDestTarget(int i) {
                return ((LauncherEvent) this.instance).getDestTarget(i);
            }

            public Builder setDestTarget(int i, Target target) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setDestTarget(i, target);
                return this;
            }

            public Builder setDestTarget(int i, Target.Builder builder) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setDestTarget(i, builder);
                return this;
            }

            public Builder addDestTarget(Target target) {
                copyOnWrite();
                ((LauncherEvent) this.instance).addDestTarget(target);
                return this;
            }

            public Builder addDestTarget(int i, Target target) {
                copyOnWrite();
                ((LauncherEvent) this.instance).addDestTarget(i, target);
                return this;
            }

            public Builder addDestTarget(Target.Builder builder) {
                copyOnWrite();
                ((LauncherEvent) this.instance).addDestTarget(builder);
                return this;
            }

            public Builder addDestTarget(int i, Target.Builder builder) {
                copyOnWrite();
                ((LauncherEvent) this.instance).addDestTarget(i, builder);
                return this;
            }

            public Builder addAllDestTarget(Iterable<? extends Target> iterable) {
                copyOnWrite();
                ((LauncherEvent) this.instance).addAllDestTarget(iterable);
                return this;
            }

            public Builder clearDestTarget() {
                copyOnWrite();
                ((LauncherEvent) this.instance).clearDestTarget();
                return this;
            }

            public Builder removeDestTarget(int i) {
                copyOnWrite();
                ((LauncherEvent) this.instance).removeDestTarget(i);
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public boolean hasActionDurationMillis() {
                return ((LauncherEvent) this.instance).hasActionDurationMillis();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public long getActionDurationMillis() {
                return ((LauncherEvent) this.instance).getActionDurationMillis();
            }

            public Builder setActionDurationMillis(long j) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setActionDurationMillis(j);
                return this;
            }

            public Builder clearActionDurationMillis() {
                copyOnWrite();
                ((LauncherEvent) this.instance).clearActionDurationMillis();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public boolean hasElapsedContainerMillis() {
                return ((LauncherEvent) this.instance).hasElapsedContainerMillis();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public long getElapsedContainerMillis() {
                return ((LauncherEvent) this.instance).getElapsedContainerMillis();
            }

            public Builder setElapsedContainerMillis(long j) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setElapsedContainerMillis(j);
                return this;
            }

            public Builder clearElapsedContainerMillis() {
                copyOnWrite();
                ((LauncherEvent) this.instance).clearElapsedContainerMillis();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public boolean hasElapsedSessionMillis() {
                return ((LauncherEvent) this.instance).hasElapsedSessionMillis();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public long getElapsedSessionMillis() {
                return ((LauncherEvent) this.instance).getElapsedSessionMillis();
            }

            public Builder setElapsedSessionMillis(long j) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setElapsedSessionMillis(j);
                return this;
            }

            public Builder clearElapsedSessionMillis() {
                copyOnWrite();
                ((LauncherEvent) this.instance).clearElapsedSessionMillis();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            @Deprecated
            public boolean hasIsInMultiWindowMode() {
                return ((LauncherEvent) this.instance).hasIsInMultiWindowMode();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            @Deprecated
            public boolean getIsInMultiWindowMode() {
                return ((LauncherEvent) this.instance).getIsInMultiWindowMode();
            }

            @Deprecated
            public Builder setIsInMultiWindowMode(boolean z) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setIsInMultiWindowMode(z);
                return this;
            }

            @Deprecated
            public Builder clearIsInMultiWindowMode() {
                copyOnWrite();
                ((LauncherEvent) this.instance).clearIsInMultiWindowMode();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            @Deprecated
            public boolean hasIsInLandscapeMode() {
                return ((LauncherEvent) this.instance).hasIsInLandscapeMode();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            @Deprecated
            public boolean getIsInLandscapeMode() {
                return ((LauncherEvent) this.instance).getIsInLandscapeMode();
            }

            @Deprecated
            public Builder setIsInLandscapeMode(boolean z) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setIsInLandscapeMode(z);
                return this;
            }

            @Deprecated
            public Builder clearIsInLandscapeMode() {
                copyOnWrite();
                ((LauncherEvent) this.instance).clearIsInLandscapeMode();
                return this;
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public boolean hasExtension() {
                return ((LauncherEvent) this.instance).hasExtension();
            }

            @Override // com.android.launcher3.userevent.LauncherLogProto.LauncherEventOrBuilder
            public LauncherLogExtensions.LauncherEventExtension getExtension() {
                return ((LauncherEvent) this.instance).getExtension();
            }

            public Builder setExtension(LauncherLogExtensions.LauncherEventExtension launcherEventExtension) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setExtension(launcherEventExtension);
                return this;
            }

            public Builder setExtension(LauncherLogExtensions.LauncherEventExtension.Builder builder) {
                copyOnWrite();
                ((LauncherEvent) this.instance).setExtension(builder);
                return this;
            }

            public Builder mergeExtension(LauncherLogExtensions.LauncherEventExtension launcherEventExtension) {
                copyOnWrite();
                ((LauncherEvent) this.instance).mergeExtension(launcherEventExtension);
                return this;
            }

            public Builder clearExtension() {
                copyOnWrite();
                ((LauncherEvent) this.instance).clearExtension();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new LauncherEvent();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\t\u0000\u0001\u0001\t\t\u0000\u0002\u0001\u0001ԉ\u0000\u0002\u001b\u0003\u001b\u0004\u0002\u0001\u0005\u0002\u0002\u0006\u0002\u0003\u0007\u0007\u0004\b\u0007\u0005\t\t\u0006", new Object[]{"bitField0_", "action_", "srcTarget_", Target.class, "destTarget_", Target.class, "actionDurationMillis_", "elapsedContainerMillis_", "elapsedSessionMillis_", "isInMultiWindowMode_", "isInLandscapeMode_", "extension_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<LauncherEvent> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (LauncherEvent.class) {
                            defaultInstanceBasedParser = PARSER;
                            if (defaultInstanceBasedParser == null) {
                                defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                                PARSER = defaultInstanceBasedParser;
                            }
                            break;
                        }
                    }
                    return defaultInstanceBasedParser;
                case 6:
                    return Byte.valueOf(this.memoizedIsInitialized);
                case 7:
                    this.memoizedIsInitialized = (byte) (obj == null ? 0 : 1);
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            LauncherEvent launcherEvent = new LauncherEvent();
            DEFAULT_INSTANCE = launcherEvent;
            GeneratedMessageLite.registerDefaultInstance(LauncherEvent.class, launcherEvent);
        }

        public static LauncherEvent getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<LauncherEvent> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }
}
