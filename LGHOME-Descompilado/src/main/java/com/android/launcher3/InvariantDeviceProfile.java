package com.android.launcher3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.Display;
import android.view.WindowManager;
import com.android.launcher3.config.ProviderConfig;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.PackageManagerHelper;
import com.lge.display.DisplayManagerHelper;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.profile.LGDeviceProfile;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class InvariantDeviceProfile {
    public static final int CHANGE_FLAG_COVER_WINDOW_SIZE_UPDATED = 8;
    public static final int CHANGE_FLAG_GRID = 1;
    public static final int CHANGE_FLAG_ICON_PARAMS = 2;
    public static final int CHANGE_FLAG_WINDOW_SIZE_UPDATED = 4;
    private static boolean DEBUG = false;
    private static final float ICON_SIZE_DEFINED_IN_APP_DP = 48.0f;
    public static final String KEY_ICON_PATH_REF = "pref_icon_shape_path";
    private static String TAG = "InvariantDeviceProfile";
    public Rect availableSizes;
    public Rect availableSizesForMultiDisplay;
    public int defaultLayoutId;
    public Point defaultWallpaperSize;
    public int fillResIconDpi;
    protected int hotseatAllAppsRank;
    protected float hotseatIconSize;
    public float hotseatScale;
    public int iconBitmapSize;
    public float iconSize;
    public float iconTextSize;
    public DeviceProfile landscapeProfile;
    public DeviceProfile landscapeProfileForMultiDisplay;
    private final ArrayList<OnIDPChangeListener> mChangeListeners;
    public int mColorOfLetterBox;
    public float mDefaultTargetYRatio;
    public boolean mEnableCover;
    public Display mMultiDisplay;
    private OverlayMonitor mOverlayMonitor;
    public Point mRealSize;
    protected int minAllAppsPredictionColumns;
    protected float minHeightDps;
    protected float minWidthDps;
    protected String name;
    public int numColumns;
    public int numFolderColumns;
    public int numFolderColumnsForSwivel;
    public int numFolderRows;
    public int numFolderRowsForSwivel;
    public int numHotseatIcons;
    public int numRows;
    public DeviceProfile portraitProfile;
    public DeviceProfile portraitProfileForMultiDisplay;
    public static final MainThreadInitializedObject<InvariantDeviceProfile> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.launcher3.-$$Lambda$7ADryerlLCLIG6jwMKnGExKIIoM
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return new LGInvariantDeviceProfile(context);
        }
    });
    private static float DEFAULT_ICON_SIZE_DP = 60.0f;
    private static float KNEARESTNEIGHBOR = 3.0f;
    private static float WEIGHT_POWER = 5.0f;
    private static float WEIGHT_EFFICIENT = 100000.0f;

    public interface OnIDPChangeListener {
        void onIdpChanged(int changeFlags, InvariantDeviceProfile profile);
    }

    private static float wallpaperTravelToScreenWidthRatio(int width, int height) {
        return ((width / height) * 0.30769226f) + 1.0076923f;
    }

    public InvariantDeviceProfile() {
        this.mChangeListeners = new ArrayList<>();
        this.mRealSize = new Point();
        this.availableSizes = new Rect();
        this.availableSizesForMultiDisplay = new Rect();
        this.mEnableCover = true;
    }

    public InvariantDeviceProfile(InvariantDeviceProfile p) {
        this(p.name, p.minWidthDps, p.minHeightDps, p.numRows, p.numColumns, p.numFolderRows, p.numFolderColumns, p.minAllAppsPredictionColumns, p.iconSize, p.iconTextSize, p.numHotseatIcons, p.hotseatIconSize, p.defaultLayoutId);
        this.mDefaultTargetYRatio = p.mDefaultTargetYRatio;
        this.iconBitmapSize = p.iconBitmapSize;
        this.mOverlayMonitor = p.mOverlayMonitor;
    }

    protected InvariantDeviceProfile(String n, float w, float h, int r, int c, int fr, int fc, int maapc, float is, float its, int hs, float his, int dlId) {
        this.mChangeListeners = new ArrayList<>();
        this.mRealSize = new Point();
        this.availableSizes = new Rect();
        this.availableSizesForMultiDisplay = new Rect();
        this.mEnableCover = true;
        this.name = n;
        this.minWidthDps = w;
        this.minHeightDps = h;
        this.numRows = r;
        this.numColumns = c;
        this.numFolderRows = fr;
        this.numFolderColumns = fc;
        this.minAllAppsPredictionColumns = maapc;
        this.iconSize = is;
        this.iconTextSize = its;
        this.numHotseatIcons = hs;
        this.hotseatIconSize = his;
        this.defaultLayoutId = dlId;
        this.hotseatScale = his / is;
    }

    public InvariantDeviceProfile(final Context context) {
        this.mChangeListeners = new ArrayList<>();
        this.mRealSize = new Point();
        this.availableSizes = new Rect();
        this.availableSizesForMultiDisplay = new Rect();
        this.mEnableCover = true;
        initGrid(context, null);
        DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).addChangeListener(new DisplayController.DisplayInfoChangeListener() { // from class: com.android.launcher3.-$$Lambda$InvariantDeviceProfile$dDdRlEfFpGN8ujgnBYSzGWmXkm8
            @Override // com.android.launcher3.util.DisplayController.DisplayInfoChangeListener
            public final void onDisplayInfoChanged(Context context2, DisplayController.Info info, int i) {
                this.f$0.lambda$new$0$InvariantDeviceProfile(context, context2, info, i);
            }
        });
        this.mOverlayMonitor = new OverlayMonitor(context);
    }

    public /* synthetic */ void lambda$new$0$InvariantDeviceProfile(Context context, Context context2, DisplayController.Info info, int i) {
        if (!WindowUtils.isWideMode(context) && (i & 32) == 0) {
            initGrid(context, null);
        }
        if ((i & 24) != 0) {
            onConfigChanged(context2);
        }
    }

    public String initGrid(Context context, String gridName) {
        LGLog.i(InvariantDeviceProfile.class.getSimpleName(), "initGrid");
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        Point point = new Point();
        Point point2 = new Point();
        defaultDisplay.getCurrentSizeRange(point, point2);
        this.minWidthDps = Utilities.dpiFromPx(Math.min(point.x, point.y), displayMetrics);
        float fDpiFromPx = Utilities.dpiFromPx(Math.min(point2.x, point2.y), displayMetrics);
        this.minHeightDps = fDpiFromPx;
        ArrayList<InvariantDeviceProfile> arrayListFindClosestDeviceProfiles = findClosestDeviceProfiles(this.minWidthDps, fDpiFromPx, getPredefinedDeviceProfiles(context));
        InvariantDeviceProfile invariantDeviceProfileInvDistWeightedInterpolate = invDistWeightedInterpolate(this.minWidthDps, this.minHeightDps, arrayListFindClosestDeviceProfiles);
        InvariantDeviceProfile invariantDeviceProfile = arrayListFindClosestDeviceProfiles.get(0);
        this.numRows = invariantDeviceProfile.numRows;
        this.numColumns = invariantDeviceProfile.numColumns;
        int i = invariantDeviceProfile.numHotseatIcons;
        this.numHotseatIcons = i;
        this.hotseatAllAppsRank = i / 2;
        this.defaultLayoutId = invariantDeviceProfile.defaultLayoutId;
        this.numFolderRows = invariantDeviceProfile.numFolderRows;
        this.numFolderColumns = invariantDeviceProfile.numFolderColumns;
        this.minAllAppsPredictionColumns = invariantDeviceProfile.minAllAppsPredictionColumns;
        float f = invariantDeviceProfileInvDistWeightedInterpolate.iconSize;
        this.iconSize = f;
        int iPxFromDp = Utilities.pxFromDp(f, displayMetrics);
        this.iconBitmapSize = iPxFromDp;
        this.iconTextSize = invariantDeviceProfileInvDistWeightedInterpolate.iconTextSize;
        this.hotseatIconSize = invariantDeviceProfileInvDistWeightedInterpolate.hotseatIconSize;
        this.fillResIconDpi = getLauncherIconDensity(iPxFromDp);
        applyPartnerDeviceProfileOverrides(context, displayMetrics);
        this.hotseatScale = this.hotseatIconSize / this.iconSize;
        Point point3 = new Point();
        defaultDisplay.getRealSize(point3);
        int iMin = Math.min(point3.x, point3.y);
        int iMax = Math.max(point3.x, point3.y);
        this.landscapeProfile = new DeviceProfile(context, this, point, point2, iMax, iMin, true, false);
        this.portraitProfile = new DeviceProfile(context, this, point, point2, iMin, iMax, false, false);
        createMultiDisplayProfile(context);
        if (context.getResources().getConfiguration().smallestScreenWidthDp >= 720) {
            this.defaultWallpaperSize = new Point((int) (iMax * wallpaperTravelToScreenWidthRatio(iMax, iMin)), iMax);
            return null;
        }
        this.defaultWallpaperSize = new Point(Math.max(iMin * 2, iMax), iMax);
        return null;
    }

    public boolean createMultiDisplayProfile(Context context) {
        if (!DisplayManagerHelper.isMultiDisplayDevice()) {
            return false;
        }
        DisplayManager displayManager = (DisplayManager) context.getSystemService(DisplayManager.class);
        int multiDisplayId = new DisplayManagerHelper(context).getMultiDisplayId();
        Display display = displayManager.getDisplay(multiDisplayId);
        if (display != null) {
            display.getMetrics(new DisplayMetrics());
            Point point = new Point();
            Point point2 = new Point();
            display.getCurrentSizeRange(point, point2);
            Point point3 = new Point();
            display.getRealSize(point3);
            int iMin = Math.min(point3.x, point3.y);
            int iMax = Math.max(point3.x, point3.y);
            this.landscapeProfileForMultiDisplay = new LGDeviceProfile(context, this, point, point2, iMax, iMin, true, false, multiDisplayId, true);
            this.portraitProfileForMultiDisplay = new LGDeviceProfile(context, this, point, point2, iMin, iMax, false, false, multiDisplayId, true);
            this.mMultiDisplay = display;
            LGLog.i(TAG, String.format("[DEVICE_PROFILE] createMultiDisplayProfile : success! multiDIsplay = %s, id = %s, S(%s, %s), L(%s, %s)", display, Integer.valueOf(multiDisplayId), Integer.valueOf(point.x), Integer.valueOf(point.y), Integer.valueOf(point2.x), Integer.valueOf(point2.y)));
            return true;
        }
        LGLog.i(TAG, "createMultiDisplayProfile : failed because multiDisplay is null");
        this.mMultiDisplay = null;
        return false;
    }

    public DeviceProfile getDeviceProfile(Context context, int displayId) {
        if (displayId == 0) {
            return getMainDeviceProfile(context);
        }
        return getMultiDisplayProfile(context);
    }

    public DeviceProfile getDeviceProfile(Context context) {
        if (DEBUG) {
            LGLog.d(getClass().getSimpleName(), "getDeviceProfile - heightPixels = " + context.getResources().getDisplayMetrics().heightPixels + ", widthPixels = " + context.getResources().getDisplayMetrics().widthPixels + ", orientation =" + context.getResources().getConfiguration().orientation);
        }
        return getDeviceProfile(context, context.getDisplayId());
    }

    public DeviceProfile getMainDeviceProfile(Context context) {
        return context.getResources().getConfiguration().windowConfiguration.getWindowingMode() != 4 ? context.getResources().getDisplayMetrics().heightPixels < context.getResources().getDisplayMetrics().widthPixels ? this.landscapeProfile : this.portraitProfile : context.getResources().getConfiguration().orientation == 2 ? this.landscapeProfile : this.portraitProfile;
    }

    public DeviceProfile getMultiDisplayProfile(Context context) {
        Display display;
        int rotation;
        boolean z = true;
        if ((this.portraitProfileForMultiDisplay == null || this.landscapeProfileForMultiDisplay == null) ? createMultiDisplayProfile(context) : true) {
            boolean z2 = context.getDisplayId() != 0;
            boolean z3 = this.portraitProfile.widthPx <= this.portraitProfile.heightPx;
            if (!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() || (display = this.mMultiDisplay) == null ? !(!z2 || (!z3 ? context.getResources().getDisplayMetrics().widthPixels >= context.getResources().getDisplayMetrics().heightPixels : context.getResources().getDisplayMetrics().widthPixels <= context.getResources().getDisplayMetrics().heightPixels)) : !((rotation = display.getRotation()) == 0 || rotation == 2)) {
                z = false;
            }
            if (z) {
                return this.portraitProfileForMultiDisplay;
            }
            return this.landscapeProfileForMultiDisplay;
        }
        return getMainDeviceProfile(context);
    }

    /* JADX DEBUG: Finally have unexpected throw blocks count: 3, expect 1 */
    protected ArrayList<InvariantDeviceProfile> getPredefinedDeviceProfiles(Context context) {
        ArrayList<InvariantDeviceProfile> arrayList = new ArrayList<>();
        try {
            XmlResourceParser xml = context.getResources().getXml(R.xml.device_profiles);
            try {
                int depth = xml.getDepth();
                while (true) {
                    int next = xml.next();
                    if ((next == 3 && xml.getDepth() <= depth) || next == 1) {
                        break;
                    }
                    if (next == 2 && ItemInfo.EXTRA_PROFILE.equals(xml.getName())) {
                        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xml), R.styleable.InvariantDeviceProfile);
                        int i = typedArrayObtainStyledAttributes.getInt(12, 0);
                        int i2 = typedArrayObtainStyledAttributes.getInt(8, 0);
                        float f = typedArrayObtainStyledAttributes.getFloat(2, 0.0f);
                        arrayList.add(new InvariantDeviceProfile(typedArrayObtainStyledAttributes.getString(7), typedArrayObtainStyledAttributes.getFloat(6, 0.0f), typedArrayObtainStyledAttributes.getFloat(5, 0.0f), i, i2, typedArrayObtainStyledAttributes.getInt(10, i), typedArrayObtainStyledAttributes.getInt(9, i2), typedArrayObtainStyledAttributes.getInt(4, i2), f, typedArrayObtainStyledAttributes.getFloat(3, 0.0f), typedArrayObtainStyledAttributes.getInt(11, i2), typedArrayObtainStyledAttributes.getFloat(1, f), typedArrayObtainStyledAttributes.getResourceId(0, 0)));
                        typedArrayObtainStyledAttributes.recycle();
                    }
                }
                if (xml != null) {
                    xml.close();
                }
                return arrayList;
            } finally {
            }
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }

    private int getLauncherIconDensity(int requiredSize) {
        int[] iArr = {120, 160, 213, ShortcutInfo.FLAG_RESTORED_APP_TYPE, LauncherAnimUtils.ALL_APPS_TRANSITION_MS, 480, 640};
        int i = 640;
        for (int i2 = 6; i2 >= 0; i2--) {
            if ((iArr[i2] * ICON_SIZE_DEFINED_IN_APP_DP) / 160.0f >= requiredSize) {
                i = iArr[i2];
            }
        }
        return i;
    }

    private void applyPartnerDeviceProfileOverrides(Context context, DisplayMetrics dm) {
        Partner partner = Partner.get(context.getPackageManager());
        if (partner != null) {
            partner.applyInvariantDeviceProfileOverrides(this, dm);
        }
    }

    float dist(float x0, float y0, float x1, float y1) {
        return (float) Math.hypot(x1 - x0, y1 - y0);
    }

    ArrayList<InvariantDeviceProfile> findClosestDeviceProfiles(final float width, final float height, ArrayList<InvariantDeviceProfile> points) {
        Collections.sort(points, new Comparator<InvariantDeviceProfile>() { // from class: com.android.launcher3.InvariantDeviceProfile.1
            /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
            @Override // java.util.Comparator
            public int compare(InvariantDeviceProfile a, InvariantDeviceProfile b) {
                return Float.compare(InvariantDeviceProfile.this.dist(width, height, a.minWidthDps, a.minHeightDps), InvariantDeviceProfile.this.dist(width, height, b.minWidthDps, b.minHeightDps));
            }
        });
        return points;
    }

    InvariantDeviceProfile invDistWeightedInterpolate(float width, float height, ArrayList<InvariantDeviceProfile> points) {
        InvariantDeviceProfile invariantDeviceProfile = points.get(0);
        float f = 0.0f;
        if (dist(width, height, invariantDeviceProfile.minWidthDps, invariantDeviceProfile.minHeightDps) == 0.0f) {
            return invariantDeviceProfile;
        }
        InvariantDeviceProfile invariantDeviceProfile2 = new InvariantDeviceProfile();
        for (int i = 0; i < points.size() && i < KNEARESTNEIGHBOR; i++) {
            InvariantDeviceProfile invariantDeviceProfile3 = new InvariantDeviceProfile(points.get(i));
            float fWeight = weight(width, height, invariantDeviceProfile3.minWidthDps, invariantDeviceProfile3.minHeightDps, WEIGHT_POWER);
            f += fWeight;
            invariantDeviceProfile2.add(invariantDeviceProfile3.multiply(fWeight));
        }
        return invariantDeviceProfile2.multiply(1.0f / f);
    }

    private void add(InvariantDeviceProfile p) {
        this.iconSize += p.iconSize;
        this.iconTextSize += p.iconTextSize;
        this.hotseatIconSize += p.hotseatIconSize;
    }

    private InvariantDeviceProfile multiply(float w) {
        this.iconSize *= w;
        this.iconTextSize *= w;
        this.hotseatIconSize *= w;
        return this;
    }

    public int getAllAppsButtonRank() {
        if (ProviderConfig.IS_DOGFOOD_BUILD) {
            throw new IllegalAccessError("Accessing all apps rank when all-apps is disabled");
        }
        return this.numHotseatIcons / 2;
    }

    public boolean isAllAppsButtonRank(int rank) {
        return rank == getAllAppsButtonRank();
    }

    private float weight(float x0, float y0, float x1, float y1, float pow) {
        float fDist = dist(x0, y0, x1, y1);
        if (Float.compare(fDist, 0.0f) == 0) {
            return Float.POSITIVE_INFINITY;
        }
        return (float) (((double) WEIGHT_EFFICIENT) / Math.pow(fDist, pow));
    }

    public void addOnChangeListener(OnIDPChangeListener listener) {
        this.mChangeListeners.add(listener);
    }

    public void removeOnChangeListener(OnIDPChangeListener listener) {
        this.mChangeListeners.remove(listener);
    }

    private void apply(Context context, int changeFlags) {
        Iterator<OnIDPChangeListener> it = this.mChangeListeners.iterator();
        while (it.hasNext()) {
            it.next().onIdpChanged(changeFlags, this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConfigChanged(Context context) {
        DeviceProfile deviceProfile;
        if (WindowUtils.isWideMode(context)) {
            LGLog.d(InvariantDeviceProfile.class.getSimpleName(), "skip onConfigChanged because wide mode is on");
            return;
        }
        DeviceProfile deviceProfile2 = this.portraitProfile;
        int i = deviceProfile2 != null ? deviceProfile2.iconSizePx : 0;
        LGInvariantDeviceProfile lGInvariantDeviceProfile = new LGInvariantDeviceProfile(this);
        initGrid(context, null);
        int i2 = (this.numRows == lGInvariantDeviceProfile.numRows && this.numColumns == lGInvariantDeviceProfile.numColumns && this.numFolderColumns == lGInvariantDeviceProfile.numFolderColumns && this.numFolderRows == lGInvariantDeviceProfile.numFolderRows && this.numHotseatIcons == lGInvariantDeviceProfile.numHotseatIcons) ? 0 : 1;
        if (!this.availableSizes.equals(lGInvariantDeviceProfile.availableSizes) || !this.mRealSize.equals(lGInvariantDeviceProfile.mRealSize)) {
            LGLog.d(InvariantDeviceProfile.class.getSimpleName(), String.format("[DEVICE_PROFILE] onConfigChanged : Main(%s <- %s)", this.availableSizes, lGInvariantDeviceProfile.availableSizes));
            i2 |= 4;
        }
        if (!this.availableSizesForMultiDisplay.equals(lGInvariantDeviceProfile.availableSizesForMultiDisplay)) {
            LGLog.d(InvariantDeviceProfile.class.getSimpleName(), String.format("[DEVICE_PROFILE] onConfigChanged : MultiDisplay(%s <- %s)", this.availableSizesForMultiDisplay, lGInvariantDeviceProfile.availableSizesForMultiDisplay));
            i2 |= 8;
        }
        if (this.iconSize != lGInvariantDeviceProfile.iconSize || this.iconBitmapSize != lGInvariantDeviceProfile.iconBitmapSize || ((deviceProfile = this.portraitProfile) != null && i != deviceProfile.iconSizePx)) {
            i2 |= 2;
        }
        WindowUtils.resetNavigationBarHeight();
        LGLog.i(InvariantDeviceProfile.class.getSimpleName(), "[DEVICE_PROFILE] onConfigChanged : " + i2);
        apply(context, i2);
    }

    private class OverlayMonitor extends BroadcastReceiver {
        private final String ACTION_OVERLAY_CHANGED = "android.intent.action.OVERLAY_CHANGED";

        OverlayMonitor(Context context) {
            context.registerReceiver(this, PackageManagerHelper.getPackageFilter(LauncherConst.PACKAGE_NAME_NATIVE, "android.intent.action.OVERLAY_CHANGED"));
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            InvariantDeviceProfile.this.onConfigChanged(context);
        }
    }
}
