package com.lge.launcher3.wallpaperpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Gallery;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperChooserGallery extends Gallery {
    public WallpaperChooserGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WallpaperChooserGallery(Context context) {
        super(context);
    }

    @Override // android.widget.Gallery, android.view.ViewGroup
    protected int getChildDrawingOrder(int childCount, int i) {
        int selectedItemPosition = getSelectedItemPosition() - getFirstVisiblePosition();
        if (selectedItemPosition < 0) {
            return i;
        }
        if (i == childCount - 1) {
            return selectedItemPosition;
        }
        if (i != childCount - 2) {
            return i >= selectedItemPosition ? i + 2 : i;
        }
        int i2 = selectedItemPosition + 1;
        return i2 == childCount ? i : i2;
    }
}
