package com.lge.launcher3.silentota;

import android.content.Context;
import android.content.res.Resources;
import com.lge.launcher3.util.LGLog;
import java.io.IOException;
import java.util.HashSet;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class SilentOTASwivel {
    private static String TAG = "SilentOTASwivel";
    private static HashSet<String> sSilentSwivelPackages = new HashSet<>();

    public SilentOTASwivel(Context context) {
        sSilentSwivelPackages = makeSilentSwivelPackage(context);
    }

    public static HashSet<String> getSilentSwivelPackages() {
        return sSilentSwivelPackages;
    }

    public HashSet<String> makeSilentSwivelPackage(Context context) {
        LGLog.i(TAG, "makeSilentSwivelPackage");
        Resources resources = context.getResources();
        SwivelSilentParser swivelSilentParser = new SwivelSilentParser(context, resources, resources.getIdentifier("default_workspace_items_swivel", "xml", context.getPackageName()), "favorites");
        sSilentSwivelPackages.clear();
        try {
            sSilentSwivelPackages = swivelSilentParser.parseLayout(context);
        } catch (IOException e) {
            LGLog.e(TAG, "IOException on makeSilentSwivelPackage : " + e);
        } catch (XmlPullParserException e2) {
            LGLog.e(TAG, "XmlPullParserException on makeSilentSwivelPackage : " + e2);
        }
        return sSilentSwivelPackages;
    }

    public static void updateSilentSwivelPackage(String packageName) {
        LGLog.i(TAG, packageName + " is installed, so remove it from the silent swivel list");
        sSilentSwivelPackages.remove(packageName);
    }
}
