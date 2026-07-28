package com.android.launcher3.logging;

import android.os.Process;
import android.text.TextUtils;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.nano.LauncherDumpProto;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DumpTargetWrapper {
    ArrayList<DumpTargetWrapper> children;
    LauncherDumpProto.DumpTarget node;

    public DumpTargetWrapper() {
        this.children = new ArrayList<>();
    }

    public DumpTargetWrapper(int containerType, int id) {
        this();
        this.node = newContainerTarget(containerType, id);
    }

    public DumpTargetWrapper(ItemInfo info) {
        this();
        this.node = newItemTarget(info);
    }

    public LauncherDumpProto.DumpTarget getDumpTarget() {
        return this.node;
    }

    public void add(DumpTargetWrapper child) {
        this.children.add(child);
    }

    public List<LauncherDumpProto.DumpTarget> getFlattenedList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.node);
        if (!this.children.isEmpty()) {
            Iterator<DumpTargetWrapper> it = this.children.iterator();
            while (it.hasNext()) {
                arrayList.addAll(it.next().getFlattenedList());
            }
            arrayList.add(this.node);
        }
        return arrayList;
    }

    public LauncherDumpProto.DumpTarget newItemTarget(ItemInfo info) {
        LauncherDumpProto.DumpTarget dumpTarget = new LauncherDumpProto.DumpTarget();
        dumpTarget.type = 1;
        int i = info.itemType;
        if (i == 0) {
            dumpTarget.itemType = 1;
        } else if (i == 1) {
            dumpTarget.itemType = 0;
        } else if (i == 4) {
            dumpTarget.itemType = 2;
        } else if (i == 6) {
            dumpTarget.itemType = 3;
        }
        return dumpTarget;
    }

    public LauncherDumpProto.DumpTarget newContainerTarget(int type, int id) {
        LauncherDumpProto.DumpTarget dumpTarget = new LauncherDumpProto.DumpTarget();
        dumpTarget.type = 2;
        dumpTarget.containerType = type;
        dumpTarget.pageId = id;
        return dumpTarget;
    }

    public static String getDumpTargetStr(LauncherDumpProto.DumpTarget t) {
        if (t == null) {
            return "";
        }
        int i = t.type;
        if (i == 1) {
            return getItemStr(t);
        }
        if (i != 2) {
            return "UNKNOWN TARGET TYPE";
        }
        String fieldName = LoggerUtils.getFieldName(t.containerType, LauncherDumpProto.ContainerType.class);
        if (t.containerType == 1) {
            return fieldName + " id=" + t.pageId;
        }
        if (t.containerType != 3) {
            return fieldName;
        }
        return fieldName + " grid(" + t.gridX + "," + t.gridY + ")";
    }

    private static String getItemStr(LauncherDumpProto.DumpTarget t) {
        String fieldName = LoggerUtils.getFieldName(t.itemType, LauncherDumpProto.ItemType.class);
        if (!TextUtils.isEmpty(t.packageName)) {
            fieldName = fieldName + ", package=" + t.packageName;
        }
        if (!TextUtils.isEmpty(t.component)) {
            fieldName = fieldName + ", component=" + t.component;
        }
        return fieldName + ", grid(" + t.gridX + "," + t.gridY + "), span(" + t.spanX + "," + t.spanY + "), pageIdx=" + t.pageId + " user=" + t.userType;
    }

    public LauncherDumpProto.DumpTarget writeToDumpTarget(ItemInfo itemInfo) {
        this.node.component = itemInfo.getTargetComponent() == null ? "" : itemInfo.getTargetComponent().flattenToString();
        this.node.packageName = itemInfo.getTargetComponent() != null ? itemInfo.getTargetComponent().getPackageName() : "";
        if (itemInfo instanceof LauncherAppWidgetInfo) {
            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
            this.node.component = launcherAppWidgetInfo.providerName.flattenToString();
            this.node.packageName = launcherAppWidgetInfo.providerName.getPackageName();
        }
        this.node.gridX = itemInfo.cellX;
        this.node.gridY = itemInfo.cellY;
        this.node.spanX = itemInfo.spanX;
        this.node.spanY = itemInfo.spanY;
        this.node.userType = !itemInfo.user.equals(Process.myUserHandle()) ? 1 : 0;
        return this.node;
    }
}
