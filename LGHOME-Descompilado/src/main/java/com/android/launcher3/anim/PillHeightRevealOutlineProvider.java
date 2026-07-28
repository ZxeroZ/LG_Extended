package com.android.launcher3.anim;

import android.graphics.Rect;
import com.android.launcher3.util.PillRevealOutlineProvider;

/* JADX INFO: loaded from: classes.dex */
public class PillHeightRevealOutlineProvider extends PillRevealOutlineProvider {
    private final int mNewHeight;

    public PillHeightRevealOutlineProvider(Rect pillRect, float radius, int newHeight) {
        super(0, 0, pillRect, radius);
        this.mOutline.set(pillRect);
        this.mNewHeight = newHeight;
    }

    @Override // com.android.launcher3.util.PillRevealOutlineProvider
    public void setProgress(float progress) {
        this.mOutline.top = 0;
        this.mOutline.bottom = (int) (this.mPillRect.bottom - ((this.mPillRect.height() - this.mNewHeight) * (1.0f - progress)));
    }
}
