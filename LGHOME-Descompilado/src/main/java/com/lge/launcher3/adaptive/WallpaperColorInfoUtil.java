package com.lge.launcher3.adaptive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.Settings;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperColorInfoUtil {
    private static final boolean DEBUG = false;
    private static final int MAX_SAMPLE_PIXELS = 20000;
    private static final String TAG = "WallpaperColorInfoUtil";
    private static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();
    private static WallpaperColorInfoUtil sInstance;
    private int[] mCount;
    private WallpaperColorInfo mWallpaperColorInfo;
    private OnSwivelWallpaperChangeListener[] mTempListeners = new OnSwivelWallpaperChangeListener[0];
    private final ArrayList<OnSwivelWallpaperChangeListener> mListeners = new ArrayList<>();

    public interface OnSwivelWallpaperChangeListener {
        void onSwivelWallpaperChanged();
    }

    public static WallpaperColorInfoUtil getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WallpaperColorInfoUtil(context);
        }
        return sInstance;
    }

    private WallpaperColorInfoUtil(Context context) {
        int integer = context.getResources().getInteger(R.integer.config_row_for_wallpaper_info);
        int integer2 = context.getResources().getInteger(R.integer.config_column_for_wallpaper_info);
        this.mCount = new int[integer * integer2];
        this.mWallpaperColorInfo = new WallpaperColorInfo(integer, integer2);
    }

    public void notifyChange() {
        OnSwivelWallpaperChangeListener[] onSwivelWallpaperChangeListenerArr = (OnSwivelWallpaperChangeListener[]) this.mListeners.toArray(this.mTempListeners);
        this.mTempListeners = onSwivelWallpaperChangeListenerArr;
        for (OnSwivelWallpaperChangeListener onSwivelWallpaperChangeListener : onSwivelWallpaperChangeListenerArr) {
            if (onSwivelWallpaperChangeListener != null) {
                onSwivelWallpaperChangeListener.onSwivelWallpaperChanged();
            }
        }
    }

    public void runMakeInfo(final Context context, final Runnable callback) {
        sExecutorService.submit(new Runnable() { // from class: com.lge.launcher3.adaptive.-$$Lambda$WallpaperColorInfoUtil$-opMvNf1CIrvZHqwvviduZemmjs
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$runMakeInfo$0$WallpaperColorInfoUtil(context, callback);
            }
        });
    }

    public /* synthetic */ void lambda$runMakeInfo$0$WallpaperColorInfoUtil(Context context, Runnable runnable) {
        makeInfo(context);
        if (runnable != null) {
            runnable.run();
        }
    }

    public int getBrightnessByDirection(Context context) {
        int brightness;
        int i;
        boolean zIsLiveWallpaperMode = AdaptiveTextUtil.isLiveWallpaperMode(context);
        int i2 = 0;
        if (zIsLiveWallpaperMode) {
            brightness = Settings.Secure.getIntForUser(context.getContentResolver(), LiveWallpaperColorObserver.URI_NAME, 0, -2);
            i = 0;
        } else {
            int rotation = WindowUtils.getRotation(context);
            if (rotation == 0) {
                i2 = 1;
            } else if (rotation == 1) {
                i2 = 3;
            } else if (rotation == 2) {
                i2 = 2;
            }
            brightness = getColorInfo().getBrightness(i2);
            i = i2;
            i2 = rotation;
        }
        LGLog.d(TAG, "getBrightnessByDirection : rotation = " + i2 + ", position = " + i + ", result = " + brightness + ", isLiveWallpaper = " + zIsLiveWallpaperMode + ", " + getInstance(context).getColorInfo());
        return brightness;
    }

    public WallpaperColorInfo getColorInfo() {
        return this.mWallpaperColorInfo;
    }

    private final int calcBrightness(int color) {
        return (Color.red(color) * 299) + (Color.green(color) * 587) + (Color.blue(color) * 114);
    }

    private int getSamplingOffset(int imagePixels) {
        int i = 1;
        while (imagePixels / (i * i) > MAX_SAMPLE_PIXELS) {
            i++;
        }
        return i;
    }

    public void makeInfo(Context context) {
        WallpaperRetreiver wallpaperRetreiver = new WallpaperRetreiver(context);
        Bitmap wallpaperBitmapForSwivel = wallpaperRetreiver.getWallpaperBitmapForSwivel();
        if (wallpaperBitmapForSwivel == null) {
            return;
        }
        int width = wallpaperBitmapForSwivel.getWidth();
        int height = wallpaperBitmapForSwivel.getHeight();
        int statusBarHeight = WindowUtils.getStatusBarHeight(context);
        int samplingOffset = getSamplingOffset((width * height) / this.mWallpaperColorInfo.size);
        int i = (width / samplingOffset) + 1;
        int i2 = (height / samplingOffset) + 1;
        int[] iArr = new int[width];
        int i3 = this.mWallpaperColorInfo.size;
        long[] jArr = new long[i3];
        int i4 = this.mWallpaperColorInfo.size;
        long[] jArr2 = new long[i4];
        long[] jArr3 = new long[2];
        int[] iArr2 = new int[2];
        int i5 = 0;
        while (i5 < height) {
            int i6 = i5;
            int[] iArr3 = iArr2;
            long[] jArr4 = jArr3;
            long[] jArr5 = jArr2;
            WallpaperRetreiver wallpaperRetreiver2 = wallpaperRetreiver;
            int i7 = i4;
            wallpaperBitmapForSwivel.getPixels(iArr, 0, width, 0, i6, width, 1);
            int i8 = 0;
            while (i8 < width) {
                int iCalcBrightness = calcBrightness(iArr[i8]);
                long[] jArr6 = jArr;
                int i9 = i3;
                int[] iArr4 = iArr;
                int i10 = statusBarHeight;
                int i11 = height;
                int i12 = width;
                int index = getIndex(i8, i6, width, height, this.mWallpaperColorInfo);
                long j = iCalcBrightness;
                jArr5[index] = jArr5[index] + j;
                if (i8 <= i10) {
                    jArr4[0] = jArr4[0] + j;
                    iArr3[0] = iArr3[0] + 1;
                } else if (i8 >= i12 - i10) {
                    jArr4[1] = jArr4[1] + j;
                    iArr3[1] = iArr3[1] + 1;
                }
                i8 += samplingOffset;
                jArr = jArr6;
                i3 = i9;
                iArr = iArr4;
                statusBarHeight = i10;
                height = i11;
                width = i12;
            }
            jArr3 = jArr4;
            iArr2 = iArr3;
            jArr2 = jArr5;
            i4 = i7;
            wallpaperRetreiver = wallpaperRetreiver2;
            i5 = i6 + samplingOffset;
            jArr = jArr;
        }
        long[] jArr7 = jArr;
        WallpaperRetreiver wallpaperRetreiver3 = wallpaperRetreiver;
        int[] iArr5 = iArr2;
        long[] jArr8 = jArr3;
        long[] jArr9 = jArr2;
        int i13 = i4;
        int i14 = i3;
        for (int i15 = 0; i15 < i13; i15++) {
            jArr7[i15] = (jArr9[i15] * ((long) this.mWallpaperColorInfo.size)) / ((long) ((i * i2) * 1000));
        }
        int i16 = iArr5[0] * 1000;
        if (i16 == 0) {
            i16 = 1;
        }
        this.mWallpaperColorInfo.brightnessOfstatusBar[0] = (int) (jArr8[0] / ((long) i16));
        int i17 = iArr5[1] * 1000;
        if (i17 == 0) {
            i17 = 1;
        }
        this.mWallpaperColorInfo.brightnessOfstatusBar[1] = (int) (jArr8[1] / ((long) i17));
        for (int i18 = 0; i18 < i14; i18++) {
            this.mWallpaperColorInfo.setBrightness(i18, (int) jArr7[i18]);
        }
        wallpaperRetreiver3.recycleBitmapForSwivel();
        LGLog.d(TAG, "makeInfo : " + this.mWallpaperColorInfo + ", " + Arrays.toString(this.mCount));
    }

    private int getIndex(int x, int y, int witdh, int height, WallpaperColorInfo info) {
        int i = witdh / info.row;
        int i2 = height / info.col;
        if (i == 0) {
            i = 1;
        }
        if (i2 == 0) {
            i2 = 1;
        }
        int i3 = (x / i) + ((y / i2) * info.row);
        return i3 >= info.size ? info.size - 1 : i3;
    }

    class WallpaperColorInfo {
        private int[] brightness;
        private int[] brightnessOfstatusBar = new int[2];
        private boolean calculateBrightOfStatusbar = true;
        private int col;
        private int row;
        private int size;

        public WallpaperColorInfo(int col, int row) {
            this.col = col;
            this.row = row;
            int i = col * row;
            this.size = i;
            this.brightness = new int[i];
            int i2 = 0;
            while (true) {
                int[] iArr = this.brightness;
                if (i2 >= iArr.length) {
                    return;
                }
                iArr[i2] = -1;
                i2++;
            }
        }

        public void setBrightness(int id, int color) {
            int[] iArr = this.brightness;
            if (id < iArr.length) {
                iArr[id] = color;
            }
        }

        public int getBrightnessOfStatusBar(Context context) {
            return this.brightnessOfstatusBar[WindowUtils.getRotation(context, 0) != 2 ? (char) 1 : (char) 0];
        }

        public int getBrightness(int id) {
            int[] iArr = this.brightness;
            int i = id < iArr.length ? iArr[id] : 0;
            LGLog.d(WallpaperColorInfoUtil.TAG, "getBrightness : result = " + i + ", id = " + id + ", " + this);
            return i;
        }

        public String toString() {
            return "statusbar = " + Arrays.toString(this.brightnessOfstatusBar) + ", all = " + Arrays.toString(this.brightness);
        }
    }

    public void addOnChangeListener(OnSwivelWallpaperChangeListener listener) {
        this.mListeners.add(listener);
    }

    public void removeOnChangeListener(OnSwivelWallpaperChangeListener listener) {
        this.mListeners.remove(listener);
    }
}
