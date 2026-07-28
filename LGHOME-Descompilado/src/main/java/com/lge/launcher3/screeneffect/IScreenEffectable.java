package com.lge.launcher3.screeneffect;

import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
public interface IScreenEffectable {
    Bitmap getChildrenDrawingCache(boolean autoScale);

    ScreenEffectBase getCustomScreenEffect();

    int getShortcutAndWidgetLayer();

    void setCustomScreenEffect(ScreenEffectBase screenEffect);

    void setShortcutAndWidgetAlpha(float alpha);
}
