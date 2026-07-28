package com.lge.launcher3;

import android.appwidget.AppWidgetHost;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.DefaultLayoutParser;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Partner;
import com.lge.launcher3.util.LGLog;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* JADX INFO: loaded from: classes.dex */
public class PartnerFileLayoutParser extends DefaultLayoutParser {
    private static final String TAG = "PartnerFileLayoutParser";
    private String mFilePath;
    private String mSourcePackage;
    private final long[] mTemp;

    public PartnerFileLayoutParser(Context context, AppWidgetHost appWidgetHost, AutoInstallsLayout.LayoutParserCallback callback, Partner partner) {
        super(context, appWidgetHost, callback, partner.getResources(), -1);
        this.mTemp = new long[2];
        this.mFilePath = partner.getFilePath();
        this.mSourcePackage = partner.getPackageName();
    }

    @Override // com.android.launcher3.AutoInstallsLayout
    public int loadLayout(SQLiteDatabase db, ArrayList<Long> screenIds) {
        this.mDb = db;
        try {
            return parseLayout(this.mFilePath, screenIds);
        } catch (Exception e) {
            LGLog.w(TAG, "Got exception parsing layout.", e, new int[0]);
            return -1;
        }
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:28:0x0054 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:48:0x0002 */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1 */
    /* JADX WARN: Type inference failed for: r0v13 */
    /* JADX WARN: Type inference failed for: r0v14 */
    /* JADX WARN: Type inference failed for: r0v15 */
    /* JADX WARN: Type inference failed for: r0v16 */
    /* JADX WARN: Type inference failed for: r0v17 */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r0v6 */
    /* JADX WARN: Type inference failed for: r0v7 */
    /* JADX WARN: Type inference failed for: r7v0, types: [com.lge.launcher3.PartnerFileLayoutParser] */
    protected int parseLayout(String str, ArrayList<Long> arrayList) throws Throwable {
        int andAddNode;
        ?? r0 = 0;
        FileInputStream fileInputStream = null;
        try {
            try {
                FileInputStream fileInputStream2 = new FileInputStream(str);
                try {
                    try {
                        XmlPullParser xmlPullParserNewPullParser = XmlPullParserFactory.newInstance().newPullParser();
                        xmlPullParserNewPullParser.setInput(fileInputStream2, null);
                        beginDocument(xmlPullParserNewPullParser, this.mRootTag);
                        int depth = xmlPullParserNewPullParser.getDepth();
                        HashMap<String, AutoInstallsLayout.TagParser> layoutElementsMap = getLayoutElementsMap();
                        andAddNode = 0;
                        while (true) {
                            try {
                                int next = xmlPullParserNewPullParser.next();
                                if ((next == 3 && xmlPullParserNewPullParser.getDepth() <= depth) || next == 1) {
                                    try {
                                        fileInputStream2.close();
                                        r0 = depth;
                                        break;
                                    } catch (IOException e) {
                                        int[] iArr = new int[0];
                                        LGLog.w(TAG, e.toString(), iArr);
                                        r0 = iArr;
                                    }
                                } else if (next == 2) {
                                    andAddNode += parseAndAddNode(xmlPullParserNewPullParser, layoutElementsMap, arrayList);
                                }
                            } catch (IOException e2) {
                                e = e2;
                                fileInputStream = fileInputStream2;
                                LGLog.w(TAG, e.toString(), new int[0]);
                                r0 = fileInputStream;
                                if (fileInputStream != null) {
                                    try {
                                        fileInputStream.close();
                                        r0 = fileInputStream;
                                    } catch (IOException e3) {
                                        int[] iArr2 = new int[0];
                                        LGLog.w(TAG, e3.toString(), iArr2);
                                        r0 = iArr2;
                                    }
                                }
                            }
                        }
                    } catch (Throwable th) {
                        th = th;
                        r0 = fileInputStream2;
                        if (r0 != 0) {
                            try {
                                r0.close();
                            } catch (IOException e4) {
                                LGLog.w(TAG, e4.toString(), new int[0]);
                            }
                        }
                        throw th;
                    }
                } catch (IOException e5) {
                    e = e5;
                    andAddNode = 0;
                }
            } catch (IOException e6) {
                e = e6;
                andAddNode = 0;
            }
            return andAddNode;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    protected int parseAndAddNode(XmlPullParser parser, HashMap<String, AutoInstallsLayout.TagParser> tagParserMap, ArrayList<Long> screenIds) throws XmlPullParserException, IOException {
        this.mValues.clear();
        parseContainerAndScreen(parser, this.mTemp);
        long[] jArr = this.mTemp;
        long j = jArr[0];
        long j2 = jArr[1];
        this.mValues.put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(j));
        this.mValues.put("screen", Long.valueOf(j2));
        this.mValues.put(LauncherSettings.Favorites.CELLX, parser.getAttributeValue(null, "x"));
        this.mValues.put(LauncherSettings.Favorites.CELLY, parser.getAttributeValue(null, "y"));
        AutoInstallsLayout.TagParser tagParser = tagParserMap.get(parser.getName());
        if (tagParser != null) {
            if (tagParser.parseAndAdd(parser, this.mSourcePackage) < 0) {
                return 0;
            }
            if (!screenIds.contains(Long.valueOf(j2)) && j == -100) {
                screenIds.add(Long.valueOf(j2));
            }
            return 1;
        }
        LGLog.d(TAG, "Ignoring unknown element tag: " + parser.getName());
        return 0;
    }

    protected void parseContainerAndScreen(XmlPullParser parser, long[] out) {
        out[0] = -100;
        String attributeValue = parser.getAttributeValue(null, LauncherSettings.Favorites.CONTAINER);
        if (attributeValue != null) {
            out[0] = Long.valueOf(attributeValue).longValue();
        }
        out[1] = Long.parseLong(parser.getAttributeValue(null, "screen"));
    }
}
