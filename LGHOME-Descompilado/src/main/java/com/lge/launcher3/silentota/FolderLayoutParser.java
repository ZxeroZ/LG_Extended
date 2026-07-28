package com.lge.launcher3.silentota;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import com.android.launcher3.DefaultLayoutParser;
import com.lge.launcher3.config.LauncherConst;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class FolderLayoutParser extends DefaultLayoutParser {
    private Context mContext;
    private HashMap<ComponentName, SilentAppInfo> mHashMap;

    public FolderLayoutParser(Context context, Resources sourceRes, int layoutId) {
        super(context, null, null, sourceRes, layoutId);
        this.mHashMap = new HashMap<>();
        this.mContext = context;
    }

    public int parseLayout() throws XmlPullParserException, IOException {
        XmlResourceParser xml = this.mSourceRes.getXml(this.mLayoutId);
        beginDocument(xml, this.mRootTag);
        int depth = xml.getDepth();
        int folder = 0;
        while (true) {
            int next = xml.next();
            if ((next == 3 && xml.getDepth() <= depth) || next == 1) {
                break;
            }
            if (next == 2) {
                if ("folder".equals(xml.getName())) {
                    String string = null;
                    int attributeResourceValue = getAttributeResourceValue(xml, "title", 0);
                    if (attributeResourceValue != 0) {
                        string = this.mSourceRes.getString(attributeResourceValue);
                    } else {
                        int identifier = this.mSourceRes.getIdentifier(getAttributeValue(xml, "title"), "string", this.mContext.getPackageName());
                        if (identifier != 0) {
                            string = this.mSourceRes.getString(identifier);
                        }
                    }
                    folder += parseFolder(xml, string);
                } else if ("favorite".equals(xml.getName())) {
                    String attributeValue = getAttributeValue(xml, LauncherConst.EXTRA_CLASS_NAME);
                    String attributeValue2 = getAttributeValue(xml, LauncherConst.EXTRA_PACKAGE_NAME);
                    this.mHashMap.put(new ComponentName(attributeValue2, attributeValue), new SilentAppInfo(attributeValue, attributeValue2, getAttributeValue(xml, "screen"), getAttributeValue(xml, "x"), getAttributeValue(xml, "y")));
                }
            }
        }
        return folder;
    }

    private int parseFolder(XmlResourceParser parser, String folderTitle) throws XmlPullParserException, IOException {
        int depth = parser.getDepth();
        int i = 0;
        while (true) {
            int next = parser.next();
            if (next == 3 && parser.getDepth() <= depth) {
                return i;
            }
            if (next == 2 && "favorite".equals(parser.getName()) && folderTitle != null) {
                String attributeValue = getAttributeValue(parser, LauncherConst.EXTRA_PACKAGE_NAME);
                String attributeValue2 = getAttributeValue(parser, LauncherConst.EXTRA_CLASS_NAME);
                this.mHashMap.put(new ComponentName(attributeValue, attributeValue2), new SilentAppInfo(attributeValue2, attributeValue, folderTitle));
                i++;
            }
        }
    }

    public String getFolderName(ComponentName componentName) {
        SilentAppInfo silentAppInfo = this.mHashMap.get(componentName);
        if (silentAppInfo != null) {
            return silentAppInfo.getFolderTitle();
        }
        return null;
    }

    public SilentAppInfo getFavoriteInfo(ComponentName componentName) {
        return this.mHashMap.get(componentName);
    }
}
