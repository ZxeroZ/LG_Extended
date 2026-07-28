package com.android.launcher3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.DefaultLayoutParser;
import com.android.launcher3.LauncherSettings;
import com.lge.launcher3.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class CommonAppTypeParser implements AutoInstallsLayout.LayoutParserCallback {
    private static final int RESTORE_FLAG_BIT_SHIFT = 4;
    public static final int SUPPORTED_TYPE_COUNT = 7;
    private static final String TAG = "CommonAppTypeParser";
    final Context mContext;
    private final long mItemId;
    final int mResId;
    Intent parsedIntent;
    String parsedTitle;
    ContentValues parsedValues;

    public static int decodeItemTypeFromFlag(int flag) {
        return (flag & ShortcutInfo.FLAG_RESTORED_APP_TYPE) >> 4;
    }

    public static int encodeItemTypeToFlag(int itemType) {
        return itemType << 4;
    }

    public static int getResourceForItemType(int type) {
        switch (type) {
            case 1:
                return R.xml.app_target_phone;
            case 2:
                return R.xml.app_target_messenger;
            case 3:
                return R.xml.app_target_email;
            case 4:
                return R.xml.app_target_browser;
            case 5:
                return R.xml.app_target_gallery;
            case 6:
                return R.xml.app_target_camera;
            default:
                return 0;
        }
    }

    public CommonAppTypeParser(long itemId, int itemType, Context context) {
        this.mItemId = itemId;
        this.mContext = context;
        this.mResId = getResourceForItemType(itemType);
    }

    @Override // com.android.launcher3.AutoInstallsLayout.LayoutParserCallback
    public long generateNewItemId() {
        return this.mItemId;
    }

    @Override // com.android.launcher3.AutoInstallsLayout.LayoutParserCallback
    public long insertAndCheck(SQLiteDatabase db, ContentValues values) {
        this.parsedValues = values;
        values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, (Integer) null);
        String str = (String) null;
        values.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, str);
        values.put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, str);
        values.put("icon", (byte[]) null);
        return 1L;
    }

    public boolean findDefaultApp() {
        if (this.mResId == 0) {
            return false;
        }
        this.parsedIntent = null;
        this.parsedValues = null;
        new MyLayoutParser().parseValues();
        return (this.parsedValues == null || this.parsedIntent == null) ? false : true;
    }

    private class MyLayoutParser extends DefaultLayoutParser {
        public MyLayoutParser() {
            super(CommonAppTypeParser.this.mContext, null, CommonAppTypeParser.this, CommonAppTypeParser.this.mContext.getResources(), CommonAppTypeParser.this.mResId, "resolve", 0);
        }

        @Override // com.android.launcher3.AutoInstallsLayout
        protected long addShortcut(String title, Intent intent, int type) {
            if (type == 0) {
                CommonAppTypeParser.this.parsedIntent = intent;
                CommonAppTypeParser.this.parsedTitle = title;
            }
            return super.addShortcut(title, intent, type);
        }

        public void parseValues() {
            XmlResourceParser xml = this.mSourceRes.getXml(this.mLayoutId);
            try {
                beginDocument(xml, this.mRootTag);
                new DefaultLayoutParser.ResolveParser().parseAndAdd(xml);
            } catch (IOException | XmlPullParserException e) {
                Log.e(CommonAppTypeParser.TAG, "Unable to parse default app info", e);
            }
            xml.close();
        }
    }
}
