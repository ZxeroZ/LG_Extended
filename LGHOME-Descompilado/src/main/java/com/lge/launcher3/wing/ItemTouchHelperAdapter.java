package com.lge.launcher3.wing;

import com.android.launcher3.ShortcutInfo;

/* JADX INFO: loaded from: classes2.dex */
public interface ItemTouchHelperAdapter {
    void onItemDismiss(int pos);

    void onItemInsert(ShortcutInfo info, int pos);

    void onItemMove(int fromPos, int targetPos);
}
