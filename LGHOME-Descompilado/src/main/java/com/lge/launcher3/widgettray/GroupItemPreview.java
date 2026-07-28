package com.lge.launcher3.widgettray;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/* JADX INFO: loaded from: classes.dex */
public class GroupItemPreview extends ImageView implements PreviewAppliable {
    private static final int FADE_IN_DURATION_MS = 90;

    public GroupItemPreview(Context context) {
        this(context, null);
    }

    public GroupItemPreview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupItemPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GroupItemPreview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // com.lge.launcher3.widgettray.PreviewAppliable
    public void applyPreview(Bitmap bitmap) {
        if (bitmap != null) {
            setImageBitmap(bitmap);
            setAlpha(0.0f);
            animate().alpha(1.0f).setDuration(90L);
        }
    }
}
