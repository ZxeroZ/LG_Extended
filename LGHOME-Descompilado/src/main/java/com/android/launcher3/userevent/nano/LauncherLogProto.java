package com.android.launcher3.userevent.nano;

import com.android.launcher3.userevent.nano.LauncherLogExtensions;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public interface LauncherLogProto {

    public interface ContainerType {
        public static final int ALLAPPS = 4;
        public static final int APP = 13;
        public static final int DEEPSHORTCUTS = 9;
        public static final int DEFAULT_CONTAINERTYPE = 0;
        public static final int FOLDER = 3;
        public static final int HOTSEAT = 2;
        public static final int NAVBAR = 11;
        public static final int OTHER_LAUNCHER_APP = 15;
        public static final int OVERVIEW = 6;
        public static final int PINITEM = 10;
        public static final int PREDICTION = 7;
        public static final int SEARCHRESULT = 8;
        public static final int TASKSWITCHER = 12;
        public static final int TIP = 14;
        public static final int WIDGETS = 5;
        public static final int WORKSPACE = 1;
    }

    public interface ControlType {
        public static final int ALL_APPS_BUTTON = 1;
        public static final int APPINFO_TARGET = 7;
        public static final int APP_USAGE_SETTINGS = 18;
        public static final int BACK_BUTTON = 11;
        public static final int BACK_GESTURE = 19;
        public static final int CANCEL_TARGET = 14;
        public static final int CLEAR_ALL_BUTTON = 13;
        public static final int DEFAULT_CONTROLTYPE = 0;
        public static final int DISMISS_PREDICTION = 21;
        public static final int HOME_INTENT = 10;
        public static final int HYBRID_HOTSEAT_ACCEPTED = 22;
        public static final int HYBRID_HOTSEAT_CANCELED = 23;
        public static final int OVERVIEW_ACTIONS_SCREENSHOT_BUTTON = 25;
        public static final int OVERVIEW_ACTIONS_SELECT_BUTTON = 26;
        public static final int OVERVIEW_ACTIONS_SHARE_BUTTON = 24;
        public static final int QUICK_SCRUB_BUTTON = 12;
        public static final int REMOTE_ACTION_SHORTCUT = 17;
        public static final int REMOVE_TARGET = 5;
        public static final int RESIZE_HANDLE = 8;
        public static final int SELECT_MODE_CLOSE_BUTTON = 27;
        public static final int SELECT_MODE_ITEM = 28;
        public static final int SETTINGS_BUTTON = 4;
        public static final int SPLIT_SCREEN_TARGET = 16;
        public static final int TASK_PREVIEW = 15;
        public static final int UNDO = 20;
        public static final int UNINSTALL_TARGET = 6;
        public static final int VERTICAL_SCROLL = 9;
        public static final int WALLPAPER_BUTTON = 3;
        public static final int WIDGETS_BUTTON = 2;
    }

    public interface ItemType {
        public static final int APP_ICON = 1;
        public static final int DEEPSHORTCUT = 5;
        public static final int DEFAULT_ITEMTYPE = 0;
        public static final int EDITTEXT = 7;
        public static final int FOLDER_ICON = 4;
        public static final int NOTIFICATION = 8;
        public static final int SEARCHBOX = 6;
        public static final int SHORTCUT = 2;
        public static final int TASK = 9;
        public static final int TASK_ICON = 11;
        public static final int WEB_APP = 10;
        public static final int WIDGET = 3;
    }

    public interface TipType {
        public static final int BOUNCE = 1;
        public static final int DEFAULT_NONE = 0;
        public static final int DWB_TOAST = 5;
        public static final int HYBRID_HOTSEAT = 6;
        public static final int PREDICTION_TEXT = 4;
        public static final int QUICK_SCRUB_TEXT = 3;
        public static final int SWIPE_UP_TEXT = 2;
    }

    public static final class Target extends MessageNano {
        private static volatile Target[] _emptyArray;
        public int cardinality;
        public int componentHash;
        public int containerType;
        public int controlType;
        public LauncherLogExtensions.TargetExtension extension;
        public int fromFolderLabelState;
        public int gridX;
        public int gridY;
        public int intentHash;
        public boolean isWorkApp;
        public int itemType;
        public int packageNameHash;
        public int pageIndex;
        public int predictedRank;
        public int rank;
        public int searchQueryLength;
        public int spanX;
        public int spanY;
        public int tipType;
        public int toFolderLabelState;
        public int type;

        public interface FromFolderLabelState {
            public static final int FROM_CUSTOM = 2;
            public static final int FROM_EMPTY = 1;
            public static final int FROM_FOLDER_LABEL_STATE_UNSPECIFIED = 0;
            public static final int FROM_SUGGESTED = 3;
        }

        public interface ToFolderLabelState {
            public static final int TO_CUSTOM_WITH_EMPTY_SUGGESTIONS = 12;
            public static final int TO_CUSTOM_WITH_SUGGESTIONS_DISABLED = 13;
            public static final int TO_CUSTOM_WITH_VALID_PRIMARY = 17;
            public static final int TO_CUSTOM_WITH_VALID_SUGGESTIONS = 11;
            public static final int TO_CUSTOM_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY = 18;
            public static final int TO_EMPTY_WITH_EMPTY_SUGGESTIONS = 9;
            public static final int TO_EMPTY_WITH_SUGGESTIONS_DISABLED = 10;
            public static final int TO_EMPTY_WITH_VALID_PRIMARY = 15;
            public static final int TO_EMPTY_WITH_VALID_SUGGESTIONS = 8;
            public static final int TO_EMPTY_WITH_VALID_SUGGESTIONS_AND_EMPTY_PRIMARY = 16;
            public static final int TO_FOLDER_LABEL_STATE_UNSPECIFIED = 0;
            public static final int TO_SUGGESTION0_WITH_VALID_PRIMARY = 1;
            public static final int TO_SUGGESTION1_WITH_EMPTY_PRIMARY = 3;
            public static final int TO_SUGGESTION1_WITH_VALID_PRIMARY = 2;
            public static final int TO_SUGGESTION2_WITH_EMPTY_PRIMARY = 5;
            public static final int TO_SUGGESTION2_WITH_VALID_PRIMARY = 4;
            public static final int TO_SUGGESTION3_WITH_EMPTY_PRIMARY = 7;
            public static final int TO_SUGGESTION3_WITH_VALID_PRIMARY = 6;
            public static final int UNCHANGED = 14;
        }

        public interface Type {
            public static final int CONTAINER = 3;
            public static final int CONTROL = 2;
            public static final int ITEM = 1;
            public static final int NONE = 0;
        }

        public static Target[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Target[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Target() {
            clear();
        }

        public Target clear() {
            this.type = 0;
            this.pageIndex = 0;
            this.rank = 0;
            this.gridX = 0;
            this.gridY = 0;
            this.containerType = 0;
            this.cardinality = 0;
            this.controlType = 0;
            this.itemType = 0;
            this.packageNameHash = 0;
            this.componentHash = 0;
            this.intentHash = 0;
            this.spanX = 1;
            this.spanY = 1;
            this.predictedRank = 0;
            this.extension = null;
            this.tipType = 0;
            this.searchQueryLength = 0;
            this.isWorkApp = false;
            this.fromFolderLabelState = 0;
            this.toFolderLabelState = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.type;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            int i2 = this.pageIndex;
            if (i2 != 0) {
                output.writeInt32(2, i2);
            }
            int i3 = this.rank;
            if (i3 != 0) {
                output.writeInt32(3, i3);
            }
            int i4 = this.gridX;
            if (i4 != 0) {
                output.writeInt32(4, i4);
            }
            int i5 = this.gridY;
            if (i5 != 0) {
                output.writeInt32(5, i5);
            }
            int i6 = this.containerType;
            if (i6 != 0) {
                output.writeInt32(6, i6);
            }
            int i7 = this.cardinality;
            if (i7 != 0) {
                output.writeInt32(7, i7);
            }
            int i8 = this.controlType;
            if (i8 != 0) {
                output.writeInt32(8, i8);
            }
            int i9 = this.itemType;
            if (i9 != 0) {
                output.writeInt32(9, i9);
            }
            int i10 = this.packageNameHash;
            if (i10 != 0) {
                output.writeInt32(10, i10);
            }
            int i11 = this.componentHash;
            if (i11 != 0) {
                output.writeInt32(11, i11);
            }
            int i12 = this.intentHash;
            if (i12 != 0) {
                output.writeInt32(12, i12);
            }
            int i13 = this.spanX;
            if (i13 != 1) {
                output.writeInt32(13, i13);
            }
            int i14 = this.spanY;
            if (i14 != 1) {
                output.writeInt32(14, i14);
            }
            int i15 = this.predictedRank;
            if (i15 != 0) {
                output.writeInt32(15, i15);
            }
            LauncherLogExtensions.TargetExtension targetExtension = this.extension;
            if (targetExtension != null) {
                output.writeMessage(16, targetExtension);
            }
            int i16 = this.tipType;
            if (i16 != 0) {
                output.writeInt32(17, i16);
            }
            int i17 = this.searchQueryLength;
            if (i17 != 0) {
                output.writeInt32(18, i17);
            }
            boolean z = this.isWorkApp;
            if (z) {
                output.writeBool(19, z);
            }
            int i18 = this.fromFolderLabelState;
            if (i18 != 0) {
                output.writeInt32(20, i18);
            }
            int i19 = this.toFolderLabelState;
            if (i19 != 0) {
                output.writeInt32(21, i19);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.type;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.pageIndex;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.rank;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.gridX;
            if (i4 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
            }
            int i5 = this.gridY;
            if (i5 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i5);
            }
            int i6 = this.containerType;
            if (i6 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i6);
            }
            int i7 = this.cardinality;
            if (i7 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i7);
            }
            int i8 = this.controlType;
            if (i8 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i8);
            }
            int i9 = this.itemType;
            if (i9 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i9);
            }
            int i10 = this.packageNameHash;
            if (i10 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i10);
            }
            int i11 = this.componentHash;
            if (i11 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, i11);
            }
            int i12 = this.intentHash;
            if (i12 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(12, i12);
            }
            int i13 = this.spanX;
            if (i13 != 1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, i13);
            }
            int i14 = this.spanY;
            if (i14 != 1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(14, i14);
            }
            int i15 = this.predictedRank;
            if (i15 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(15, i15);
            }
            LauncherLogExtensions.TargetExtension targetExtension = this.extension;
            if (targetExtension != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(16, targetExtension);
            }
            int i16 = this.tipType;
            if (i16 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(17, i16);
            }
            int i17 = this.searchQueryLength;
            if (i17 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(18, i17);
            }
            boolean z = this.isWorkApp;
            if (z) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(19, z);
            }
            int i18 = this.fromFolderLabelState;
            if (i18 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(20, i18);
            }
            int i19 = this.toFolderLabelState;
            return i19 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(21, i19) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Target mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        int int32 = input.readInt32();
                        if (int32 == 0 || int32 == 1 || int32 == 2 || int32 == 3) {
                            this.type = int32;
                        }
                        break;
                    case 16:
                        this.pageIndex = input.readInt32();
                        break;
                    case 24:
                        this.rank = input.readInt32();
                        break;
                    case 32:
                        this.gridX = input.readInt32();
                        break;
                    case 40:
                        this.gridY = input.readInt32();
                        break;
                    case 48:
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
                                this.containerType = int322;
                                break;
                        }
                        break;
                    case 56:
                        this.cardinality = input.readInt32();
                        break;
                    case 64:
                        int int323 = input.readInt32();
                        switch (int323) {
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
                            case 17:
                            case 18:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 27:
                            case 28:
                                this.controlType = int323;
                                break;
                        }
                        break;
                    case 72:
                        int int324 = input.readInt32();
                        switch (int324) {
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
                                this.itemType = int324;
                                break;
                        }
                        break;
                    case 80:
                        this.packageNameHash = input.readInt32();
                        break;
                    case 88:
                        this.componentHash = input.readInt32();
                        break;
                    case 96:
                        this.intentHash = input.readInt32();
                        break;
                    case 104:
                        this.spanX = input.readInt32();
                        break;
                    case 112:
                        this.spanY = input.readInt32();
                        break;
                    case 120:
                        this.predictedRank = input.readInt32();
                        break;
                    case 130:
                        if (this.extension == null) {
                            this.extension = new LauncherLogExtensions.TargetExtension();
                        }
                        input.readMessage(this.extension);
                        break;
                    case 136:
                        int int325 = input.readInt32();
                        switch (int325) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.tipType = int325;
                                break;
                        }
                        break;
                    case 144:
                        this.searchQueryLength = input.readInt32();
                        break;
                    case 152:
                        this.isWorkApp = input.readBool();
                        break;
                    case 160:
                        int int326 = input.readInt32();
                        if (int326 == 0 || int326 == 1 || int326 == 2 || int326 == 3) {
                            this.fromFolderLabelState = int326;
                        }
                        break;
                    case 168:
                        int int327 = input.readInt32();
                        switch (int327) {
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
                            case 17:
                            case 18:
                                this.toFolderLabelState = int327;
                                break;
                        }
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

        public static Target parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Target) MessageNano.mergeFrom(new Target(), data);
        }

        public static Target parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Target().mergeFrom(input);
        }
    }

    public static final class Action extends MessageNano {
        private static volatile Action[] _emptyArray;
        public int command;
        public int dir;
        public boolean isOutside;
        public boolean isStateChange;
        public int touch;
        public int type;

        public interface Command {
            public static final int BACK = 1;
            public static final int CANCEL = 3;
            public static final int CONFIRM = 4;
            public static final int ENTRY = 2;
            public static final int HOME_INTENT = 0;
            public static final int RECENTS_BUTTON = 6;
            public static final int RESUME = 7;
            public static final int STOP = 5;
        }

        public interface Direction {
            public static final int DOWN = 2;
            public static final int LEFT = 3;
            public static final int NONE = 0;
            public static final int RIGHT = 4;
            public static final int UP = 1;
            public static final int UPLEFT = 6;
            public static final int UPRIGHT = 5;
        }

        public interface Touch {
            public static final int DRAGDROP = 2;
            public static final int FLING = 4;
            public static final int LONGPRESS = 1;
            public static final int PINCH = 5;
            public static final int SWIPE = 3;
            public static final int SWIPE_NOOP = 6;
            public static final int TAP = 0;
        }

        public interface Type {
            public static final int AUTOMATED = 1;
            public static final int COMMAND = 2;
            public static final int SOFT_KEYBOARD = 4;
            public static final int TIP = 3;
            public static final int TOUCH = 0;
        }

        public static Action[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Action[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Action() {
            clear();
        }

        public Action clear() {
            this.type = 0;
            this.touch = 0;
            this.dir = 0;
            this.command = 0;
            this.isOutside = false;
            this.isStateChange = false;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.type;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            int i2 = this.touch;
            if (i2 != 0) {
                output.writeInt32(2, i2);
            }
            int i3 = this.dir;
            if (i3 != 0) {
                output.writeInt32(3, i3);
            }
            int i4 = this.command;
            if (i4 != 0) {
                output.writeInt32(4, i4);
            }
            boolean z = this.isOutside;
            if (z) {
                output.writeBool(5, z);
            }
            boolean z2 = this.isStateChange;
            if (z2) {
                output.writeBool(6, z2);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.type;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.touch;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.dir;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.command;
            if (i4 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
            }
            boolean z = this.isOutside;
            if (z) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z);
            }
            boolean z2 = this.isStateChange;
            return z2 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(6, z2) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Action mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    int int32 = input.readInt32();
                    if (int32 == 0 || int32 == 1 || int32 == 2 || int32 == 3 || int32 == 4) {
                        this.type = int32;
                    }
                } else if (tag == 16) {
                    int int322 = input.readInt32();
                    switch (int322) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            this.touch = int322;
                            break;
                    }
                } else if (tag == 24) {
                    int int323 = input.readInt32();
                    switch (int323) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            this.dir = int323;
                            break;
                    }
                } else if (tag == 32) {
                    int int324 = input.readInt32();
                    switch (int324) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            this.command = int324;
                            break;
                    }
                } else if (tag == 40) {
                    this.isOutside = input.readBool();
                } else if (tag != 48) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.isStateChange = input.readBool();
                }
            }
        }

        public static Action parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Action) MessageNano.mergeFrom(new Action(), data);
        }

        public static Action parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Action().mergeFrom(input);
        }
    }

    public static final class LauncherEvent extends MessageNano {
        private static volatile LauncherEvent[] _emptyArray;
        public Action action;
        public long actionDurationMillis;
        public Target[] destTarget;
        public long elapsedContainerMillis;
        public long elapsedSessionMillis;
        public LauncherLogExtensions.LauncherEventExtension extension;
        public boolean isInLandscapeMode;
        public boolean isInMultiWindowMode;
        public Target[] srcTarget;

        public static LauncherEvent[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new LauncherEvent[0];
                    }
                }
            }
            return _emptyArray;
        }

        public LauncherEvent() {
            clear();
        }

        public LauncherEvent clear() {
            this.action = null;
            this.srcTarget = Target.emptyArray();
            this.destTarget = Target.emptyArray();
            this.actionDurationMillis = 0L;
            this.elapsedContainerMillis = 0L;
            this.elapsedSessionMillis = 0L;
            this.isInMultiWindowMode = false;
            this.isInLandscapeMode = false;
            this.extension = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            Action action = this.action;
            if (action != null) {
                output.writeMessage(1, action);
            }
            Target[] targetArr = this.srcTarget;
            int i = 0;
            if (targetArr != null && targetArr.length > 0) {
                int i2 = 0;
                while (true) {
                    Target[] targetArr2 = this.srcTarget;
                    if (i2 >= targetArr2.length) {
                        break;
                    }
                    Target target = targetArr2[i2];
                    if (target != null) {
                        output.writeMessage(2, target);
                    }
                    i2++;
                }
            }
            Target[] targetArr3 = this.destTarget;
            if (targetArr3 != null && targetArr3.length > 0) {
                while (true) {
                    Target[] targetArr4 = this.destTarget;
                    if (i >= targetArr4.length) {
                        break;
                    }
                    Target target2 = targetArr4[i];
                    if (target2 != null) {
                        output.writeMessage(3, target2);
                    }
                    i++;
                }
            }
            long j = this.actionDurationMillis;
            if (j != 0) {
                output.writeInt64(4, j);
            }
            long j2 = this.elapsedContainerMillis;
            if (j2 != 0) {
                output.writeInt64(5, j2);
            }
            long j3 = this.elapsedSessionMillis;
            if (j3 != 0) {
                output.writeInt64(6, j3);
            }
            boolean z = this.isInMultiWindowMode;
            if (z) {
                output.writeBool(7, z);
            }
            boolean z2 = this.isInLandscapeMode;
            if (z2) {
                output.writeBool(8, z2);
            }
            LauncherLogExtensions.LauncherEventExtension launcherEventExtension = this.extension;
            if (launcherEventExtension != null) {
                output.writeMessage(9, launcherEventExtension);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            Action action = this.action;
            if (action != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, action);
            }
            Target[] targetArr = this.srcTarget;
            int i = 0;
            if (targetArr != null && targetArr.length > 0) {
                int i2 = 0;
                while (true) {
                    Target[] targetArr2 = this.srcTarget;
                    if (i2 >= targetArr2.length) {
                        break;
                    }
                    Target target = targetArr2[i2];
                    if (target != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, target);
                    }
                    i2++;
                }
            }
            Target[] targetArr3 = this.destTarget;
            if (targetArr3 != null && targetArr3.length > 0) {
                while (true) {
                    Target[] targetArr4 = this.destTarget;
                    if (i >= targetArr4.length) {
                        break;
                    }
                    Target target2 = targetArr4[i];
                    if (target2 != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, target2);
                    }
                    i++;
                }
            }
            long j = this.actionDurationMillis;
            if (j != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(4, j);
            }
            long j2 = this.elapsedContainerMillis;
            if (j2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(5, j2);
            }
            long j3 = this.elapsedSessionMillis;
            if (j3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(6, j3);
            }
            boolean z = this.isInMultiWindowMode;
            if (z) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z);
            }
            boolean z2 = this.isInLandscapeMode;
            if (z2) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(8, z2);
            }
            LauncherLogExtensions.LauncherEventExtension launcherEventExtension = this.extension;
            return launcherEventExtension != null ? iComputeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(9, launcherEventExtension) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public LauncherEvent mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    if (this.action == null) {
                        this.action = new Action();
                    }
                    input.readMessage(this.action);
                } else if (tag == 18) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 18);
                    Target[] targetArr = this.srcTarget;
                    int length = targetArr == null ? 0 : targetArr.length;
                    int i = repeatedFieldArrayLength + length;
                    Target[] targetArr2 = new Target[i];
                    if (length != 0) {
                        System.arraycopy(targetArr, 0, targetArr2, 0, length);
                    }
                    while (length < i - 1) {
                        targetArr2[length] = new Target();
                        input.readMessage(targetArr2[length]);
                        input.readTag();
                        length++;
                    }
                    targetArr2[length] = new Target();
                    input.readMessage(targetArr2[length]);
                    this.srcTarget = targetArr2;
                } else if (tag == 26) {
                    int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(input, 26);
                    Target[] targetArr3 = this.destTarget;
                    int length2 = targetArr3 == null ? 0 : targetArr3.length;
                    int i2 = repeatedFieldArrayLength2 + length2;
                    Target[] targetArr4 = new Target[i2];
                    if (length2 != 0) {
                        System.arraycopy(targetArr3, 0, targetArr4, 0, length2);
                    }
                    while (length2 < i2 - 1) {
                        targetArr4[length2] = new Target();
                        input.readMessage(targetArr4[length2]);
                        input.readTag();
                        length2++;
                    }
                    targetArr4[length2] = new Target();
                    input.readMessage(targetArr4[length2]);
                    this.destTarget = targetArr4;
                } else if (tag == 32) {
                    this.actionDurationMillis = input.readInt64();
                } else if (tag == 40) {
                    this.elapsedContainerMillis = input.readInt64();
                } else if (tag == 48) {
                    this.elapsedSessionMillis = input.readInt64();
                } else if (tag == 56) {
                    this.isInMultiWindowMode = input.readBool();
                } else if (tag == 64) {
                    this.isInLandscapeMode = input.readBool();
                } else if (tag != 74) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    if (this.extension == null) {
                        this.extension = new LauncherLogExtensions.LauncherEventExtension();
                    }
                    input.readMessage(this.extension);
                }
            }
        }

        public static LauncherEvent parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (LauncherEvent) MessageNano.mergeFrom(new LauncherEvent(), data);
        }

        public static LauncherEvent parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new LauncherEvent().mergeFrom(input);
        }
    }
}
