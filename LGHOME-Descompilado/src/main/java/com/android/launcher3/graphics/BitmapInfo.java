package com.android.launcher3.graphics;

import android.graphics.Bitmap;
import com.android.launcher3.model.data.ItemInfoWithIcon;

/* JADX INFO: loaded from: classes.dex */
public class BitmapInfo {
    public int color;
    public Bitmap icon;

    public void applyTo(ItemInfoWithIcon info) {
        info.iconBitmap = this.icon;
        info.iconColor = this.color;
    }

    public void applyTo(BitmapInfo info) {
        info.icon = this.icon;
        info.color = this.color;
    }

    public static BitmapInfo fromBitmap(Bitmap bitmap) {
        BitmapInfo bitmapInfo = new BitmapInfo();
        bitmapInfo.icon = bitmap;
        bitmapInfo.color = ColorExtractor.findDominantColorByHue(bitmap);
        return bitmapInfo;
    }
}
