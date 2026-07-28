package com.android.launcher3.logger.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public interface LauncherAtom {

    public interface Attribute {
        public static final int ADD_TO_HOMESCREEN = 6;
        public static final int ALLAPPS_ATOZ = 4;
        public static final int ALLAPPS_PREDICTION = 7;
        public static final int BACKUP_RESTORE = 2;
        public static final int DEFAULT_LAYOUT = 1;
        public static final int EMPTY_LABEL = 12;
        public static final int HOTSEAT_PREDICTION = 8;
        public static final int MANUAL_LABEL = 10;
        public static final int PINITEM = 3;
        public static final int SUGGESTED_LABEL = 9;
        public static final int UNKNOWN = 0;
        public static final int UNLABELED = 11;
        public static final int WIDGETS = 5;
    }

    public interface FromState {
        public static final int FROM_CUSTOM = 2;
        public static final int FROM_EMPTY = 1;
        public static final int FROM_STATE_UNSPECIFIED = 0;
        public static final int FROM_SUGGESTED = 3;
    }

    public interface ToState {
        public static final int TO_CUSTOM_WITH_EMPTY_SUGGESTIONS = 15;
        public static final int TO_CUSTOM_WITH_SUGGESTIONS_DISABLED = 16;
        public static final int TO_CUSTOM_WITH_VALID_PRIMARY = 13;
        public static final int TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY = 14;
        public static final int TO_EMPTY_WITH_EMPTY_SUGGESTIONS = 11;
        public static final int TO_EMPTY_WITH_SUGGESTIONS_DISABLED = 12;
        public static final int TO_EMPTY_WITH_VALID_PRIMARY = 9;
        public static final int TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY = 10;
        public static final int TO_STATE_UNSPECIFIED = 0;
        public static final int TO_SUGGESTION0 = 2;
        public static final int TO_SUGGESTION1_WITH_EMPTY_PRIMARY = 4;
        public static final int TO_SUGGESTION1_WITH_VALID_PRIMARY = 3;
        public static final int TO_SUGGESTION2_WITH_EMPTY_PRIMARY = 6;
        public static final int TO_SUGGESTION2_WITH_VALID_PRIMARY = 5;
        public static final int TO_SUGGESTION3_WITH_EMPTY_PRIMARY = 8;
        public static final int TO_SUGGESTION3_WITH_VALID_PRIMARY = 7;
        public static final int UNCHANGED = 1;
    }

    public static final class ItemInfo extends MessageNano {
        public static final int APPLICATION_FIELD_NUMBER = 1;
        public static final int FOLDER_ICON_FIELD_NUMBER = 9;
        public static final int SHORTCUT_FIELD_NUMBER = 3;
        public static final int TASK_FIELD_NUMBER = 2;
        public static final int WIDGET_FIELD_NUMBER = 4;
        private static volatile ItemInfo[] _emptyArray;
        public int attribute;
        public ContainerInfo containerInfo;
        public boolean isWork;
        private int itemCase_ = 0;
        private Object item_;
        public int rank;

        public int getItemCase() {
            return this.itemCase_;
        }

        public ItemInfo clearItem() {
            this.itemCase_ = 0;
            this.item_ = null;
            return this;
        }

        public static ItemInfo[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new ItemInfo[0];
                    }
                }
            }
            return _emptyArray;
        }

        public boolean hasApplication() {
            return this.itemCase_ == 1;
        }

        public Application getApplication() {
            if (this.itemCase_ == 1) {
                return (Application) this.item_;
            }
            return null;
        }

        public ItemInfo setApplication(Application value) {
            Objects.requireNonNull(value);
            this.itemCase_ = 1;
            this.item_ = value;
            return this;
        }

        public boolean hasTask() {
            return this.itemCase_ == 2;
        }

        public Task getTask() {
            if (this.itemCase_ == 2) {
                return (Task) this.item_;
            }
            return null;
        }

        public ItemInfo setTask(Task value) {
            Objects.requireNonNull(value);
            this.itemCase_ = 2;
            this.item_ = value;
            return this;
        }

        public boolean hasShortcut() {
            return this.itemCase_ == 3;
        }

        public Shortcut getShortcut() {
            if (this.itemCase_ == 3) {
                return (Shortcut) this.item_;
            }
            return null;
        }

        public ItemInfo setShortcut(Shortcut value) {
            Objects.requireNonNull(value);
            this.itemCase_ = 3;
            this.item_ = value;
            return this;
        }

        public boolean hasWidget() {
            return this.itemCase_ == 4;
        }

        public Widget getWidget() {
            if (this.itemCase_ == 4) {
                return (Widget) this.item_;
            }
            return null;
        }

        public ItemInfo setWidget(Widget value) {
            Objects.requireNonNull(value);
            this.itemCase_ = 4;
            this.item_ = value;
            return this;
        }

        public boolean hasFolderIcon() {
            return this.itemCase_ == 9;
        }

        public FolderIcon getFolderIcon() {
            if (this.itemCase_ == 9) {
                return (FolderIcon) this.item_;
            }
            return null;
        }

        public ItemInfo setFolderIcon(FolderIcon value) {
            Objects.requireNonNull(value);
            this.itemCase_ = 9;
            this.item_ = value;
            return this;
        }

        public ItemInfo() {
            clear();
        }

        public ItemInfo clear() {
            this.rank = 0;
            this.isWork = false;
            this.containerInfo = null;
            this.attribute = 0;
            clearItem();
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.itemCase_ == 1) {
                output.writeMessage(1, (MessageNano) this.item_);
            }
            if (this.itemCase_ == 2) {
                output.writeMessage(2, (MessageNano) this.item_);
            }
            if (this.itemCase_ == 3) {
                output.writeMessage(3, (MessageNano) this.item_);
            }
            if (this.itemCase_ == 4) {
                output.writeMessage(4, (MessageNano) this.item_);
            }
            int i = this.rank;
            if (i != 0) {
                output.writeInt32(5, i);
            }
            boolean z = this.isWork;
            if (z) {
                output.writeBool(6, z);
            }
            ContainerInfo containerInfo = this.containerInfo;
            if (containerInfo != null) {
                output.writeMessage(7, containerInfo);
            }
            int i2 = this.attribute;
            if (i2 != 0) {
                output.writeInt32(8, i2);
            }
            if (this.itemCase_ == 9) {
                output.writeMessage(9, (MessageNano) this.item_);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            if (this.itemCase_ == 1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, (MessageNano) this.item_);
            }
            if (this.itemCase_ == 2) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, (MessageNano) this.item_);
            }
            if (this.itemCase_ == 3) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, (MessageNano) this.item_);
            }
            if (this.itemCase_ == 4) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, (MessageNano) this.item_);
            }
            int i = this.rank;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i);
            }
            boolean z = this.isWork;
            if (z) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(6, z);
            }
            ContainerInfo containerInfo = this.containerInfo;
            if (containerInfo != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, containerInfo);
            }
            int i2 = this.attribute;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i2);
            }
            return this.itemCase_ == 9 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(9, (MessageNano) this.item_) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public ItemInfo mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    if (this.itemCase_ != 1) {
                        this.item_ = new Application();
                    }
                    input.readMessage((MessageNano) this.item_);
                    this.itemCase_ = 1;
                } else if (tag == 18) {
                    if (this.itemCase_ != 2) {
                        this.item_ = new Task();
                    }
                    input.readMessage((MessageNano) this.item_);
                    this.itemCase_ = 2;
                } else if (tag == 26) {
                    if (this.itemCase_ != 3) {
                        this.item_ = new Shortcut();
                    }
                    input.readMessage((MessageNano) this.item_);
                    this.itemCase_ = 3;
                } else if (tag == 34) {
                    if (this.itemCase_ != 4) {
                        this.item_ = new Widget();
                    }
                    input.readMessage((MessageNano) this.item_);
                    this.itemCase_ = 4;
                } else if (tag == 40) {
                    this.rank = input.readInt32();
                } else if (tag == 48) {
                    this.isWork = input.readBool();
                } else if (tag == 58) {
                    if (this.containerInfo == null) {
                        this.containerInfo = new ContainerInfo();
                    }
                    input.readMessage(this.containerInfo);
                } else if (tag == 64) {
                    int int32 = input.readInt32();
                    switch (int32) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                            this.attribute = int32;
                            break;
                    }
                } else if (tag != 74) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    if (this.itemCase_ != 9) {
                        this.item_ = new FolderIcon();
                    }
                    input.readMessage((MessageNano) this.item_);
                    this.itemCase_ = 9;
                }
            }
        }

        public static ItemInfo parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (ItemInfo) MessageNano.mergeFrom(new ItemInfo(), data);
        }

        public static ItemInfo parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new ItemInfo().mergeFrom(input);
        }
    }

    public static final class ContainerInfo extends MessageNano {
        public static final int ALL_APPS_CONTAINER_FIELD_NUMBER = 4;
        public static final int FOLDER_FIELD_NUMBER = 3;
        public static final int HOTSEAT_FIELD_NUMBER = 2;
        public static final int PREDICTED_HOTSEAT_CONTAINER_FIELD_NUMBER = 10;
        public static final int PREDICTION_CONTAINER_FIELD_NUMBER = 6;
        public static final int SEARCH_RESULT_CONTAINER_FIELD_NUMBER = 7;
        public static final int SETTINGS_CONTAINER_FIELD_NUMBER = 9;
        public static final int SHORTCUTS_CONTAINER_FIELD_NUMBER = 8;
        public static final int TASK_SWITCHER_CONTAINER_FIELD_NUMBER = 11;
        public static final int WIDGETS_CONTAINER_FIELD_NUMBER = 5;
        public static final int WORKSPACE_FIELD_NUMBER = 1;
        private static volatile ContainerInfo[] _emptyArray;
        private int containerCase_ = 0;
        private Object container_;

        public int getContainerCase() {
            return this.containerCase_;
        }

        public ContainerInfo clearContainer() {
            this.containerCase_ = 0;
            this.container_ = null;
            return this;
        }

        public static ContainerInfo[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new ContainerInfo[0];
                    }
                }
            }
            return _emptyArray;
        }

        public boolean hasWorkspace() {
            return this.containerCase_ == 1;
        }

        public WorkspaceContainer getWorkspace() {
            if (this.containerCase_ == 1) {
                return (WorkspaceContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setWorkspace(WorkspaceContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 1;
            this.container_ = value;
            return this;
        }

        public boolean hasHotseat() {
            return this.containerCase_ == 2;
        }

        public HotseatContainer getHotseat() {
            if (this.containerCase_ == 2) {
                return (HotseatContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setHotseat(HotseatContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 2;
            this.container_ = value;
            return this;
        }

        public boolean hasFolder() {
            return this.containerCase_ == 3;
        }

        public FolderContainer getFolder() {
            if (this.containerCase_ == 3) {
                return (FolderContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setFolder(FolderContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 3;
            this.container_ = value;
            return this;
        }

        public boolean hasAllAppsContainer() {
            return this.containerCase_ == 4;
        }

        public AllAppsContainer getAllAppsContainer() {
            if (this.containerCase_ == 4) {
                return (AllAppsContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setAllAppsContainer(AllAppsContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 4;
            this.container_ = value;
            return this;
        }

        public boolean hasWidgetsContainer() {
            return this.containerCase_ == 5;
        }

        public WidgetsContainer getWidgetsContainer() {
            if (this.containerCase_ == 5) {
                return (WidgetsContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setWidgetsContainer(WidgetsContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 5;
            this.container_ = value;
            return this;
        }

        public boolean hasPredictionContainer() {
            return this.containerCase_ == 6;
        }

        public PredictionContainer getPredictionContainer() {
            if (this.containerCase_ == 6) {
                return (PredictionContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setPredictionContainer(PredictionContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 6;
            this.container_ = value;
            return this;
        }

        public boolean hasSearchResultContainer() {
            return this.containerCase_ == 7;
        }

        public SearchResultContainer getSearchResultContainer() {
            if (this.containerCase_ == 7) {
                return (SearchResultContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setSearchResultContainer(SearchResultContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 7;
            this.container_ = value;
            return this;
        }

        public boolean hasShortcutsContainer() {
            return this.containerCase_ == 8;
        }

        public ShortcutsContainer getShortcutsContainer() {
            if (this.containerCase_ == 8) {
                return (ShortcutsContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setShortcutsContainer(ShortcutsContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 8;
            this.container_ = value;
            return this;
        }

        public boolean hasSettingsContainer() {
            return this.containerCase_ == 9;
        }

        public SettingsContainer getSettingsContainer() {
            if (this.containerCase_ == 9) {
                return (SettingsContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setSettingsContainer(SettingsContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 9;
            this.container_ = value;
            return this;
        }

        public boolean hasPredictedHotseatContainer() {
            return this.containerCase_ == 10;
        }

        public PredictedHotseatContainer getPredictedHotseatContainer() {
            if (this.containerCase_ == 10) {
                return (PredictedHotseatContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setPredictedHotseatContainer(PredictedHotseatContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 10;
            this.container_ = value;
            return this;
        }

        public boolean hasTaskSwitcherContainer() {
            return this.containerCase_ == 11;
        }

        public TaskSwitcherContainer getTaskSwitcherContainer() {
            if (this.containerCase_ == 11) {
                return (TaskSwitcherContainer) this.container_;
            }
            return null;
        }

        public ContainerInfo setTaskSwitcherContainer(TaskSwitcherContainer value) {
            Objects.requireNonNull(value);
            this.containerCase_ = 11;
            this.container_ = value;
            return this;
        }

        public ContainerInfo() {
            clear();
        }

        public ContainerInfo clear() {
            clearContainer();
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (this.containerCase_ == 1) {
                output.writeMessage(1, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 2) {
                output.writeMessage(2, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 3) {
                output.writeMessage(3, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 4) {
                output.writeMessage(4, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 5) {
                output.writeMessage(5, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 6) {
                output.writeMessage(6, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 7) {
                output.writeMessage(7, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 8) {
                output.writeMessage(8, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 9) {
                output.writeMessage(9, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 10) {
                output.writeMessage(10, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 11) {
                output.writeMessage(11, (MessageNano) this.container_);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            if (this.containerCase_ == 1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 2) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 3) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 4) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 5) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 6) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 7) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 8) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(8, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 9) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(9, (MessageNano) this.container_);
            }
            if (this.containerCase_ == 10) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(10, (MessageNano) this.container_);
            }
            return this.containerCase_ == 11 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(11, (MessageNano) this.container_) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public ContainerInfo mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 10:
                        if (this.containerCase_ != 1) {
                            this.container_ = new WorkspaceContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 1;
                        break;
                    case 18:
                        if (this.containerCase_ != 2) {
                            this.container_ = new HotseatContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 2;
                        break;
                    case 26:
                        if (this.containerCase_ != 3) {
                            this.container_ = new FolderContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 3;
                        break;
                    case 34:
                        if (this.containerCase_ != 4) {
                            this.container_ = new AllAppsContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 4;
                        break;
                    case 42:
                        if (this.containerCase_ != 5) {
                            this.container_ = new WidgetsContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 5;
                        break;
                    case 50:
                        if (this.containerCase_ != 6) {
                            this.container_ = new PredictionContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 6;
                        break;
                    case 58:
                        if (this.containerCase_ != 7) {
                            this.container_ = new SearchResultContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 7;
                        break;
                    case 66:
                        if (this.containerCase_ != 8) {
                            this.container_ = new ShortcutsContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 8;
                        break;
                    case 74:
                        if (this.containerCase_ != 9) {
                            this.container_ = new SettingsContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 9;
                        break;
                    case 82:
                        if (this.containerCase_ != 10) {
                            this.container_ = new PredictedHotseatContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 10;
                        break;
                    case 90:
                        if (this.containerCase_ != 11) {
                            this.container_ = new TaskSwitcherContainer();
                        }
                        input.readMessage((MessageNano) this.container_);
                        this.containerCase_ = 11;
                        break;
                    default:
                        if (!WireFormatNano.parseUnknownField(input, tag)) {
                            return this;
                        }
                        break;
                        break;
                }
            }
        }

        public static ContainerInfo parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (ContainerInfo) MessageNano.mergeFrom(new ContainerInfo(), data);
        }

        public static ContainerInfo parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new ContainerInfo().mergeFrom(input);
        }
    }

    public static final class AllAppsContainer extends MessageNano {
        private static volatile AllAppsContainer[] _emptyArray;

        public static AllAppsContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new AllAppsContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public AllAppsContainer() {
            clear();
        }

        public AllAppsContainer clear() {
            this.cachedSize = -1;
            return this;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public AllAppsContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            int tag;
            do {
                tag = input.readTag();
                if (tag == 0) {
                    break;
                }
            } while (WireFormatNano.parseUnknownField(input, tag));
            return this;
        }

        public static AllAppsContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (AllAppsContainer) MessageNano.mergeFrom(new AllAppsContainer(), data);
        }

        public static AllAppsContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new AllAppsContainer().mergeFrom(input);
        }
    }

    public static final class WidgetsContainer extends MessageNano {
        private static volatile WidgetsContainer[] _emptyArray;

        public static WidgetsContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new WidgetsContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public WidgetsContainer() {
            clear();
        }

        public WidgetsContainer clear() {
            this.cachedSize = -1;
            return this;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public WidgetsContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            int tag;
            do {
                tag = input.readTag();
                if (tag == 0) {
                    break;
                }
            } while (WireFormatNano.parseUnknownField(input, tag));
            return this;
        }

        public static WidgetsContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (WidgetsContainer) MessageNano.mergeFrom(new WidgetsContainer(), data);
        }

        public static WidgetsContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new WidgetsContainer().mergeFrom(input);
        }
    }

    public static final class PredictionContainer extends MessageNano {
        private static volatile PredictionContainer[] _emptyArray;

        public static PredictionContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new PredictionContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public PredictionContainer() {
            clear();
        }

        public PredictionContainer clear() {
            this.cachedSize = -1;
            return this;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public PredictionContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            int tag;
            do {
                tag = input.readTag();
                if (tag == 0) {
                    break;
                }
            } while (WireFormatNano.parseUnknownField(input, tag));
            return this;
        }

        public static PredictionContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (PredictionContainer) MessageNano.mergeFrom(new PredictionContainer(), data);
        }

        public static PredictionContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new PredictionContainer().mergeFrom(input);
        }
    }

    public static final class SearchResultContainer extends MessageNano {
        public static final int ALL_APPS_CONTAINER_FIELD_NUMBER = 3;
        public static final int WORKSPACE_FIELD_NUMBER = 2;
        private static volatile SearchResultContainer[] _emptyArray;
        private int parentContainerCase_ = 0;
        private Object parentContainer_;
        public int queryLength;

        public int getParentContainerCase() {
            return this.parentContainerCase_;
        }

        public SearchResultContainer clearParentContainer() {
            this.parentContainerCase_ = 0;
            this.parentContainer_ = null;
            return this;
        }

        public static SearchResultContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new SearchResultContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public boolean hasWorkspace() {
            return this.parentContainerCase_ == 2;
        }

        public WorkspaceContainer getWorkspace() {
            if (this.parentContainerCase_ == 2) {
                return (WorkspaceContainer) this.parentContainer_;
            }
            return null;
        }

        public SearchResultContainer setWorkspace(WorkspaceContainer value) {
            Objects.requireNonNull(value);
            this.parentContainerCase_ = 2;
            this.parentContainer_ = value;
            return this;
        }

        public boolean hasAllAppsContainer() {
            return this.parentContainerCase_ == 3;
        }

        public AllAppsContainer getAllAppsContainer() {
            if (this.parentContainerCase_ == 3) {
                return (AllAppsContainer) this.parentContainer_;
            }
            return null;
        }

        public SearchResultContainer setAllAppsContainer(AllAppsContainer value) {
            Objects.requireNonNull(value);
            this.parentContainerCase_ = 3;
            this.parentContainer_ = value;
            return this;
        }

        public SearchResultContainer() {
            clear();
        }

        public SearchResultContainer clear() {
            this.queryLength = 0;
            clearParentContainer();
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.queryLength;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            if (this.parentContainerCase_ == 2) {
                output.writeMessage(2, (MessageNano) this.parentContainer_);
            }
            if (this.parentContainerCase_ == 3) {
                output.writeMessage(3, (MessageNano) this.parentContainer_);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.queryLength;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            if (this.parentContainerCase_ == 2) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, (MessageNano) this.parentContainer_);
            }
            return this.parentContainerCase_ == 3 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(3, (MessageNano) this.parentContainer_) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public SearchResultContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.queryLength = input.readInt32();
                } else if (tag == 18) {
                    if (this.parentContainerCase_ != 2) {
                        this.parentContainer_ = new WorkspaceContainer();
                    }
                    input.readMessage((MessageNano) this.parentContainer_);
                    this.parentContainerCase_ = 2;
                } else if (tag != 26) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    if (this.parentContainerCase_ != 3) {
                        this.parentContainer_ = new AllAppsContainer();
                    }
                    input.readMessage((MessageNano) this.parentContainer_);
                    this.parentContainerCase_ = 3;
                }
            }
        }

        public static SearchResultContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (SearchResultContainer) MessageNano.mergeFrom(new SearchResultContainer(), data);
        }

        public static SearchResultContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new SearchResultContainer().mergeFrom(input);
        }
    }

    public static final class ShortcutsContainer extends MessageNano {
        private static volatile ShortcutsContainer[] _emptyArray;

        public static ShortcutsContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new ShortcutsContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public ShortcutsContainer() {
            clear();
        }

        public ShortcutsContainer clear() {
            this.cachedSize = -1;
            return this;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public ShortcutsContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            int tag;
            do {
                tag = input.readTag();
                if (tag == 0) {
                    break;
                }
            } while (WireFormatNano.parseUnknownField(input, tag));
            return this;
        }

        public static ShortcutsContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (ShortcutsContainer) MessageNano.mergeFrom(new ShortcutsContainer(), data);
        }

        public static ShortcutsContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new ShortcutsContainer().mergeFrom(input);
        }
    }

    public static final class SettingsContainer extends MessageNano {
        private static volatile SettingsContainer[] _emptyArray;

        public static SettingsContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new SettingsContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public SettingsContainer() {
            clear();
        }

        public SettingsContainer clear() {
            this.cachedSize = -1;
            return this;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public SettingsContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            int tag;
            do {
                tag = input.readTag();
                if (tag == 0) {
                    break;
                }
            } while (WireFormatNano.parseUnknownField(input, tag));
            return this;
        }

        public static SettingsContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (SettingsContainer) MessageNano.mergeFrom(new SettingsContainer(), data);
        }

        public static SettingsContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new SettingsContainer().mergeFrom(input);
        }
    }

    public static final class TaskSwitcherContainer extends MessageNano {
        private static volatile TaskSwitcherContainer[] _emptyArray;

        public static TaskSwitcherContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new TaskSwitcherContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public TaskSwitcherContainer() {
            clear();
        }

        public TaskSwitcherContainer clear() {
            this.cachedSize = -1;
            return this;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public TaskSwitcherContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            int tag;
            do {
                tag = input.readTag();
                if (tag == 0) {
                    break;
                }
            } while (WireFormatNano.parseUnknownField(input, tag));
            return this;
        }

        public static TaskSwitcherContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (TaskSwitcherContainer) MessageNano.mergeFrom(new TaskSwitcherContainer(), data);
        }

        public static TaskSwitcherContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new TaskSwitcherContainer().mergeFrom(input);
        }
    }

    public static final class Application extends MessageNano {
        private static volatile Application[] _emptyArray;
        public String componentName;
        public String packageName;

        public static Application[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Application[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Application() {
            clear();
        }

        public Application clear() {
            this.packageName = "";
            this.componentName = "";
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.packageName.equals("")) {
                output.writeString(1, this.packageName);
            }
            if (!this.componentName.equals("")) {
                output.writeString(2, this.componentName);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            if (!this.packageName.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.packageName);
            }
            return !this.componentName.equals("") ? iComputeSerializedSize + CodedOutputByteBufferNano.computeStringSize(2, this.componentName) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Application mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    this.packageName = input.readString();
                } else if (tag != 18) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.componentName = input.readString();
                }
            }
        }

        public static Application parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Application) MessageNano.mergeFrom(new Application(), data);
        }

        public static Application parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Application().mergeFrom(input);
        }
    }

    public static final class Shortcut extends MessageNano {
        private static volatile Shortcut[] _emptyArray;
        public String shortcutName;

        public static Shortcut[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Shortcut[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Shortcut() {
            clear();
        }

        public Shortcut clear() {
            this.shortcutName = "";
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.shortcutName.equals("")) {
                output.writeString(1, this.shortcutName);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            return !this.shortcutName.equals("") ? iComputeSerializedSize + CodedOutputByteBufferNano.computeStringSize(1, this.shortcutName) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Shortcut mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag != 10) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.shortcutName = input.readString();
                }
            }
        }

        public static Shortcut parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Shortcut) MessageNano.mergeFrom(new Shortcut(), data);
        }

        public static Shortcut parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Shortcut().mergeFrom(input);
        }
    }

    public static final class Widget extends MessageNano {
        private static volatile Widget[] _emptyArray;
        public int appWidgetId;
        public String componentName;
        public String packageName;
        public int spanX;
        public int spanY;

        public static Widget[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Widget[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Widget() {
            clear();
        }

        public Widget clear() {
            this.spanX = 1;
            this.spanY = 1;
            this.appWidgetId = 0;
            this.packageName = "";
            this.componentName = "";
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.spanX;
            if (i != 1) {
                output.writeInt32(1, i);
            }
            int i2 = this.spanY;
            if (i2 != 1) {
                output.writeInt32(2, i2);
            }
            int i3 = this.appWidgetId;
            if (i3 != 0) {
                output.writeInt32(3, i3);
            }
            if (!this.packageName.equals("")) {
                output.writeString(4, this.packageName);
            }
            if (!this.componentName.equals("")) {
                output.writeString(5, this.componentName);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.spanX;
            if (i != 1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.spanY;
            if (i2 != 1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.appWidgetId;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            if (!this.packageName.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(4, this.packageName);
            }
            return !this.componentName.equals("") ? iComputeSerializedSize + CodedOutputByteBufferNano.computeStringSize(5, this.componentName) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Widget mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.spanX = input.readInt32();
                } else if (tag == 16) {
                    this.spanY = input.readInt32();
                } else if (tag == 24) {
                    this.appWidgetId = input.readInt32();
                } else if (tag == 34) {
                    this.packageName = input.readString();
                } else if (tag != 42) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.componentName = input.readString();
                }
            }
        }

        public static Widget parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Widget) MessageNano.mergeFrom(new Widget(), data);
        }

        public static Widget parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Widget().mergeFrom(input);
        }
    }

    public static final class Task extends MessageNano {
        private static volatile Task[] _emptyArray;
        public String componentName;
        public int index;
        public String packageName;

        public static Task[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Task[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Task() {
            clear();
        }

        public Task clear() {
            this.packageName = "";
            this.componentName = "";
            this.index = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (!this.packageName.equals("")) {
                output.writeString(1, this.packageName);
            }
            if (!this.componentName.equals("")) {
                output.writeString(2, this.componentName);
            }
            int i = this.index;
            if (i != 0) {
                output.writeInt32(3, i);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            if (!this.packageName.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.packageName);
            }
            if (!this.componentName.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.componentName);
            }
            int i = this.index;
            return i != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, i) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Task mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    this.packageName = input.readString();
                } else if (tag == 18) {
                    this.componentName = input.readString();
                } else if (tag != 24) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.index = input.readInt32();
                }
            }
        }

        public static Task parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Task) MessageNano.mergeFrom(new Task(), data);
        }

        public static Task parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Task().mergeFrom(input);
        }
    }

    public static final class FolderIcon extends MessageNano {
        private static volatile FolderIcon[] _emptyArray;
        public int cardinality;
        public int fromLabelState;
        public String labelInfo;
        public int toLabelState;

        public static FolderIcon[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new FolderIcon[0];
                    }
                }
            }
            return _emptyArray;
        }

        public FolderIcon() {
            clear();
        }

        public FolderIcon clear() {
            this.cardinality = 0;
            this.fromLabelState = 0;
            this.toLabelState = 0;
            this.labelInfo = "";
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.cardinality;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            int i2 = this.fromLabelState;
            if (i2 != 0) {
                output.writeInt32(2, i2);
            }
            int i3 = this.toLabelState;
            if (i3 != 0) {
                output.writeInt32(3, i3);
            }
            if (!this.labelInfo.equals("")) {
                output.writeString(4, this.labelInfo);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.cardinality;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.fromLabelState;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.toLabelState;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            return !this.labelInfo.equals("") ? iComputeSerializedSize + CodedOutputByteBufferNano.computeStringSize(4, this.labelInfo) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public FolderIcon mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.cardinality = input.readInt32();
                } else if (tag == 16) {
                    int int32 = input.readInt32();
                    if (int32 == 0 || int32 == 1 || int32 == 2 || int32 == 3) {
                        this.fromLabelState = int32;
                    }
                } else if (tag == 24) {
                    int int322 = input.readInt32();
                    switch (int322) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 16:
                            this.toLabelState = int322;
                            break;
                    }
                } else if (tag != 34) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.labelInfo = input.readString();
                }
            }
        }

        public static FolderIcon parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (FolderIcon) MessageNano.mergeFrom(new FolderIcon(), data);
        }

        public static FolderIcon parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new FolderIcon().mergeFrom(input);
        }
    }

    public static final class WorkspaceContainer extends MessageNano {
        private static volatile WorkspaceContainer[] _emptyArray;
        public int gridX;
        public int gridY;
        public int pageIndex;

        public static WorkspaceContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new WorkspaceContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public WorkspaceContainer() {
            clear();
        }

        public WorkspaceContainer clear() {
            this.pageIndex = -2;
            this.gridX = -1;
            this.gridY = -1;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.pageIndex;
            if (i != -2) {
                output.writeInt32(1, i);
            }
            int i2 = this.gridX;
            if (i2 != -1) {
                output.writeInt32(2, i2);
            }
            int i3 = this.gridY;
            if (i3 != -1) {
                output.writeInt32(3, i3);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.pageIndex;
            if (i != -2) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.gridX;
            if (i2 != -1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.gridY;
            return i3 != -1 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(3, i3) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public WorkspaceContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.pageIndex = input.readInt32();
                } else if (tag == 16) {
                    this.gridX = input.readInt32();
                } else if (tag != 24) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.gridY = input.readInt32();
                }
            }
        }

        public static WorkspaceContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (WorkspaceContainer) MessageNano.mergeFrom(new WorkspaceContainer(), data);
        }

        public static WorkspaceContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new WorkspaceContainer().mergeFrom(input);
        }
    }

    public static final class HotseatContainer extends MessageNano {
        private static volatile HotseatContainer[] _emptyArray;
        public int index;

        public static HotseatContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new HotseatContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public HotseatContainer() {
            clear();
        }

        public HotseatContainer clear() {
            this.index = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.index;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.index;
            return i != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(1, i) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public HotseatContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag != 8) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.index = input.readInt32();
                }
            }
        }

        public static HotseatContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (HotseatContainer) MessageNano.mergeFrom(new HotseatContainer(), data);
        }

        public static HotseatContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new HotseatContainer().mergeFrom(input);
        }
    }

    public static final class PredictedHotseatContainer extends MessageNano {
        private static volatile PredictedHotseatContainer[] _emptyArray;
        public int cardinality;
        public int index;

        public static PredictedHotseatContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new PredictedHotseatContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public PredictedHotseatContainer() {
            clear();
        }

        public PredictedHotseatContainer clear() {
            this.index = 0;
            this.cardinality = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.index;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            int i2 = this.cardinality;
            if (i2 != 0) {
                output.writeInt32(2, i2);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.index;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.cardinality;
            return i2 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i2) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public PredictedHotseatContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.index = input.readInt32();
                } else if (tag != 16) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.cardinality = input.readInt32();
                }
            }
        }

        public static PredictedHotseatContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (PredictedHotseatContainer) MessageNano.mergeFrom(new PredictedHotseatContainer(), data);
        }

        public static PredictedHotseatContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new PredictedHotseatContainer().mergeFrom(input);
        }
    }

    public static final class FolderContainer extends MessageNano {
        public static final int HOTSEAT_FIELD_NUMBER = 5;
        public static final int WORKSPACE_FIELD_NUMBER = 4;
        private static volatile FolderContainer[] _emptyArray;
        public int gridX;
        public int gridY;
        public int pageIndex;
        private int parentContainerCase_ = 0;
        private Object parentContainer_;

        public int getParentContainerCase() {
            return this.parentContainerCase_;
        }

        public FolderContainer clearParentContainer() {
            this.parentContainerCase_ = 0;
            this.parentContainer_ = null;
            return this;
        }

        public static FolderContainer[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new FolderContainer[0];
                    }
                }
            }
            return _emptyArray;
        }

        public boolean hasWorkspace() {
            return this.parentContainerCase_ == 4;
        }

        public WorkspaceContainer getWorkspace() {
            if (this.parentContainerCase_ == 4) {
                return (WorkspaceContainer) this.parentContainer_;
            }
            return null;
        }

        public FolderContainer setWorkspace(WorkspaceContainer value) {
            Objects.requireNonNull(value);
            this.parentContainerCase_ = 4;
            this.parentContainer_ = value;
            return this;
        }

        public boolean hasHotseat() {
            return this.parentContainerCase_ == 5;
        }

        public HotseatContainer getHotseat() {
            if (this.parentContainerCase_ == 5) {
                return (HotseatContainer) this.parentContainer_;
            }
            return null;
        }

        public FolderContainer setHotseat(HotseatContainer value) {
            Objects.requireNonNull(value);
            this.parentContainerCase_ = 5;
            this.parentContainer_ = value;
            return this;
        }

        public FolderContainer() {
            clear();
        }

        public FolderContainer clear() {
            this.pageIndex = -1;
            this.gridX = -1;
            this.gridY = -1;
            clearParentContainer();
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.pageIndex;
            if (i != -1) {
                output.writeInt32(1, i);
            }
            int i2 = this.gridX;
            if (i2 != -1) {
                output.writeInt32(2, i2);
            }
            int i3 = this.gridY;
            if (i3 != -1) {
                output.writeInt32(3, i3);
            }
            if (this.parentContainerCase_ == 4) {
                output.writeMessage(4, (MessageNano) this.parentContainer_);
            }
            if (this.parentContainerCase_ == 5) {
                output.writeMessage(5, (MessageNano) this.parentContainer_);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.pageIndex;
            if (i != -1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.gridX;
            if (i2 != -1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.gridY;
            if (i3 != -1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            if (this.parentContainerCase_ == 4) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, (MessageNano) this.parentContainer_);
            }
            return this.parentContainerCase_ == 5 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(5, (MessageNano) this.parentContainer_) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public FolderContainer mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.pageIndex = input.readInt32();
                } else if (tag == 16) {
                    this.gridX = input.readInt32();
                } else if (tag == 24) {
                    this.gridY = input.readInt32();
                } else if (tag == 34) {
                    if (this.parentContainerCase_ != 4) {
                        this.parentContainer_ = new WorkspaceContainer();
                    }
                    input.readMessage((MessageNano) this.parentContainer_);
                    this.parentContainerCase_ = 4;
                } else if (tag != 42) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    if (this.parentContainerCase_ != 5) {
                        this.parentContainer_ = new HotseatContainer();
                    }
                    input.readMessage((MessageNano) this.parentContainer_);
                    this.parentContainerCase_ = 5;
                }
            }
        }

        public static FolderContainer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (FolderContainer) MessageNano.mergeFrom(new FolderContainer(), data);
        }

        public static FolderContainer parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new FolderContainer().mergeFrom(input);
        }
    }
}
