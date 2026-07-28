package com.android.launcher3.uioverrides;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.systemui.shared.system.TonalCompat;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperColorInfo implements WallpaperManager.OnColorsChangedListener {
    public static final MainThreadInitializedObject<WallpaperColorInfo> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.launcher3.uioverrides.-$$Lambda$WallpaperColorInfo$fGO3As6TKFGu_6vfPXpJQHIfcTA
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return WallpaperColorInfo.lambda$fGO3As6TKFGu_6vfPXpJQHIfcTA(context);
        }
    });
    private static final int MAIN_COLOR_DARK = -14671580;
    private static final int MAIN_COLOR_LIGHT = -2433824;
    private static final int MAIN_COLOR_REGULAR = -16777216;
    private TonalCompat.ExtractionInfo mExtractionInfo;
    private final ArrayList<OnChangeListener> mListeners = new ArrayList<>();
    private OnChangeListener[] mTempListeners = new OnChangeListener[0];
    private final TonalCompat mTonalCompat;
    private final WallpaperManager mWallpaperManager;

    public interface OnChangeListener {
        void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo);
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR (r1v0 android.content.Context) A[MD:(android.content.Context):void (m)] call: com.android.launcher3.uioverrides.WallpaperColorInfo.<init>(android.content.Context):void type: CONSTRUCTOR */
    public static /* synthetic */ WallpaperColorInfo lambda$fGO3As6TKFGu_6vfPXpJQHIfcTA(Context context) {
        return new WallpaperColorInfo(context);
    }

    private WallpaperColorInfo(Context context) {
        WallpaperManager wallpaperManager = (WallpaperManager) context.getSystemService(WallpaperManager.class);
        this.mWallpaperManager = wallpaperManager;
        this.mTonalCompat = new TonalCompat(context);
        wallpaperManager.addOnColorsChangedListener(this, new Handler(Looper.getMainLooper()));
        update(wallpaperManager.getWallpaperColors(1));
    }

    public int getMainColor() {
        return this.mExtractionInfo.mainColor;
    }

    public int getSecondaryColor() {
        return this.mExtractionInfo.secondaryColor;
    }

    public boolean isDark() {
        return this.mExtractionInfo.supportsDarkTheme;
    }

    public boolean supportsDarkText() {
        return this.mExtractionInfo.supportsDarkText;
    }

    public boolean isMainColorDark() {
        return this.mExtractionInfo.mainColor == MAIN_COLOR_DARK;
    }

    @Override // android.app.WallpaperManager.OnColorsChangedListener
    public void onColorsChanged(WallpaperColors colors, int which) {
        if ((which & 1) != 0) {
            update(colors);
            notifyChange();
        }
    }

    private void update(WallpaperColors wallpaperColors) {
        this.mExtractionInfo = this.mTonalCompat.extractDarkColors(wallpaperColors);
    }

    public void addOnChangeListener(OnChangeListener listener) {
        this.mListeners.add(listener);
    }

    public void removeOnChangeListener(OnChangeListener listener) {
        this.mListeners.remove(listener);
    }

    private void notifyChange() {
        OnChangeListener[] onChangeListenerArr = (OnChangeListener[]) this.mListeners.toArray(this.mTempListeners);
        this.mTempListeners = onChangeListenerArr;
        for (OnChangeListener onChangeListener : onChangeListenerArr) {
            if (onChangeListener != null) {
                onChangeListener.onExtractedColorsChanged(this);
            }
        }
    }
}
