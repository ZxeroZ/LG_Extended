package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import androidx.core.graphics.ColorUtils;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.util.Themes;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class PlaceHolderIconDrawable extends FastBitmapDrawable {
    private final Path mProgressPath;

    public PlaceHolderIconDrawable(com.android.launcher3.icons.BitmapInfo info, Context context) {
        super(info);
        this.mProgressPath = IconShape.getShapePath();
        this.mPaint.setColor(ColorUtils.compositeColors(Themes.getAttrColor(context, R.attr.loadingIconColor), info.color));
    }

    @Override // com.android.launcher3.FastBitmapDrawable
    protected void drawInternal(Canvas canvas, Rect bounds) {
        int iSave = canvas.save();
        canvas.translate(bounds.left, bounds.top);
        canvas.scale(bounds.width() / 100.0f, bounds.height() / 100.0f);
        canvas.drawPath(this.mProgressPath, this.mPaint);
        canvas.restoreToCount(iSave);
    }
}
