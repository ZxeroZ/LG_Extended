package com.lge.launcher3.wallpaperpicker.utils;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.widget.Toast;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGOptimusThemeResources;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class LGWallpaperManagerUtil {
    private static final int CARRIER_WALLPAPERS_XML = 2132017161;
    private static final int CHAR_TYPE_ENGLISH = 5;
    private static final int CHAR_TYPE_JAPANESE = 4;
    private static final int CHAR_TYPE_KOREAN = 3;
    private static final int CHAR_TYPE_NUMBER = 2;
    private static final int CHAR_TYPE_OTHER = 6;
    private static final int CHAR_TYPE_SYMBOL = 1;
    private static final Collator COLLATOR;
    private static final String ITEM_TAG = "item";
    private static final String NETWORK_CODE_TAG = "networkcode";
    public static final String PROPERTY_RO_LGE_DEVICE_COLOR;
    public static final String PROPERTY_RO_SPRINT_HFA_FLAG;
    private static final String TAG = "LGWallpaperManagerUtil";
    private static final String WALLPAPER_NAME_TAG = "wallpapername";
    private static int sCalColorForPreviousModel;
    private static boolean sIsJapanese;
    private static boolean sIsKorean;
    protected Context mContext;
    protected PackageManager mPm;
    protected WallpaperManager mWallpaperManager;
    final String carrierWallpaper_datapath = Environment.getDataDirectory() + "/data/com.lge.launcher3/files/carrier_default_wallpaper.jpg";
    final String carrierWallpaper_ThumbnailPath = Environment.getDataDirectory() + "/data/com.lge.launcher3/files/carrier_default_wallpaper_small.jpg";
    final String carrierPatitionWallpaperPath = "/carrier/media/default_wallpaper.jpg";
    final String deviceColorWallpaperNamePrefix = "device_color_wallpaper_";
    public boolean mUseCarrierWallpaper = false;
    private LinkedHashMap<String, String> mOperatorWallpaperTable = null;
    protected List<LGWallpaperItem> mList = new ArrayList();

    static {
        PROPERTY_RO_LGE_DEVICE_COLOR = Build.VERSION.SDK_INT < 28 ? "ro.lge.device_color" : "ro.boot.product.lge.device_color";
        PROPERTY_RO_SPRINT_HFA_FLAG = Build.VERSION.SDK_INT < 28 ? "ro.sprint.hfa.flag" : "ro.product.lge.sprint.hfa.flag";
        sIsKorean = Locale.KOREA.equals(Locale.getDefault());
        sIsJapanese = Locale.JAPAN.equals(Locale.getDefault());
        COLLATOR = Collator.getInstance();
        sCalColorForPreviousModel = -1;
    }

    static class WallpaperComparator implements Comparator<LGWallpaperItem> {
        WallpaperComparator() {
        }

        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public final int compare(LGWallpaperItem a, LGWallpaperItem b) {
            return koreanCompapre(a.displayLabel.toString(), b.displayLabel.toString());
        }

        public int koreanCompapre(String a, String b) {
            if (LGWallpaperManagerUtil.sIsKorean) {
                int charType = getCharType(a);
                int charType2 = getCharType(b);
                if (charType < charType2) {
                    return -1;
                }
                if (charType > charType2) {
                    return 1;
                }
            } else if (LGWallpaperManagerUtil.sIsJapanese) {
                int charType3 = getCharType(a);
                int charType4 = getCharType(b);
                if (charType3 < charType4) {
                    return -1;
                }
                if (charType3 > charType4) {
                    return 1;
                }
            }
            return LGWallpaperManagerUtil.COLLATOR.compare(a, b);
        }

        public final int getCharType(String str) {
            if (str == null || str.length() <= 0) {
                return 6;
            }
            int iCodePointAt = str.codePointAt(0);
            if (iCodePointAt >= 48 && iCodePointAt <= 57) {
                return 2;
            }
            if (iCodePointAt >= 65 && iCodePointAt <= 90) {
                return 5;
            }
            if (iCodePointAt >= 97 && iCodePointAt <= 122) {
                return 5;
            }
            if (iCodePointAt < 127) {
                return 1;
            }
            if (LGWallpaperManagerUtil.sIsKorean) {
                if (iCodePointAt >= 44032 && iCodePointAt <= 55203) {
                    return 3;
                }
                if (iCodePointAt >= 12593 && iCodePointAt <= 12686) {
                    return 3;
                }
            }
            if (!LGWallpaperManagerUtil.sIsJapanese) {
                return 6;
            }
            if ((iCodePointAt >= 12353 && iCodePointAt < 12363) || iCodePointAt == 3094) {
                return 4;
            }
            if (iCodePointAt >= 12449 && iCodePointAt < 12459) {
                return 4;
            }
            if (iCodePointAt >= 65393 && iCodePointAt < 65398) {
                return 4;
            }
            if (iCodePointAt >= 65383 && iCodePointAt < 65388) {
                return 4;
            }
            if ((iCodePointAt >= 12363 && iCodePointAt < 12373) || iCodePointAt == 12437 || iCodePointAt == 12438) {
                return 4;
            }
            if ((iCodePointAt >= 12459 && iCodePointAt < 12469) || iCodePointAt == 12533 || iCodePointAt == 12534) {
                return 4;
            }
            if (iCodePointAt >= 65398 && iCodePointAt < 65403) {
                return 4;
            }
            if (iCodePointAt >= 12373 && iCodePointAt < 12383) {
                return 4;
            }
            if (iCodePointAt >= 12469 && iCodePointAt < 12479) {
                return 4;
            }
            if (iCodePointAt >= 65403 && iCodePointAt < 65408) {
                return 4;
            }
            if (iCodePointAt >= 12383 && iCodePointAt < 12394) {
                return 4;
            }
            if (iCodePointAt >= 12479 && iCodePointAt < 12490) {
                return 4;
            }
            if ((iCodePointAt >= 65408 && iCodePointAt < 65413) || iCodePointAt == 65391) {
                return 4;
            }
            if (iCodePointAt >= 12394 && iCodePointAt < 12399) {
                return 4;
            }
            if (iCodePointAt >= 12490 && iCodePointAt < 12495) {
                return 4;
            }
            if (iCodePointAt >= 65413 && iCodePointAt < 65418) {
                return 4;
            }
            if (iCodePointAt >= 12399 && iCodePointAt < 12414) {
                return 4;
            }
            if (iCodePointAt >= 12495 && iCodePointAt < 12510) {
                return 4;
            }
            if (iCodePointAt >= 65418 && iCodePointAt < 65423) {
                return 4;
            }
            if (iCodePointAt >= 12414 && iCodePointAt < 12419) {
                return 4;
            }
            if (iCodePointAt >= 12510 && iCodePointAt < 12515) {
                return 4;
            }
            if (iCodePointAt >= 65423 && iCodePointAt < 65428) {
                return 4;
            }
            if (iCodePointAt >= 12419 && iCodePointAt < 12425) {
                return 4;
            }
            if (iCodePointAt >= 12515 && iCodePointAt < 12521) {
                return 4;
            }
            if (iCodePointAt >= 65428 && iCodePointAt < 65431) {
                return 4;
            }
            if (iCodePointAt >= 12425 && iCodePointAt < 12430) {
                return 4;
            }
            if (iCodePointAt >= 12521 && iCodePointAt < 12526) {
                return 4;
            }
            if (iCodePointAt >= 65431 && iCodePointAt < 65436) {
                return 4;
            }
            if (iCodePointAt < 12430 || iCodePointAt >= 12435) {
                return ((iCodePointAt >= 12526 && iCodePointAt < 12532) || iCodePointAt == 65382 || iCodePointAt == 65436 || iCodePointAt == 65437) ? 4 : 6;
            }
            return 4;
        }
    }

    public final class LGWallpaperItem {
        public Bitmap bitmapDrawableRes;
        public Bitmap bitmapDrawableThumbRes;
        CharSequence displayLabel;
        public boolean isExternalItem;
        public Drawable mDrawableImage;
        public String mPackageName;
        public Drawable mThumbDrawableImage;
        public int resImage;
        public int resThumb;

        public LGWallpaperItem(CharSequence roLabel, Bitmap bitmapThumbRes, Bitmap bitmapRes) {
            this.isExternalItem = false;
            this.bitmapDrawableRes = null;
            this.bitmapDrawableThumbRes = null;
            this.displayLabel = roLabel;
            this.resThumb = 0;
            this.resImage = 0;
            this.isExternalItem = true;
            this.bitmapDrawableRes = bitmapRes;
            this.bitmapDrawableThumbRes = bitmapThumbRes;
            this.mThumbDrawableImage = null;
            this.mDrawableImage = null;
        }

        public LGWallpaperItem(CharSequence roLabel, int resThumb, int resImage, String packageName) {
            this.isExternalItem = false;
            this.bitmapDrawableRes = null;
            this.bitmapDrawableThumbRes = null;
            this.displayLabel = roLabel;
            this.resThumb = resThumb;
            this.resImage = resImage;
            this.isExternalItem = false;
            this.bitmapDrawableRes = null;
            this.bitmapDrawableThumbRes = null;
            this.mPackageName = packageName;
            this.mThumbDrawableImage = null;
            this.mDrawableImage = null;
        }

        public void destoryItem() {
            Bitmap bitmap = this.bitmapDrawableRes;
            if (bitmap != null) {
                bitmap.recycle();
                this.bitmapDrawableRes = null;
            }
            Bitmap bitmap2 = this.bitmapDrawableThumbRes;
            if (bitmap2 != null) {
                bitmap2.recycle();
                this.bitmapDrawableThumbRes = null;
            }
        }
    }

    public LGWallpaperManagerUtil(Context context) throws Throwable {
        this.mContext = null;
        this.mWallpaperManager = null;
        this.mContext = context;
        this.mWallpaperManager = WallpaperManager.getInstance(context);
        this.mPm = this.mContext.getPackageManager();
        findWallpapers();
    }

    public List<LGWallpaperItem> getList() {
        return this.mList;
    }

    public Drawable getCurrentWallpaper() {
        WallpaperManager wallpaperManager = this.mWallpaperManager;
        if (wallpaperManager != null) {
            return wallpaperManager.getDrawable();
        }
        return null;
    }

    private boolean isSystemWallpaperExist() {
        File file = new File(Environment.getDataDirectory() + "/data/com.android.settings/files/wallpaper");
        return file.exists() && file.isFile();
    }

    private void showToastSetWallpaper() {
        Context context = this.mContext;
        if (context == null) {
            return;
        }
        Toast toastMakeText = Toast.makeText(context, "null", 0);
        toastMakeText.setText(this.mContext.getString(R.string.sp_wallpaper_changed_NORMAL));
        toastMakeText.show();
    }

    public void setWallpaper(int position) {
        Context context = this.mContext;
        if (context == null) {
            LGLog.e(TAG, "setWallpaper : mContext is null");
            return;
        }
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        LGWallpaperItem lGWallpaperItem = this.mList.get(position);
        if (lGWallpaperItem == null) {
            return;
        }
        Context contextCreatePackageContext = null;
        if (lGWallpaperItem.isExternalItem) {
            Context context2 = this.mContext;
            if (context2 instanceof Activity) {
                LGWallpaperSwitchUtil.changeWallpaper(context2, lGWallpaperItem.bitmapDrawableRes);
                return;
            }
            try {
                wallpaperManager.setBitmap(this.mList.get(position).bitmapDrawableRes, null, true, 1);
                showToastSetWallpaper();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        if (lGWallpaperItem.resImage != 0) {
            try {
                contextCreatePackageContext = this.mContext.createPackageContext(lGWallpaperItem.mPackageName, 2);
            } catch (PackageManager.NameNotFoundException unused) {
                LGLog.d(TAG, "packageName : " + lGWallpaperItem.mPackageName);
            }
            LGWallpaperSwitchUtil.changeWallpaper(contextCreatePackageContext, lGWallpaperItem.resImage);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x008a  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00a8  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00ae  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00b2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void findWallpapers() throws java.lang.Throwable {
        /*
            r11 = this;
            android.content.Context r0 = r11.mContext
            android.content.res.Resources r0 = r0.getResources()
            android.content.Context r1 = r11.mContext
            java.lang.String r1 = r1.getPackageName()
            com.lge.launcher3.util.LGHomeFeature$Config r2 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_USE_CARRIER_WALLPAPER_ITEM
            boolean r2 = r2.getValue()
            r3 = 0
            r4 = 1
            if (r2 != r4) goto Ldf
            java.lang.String r2 = com.lge.launcher3.config.LauncherConst.PROPERTY_RO_BUILD_TARGET_OPERATOR
            java.lang.String r5 = "OPEN"
            java.lang.String r2 = android.os.SystemProperties.get(r2, r5)
            java.lang.String r5 = "SPR"
            boolean r5 = r2.equals(r5)
            if (r5 != 0) goto L31
            java.lang.String r5 = "BM"
            boolean r5 = r2.equals(r5)
            if (r5 == 0) goto L2f
            goto L31
        L2f:
            r5 = r3
            goto L32
        L31:
            r5 = r4
        L32:
            java.lang.String r6 = "ACG"
            boolean r7 = r2.equals(r6)
            java.lang.String r8 = "LRA"
            java.lang.String r9 = "VDF"
            if (r7 != 0) goto L4d
            boolean r7 = r2.equals(r9)
            if (r7 != 0) goto L4d
            boolean r7 = r2.equals(r8)
            if (r7 == 0) goto L4b
            goto L4d
        L4b:
            r7 = r3
            goto L4e
        L4d:
            r7 = r4
        L4e:
            if (r7 == 0) goto L53
            r11.LGWallpaperCarrierCodeResolver()
        L53:
            if (r5 != 0) goto L57
            if (r7 == 0) goto Ldf
        L57:
            java.lang.String r5 = com.lge.launcher3.wallpaperpicker.utils.LGWallpaperManagerUtil.PROPERTY_RO_SPRINT_HFA_FLAG
            java.lang.String r7 = "none"
            java.lang.String r5 = android.os.SystemProperties.get(r5, r7)
            java.lang.String r7 = "activationOK"
            boolean r5 = r5.equals(r7)
            boolean r6 = r2.equals(r6)
            java.lang.String r7 = "nCarrierCode value is not integer"
            java.lang.String r10 = "LGWallpaperManagerUtil"
            if (r6 != 0) goto L8c
            boolean r6 = r2.equals(r8)
            if (r6 == 0) goto L76
            goto L8c
        L76:
            boolean r2 = r2.equals(r9)
            if (r2 == 0) goto L8a
            r2 = 0
            int r2 = java.lang.Integer.parseInt(r2)     // Catch: java.lang.NumberFormatException -> L86
            r6 = 655(0x28f, float:9.18E-43)
            if (r2 != r6) goto L8a
            goto La4
        L86:
            r2 = move-exception
            com.lge.launcher3.util.LGLog.e(r10, r7, r2)
        L8a:
            r4 = r3
            goto La4
        L8c:
            java.lang.String r2 = com.lge.launcher3.config.LauncherConst.PROPERTY_RO_CARRIER_CODE
            java.lang.String r6 = "0"
            java.lang.String r2 = android.os.SystemProperties.get(r2, r6)
            int r2 = java.lang.Integer.parseInt(r2)     // Catch: java.lang.NumberFormatException -> L9f
            if (r2 <= 0) goto L8a
            r6 = 41
            if (r2 >= r6) goto L8a
            goto La4
        L9f:
            r2 = move-exception
            com.lge.launcher3.util.LGLog.e(r10, r7, r2)
            goto L8a
        La4:
            if (r5 != 0) goto La8
            if (r4 == 0) goto Ldf
        La8:
            boolean r2 = r11.isCarrierWallpaperExist()
            if (r2 == 0) goto Lb2
            r11.addCarrierPartitionWallpaper()
            goto Ldf
        Lb2:
            boolean r2 = r11.isCarrierCodeWallpaperExist()
            if (r2 == 0) goto Lbc
            r11.addCarrierWallpapers()
            goto Ldf
        Lbc:
            java.lang.String r2 = "ro.cdma.home.operator.numeric"
            java.lang.String r4 = "111111"
            java.lang.String r2 = android.os.SystemProperties.get(r2, r4)
            java.lang.String r4 = "311870"
            boolean r4 = r2.equals(r4)
            if (r4 != 0) goto Ldc
            java.lang.String r4 = "31012"
            boolean r4 = r2.equals(r4)
            if (r4 != 0) goto Ldc
            java.lang.String r4 = "311490"
            boolean r2 = r2.equals(r4)
            if (r2 == 0) goto Ldf
        Ldc:
            r11.addCarrierWallpapers()
        Ldf:
            java.lang.String r2 = "ro.lge.use_opr_wallpaper"
            boolean r2 = android.os.SystemProperties.getBoolean(r2, r3)
            r3 = 2130903066(0x7f03001a, float:1.741294E38)
            if (r2 == 0) goto Lf1
            r11.addWallpapers(r0, r1, r3)
            r11.addDeviceColorWallpapers()
            goto Lf7
        Lf1:
            r11.addDeviceColorWallpapers()
            r11.addWallpapers(r0, r1, r3)
        Lf7:
            r11.addLGHomeWallpapers()
            r2 = 2130903053(0x7f03000d, float:1.7412913E38)
            r11.addWallpapers(r0, r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.wallpaperpicker.utils.LGWallpaperManagerUtil.findWallpapers():void");
    }

    private boolean isValidCarrierCode(String estimatedCode) {
        LinkedHashMap<String, String> linkedHashMap = this.mOperatorWallpaperTable;
        if (linkedHashMap == null) {
            return false;
        }
        if (linkedHashMap.containsKey(estimatedCode) || this.mOperatorWallpaperTable.containsValue(estimatedCode)) {
            LGLog.i(TAG, "LinkedMap has same key");
            return true;
        }
        LGLog.i(TAG, "LinkedMap has not the key");
        return false;
    }

    private boolean isCarrierCodeWallpaperExist() {
        boolean zIsValidCarrierCode;
        String str = SystemProperties.get(LauncherConst.PROPERTY_RO_BUILD_TARGET_OPERATOR, "OPEN");
        boolean z = str.equals("ACG") || str.equals("VDF") || str.equals("LRA");
        if (str.equals("ACG") || str.equals("LRA")) {
            try {
                zIsValidCarrierCode = isValidCarrierCode(SystemProperties.get(LauncherConst.PROPERTY_RO_CARRIER_CODE, "0"));
            } catch (NumberFormatException e) {
                LGLog.e(TAG, "carrierCode value is not integer", e);
                zIsValidCarrierCode = false;
            }
        } else if (str.equals("VDF")) {
            try {
                zIsValidCarrierCode = isValidCarrierCode(null);
            } catch (NumberFormatException e2) {
                LGLog.e(TAG, "operatorCode value is not integer", e2);
                zIsValidCarrierCode = false;
            }
        } else {
            zIsValidCarrierCode = false;
        }
        return zIsValidCarrierCode && z;
    }

    private boolean isCarrierWallpaperExist() {
        File file = new File("/carrier/media/default_wallpaper.jpg");
        return file.exists() && file.isFile() && file.length() >= 40;
    }

    private void addCarrierWallpapers() {
        Bitmap bitmapSaveBackgroundBitmap = saveBackgroundBitmap(this.carrierWallpaper_datapath, 2392);
        Bitmap bitmapSaveBackgroundBitmap2ThumbnailFile = SaveBackgroundBitmap2ThumbnailFile(this.carrierWallpaper_datapath, this.carrierWallpaper_ThumbnailPath, 210, true);
        if (bitmapSaveBackgroundBitmap == null || bitmapSaveBackgroundBitmap2ThumbnailFile == null) {
            return;
        }
        this.mList.add(new LGWallpaperItem("carrier_wallpaper", bitmapSaveBackgroundBitmap2ThumbnailFile, bitmapSaveBackgroundBitmap));
    }

    private Bitmap saveBackgroundBitmap(String strFilePath, int MaxImageSize) {
        Bitmap bitmap;
        FileOutputStream fileOutputStream;
        NullPointerException e;
        IOException e2;
        FileOutputStream fileOutputStream2 = null;
        if (strFilePath == null) {
            return null;
        }
        File file = new File(strFilePath);
        boolean z = !isSystemWallpaperExist();
        if ((!file.exists() || !file.isFile()) && z) {
            bitmap = ((BitmapDrawable) WallpaperManager.getInstance(this.mContext).getBuiltInDrawable()).getBitmap();
            if (file.getParentFile() == null) {
                return null;
            }
            try {
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);
            } catch (IOException e3) {
                fileOutputStream = null;
                e2 = e3;
            } catch (NullPointerException e4) {
                fileOutputStream = null;
                e = e4;
            }
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            } catch (IOException e5) {
                e2 = e5;
                LGLog.i(TAG, e2.getMessage());
            } catch (NullPointerException e6) {
                e = e6;
                LGLog.i(TAG, e.getMessage());
            }
            fileOutputStream2 = fileOutputStream;
        } else {
            bitmap = SafeDecodeBitmapFileSampleSize(strFilePath, 1);
        }
        if (fileOutputStream2 != null) {
            try {
                fileOutputStream2.close();
            } catch (IOException e7) {
                LGLog.i(TAG, "wallpaperFile close:" + e7.getMessage());
                e7.printStackTrace();
            }
        }
        return bitmap;
    }

    private Bitmap SaveBackgroundBitmap2ThumbnailFile(String strFilePath, String thumbFilePath, int MaxImageSize, boolean checkExistSystemWallpaper) {
        Bitmap bitmapDecodeFile;
        FileOutputStream fileOutputStream = null;
        if (!new File(strFilePath).exists()) {
            return null;
        }
        File file = new File(thumbFilePath);
        if (!file.exists() || !file.isFile()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            BitmapFactory.decodeFile(strFilePath, options);
            options.inSampleSize = 2;
            options.outWidth = MaxImageSize;
            options.outHeight = MaxImageSize;
            options.inPurgeable = true;
            options.inDither = false;
            options.inScaled = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmapDecodeFile = BitmapFactory.decodeFile(strFilePath, options);
            if (bitmapDecodeFile != null) {
                if ((checkExistSystemWallpaper && isSystemWallpaperExist()) || file.getParentFile() == null) {
                    return null;
                }
                try {
                    file.createNewFile();
                    FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                    try {
                        bitmapDecodeFile.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream2);
                        LGLog.i(TAG, "make a carrier wallpaper thumbnail success");
                        fileOutputStream = fileOutputStream2;
                    } catch (IOException e) {
                        e = e;
                        fileOutputStream = fileOutputStream2;
                        LGLog.i(TAG, e.getMessage());
                    }
                } catch (IOException e2) {
                    e = e2;
                }
            }
        } else {
            bitmapDecodeFile = SafeDecodeBitmapFileSampleSize(thumbFilePath, 2);
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e3) {
                LGLog.i(TAG, "thumbnailFile close:" + e3.getMessage());
            }
        }
        return bitmapDecodeFile;
    }

    private Bitmap SafeDecodeBitmapFile(String strFilePath, int maxImageSize) {
        if (!new File(strFilePath).exists()) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strFilePath, options);
        if (options.outHeight * options.outWidth >= maxImageSize * maxImageSize) {
            options.inSampleSize = (int) Math.pow(2.0d, (int) Math.round(Math.log(((double) maxImageSize) / ((double) Math.max(options.outHeight, options.outWidth))) / Math.log(0.5d)));
        }
        options.inJustDecodeBounds = false;
        options.inPurgeable = false;
        options.inDither = false;
        options.inScaled = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(strFilePath, options);
    }

    private Bitmap SafeDecodeBitmapFileSampleSize(String strFilePath, int sampleSize) {
        if (!new File(strFilePath).exists()) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        options.inPurgeable = false;
        options.inDither = false;
        options.inScaled = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(strFilePath, options);
    }

    private void addCarrierPartitionWallpaper() throws Throwable {
        Bitmap bitmapWallpaperFromCarrierPartition = getBitmapWallpaperFromCarrierPartition("/carrier/media/default_wallpaper.jpg");
        Bitmap bitmapSaveBackgroundBitmap2ThumbnailFile = SaveBackgroundBitmap2ThumbnailFile("/carrier/media/default_wallpaper.jpg", this.carrierWallpaper_ThumbnailPath, 210, false);
        if (bitmapWallpaperFromCarrierPartition == null || bitmapSaveBackgroundBitmap2ThumbnailFile == null) {
            return;
        }
        this.mList.add(new LGWallpaperItem("carrier_wallpaper", bitmapSaveBackgroundBitmap2ThumbnailFile, bitmapWallpaperFromCarrierPartition));
        this.mUseCarrierWallpaper = true;
    }

    /* JADX DEBUG: Don't trust debug lines info. Repeating lines: [742=4, 745=4] */
    private Bitmap getBitmapWallpaperFromCarrierPartition(String strFilePath) throws Throwable {
        InputStream inputStream;
        FileInputStream fileInputStream;
        BufferedInputStream bufferedInputStream;
        FileInputStream fileInputStream2 = null;
        try {
            try {
                fileInputStream = new FileInputStream(strFilePath);
            } catch (FileNotFoundException e) {
                e = e;
                fileInputStream = null;
                bufferedInputStream = null;
            } catch (NullPointerException e2) {
                e = e2;
                fileInputStream = null;
                bufferedInputStream = null;
            } catch (SecurityException e3) {
                e = e3;
                fileInputStream = null;
                bufferedInputStream = null;
            } catch (Throwable th) {
                th = th;
                inputStream = null;
                quiteinputStream(fileInputStream2);
                quiteinputStream(inputStream);
                throw th;
            }
            try {
                try {
                    bufferedInputStream = new BufferedInputStream(fileInputStream);
                    try {
                        try {
                            try {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.outWidth = 1440;
                                options.outHeight = 1440;
                                options.inJustDecodeBounds = false;
                                options.inPurgeable = true;
                                options.inDither = false;
                                options.inScaled = true;
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(bufferedInputStream, null, options);
                                if (bitmapDecodeStream != null) {
                                    bitmapDecodeStream.recycle();
                                }
                                Bitmap bitmapSafeDecodeBitmapFile = SafeDecodeBitmapFile(strFilePath, 1440);
                                try {
                                    fileInputStream.close();
                                } catch (IOException e4) {
                                    LGLog.w(TAG, "IOException:" + e4, new int[0]);
                                }
                                quiteinputStream(fileInputStream);
                                quiteinputStream(bufferedInputStream);
                                return bitmapSafeDecodeBitmapFile;
                            } catch (OutOfMemoryError e5) {
                                LGLog.i(TAG, "Can't decode stream", e5);
                                try {
                                    fileInputStream.close();
                                } catch (IOException e6) {
                                    LGLog.w(TAG, "IOException:" + e6, new int[0]);
                                }
                                quiteinputStream(fileInputStream);
                                quiteinputStream(bufferedInputStream);
                                return null;
                            }
                        } catch (NullPointerException e7) {
                            LGLog.w(TAG, "Image Decoding Error", e7, new int[0]);
                            try {
                                fileInputStream.close();
                            } catch (IOException e8) {
                                LGLog.w(TAG, "IOException:" + e8, new int[0]);
                            }
                            quiteinputStream(fileInputStream);
                            quiteinputStream(bufferedInputStream);
                            return null;
                        }
                    } catch (Throwable th2) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e9) {
                            LGLog.w(TAG, "IOException:" + e9, new int[0]);
                        }
                        throw th2;
                    }
                } catch (FileNotFoundException e10) {
                    e = e10;
                    bufferedInputStream = null;
                    LGLog.w(TAG, "FileNotFoundException", e, new int[0]);
                    quiteinputStream(fileInputStream);
                    quiteinputStream(bufferedInputStream);
                    return null;
                } catch (NullPointerException e11) {
                    e = e11;
                    bufferedInputStream = null;
                    LGLog.w(TAG, "Image Decoding Error", e, new int[0]);
                    quiteinputStream(fileInputStream);
                    quiteinputStream(bufferedInputStream);
                    return null;
                } catch (SecurityException e12) {
                    e = e12;
                    bufferedInputStream = null;
                    LGLog.w(TAG, "SecurityException", e, new int[0]);
                    quiteinputStream(fileInputStream);
                    quiteinputStream(bufferedInputStream);
                    return null;
                } catch (Throwable th3) {
                    th = th3;
                    inputStream = null;
                    fileInputStream2 = fileInputStream;
                    quiteinputStream(fileInputStream2);
                    quiteinputStream(inputStream);
                    throw th;
                }
            } catch (FileNotFoundException e13) {
                e = e13;
                LGLog.w(TAG, "FileNotFoundException", e, new int[0]);
                quiteinputStream(fileInputStream);
                quiteinputStream(bufferedInputStream);
                return null;
            } catch (NullPointerException e14) {
                e = e14;
                LGLog.w(TAG, "Image Decoding Error", e, new int[0]);
                quiteinputStream(fileInputStream);
                quiteinputStream(bufferedInputStream);
                return null;
            } catch (SecurityException e15) {
                e = e15;
                LGLog.w(TAG, "SecurityException", e, new int[0]);
                quiteinputStream(fileInputStream);
                quiteinputStream(bufferedInputStream);
                return null;
            }
        } catch (Throwable th4) {
            th = th4;
        }
    }

    private static void quiteinputStream(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                LGLog.w(TAG, "quiteinputStream IOException", e, new int[0]);
            }
        }
    }

    private void addWallpapers(Resources resources, String packageName, int list) {
        String[] stringArray = resources.getStringArray(list);
        if (stringArray == null) {
            return;
        }
        for (String str : stringArray) {
            int identifier = resources.getIdentifier(str, LauncherConst.RESOURCE_IMAGE_TYPE, packageName);
            if (identifier != 0) {
                int identifier2 = resources.getIdentifier(str + "_small", LauncherConst.RESOURCE_IMAGE_TYPE, packageName);
                if (identifier2 != 0) {
                    this.mList.add(new LGWallpaperItem(null, identifier2, identifier, packageName));
                }
            }
        }
    }

    private void addDeviceColorWallpapers() {
        String[] stringArray = LGOptimusThemeResources.getInstance(this.mContext).getStringArray("lg_device_color_list");
        String str = SystemProperties.get(PROPERTY_RO_LGE_DEVICE_COLOR);
        String str2 = SystemProperties.get(LauncherConst.PROPERTY_RO_LGE_HYDRA);
        LGLog.d(TAG, "addDeviceColorWallpapers : " + str);
        if (Build.DEVICE.equals("joan") && str2 != null && str2.equals("Signature")) {
            return;
        }
        if (TextUtils.isEmpty(str) || stringArray == null) {
            LGLog.d(TAG, "addDeviceColorWallpapers : deviceColor is null");
            return;
        }
        int i = 0;
        while (true) {
            if (i >= stringArray.length) {
                break;
            }
            if (stringArray[i] != null && stringArray[i].equals(str)) {
                int identifier = LGOptimusThemeResources.getInstance(this.mContext).getIdentifier("device_color_wallpaper_" + str.toLowerCase() + "_small", LauncherConst.RESOURCE_IMAGE_TYPE);
                int identifier2 = LGOptimusThemeResources.getInstance(this.mContext).getIdentifier("device_color_wallpaper_" + str.toLowerCase(), LauncherConst.RESOURCE_IMAGE_TYPE);
                if (identifier != 0 && identifier2 != 0) {
                    this.mList.add(new LGWallpaperItem(null, identifier, identifier2, "com.lge.launcher2.theme.optimus"));
                    break;
                }
            }
            i++;
        }
        for (int i2 = 0; i2 < stringArray.length; i2++) {
            if (stringArray[i2] != null && !stringArray[i2].equals(str)) {
                int identifier3 = LGOptimusThemeResources.getInstance(this.mContext).getIdentifier("device_color_wallpaper_" + stringArray[i2].toLowerCase() + "_small", LauncherConst.RESOURCE_IMAGE_TYPE);
                int identifier4 = LGOptimusThemeResources.getInstance(this.mContext).getIdentifier("device_color_wallpaper_" + stringArray[i2].toLowerCase(), LauncherConst.RESOURCE_IMAGE_TYPE);
                if (identifier3 != 0 && identifier4 != 0) {
                    this.mList.add(new LGWallpaperItem(null, identifier3, identifier4, "com.lge.launcher2.theme.optimus"));
                }
            }
        }
    }

    private void addLGHomeWallpapers() {
        String[] stringArray;
        LGOptimusThemeResources lGOptimusThemeResources = LGOptimusThemeResources.getInstance(this.mContext);
        String str = SystemProperties.get(LauncherConst.PROPERTY_RO_LGE_HYDRA);
        LGLog.i(TAG, "addLGHomeWallpapers deviceType : " + str);
        if (!Build.DEVICE.equals("joan") || str == null || !str.equals("Signature") || (stringArray = lGOptimusThemeResources.getStringArray("wallpapers_signature")) == null || stringArray.length == 0) {
            stringArray = lGOptimusThemeResources.getStringArray("wallpapers");
        }
        if (stringArray == null) {
            return;
        }
        for (String str2 : stringArray) {
            int identifier = lGOptimusThemeResources.getIdentifier(str2 + "_small", LauncherConst.RESOURCE_IMAGE_TYPE);
            int identifier2 = lGOptimusThemeResources.getIdentifier(str2, LauncherConst.RESOURCE_IMAGE_TYPE);
            if (identifier == 0) {
                return;
            }
            this.mList.add(new LGWallpaperItem(null, identifier, identifier2, "com.lge.launcher2.theme.optimus"));
        }
    }

    public int getwallpaperSize() {
        return this.mList.size();
    }

    public LGWallpaperItem getwallpaperList(int i) {
        return this.mList.get(i);
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0031, code lost:
    
        if (r6 == null) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0033, code lost:
    
        if (r7 == null) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0035, code lost:
    
        r2.put(r6, r7);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.util.LinkedHashMap<java.lang.String, java.lang.String> parseXml(android.content.Context r12) {
        /*
            java.lang.String r0 = "LGWallpaperManagerUtil"
            android.content.res.Resources r1 = r12.getResources()
            r2 = 2132017161(0x7f140009, float:1.9672593E38)
            android.content.res.XmlResourceParser r1 = r1.getXml(r2)
            java.util.LinkedHashMap r2 = new java.util.LinkedHashMap
            r2.<init>()
            r3 = 0
            r4 = 0
        L14:
            r5 = r3
            r6 = r4
            r7 = r6
        L17:
            int r8 = r1.next()     // Catch: java.io.IOException -> L61 org.xmlpull.v1.XmlPullParserException -> L68
            r9 = 1
            if (r8 == r9) goto L6e
            r10 = 2
            java.lang.String r11 = "item"
            if (r8 == r10) goto L39
            r9 = 3
            if (r8 == r9) goto L27
            goto L17
        L27:
            java.lang.String r8 = r1.getName()     // Catch: java.io.IOException -> L61 org.xmlpull.v1.XmlPullParserException -> L68
            boolean r8 = r11.equals(r8)     // Catch: java.io.IOException -> L61 org.xmlpull.v1.XmlPullParserException -> L68
            if (r8 == 0) goto L17
            if (r6 == 0) goto L14
            if (r7 == 0) goto L14
            r2.put(r6, r7)     // Catch: java.io.IOException -> L61 org.xmlpull.v1.XmlPullParserException -> L68
            goto L14
        L39:
            java.lang.String r8 = r1.getName()     // Catch: java.io.IOException -> L61 org.xmlpull.v1.XmlPullParserException -> L68
            boolean r10 = r11.equals(r8)     // Catch: java.io.IOException -> L61 org.xmlpull.v1.XmlPullParserException -> L68
            if (r10 == 0) goto L44
            r5 = r9
        L44:
            if (r5 == 0) goto L17
            java.lang.String r9 = "networkcode"
            boolean r9 = r9.equals(r8)     // Catch: java.io.IOException -> L61 org.xmlpull.v1.XmlPullParserException -> L68
            if (r9 == 0) goto L53
            java.lang.String r6 = r1.getAttributeValue(r3)     // Catch: java.io.IOException -> L61 org.xmlpull.v1.XmlPullParserException -> L68
            goto L17
        L53:
            java.lang.String r9 = "wallpapername"
            boolean r8 = r9.equals(r8)     // Catch: java.io.IOException -> L61 org.xmlpull.v1.XmlPullParserException -> L68
            if (r8 == 0) goto L17
            java.lang.String r7 = r1.getAttributeValue(r3)     // Catch: java.io.IOException -> L61 org.xmlpull.v1.XmlPullParserException -> L68
            goto L17
        L61:
            r1 = move-exception
            java.lang.String r3 = "Got exception I/O parsing in carrier wallpaper :"
            com.lge.launcher3.util.LGLog.e(r0, r3, r1)
            goto L6e
        L68:
            r1 = move-exception
            java.lang.String r3 = "Got exception parsing in carrier wallpaper :"
            com.lge.launcher3.util.LGLog.e(r0, r3, r1)
        L6e:
            int r0 = r2.size()
            if (r0 > 0) goto L78
            java.util.LinkedHashMap r2 = parseXmlOnlyItem(r12)
        L78:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.wallpaperpicker.utils.LGWallpaperManagerUtil.parseXml(android.content.Context):java.util.LinkedHashMap");
    }

    private static LinkedHashMap<String, String> parseXmlOnlyItem(Context context) {
        XmlResourceParser xml = context.getResources().getXml(R.xml.carrier_wallpapers);
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        String attributeValue = null;
        String attributeValue2 = null;
        while (true) {
            try {
                int next = xml.next();
                if (next == 1) {
                    break;
                }
                if (next == 2) {
                    String name = xml.getName();
                    if (NETWORK_CODE_TAG.equals(name)) {
                        attributeValue = xml.getAttributeValue(0);
                    } else if (WALLPAPER_NAME_TAG.equals(name)) {
                        attributeValue2 = xml.getAttributeValue(0);
                    }
                }
            } catch (IOException e) {
                LGLog.e(TAG, "Got exception I/O parsing in carrier wallpaper :", e);
            } catch (XmlPullParserException e2) {
                LGLog.e(TAG, "Got exception parsing in carrier wallpaper :", e2);
            }
        }
        if (attributeValue == null || attributeValue2 == null) {
            return null;
        }
        linkedHashMap.put(attributeValue, attributeValue2);
        return linkedHashMap;
    }

    private void LGWallpaperCarrierCodeResolver() {
        Context context = this.mContext;
        if (context == null || context.getResources() == null) {
            return;
        }
        this.mOperatorWallpaperTable = parseXml(this.mContext);
    }

    public static Future<LGWallpaperManagerUtil> initWallpaperManager(final Context context) {
        ExecutorService executorServiceNewSingleThreadExecutor = Executors.newSingleThreadExecutor();
        Future<LGWallpaperManagerUtil> futureSubmit = executorServiceNewSingleThreadExecutor.submit(new Callable<LGWallpaperManagerUtil>() { // from class: com.lge.launcher3.wallpaperpicker.utils.LGWallpaperManagerUtil.1
            /* JADX DEBUG: Method merged with bridge method: call()Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public LGWallpaperManagerUtil call() throws Exception {
                return new LGWallpaperManagerUtil(context);
            }
        });
        try {
            executorServiceNewSingleThreadExecutor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return futureSubmit;
    }

    public static boolean isPackageUninstalled(Context context, String packageName) {
        if (packageName == null || context == null) {
            LGLog.e(TAG, "Invalid Parameters");
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName, 128);
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return true;
        } catch (Exception e) {
            LGLog.e(TAG, "isPackageUninstalled exception: " + e);
            return false;
        }
    }

    public static boolean isCalColorForPreviousModel(Context context) {
        if (context == null) {
            return false;
        }
        int i = sCalColorForPreviousModel;
        if (i != -1) {
            return i == 1;
        }
        String str = SystemProperties.get("ro.product.device");
        int i2 = ((str == null || !"winglm".equals(str)) ? 0 : 1) ^ 1;
        sCalColorForPreviousModel = i2;
        LGLog.d(TAG, "sCalColorForPreviousModel : " + i2);
        return sCalColorForPreviousModel != 0;
    }
}
