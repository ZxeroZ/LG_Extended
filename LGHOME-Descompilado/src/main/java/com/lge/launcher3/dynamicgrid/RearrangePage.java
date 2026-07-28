package com.lge.launcher3.dynamicgrid;

import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.util.LGLog;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class RearrangePage {
    private static final long NEW_SCREEN_ID = -1000;
    private static final String TAG = "RearrangePage";
    private int mColumns;
    private boolean[][] mOcuppiedArray;
    private int mRows;
    Long mScreenId;
    private ArrayList<ItemInfo> mItems = new ArrayList<>();
    private ArrayList<ItemInfo> mDeleteItems = new ArrayList<>();
    ArrayList<RearrangePage> mSiblingPages = new ArrayList<>();
    private Comparator<ItemInfo> ItemInfoComparator = new Comparator<ItemInfo>() { // from class: com.lge.launcher3.dynamicgrid.RearrangePage.1
        private int compareID(long aID, long bID) {
            if (aID > bID) {
                return -1;
            }
            return aID < bID ? 1 : 0;
        }

        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public final int compare(ItemInfo first, ItemInfo second) {
            if (first == null || second == null) {
                LGLog.d(RearrangePage.TAG, "Comparing itemInfo is null : firstItemInfo:" + first + ", secondItemInfo :" + second);
                return 0;
            }
            long j = first.cellX;
            long j2 = second.cellX;
            long j3 = first.cellY;
            long j4 = second.cellY;
            long j5 = first.id;
            long j6 = second.id;
            if (j3 > j4) {
                return -1;
            }
            if (j3 < j4) {
                return 1;
            }
            return compareCellX(j, j2, j5, j6);
        }

        private int compareCellX(long aCellX, long bCellX, long aId, long bID) {
            if (aCellX > bCellX) {
                return -1;
            }
            if (aCellX < bCellX) {
                return 1;
            }
            return compareID(aId, bID);
        }
    };

    public RearrangePage(ArrayList<ItemInfo> items, int columns, int rows, long screenId) {
        this.mRows = -1;
        this.mColumns = -1;
        this.mScreenId = -1L;
        this.mRows = rows;
        this.mColumns = columns;
        this.mScreenId = Long.valueOf(screenId);
        this.mOcuppiedArray = (boolean[][]) Array.newInstance((Class<?>) boolean.class, this.mColumns, this.mRows);
        for (int i = 0; i < this.mColumns; i++) {
            for (int i2 = 0; i2 < this.mRows; i2++) {
                this.mOcuppiedArray[i][i2] = false;
            }
        }
        resizeItems(items);
        Collections.sort(items, this.ItemInfoComparator);
        matchItemToPage(items);
    }

    private void resizeItems(ArrayList<ItemInfo> items) {
        for (int size = items.size() - 1; size >= 0; size--) {
            ItemInfo itemInfo = items.get(size);
            if (needResize(itemInfo)) {
                resize(itemInfo);
            }
        }
    }

    private void matchItemToPage(ArrayList<ItemInfo> items) {
        for (int size = items.size() - 1; size >= 0; size--) {
            ItemInfo itemInfo = items.get(size);
            if (isExceedBound(itemInfo)) {
                if (itemInfo.spanX > this.mColumns || itemInfo.spanY > this.mRows) {
                    addRemoveItemList(itemInfo);
                } else {
                    addItemOnSiblingPage(itemInfo);
                }
            } else {
                addItem(itemInfo);
            }
        }
    }

    private void addRemoveItemList(ItemInfo info) {
        info.spanX = -1;
        info.spanY = -1;
        this.mDeleteItems.add(info);
    }

    private boolean needResize(ItemInfo info) {
        if (info.cellX != 0 || info.spanX <= this.mColumns) {
            return info.cellY == 0 && info.spanY > this.mRows;
        }
        return true;
    }

    private void resize(ItemInfo info) {
        if (info.spanX > this.mColumns) {
            int i = info.minSpanX;
            int i2 = this.mColumns;
            if (i <= i2) {
                info.spanX = i2;
                info.requiresDbUpdate = true;
            }
        }
        if (info.spanY > this.mRows) {
            int i3 = info.minSpanY;
            int i4 = this.mRows;
            if (i3 <= i4) {
                info.spanY = i4;
                info.requiresDbUpdate = true;
            }
        }
    }

    private void addItem(ItemInfo info) {
        this.mItems.add(info);
        setOcuppiedArray(info);
    }

    private void setOcuppiedArray(ItemInfo info) {
        for (int i = info.cellX; i < info.cellX + info.spanX; i++) {
            for (int i2 = info.cellY; i2 < info.cellY + info.spanY; i2++) {
                this.mOcuppiedArray[i][i2] = true;
            }
        }
    }

    private boolean isExceedBound(ItemInfo info) {
        return info.cellX + info.spanX > this.mColumns || info.cellY + info.spanY > this.mRows;
    }

    private void addItemOnSiblingPage(ItemInfo info) {
        if (getLastSiblingPage().insertAfterRearragne(info)) {
            return;
        }
        makeNewSiblingPage().insertAfterRearragne(info);
    }

    private RearrangePage makeNewSiblingPage() {
        RearrangePage rearrangePage = new RearrangePage(new ArrayList(), this.mColumns, this.mRows, NEW_SCREEN_ID);
        this.mSiblingPages.add(rearrangePage);
        return rearrangePage;
    }

    private RearrangePage getLastSiblingPage() {
        if (this.mSiblingPages.size() == 0) {
            makeNewSiblingPage();
        }
        return this.mSiblingPages.get(r0.size() - 1);
    }

    private boolean insertAfterRearragne(ItemInfo info) {
        int[] iArrFindEmptyPosition = findEmptyPosition(info);
        if (iArrFindEmptyPosition[0] == -1 && iArrFindEmptyPosition[1] == -1) {
            return false;
        }
        info.cellX = iArrFindEmptyPosition[0];
        info.cellY = iArrFindEmptyPosition[1];
        info.requiresDbUpdate = true;
        addItem(info);
        return true;
    }

    private int[] findEmptyPosition(ItemInfo info) {
        int[] iArr = {-1, -1};
        for (int i = 0; i < this.mRows; i++) {
            for (int i2 = 0; i2 < this.mColumns; i2++) {
                if (!this.mOcuppiedArray[i2][i] && isVailidPosition(info, i2, i)) {
                    iArr[0] = i2;
                    iArr[1] = i;
                    return iArr;
                }
            }
        }
        return iArr;
    }

    private boolean isVailidPosition(ItemInfo info, int cellX, int cellY) {
        for (int i = cellY; i < info.spanY + cellY; i++) {
            for (int i2 = cellX; i2 < info.spanX + cellX; i2++) {
                if (i >= this.mRows || i2 >= this.mColumns || this.mOcuppiedArray[i2][i]) {
                    return false;
                }
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Page(mScreenId):" + this.mScreenId + " ------------------- start\n");
        for (int i = 0; i < this.mRows; i++) {
            sb.append("[ ");
            for (int i2 = 0; i2 < this.mColumns; i2++) {
                sb.append(" " + (this.mOcuppiedArray[i2][i] ? "O" : "X") + " ");
            }
            sb.append(" ]\n");
        }
        for (RearrangePage rearrangePage : this.mSiblingPages) {
            sb.append("Sibling Page(" + this.mSiblingPages.indexOf(rearrangePage) + ") ===> \n");
            sb.append(rearrangePage.toString());
        }
        sb.append("Page(mScreenId):" + this.mScreenId + " ------------------- end\n");
        return sb.toString();
    }

    public void setScreenId(Long newScreenId) {
        this.mScreenId = newScreenId;
    }

    public void setScreenIdAllItems(Long newScreenId) {
        for (ItemInfo itemInfo : this.mItems) {
            itemInfo.screenId = newScreenId.longValue();
            itemInfo.requiresDbUpdate = true;
        }
    }

    public void getModifiedItems(ArrayList<ItemInfo> items) {
        for (ItemInfo itemInfo : this.mItems) {
            if (itemInfo.requiresDbUpdate) {
                items.add(itemInfo);
            }
        }
        Iterator<RearrangePage> it = this.mSiblingPages.iterator();
        while (it.hasNext()) {
            it.next().getModifiedItems(items);
        }
    }

    public void getDeleteItems(ArrayList<ItemInfo> deleteItems) {
        Iterator<ItemInfo> it = this.mDeleteItems.iterator();
        while (it.hasNext()) {
            deleteItems.add(it.next());
        }
        Iterator<RearrangePage> it2 = this.mSiblingPages.iterator();
        while (it2.hasNext()) {
            it2.next().getDeleteItems(deleteItems);
        }
    }
}
