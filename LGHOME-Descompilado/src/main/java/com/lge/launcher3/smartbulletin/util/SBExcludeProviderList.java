package com.lge.launcher3.smartbulletin.util;

import android.content.Context;
import android.content.res.XmlResourceParser;
import com.lge.launcher3.R;
import com.lge.launcher3.smartbulletin.info.SBExcludeProviderInfo;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class SBExcludeProviderList {
    private static final String CLASS_NAME_TAG = "class_name";
    private static final String PACKAGE_NAME_TAG = "package_name";
    private static final String PROVIDER_TAG = "provider";

    public static ArrayList<SBExcludeProviderInfo> loadExcludeProviderListFromXml(Context context) {
        int next;
        ArrayList<SBExcludeProviderInfo> arrayList = new ArrayList<>();
        try {
            try {
                try {
                    XmlResourceParser xml = context.getResources().getXml(R.xml.smartbulletin_exclude_provider_list);
                    do {
                        next = xml.next();
                        if (next == 2 && PROVIDER_TAG.equals(xml.getName())) {
                            SBExcludeProviderInfo sBExcludeProviderInfo = new SBExcludeProviderInfo();
                            arrayList.add(sBExcludeProviderInfo);
                            while (true) {
                                next = parseExcludeProvider(xml, next, sBExcludeProviderInfo);
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

    private static int parseExcludeProvider(final XmlPullParser parser, int type, SBExcludeProviderInfo provider) throws XmlPullParserException, IOException {
        if (type == 2) {
            if ("package_name".equals(parser.getName())) {
                if (parser.next() == 4) {
                    provider.mPakageName = parser.getText();
                }
            } else if (CLASS_NAME_TAG.equals(parser.getName()) && parser.next() == 4) {
                provider.mClassName = parser.getText();
            }
        }
        return parser.next();
    }
}
