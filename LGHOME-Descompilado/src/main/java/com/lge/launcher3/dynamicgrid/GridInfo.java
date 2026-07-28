package com.lge.launcher3.dynamicgrid;

import android.util.Log;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.LongArrayMap;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class GridInfo {
    private int mColumns;
    private LongArrayMap<ItemInfo> mInputItems;
    private ArrayList<Long> mPageOrders;
    private int mRows;
    private LongArrayMap<RearrangePage> mPages = new LongArrayMap<>();
    private Long mMaxScreenId = -1L;

    public GridInfo(LongArrayMap<ItemInfo> items, int columns, int rows, ArrayList<Long> pageOrders) {
        this.mPageOrders = null;
        this.mRows = rows;
        this.mColumns = columns;
        this.mPageOrders = pageOrders != null ? (ArrayList) pageOrders.clone() : null;
        this.mInputItems = cloneMap(items);
        initMaxScreenId(pageOrders);
        makePages(this.mColumns, this.mRows);
        allocateNewScreenId();
    }

    private void initMaxScreenId(ArrayList<Long> pageOrders) {
        if (pageOrders != null) {
            for (Long l : pageOrders) {
                if (this.mMaxScreenId.longValue() < l.longValue()) {
                    this.mMaxScreenId = l;
                }
            }
        }
    }

    private void allocateNewScreenId() {
        for (RearrangePage rearrangePage : this.mPages) {
            for (RearrangePage rearrangePage2 : rearrangePage.mSiblingPages) {
                Long lGenerateNewScreenId = generateNewScreenId();
                rearrangePage2.setScreenId(lGenerateNewScreenId);
                rearrangePage2.setScreenIdAllItems(lGenerateNewScreenId);
                insertNewScreenId(rearrangePage.mScreenId, lGenerateNewScreenId);
            }
        }
    }

    private void insertNewScreenId(Long mScreenId, Long newScreenId) {
        for (int i = 0; i < this.mPageOrders.size(); i++) {
            if (this.mPageOrders.get(i) == mScreenId) {
                this.mPageOrders.add(i + 1, newScreenId);
                return;
            }
        }
    }

    private Long generateNewScreenId() {
        Long lValueOf = Long.valueOf(this.mMaxScreenId.longValue() + 1);
        this.mMaxScreenId = lValueOf;
        return lValueOf;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v6, resolved type: E */
    /* JADX WARN: Multi-variable type inference failed */
    private void makePages(int columns, int rows) {
        LongArrayMap<ArrayList> longArrayMap = new LongArrayMap();
        for (ItemInfo itemInfo : this.mInputItems) {
            if (longArrayMap.get(itemInfo.screenId) == 0) {
                longArrayMap.put(itemInfo.screenId, new ArrayList());
            }
            ((ArrayList) longArrayMap.get(itemInfo.screenId)).add(itemInfo);
        }
        for (ArrayList arrayList : longArrayMap) {
            this.mPages.put(longArrayMap.keyAt(longArrayMap.indexOfValue(arrayList)), new RearrangePage(arrayList, columns, rows, longArrayMap.keyAt(longArrayMap.indexOfValue(arrayList))));
        }
    }

    public LongArrayMap<ItemInfo> printAllItems() {
        Iterator<RearrangePage> it = this.mPages.iterator();
        while (it.hasNext()) {
            Log.e("GridPageInfo", it.next().toString());
        }
        return null;
    }

    private LongArrayMap<ItemInfo> cloneMap(LongArrayMap<ItemInfo> items) {
        LongArrayMap<ItemInfo> longArrayMap = new LongArrayMap<>();
        Iterator<ItemInfo> it = items.iterator();
        while (it.hasNext()) {
            longArrayMap.put(items.indexOfValue(r2), copyItemInfo(it.next()));
        }
        return longArrayMap;
    }

    private ItemInfo copyItemInfo(ItemInfo info) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.copyFrom(info);
        return itemInfo;
    }

    public ArrayList<ItemInfo> getModifiedItems() {
        ArrayList<ItemInfo> arrayList = new ArrayList<>();
        Iterator<RearrangePage> it = this.mPages.iterator();
        while (it.hasNext()) {
            it.next().getModifiedItems(arrayList);
        }
        return arrayList;
    }

    public ArrayList<ItemInfo> getDeleteItems() {
        ArrayList<ItemInfo> arrayList = new ArrayList<>();
        Iterator<RearrangePage> it = this.mPages.iterator();
        while (it.hasNext()) {
            it.next().getDeleteItems(arrayList);
        }
        return arrayList;
    }

    public ArrayList<Long> getPageOrders() {
        return this.mPageOrders;
    }

    public ItemInfo getItemInfo(long itemId) {
        for (ItemInfo itemInfo : this.mInputItems) {
            if (itemInfo.id == itemId) {
                return itemInfo;
            }
        }
        return null;
    }

    public long getRepresentScreenId(long screenId) {
        for (RearrangePage rearrangePage : this.mPages) {
            if (rearrangePage.mScreenId.longValue() == screenId) {
                return rearrangePage.mScreenId.longValue();
            }
            Iterator<RearrangePage> it = rearrangePage.mSiblingPages.iterator();
            while (it.hasNext()) {
                if (it.next().mScreenId.longValue() == screenId) {
                    return rearrangePage.mScreenId.longValue();
                }
            }
        }
        return screenId;
    }
}
