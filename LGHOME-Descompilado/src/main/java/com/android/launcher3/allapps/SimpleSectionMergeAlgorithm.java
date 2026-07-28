package com.android.launcher3.allapps;

import com.android.launcher3.allapps.AlphabeticalAppsList;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/* JADX INFO: compiled from: AllAppsContainerView.java */
/* JADX INFO: loaded from: classes.dex */
final class SimpleSectionMergeAlgorithm implements AlphabeticalAppsList.MergeAlgorithm {
    private CharsetEncoder mAsciiEncoder = Charset.forName("US-ASCII").newEncoder();
    private int mMaxAllowableMerges;
    private int mMinAppsPerRow;
    private int mMinRowsInMergedSection;

    public SimpleSectionMergeAlgorithm(int minAppsPerRow, int minRowsInMergedSection, int maxNumMerges) {
        this.mMinAppsPerRow = minAppsPerRow;
        this.mMinRowsInMergedSection = minRowsInMergedSection;
        this.mMaxAllowableMerges = maxNumMerges;
    }

    @Override // com.android.launcher3.allapps.AlphabeticalAppsList.MergeAlgorithm
    public boolean continueMerging(AlphabeticalAppsList.SectionInfo section, AlphabeticalAppsList.SectionInfo withSection, int sectionAppCount, int numAppsPerRow, int mergeCount) {
        if (section.firstAppItem.viewType != 1) {
            return false;
        }
        int i = sectionAppCount / numAppsPerRow;
        int i2 = sectionAppCount % numAppsPerRow;
        return i2 > 0 && i2 < this.mMinAppsPerRow && i < this.mMinRowsInMergedSection && mergeCount < this.mMaxAllowableMerges && !(section.firstAppItem != null && withSection.firstAppItem != null && this.mAsciiEncoder.canEncode(section.firstAppItem.sectionName) != this.mAsciiEncoder.canEncode(withSection.firstAppItem.sectionName));
    }
}
