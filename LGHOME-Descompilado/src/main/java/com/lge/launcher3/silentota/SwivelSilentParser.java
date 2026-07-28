package com.lge.launcher3.silentota;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import com.android.launcher3.SwivelLayoutParser;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import java.io.IOException;
import java.util.HashSet;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class SwivelSilentParser extends SwivelLayoutParser {
    private static String TAG = "SwivelSilentParser";

    public SwivelSilentParser(Context context, Resources sourceRes, int layoutId, String rootTag) {
        super(context, null, sourceRes, layoutId, rootTag);
    }

    public HashSet<String> parseLayout(Context context) throws XmlPullParserException, IOException {
        String attributeValue;
        HashSet<String> hashSet = new HashSet<>();
        XmlResourceParser xml = this.mSourceRes.getXml(this.mLayoutId);
        beginDocument(xml, this.mRootTag);
        int depth = xml.getDepth();
        while (true) {
            int next = xml.next();
            if ((next == 3 && xml.getDepth() <= depth) || next == 1) {
                break;
            }
            if (next == 2 && "appicon".equals(xml.getName()) && (attributeValue = getAttributeValue(xml, LauncherConst.EXTRA_PACKAGE_NAME)) != null) {
                if (PackageUtils.isPackageInstalled(context, attributeValue)) {
                    LGLog.i(TAG, attributeValue + "is already installed, so ignore it on silent swivel OTA list");
                } else if (isPromisingPackage(attributeValue)) {
                    LGLog.i(TAG, attributeValue + "is in promising apps list, so ignore it on silent swivel OTA list");
                } else {
                    LGLog.i(TAG, attributeValue + "is listed on silent swivel OTA list");
                    hashSet.add(attributeValue);
                }
            }
        }
        return hashSet;
    }

    private boolean isPromisingPackage(String packageName) {
        return packageName != null && SilentOTA_Extension.getAddedPromisingPackage().contains(packageName);
    }
}
