package com.android.launcher3.model;

import android.content.Context;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import java.text.Collator;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class AppNameComparator {
    private final AbstractUserComparator<ItemInfo> mAppInfoComparator;
    private final Collator mCollator = Collator.getInstance();
    private final Comparator<String> mSectionNameComparator = new Comparator<String>() { // from class: com.android.launcher3.model.AppNameComparator.2
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(String o1, String o2) {
            return AppNameComparator.this.compareTitles(o1, o2);
        }
    };

    public AppNameComparator(Context context) {
        this.mAppInfoComparator = new AbstractUserComparator<ItemInfo>(context) { // from class: com.android.launcher3.model.AppNameComparator.1
            /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
            @Override // com.android.launcher3.model.AbstractUserComparator, java.util.Comparator
            public final int compare(ItemInfo a, ItemInfo b) {
                int iCompareTitles = AppNameComparator.this.compareTitles(a.title.toString(), b.title.toString());
                return (iCompareTitles == 0 && (a instanceof AppInfo) && (b instanceof AppInfo) && (iCompareTitles = ((AppInfo) a).componentName.compareTo(((AppInfo) b).componentName)) == 0) ? super.compare(a, b) : iCompareTitles;
            }
        };
    }

    public Comparator<ItemInfo> getAppInfoComparator() {
        this.mAppInfoComparator.clearUserCache();
        return this.mAppInfoComparator;
    }

    public Comparator<String> getSectionNameComparator() {
        return this.mSectionNameComparator;
    }

    int compareTitles(String titleA, String titleB) {
        boolean z = false;
        boolean z2 = titleA.length() > 0 && Character.isLetterOrDigit(titleA.codePointAt(0));
        if (titleB.length() > 0 && Character.isLetterOrDigit(titleB.codePointAt(0))) {
            z = true;
        }
        if (z2 && !z) {
            return -1;
        }
        if (z2 || !z) {
            return this.mCollator.compare(titleA, titleB);
        }
        return 1;
    }
}
