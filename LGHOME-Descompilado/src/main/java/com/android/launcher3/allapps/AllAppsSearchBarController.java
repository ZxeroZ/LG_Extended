package com.android.launcher3.allapps;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.util.ComponentKey;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class AllAppsSearchBarController {
    protected AlphabeticalAppsList mApps;
    protected Callbacks mCb;

    public interface Callbacks {
        void clearSearchResult();

        void onBoundsChanged(Rect newBounds);

        void onSearchResult(String query, ArrayList<ComponentKey> apps);
    }

    public abstract void focusSearchField();

    public abstract View getView(ViewGroup parent);

    public abstract boolean isSearchFieldFocused();

    protected abstract void onInitialize();

    public abstract void reset();

    @Deprecated
    public abstract boolean shouldShowPredictionBar();

    public final void initialize(AlphabeticalAppsList apps, Callbacks cb) {
        this.mApps = apps;
        this.mCb = cb;
        onInitialize();
    }
}
