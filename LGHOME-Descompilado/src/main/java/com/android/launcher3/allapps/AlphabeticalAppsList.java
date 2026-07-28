package com.android.launcher3.allapps;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.Launcher;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.model.AppNameComparator;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.util.ComponentKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/* JADX INFO: loaded from: classes.dex */
public class AlphabeticalAppsList {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_PREDICTIONS = false;
    public static final String TAG = "AlphabeticalAppsList";
    private RecyclerView.Adapter mAdapter;
    private AppNameComparator mAppNameComparator;
    private AlphabeticIndexCompat mIndexer;
    private Launcher mLauncher;
    private MergeAlgorithm mMergeAlgorithm;
    private int mNumAppRowsInAdapter;
    private int mNumAppsPerRow;
    private int mNumPredictedAppsPerRow;
    private ArrayList<ComponentKey> mSearchResults;
    private final List<AppInfo> mApps = new ArrayList();
    private final HashMap<ComponentKey, AppInfo> mComponentToAppMap = new HashMap<>();
    private List<AppInfo> mFilteredApps = new ArrayList();
    private List<AdapterItem> mAdapterItems = new ArrayList();
    private List<SectionInfo> mSections = new ArrayList();
    private List<FastScrollSectionInfo> mFastScrollerSections = new ArrayList();
    private List<ComponentKey> mPredictedAppComponents = new ArrayList();
    private List<AppInfo> mPredictedApps = new ArrayList();
    private HashMap<CharSequence, String> mCachedSectionNames = new HashMap<>();

    public interface MergeAlgorithm {
        boolean continueMerging(SectionInfo section, SectionInfo withSection, int sectionAppCount, int numAppsPerRow, int mergeCount);
    }

    public static class SectionInfo {
        public AdapterItem firstAppItem;
        public int numApps;
        public AdapterItem sectionBreakItem;
    }

    public static class FastScrollSectionInfo {
        public AdapterItem fastScrollToItem;
        public String sectionName;
        public float touchFraction;

        public FastScrollSectionInfo(String sectionName) {
            this.sectionName = sectionName;
        }
    }

    public static class AdapterItem {
        public int position;
        public int rowAppIndex;
        public int rowIndex;
        public SectionInfo sectionInfo;
        public int viewType;
        public String sectionName = null;
        public int sectionAppIndex = -1;
        public AppInfo appInfo = null;
        public int appIndex = -1;

        public static AdapterItem asSectionBreak(int pos, SectionInfo section) {
            AdapterItem adapterItem = new AdapterItem();
            adapterItem.viewType = 0;
            adapterItem.position = pos;
            adapterItem.sectionInfo = section;
            section.sectionBreakItem = adapterItem;
            return adapterItem;
        }

        public static AdapterItem asPredictedApp(int pos, SectionInfo section, String sectionName, int sectionAppIndex, AppInfo appInfo, int appIndex) {
            AdapterItem adapterItemAsApp = asApp(pos, section, sectionName, sectionAppIndex, appInfo, appIndex);
            adapterItemAsApp.viewType = 2;
            return adapterItemAsApp;
        }

        public static AdapterItem asApp(int pos, SectionInfo section, String sectionName, int sectionAppIndex, AppInfo appInfo, int appIndex) {
            AdapterItem adapterItem = new AdapterItem();
            adapterItem.viewType = 1;
            adapterItem.position = pos;
            adapterItem.sectionInfo = section;
            adapterItem.sectionName = sectionName;
            adapterItem.sectionAppIndex = sectionAppIndex;
            adapterItem.appInfo = appInfo;
            adapterItem.appIndex = appIndex;
            return adapterItem;
        }
    }

    public AlphabeticalAppsList(Context context) {
        this.mLauncher = (Launcher) context;
        this.mIndexer = new AlphabeticIndexCompat(context);
        this.mAppNameComparator = new AppNameComparator(context);
    }

