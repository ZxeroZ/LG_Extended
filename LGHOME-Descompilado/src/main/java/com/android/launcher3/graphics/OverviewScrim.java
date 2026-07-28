package com.android.launcher3.graphics;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.LauncherState;

/* JADX INFO: loaded from: classes.dex */
public class OverviewScrim extends Scrim {
    private View mCurrentScrimmedView;
    private View mStableScrimmedView;

    public OverviewScrim(View view) {
        super(view);
        View overviewPanel = this.mLauncher.getOverviewPanel();
        this.mCurrentScrimmedView = overviewPanel;
        this.mStableScrimmedView = overviewPanel;
        onExtractedColorsChanged(this.mWallpaperColorInfo);
    }

    public void onInsetsChanged(Rect insets) {
        View overviewPanel;
        if ((LauncherState.OVERVIEW.getVisibleElements(this.mLauncher) & 1) != 0) {
            overviewPanel = this.mLauncher.getHotseat();
        } else {
            overviewPanel = this.mLauncher.getOverviewPanel();
        }
        this.mStableScrimmedView = overviewPanel;
    }

    public void updateCurrentScrimmedView(ViewGroup root) {
        View view = this.mStableScrimmedView;
        this.mCurrentScrimmedView = view;
        int iIndexOfChild = root.indexOfChild(view);
        int childCount = root.getChildCount();
        while (true) {
            View view2 = this.mCurrentScrimmedView;
            if (view2 == null || view2.getVisibility() == 0 || iIndexOfChild >= childCount) {
                return;
            }
            iIndexOfChild++;
            this.mCurrentScrimmedView = root.getChildAt(iIndexOfChild);
        }
    }

    public View getScrimmedView() {
        return this.mCurrentScrimmedView;
    }
}
