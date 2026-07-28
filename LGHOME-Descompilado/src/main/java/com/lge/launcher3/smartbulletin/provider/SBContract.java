package com.lge.launcher3.smartbulletin.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindowAllocationException;
import android.net.Uri;
import com.lge.launcher3.smartbulletin.info.SBAppWidgetProviderInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public final class SBContract {
    public static final String AUTHORITY = "com.lge.launcher3.smartbulletin";

    private SBContract() {
    }

    public static final class SmartBulletin {
        public static final String APPWIDGET_ID = "appWidgetId";
        public static final String COMPONENT_NAME = "componentName";
        public static final String IS_ENABLED = "isEnabled";
        public static final String NOTI_ICONRES = "res";
        public static final String NOTI_TABLE = "smartbulletin_noti";
        public static final String NOTI_TIME = "time";
        public static final String NOTI_TYPE = "type";
        public static final String POSITION_X = "positionX";
        public static final String POSITION_Y = "positionY";
        public static final String SPAN_X = "spanX";
        public static final String SPAN_Y = "spanY";
        public static final String TABLE_NAME = "smartbulletin";
        public static final String _ID = "_id";
        public static final Uri CONTENT_URI = Uri.parse("content://com.lge.launcher3.smartbulletin/smartbulletin");
        public static final Uri NOTI_URI = Uri.parse("content://com.lge.launcher3.smartbulletin/smartbulletin_noti");

        private SmartBulletin() {
        }

        private static final boolean insert(ContentResolver cr, String componentName, int positionX, int positionY, int spanX, int spanY, int appWidgetId, boolean isEnabled) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("componentName", componentName);
            contentValues.put(POSITION_X, Integer.valueOf(positionX));
            contentValues.put(POSITION_Y, Integer.valueOf(positionY));
            contentValues.put("spanX", Integer.valueOf(spanX));
            contentValues.put("spanY", Integer.valueOf(spanY));
            contentValues.put("appWidgetId", Integer.valueOf(appWidgetId));
            contentValues.put(IS_ENABLED, Integer.valueOf(isEnabled ? 1 : 0));
            cr.insert(CONTENT_URI, contentValues);
            return true;
        }

        public static final int deleteById(Context context, int id) {
            try {
                return context.getContentResolver().delete(CONTENT_URI, "_id=\"" + id + "\"", null);
            } catch (CursorWindowAllocationException e) {
                e.printStackTrace();
                return -1;
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                return -1;
            } catch (SecurityException e3) {
                e3.printStackTrace();
                return -1;
            }
        }

        public static ArrayList<SBAppWidgetProviderInfo> getAllProvider(Context context) {
            Cursor cursorQuery;
            ContentResolver contentResolver = context.getContentResolver();
            ArrayList<SBAppWidgetProviderInfo> arrayList = new ArrayList<>();
            arrayList.clear();
            try {
                cursorQuery = contentResolver.query(CONTENT_URI, null, null, null, null);
            } catch (CursorWindowAllocationException e) {
                e.printStackTrace();
                cursorQuery = null;
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                cursorQuery = null;
            } catch (SecurityException e3) {
                e3.printStackTrace();
                cursorQuery = null;
            }
            return cursorQuery != null ? getAllProviderFromCursor(context, cursorQuery) : arrayList;
        }

        private static ArrayList<SBAppWidgetProviderInfo> getAllProviderFromCursor(Context context, Cursor c) {
            Cursor cursor = c;
            ArrayList<SBAppWidgetProviderInfo> arrayList = new ArrayList<>();
            arrayList.clear();
            try {
                int columnIndexOrThrow = cursor.getColumnIndexOrThrow("_id");
                int columnIndexOrThrow2 = cursor.getColumnIndexOrThrow("componentName");
                int columnIndexOrThrow3 = cursor.getColumnIndexOrThrow(POSITION_X);
                int columnIndexOrThrow4 = cursor.getColumnIndexOrThrow(POSITION_Y);
                int columnIndexOrThrow5 = cursor.getColumnIndexOrThrow("spanX");
                int columnIndexOrThrow6 = cursor.getColumnIndexOrThrow("spanY");
                int columnIndexOrThrow7 = cursor.getColumnIndexOrThrow("appWidgetId");
                int columnIndexOrThrow8 = cursor.getColumnIndexOrThrow(IS_ENABLED);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                while (c.moveToNext()) {
                    int i = cursor.getInt(columnIndexOrThrow);
                    String string = cursor.getString(columnIndexOrThrow2);
                    int i2 = cursor.getInt(columnIndexOrThrow3);
                    int i3 = cursor.getInt(columnIndexOrThrow4);
                    int i4 = cursor.getInt(columnIndexOrThrow5);
                    int i5 = columnIndexOrThrow;
                    int i6 = cursor.getInt(columnIndexOrThrow6);
                    int i7 = columnIndexOrThrow2;
                    int i8 = cursor.getInt(columnIndexOrThrow7);
                    boolean z = cursor.getInt(columnIndexOrThrow8) > 0;
                    int i9 = columnIndexOrThrow3;
                    int i10 = columnIndexOrThrow4;
                    SBAppWidgetProviderInfo sBAppWidgetProviderInfo = new SBAppWidgetProviderInfo(appWidgetManager.getAppWidgetInfo(i8));
                    sBAppWidgetProviderInfo.mDatabaseId = i;
                    sBAppWidgetProviderInfo.mCompoentName = ComponentName.unflattenFromString(string);
                    sBAppWidgetProviderInfo.mPositionX = i2;
                    sBAppWidgetProviderInfo.mPostionY = i3;
                    sBAppWidgetProviderInfo.mSpanX = i4;
                    sBAppWidgetProviderInfo.mSpanY = i6;
                    sBAppWidgetProviderInfo.mWidgetId = i8;
                    sBAppWidgetProviderInfo.mIsEnabled = z;
                    if (sBAppWidgetProviderInfo.mAppWidgetProviderInfo != null) {
                        sBAppWidgetProviderInfo.mAppWidgetProviderInfo.loadLabel(context.getPackageManager());
                        arrayList.add(sBAppWidgetProviderInfo);
                    }
                    cursor = c;
                    columnIndexOrThrow = i5;
                    columnIndexOrThrow2 = i7;
                    columnIndexOrThrow3 = i9;
                    columnIndexOrThrow4 = i10;
                }
                c.close();
                Collections.sort(arrayList);
                return arrayList;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                c.close();
                return arrayList;
            } catch (SecurityException e2) {
                e2.printStackTrace();
                c.close();
                return arrayList;
            }
        }

        public static void insertDatabase(Context context, AppWidgetProviderInfo appWidgetProviderInfo, int appWidgetId, boolean isEnabled, int positionY) {
            if (positionY == -1) {
                positionY = getEnabledLastPositionY(context) + 1;
            }
            insert(context.getContentResolver(), appWidgetProviderInfo.provider.flattenToString(), 0, positionY, 0, 0, appWidgetId, isEnabled);
        }

        private static int updateById(Context context, int id, String isEnabled, int i, String positionY, int lastPositionY) {
            ContentResolver contentResolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(isEnabled, Integer.valueOf(i));
            contentValues.put(positionY, Integer.valueOf(lastPositionY));
            try {
                return contentResolver.update(CONTENT_URI, contentValues, "_id=\"" + id + "\"", null);
            } catch (CursorWindowAllocationException e) {
                e.printStackTrace();
                return -1;
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                return -1;
            } catch (SecurityException e3) {
                e3.printStackTrace();
                return -1;
            }
        }

        private static final int updateById(Context context, int id, String key, int value) {
            ContentResolver contentResolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(key, Integer.valueOf(value));
            try {
                return contentResolver.update(CONTENT_URI, contentValues, "_id=\"" + id + "\"", null);
            } catch (CursorWindowAllocationException e) {
                e.printStackTrace();
                return -1;
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                return -1;
            } catch (SecurityException e3) {
                e3.printStackTrace();
                return -1;
            }
        }

        private static int getEnabledLastPositionY(Context context) {
            Cursor cursorQuery;
            try {
                cursorQuery = context.getContentResolver().query(CONTENT_URI, new String[]{POSITION_Y, IS_ENABLED}, null, null, null);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                cursorQuery = null;
            } catch (SecurityException e2) {
                e2.printStackTrace();
                cursorQuery = null;
            }
            if (cursorQuery != null) {
                try {
                    int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow(POSITION_Y);
                    int columnIndexOrThrow2 = cursorQuery.getColumnIndexOrThrow(IS_ENABLED);
                    int i = 0;
                    while (cursorQuery.moveToNext()) {
                        int i2 = cursorQuery.getInt(columnIndexOrThrow);
                        boolean z = cursorQuery.getInt(columnIndexOrThrow2) > 0;
                        if (i < i2 && z) {
                            i = i2;
                        }
                    }
                    cursorQuery.close();
                    return i;
                } catch (IllegalArgumentException e3) {
                    e3.printStackTrace();
                    cursorQuery.close();
                } catch (SecurityException e4) {
                    e4.printStackTrace();
                    cursorQuery.close();
                    return 0;
                }
            }
            return 0;
        }

        private static int getDisabledFirstPositionY(Context context) {
            Cursor cursorQuery;
            try {
                cursorQuery = context.getContentResolver().query(CONTENT_URI, new String[]{POSITION_Y, IS_ENABLED}, null, null, null);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                cursorQuery = null;
            } catch (SecurityException e2) {
                e2.printStackTrace();
                cursorQuery = null;
            }
            int i = 1000;
            if (cursorQuery != null) {
                try {
                    int columnIndexOrThrow = cursorQuery.getColumnIndexOrThrow(POSITION_Y);
                    int columnIndexOrThrow2 = cursorQuery.getColumnIndexOrThrow(IS_ENABLED);
                    while (cursorQuery.moveToNext()) {
                        int i2 = cursorQuery.getInt(columnIndexOrThrow);
                        boolean z = cursorQuery.getInt(columnIndexOrThrow2) > 0;
                        if (i > i2 && !z) {
                            i = i2;
                        }
                    }
                    cursorQuery.close();
                    return i;
                } catch (IllegalArgumentException e3) {
                    e3.printStackTrace();
                    cursorQuery.close();
                } catch (SecurityException e4) {
                    e4.printStackTrace();
                    cursorQuery.close();
                    return 1000;
                }
            }
            return 1000;
        }

        public static void updateByInfo(Context context, SBAppWidgetProviderInfo info) {
            updateById(context, info.mDatabaseId, IS_ENABLED, info.mIsEnabled ? 1 : 0, POSITION_Y, info.mPostionY);
        }

        public static void removeInvalidProviders(Context context) {
            Cursor cursorQuery;
            ContentResolver contentResolver = context.getContentResolver();
            new ArrayList().clear();
            try {
                cursorQuery = contentResolver.query(CONTENT_URI, new String[]{"appWidgetId"}, null, null, null);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                cursorQuery = null;
            } catch (NullPointerException e2) {
                e2.printStackTrace();
                cursorQuery = null;
            } catch (SecurityException e3) {
                e3.printStackTrace();
                cursorQuery = null;
            }
            if (cursorQuery != null) {
                Iterator<Integer> it = getInvalidProviders(context, cursorQuery).iterator();
                while (it.hasNext()) {
                    deleteByWidgetId(context, it.next().intValue());
                }
            }
        }

        private static ArrayList<Integer> getInvalidProviders(Context context, Cursor c) {
            ArrayList<Integer> arrayList = new ArrayList<>();
            arrayList.clear();
            try {
                int columnIndexOrThrow = c.getColumnIndexOrThrow("appWidgetId");
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                while (c.moveToNext()) {
                    int i = c.getInt(columnIndexOrThrow);
                    AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(i);
                    if (appWidgetInfo == null || (appWidgetInfo != null && appWidgetInfo.widgetCategory != 256)) {
                        arrayList.add(Integer.valueOf(i));
                    }
                }
                c.close();
                return arrayList;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                c.close();
                return arrayList;
            } catch (SecurityException e2) {
                e2.printStackTrace();
                c.close();
                return arrayList;
            }
        }

        private static final int deleteByWidgetId(Context context, int id) {
            try {
                return context.getContentResolver().delete(CONTENT_URI, "appWidgetId=\"" + id + "\"", null);
            } catch (CursorWindowAllocationException e) {
                e.printStackTrace();
                return -1;
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                return -1;
            } catch (SecurityException e3) {
                e3.printStackTrace();
                return -1;
            }
        }
    }
}
