package com.android.launcher3.graphics;

import android.graphics.Canvas;
import android.util.FloatProperty;
import android.view.View;
import com.android.launcher3.Launcher;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.uioverrides.WallpaperColorInfo;

/* JADX INFO: loaded from: classes.dex */
public class Scrim implements View.OnAttachStateChangeListener, WallpaperColorInfo.OnChangeListener {
    public static final FloatProperty<Scrim> SCRIM_PROGRESS = new FloatProperty<Scrim>("scrimProgress") { // from class: com.android.launcher3.graphics.Scrim.1
        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(Scrim scrim) {
            return Float.valueOf(scrim.mScrimProgress);
        }

        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(Scrim scrim, float v) {
            scrim.setScrimProgress(v);
        }
    };
    protected final Launcher mLauncher;
    protected final View mRoot;
    protected int mScrimAlpha = 0;
    protected int mScrimColor;
    protected float mScrimProgress;
    protected final WallpaperColorInfo mWallpaperColorInfo;

    public Scrim(View view) {
        this.mRoot = view;
        Launcher launcher = Launcher.getLauncher(view.getContext());
        this.mLauncher = launcher;
        this.mWallpaperColorInfo = WallpaperColorInfo.INSTANCE.lambda$get$0$MainThreadInitializedObject(launcher);
        view.addOnAttachStateChangeListener(this);
    }

    public void draw(Canvas canvas) {
        canvas.drawColor(GraphicsUtils.setColorAlphaBound(this.mScrimColor, this.mScrimAlpha));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScrimProgress(float progress) {
        if (this.mScrimProgress != progress) {
            this.mScrimProgress = progress;
            this.mScrimAlpha = Math.round(progress * 255.0f);
            invalidate();
        }
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewAttachedToWindow(View view) {
        this.mWallpaperColorInfo.addOnChangeListener(this);
        onExtractedColorsChanged(this.mWallpaperColorInfo);
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewDetachedFromWindow(View view) {
        this.mWallpaperColorInfo.removeOnChangeListener(this);
    }

    @Override // com.android.launcher3.uioverrides.WallpaperColorInfo.OnChangeListener
    public void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo) {
        this.mScrimColor = wallpaperColorInfo.getMainColor();
        if (this.mScrimAlpha > 0) {
            invalidate();
        }
    }

    public void invalidate() {
        this.mRoot.invalidate();
    }
}
