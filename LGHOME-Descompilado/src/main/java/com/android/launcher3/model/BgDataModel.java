package com.android.launcher3.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.MutableInt;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.logging.DumpTargetWrapper;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.PromiseAppInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.model.nano.LauncherDumpProto;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.android.launcher3.widget.WidgetListRowEntry;
import com.google.protobuf.nano.MessageNano;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class BgDataModel {
    private static final String TAG = "BgDataModel";
    public static final ArrayList<FolderInfo> invalidFoldersToRestore = new ArrayList<>();
    public final LongArrayMap<ItemInfo> itemsIdMap = new LongArrayMap<>();
    public final ArrayList<ItemInfo> workspaceItems = new ArrayList<>();
    public final ArrayList<LauncherAppWidgetInfo> appWidgets = new ArrayList<>();
    public final LongArrayMap<FolderInfo> folders = new LongArrayMap<>();
    public final ArrayList<Long> workspaceScreens = new ArrayList<>();
    public final Map<ShortcutKey, MutableInt> pinnedShortcutCounts = new HashMap();
    public final MultiHashMap<ComponentKey, String> deepShortcutMap = new MultiHashMap<>();

    public interface Callbacks {
        public static final int FLAG_HAS_SHORTCUT_PERMISSION = 1;
        public static final int FLAG_QUIET_MODE_CHANGE_PERMISSION = 4;
        public static final int FLAG_QUIET_MODE_ENABLED = 2;

        void bindAllApplications(AppInfo[] apps, int flags);

        void bindAllWidgets(ArrayList<WidgetListRowEntry> widgets);

        void bindAppsAdded(IntArray newScreens, ArrayList<ItemInfo> addNotAnimated, ArrayList<ItemInfo> addAnimated);

        void bindDeepShortcutMap(HashMap<ComponentKey, Integer> deepShortcutMap);

        void bindItems(List<ItemInfo> shortcuts, boolean forceAnimateIcons);

        void bindPredictedItems(List<AppInfo> appInfos, IntArray ranks);

        void bindPromiseAppProgressUpdated(PromiseAppInfo app);

        void bindRestoreItemsChange(HashSet<ItemInfo> updates);

        void bindScreens(IntArray orderedScreenIds);

        void bindWidgetsRestored(ArrayList<LauncherAppWidgetInfo> widgets);

        void bindWorkspaceComponentsRemoved(ItemInfoMatcher matcher);

        void bindWorkspaceItemsChanged(ArrayList<WorkspaceItemInfo> updated);

        void clearPendingBinds();

        void executeOnNextDraw(ViewOnDrawExecutor executor);

        void finishBindingItems(int pageBoundFirst);

        void finishFirstPageBind(ViewOnDrawExecutor executor);

        int getPageToBindSynchronously();

        void onPageBoundSynchronously(int page);

        void preAddApps();

        void startBinding();
    }

    public synchronized void clear() {
        this.workspaceItems.clear();
        this.appWidgets.clear();
        this.folders.clear();
        this.itemsIdMap.clear();
        this.workspaceScreens.clear();
        this.pinnedShortcutCounts.clear();
        this.deepShortcutMap.clear();
        invalidFoldersToRestore.clear();
    }

    public synchronized void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        if (args.length > 0 && TextUtils.equals(args[0], "--proto")) {
            dumpProto(prefix, fd, writer, args);
            return;
        }
        writer.println(prefix + "Data Model:");
        writer.print(prefix + " ---- workspace screens: ");
        for (int i = 0; i < this.workspaceScreens.size(); i++) {
            writer.print(" " + this.workspaceScreens.get(i).toString());
        }
        writer.println();
        writer.println(prefix + " ---- workspace items ");
        for (int i2 = 0; i2 < this.workspaceItems.size(); i2++) {
            writer.println(prefix + "\t" + this.workspaceItems.get(i2).toString());
        }
        writer.println(prefix + " ---- appwidget items ");
        for (int i3 = 0; i3 < this.appWidgets.size(); i3++) {
            writer.println(prefix + "\t" + this.appWidgets.get(i3).toString());
        }
        writer.println(prefix + " ---- folder items ");
        for (int i4 = 0; i4 < this.folders.size(); i4++) {
            writer.println(prefix + "\t" + this.folders.valueAt(i4).toString());
        }
        writer.println(prefix + " ---- items id map ");
        for (int i5 = 0; i5 < this.itemsIdMap.size(); i5++) {
            writer.println(prefix + "\t" + this.itemsIdMap.valueAt(i5).toString());
        }
        if (args.length > 0 && TextUtils.equals(args[0], "--all")) {
            writer.println(prefix + "shortcuts");
            Iterator<String> it = this.deepShortcutMap.values().iterator();
            while (it.hasNext()) {
                ArrayList arrayList = (ArrayList) it.next();
                writer.print(prefix + "  ");
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    writer.print(((String) it2.next()).toString() + ", ");
                }
                writer.println();
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v15, resolved type: E */
    /* JADX DEBUG: Multi-variable search result rejected for r6v20, resolved type: E */
    /* JADX DEBUG: Multi-variable search result rejected for r6v25, resolved type: E */
    /* JADX DEBUG: Multi-variable search result rejected for r6v9, resolved type: E */
    /* JADX WARN: Multi-variable type inference failed */
    private synchronized void dumpProto(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        int i = 0;
        DumpTargetWrapper dumpTargetWrapper = new DumpTargetWrapper(2, 0);
        LongArrayMap longArrayMap = new LongArrayMap();
        for (int i2 = 0; i2 < this.workspaceScreens.size(); i2++) {
            longArrayMap.put(new Long(this.workspaceScreens.get(i2).longValue()).longValue(), new DumpTargetWrapper(1, i2));
        }
        if (longArrayMap.isEmpty()) {
            return;
        }
        for (int i3 = 0; i3 < this.folders.size(); i3++) {
            FolderInfo folderInfoValueAt = this.folders.valueAt(i3);
            DumpTargetWrapper dumpTargetWrapper2 = new DumpTargetWrapper(3, this.folders.size());
            dumpTargetWrapper2.writeToDumpTarget(folderInfoValueAt);
            for (ShortcutInfo shortcutInfo : folderInfoValueAt.contents) {
                DumpTargetWrapper dumpTargetWrapper3 = new DumpTargetWrapper(shortcutInfo);
                dumpTargetWrapper3.writeToDumpTarget(shortcutInfo);
                dumpTargetWrapper2.add(dumpTargetWrapper3);
            }
            if (folderInfoValueAt.container == -101) {
                dumpTargetWrapper.add(dumpTargetWrapper2);
            } else if (folderInfoValueAt.container == -100) {
                ((DumpTargetWrapper) longArrayMap.get(new Long(folderInfoValueAt.screenId).longValue())).add(dumpTargetWrapper2);
            }
        }
        for (int i4 = 0; i4 < this.workspaceItems.size(); i4++) {
            ItemInfo itemInfo = this.workspaceItems.get(i4);
            if (!(itemInfo instanceof FolderInfo)) {
                DumpTargetWrapper dumpTargetWrapper4 = new DumpTargetWrapper(itemInfo);
                dumpTargetWrapper4.writeToDumpTarget(itemInfo);
                if (itemInfo.container == -101) {
                    dumpTargetWrapper.add(dumpTargetWrapper4);
                } else if (itemInfo.container == -100) {
                    ((DumpTargetWrapper) longArrayMap.get(new Long(itemInfo.screenId).longValue())).add(dumpTargetWrapper4);
                }
            }
        }
        for (int i5 = 0; i5 < this.appWidgets.size(); i5++) {
            LauncherAppWidgetInfo launcherAppWidgetInfo = this.appWidgets.get(i5);
            DumpTargetWrapper dumpTargetWrapper5 = new DumpTargetWrapper(launcherAppWidgetInfo);
            dumpTargetWrapper5.writeToDumpTarget(launcherAppWidgetInfo);
            if (launcherAppWidgetInfo.container == -101) {
                dumpTargetWrapper.add(dumpTargetWrapper5);
            } else if (launcherAppWidgetInfo.container == -100) {
                ((DumpTargetWrapper) longArrayMap.get(new Long(launcherAppWidgetInfo.screenId).longValue())).add(dumpTargetWrapper5);
            }
        }
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(dumpTargetWrapper.getFlattenedList());
        for (int i6 = 0; i6 < longArrayMap.size(); i6++) {
            arrayList.addAll(((DumpTargetWrapper) longArrayMap.valueAt(i6)).getFlattenedList());
        }
        if (args.length > 1 && TextUtils.equals(args[1], "--debug")) {
            while (i < arrayList.size()) {
                writer.println(prefix + DumpTargetWrapper.getDumpTargetStr((LauncherDumpProto.DumpTarget) arrayList.get(i)));
                i++;
            }
            return;
        }
        LauncherDumpProto.LauncherImpression launcherImpression = new LauncherDumpProto.LauncherImpression();
        launcherImpression.targets = new LauncherDumpProto.DumpTarget[arrayList.size()];
        while (i < arrayList.size()) {
            launcherImpression.targets[i] = (LauncherDumpProto.DumpTarget) arrayList.get(i);
            i++;
        }
        try {
            new FileOutputStream(fd).write(MessageNano.toByteArray(launcherImpression));
            Log.d(TAG, MessageNano.toByteArray(launcherImpression).length + "Bytes");
        } catch (IOException e) {
            Log.e(TAG, "Exception writing dumpsys --proto", e);
        }
    }

    public synchronized void removeItem(Context context, ItemInfo... items) {
        removeItem(context, Arrays.asList(items));
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x003b A[Catch: all -> 0x00d6, TryCatch #0 {, blocks: (B:3:0x0001, B:4:0x0005, B:6:0x000b, B:36:0x00a7, B:18:0x0026, B:20:0x0034, B:22:0x003b, B:24:0x0045, B:25:0x004d, B:26:0x0053, B:28:0x005e, B:29:0x0064, B:31:0x006a, B:33:0x0078, B:34:0x009c, B:35:0x00a2), top: B:43:0x0001 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized void removeItem(android.content.Context r9, java.lang.Iterable<? extends com.android.launcher3.model.data.ItemInfo> r10) {
        /*
            r8 = this;
            monitor-enter(r8)
            java.util.Iterator r10 = r10.iterator()     // Catch: java.lang.Throwable -> Ld6
        L5:
            boolean r0 = r10.hasNext()     // Catch: java.lang.Throwable -> Ld6
            if (r0 == 0) goto Ld4
            java.lang.Object r0 = r10.next()     // Catch: java.lang.Throwable -> Ld6
            com.android.launcher3.model.data.ItemInfo r0 = (com.android.launcher3.model.data.ItemInfo) r0     // Catch: java.lang.Throwable -> Ld6
            int r1 = r0.itemType     // Catch: java.lang.Throwable -> Ld6
            r2 = 1
            if (r1 == 0) goto La2
            if (r1 == r2) goto La2
            r3 = 2
            if (r1 == r3) goto L53
            r3 = 4
            if (r1 == r3) goto L4d
            r3 = 5
            if (r1 == r3) goto L4d
            r3 = 6
            if (r1 == r3) goto L26
            goto La7
        L26:
            com.android.launcher3.shortcuts.ShortcutKey r1 = com.android.launcher3.shortcuts.ShortcutKey.fromItemInfo(r0)     // Catch: java.lang.Throwable -> Ld6
            java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, android.util.MutableInt> r3 = r8.pinnedShortcutCounts     // Catch: java.lang.Throwable -> Ld6
            java.lang.Object r3 = r3.get(r1)     // Catch: java.lang.Throwable -> Ld6
            android.util.MutableInt r3 = (android.util.MutableInt) r3     // Catch: java.lang.Throwable -> Ld6
            if (r3 == 0) goto L3b
            int r4 = r3.value     // Catch: java.lang.Throwable -> Ld6
            int r4 = r4 - r2
            r3.value = r4     // Catch: java.lang.Throwable -> Ld6
            if (r4 != 0) goto La2
        L3b:
            java.util.HashSet r3 = com.android.launcher3.InstallShortcutReceiver.getPendingShortcuts(r9)     // Catch: java.lang.Throwable -> Ld6
            boolean r3 = r3.contains(r1)     // Catch: java.lang.Throwable -> Ld6
            if (r3 != 0) goto La2
            com.android.launcher3.shortcuts.DeepShortcutManager r3 = com.android.launcher3.shortcuts.DeepShortcutManager.getInstance(r9)     // Catch: java.lang.Throwable -> Ld6
            r3.unpinShortcut(r1)     // Catch: java.lang.Throwable -> Ld6
            goto La2
        L4d:
            java.util.ArrayList<com.android.launcher3.model.data.LauncherAppWidgetInfo> r1 = r8.appWidgets     // Catch: java.lang.Throwable -> Ld6
            r1.remove(r0)     // Catch: java.lang.Throwable -> Ld6
            goto La7
        L53:
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.FolderInfo> r1 = r8.folders     // Catch: java.lang.Throwable -> Ld6
            long r3 = r0.id     // Catch: java.lang.Throwable -> Ld6
            r1.remove(r3)     // Catch: java.lang.Throwable -> Ld6
            boolean r1 = com.android.launcher3.config.ProviderConfig.IS_DOGFOOD_BUILD     // Catch: java.lang.Throwable -> Ld6
            if (r1 == 0) goto L9c
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.ItemInfo> r1 = r8.itemsIdMap     // Catch: java.lang.Throwable -> Ld6
            java.util.Iterator r1 = r1.iterator()     // Catch: java.lang.Throwable -> Ld6
        L64:
            boolean r3 = r1.hasNext()     // Catch: java.lang.Throwable -> Ld6
            if (r3 == 0) goto L9c
            java.lang.Object r3 = r1.next()     // Catch: java.lang.Throwable -> Ld6
            com.android.launcher3.model.data.ItemInfo r3 = (com.android.launcher3.model.data.ItemInfo) r3     // Catch: java.lang.Throwable -> Ld6
            long r4 = r3.container     // Catch: java.lang.Throwable -> Ld6
            long r6 = r0.id     // Catch: java.lang.Throwable -> Ld6
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 != 0) goto L64
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Ld6
            r4.<init>()     // Catch: java.lang.Throwable -> Ld6
            java.lang.String r5 = "deleting a folder ("
            r4.append(r5)     // Catch: java.lang.Throwable -> Ld6
            r4.append(r0)     // Catch: java.lang.Throwable -> Ld6
            java.lang.String r5 = ") which still contains items ("
            r4.append(r5)     // Catch: java.lang.Throwable -> Ld6
            r4.append(r3)     // Catch: java.lang.Throwable -> Ld6
            java.lang.String r3 = ")"
            r4.append(r3)     // Catch: java.lang.Throwable -> Ld6
            java.lang.String r3 = r4.toString()     // Catch: java.lang.Throwable -> Ld6
            java.lang.String r4 = "BgDataModel"
            android.util.Log.e(r4, r3)     // Catch: java.lang.Throwable -> Ld6
            goto L64
        L9c:
            java.util.ArrayList<com.android.launcher3.model.data.ItemInfo> r1 = r8.workspaceItems     // Catch: java.lang.Throwable -> Ld6
            r1.remove(r0)     // Catch: java.lang.Throwable -> Ld6
            goto La7
        La2:
            java.util.ArrayList<com.android.launcher3.model.data.ItemInfo> r1 = r8.workspaceItems     // Catch: java.lang.Throwable -> Ld6
            r1.remove(r0)     // Catch: java.lang.Throwable -> Ld6
        La7:
            r0.isRemoved = r2     // Catch: java.lang.Throwable -> Ld6
            java.lang.String r1 = "BgDataModel"
            int r2 = r0.hashCode()     // Catch: java.lang.Throwable -> Ld6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Ld6
            r3.<init>()     // Catch: java.lang.Throwable -> Ld6
            java.lang.String r4 = "Item is removed in BgDataModel - "
            r3.append(r4)     // Catch: java.lang.Throwable -> Ld6
            r3.append(r0)     // Catch: java.lang.Throwable -> Ld6
            java.lang.String r4 = ", "
            r3.append(r4)     // Catch: java.lang.Throwable -> Ld6
            r3.append(r2)     // Catch: java.lang.Throwable -> Ld6
            java.lang.String r2 = r3.toString()     // Catch: java.lang.Throwable -> Ld6
            com.lge.launcher3.util.LGLog.d(r1, r2)     // Catch: java.lang.Throwable -> Ld6
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.ItemInfo> r1 = r8.itemsIdMap     // Catch: java.lang.Throwable -> Ld6
            long r2 = r0.id     // Catch: java.lang.Throwable -> Ld6
            r1.remove(r2)     // Catch: java.lang.Throwable -> Ld6
            goto L5
        Ld4:
            monitor-exit(r8)
            return
        Ld6:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.BgDataModel.removeItem(android.content.Context, java.lang.Iterable):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x00a6 A[Catch: all -> 0x00ad, TRY_LEAVE, TryCatch #0 {, blocks: (B:3:0x0001, B:16:0x001d, B:18:0x002b, B:21:0x003d, B:23:0x0041, B:19:0x0036, B:24:0x0049, B:25:0x0051, B:26:0x0061, B:28:0x0069, B:32:0x0074, B:34:0x007e, B:35:0x009a, B:36:0x00a6), top: B:42:0x0001 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized void addItem(android.content.Context r5, com.android.launcher3.model.data.ItemInfo r6, boolean r7) {
        /*
            r4 = this;
            monitor-enter(r4)
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.ItemInfo> r0 = r4.itemsIdMap     // Catch: java.lang.Throwable -> Lad
            long r1 = r6.id     // Catch: java.lang.Throwable -> Lad
            r0.put(r1, r6)     // Catch: java.lang.Throwable -> Lad
            int r0 = r6.itemType     // Catch: java.lang.Throwable -> Lad
            if (r0 == 0) goto L61
            r1 = 1
            if (r0 == r1) goto L61
            r2 = 2
            if (r0 == r2) goto L51
            r2 = 4
            if (r0 == r2) goto L49
            r2 = 5
            if (r0 == r2) goto L49
            r2 = 6
            if (r0 == r2) goto L1d
            goto Lab
        L1d:
            com.android.launcher3.shortcuts.ShortcutKey r0 = com.android.launcher3.shortcuts.ShortcutKey.fromItemInfo(r6)     // Catch: java.lang.Throwable -> Lad
            java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, android.util.MutableInt> r2 = r4.pinnedShortcutCounts     // Catch: java.lang.Throwable -> Lad
            java.lang.Object r2 = r2.get(r0)     // Catch: java.lang.Throwable -> Lad
            android.util.MutableInt r2 = (android.util.MutableInt) r2     // Catch: java.lang.Throwable -> Lad
            if (r2 != 0) goto L36
            android.util.MutableInt r2 = new android.util.MutableInt     // Catch: java.lang.Throwable -> Lad
            r2.<init>(r1)     // Catch: java.lang.Throwable -> Lad
            java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, android.util.MutableInt> r3 = r4.pinnedShortcutCounts     // Catch: java.lang.Throwable -> Lad
            r3.put(r0, r2)     // Catch: java.lang.Throwable -> Lad
            goto L3b
        L36:
            int r3 = r2.value     // Catch: java.lang.Throwable -> Lad
            int r3 = r3 + r1
            r2.value = r3     // Catch: java.lang.Throwable -> Lad
        L3b:
            if (r7 == 0) goto L61
            int r2 = r2.value     // Catch: java.lang.Throwable -> Lad
            if (r2 != r1) goto L61
            com.android.launcher3.shortcuts.DeepShortcutManager r5 = com.android.launcher3.shortcuts.DeepShortcutManager.getInstance(r5)     // Catch: java.lang.Throwable -> Lad
            r5.pinShortcut(r0)     // Catch: java.lang.Throwable -> Lad
            goto L61
        L49:
            java.util.ArrayList<com.android.launcher3.model.data.LauncherAppWidgetInfo> r5 = r4.appWidgets     // Catch: java.lang.Throwable -> Lad
            com.android.launcher3.model.data.LauncherAppWidgetInfo r6 = (com.android.launcher3.model.data.LauncherAppWidgetInfo) r6     // Catch: java.lang.Throwable -> Lad
            r5.add(r6)     // Catch: java.lang.Throwable -> Lad
            goto Lab
        L51:
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.FolderInfo> r5 = r4.folders     // Catch: java.lang.Throwable -> Lad
            long r0 = r6.id     // Catch: java.lang.Throwable -> Lad
            r7 = r6
            com.android.launcher3.model.data.FolderInfo r7 = (com.android.launcher3.model.data.FolderInfo) r7     // Catch: java.lang.Throwable -> Lad
            r5.put(r0, r7)     // Catch: java.lang.Throwable -> Lad
            java.util.ArrayList<com.android.launcher3.model.data.ItemInfo> r5 = r4.workspaceItems     // Catch: java.lang.Throwable -> Lad
            r5.add(r6)     // Catch: java.lang.Throwable -> Lad
            goto Lab
        L61:
            long r0 = r6.container     // Catch: java.lang.Throwable -> Lad
            r2 = -100
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 == 0) goto La6
            long r0 = r6.container     // Catch: java.lang.Throwable -> Lad
            r2 = -101(0xffffffffffffff9b, double:NaN)
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 != 0) goto L72
            goto La6
        L72:
            if (r7 == 0) goto L9a
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.model.data.FolderInfo> r5 = r4.folders     // Catch: java.lang.Throwable -> Lad
            long r0 = r6.container     // Catch: java.lang.Throwable -> Lad
            boolean r5 = r5.containsKey(r0)     // Catch: java.lang.Throwable -> Lad
            if (r5 != 0) goto Lab
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lad
            r5.<init>()     // Catch: java.lang.Throwable -> Lad
            java.lang.String r7 = "adding item: "
            r5.append(r7)     // Catch: java.lang.Throwable -> Lad
            r5.append(r6)     // Catch: java.lang.Throwable -> Lad
            java.lang.String r6 = " to a folder that  doesn't exist"
            r5.append(r6)     // Catch: java.lang.Throwable -> Lad
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> Lad
            java.lang.String r6 = "BgDataModel"
            android.util.Log.e(r6, r5)     // Catch: java.lang.Throwable -> Lad
            goto Lab
        L9a:
            long r0 = r6.container     // Catch: java.lang.Throwable -> Lad
            com.android.launcher3.model.data.FolderInfo r5 = r4.findOrMakeFolder(r0)     // Catch: java.lang.Throwable -> Lad
            com.android.launcher3.ShortcutInfo r6 = (com.android.launcher3.ShortcutInfo) r6     // Catch: java.lang.Throwable -> Lad
            r5.add(r6)     // Catch: java.lang.Throwable -> Lad
            goto Lab
        La6:
            java.util.ArrayList<com.android.launcher3.model.data.ItemInfo> r5 = r4.workspaceItems     // Catch: java.lang.Throwable -> Lad
            r5.add(r6)     // Catch: java.lang.Throwable -> Lad
        Lab:
            monitor-exit(r4)
            return
        Lad:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.BgDataModel.addItem(android.content.Context, com.android.launcher3.model.data.ItemInfo, boolean):void");
    }

    public synchronized FolderInfo findOrMakeFolder(long id) {
        FolderInfo folderInfo;
        folderInfo = this.folders.get(id);
        if (folderInfo == null) {
            folderInfo = new FolderInfo();
            this.folders.put(id, folderInfo);
        }
        return folderInfo;
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x003b A[Catch: all -> 0x0071, TryCatch #0 {, blocks: (B:4:0x0003, B:5:0x000d, B:7:0x0013, B:9:0x0025, B:11:0x002d, B:12:0x0031, B:13:0x0035, B:15:0x003b, B:17:0x0047, B:19:0x004d, B:24:0x0058), top: B:31:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized void updateDeepShortcutMap(java.lang.String r4, android.os.UserHandle r5, java.util.List<com.android.launcher3.shortcuts.ShortcutInfoCompat> r6) {
        /*
            r3 = this;
            monitor-enter(r3)
            if (r4 == 0) goto L31
            com.android.launcher3.util.MultiHashMap<com.android.launcher3.util.ComponentKey, java.lang.String> r0 = r3.deepShortcutMap     // Catch: java.lang.Throwable -> L71
            java.util.Set r0 = r0.keySet()     // Catch: java.lang.Throwable -> L71
            java.util.Iterator r0 = r0.iterator()     // Catch: java.lang.Throwable -> L71
        Ld:
            boolean r1 = r0.hasNext()     // Catch: java.lang.Throwable -> L71
            if (r1 == 0) goto L31
            java.lang.Object r1 = r0.next()     // Catch: java.lang.Throwable -> L71
            com.android.launcher3.util.ComponentKey r1 = (com.android.launcher3.util.ComponentKey) r1     // Catch: java.lang.Throwable -> L71
            android.content.ComponentName r2 = r1.componentName     // Catch: java.lang.Throwable -> L71
            java.lang.String r2 = r2.getPackageName()     // Catch: java.lang.Throwable -> L71
            boolean r2 = r2.equals(r4)     // Catch: java.lang.Throwable -> L71
            if (r2 == 0) goto Ld
            android.os.UserHandle r1 = r1.user     // Catch: java.lang.Throwable -> L71
            boolean r1 = r1.equals(r5)     // Catch: java.lang.Throwable -> L71
            if (r1 == 0) goto Ld
            r0.remove()     // Catch: java.lang.Throwable -> L71
            goto Ld
        L31:
            java.util.Iterator r4 = r6.iterator()     // Catch: java.lang.Throwable -> L71
        L35:
            boolean r5 = r4.hasNext()     // Catch: java.lang.Throwable -> L71
            if (r5 == 0) goto L6f
            java.lang.Object r5 = r4.next()     // Catch: java.lang.Throwable -> L71
            com.android.launcher3.shortcuts.ShortcutInfoCompat r5 = (com.android.launcher3.shortcuts.ShortcutInfoCompat) r5     // Catch: java.lang.Throwable -> L71
            boolean r6 = r5.isEnabled()     // Catch: java.lang.Throwable -> L71
            if (r6 == 0) goto L55
            boolean r6 = r5.isDeclaredInManifest()     // Catch: java.lang.Throwable -> L71
            if (r6 != 0) goto L53
            boolean r6 = r5.isDynamic()     // Catch: java.lang.Throwable -> L71
            if (r6 == 0) goto L55
        L53:
            r6 = 1
            goto L56
        L55:
            r6 = 0
        L56:
            if (r6 == 0) goto L35
            com.android.launcher3.util.ComponentKey r6 = new com.android.launcher3.util.ComponentKey     // Catch: java.lang.Throwable -> L71
            android.content.ComponentName r0 = r5.getActivity()     // Catch: java.lang.Throwable -> L71
            android.os.UserHandle r1 = r5.getUserHandle()     // Catch: java.lang.Throwable -> L71
            r6.<init>(r0, r1)     // Catch: java.lang.Throwable -> L71
            com.android.launcher3.util.MultiHashMap<com.android.launcher3.util.ComponentKey, java.lang.String> r0 = r3.deepShortcutMap     // Catch: java.lang.Throwable -> L71
            java.lang.String r5 = r5.getId()     // Catch: java.lang.Throwable -> L71
            r0.addToList(r6, r5)     // Catch: java.lang.Throwable -> L71
            goto L35
        L6f:
            monitor-exit(r3)
            return
        L71:
            r4 = move-exception
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.BgDataModel.updateDeepShortcutMap(java.lang.String, android.os.UserHandle, java.util.List):void");
    }
}