    public void setNumAppsPerRow(int numAppsPerRow, int numPredictedAppsPerRow, MergeAlgorithm mergeAlgorithm) {
        this.mNumAppsPerRow = numAppsPerRow;
        this.mNumPredictedAppsPerRow = numPredictedAppsPerRow;
        this.mMergeAlgorithm = mergeAlgorithm;
        updateAdapterItems();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

    public List<AppInfo> getApps() {
        return this.mApps;
    }

    public List<SectionInfo> getSections() {
        return this.mSections;
    }

    public List<FastScrollSectionInfo> getFastScrollerSections() {
        return this.mFastScrollerSections;
    }

    public List<AdapterItem> getAdapterItems() {
        return this.mAdapterItems;
    }

    public int getSize() {
        return this.mFilteredApps.size();
    }

    public int getNumAppRows() {
        return this.mNumAppRowsInAdapter;
    }

    public boolean hasFilter() {
        return this.mSearchResults != null;
    }

    public boolean hasNoFilteredResults() {
        return this.mSearchResults != null && this.mFilteredApps.isEmpty();
    }

    public void setOrderedFilter(ArrayList<ComponentKey> f) {
        if (this.mSearchResults != f) {
            this.mSearchResults = f;
            updateAdapterItems();
        }
    }

    public void setPredictedApps(List<ComponentKey> apps) {
        this.mPredictedAppComponents.clear();
        this.mPredictedAppComponents.addAll(apps);
        onAppsUpdated();
    }

    public void setApps(List<AppInfo> apps) {
        this.mComponentToAppMap.clear();
        addApps(apps);
    }

    public void addApps(List<AppInfo> apps) {
        updateApps(apps);
    }

    public void updateApps(List<AppInfo> apps) {
        for (AppInfo appInfo : apps) {
            this.mComponentToAppMap.put(appInfo.toComponentKey(), appInfo);
        }
        onAppsUpdated();
    }

    public void removeApps(List<AppInfo> apps) {
        Iterator<AppInfo> it = apps.iterator();
        while (it.hasNext()) {
            this.mComponentToAppMap.remove(it.next().toComponentKey());
        }
        onAppsUpdated();
    }

    private void onAppsUpdated() {
        this.mApps.clear();
        this.mApps.addAll(this.mComponentToAppMap.values());
        Collections.sort(this.mApps, this.mAppNameComparator.getAppInfoComparator());
        if (this.mLauncher.getResources().getConfiguration().locale.equals(Locale.SIMPLIFIED_CHINESE)) {
            TreeMap treeMap = new TreeMap(this.mAppNameComparator.getSectionNameComparator());
            for (AppInfo appInfo : this.mApps) {
                String andUpdateCachedSectionName = getAndUpdateCachedSectionName(appInfo.title);
                ArrayList arrayList = (ArrayList) treeMap.get(andUpdateCachedSectionName);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    treeMap.put(andUpdateCachedSectionName, arrayList);
                }
                arrayList.add(appInfo);
            }
            ArrayList arrayList2 = new ArrayList(this.mApps.size());
            Iterator it = treeMap.entrySet().iterator();
            while (it.hasNext()) {
                arrayList2.addAll((Collection) ((Map.Entry) it.next()).getValue());
            }
            this.mApps.clear();
            this.mApps.addAll(arrayList2);
        } else {
            Iterator<AppInfo> it2 = this.mApps.iterator();
            while (it2.hasNext()) {
                getAndUpdateCachedSectionName(it2.next().title);
            }
        }
        updateAdapterItems();
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00d7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void updateAdapterItems() {
        /*
            r15 = this;
            java.util.List<com.android.launcher3.model.data.AppInfo> r0 = r15.mFilteredApps
            r0.clear()
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo> r0 = r15.mFastScrollerSections
            r0.clear()
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r0 = r15.mAdapterItems
            r0.clear()
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$SectionInfo> r0 = r15.mSections
            r0.clear()
            java.util.List<com.android.launcher3.model.data.AppInfo> r0 = r15.mPredictedApps
            r0.clear()
            java.util.List<com.android.launcher3.util.ComponentKey> r0 = r15.mPredictedAppComponents
            r1 = 0
            r2 = 0
            r3 = 1
            if (r0 == 0) goto Ld7
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto Ld7
            boolean r0 = r15.hasFilter()
            if (r0 != 0) goto Ld7
            java.util.List<com.android.launcher3.util.ComponentKey> r0 = r15.mPredictedAppComponents
            java.util.Iterator r0 = r0.iterator()
        L32:
            boolean r4 = r0.hasNext()
            if (r4 == 0) goto L74
            java.lang.Object r4 = r0.next()
            com.android.launcher3.util.ComponentKey r4 = (com.android.launcher3.util.ComponentKey) r4
            java.util.HashMap<com.android.launcher3.util.ComponentKey, com.android.launcher3.model.data.AppInfo> r5 = r15.mComponentToAppMap
            java.lang.Object r5 = r5.get(r4)
            com.android.launcher3.model.data.AppInfo r5 = (com.android.launcher3.model.data.AppInfo) r5
            if (r5 == 0) goto L4e
            java.util.List<com.android.launcher3.model.data.AppInfo> r4 = r15.mPredictedApps
            r4.add(r5)
            goto L6a
        L4e:
            boolean r5 = com.android.launcher3.LauncherAppState.isDogfoodBuild()
            if (r5 == 0) goto L6a
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Predicted app not found: "
            r5.append(r6)
            r5.append(r4)
            java.lang.String r4 = r5.toString()
            java.lang.String r5 = "AlphabeticalAppsList"
            android.util.Log.e(r5, r4)
        L6a:
            java.util.List<com.android.launcher3.model.data.AppInfo> r4 = r15.mPredictedApps
            int r4 = r4.size()
            int r5 = r15.mNumPredictedAppsPerRow
            if (r4 != r5) goto L32
        L74:
            java.util.List<com.android.launcher3.model.data.AppInfo> r0 = r15.mPredictedApps
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto Ld7
            com.android.launcher3.allapps.AlphabeticalAppsList$SectionInfo r0 = new com.android.launcher3.allapps.AlphabeticalAppsList$SectionInfo
            r0.<init>()
            com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo r10 = new com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo
            java.lang.String r4 = ""
            r10.<init>(r4)
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r4 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asSectionBreak(r2, r0)
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$SectionInfo> r5 = r15.mSections
            r5.add(r0)
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo> r5 = r15.mFastScrollerSections
            r5.add(r10)
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r5 = r15.mAdapterItems
            r5.add(r4)
            java.util.List<com.android.launcher3.model.data.AppInfo> r4 = r15.mPredictedApps
            java.util.Iterator r11 = r4.iterator()
            r9 = r2
            r4 = r3
        La3:
            boolean r5 = r11.hasNext()
            if (r5 == 0) goto Ldb
            java.lang.Object r5 = r11.next()
            r12 = r5
            com.android.launcher3.model.data.AppInfo r12 = (com.android.launcher3.model.data.AppInfo) r12
            int r13 = r4 + 1
            int r7 = r0.numApps
            int r5 = r7 + 1
            r0.numApps = r5
            int r14 = r9 + 1
            java.lang.String r6 = ""
            r5 = r0
            r8 = r12
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r4 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asPredictedApp(r4, r5, r6, r7, r8, r9)
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r5 = r0.firstAppItem
            if (r5 != 0) goto Lca
            r0.firstAppItem = r4
            r10.fastScrollToItem = r4
        Lca:
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r5 = r15.mAdapterItems
            r5.add(r4)
            java.util.List<com.android.launcher3.model.data.AppInfo> r4 = r15.mFilteredApps
            r4.add(r12)
            r4 = r13
            r9 = r14
            goto La3
        Ld7:
            r0 = r1
            r10 = r0
            r4 = r2
            r9 = r4
        Ldb:
            java.util.List r5 = r15.getFiltersAppInfos()
            java.util.Iterator r5 = r5.iterator()
            r11 = r9
        Le4:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L152
            java.lang.Object r6 = r5.next()
            r12 = r6
            com.android.launcher3.model.data.AppInfo r12 = (com.android.launcher3.model.data.AppInfo) r12
            java.lang.CharSequence r6 = r12.title
            java.lang.String r8 = r15.getAndUpdateCachedSectionName(r6)
            if (r0 == 0) goto L103
            boolean r6 = r8.equals(r1)
            if (r6 != 0) goto L100
            goto L103
        L100:
            r6 = r4
            r4 = r10
            goto L12c
        L103:
            com.android.launcher3.allapps.AlphabeticalAppsList$SectionInfo r0 = new com.android.launcher3.allapps.AlphabeticalAppsList$SectionInfo
            r0.<init>()
            com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo r1 = new com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo
            r1.<init>(r8)
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$SectionInfo> r6 = r15.mSections
            r6.add(r0)
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo> r6 = r15.mFastScrollerSections
            r6.add(r1)
            boolean r6 = r15.hasFilter()
            if (r6 != 0) goto L129
            int r6 = r4 + 1
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r4 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asSectionBreak(r4, r0)
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r7 = r15.mAdapterItems
            r7.add(r4)
            goto L12a
        L129:
            r6 = r4
        L12a:
            r4 = r1
            r1 = r8
        L12c:
            int r13 = r6 + 1
            int r9 = r0.numApps
            int r7 = r9 + 1
            r0.numApps = r7
            int r14 = r11 + 1
            r7 = r0
            r10 = r12
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r6 = com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem.asApp(r6, r7, r8, r9, r10, r11)
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r7 = r0.firstAppItem
            if (r7 != 0) goto L144
            r0.firstAppItem = r6
            r4.fastScrollToItem = r6
        L144:
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r7 = r15.mAdapterItems
            r7.add(r6)
            java.util.List<com.android.launcher3.model.data.AppInfo> r6 = r15.mFilteredApps
            r6.add(r12)
            r10 = r4
            r4 = r13
            r11 = r14
            goto Le4
        L152:
            r15.mergeSections()
            int r0 = r15.mNumAppsPerRow
            if (r0 == 0) goto L1c8
            r0 = -1
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem> r1 = r15.mAdapterItems
            java.util.Iterator r1 = r1.iterator()
            r4 = r2
            r5 = r4
        L162:
            boolean r6 = r1.hasNext()
            r7 = 2
            if (r6 == 0) goto L190
            java.lang.Object r6 = r1.next()
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r6 = (com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem) r6
            r6.rowIndex = r2
            int r8 = r6.viewType
            if (r8 != 0) goto L177
            r4 = r2
            goto L162
        L177:
            int r8 = r6.viewType
            if (r8 == r3) goto L17f
            int r8 = r6.viewType
            if (r8 != r7) goto L162
        L17f:
            int r7 = r15.mNumAppsPerRow
            int r7 = r4 % r7
            if (r7 != 0) goto L188
            int r0 = r0 + 1
            r5 = r2
        L188:
            r6.rowIndex = r0
            r6.rowAppIndex = r5
            int r4 = r4 + 1
            int r5 = r5 + r3
            goto L162
        L190:
            int r0 = r0 + r3
            r15.mNumAppRowsInAdapter = r0
            r1 = 1065353216(0x3f800000, float:1.0)
            float r0 = (float) r0
            float r1 = r1 / r0
            java.util.List<com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo> r0 = r15.mFastScrollerSections
            java.util.Iterator r0 = r0.iterator()
        L19d:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L1c8
            java.lang.Object r2 = r0.next()
            com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo r2 = (com.android.launcher3.allapps.AlphabeticalAppsList.FastScrollSectionInfo) r2
            com.android.launcher3.allapps.AlphabeticalAppsList$AdapterItem r4 = r2.fastScrollToItem
            int r5 = r4.viewType
            if (r5 == r3) goto L1b7
            int r5 = r4.viewType
            if (r5 == r7) goto L1b7
            r4 = 0
            r2.touchFraction = r4
            goto L19d
        L1b7:
            int r5 = r4.rowAppIndex
            float r5 = (float) r5
            int r6 = r15.mNumAppsPerRow
            float r6 = (float) r6
            float r6 = r1 / r6
            float r5 = r5 * r6
            int r4 = r4.rowIndex
            float r4 = (float) r4
            float r4 = r4 * r1
            float r4 = r4 + r5
            r2.touchFraction = r4
            goto L19d
        L1c8:
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r15.mAdapter
            if (r0 == 0) goto L1cf
            r0.notifyDataSetChanged()
        L1cf:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.AlphabeticalAppsList.updateAdapterItems():void");
    }

    private List<AppInfo> getFiltersAppInfos() {
        if (this.mSearchResults == null) {
            return this.mApps;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<ComponentKey> it = this.mSearchResults.iterator();
        while (it.hasNext()) {
            AppInfo appInfo = this.mComponentToAppMap.get(it.next());
            if (appInfo != null) {
                arrayList.add(appInfo);
            }
        }
        return arrayList;
    }

    private void mergeSections() {
        if (this.mMergeAlgorithm == null || this.mNumAppsPerRow == 0 || hasFilter()) {
            return;
        }
        for (int i = 0; i < this.mSections.size() - 1; i++) {
            SectionInfo sectionInfo = this.mSections.get(i);
            int i2 = 1;
            int i3 = sectionInfo.numApps;
            while (i < this.mSections.size() - 1) {
                int i4 = i + 1;
                if (this.mMergeAlgorithm.continueMerging(sectionInfo, this.mSections.get(i4), i3, this.mNumAppsPerRow, i2)) {
                    SectionInfo sectionInfoRemove = this.mSections.remove(i4);
                    this.mAdapterItems.remove(sectionInfoRemove.sectionBreakItem);
                    int iIndexOf = this.mAdapterItems.indexOf(sectionInfo.firstAppItem) + sectionInfo.numApps;
                    for (int i5 = iIndexOf; i5 < sectionInfoRemove.numApps + iIndexOf; i5++) {
                        AdapterItem adapterItem = this.mAdapterItems.get(i5);
                        adapterItem.sectionInfo = sectionInfo;
                        adapterItem.sectionAppIndex += sectionInfo.numApps;
                    }
                    for (int iIndexOf2 = this.mAdapterItems.indexOf(sectionInfoRemove.firstAppItem); iIndexOf2 < this.mAdapterItems.size(); iIndexOf2++) {
                        this.mAdapterItems.get(iIndexOf2).position--;
                    }
                    sectionInfo.numApps += sectionInfoRemove.numApps;
                    i3 += sectionInfoRemove.numApps;
                    i2++;
                }
            }
        }
    }

    private String getAndUpdateCachedSectionName(CharSequence title) {
        String str = this.mCachedSectionNames.get(title);
        if (str != null) {
            return str;
        }
        String strComputeSectionName = this.mIndexer.computeSectionName(title);
        this.mCachedSectionNames.put(title, strComputeSectionName);
        return strComputeSectionName;
    }
}
