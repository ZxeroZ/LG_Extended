package com.lge.launcher3.smartbulletin.util;

import android.content.Context;
import android.content.res.XmlResourceParser;
import com.lge.launcher3.R;
import com.lge.launcher3.smartbulletin.info.SBDefaultProviderInfo;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class SBDefaultProviderList {
    private static final String CLASS_NAME_TAG = "class_name";
    private static final String ENABLE_TAG = "enable";
    private static final String PACKAGE_NAME_TAG = "package_name";
    private static final String PROVIDER_TAG = "provider";

    public static ArrayList<SBDefaultProviderInfo> loadProviderListFromXml(Context context) {
        int next;
        ArrayList<SBDefaultProviderInfo> arrayList = new ArrayList<>();
        try {
            try {
                try {
                    XmlResourceParser xml = context.getResources().getXml(R.xml.smartbulletin_default_provider_list);
                    do {
                        next = xml.next();
                        if (next == 2 && PROVIDER_TAG.equals(xml.getName())) {
                            SBDefaultProviderInfo sBDefaultProviderInfo = new SBDefaultProviderInfo();
                            arrayList.add(sBDefaultProviderInfo);
                            while (true) {
                                next = parseProvider(xml, next, sBDefaultProviderInfo);
                                if (next == 3 && PROVIDER_TAG.equals(xml.getName())) {
                                    break;
                                }
                            }
                        }
                    } while (next != 1);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            }
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return arrayList;
    }

    private static int parseProvider(final XmlPullParser parser, int type, SBDefaultProviderInfo provider) throws XmlPullParserException, IOException {
        if (type == 2) {
            if ("package_name".equals(parser.getName())) {
                if (parser.next() == 4) {
                    provider.mPakageName = parser.getText();
                }
            } else if (CLASS_NAME_TAG.equals(parser.getName())) {
                if (parser.next() == 4) {
                    provider.mClassName = parser.getText();
                }
            } else if (ENABLE_TAG.equals(parser.getName()) && parser.next() == 4) {
                if ("true".equals(parser.getText())) {
                    provider.mEnable = true;
                } else {
                    provider.mEnable = false;
                }
            }
        }
        return parser.next();
    }
}
