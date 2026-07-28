package com.android.launcher3.logging;

import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.View;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogExtensions;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.InstantAppResolver;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/* JADX INFO: loaded from: classes.dex */
public class LoggerUtils {
    private static final int DEFAULT_PREDICTED_RANK = 10000;
    private static final String DELIMITER_DOT = "\\.";
    private static final String UNKNOWN = "UNKNOWN";
    private static final ArrayMap<Class, SparseArray<String>> sNameCache = new ArrayMap<>();

    public static String getFieldName(int value, Class c) {
        SparseArray<String> sparseArray;
        ArrayMap<Class, SparseArray<String>> arrayMap = sNameCache;
        synchronized (arrayMap) {
            sparseArray = arrayMap.get(c);
            if (sparseArray == null) {
                sparseArray = new SparseArray<>();
                for (Field field : c.getDeclaredFields()) {
                    if (field.getType() == Integer.TYPE && Modifier.isStatic(field.getModifiers())) {
                        try {
                            field.setAccessible(true);
                            sparseArray.put(field.getInt(null), field.getName());
                        } catch (IllegalAccessException unused) {
                        }
                    }
                }
                sNameCache.put(c, sparseArray);
            }
        }
        String str = sparseArray.get(value);
        return str != null ? str : UNKNOWN;
    }

    public static String getActionStr(LauncherLogProto.Action action) {
        int i = action.type;
        if (i != 0) {
            if (i == 2) {
                return getFieldName(action.command, LauncherLogProto.Action.Command.class);
            }
            return getFieldName(action.type, LauncherLogProto.Action.Type.class);
        }
        String str = "" + getFieldName(action.touch, LauncherLogProto.Action.Touch.class);
        if (action.touch != 3 && action.touch != 4) {
            return str;
        }
        return str + " direction=" + getFieldName(action.dir, LauncherLogProto.Action.Direction.class);
    }

    public static String getTargetStr(LauncherLogProto.Target t) {
        String itemStr;
        if (t == null) {
            return "";
        }
        int i = t.type;
        if (i == 1) {
            itemStr = getItemStr(t);
        } else if (i == 2) {
            itemStr = getFieldName(t.controlType, LauncherLogProto.ControlType.class);
        } else if (i == 3) {
            itemStr = getFieldName(t.containerType, LauncherLogProto.ContainerType.class);
            if (t.containerType == 1 || t.containerType == 2) {
                itemStr = itemStr + " id=" + t.pageIndex;
            } else if (t.containerType == 3) {
                itemStr = itemStr + " grid(" + t.gridX + "," + t.gridY + ")";
            }
        } else {
            itemStr = "UNKNOWN TARGET TYPE";
        }
        if (t.tipType == 0) {
            return itemStr;
        }
        return itemStr + " " + getFieldName(t.tipType, LauncherLogProto.TipType.class);
    }

    private static String getItemStr(LauncherLogProto.Target t) {
        String fieldName = getFieldName(t.itemType, LauncherLogProto.ItemType.class);
        if (t.packageNameHash != 0) {
            fieldName = fieldName + ", packageHash=" + t.packageNameHash;
        }
        if (t.componentHash != 0) {
            fieldName = fieldName + ", componentHash=" + t.componentHash;
        }
        if (t.intentHash != 0) {
            fieldName = fieldName + ", intentHash=" + t.intentHash;
        }
        if ((t.packageNameHash != 0 || t.componentHash != 0 || t.intentHash != 0) && t.itemType != 9) {
            fieldName = fieldName + ", predictiveRank=" + t.predictedRank + ", grid(" + t.gridX + "," + t.gridY + "), span(" + t.spanX + "," + t.spanY + "), pageIdx=" + t.pageIndex;
        }
        if (t.itemType != 9) {
            return fieldName;
        }
        return fieldName + ", pageIdx=" + t.pageIndex;
    }

    public static LauncherLogProto.Target newItemTarget(int itemType) {
        LauncherLogProto.Target targetNewTarget = newTarget(1);
        targetNewTarget.itemType = itemType;
        return targetNewTarget;
    }

    public static LauncherLogProto.Target newItemTarget(View v, InstantAppResolver instantAppResolver) {
        if (v.getTag() instanceof ItemInfo) {
            return newItemTarget((ItemInfo) v.getTag(), instantAppResolver);
        }
        return newTarget(1);
    }

    public static LauncherLogProto.Target newItemTarget(ItemInfo info, InstantAppResolver instantAppResolver) {
        int i = 1;
        LauncherLogProto.Target targetNewTarget = newTarget(1);
        int i2 = info.itemType;
        if (i2 == 0) {
            if (instantAppResolver != null && (info instanceof AppInfo) && instantAppResolver.isInstantApp((AppInfo) info)) {
                i = 10;
            }
            targetNewTarget.itemType = i;
            targetNewTarget.predictedRank = -100;
        } else if (i2 == 1) {
            targetNewTarget.itemType = 2;
        } else if (i2 == 2) {
            targetNewTarget.itemType = 4;
        } else if (i2 == 4) {
            targetNewTarget.itemType = 3;
        } else if (i2 == 6) {
            targetNewTarget.itemType = 5;
        }
        return targetNewTarget;
    }

    public static LauncherLogProto.Target newDropTarget(View v) {
        boolean z = v instanceof ButtonDropTarget;
        if (!z) {
            return newTarget(3);
        }
        if (z) {
            return ((ButtonDropTarget) v).getDropTargetForLogging();
        }
        return newTarget(2);
    }

    public static LauncherLogProto.Target newTarget(int targetType, LauncherLogExtensions.TargetExtension extension) {
        LauncherLogProto.Target target = new LauncherLogProto.Target();
        target.type = targetType;
        target.extension = extension;
        return target;
    }

    public static LauncherLogProto.Target newTarget(int targetType) {
        LauncherLogProto.Target target = new LauncherLogProto.Target();
        target.type = targetType;
        return target;
    }

    public static LauncherLogProto.Target newControlTarget(int controlType) {
        LauncherLogProto.Target targetNewTarget = newTarget(2);
        targetNewTarget.controlType = controlType;
        return targetNewTarget;
    }

    public static LauncherLogProto.Target newContainerTarget(int containerType) {
        LauncherLogProto.Target targetNewTarget = newTarget(3);
        targetNewTarget.containerType = containerType;
        return targetNewTarget;
    }

    public static LauncherLogProto.Action newAction(int type) {
        LauncherLogProto.Action action = new LauncherLogProto.Action();
        action.type = type;
        return action;
    }

    public static LauncherLogProto.Action newCommandAction(int command) {
        LauncherLogProto.Action actionNewAction = newAction(2);
        actionNewAction.command = command;
        return actionNewAction;
    }

    public static LauncherLogProto.Action newTouchAction(int touch) {
        LauncherLogProto.Action actionNewAction = newAction(0);
        actionNewAction.touch = touch;
        return actionNewAction;
    }

    public static LauncherLogProto.LauncherEvent newLauncherEvent(LauncherLogProto.Action action, LauncherLogProto.Target... srcTargets) {
        LauncherLogProto.LauncherEvent launcherEvent = new LauncherLogProto.LauncherEvent();
        launcherEvent.srcTarget = srcTargets;
        launcherEvent.action = action;
        return launcherEvent;
    }

    public static String extractObjectNameAndAddress(String stringToExtract) {
        String[] strArrSplit = stringToExtract.split(DELIMITER_DOT);
        return strArrSplit.length == 0 ? "" : strArrSplit[strArrSplit.length - 1];
    }
}
