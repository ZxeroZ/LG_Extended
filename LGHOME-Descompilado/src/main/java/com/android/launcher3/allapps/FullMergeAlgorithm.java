package com.android.launcher3.allapps;

import com.android.launcher3.allapps.AlphabeticalAppsList;

/* JADX INFO: compiled from: AllAppsContainerView.java */
/* JADX INFO: loaded from: classes.dex */
final class FullMergeAlgorithm implements AlphabeticalAppsList.MergeAlgorithm {
    FullMergeAlgorithm() {
    }

    @Override // com.android.launcher3.allapps.AlphabeticalAppsList.MergeAlgorithm
    public boolean continueMerging(AlphabeticalAppsList.SectionInfo section, AlphabeticalAppsList.SectionInfo withSection, int sectionAppCount, int numAppsPerRow, int mergeCount) {
        return section.firstAppItem.viewType == 1;
    }
}
