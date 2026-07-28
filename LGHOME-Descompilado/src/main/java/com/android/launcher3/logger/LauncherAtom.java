package com.android.launcher3.logger;

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
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherAtom {

    public interface AllAppsContainerOrBuilder extends MessageLiteOrBuilder {
    }

    public interface ApplicationOrBuilder extends MessageLiteOrBuilder {
        String getComponentName();

        ByteString getComponentNameBytes();

        String getPackageName();

        ByteString getPackageNameBytes();

        boolean hasComponentName();

        boolean hasPackageName();
    }

    public interface ContainerInfoOrBuilder extends MessageLiteOrBuilder {
        AllAppsContainer getAllAppsContainer();

        ContainerInfo.ContainerCase getContainerCase();

        FolderContainer getFolder();

        HotseatContainer getHotseat();

        PredictedHotseatContainer getPredictedHotseatContainer();

        PredictionContainer getPredictionContainer();

        SearchResultContainer getSearchResultContainer();

        SettingsContainer getSettingsContainer();

        ShortcutsContainer getShortcutsContainer();

        TaskSwitcherContainer getTaskSwitcherContainer();

        WidgetsContainer getWidgetsContainer();

        WorkspaceContainer getWorkspace();

        boolean hasAllAppsContainer();

        boolean hasFolder();

        boolean hasHotseat();

        boolean hasPredictedHotseatContainer();

        boolean hasPredictionContainer();

        boolean hasSearchResultContainer();

        boolean hasSettingsContainer();

        boolean hasShortcutsContainer();

        boolean hasTaskSwitcherContainer();

        boolean hasWidgetsContainer();

        boolean hasWorkspace();
    }

    public interface FolderContainerOrBuilder extends MessageLiteOrBuilder {
        int getGridX();

        int getGridY();

        HotseatContainer getHotseat();

        int getPageIndex();

        FolderContainer.ParentContainerCase getParentContainerCase();

        WorkspaceContainer getWorkspace();

        boolean hasGridX();

        boolean hasGridY();

        boolean hasHotseat();

        boolean hasPageIndex();

        boolean hasWorkspace();
    }

    public interface FolderIconOrBuilder extends MessageLiteOrBuilder {
        int getCardinality();

        FromState getFromLabelState();

        String getLabelInfo();

        ByteString getLabelInfoBytes();

        ToState getToLabelState();

        boolean hasCardinality();

        boolean hasFromLabelState();

        boolean hasLabelInfo();

        boolean hasToLabelState();
    }

    public interface HotseatContainerOrBuilder extends MessageLiteOrBuilder {
        int getIndex();

        boolean hasIndex();
    }

    public interface ItemInfoOrBuilder extends MessageLiteOrBuilder {
        Application getApplication();

        Attribute getAttribute();

        ContainerInfo getContainerInfo();

        FolderIcon getFolderIcon();

        boolean getIsWork();

        ItemInfo.ItemCase getItemCase();

        int getRank();

        Shortcut getShortcut();

        Task getTask();

        Widget getWidget();

        boolean hasApplication();

        boolean hasAttribute();

        boolean hasContainerInfo();

        boolean hasFolderIcon();

        boolean hasIsWork();

        boolean hasRank();

        boolean hasShortcut();

        boolean hasTask();

        boolean hasWidget();
    }

    public interface PredictedHotseatContainerOrBuilder extends MessageLiteOrBuilder {
        int getCardinality();

        int getIndex();

        boolean hasCardinality();

        boolean hasIndex();
    }

    public interface PredictionContainerOrBuilder extends MessageLiteOrBuilder {
    }

    public interface SearchResultContainerOrBuilder extends MessageLiteOrBuilder {
        AllAppsContainer getAllAppsContainer();

        SearchResultContainer.ParentContainerCase getParentContainerCase();

        int getQueryLength();

        WorkspaceContainer getWorkspace();

        boolean hasAllAppsContainer();

        boolean hasQueryLength();

        boolean hasWorkspace();
    }

    public interface SettingsContainerOrBuilder extends MessageLiteOrBuilder {
    }

    public interface ShortcutOrBuilder extends MessageLiteOrBuilder {
        String getShortcutName();

        ByteString getShortcutNameBytes();

        boolean hasShortcutName();
    }

    public interface ShortcutsContainerOrBuilder extends MessageLiteOrBuilder {
    }

    public interface TaskOrBuilder extends MessageLiteOrBuilder {
        String getComponentName();

        ByteString getComponentNameBytes();

        int getIndex();

        String getPackageName();

        ByteString getPackageNameBytes();

        boolean hasComponentName();

        boolean hasIndex();

        boolean hasPackageName();
    }

    public interface TaskSwitcherContainerOrBuilder extends MessageLiteOrBuilder {
    }

    public interface WidgetOrBuilder extends MessageLiteOrBuilder {
        int getAppWidgetId();

        String getComponentName();

        ByteString getComponentNameBytes();

        String getPackageName();

        ByteString getPackageNameBytes();

        int getSpanX();

        int getSpanY();

        boolean hasAppWidgetId();

        boolean hasComponentName();

        boolean hasPackageName();

        boolean hasSpanX();

        boolean hasSpanY();
    }

    public interface WidgetsContainerOrBuilder extends MessageLiteOrBuilder {
    }

    public interface WorkspaceContainerOrBuilder extends MessageLiteOrBuilder {
        int getGridX();

        int getGridY();

        int getPageIndex();

        boolean hasGridX();

        boolean hasGridY();

        boolean hasPageIndex();
    }

    public static void registerAllExtensions(ExtensionRegistryLite extensionRegistryLite) {
    }

    private LauncherAtom() {
    }

    public enum Attribute implements Internal.EnumLite {
        UNKNOWN(0),
        DEFAULT_LAYOUT(1),
        BACKUP_RESTORE(2),
        PINITEM(3),
        ALLAPPS_ATOZ(4),
        WIDGETS(5),
        ADD_TO_HOMESCREEN(6),
        ALLAPPS_PREDICTION(7),
        HOTSEAT_PREDICTION(8),
        SUGGESTED_LABEL(9),
        MANUAL_LABEL(10),
        UNLABELED(11),
        EMPTY_LABEL(12);

        public static final int ADD_TO_HOMESCREEN_VALUE = 6;
        public static final int ALLAPPS_ATOZ_VALUE = 4;
        public static final int ALLAPPS_PREDICTION_VALUE = 7;
        public static final int BACKUP_RESTORE_VALUE = 2;
        public static final int DEFAULT_LAYOUT_VALUE = 1;
        public static final int EMPTY_LABEL_VALUE = 12;
        public static final int HOTSEAT_PREDICTION_VALUE = 8;
        public static final int MANUAL_LABEL_VALUE = 10;
        public static final int PINITEM_VALUE = 3;
        public static final int SUGGESTED_LABEL_VALUE = 9;
        public static final int UNKNOWN_VALUE = 0;
        public static final int UNLABELED_VALUE = 11;
        public static final int WIDGETS_VALUE = 5;
        private static final Internal.EnumLiteMap<Attribute> internalValueMap = new Internal.EnumLiteMap<Attribute>() { // from class: com.android.launcher3.logger.LauncherAtom.Attribute.1
            /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
            @Override // com.google.protobuf.Internal.EnumLiteMap
            public Attribute findValueByNumber(int i) {
                return Attribute.forNumber(i);
            }
        };
        private final int value;

        @Override // com.google.protobuf.Internal.EnumLite
        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static Attribute valueOf(int i) {
            return forNumber(i);
        }

        public static Attribute forNumber(int i) {
            switch (i) {
                case 0:
                    return UNKNOWN;
                case 1:
                    return DEFAULT_LAYOUT;
                case 2:
                    return BACKUP_RESTORE;
                case 3:
                    return PINITEM;
                case 4:
                    return ALLAPPS_ATOZ;
                case 5:
                    return WIDGETS;
                case 6:
                    return ADD_TO_HOMESCREEN;
                case 7:
                    return ALLAPPS_PREDICTION;
                case 8:
                    return HOTSEAT_PREDICTION;
                case 9:
                    return SUGGESTED_LABEL;
                case 10:
                    return MANUAL_LABEL;
                case 11:
                    return UNLABELED;
                case 12:
                    return EMPTY_LABEL;
                default:
                    return null;
            }
        }

        public static Internal.EnumLiteMap<Attribute> internalGetValueMap() {
            return internalValueMap;
        }

        public static Internal.EnumVerifier internalGetVerifier() {
            return AttributeVerifier.INSTANCE;
        }

        private static final class AttributeVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new AttributeVerifier();

            private AttributeVerifier() {
            }

            @Override // com.google.protobuf.Internal.EnumVerifier
            public boolean isInRange(int i) {
                return Attribute.forNumber(i) != null;
            }
        }

        Attribute(int i) {
            this.value = i;
        }
    }

    public enum FromState implements Internal.EnumLite {
        FROM_STATE_UNSPECIFIED(0),
        FROM_EMPTY(1),
        FROM_CUSTOM(2),
        FROM_SUGGESTED(3);

        public static final int FROM_CUSTOM_VALUE = 2;
        public static final int FROM_EMPTY_VALUE = 1;
        public static final int FROM_STATE_UNSPECIFIED_VALUE = 0;
        public static final int FROM_SUGGESTED_VALUE = 3;
        private static final Internal.EnumLiteMap<FromState> internalValueMap = new Internal.EnumLiteMap<FromState>() { // from class: com.android.launcher3.logger.LauncherAtom.FromState.1
            /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
            @Override // com.google.protobuf.Internal.EnumLiteMap
            public FromState findValueByNumber(int i) {
                return FromState.forNumber(i);
            }
        };
        private final int value;

        @Override // com.google.protobuf.Internal.EnumLite
        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static FromState valueOf(int i) {
            return forNumber(i);
        }

        public static FromState forNumber(int i) {
            if (i == 0) {
                return FROM_STATE_UNSPECIFIED;
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

        public static Internal.EnumLiteMap<FromState> internalGetValueMap() {
            return internalValueMap;
        }

        public static Internal.EnumVerifier internalGetVerifier() {
            return FromStateVerifier.INSTANCE;
        }

        private static final class FromStateVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new FromStateVerifier();

            private FromStateVerifier() {
            }

            @Override // com.google.protobuf.Internal.EnumVerifier
            public boolean isInRange(int i) {
                return FromState.forNumber(i) != null;
            }
        }

        FromState(int i) {
            this.value = i;
        }
    }

    public enum ToState implements Internal.EnumLite {
        TO_STATE_UNSPECIFIED(0),
        UNCHANGED(1),
        TO_SUGGESTION0(2),
        TO_SUGGESTION1_WITH_VALID_PRIMARY(3),
        TO_SUGGESTION1_WITH_EMPTY_PRIMARY(4),
        TO_SUGGESTION2_WITH_VALID_PRIMARY(5),
        TO_SUGGESTION2_WITH_EMPTY_PRIMARY(6),
        TO_SUGGESTION3_WITH_VALID_PRIMARY(7),
        TO_SUGGESTION3_WITH_EMPTY_PRIMARY(8),
        TO_EMPTY_WITH_VALID_PRIMARY(9),
        TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY(10),
        TO_EMPTY_WITH_EMPTY_SUGGESTIONS(11),
        TO_EMPTY_WITH_SUGGESTIONS_DISABLED(12),
        TO_CUSTOM_WITH_VALID_PRIMARY(13),
        TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY(14),
        TO_CUSTOM_WITH_EMPTY_SUGGESTIONS(15),
        TO_CUSTOM_WITH_SUGGESTIONS_DISABLED(16);

        public static final int TO_CUSTOM_WITH_EMPTY_SUGGESTIONS_VALUE = 15;
        public static final int TO_CUSTOM_WITH_SUGGESTIONS_DISABLED_VALUE = 16;
        public static final int TO_CUSTOM_WITH_VALID_PRIMARY_VALUE = 13;
        public static final int TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY_VALUE = 14;
        public static final int TO_EMPTY_WITH_EMPTY_SUGGESTIONS_VALUE = 11;
        public static final int TO_EMPTY_WITH_SUGGESTIONS_DISABLED_VALUE = 12;
        public static final int TO_EMPTY_WITH_VALID_PRIMARY_VALUE = 9;
        public static final int TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY_VALUE = 10;
        public static final int TO_STATE_UNSPECIFIED_VALUE = 0;
        public static final int TO_SUGGESTION0_VALUE = 2;
        public static final int TO_SUGGESTION1_WITH_EMPTY_PRIMARY_VALUE = 4;
        public static final int TO_SUGGESTION1_WITH_VALID_PRIMARY_VALUE = 3;
        public static final int TO_SUGGESTION2_WITH_EMPTY_PRIMARY_VALUE = 6;
        public static final int TO_SUGGESTION2_WITH_VALID_PRIMARY_VALUE = 5;
        public static final int TO_SUGGESTION3_WITH_EMPTY_PRIMARY_VALUE = 8;
        public static final int TO_SUGGESTION3_WITH_VALID_PRIMARY_VALUE = 7;
        public static final int UNCHANGED_VALUE = 1;
        private static final Internal.EnumLiteMap<ToState> internalValueMap = new Internal.EnumLiteMap<ToState>() { // from class: com.android.launcher3.logger.LauncherAtom.ToState.1
            /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
            @Override // com.google.protobuf.Internal.EnumLiteMap
            public ToState findValueByNumber(int i) {
                return ToState.forNumber(i);
            }
        };
        private final int value;

        @Override // com.google.protobuf.Internal.EnumLite
        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static ToState valueOf(int i) {
            return forNumber(i);
        }

        public static ToState forNumber(int i) {
            switch (i) {
                case 0:
                    return TO_STATE_UNSPECIFIED;
                case 1:
                    return UNCHANGED;
                case 2:
                    return TO_SUGGESTION0;
                case 3:
                    return TO_SUGGESTION1_WITH_VALID_PRIMARY;
                case 4:
                    return TO_SUGGESTION1_WITH_EMPTY_PRIMARY;
                case 5:
                    return TO_SUGGESTION2_WITH_VALID_PRIMARY;
                case 6:
                    return TO_SUGGESTION2_WITH_EMPTY_PRIMARY;
                case 7:
                    return TO_SUGGESTION3_WITH_VALID_PRIMARY;
                case 8:
                    return TO_SUGGESTION3_WITH_EMPTY_PRIMARY;
                case 9:
                    return TO_EMPTY_WITH_VALID_PRIMARY;
                case 10:
                    return TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY;
                case 11:
                    return TO_EMPTY_WITH_EMPTY_SUGGESTIONS;
                case 12:
                    return TO_EMPTY_WITH_SUGGESTIONS_DISABLED;
                case 13:
                    return TO_CUSTOM_WITH_VALID_PRIMARY;
                case 14:
                    return TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY;
                case 15:
                    return TO_CUSTOM_WITH_EMPTY_SUGGESTIONS;
                case 16:
                    return TO_CUSTOM_WITH_SUGGESTIONS_DISABLED;
                default:
                    return null;
            }
        }

        public static Internal.EnumLiteMap<ToState> internalGetValueMap() {
            return internalValueMap;
        }

        public static Internal.EnumVerifier internalGetVerifier() {
            return ToStateVerifier.INSTANCE;
        }

        private static final class ToStateVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new ToStateVerifier();

            private ToStateVerifier() {
            }

            @Override // com.google.protobuf.Internal.EnumVerifier
            public boolean isInRange(int i) {
                return ToState.forNumber(i) != null;
            }
        }

        ToState(int i) {
            this.value = i;
        }
    }

    public static final class ItemInfo extends GeneratedMessageLite<ItemInfo, Builder> implements ItemInfoOrBuilder {
        public static final int APPLICATION_FIELD_NUMBER = 1;
        public static final int ATTRIBUTE_FIELD_NUMBER = 8;
        public static final int CONTAINER_INFO_FIELD_NUMBER = 7;
        private static final ItemInfo DEFAULT_INSTANCE;
        public static final int FOLDER_ICON_FIELD_NUMBER = 9;
        public static final int IS_WORK_FIELD_NUMBER = 6;
        private static volatile Parser<ItemInfo> PARSER = null;
        public static final int RANK_FIELD_NUMBER = 5;
        public static final int SHORTCUT_FIELD_NUMBER = 3;
        public static final int TASK_FIELD_NUMBER = 2;
        public static final int WIDGET_FIELD_NUMBER = 4;
        private int attribute_;
        private int bitField0_;
        private ContainerInfo containerInfo_;
        private boolean isWork_;
        private int itemCase_ = 0;
        private Object item_;
        private int rank_;

        private ItemInfo() {
        }

        public enum ItemCase implements Internal.EnumLite {
            APPLICATION(1),
            TASK(2),
            SHORTCUT(3),
            WIDGET(4),
            FOLDER_ICON(9),
            ITEM_NOT_SET(0);

            private final int value;

            ItemCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ItemCase valueOf(int i) {
                return forNumber(i);
            }

            public static ItemCase forNumber(int i) {
                if (i == 0) {
                    return ITEM_NOT_SET;
                }
                if (i == 1) {
                    return APPLICATION;
                }
                if (i == 2) {
                    return TASK;
                }
                if (i == 3) {
                    return SHORTCUT;
                }
                if (i == 4) {
                    return WIDGET;
                }
                if (i != 9) {
                    return null;
                }
                return FOLDER_ICON;
            }

            @Override // com.google.protobuf.Internal.EnumLite
            public int getNumber() {
                return this.value;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public ItemCase getItemCase() {
            return ItemCase.forNumber(this.itemCase_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearItem() {
            this.itemCase_ = 0;
            this.item_ = null;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public boolean hasApplication() {
            return this.itemCase_ == 1;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public Application getApplication() {
            if (this.itemCase_ == 1) {
                return (Application) this.item_;
            }
            return Application.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setApplication(Application application) {
            Objects.requireNonNull(application);
            this.item_ = application;
            this.itemCase_ = 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setApplication(Application.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeApplication(Application application) {
            Objects.requireNonNull(application);
            if (this.itemCase_ == 1 && this.item_ != Application.getDefaultInstance()) {
                this.item_ = Application.newBuilder((Application) this.item_).mergeFrom(application).buildPartial();
            } else {
                this.item_ = application;
            }
            this.itemCase_ = 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearApplication() {
            if (this.itemCase_ == 1) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public boolean hasTask() {
            return this.itemCase_ == 2;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public Task getTask() {
            if (this.itemCase_ == 2) {
                return (Task) this.item_;
            }
            return Task.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setTask(Task task) {
            Objects.requireNonNull(task);
            this.item_ = task;
            this.itemCase_ = 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setTask(Task.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeTask(Task task) {
            Objects.requireNonNull(task);
            if (this.itemCase_ == 2 && this.item_ != Task.getDefaultInstance()) {
                this.item_ = Task.newBuilder((Task) this.item_).mergeFrom(task).buildPartial();
            } else {
                this.item_ = task;
            }
            this.itemCase_ = 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearTask() {
            if (this.itemCase_ == 2) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public boolean hasShortcut() {
            return this.itemCase_ == 3;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public Shortcut getShortcut() {
            if (this.itemCase_ == 3) {
                return (Shortcut) this.item_;
            }
            return Shortcut.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setShortcut(Shortcut shortcut) {
            Objects.requireNonNull(shortcut);
            this.item_ = shortcut;
            this.itemCase_ = 3;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setShortcut(Shortcut.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 3;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeShortcut(Shortcut shortcut) {
            Objects.requireNonNull(shortcut);
            if (this.itemCase_ == 3 && this.item_ != Shortcut.getDefaultInstance()) {
                this.item_ = Shortcut.newBuilder((Shortcut) this.item_).mergeFrom(shortcut).buildPartial();
            } else {
                this.item_ = shortcut;
            }
            this.itemCase_ = 3;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearShortcut() {
            if (this.itemCase_ == 3) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public boolean hasWidget() {
            return this.itemCase_ == 4;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public Widget getWidget() {
            if (this.itemCase_ == 4) {
                return (Widget) this.item_;
            }
            return Widget.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWidget(Widget widget) {
            Objects.requireNonNull(widget);
            this.item_ = widget;
            this.itemCase_ = 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWidget(Widget.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeWidget(Widget widget) {
            Objects.requireNonNull(widget);
            if (this.itemCase_ == 4 && this.item_ != Widget.getDefaultInstance()) {
                this.item_ = Widget.newBuilder((Widget) this.item_).mergeFrom(widget).buildPartial();
            } else {
                this.item_ = widget;
            }
            this.itemCase_ = 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearWidget() {
            if (this.itemCase_ == 4) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public boolean hasFolderIcon() {
            return this.itemCase_ == 9;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public FolderIcon getFolderIcon() {
            if (this.itemCase_ == 9) {
                return (FolderIcon) this.item_;
            }
            return FolderIcon.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFolderIcon(FolderIcon folderIcon) {
            Objects.requireNonNull(folderIcon);
            this.item_ = folderIcon;
            this.itemCase_ = 9;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFolderIcon(FolderIcon.Builder builder) {
            this.item_ = builder.build();
            this.itemCase_ = 9;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeFolderIcon(FolderIcon folderIcon) {
            Objects.requireNonNull(folderIcon);
            if (this.itemCase_ == 9 && this.item_ != FolderIcon.getDefaultInstance()) {
                this.item_ = FolderIcon.newBuilder((FolderIcon) this.item_).mergeFrom(folderIcon).buildPartial();
            } else {
                this.item_ = folderIcon;
            }
            this.itemCase_ = 9;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearFolderIcon() {
            if (this.itemCase_ == 9) {
                this.itemCase_ = 0;
                this.item_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public boolean hasRank() {
            return (this.bitField0_ & 32) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public int getRank() {
            return this.rank_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setRank(int i) {
            this.bitField0_ |= 32;
            this.rank_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearRank() {
            this.bitField0_ &= -33;
            this.rank_ = 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public boolean hasIsWork() {
            return (this.bitField0_ & 64) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public boolean getIsWork() {
            return this.isWork_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIsWork(boolean z) {
            this.bitField0_ |= 64;
            this.isWork_ = z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIsWork() {
            this.bitField0_ &= -65;
            this.isWork_ = false;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public boolean hasContainerInfo() {
            return (this.bitField0_ & 128) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public ContainerInfo getContainerInfo() {
            ContainerInfo containerInfo = this.containerInfo_;
            return containerInfo == null ? ContainerInfo.getDefaultInstance() : containerInfo;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setContainerInfo(ContainerInfo containerInfo) {
            Objects.requireNonNull(containerInfo);
            this.containerInfo_ = containerInfo;
            this.bitField0_ |= 128;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setContainerInfo(ContainerInfo.Builder builder) {
            this.containerInfo_ = builder.build();
            this.bitField0_ |= 128;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeContainerInfo(ContainerInfo containerInfo) {
            Objects.requireNonNull(containerInfo);
            ContainerInfo containerInfo2 = this.containerInfo_;
            if (containerInfo2 != null && containerInfo2 != ContainerInfo.getDefaultInstance()) {
                this.containerInfo_ = ContainerInfo.newBuilder(this.containerInfo_).mergeFrom(containerInfo).buildPartial();
            } else {
                this.containerInfo_ = containerInfo;
            }
            this.bitField0_ |= 128;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearContainerInfo() {
            this.containerInfo_ = null;
            this.bitField0_ &= -129;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public boolean hasAttribute() {
            return (this.bitField0_ & 256) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
        public Attribute getAttribute() {
            Attribute attributeForNumber = Attribute.forNumber(this.attribute_);
            return attributeForNumber == null ? Attribute.UNKNOWN : attributeForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAttribute(Attribute attribute) {
            Objects.requireNonNull(attribute);
            this.bitField0_ |= 256;
            this.attribute_ = attribute.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearAttribute() {
            this.bitField0_ &= -257;
            this.attribute_ = 0;
        }

        public static ItemInfo parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static ItemInfo parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static ItemInfo parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static ItemInfo parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static ItemInfo parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static ItemInfo parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static ItemInfo parseFrom(InputStream inputStream) throws IOException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ItemInfo parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ItemInfo parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (ItemInfo) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ItemInfo parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ItemInfo) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ItemInfo parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static ItemInfo parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ItemInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(ItemInfo itemInfo) {
            return DEFAULT_INSTANCE.createBuilder(itemInfo);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<ItemInfo, Builder> implements ItemInfoOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:1609) call: com.android.launcher3.logger.LauncherAtom.ItemInfo.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(ItemInfo.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public ItemCase getItemCase() {
                return ((ItemInfo) this.instance).getItemCase();
            }

            public Builder clearItem() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearItem();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public boolean hasApplication() {
                return ((ItemInfo) this.instance).hasApplication();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public Application getApplication() {
                return ((ItemInfo) this.instance).getApplication();
            }

            public Builder setApplication(Application application) {
                copyOnWrite();
                ((ItemInfo) this.instance).setApplication(application);
                return this;
            }

            public Builder setApplication(Application.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setApplication(builder);
                return this;
            }

            public Builder mergeApplication(Application application) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeApplication(application);
                return this;
            }

            public Builder clearApplication() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearApplication();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public boolean hasTask() {
                return ((ItemInfo) this.instance).hasTask();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public Task getTask() {
                return ((ItemInfo) this.instance).getTask();
            }

            public Builder setTask(Task task) {
                copyOnWrite();
                ((ItemInfo) this.instance).setTask(task);
                return this;
            }

            public Builder setTask(Task.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setTask(builder);
                return this;
            }

            public Builder mergeTask(Task task) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeTask(task);
                return this;
            }

            public Builder clearTask() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearTask();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public boolean hasShortcut() {
                return ((ItemInfo) this.instance).hasShortcut();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public Shortcut getShortcut() {
                return ((ItemInfo) this.instance).getShortcut();
            }

            public Builder setShortcut(Shortcut shortcut) {
                copyOnWrite();
                ((ItemInfo) this.instance).setShortcut(shortcut);
                return this;
            }

            public Builder setShortcut(Shortcut.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setShortcut(builder);
                return this;
            }

            public Builder mergeShortcut(Shortcut shortcut) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeShortcut(shortcut);
                return this;
            }

            public Builder clearShortcut() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearShortcut();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public boolean hasWidget() {
                return ((ItemInfo) this.instance).hasWidget();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public Widget getWidget() {
                return ((ItemInfo) this.instance).getWidget();
            }

            public Builder setWidget(Widget widget) {
                copyOnWrite();
                ((ItemInfo) this.instance).setWidget(widget);
                return this;
            }

            public Builder setWidget(Widget.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setWidget(builder);
                return this;
            }

            public Builder mergeWidget(Widget widget) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeWidget(widget);
                return this;
            }

            public Builder clearWidget() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearWidget();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public boolean hasFolderIcon() {
                return ((ItemInfo) this.instance).hasFolderIcon();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public FolderIcon getFolderIcon() {
                return ((ItemInfo) this.instance).getFolderIcon();
            }

            public Builder setFolderIcon(FolderIcon folderIcon) {
                copyOnWrite();
                ((ItemInfo) this.instance).setFolderIcon(folderIcon);
                return this;
            }

            public Builder setFolderIcon(FolderIcon.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setFolderIcon(builder);
                return this;
            }

            public Builder mergeFolderIcon(FolderIcon folderIcon) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeFolderIcon(folderIcon);
                return this;
            }

            public Builder clearFolderIcon() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearFolderIcon();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public boolean hasRank() {
                return ((ItemInfo) this.instance).hasRank();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public int getRank() {
                return ((ItemInfo) this.instance).getRank();
            }

            public Builder setRank(int i) {
                copyOnWrite();
                ((ItemInfo) this.instance).setRank(i);
                return this;
            }

            public Builder clearRank() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearRank();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public boolean hasIsWork() {
                return ((ItemInfo) this.instance).hasIsWork();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public boolean getIsWork() {
                return ((ItemInfo) this.instance).getIsWork();
            }

            public Builder setIsWork(boolean z) {
                copyOnWrite();
                ((ItemInfo) this.instance).setIsWork(z);
                return this;
            }

            public Builder clearIsWork() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearIsWork();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public boolean hasContainerInfo() {
                return ((ItemInfo) this.instance).hasContainerInfo();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public ContainerInfo getContainerInfo() {
                return ((ItemInfo) this.instance).getContainerInfo();
            }

            public Builder setContainerInfo(ContainerInfo containerInfo) {
                copyOnWrite();
                ((ItemInfo) this.instance).setContainerInfo(containerInfo);
                return this;
            }

            public Builder setContainerInfo(ContainerInfo.Builder builder) {
                copyOnWrite();
                ((ItemInfo) this.instance).setContainerInfo(builder);
                return this;
            }

            public Builder mergeContainerInfo(ContainerInfo containerInfo) {
                copyOnWrite();
                ((ItemInfo) this.instance).mergeContainerInfo(containerInfo);
                return this;
            }

            public Builder clearContainerInfo() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearContainerInfo();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public boolean hasAttribute() {
                return ((ItemInfo) this.instance).hasAttribute();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ItemInfoOrBuilder
            public Attribute getAttribute() {
                return ((ItemInfo) this.instance).getAttribute();
            }

            public Builder setAttribute(Attribute attribute) {
                copyOnWrite();
                ((ItemInfo) this.instance).setAttribute(attribute);
                return this;
            }

            public Builder clearAttribute() {
                copyOnWrite();
                ((ItemInfo) this.instance).clearAttribute();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new ItemInfo();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\t\u0001\u0001\u0001\t\t\u0000\u0000\u0000\u0001<\u0000\u0002<\u0000\u0003<\u0000\u0004<\u0000\u0005\u0004\u0005\u0006\u0007\u0006\u0007\t\u0007\b\f\b\t<\u0000", new Object[]{"item_", "itemCase_", "bitField0_", Application.class, Task.class, Shortcut.class, Widget.class, "rank_", "isWork_", "containerInfo_", "attribute_", Attribute.internalGetVerifier(), FolderIcon.class});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<ItemInfo> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (ItemInfo.class) {
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
            ItemInfo itemInfo = new ItemInfo();
            DEFAULT_INSTANCE = itemInfo;
            GeneratedMessageLite.registerDefaultInstance(ItemInfo.class, itemInfo);
        }

        public static ItemInfo getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ItemInfo> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* JADX INFO: renamed from: com.android.launcher3.logger.LauncherAtom$1, reason: invalid class name */
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

    public static final class ContainerInfo extends GeneratedMessageLite<ContainerInfo, Builder> implements ContainerInfoOrBuilder {
        public static final int ALL_APPS_CONTAINER_FIELD_NUMBER = 4;
        private static final ContainerInfo DEFAULT_INSTANCE;
        public static final int FOLDER_FIELD_NUMBER = 3;
        public static final int HOTSEAT_FIELD_NUMBER = 2;
        private static volatile Parser<ContainerInfo> PARSER = null;
        public static final int PREDICTED_HOTSEAT_CONTAINER_FIELD_NUMBER = 10;
        public static final int PREDICTION_CONTAINER_FIELD_NUMBER = 6;
        public static final int SEARCH_RESULT_CONTAINER_FIELD_NUMBER = 7;
        public static final int SETTINGS_CONTAINER_FIELD_NUMBER = 9;
        public static final int SHORTCUTS_CONTAINER_FIELD_NUMBER = 8;
        public static final int TASK_SWITCHER_CONTAINER_FIELD_NUMBER = 11;
        public static final int WIDGETS_CONTAINER_FIELD_NUMBER = 5;
        public static final int WORKSPACE_FIELD_NUMBER = 1;
        private int bitField0_;
        private int containerCase_ = 0;
        private Object container_;

        private ContainerInfo() {
        }

        public enum ContainerCase implements Internal.EnumLite {
            WORKSPACE(1),
            HOTSEAT(2),
            FOLDER(3),
            ALL_APPS_CONTAINER(4),
            WIDGETS_CONTAINER(5),
            PREDICTION_CONTAINER(6),
            SEARCH_RESULT_CONTAINER(7),
            SHORTCUTS_CONTAINER(8),
            SETTINGS_CONTAINER(9),
            PREDICTED_HOTSEAT_CONTAINER(10),
            TASK_SWITCHER_CONTAINER(11),
            CONTAINER_NOT_SET(0);

            private final int value;

            ContainerCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ContainerCase valueOf(int i) {
                return forNumber(i);
            }

            public static ContainerCase forNumber(int i) {
                switch (i) {
                    case 0:
                        return CONTAINER_NOT_SET;
                    case 1:
                        return WORKSPACE;
                    case 2:
                        return HOTSEAT;
                    case 3:
                        return FOLDER;
                    case 4:
                        return ALL_APPS_CONTAINER;
                    case 5:
                        return WIDGETS_CONTAINER;
                    case 6:
                        return PREDICTION_CONTAINER;
                    case 7:
                        return SEARCH_RESULT_CONTAINER;
                    case 8:
                        return SHORTCUTS_CONTAINER;
                    case 9:
                        return SETTINGS_CONTAINER;
                    case 10:
                        return PREDICTED_HOTSEAT_CONTAINER;
                    case 11:
                        return TASK_SWITCHER_CONTAINER;
                    default:
                        return null;
                }
            }

            @Override // com.google.protobuf.Internal.EnumLite
            public int getNumber() {
                return this.value;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public ContainerCase getContainerCase() {
            return ContainerCase.forNumber(this.containerCase_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearContainer() {
            this.containerCase_ = 0;
            this.container_ = null;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasWorkspace() {
            return this.containerCase_ == 1;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public WorkspaceContainer getWorkspace() {
            if (this.containerCase_ == 1) {
                return (WorkspaceContainer) this.container_;
            }
            return WorkspaceContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer workspaceContainer) {
            Objects.requireNonNull(workspaceContainer);
            this.container_ = workspaceContainer;
            this.containerCase_ = 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeWorkspace(WorkspaceContainer workspaceContainer) {
            Objects.requireNonNull(workspaceContainer);
            if (this.containerCase_ == 1 && this.container_ != WorkspaceContainer.getDefaultInstance()) {
                this.container_ = WorkspaceContainer.newBuilder((WorkspaceContainer) this.container_).mergeFrom(workspaceContainer).buildPartial();
            } else {
                this.container_ = workspaceContainer;
            }
            this.containerCase_ = 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearWorkspace() {
            if (this.containerCase_ == 1) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasHotseat() {
            return this.containerCase_ == 2;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public HotseatContainer getHotseat() {
            if (this.containerCase_ == 2) {
                return (HotseatContainer) this.container_;
            }
            return HotseatContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setHotseat(HotseatContainer hotseatContainer) {
            Objects.requireNonNull(hotseatContainer);
            this.container_ = hotseatContainer;
            this.containerCase_ = 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setHotseat(HotseatContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeHotseat(HotseatContainer hotseatContainer) {
            Objects.requireNonNull(hotseatContainer);
            if (this.containerCase_ == 2 && this.container_ != HotseatContainer.getDefaultInstance()) {
                this.container_ = HotseatContainer.newBuilder((HotseatContainer) this.container_).mergeFrom(hotseatContainer).buildPartial();
            } else {
                this.container_ = hotseatContainer;
            }
            this.containerCase_ = 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearHotseat() {
            if (this.containerCase_ == 2) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasFolder() {
            return this.containerCase_ == 3;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public FolderContainer getFolder() {
            if (this.containerCase_ == 3) {
                return (FolderContainer) this.container_;
            }
            return FolderContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFolder(FolderContainer folderContainer) {
            Objects.requireNonNull(folderContainer);
            this.container_ = folderContainer;
            this.containerCase_ = 3;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFolder(FolderContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 3;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeFolder(FolderContainer folderContainer) {
            Objects.requireNonNull(folderContainer);
            if (this.containerCase_ == 3 && this.container_ != FolderContainer.getDefaultInstance()) {
                this.container_ = FolderContainer.newBuilder((FolderContainer) this.container_).mergeFrom(folderContainer).buildPartial();
            } else {
                this.container_ = folderContainer;
            }
            this.containerCase_ = 3;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearFolder() {
            if (this.containerCase_ == 3) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasAllAppsContainer() {
            return this.containerCase_ == 4;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public AllAppsContainer getAllAppsContainer() {
            if (this.containerCase_ == 4) {
                return (AllAppsContainer) this.container_;
            }
            return AllAppsContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAllAppsContainer(AllAppsContainer allAppsContainer) {
            Objects.requireNonNull(allAppsContainer);
            this.container_ = allAppsContainer;
            this.containerCase_ = 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAllAppsContainer(AllAppsContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeAllAppsContainer(AllAppsContainer allAppsContainer) {
            Objects.requireNonNull(allAppsContainer);
            if (this.containerCase_ == 4 && this.container_ != AllAppsContainer.getDefaultInstance()) {
                this.container_ = AllAppsContainer.newBuilder((AllAppsContainer) this.container_).mergeFrom(allAppsContainer).buildPartial();
            } else {
                this.container_ = allAppsContainer;
            }
            this.containerCase_ = 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearAllAppsContainer() {
            if (this.containerCase_ == 4) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasWidgetsContainer() {
            return this.containerCase_ == 5;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public WidgetsContainer getWidgetsContainer() {
            if (this.containerCase_ == 5) {
                return (WidgetsContainer) this.container_;
            }
            return WidgetsContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWidgetsContainer(WidgetsContainer widgetsContainer) {
            Objects.requireNonNull(widgetsContainer);
            this.container_ = widgetsContainer;
            this.containerCase_ = 5;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWidgetsContainer(WidgetsContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 5;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeWidgetsContainer(WidgetsContainer widgetsContainer) {
            Objects.requireNonNull(widgetsContainer);
            if (this.containerCase_ == 5 && this.container_ != WidgetsContainer.getDefaultInstance()) {
                this.container_ = WidgetsContainer.newBuilder((WidgetsContainer) this.container_).mergeFrom(widgetsContainer).buildPartial();
            } else {
                this.container_ = widgetsContainer;
            }
            this.containerCase_ = 5;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearWidgetsContainer() {
            if (this.containerCase_ == 5) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasPredictionContainer() {
            return this.containerCase_ == 6;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public PredictionContainer getPredictionContainer() {
            if (this.containerCase_ == 6) {
                return (PredictionContainer) this.container_;
            }
            return PredictionContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPredictionContainer(PredictionContainer predictionContainer) {
            Objects.requireNonNull(predictionContainer);
            this.container_ = predictionContainer;
            this.containerCase_ = 6;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPredictionContainer(PredictionContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 6;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergePredictionContainer(PredictionContainer predictionContainer) {
            Objects.requireNonNull(predictionContainer);
            if (this.containerCase_ == 6 && this.container_ != PredictionContainer.getDefaultInstance()) {
                this.container_ = PredictionContainer.newBuilder((PredictionContainer) this.container_).mergeFrom(predictionContainer).buildPartial();
            } else {
                this.container_ = predictionContainer;
            }
            this.containerCase_ = 6;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPredictionContainer() {
            if (this.containerCase_ == 6) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasSearchResultContainer() {
            return this.containerCase_ == 7;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public SearchResultContainer getSearchResultContainer() {
            if (this.containerCase_ == 7) {
                return (SearchResultContainer) this.container_;
            }
            return SearchResultContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSearchResultContainer(SearchResultContainer searchResultContainer) {
            Objects.requireNonNull(searchResultContainer);
            this.container_ = searchResultContainer;
            this.containerCase_ = 7;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSearchResultContainer(SearchResultContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 7;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeSearchResultContainer(SearchResultContainer searchResultContainer) {
            Objects.requireNonNull(searchResultContainer);
            if (this.containerCase_ == 7 && this.container_ != SearchResultContainer.getDefaultInstance()) {
                this.container_ = SearchResultContainer.newBuilder((SearchResultContainer) this.container_).mergeFrom(searchResultContainer).buildPartial();
            } else {
                this.container_ = searchResultContainer;
            }
            this.containerCase_ = 7;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSearchResultContainer() {
            if (this.containerCase_ == 7) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasShortcutsContainer() {
            return this.containerCase_ == 8;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public ShortcutsContainer getShortcutsContainer() {
            if (this.containerCase_ == 8) {
                return (ShortcutsContainer) this.container_;
            }
            return ShortcutsContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setShortcutsContainer(ShortcutsContainer shortcutsContainer) {
            Objects.requireNonNull(shortcutsContainer);
            this.container_ = shortcutsContainer;
            this.containerCase_ = 8;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setShortcutsContainer(ShortcutsContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 8;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeShortcutsContainer(ShortcutsContainer shortcutsContainer) {
            Objects.requireNonNull(shortcutsContainer);
            if (this.containerCase_ == 8 && this.container_ != ShortcutsContainer.getDefaultInstance()) {
                this.container_ = ShortcutsContainer.newBuilder((ShortcutsContainer) this.container_).mergeFrom(shortcutsContainer).buildPartial();
            } else {
                this.container_ = shortcutsContainer;
            }
            this.containerCase_ = 8;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearShortcutsContainer() {
            if (this.containerCase_ == 8) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasSettingsContainer() {
            return this.containerCase_ == 9;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public SettingsContainer getSettingsContainer() {
            if (this.containerCase_ == 9) {
                return (SettingsContainer) this.container_;
            }
            return SettingsContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSettingsContainer(SettingsContainer settingsContainer) {
            Objects.requireNonNull(settingsContainer);
            this.container_ = settingsContainer;
            this.containerCase_ = 9;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSettingsContainer(SettingsContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 9;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeSettingsContainer(SettingsContainer settingsContainer) {
            Objects.requireNonNull(settingsContainer);
            if (this.containerCase_ == 9 && this.container_ != SettingsContainer.getDefaultInstance()) {
                this.container_ = SettingsContainer.newBuilder((SettingsContainer) this.container_).mergeFrom(settingsContainer).buildPartial();
            } else {
                this.container_ = settingsContainer;
            }
            this.containerCase_ = 9;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSettingsContainer() {
            if (this.containerCase_ == 9) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasPredictedHotseatContainer() {
            return this.containerCase_ == 10;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public PredictedHotseatContainer getPredictedHotseatContainer() {
            if (this.containerCase_ == 10) {
                return (PredictedHotseatContainer) this.container_;
            }
            return PredictedHotseatContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPredictedHotseatContainer(PredictedHotseatContainer predictedHotseatContainer) {
            Objects.requireNonNull(predictedHotseatContainer);
            this.container_ = predictedHotseatContainer;
            this.containerCase_ = 10;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPredictedHotseatContainer(PredictedHotseatContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 10;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergePredictedHotseatContainer(PredictedHotseatContainer predictedHotseatContainer) {
            Objects.requireNonNull(predictedHotseatContainer);
            if (this.containerCase_ == 10 && this.container_ != PredictedHotseatContainer.getDefaultInstance()) {
                this.container_ = PredictedHotseatContainer.newBuilder((PredictedHotseatContainer) this.container_).mergeFrom(predictedHotseatContainer).buildPartial();
            } else {
                this.container_ = predictedHotseatContainer;
            }
            this.containerCase_ = 10;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPredictedHotseatContainer() {
            if (this.containerCase_ == 10) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public boolean hasTaskSwitcherContainer() {
            return this.containerCase_ == 11;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
        public TaskSwitcherContainer getTaskSwitcherContainer() {
            if (this.containerCase_ == 11) {
                return (TaskSwitcherContainer) this.container_;
            }
            return TaskSwitcherContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setTaskSwitcherContainer(TaskSwitcherContainer taskSwitcherContainer) {
            Objects.requireNonNull(taskSwitcherContainer);
            this.container_ = taskSwitcherContainer;
            this.containerCase_ = 11;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setTaskSwitcherContainer(TaskSwitcherContainer.Builder builder) {
            this.container_ = builder.build();
            this.containerCase_ = 11;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeTaskSwitcherContainer(TaskSwitcherContainer taskSwitcherContainer) {
            Objects.requireNonNull(taskSwitcherContainer);
            if (this.containerCase_ == 11 && this.container_ != TaskSwitcherContainer.getDefaultInstance()) {
                this.container_ = TaskSwitcherContainer.newBuilder((TaskSwitcherContainer) this.container_).mergeFrom(taskSwitcherContainer).buildPartial();
            } else {
                this.container_ = taskSwitcherContainer;
            }
            this.containerCase_ = 11;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearTaskSwitcherContainer() {
            if (this.containerCase_ == 11) {
                this.containerCase_ = 0;
                this.container_ = null;
            }
        }

        public static ContainerInfo parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static ContainerInfo parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static ContainerInfo parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static ContainerInfo parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static ContainerInfo parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static ContainerInfo parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static ContainerInfo parseFrom(InputStream inputStream) throws IOException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ContainerInfo parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ContainerInfo parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (ContainerInfo) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ContainerInfo parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ContainerInfo) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ContainerInfo parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static ContainerInfo parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ContainerInfo) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(ContainerInfo containerInfo) {
            return DEFAULT_INSTANCE.createBuilder(containerInfo);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<ContainerInfo, Builder> implements ContainerInfoOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:3125) call: com.android.launcher3.logger.LauncherAtom.ContainerInfo.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(ContainerInfo.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public ContainerCase getContainerCase() {
                return ((ContainerInfo) this.instance).getContainerCase();
            }

            public Builder clearContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearContainer();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasWorkspace() {
                return ((ContainerInfo) this.instance).hasWorkspace();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public WorkspaceContainer getWorkspace() {
                return ((ContainerInfo) this.instance).getWorkspace();
            }

            public Builder setWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setWorkspace(workspaceContainer);
                return this;
            }

            public Builder setWorkspace(WorkspaceContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setWorkspace(builder);
                return this;
            }

            public Builder mergeWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeWorkspace(workspaceContainer);
                return this;
            }

            public Builder clearWorkspace() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearWorkspace();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasHotseat() {
                return ((ContainerInfo) this.instance).hasHotseat();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public HotseatContainer getHotseat() {
                return ((ContainerInfo) this.instance).getHotseat();
            }

            public Builder setHotseat(HotseatContainer hotseatContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setHotseat(hotseatContainer);
                return this;
            }

            public Builder setHotseat(HotseatContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setHotseat(builder);
                return this;
            }

            public Builder mergeHotseat(HotseatContainer hotseatContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeHotseat(hotseatContainer);
                return this;
            }

            public Builder clearHotseat() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearHotseat();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasFolder() {
                return ((ContainerInfo) this.instance).hasFolder();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public FolderContainer getFolder() {
                return ((ContainerInfo) this.instance).getFolder();
            }

            public Builder setFolder(FolderContainer folderContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setFolder(folderContainer);
                return this;
            }

            public Builder setFolder(FolderContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setFolder(builder);
                return this;
            }

            public Builder mergeFolder(FolderContainer folderContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeFolder(folderContainer);
                return this;
            }

            public Builder clearFolder() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearFolder();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasAllAppsContainer() {
                return ((ContainerInfo) this.instance).hasAllAppsContainer();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public AllAppsContainer getAllAppsContainer() {
                return ((ContainerInfo) this.instance).getAllAppsContainer();
            }

            public Builder setAllAppsContainer(AllAppsContainer allAppsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setAllAppsContainer(allAppsContainer);
                return this;
            }

            public Builder setAllAppsContainer(AllAppsContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setAllAppsContainer(builder);
                return this;
            }

            public Builder mergeAllAppsContainer(AllAppsContainer allAppsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeAllAppsContainer(allAppsContainer);
                return this;
            }

            public Builder clearAllAppsContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearAllAppsContainer();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasWidgetsContainer() {
                return ((ContainerInfo) this.instance).hasWidgetsContainer();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public WidgetsContainer getWidgetsContainer() {
                return ((ContainerInfo) this.instance).getWidgetsContainer();
            }

            public Builder setWidgetsContainer(WidgetsContainer widgetsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setWidgetsContainer(widgetsContainer);
                return this;
            }

            public Builder setWidgetsContainer(WidgetsContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setWidgetsContainer(builder);
                return this;
            }

            public Builder mergeWidgetsContainer(WidgetsContainer widgetsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeWidgetsContainer(widgetsContainer);
                return this;
            }

            public Builder clearWidgetsContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearWidgetsContainer();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasPredictionContainer() {
                return ((ContainerInfo) this.instance).hasPredictionContainer();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public PredictionContainer getPredictionContainer() {
                return ((ContainerInfo) this.instance).getPredictionContainer();
            }

            public Builder setPredictionContainer(PredictionContainer predictionContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setPredictionContainer(predictionContainer);
                return this;
            }

            public Builder setPredictionContainer(PredictionContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setPredictionContainer(builder);
                return this;
            }

            public Builder mergePredictionContainer(PredictionContainer predictionContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergePredictionContainer(predictionContainer);
                return this;
            }

            public Builder clearPredictionContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearPredictionContainer();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasSearchResultContainer() {
                return ((ContainerInfo) this.instance).hasSearchResultContainer();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public SearchResultContainer getSearchResultContainer() {
                return ((ContainerInfo) this.instance).getSearchResultContainer();
            }

            public Builder setSearchResultContainer(SearchResultContainer searchResultContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setSearchResultContainer(searchResultContainer);
                return this;
            }

            public Builder setSearchResultContainer(SearchResultContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setSearchResultContainer(builder);
                return this;
            }

            public Builder mergeSearchResultContainer(SearchResultContainer searchResultContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeSearchResultContainer(searchResultContainer);
                return this;
            }

            public Builder clearSearchResultContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearSearchResultContainer();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasShortcutsContainer() {
                return ((ContainerInfo) this.instance).hasShortcutsContainer();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public ShortcutsContainer getShortcutsContainer() {
                return ((ContainerInfo) this.instance).getShortcutsContainer();
            }

            public Builder setShortcutsContainer(ShortcutsContainer shortcutsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setShortcutsContainer(shortcutsContainer);
                return this;
            }

            public Builder setShortcutsContainer(ShortcutsContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setShortcutsContainer(builder);
                return this;
            }

            public Builder mergeShortcutsContainer(ShortcutsContainer shortcutsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeShortcutsContainer(shortcutsContainer);
                return this;
            }

            public Builder clearShortcutsContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearShortcutsContainer();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasSettingsContainer() {
                return ((ContainerInfo) this.instance).hasSettingsContainer();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public SettingsContainer getSettingsContainer() {
                return ((ContainerInfo) this.instance).getSettingsContainer();
            }

            public Builder setSettingsContainer(SettingsContainer settingsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setSettingsContainer(settingsContainer);
                return this;
            }

            public Builder setSettingsContainer(SettingsContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setSettingsContainer(builder);
                return this;
            }

            public Builder mergeSettingsContainer(SettingsContainer settingsContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeSettingsContainer(settingsContainer);
                return this;
            }

            public Builder clearSettingsContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearSettingsContainer();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasPredictedHotseatContainer() {
                return ((ContainerInfo) this.instance).hasPredictedHotseatContainer();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public PredictedHotseatContainer getPredictedHotseatContainer() {
                return ((ContainerInfo) this.instance).getPredictedHotseatContainer();
            }

            public Builder setPredictedHotseatContainer(PredictedHotseatContainer predictedHotseatContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setPredictedHotseatContainer(predictedHotseatContainer);
                return this;
            }

            public Builder setPredictedHotseatContainer(PredictedHotseatContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setPredictedHotseatContainer(builder);
                return this;
            }

            public Builder mergePredictedHotseatContainer(PredictedHotseatContainer predictedHotseatContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergePredictedHotseatContainer(predictedHotseatContainer);
                return this;
            }

            public Builder clearPredictedHotseatContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearPredictedHotseatContainer();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public boolean hasTaskSwitcherContainer() {
                return ((ContainerInfo) this.instance).hasTaskSwitcherContainer();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ContainerInfoOrBuilder
            public TaskSwitcherContainer getTaskSwitcherContainer() {
                return ((ContainerInfo) this.instance).getTaskSwitcherContainer();
            }

            public Builder setTaskSwitcherContainer(TaskSwitcherContainer taskSwitcherContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setTaskSwitcherContainer(taskSwitcherContainer);
                return this;
            }

            public Builder setTaskSwitcherContainer(TaskSwitcherContainer.Builder builder) {
                copyOnWrite();
                ((ContainerInfo) this.instance).setTaskSwitcherContainer(builder);
                return this;
            }

            public Builder mergeTaskSwitcherContainer(TaskSwitcherContainer taskSwitcherContainer) {
                copyOnWrite();
                ((ContainerInfo) this.instance).mergeTaskSwitcherContainer(taskSwitcherContainer);
                return this;
            }

            public Builder clearTaskSwitcherContainer() {
                copyOnWrite();
                ((ContainerInfo) this.instance).clearTaskSwitcherContainer();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new ContainerInfo();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u000b\u0001\u0001\u0001\u000b\u000b\u0000\u0000\u0000\u0001<\u0000\u0002<\u0000\u0003<\u0000\u0004<\u0000\u0005<\u0000\u0006<\u0000\u0007<\u0000\b<\u0000\t<\u0000\n<\u0000\u000b<\u0000", new Object[]{"container_", "containerCase_", "bitField0_", WorkspaceContainer.class, HotseatContainer.class, FolderContainer.class, AllAppsContainer.class, WidgetsContainer.class, PredictionContainer.class, SearchResultContainer.class, ShortcutsContainer.class, SettingsContainer.class, PredictedHotseatContainer.class, TaskSwitcherContainer.class});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<ContainerInfo> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (ContainerInfo.class) {
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
            ContainerInfo containerInfo = new ContainerInfo();
            DEFAULT_INSTANCE = containerInfo;
            GeneratedMessageLite.registerDefaultInstance(ContainerInfo.class, containerInfo);
        }

        public static ContainerInfo getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ContainerInfo> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class AllAppsContainer extends GeneratedMessageLite<AllAppsContainer, Builder> implements AllAppsContainerOrBuilder {
        private static final AllAppsContainer DEFAULT_INSTANCE;
        private static volatile Parser<AllAppsContainer> PARSER;

        private AllAppsContainer() {
        }

        public static AllAppsContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static AllAppsContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static AllAppsContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static AllAppsContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static AllAppsContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static AllAppsContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static AllAppsContainer parseFrom(InputStream inputStream) throws IOException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static AllAppsContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static AllAppsContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (AllAppsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static AllAppsContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (AllAppsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static AllAppsContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static AllAppsContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (AllAppsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(AllAppsContainer allAppsContainer) {
            return DEFAULT_INSTANCE.createBuilder(allAppsContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<AllAppsContainer, Builder> implements AllAppsContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:3871) call: com.android.launcher3.logger.LauncherAtom.AllAppsContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(AllAppsContainer.DEFAULT_INSTANCE);
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new AllAppsContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0000", null);
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<AllAppsContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (AllAppsContainer.class) {
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
            AllAppsContainer allAppsContainer = new AllAppsContainer();
            DEFAULT_INSTANCE = allAppsContainer;
            GeneratedMessageLite.registerDefaultInstance(AllAppsContainer.class, allAppsContainer);
        }

        public static AllAppsContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<AllAppsContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class WidgetsContainer extends GeneratedMessageLite<WidgetsContainer, Builder> implements WidgetsContainerOrBuilder {
        private static final WidgetsContainer DEFAULT_INSTANCE;
        private static volatile Parser<WidgetsContainer> PARSER;

        private WidgetsContainer() {
        }

        public static WidgetsContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static WidgetsContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static WidgetsContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static WidgetsContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static WidgetsContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static WidgetsContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static WidgetsContainer parseFrom(InputStream inputStream) throws IOException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static WidgetsContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static WidgetsContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (WidgetsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static WidgetsContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WidgetsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static WidgetsContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static WidgetsContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WidgetsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(WidgetsContainer widgetsContainer) {
            return DEFAULT_INSTANCE.createBuilder(widgetsContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<WidgetsContainer, Builder> implements WidgetsContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:4051) call: com.android.launcher3.logger.LauncherAtom.WidgetsContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(WidgetsContainer.DEFAULT_INSTANCE);
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new WidgetsContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0000", null);
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<WidgetsContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (WidgetsContainer.class) {
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
            WidgetsContainer widgetsContainer = new WidgetsContainer();
            DEFAULT_INSTANCE = widgetsContainer;
            GeneratedMessageLite.registerDefaultInstance(WidgetsContainer.class, widgetsContainer);
        }

        public static WidgetsContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<WidgetsContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class PredictionContainer extends GeneratedMessageLite<PredictionContainer, Builder> implements PredictionContainerOrBuilder {
        private static final PredictionContainer DEFAULT_INSTANCE;
        private static volatile Parser<PredictionContainer> PARSER;

        private PredictionContainer() {
        }

        public static PredictionContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static PredictionContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static PredictionContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static PredictionContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static PredictionContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static PredictionContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static PredictionContainer parseFrom(InputStream inputStream) throws IOException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static PredictionContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static PredictionContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (PredictionContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static PredictionContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictionContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static PredictionContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static PredictionContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictionContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(PredictionContainer predictionContainer) {
            return DEFAULT_INSTANCE.createBuilder(predictionContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<PredictionContainer, Builder> implements PredictionContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:4239) call: com.android.launcher3.logger.LauncherAtom.PredictionContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(PredictionContainer.DEFAULT_INSTANCE);
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new PredictionContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0000", null);
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<PredictionContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (PredictionContainer.class) {
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
            PredictionContainer predictionContainer = new PredictionContainer();
            DEFAULT_INSTANCE = predictionContainer;
            GeneratedMessageLite.registerDefaultInstance(PredictionContainer.class, predictionContainer);
        }

        public static PredictionContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<PredictionContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class SearchResultContainer extends GeneratedMessageLite<SearchResultContainer, Builder> implements SearchResultContainerOrBuilder {
        public static final int ALL_APPS_CONTAINER_FIELD_NUMBER = 3;
        private static final SearchResultContainer DEFAULT_INSTANCE;
        private static volatile Parser<SearchResultContainer> PARSER = null;
        public static final int QUERY_LENGTH_FIELD_NUMBER = 1;
        public static final int WORKSPACE_FIELD_NUMBER = 2;
        private int bitField0_;
        private int parentContainerCase_ = 0;
        private Object parentContainer_;
        private int queryLength_;

        private SearchResultContainer() {
        }

        public enum ParentContainerCase implements Internal.EnumLite {
            WORKSPACE(2),
            ALL_APPS_CONTAINER(3),
            PARENTCONTAINER_NOT_SET(0);

            private final int value;

            ParentContainerCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ParentContainerCase valueOf(int i) {
                return forNumber(i);
            }

            public static ParentContainerCase forNumber(int i) {
                if (i == 0) {
                    return PARENTCONTAINER_NOT_SET;
                }
                if (i == 2) {
                    return WORKSPACE;
                }
                if (i != 3) {
                    return null;
                }
                return ALL_APPS_CONTAINER;
            }

            @Override // com.google.protobuf.Internal.EnumLite
            public int getNumber() {
                return this.value;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
        public ParentContainerCase getParentContainerCase() {
            return ParentContainerCase.forNumber(this.parentContainerCase_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearParentContainer() {
            this.parentContainerCase_ = 0;
            this.parentContainer_ = null;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
        public boolean hasQueryLength() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
        public int getQueryLength() {
            return this.queryLength_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setQueryLength(int i) {
            this.bitField0_ |= 1;
            this.queryLength_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearQueryLength() {
            this.bitField0_ &= -2;
            this.queryLength_ = 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
        public boolean hasWorkspace() {
            return this.parentContainerCase_ == 2;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
        public WorkspaceContainer getWorkspace() {
            if (this.parentContainerCase_ == 2) {
                return (WorkspaceContainer) this.parentContainer_;
            }
            return WorkspaceContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer workspaceContainer) {
            Objects.requireNonNull(workspaceContainer);
            this.parentContainer_ = workspaceContainer;
            this.parentContainerCase_ = 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeWorkspace(WorkspaceContainer workspaceContainer) {
            Objects.requireNonNull(workspaceContainer);
            if (this.parentContainerCase_ == 2 && this.parentContainer_ != WorkspaceContainer.getDefaultInstance()) {
                this.parentContainer_ = WorkspaceContainer.newBuilder((WorkspaceContainer) this.parentContainer_).mergeFrom(workspaceContainer).buildPartial();
            } else {
                this.parentContainer_ = workspaceContainer;
            }
            this.parentContainerCase_ = 2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearWorkspace() {
            if (this.parentContainerCase_ == 2) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
        public boolean hasAllAppsContainer() {
            return this.parentContainerCase_ == 3;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
        public AllAppsContainer getAllAppsContainer() {
            if (this.parentContainerCase_ == 3) {
                return (AllAppsContainer) this.parentContainer_;
            }
            return AllAppsContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAllAppsContainer(AllAppsContainer allAppsContainer) {
            Objects.requireNonNull(allAppsContainer);
            this.parentContainer_ = allAppsContainer;
            this.parentContainerCase_ = 3;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAllAppsContainer(AllAppsContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 3;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeAllAppsContainer(AllAppsContainer allAppsContainer) {
            Objects.requireNonNull(allAppsContainer);
            if (this.parentContainerCase_ == 3 && this.parentContainer_ != AllAppsContainer.getDefaultInstance()) {
                this.parentContainer_ = AllAppsContainer.newBuilder((AllAppsContainer) this.parentContainer_).mergeFrom(allAppsContainer).buildPartial();
            } else {
                this.parentContainer_ = allAppsContainer;
            }
            this.parentContainerCase_ = 3;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearAllAppsContainer() {
            if (this.parentContainerCase_ == 3) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        public static SearchResultContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static SearchResultContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static SearchResultContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static SearchResultContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static SearchResultContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static SearchResultContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static SearchResultContainer parseFrom(InputStream inputStream) throws IOException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static SearchResultContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static SearchResultContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (SearchResultContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static SearchResultContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SearchResultContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static SearchResultContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static SearchResultContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SearchResultContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(SearchResultContainer searchResultContainer) {
            return DEFAULT_INSTANCE.createBuilder(searchResultContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<SearchResultContainer, Builder> implements SearchResultContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:4681) call: com.android.launcher3.logger.LauncherAtom.SearchResultContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(SearchResultContainer.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
            public ParentContainerCase getParentContainerCase() {
                return ((SearchResultContainer) this.instance).getParentContainerCase();
            }

            public Builder clearParentContainer() {
                copyOnWrite();
                ((SearchResultContainer) this.instance).clearParentContainer();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
            public boolean hasQueryLength() {
                return ((SearchResultContainer) this.instance).hasQueryLength();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
            public int getQueryLength() {
                return ((SearchResultContainer) this.instance).getQueryLength();
            }

            public Builder setQueryLength(int i) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).setQueryLength(i);
                return this;
            }

            public Builder clearQueryLength() {
                copyOnWrite();
                ((SearchResultContainer) this.instance).clearQueryLength();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
            public boolean hasWorkspace() {
                return ((SearchResultContainer) this.instance).hasWorkspace();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
            public WorkspaceContainer getWorkspace() {
                return ((SearchResultContainer) this.instance).getWorkspace();
            }

            public Builder setWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).setWorkspace(workspaceContainer);
                return this;
            }

            public Builder setWorkspace(WorkspaceContainer.Builder builder) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).setWorkspace(builder);
                return this;
            }

            public Builder mergeWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).mergeWorkspace(workspaceContainer);
                return this;
            }

            public Builder clearWorkspace() {
                copyOnWrite();
                ((SearchResultContainer) this.instance).clearWorkspace();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
            public boolean hasAllAppsContainer() {
                return ((SearchResultContainer) this.instance).hasAllAppsContainer();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.SearchResultContainerOrBuilder
            public AllAppsContainer getAllAppsContainer() {
                return ((SearchResultContainer) this.instance).getAllAppsContainer();
            }

            public Builder setAllAppsContainer(AllAppsContainer allAppsContainer) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).setAllAppsContainer(allAppsContainer);
                return this;
            }

            public Builder setAllAppsContainer(AllAppsContainer.Builder builder) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).setAllAppsContainer(builder);
                return this;
            }

            public Builder mergeAllAppsContainer(AllAppsContainer allAppsContainer) {
                copyOnWrite();
                ((SearchResultContainer) this.instance).mergeAllAppsContainer(allAppsContainer);
                return this;
            }

            public Builder clearAllAppsContainer() {
                copyOnWrite();
                ((SearchResultContainer) this.instance).clearAllAppsContainer();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new SearchResultContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0003\u0001\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001\u0004\u0000\u0002<\u0000\u0003<\u0000", new Object[]{"parentContainer_", "parentContainerCase_", "bitField0_", "queryLength_", WorkspaceContainer.class, AllAppsContainer.class});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<SearchResultContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (SearchResultContainer.class) {
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
            SearchResultContainer searchResultContainer = new SearchResultContainer();
            DEFAULT_INSTANCE = searchResultContainer;
            GeneratedMessageLite.registerDefaultInstance(SearchResultContainer.class, searchResultContainer);
        }

        public static SearchResultContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SearchResultContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class ShortcutsContainer extends GeneratedMessageLite<ShortcutsContainer, Builder> implements ShortcutsContainerOrBuilder {
        private static final ShortcutsContainer DEFAULT_INSTANCE;
        private static volatile Parser<ShortcutsContainer> PARSER;

        private ShortcutsContainer() {
        }

        public static ShortcutsContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static ShortcutsContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static ShortcutsContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static ShortcutsContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static ShortcutsContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static ShortcutsContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static ShortcutsContainer parseFrom(InputStream inputStream) throws IOException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ShortcutsContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ShortcutsContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (ShortcutsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static ShortcutsContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ShortcutsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static ShortcutsContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static ShortcutsContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (ShortcutsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(ShortcutsContainer shortcutsContainer) {
            return DEFAULT_INSTANCE.createBuilder(shortcutsContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<ShortcutsContainer, Builder> implements ShortcutsContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:5035) call: com.android.launcher3.logger.LauncherAtom.ShortcutsContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(ShortcutsContainer.DEFAULT_INSTANCE);
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new ShortcutsContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0000", null);
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<ShortcutsContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (ShortcutsContainer.class) {
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
            ShortcutsContainer shortcutsContainer = new ShortcutsContainer();
            DEFAULT_INSTANCE = shortcutsContainer;
            GeneratedMessageLite.registerDefaultInstance(ShortcutsContainer.class, shortcutsContainer);
        }

        public static ShortcutsContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<ShortcutsContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class SettingsContainer extends GeneratedMessageLite<SettingsContainer, Builder> implements SettingsContainerOrBuilder {
        private static final SettingsContainer DEFAULT_INSTANCE;
        private static volatile Parser<SettingsContainer> PARSER;

        private SettingsContainer() {
        }

        public static SettingsContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static SettingsContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static SettingsContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static SettingsContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static SettingsContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static SettingsContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static SettingsContainer parseFrom(InputStream inputStream) throws IOException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static SettingsContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static SettingsContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (SettingsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static SettingsContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SettingsContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static SettingsContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static SettingsContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (SettingsContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(SettingsContainer settingsContainer) {
            return DEFAULT_INSTANCE.createBuilder(settingsContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<SettingsContainer, Builder> implements SettingsContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:5225) call: com.android.launcher3.logger.LauncherAtom.SettingsContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(SettingsContainer.DEFAULT_INSTANCE);
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new SettingsContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0000", null);
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<SettingsContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (SettingsContainer.class) {
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
            SettingsContainer settingsContainer = new SettingsContainer();
            DEFAULT_INSTANCE = settingsContainer;
            GeneratedMessageLite.registerDefaultInstance(SettingsContainer.class, settingsContainer);
        }

        public static SettingsContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SettingsContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class TaskSwitcherContainer extends GeneratedMessageLite<TaskSwitcherContainer, Builder> implements TaskSwitcherContainerOrBuilder {
        private static final TaskSwitcherContainer DEFAULT_INSTANCE;
        private static volatile Parser<TaskSwitcherContainer> PARSER;

        private TaskSwitcherContainer() {
        }

        public static TaskSwitcherContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static TaskSwitcherContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static TaskSwitcherContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static TaskSwitcherContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static TaskSwitcherContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static TaskSwitcherContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static TaskSwitcherContainer parseFrom(InputStream inputStream) throws IOException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static TaskSwitcherContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static TaskSwitcherContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (TaskSwitcherContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static TaskSwitcherContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TaskSwitcherContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static TaskSwitcherContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static TaskSwitcherContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TaskSwitcherContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(TaskSwitcherContainer taskSwitcherContainer) {
            return DEFAULT_INSTANCE.createBuilder(taskSwitcherContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<TaskSwitcherContainer, Builder> implements TaskSwitcherContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:5405) call: com.android.launcher3.logger.LauncherAtom.TaskSwitcherContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(TaskSwitcherContainer.DEFAULT_INSTANCE);
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new TaskSwitcherContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0000", null);
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<TaskSwitcherContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (TaskSwitcherContainer.class) {
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
            TaskSwitcherContainer taskSwitcherContainer = new TaskSwitcherContainer();
            DEFAULT_INSTANCE = taskSwitcherContainer;
            GeneratedMessageLite.registerDefaultInstance(TaskSwitcherContainer.class, taskSwitcherContainer);
        }

        public static TaskSwitcherContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<TaskSwitcherContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class Application extends GeneratedMessageLite<Application, Builder> implements ApplicationOrBuilder {
        public static final int COMPONENT_NAME_FIELD_NUMBER = 2;
        private static final Application DEFAULT_INSTANCE;
        public static final int PACKAGE_NAME_FIELD_NUMBER = 1;
        private static volatile Parser<Application> PARSER;
        private int bitField0_;
        private String packageName_ = "";
        private String componentName_ = "";

        private Application() {
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
        public boolean hasPackageName() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
        public String getPackageName() {
            return this.packageName_;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
        public ByteString getPackageNameBytes() {
            return ByteString.copyFromUtf8(this.packageName_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPackageName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 1;
            this.packageName_ = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPackageName() {
            this.bitField0_ &= -2;
            this.packageName_ = getDefaultInstance().getPackageName();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPackageNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 1;
            this.packageName_ = byteString.toStringUtf8();
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
        public boolean hasComponentName() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
        public String getComponentName() {
            return this.componentName_;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
        public ByteString getComponentNameBytes() {
            return ByteString.copyFromUtf8(this.componentName_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setComponentName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 2;
            this.componentName_ = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearComponentName() {
            this.bitField0_ &= -3;
            this.componentName_ = getDefaultInstance().getComponentName();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setComponentNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 2;
            this.componentName_ = byteString.toStringUtf8();
        }

        public static Application parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Application parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static Application parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Application parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Application parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Application parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Application parseFrom(InputStream inputStream) throws IOException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Application parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Application parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Application) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Application parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Application) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Application parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Application parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Application) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Application application) {
            return DEFAULT_INSTANCE.createBuilder(application);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Application, Builder> implements ApplicationOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:5732) call: com.android.launcher3.logger.LauncherAtom.Application.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(Application.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
            public boolean hasPackageName() {
                return ((Application) this.instance).hasPackageName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
            public String getPackageName() {
                return ((Application) this.instance).getPackageName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
            public ByteString getPackageNameBytes() {
                return ((Application) this.instance).getPackageNameBytes();
            }

            public Builder setPackageName(String str) {
                copyOnWrite();
                ((Application) this.instance).setPackageName(str);
                return this;
            }

            public Builder clearPackageName() {
                copyOnWrite();
                ((Application) this.instance).clearPackageName();
                return this;
            }

            public Builder setPackageNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Application) this.instance).setPackageNameBytes(byteString);
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
            public boolean hasComponentName() {
                return ((Application) this.instance).hasComponentName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
            public String getComponentName() {
                return ((Application) this.instance).getComponentName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ApplicationOrBuilder
            public ByteString getComponentNameBytes() {
                return ((Application) this.instance).getComponentNameBytes();
            }

            public Builder setComponentName(String str) {
                copyOnWrite();
                ((Application) this.instance).setComponentName(str);
                return this;
            }

            public Builder clearComponentName() {
                copyOnWrite();
                ((Application) this.instance).clearComponentName();
                return this;
            }

            public Builder setComponentNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Application) this.instance).setComponentNameBytes(byteString);
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Application();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001\b\u0000\u0002\b\u0001", new Object[]{"bitField0_", "packageName_", "componentName_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<Application> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (Application.class) {
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
            Application application = new Application();
            DEFAULT_INSTANCE = application;
            GeneratedMessageLite.registerDefaultInstance(Application.class, application);
        }

        public static Application getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Application> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class Shortcut extends GeneratedMessageLite<Shortcut, Builder> implements ShortcutOrBuilder {
        private static final Shortcut DEFAULT_INSTANCE;
        private static volatile Parser<Shortcut> PARSER = null;
        public static final int SHORTCUT_NAME_FIELD_NUMBER = 1;
        private int bitField0_;
        private String shortcutName_ = "";

        private Shortcut() {
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ShortcutOrBuilder
        public boolean hasShortcutName() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ShortcutOrBuilder
        public String getShortcutName() {
            return this.shortcutName_;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.ShortcutOrBuilder
        public ByteString getShortcutNameBytes() {
            return ByteString.copyFromUtf8(this.shortcutName_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setShortcutName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 1;
            this.shortcutName_ = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearShortcutName() {
            this.bitField0_ &= -2;
            this.shortcutName_ = getDefaultInstance().getShortcutName();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setShortcutNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 1;
            this.shortcutName_ = byteString.toStringUtf8();
        }

        public static Shortcut parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Shortcut parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static Shortcut parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Shortcut parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Shortcut parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Shortcut parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Shortcut parseFrom(InputStream inputStream) throws IOException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Shortcut parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Shortcut parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Shortcut) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Shortcut parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Shortcut) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Shortcut parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Shortcut parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Shortcut) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Shortcut shortcut) {
            return DEFAULT_INSTANCE.createBuilder(shortcut);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Shortcut, Builder> implements ShortcutOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:6094) call: com.android.launcher3.logger.LauncherAtom.Shortcut.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(Shortcut.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ShortcutOrBuilder
            public boolean hasShortcutName() {
                return ((Shortcut) this.instance).hasShortcutName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ShortcutOrBuilder
            public String getShortcutName() {
                return ((Shortcut) this.instance).getShortcutName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.ShortcutOrBuilder
            public ByteString getShortcutNameBytes() {
                return ((Shortcut) this.instance).getShortcutNameBytes();
            }

            public Builder setShortcutName(String str) {
                copyOnWrite();
                ((Shortcut) this.instance).setShortcutName(str);
                return this;
            }

            public Builder clearShortcutName() {
                copyOnWrite();
                ((Shortcut) this.instance).clearShortcutName();
                return this;
            }

            public Builder setShortcutNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Shortcut) this.instance).setShortcutNameBytes(byteString);
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Shortcut();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001\b\u0000", new Object[]{"bitField0_", "shortcutName_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<Shortcut> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (Shortcut.class) {
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
            Shortcut shortcut = new Shortcut();
            DEFAULT_INSTANCE = shortcut;
            GeneratedMessageLite.registerDefaultInstance(Shortcut.class, shortcut);
        }

        public static Shortcut getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Shortcut> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class Widget extends GeneratedMessageLite<Widget, Builder> implements WidgetOrBuilder {
        public static final int APP_WIDGET_ID_FIELD_NUMBER = 3;
        public static final int COMPONENT_NAME_FIELD_NUMBER = 5;
        private static final Widget DEFAULT_INSTANCE;
        public static final int PACKAGE_NAME_FIELD_NUMBER = 4;
        private static volatile Parser<Widget> PARSER = null;
        public static final int SPAN_X_FIELD_NUMBER = 1;
        public static final int SPAN_Y_FIELD_NUMBER = 2;
        private int appWidgetId_;
        private int bitField0_;
        private int spanX_ = 1;
        private int spanY_ = 1;
        private String packageName_ = "";
        private String componentName_ = "";

        private Widget() {
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public boolean hasSpanX() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public int getSpanX() {
            return this.spanX_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSpanX(int i) {
            this.bitField0_ |= 1;
            this.spanX_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSpanX() {
            this.bitField0_ &= -2;
            this.spanX_ = 1;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public boolean hasSpanY() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public int getSpanY() {
            return this.spanY_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSpanY(int i) {
            this.bitField0_ |= 2;
            this.spanY_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSpanY() {
            this.bitField0_ &= -3;
            this.spanY_ = 1;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public boolean hasAppWidgetId() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public int getAppWidgetId() {
            return this.appWidgetId_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setAppWidgetId(int i) {
            this.bitField0_ |= 4;
            this.appWidgetId_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearAppWidgetId() {
            this.bitField0_ &= -5;
            this.appWidgetId_ = 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public boolean hasPackageName() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public String getPackageName() {
            return this.packageName_;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public ByteString getPackageNameBytes() {
            return ByteString.copyFromUtf8(this.packageName_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPackageName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 8;
            this.packageName_ = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPackageName() {
            this.bitField0_ &= -9;
            this.packageName_ = getDefaultInstance().getPackageName();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPackageNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 8;
            this.packageName_ = byteString.toStringUtf8();
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public boolean hasComponentName() {
            return (this.bitField0_ & 16) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public String getComponentName() {
            return this.componentName_;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
        public ByteString getComponentNameBytes() {
            return ByteString.copyFromUtf8(this.componentName_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setComponentName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 16;
            this.componentName_ = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearComponentName() {
            this.bitField0_ &= -17;
            this.componentName_ = getDefaultInstance().getComponentName();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setComponentNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 16;
            this.componentName_ = byteString.toStringUtf8();
        }

        public static Widget parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Widget parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static Widget parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Widget parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Widget parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Widget parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Widget parseFrom(InputStream inputStream) throws IOException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Widget parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Widget parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Widget) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Widget parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Widget) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Widget parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Widget parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Widget) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Widget widget) {
            return DEFAULT_INSTANCE.createBuilder(widget);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Widget, Builder> implements WidgetOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:6668) call: com.android.launcher3.logger.LauncherAtom.Widget.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(Widget.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public boolean hasSpanX() {
                return ((Widget) this.instance).hasSpanX();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public int getSpanX() {
                return ((Widget) this.instance).getSpanX();
            }

            public Builder setSpanX(int i) {
                copyOnWrite();
                ((Widget) this.instance).setSpanX(i);
                return this;
            }

            public Builder clearSpanX() {
                copyOnWrite();
                ((Widget) this.instance).clearSpanX();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public boolean hasSpanY() {
                return ((Widget) this.instance).hasSpanY();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public int getSpanY() {
                return ((Widget) this.instance).getSpanY();
            }

            public Builder setSpanY(int i) {
                copyOnWrite();
                ((Widget) this.instance).setSpanY(i);
                return this;
            }

            public Builder clearSpanY() {
                copyOnWrite();
                ((Widget) this.instance).clearSpanY();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public boolean hasAppWidgetId() {
                return ((Widget) this.instance).hasAppWidgetId();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public int getAppWidgetId() {
                return ((Widget) this.instance).getAppWidgetId();
            }

            public Builder setAppWidgetId(int i) {
                copyOnWrite();
                ((Widget) this.instance).setAppWidgetId(i);
                return this;
            }

            public Builder clearAppWidgetId() {
                copyOnWrite();
                ((Widget) this.instance).clearAppWidgetId();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public boolean hasPackageName() {
                return ((Widget) this.instance).hasPackageName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public String getPackageName() {
                return ((Widget) this.instance).getPackageName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public ByteString getPackageNameBytes() {
                return ((Widget) this.instance).getPackageNameBytes();
            }

            public Builder setPackageName(String str) {
                copyOnWrite();
                ((Widget) this.instance).setPackageName(str);
                return this;
            }

            public Builder clearPackageName() {
                copyOnWrite();
                ((Widget) this.instance).clearPackageName();
                return this;
            }

            public Builder setPackageNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Widget) this.instance).setPackageNameBytes(byteString);
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public boolean hasComponentName() {
                return ((Widget) this.instance).hasComponentName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public String getComponentName() {
                return ((Widget) this.instance).getComponentName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WidgetOrBuilder
            public ByteString getComponentNameBytes() {
                return ((Widget) this.instance).getComponentNameBytes();
            }

            public Builder setComponentName(String str) {
                copyOnWrite();
                ((Widget) this.instance).setComponentName(str);
                return this;
            }

            public Builder clearComponentName() {
                copyOnWrite();
                ((Widget) this.instance).clearComponentName();
                return this;
            }

            public Builder setComponentNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Widget) this.instance).setComponentNameBytes(byteString);
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Widget();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0005\u0000\u0001\u0001\u0005\u0005\u0000\u0000\u0000\u0001\u0004\u0000\u0002\u0004\u0001\u0003\u0004\u0002\u0004\b\u0003\u0005\b\u0004", new Object[]{"bitField0_", "spanX_", "spanY_", "appWidgetId_", "packageName_", "componentName_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<Widget> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (Widget.class) {
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
            Widget widget = new Widget();
            DEFAULT_INSTANCE = widget;
            GeneratedMessageLite.registerDefaultInstance(Widget.class, widget);
        }

        public static Widget getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Widget> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class Task extends GeneratedMessageLite<Task, Builder> implements TaskOrBuilder {
        public static final int COMPONENT_NAME_FIELD_NUMBER = 2;
        private static final Task DEFAULT_INSTANCE;
        public static final int INDEX_FIELD_NUMBER = 3;
        public static final int PACKAGE_NAME_FIELD_NUMBER = 1;
        private static volatile Parser<Task> PARSER;
        private int bitField0_;
        private int index_;
        private String packageName_ = "";
        private String componentName_ = "";

        private Task() {
        }

        @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
        public boolean hasPackageName() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
        public String getPackageName() {
            return this.packageName_;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
        public ByteString getPackageNameBytes() {
            return ByteString.copyFromUtf8(this.packageName_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPackageName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 1;
            this.packageName_ = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPackageName() {
            this.bitField0_ &= -2;
            this.packageName_ = getDefaultInstance().getPackageName();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPackageNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 1;
            this.packageName_ = byteString.toStringUtf8();
        }

        @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
        public boolean hasComponentName() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
        public String getComponentName() {
            return this.componentName_;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
        public ByteString getComponentNameBytes() {
            return ByteString.copyFromUtf8(this.componentName_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setComponentName(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 2;
            this.componentName_ = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearComponentName() {
            this.bitField0_ &= -3;
            this.componentName_ = getDefaultInstance().getComponentName();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setComponentNameBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 2;
            this.componentName_ = byteString.toStringUtf8();
        }

        @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
        public boolean hasIndex() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
        public int getIndex() {
            return this.index_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIndex(int i) {
            this.bitField0_ |= 4;
            this.index_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIndex() {
            this.bitField0_ &= -5;
            this.index_ = 0;
        }

        public static Task parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static Task parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static Task parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static Task parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static Task parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static Task parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static Task parseFrom(InputStream inputStream) throws IOException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Task parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Task parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (Task) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static Task parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Task) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static Task parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static Task parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (Task) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(Task task) {
            return DEFAULT_INSTANCE.createBuilder(task);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<Task, Builder> implements TaskOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:7283) call: com.android.launcher3.logger.LauncherAtom.Task.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(Task.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
            public boolean hasPackageName() {
                return ((Task) this.instance).hasPackageName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
            public String getPackageName() {
                return ((Task) this.instance).getPackageName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
            public ByteString getPackageNameBytes() {
                return ((Task) this.instance).getPackageNameBytes();
            }

            public Builder setPackageName(String str) {
                copyOnWrite();
                ((Task) this.instance).setPackageName(str);
                return this;
            }

            public Builder clearPackageName() {
                copyOnWrite();
                ((Task) this.instance).clearPackageName();
                return this;
            }

            public Builder setPackageNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Task) this.instance).setPackageNameBytes(byteString);
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
            public boolean hasComponentName() {
                return ((Task) this.instance).hasComponentName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
            public String getComponentName() {
                return ((Task) this.instance).getComponentName();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
            public ByteString getComponentNameBytes() {
                return ((Task) this.instance).getComponentNameBytes();
            }

            public Builder setComponentName(String str) {
                copyOnWrite();
                ((Task) this.instance).setComponentName(str);
                return this;
            }

            public Builder clearComponentName() {
                copyOnWrite();
                ((Task) this.instance).clearComponentName();
                return this;
            }

            public Builder setComponentNameBytes(ByteString byteString) {
                copyOnWrite();
                ((Task) this.instance).setComponentNameBytes(byteString);
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
            public boolean hasIndex() {
                return ((Task) this.instance).hasIndex();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.TaskOrBuilder
            public int getIndex() {
                return ((Task) this.instance).getIndex();
            }

            public Builder setIndex(int i) {
                copyOnWrite();
                ((Task) this.instance).setIndex(i);
                return this;
            }

            public Builder clearIndex() {
                copyOnWrite();
                ((Task) this.instance).clearIndex();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new Task();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001\b\u0000\u0002\b\u0001\u0003\u0004\u0002", new Object[]{"bitField0_", "packageName_", "componentName_", "index_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<Task> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (Task.class) {
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
            Task task = new Task();
            DEFAULT_INSTANCE = task;
            GeneratedMessageLite.registerDefaultInstance(Task.class, task);
        }

        public static Task getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Task> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class FolderIcon extends GeneratedMessageLite<FolderIcon, Builder> implements FolderIconOrBuilder {
        public static final int CARDINALITY_FIELD_NUMBER = 1;
        private static final FolderIcon DEFAULT_INSTANCE;
        public static final int FROM_LABEL_STATE_FIELD_NUMBER = 2;
        public static final int LABEL_INFO_FIELD_NUMBER = 4;
        private static volatile Parser<FolderIcon> PARSER = null;
        public static final int TO_LABEL_STATE_FIELD_NUMBER = 3;
        private int bitField0_;
        private int cardinality_;
        private int fromLabelState_;
        private String labelInfo_ = "";
        private int toLabelState_;

        private FolderIcon() {
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
        public boolean hasCardinality() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
        public int getCardinality() {
            return this.cardinality_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCardinality(int i) {
            this.bitField0_ |= 1;
            this.cardinality_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearCardinality() {
            this.bitField0_ &= -2;
            this.cardinality_ = 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
        public boolean hasFromLabelState() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
        public FromState getFromLabelState() {
            FromState fromStateForNumber = FromState.forNumber(this.fromLabelState_);
            return fromStateForNumber == null ? FromState.FROM_STATE_UNSPECIFIED : fromStateForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setFromLabelState(FromState fromState) {
            Objects.requireNonNull(fromState);
            this.bitField0_ |= 2;
            this.fromLabelState_ = fromState.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearFromLabelState() {
            this.bitField0_ &= -3;
            this.fromLabelState_ = 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
        public boolean hasToLabelState() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
        public ToState getToLabelState() {
            ToState toStateForNumber = ToState.forNumber(this.toLabelState_);
            return toStateForNumber == null ? ToState.TO_STATE_UNSPECIFIED : toStateForNumber;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setToLabelState(ToState toState) {
            Objects.requireNonNull(toState);
            this.bitField0_ |= 4;
            this.toLabelState_ = toState.getNumber();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearToLabelState() {
            this.bitField0_ &= -5;
            this.toLabelState_ = 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
        public boolean hasLabelInfo() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
        public String getLabelInfo() {
            return this.labelInfo_;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
        public ByteString getLabelInfoBytes() {
            return ByteString.copyFromUtf8(this.labelInfo_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLabelInfo(String str) {
            Objects.requireNonNull(str);
            this.bitField0_ |= 8;
            this.labelInfo_ = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearLabelInfo() {
            this.bitField0_ &= -9;
            this.labelInfo_ = getDefaultInstance().getLabelInfo();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setLabelInfoBytes(ByteString byteString) {
            Objects.requireNonNull(byteString);
            this.bitField0_ |= 8;
            this.labelInfo_ = byteString.toStringUtf8();
        }

        public static FolderIcon parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static FolderIcon parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static FolderIcon parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static FolderIcon parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static FolderIcon parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static FolderIcon parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static FolderIcon parseFrom(InputStream inputStream) throws IOException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static FolderIcon parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static FolderIcon parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (FolderIcon) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static FolderIcon parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderIcon) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static FolderIcon parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static FolderIcon parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderIcon) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(FolderIcon folderIcon) {
            return DEFAULT_INSTANCE.createBuilder(folderIcon);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<FolderIcon, Builder> implements FolderIconOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:7922) call: com.android.launcher3.logger.LauncherAtom.FolderIcon.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(FolderIcon.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
            public boolean hasCardinality() {
                return ((FolderIcon) this.instance).hasCardinality();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
            public int getCardinality() {
                return ((FolderIcon) this.instance).getCardinality();
            }

            public Builder setCardinality(int i) {
                copyOnWrite();
                ((FolderIcon) this.instance).setCardinality(i);
                return this;
            }

            public Builder clearCardinality() {
                copyOnWrite();
                ((FolderIcon) this.instance).clearCardinality();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
            public boolean hasFromLabelState() {
                return ((FolderIcon) this.instance).hasFromLabelState();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
            public FromState getFromLabelState() {
                return ((FolderIcon) this.instance).getFromLabelState();
            }

            public Builder setFromLabelState(FromState fromState) {
                copyOnWrite();
                ((FolderIcon) this.instance).setFromLabelState(fromState);
                return this;
            }

            public Builder clearFromLabelState() {
                copyOnWrite();
                ((FolderIcon) this.instance).clearFromLabelState();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
            public boolean hasToLabelState() {
                return ((FolderIcon) this.instance).hasToLabelState();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
            public ToState getToLabelState() {
                return ((FolderIcon) this.instance).getToLabelState();
            }

            public Builder setToLabelState(ToState toState) {
                copyOnWrite();
                ((FolderIcon) this.instance).setToLabelState(toState);
                return this;
            }

            public Builder clearToLabelState() {
                copyOnWrite();
                ((FolderIcon) this.instance).clearToLabelState();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
            public boolean hasLabelInfo() {
                return ((FolderIcon) this.instance).hasLabelInfo();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
            public String getLabelInfo() {
                return ((FolderIcon) this.instance).getLabelInfo();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderIconOrBuilder
            public ByteString getLabelInfoBytes() {
                return ((FolderIcon) this.instance).getLabelInfoBytes();
            }

            public Builder setLabelInfo(String str) {
                copyOnWrite();
                ((FolderIcon) this.instance).setLabelInfo(str);
                return this;
            }

            public Builder clearLabelInfo() {
                copyOnWrite();
                ((FolderIcon) this.instance).clearLabelInfo();
                return this;
            }

            public Builder setLabelInfoBytes(ByteString byteString) {
                copyOnWrite();
                ((FolderIcon) this.instance).setLabelInfoBytes(byteString);
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new FolderIcon();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0004\u0000\u0001\u0001\u0004\u0004\u0000\u0000\u0000\u0001\u0004\u0000\u0002\f\u0001\u0003\f\u0002\u0004\b\u0003", new Object[]{"bitField0_", "cardinality_", "fromLabelState_", FromState.internalGetVerifier(), "toLabelState_", ToState.internalGetVerifier(), "labelInfo_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<FolderIcon> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (FolderIcon.class) {
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
            FolderIcon folderIcon = new FolderIcon();
            DEFAULT_INSTANCE = folderIcon;
            GeneratedMessageLite.registerDefaultInstance(FolderIcon.class, folderIcon);
        }

        public static FolderIcon getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<FolderIcon> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class WorkspaceContainer extends GeneratedMessageLite<WorkspaceContainer, Builder> implements WorkspaceContainerOrBuilder {
        private static final WorkspaceContainer DEFAULT_INSTANCE;
        public static final int GRID_X_FIELD_NUMBER = 2;
        public static final int GRID_Y_FIELD_NUMBER = 3;
        public static final int PAGE_INDEX_FIELD_NUMBER = 1;
        private static volatile Parser<WorkspaceContainer> PARSER;
        private int bitField0_;
        private int pageIndex_ = -2;
        private int gridX_ = -1;
        private int gridY_ = -1;

        private WorkspaceContainer() {
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
        public boolean hasPageIndex() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
        public int getPageIndex() {
            return this.pageIndex_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPageIndex(int i) {
            this.bitField0_ |= 1;
            this.pageIndex_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPageIndex() {
            this.bitField0_ &= -2;
            this.pageIndex_ = -2;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
        public boolean hasGridX() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
        public int getGridX() {
            return this.gridX_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setGridX(int i) {
            this.bitField0_ |= 2;
            this.gridX_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearGridX() {
            this.bitField0_ &= -3;
            this.gridX_ = -1;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
        public boolean hasGridY() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
        public int getGridY() {
            return this.gridY_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setGridY(int i) {
            this.bitField0_ |= 4;
            this.gridY_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearGridY() {
            this.bitField0_ &= -5;
            this.gridY_ = -1;
        }

        public static WorkspaceContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static WorkspaceContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static WorkspaceContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static WorkspaceContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static WorkspaceContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static WorkspaceContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static WorkspaceContainer parseFrom(InputStream inputStream) throws IOException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static WorkspaceContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static WorkspaceContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (WorkspaceContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static WorkspaceContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WorkspaceContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static WorkspaceContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static WorkspaceContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (WorkspaceContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(WorkspaceContainer workspaceContainer) {
            return DEFAULT_INSTANCE.createBuilder(workspaceContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<WorkspaceContainer, Builder> implements WorkspaceContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:8528) call: com.android.launcher3.logger.LauncherAtom.WorkspaceContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(WorkspaceContainer.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
            public boolean hasPageIndex() {
                return ((WorkspaceContainer) this.instance).hasPageIndex();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
            public int getPageIndex() {
                return ((WorkspaceContainer) this.instance).getPageIndex();
            }

            public Builder setPageIndex(int i) {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).setPageIndex(i);
                return this;
            }

            public Builder clearPageIndex() {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).clearPageIndex();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
            public boolean hasGridX() {
                return ((WorkspaceContainer) this.instance).hasGridX();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
            public int getGridX() {
                return ((WorkspaceContainer) this.instance).getGridX();
            }

            public Builder setGridX(int i) {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).setGridX(i);
                return this;
            }

            public Builder clearGridX() {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).clearGridX();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
            public boolean hasGridY() {
                return ((WorkspaceContainer) this.instance).hasGridY();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.WorkspaceContainerOrBuilder
            public int getGridY() {
                return ((WorkspaceContainer) this.instance).getGridY();
            }

            public Builder setGridY(int i) {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).setGridY(i);
                return this;
            }

            public Builder clearGridY() {
                copyOnWrite();
                ((WorkspaceContainer) this.instance).clearGridY();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new WorkspaceContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0000\u0000\u0001\u0004\u0000\u0002\u0004\u0001\u0003\u0004\u0002", new Object[]{"bitField0_", "pageIndex_", "gridX_", "gridY_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<WorkspaceContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (WorkspaceContainer.class) {
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
            WorkspaceContainer workspaceContainer = new WorkspaceContainer();
            DEFAULT_INSTANCE = workspaceContainer;
            GeneratedMessageLite.registerDefaultInstance(WorkspaceContainer.class, workspaceContainer);
        }

        public static WorkspaceContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<WorkspaceContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class HotseatContainer extends GeneratedMessageLite<HotseatContainer, Builder> implements HotseatContainerOrBuilder {
        private static final HotseatContainer DEFAULT_INSTANCE;
        public static final int INDEX_FIELD_NUMBER = 1;
        private static volatile Parser<HotseatContainer> PARSER;
        private int bitField0_;
        private int index_;

        private HotseatContainer() {
        }

        @Override // com.android.launcher3.logger.LauncherAtom.HotseatContainerOrBuilder
        public boolean hasIndex() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.HotseatContainerOrBuilder
        public int getIndex() {
            return this.index_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIndex(int i) {
            this.bitField0_ |= 1;
            this.index_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIndex() {
            this.bitField0_ &= -2;
            this.index_ = 0;
        }

        public static HotseatContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static HotseatContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static HotseatContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static HotseatContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static HotseatContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static HotseatContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static HotseatContainer parseFrom(InputStream inputStream) throws IOException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static HotseatContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static HotseatContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (HotseatContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static HotseatContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (HotseatContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static HotseatContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static HotseatContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (HotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(HotseatContainer hotseatContainer) {
            return DEFAULT_INSTANCE.createBuilder(hotseatContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<HotseatContainer, Builder> implements HotseatContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:8897) call: com.android.launcher3.logger.LauncherAtom.HotseatContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(HotseatContainer.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.HotseatContainerOrBuilder
            public boolean hasIndex() {
                return ((HotseatContainer) this.instance).hasIndex();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.HotseatContainerOrBuilder
            public int getIndex() {
                return ((HotseatContainer) this.instance).getIndex();
            }

            public Builder setIndex(int i) {
                copyOnWrite();
                ((HotseatContainer) this.instance).setIndex(i);
                return this;
            }

            public Builder clearIndex() {
                copyOnWrite();
                ((HotseatContainer) this.instance).clearIndex();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new HotseatContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001\u0004\u0000", new Object[]{"bitField0_", "index_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<HotseatContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (HotseatContainer.class) {
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
            HotseatContainer hotseatContainer = new HotseatContainer();
            DEFAULT_INSTANCE = hotseatContainer;
            GeneratedMessageLite.registerDefaultInstance(HotseatContainer.class, hotseatContainer);
        }

        public static HotseatContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<HotseatContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class PredictedHotseatContainer extends GeneratedMessageLite<PredictedHotseatContainer, Builder> implements PredictedHotseatContainerOrBuilder {
        public static final int CARDINALITY_FIELD_NUMBER = 2;
        private static final PredictedHotseatContainer DEFAULT_INSTANCE;
        public static final int INDEX_FIELD_NUMBER = 1;
        private static volatile Parser<PredictedHotseatContainer> PARSER;
        private int bitField0_;
        private int cardinality_;
        private int index_;

        private PredictedHotseatContainer() {
        }

        @Override // com.android.launcher3.logger.LauncherAtom.PredictedHotseatContainerOrBuilder
        public boolean hasIndex() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.PredictedHotseatContainerOrBuilder
        public int getIndex() {
            return this.index_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setIndex(int i) {
            this.bitField0_ |= 1;
            this.index_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearIndex() {
            this.bitField0_ &= -2;
            this.index_ = 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.PredictedHotseatContainerOrBuilder
        public boolean hasCardinality() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.PredictedHotseatContainerOrBuilder
        public int getCardinality() {
            return this.cardinality_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCardinality(int i) {
            this.bitField0_ |= 2;
            this.cardinality_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearCardinality() {
            this.bitField0_ &= -3;
            this.cardinality_ = 0;
        }

        public static PredictedHotseatContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static PredictedHotseatContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static PredictedHotseatContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static PredictedHotseatContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static PredictedHotseatContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static PredictedHotseatContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static PredictedHotseatContainer parseFrom(InputStream inputStream) throws IOException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static PredictedHotseatContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static PredictedHotseatContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (PredictedHotseatContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static PredictedHotseatContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictedHotseatContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static PredictedHotseatContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static PredictedHotseatContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (PredictedHotseatContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(PredictedHotseatContainer predictedHotseatContainer) {
            return DEFAULT_INSTANCE.createBuilder(predictedHotseatContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<PredictedHotseatContainer, Builder> implements PredictedHotseatContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:9225) call: com.android.launcher3.logger.LauncherAtom.PredictedHotseatContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(PredictedHotseatContainer.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.PredictedHotseatContainerOrBuilder
            public boolean hasIndex() {
                return ((PredictedHotseatContainer) this.instance).hasIndex();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.PredictedHotseatContainerOrBuilder
            public int getIndex() {
                return ((PredictedHotseatContainer) this.instance).getIndex();
            }

            public Builder setIndex(int i) {
                copyOnWrite();
                ((PredictedHotseatContainer) this.instance).setIndex(i);
                return this;
            }

            public Builder clearIndex() {
                copyOnWrite();
                ((PredictedHotseatContainer) this.instance).clearIndex();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.PredictedHotseatContainerOrBuilder
            public boolean hasCardinality() {
                return ((PredictedHotseatContainer) this.instance).hasCardinality();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.PredictedHotseatContainerOrBuilder
            public int getCardinality() {
                return ((PredictedHotseatContainer) this.instance).getCardinality();
            }

            public Builder setCardinality(int i) {
                copyOnWrite();
                ((PredictedHotseatContainer) this.instance).setCardinality(i);
                return this;
            }

            public Builder clearCardinality() {
                copyOnWrite();
                ((PredictedHotseatContainer) this.instance).clearCardinality();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new PredictedHotseatContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001\u0004\u0000\u0002\u0004\u0001", new Object[]{"bitField0_", "index_", "cardinality_"});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<PredictedHotseatContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (PredictedHotseatContainer.class) {
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
            PredictedHotseatContainer predictedHotseatContainer = new PredictedHotseatContainer();
            DEFAULT_INSTANCE = predictedHotseatContainer;
            GeneratedMessageLite.registerDefaultInstance(PredictedHotseatContainer.class, predictedHotseatContainer);
        }

        public static PredictedHotseatContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<PredictedHotseatContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    public static final class FolderContainer extends GeneratedMessageLite<FolderContainer, Builder> implements FolderContainerOrBuilder {
        private static final FolderContainer DEFAULT_INSTANCE;
        public static final int GRID_X_FIELD_NUMBER = 2;
        public static final int GRID_Y_FIELD_NUMBER = 3;
        public static final int HOTSEAT_FIELD_NUMBER = 5;
        public static final int PAGE_INDEX_FIELD_NUMBER = 1;
        private static volatile Parser<FolderContainer> PARSER = null;
        public static final int WORKSPACE_FIELD_NUMBER = 4;
        private int bitField0_;
        private Object parentContainer_;
        private int parentContainerCase_ = 0;
        private int pageIndex_ = -1;
        private int gridX_ = -1;
        private int gridY_ = -1;

        private FolderContainer() {
        }

        public enum ParentContainerCase implements Internal.EnumLite {
            WORKSPACE(4),
            HOTSEAT(5),
            PARENTCONTAINER_NOT_SET(0);

            private final int value;

            ParentContainerCase(int i) {
                this.value = i;
            }

            @Deprecated
            public static ParentContainerCase valueOf(int i) {
                return forNumber(i);
            }

            public static ParentContainerCase forNumber(int i) {
                if (i == 0) {
                    return PARENTCONTAINER_NOT_SET;
                }
                if (i == 4) {
                    return WORKSPACE;
                }
                if (i != 5) {
                    return null;
                }
                return HOTSEAT;
            }

            @Override // com.google.protobuf.Internal.EnumLite
            public int getNumber() {
                return this.value;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public ParentContainerCase getParentContainerCase() {
            return ParentContainerCase.forNumber(this.parentContainerCase_);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearParentContainer() {
            this.parentContainerCase_ = 0;
            this.parentContainer_ = null;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public boolean hasPageIndex() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public int getPageIndex() {
            return this.pageIndex_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPageIndex(int i) {
            this.bitField0_ |= 1;
            this.pageIndex_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearPageIndex() {
            this.bitField0_ &= -2;
            this.pageIndex_ = -1;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public boolean hasGridX() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public int getGridX() {
            return this.gridX_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setGridX(int i) {
            this.bitField0_ |= 2;
            this.gridX_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearGridX() {
            this.bitField0_ &= -3;
            this.gridX_ = -1;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public boolean hasGridY() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public int getGridY() {
            return this.gridY_;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setGridY(int i) {
            this.bitField0_ |= 4;
            this.gridY_ = i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearGridY() {
            this.bitField0_ &= -5;
            this.gridY_ = -1;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public boolean hasWorkspace() {
            return this.parentContainerCase_ == 4;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public WorkspaceContainer getWorkspace() {
            if (this.parentContainerCase_ == 4) {
                return (WorkspaceContainer) this.parentContainer_;
            }
            return WorkspaceContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer workspaceContainer) {
            Objects.requireNonNull(workspaceContainer);
            this.parentContainer_ = workspaceContainer;
            this.parentContainerCase_ = 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setWorkspace(WorkspaceContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeWorkspace(WorkspaceContainer workspaceContainer) {
            Objects.requireNonNull(workspaceContainer);
            if (this.parentContainerCase_ == 4 && this.parentContainer_ != WorkspaceContainer.getDefaultInstance()) {
                this.parentContainer_ = WorkspaceContainer.newBuilder((WorkspaceContainer) this.parentContainer_).mergeFrom(workspaceContainer).buildPartial();
            } else {
                this.parentContainer_ = workspaceContainer;
            }
            this.parentContainerCase_ = 4;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearWorkspace() {
            if (this.parentContainerCase_ == 4) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public boolean hasHotseat() {
            return this.parentContainerCase_ == 5;
        }

        @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
        public HotseatContainer getHotseat() {
            if (this.parentContainerCase_ == 5) {
                return (HotseatContainer) this.parentContainer_;
            }
            return HotseatContainer.getDefaultInstance();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setHotseat(HotseatContainer hotseatContainer) {
            Objects.requireNonNull(hotseatContainer);
            this.parentContainer_ = hotseatContainer;
            this.parentContainerCase_ = 5;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setHotseat(HotseatContainer.Builder builder) {
            this.parentContainer_ = builder.build();
            this.parentContainerCase_ = 5;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void mergeHotseat(HotseatContainer hotseatContainer) {
            Objects.requireNonNull(hotseatContainer);
            if (this.parentContainerCase_ == 5 && this.parentContainer_ != HotseatContainer.getDefaultInstance()) {
                this.parentContainer_ = HotseatContainer.newBuilder((HotseatContainer) this.parentContainer_).mergeFrom(hotseatContainer).buildPartial();
            } else {
                this.parentContainer_ = hotseatContainer;
            }
            this.parentContainerCase_ = 5;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearHotseat() {
            if (this.parentContainerCase_ == 5) {
                this.parentContainerCase_ = 0;
                this.parentContainer_ = null;
            }
        }

        public static FolderContainer parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static FolderContainer parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static FolderContainer parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static FolderContainer parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static FolderContainer parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static FolderContainer parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static FolderContainer parseFrom(InputStream inputStream) throws IOException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static FolderContainer parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static FolderContainer parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (FolderContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static FolderContainer parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderContainer) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static FolderContainer parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static FolderContainer parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (FolderContainer) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(FolderContainer folderContainer) {
            return DEFAULT_INSTANCE.createBuilder(folderContainer);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<FolderContainer, Builder> implements FolderContainerOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:9802) call: com.android.launcher3.logger.LauncherAtom.FolderContainer.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(FolderContainer.DEFAULT_INSTANCE);
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public ParentContainerCase getParentContainerCase() {
                return ((FolderContainer) this.instance).getParentContainerCase();
            }

            public Builder clearParentContainer() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearParentContainer();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public boolean hasPageIndex() {
                return ((FolderContainer) this.instance).hasPageIndex();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public int getPageIndex() {
                return ((FolderContainer) this.instance).getPageIndex();
            }

            public Builder setPageIndex(int i) {
                copyOnWrite();
                ((FolderContainer) this.instance).setPageIndex(i);
                return this;
            }

            public Builder clearPageIndex() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearPageIndex();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public boolean hasGridX() {
                return ((FolderContainer) this.instance).hasGridX();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public int getGridX() {
                return ((FolderContainer) this.instance).getGridX();
            }

            public Builder setGridX(int i) {
                copyOnWrite();
                ((FolderContainer) this.instance).setGridX(i);
                return this;
            }

            public Builder clearGridX() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearGridX();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public boolean hasGridY() {
                return ((FolderContainer) this.instance).hasGridY();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public int getGridY() {
                return ((FolderContainer) this.instance).getGridY();
            }

            public Builder setGridY(int i) {
                copyOnWrite();
                ((FolderContainer) this.instance).setGridY(i);
                return this;
            }

            public Builder clearGridY() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearGridY();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public boolean hasWorkspace() {
                return ((FolderContainer) this.instance).hasWorkspace();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public WorkspaceContainer getWorkspace() {
                return ((FolderContainer) this.instance).getWorkspace();
            }

            public Builder setWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((FolderContainer) this.instance).setWorkspace(workspaceContainer);
                return this;
            }

            public Builder setWorkspace(WorkspaceContainer.Builder builder) {
                copyOnWrite();
                ((FolderContainer) this.instance).setWorkspace(builder);
                return this;
            }

            public Builder mergeWorkspace(WorkspaceContainer workspaceContainer) {
                copyOnWrite();
                ((FolderContainer) this.instance).mergeWorkspace(workspaceContainer);
                return this;
            }

            public Builder clearWorkspace() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearWorkspace();
                return this;
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public boolean hasHotseat() {
                return ((FolderContainer) this.instance).hasHotseat();
            }

            @Override // com.android.launcher3.logger.LauncherAtom.FolderContainerOrBuilder
            public HotseatContainer getHotseat() {
                return ((FolderContainer) this.instance).getHotseat();
            }

            public Builder setHotseat(HotseatContainer hotseatContainer) {
                copyOnWrite();
                ((FolderContainer) this.instance).setHotseat(hotseatContainer);
                return this;
            }

            public Builder setHotseat(HotseatContainer.Builder builder) {
                copyOnWrite();
                ((FolderContainer) this.instance).setHotseat(builder);
                return this;
            }

            public Builder mergeHotseat(HotseatContainer hotseatContainer) {
                copyOnWrite();
                ((FolderContainer) this.instance).mergeHotseat(hotseatContainer);
                return this;
            }

            public Builder clearHotseat() {
                copyOnWrite();
                ((FolderContainer) this.instance).clearHotseat();
                return this;
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new FolderContainer();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0005\u0001\u0001\u0001\u0005\u0005\u0000\u0000\u0000\u0001\u0004\u0000\u0002\u0004\u0001\u0003\u0004\u0002\u0004<\u0000\u0005<\u0000", new Object[]{"parentContainer_", "parentContainerCase_", "bitField0_", "pageIndex_", "gridX_", "gridY_", WorkspaceContainer.class, HotseatContainer.class});
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<FolderContainer> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (FolderContainer.class) {
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
            FolderContainer folderContainer = new FolderContainer();
            DEFAULT_INSTANCE = folderContainer;
            GeneratedMessageLite.registerDefaultInstance(FolderContainer.class, folderContainer);
        }

        public static FolderContainer getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<FolderContainer> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }
}
