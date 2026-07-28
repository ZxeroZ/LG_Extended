package com.lge.launcher3.wing;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGLog;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class SwivelWeatherUtils {
    private static final String CP_NAME_ACCU = "accuweather";
    private static final String SELECTION_CURRENT_LOCATION = "currentLocation = 'true'";
    private static final String TAG = "SwivelWeatherUtils";
    private static final int WEATHER_BG_UNAVAILABLE = -1;
    private static final String WEATHER_INTENT_ACTION = "com.lge.sizechangable.weather.platform.action";
    private static final String WEATHER_MAIN_PACKAGE = "com.lge.sizechangable.weather";
    private static final String WEATHER_PLATFORM_PACKAGE = "com.lge.sizechangable.weather.platform";
    private static final String WEATHER_PLATFORM_SERVICE = "com.lge.sizechangable.weather.platform.service.WeatherPlatform";
    private static final String WEATHER_PLATFORM_SERVICE_SEARCHING = "com.lge.sizechangable.weather.platform.service.WeatherPlatform.searching";
    private final int mInvalidOffet = 0;
    private int mOffsetHour = 0;
    private int mOffsetMin = 0;
    private static final Uri WEATHER_PLATFORM_URI = Uri.parse("content://com.lge.sizechangable.weather.platform.provider/WeatherPlatform");
    private static final String COLUMN_NAME_CP_NAME = "CPName";
    private static final String COLUMN_NAME_CITY_INDEX = "cityIndex";
    private static final String COLUMN_NAME_LOCAL_CITY = "cityName";
    private static final String COLUMN_NAME_WEATHERICON = "iconNumberFromService";
    private static final String COLUMN_NAME_WEATHERCPICON = "iconNumberFromCP";
    private static final String COLUMN_NAME_TEMPERATURE = "currentTemp";
    private static final String COLUMN_NAME_UNITTEMP = "unitTemp";
    private static final String COLUMN_NAME_SUNSET = "sunSet";
    private static final String COLUMN_NAME_SUNRISE = "sunRise";
    private static final String COLUMN_NAME_CREATED_TIME = "createdTime";
    private static final String COLUMN_NAME_LOCATION_UPDATED = "isUpdatedbyGeoLocation";
    private static final String COLUMN_NAME_CURRENT_WEATHER_WEBLINK_URL = "currentWeatherWebLinkURL";
    private static final String[] PROJECTION_FOR_WEATHER_INFO = {COLUMN_NAME_CP_NAME, COLUMN_NAME_CITY_INDEX, COLUMN_NAME_LOCAL_CITY, COLUMN_NAME_WEATHERICON, COLUMN_NAME_WEATHERCPICON, COLUMN_NAME_TEMPERATURE, COLUMN_NAME_UNITTEMP, COLUMN_NAME_SUNSET, COLUMN_NAME_SUNRISE, COLUMN_NAME_CREATED_TIME, COLUMN_NAME_LOCATION_UPDATED, COLUMN_NAME_CURRENT_WEATHER_WEBLINK_URL};

    public static int iconToDayNightCode(int IconNo, boolean isDay) {
        return (IconNo == 1 || IconNo == 3) ? isDay ? 1 : 3 : (IconNo == 4 || IconNo == 5) ? isDay ? 4 : 5 : (IconNo == 6 || IconNo == 7) ? isDay ? 7 : 6 : IconNo;
    }

    public static boolean isWeatherInstalled(Context context) {
        if (context == null) {
            LGLog.w(TAG, "isWeatherInstalled : context is null. Return false", new int[0]);
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(WEATHER_MAIN_PACKAGE, 128);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            LGLog.w(TAG, "isWeatherInstalled : weather package is not existing", new int[0]);
            return false;
        }
    }

    public static Cursor queryCurrentLocation(Context context) {
        return context.getContentResolver().query(WEATHER_PLATFORM_URI, PROJECTION_FOR_WEATHER_INFO, SELECTION_CURRENT_LOCATION, null, null);
    }

    public static boolean isCurrentLocationExists(Context context) {
        Cursor cursorQueryCurrentLocation = queryCurrentLocation(context);
        if (cursorQueryCurrentLocation != null) {
            z = cursorQueryCurrentLocation.getCount() > 0;
            cursorQueryCurrentLocation.close();
        }
        LGLog.i(TAG, "isCurrentLocationExists:exists=" + z);
        return z;
    }

    public static SwivelWeatherInformation getWeatherInfo(Context context) {
        return getCurrentWeatherInformation(context);
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:12:0x003e */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:14:0x005b */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:16:0x0061 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:29:0x0008 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:32:0x000e */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:12:0x003e  */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v11 */
    /* JADX WARN: Type inference failed for: r1v13 */
    /* JADX WARN: Type inference failed for: r1v14 */
    /* JADX WARN: Type inference failed for: r1v15 */
    /* JADX WARN: Type inference failed for: r1v2 */
    /* JADX WARN: Type inference failed for: r1v3, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v6, types: [com.lge.launcher3.wing.SwivelWeatherInformation] */
    /* JADX WARN: Type inference failed for: r1v7, types: [com.lge.launcher3.wing.SwivelWeatherInformation] */
    /* JADX WARN: Type inference failed for: r1v8 */
    /* JADX WARN: Type inference failed for: r1v9 */
    /* JADX WARN: Type inference failed for: r5v0 */
    /* JADX WARN: Type inference failed for: r6v1 */
    /* JADX WARN: Type inference failed for: r6v2 */
    /* JADX WARN: Type inference failed for: r6v4 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static com.lge.launcher3.wing.SwivelWeatherInformation getCurrentWeatherInformation(android.content.Context r6) throws java.lang.Throwable {
        /*
            java.lang.String r0 = com.lge.launcher3.wing.SwivelWeatherUtils.TAG
            java.lang.String r1 = "getCurrentWeatherInformation"
            com.lge.launcher3.util.LGLog.i(r0, r1)
            r1 = 0
            android.database.Cursor r6 = queryCurrentLocation(r6)     // Catch: java.lang.Throwable -> L61 java.lang.Exception -> L63
            if (r6 == 0) goto L3e
            boolean r2 = r6.moveToFirst()     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            if (r2 == 0) goto L3e
            com.lge.launcher3.wing.SwivelWeatherInformation r1 = getWeatherInfoFromCursor(r6)     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            java.util.Date r2 = new java.util.Date     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            long r3 = r1.getCreatedTime()     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            r3.<init>()     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            java.lang.String r4 = "getCurrentWeatherInformation:time="
            r3.append(r4)     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            r3.append(r2)     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            java.lang.String r2 = r3.toString()     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            com.lge.launcher3.util.LGLog.i(r0, r2)     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            goto L5b
        L36:
            r0 = move-exception
            r1 = r6
            goto L73
        L39:
            r0 = move-exception
            r5 = r1
            r1 = r6
            r6 = r5
            goto L65
        L3e:
            if (r6 == 0) goto L5b
            int r2 = r6.getCount()     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            r3.<init>()     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            java.lang.String r4 = "getCurrentWeatherInformation:cursor.getCount()="
            r3.append(r4)     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            r3.append(r2)     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            java.lang.String r2 = r3.toString()     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            r3 = 0
            int[] r3 = new int[r3]     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
            com.lge.launcher3.util.LGLog.w(r0, r2, r3)     // Catch: java.lang.Throwable -> L36 java.lang.Exception -> L39
        L5b:
            if (r6 == 0) goto L72
            r6.close()
            goto L72
        L61:
            r0 = move-exception
            goto L73
        L63:
            r0 = move-exception
            r6 = r1
        L65:
            java.lang.String r2 = com.lge.launcher3.wing.SwivelWeatherUtils.TAG     // Catch: java.lang.Throwable -> L61
            java.lang.String r3 = "Exception on getCurrentWeatherInformation : "
            com.lge.launcher3.util.LGLog.e(r2, r3, r0)     // Catch: java.lang.Throwable -> L61
            if (r1 == 0) goto L71
            r1.close()
        L71:
            r1 = r6
        L72:
            return r1
        L73:
            if (r1 == 0) goto L78
            r1.close()
        L78:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.wing.SwivelWeatherUtils.getCurrentWeatherInformation(android.content.Context):com.lge.launcher3.wing.SwivelWeatherInformation");
    }

    public static SwivelWeatherInformation getWeatherInfoFromCursor(Cursor c) {
        String string;
        String string2 = c.getString(c.getColumnIndex(COLUMN_NAME_CP_NAME));
        int i = c.getInt(c.getColumnIndex(COLUMN_NAME_CITY_INDEX));
        String string3 = c.getString(c.getColumnIndex(COLUMN_NAME_LOCAL_CITY));
        int i2 = c.getInt(c.getColumnIndex(COLUMN_NAME_WEATHERICON));
        int i3 = c.getInt(c.getColumnIndex(COLUMN_NAME_WEATHERCPICON));
        String string4 = c.getString(c.getColumnIndex(COLUMN_NAME_UNITTEMP));
        String string5 = c.getString(c.getColumnIndex(COLUMN_NAME_SUNSET));
        String string6 = c.getString(c.getColumnIndex(COLUMN_NAME_SUNRISE));
        long j = c.getLong(c.getColumnIndex(COLUMN_NAME_CREATED_TIME));
        String string7 = c.getString(c.getColumnIndex(COLUMN_NAME_LOCATION_UPDATED));
        String string8 = c.getString(c.getColumnIndex(COLUMN_NAME_CURRENT_WEATHER_WEBLINK_URL));
        LGLog.i(TAG, "getCurrentWeatherFromCursor:locationUpdated=" + string7 + " createdTime=" + j);
        try {
            string = c.getString(c.getColumnIndex(COLUMN_NAME_TEMPERATURE));
        } catch (NumberFormatException unused) {
            LGLog.d(TAG, "fail to get temperature.");
            string = "";
        }
        String str = string;
        LGLog.i(TAG, "weather info CP Name : " + string2 + " / city index : " + i + " / city : " + string3 + " / icon code : " + i2 + " / unit : " + string4 + " / sunset : " + string5 + " / sunrise " + string6 + " / created time : " + j + " / location updated : " + string7 + " currentUrl : " + string8 + " / temperature : " + str);
        return new SwivelWeatherInformation(string2, i, string3, i2, i3, str, string4, string5, string6, j, string7, string8);
    }

    public static String getCurrentWeatherWebLinkUrl(Context context) {
        return isCurrentLocationExists(context) ? getCurrentWeatherInformation(context).getWeatherUrl() : "";
    }

    public static int[] createIconArrayList() {
        return new int[]{R.drawable.ic_weather_widget_fair_day, R.drawable.ic_weather_widget_hot, R.drawable.ic_weather_widget_fair_night, R.drawable.ic_weather_widget_partly_cloudy_day, R.drawable.ic_weather_widget_partly_cloudy_night, R.drawable.ic_weather_widget_foggy, R.drawable.ic_weather_widget_cloudy, R.drawable.ic_weather_widget_windy, R.drawable.ic_weather_widget_rain, R.drawable.ic_weather_widget_cloudy_with_rain_snow, R.drawable.ic_weather_widget_cloudy_with_snow, R.drawable.ic_weather_widget_rain_to_clear, R.drawable.ic_weather_widget_snow_to_clear, R.drawable.ic_weather_widget_snow_rain_to_clear, R.drawable.ic_weather_widget_cloudy_with_snow_mixed, R.drawable.ic_weather_widget_cold, R.drawable.ic_weather_widget_thunder};
    }

    public static void startWeatherApp(Context context, int cityIndex) {
        Intent intent = new Intent();
        intent.setClassName(WEATHER_MAIN_PACKAGE, "com.lge.sizechangable.weather.activities.WeatherActivity_CheckTheme");
        intent.putExtra("appWidgetId", -1100);
        intent.putExtra(COLUMN_NAME_CITY_INDEX, cityIndex);
        intent.putExtra(LauncherConst.EXTRA_PACKAGE_NAME, context.getPackageName());
        intent.setFlags(67108864);
        intent.addFlags(268435456);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            LGLog.w(TAG, "Weather main app is not installed", new int[0]);
        }
    }

    public static int getIconImage(SwivelWeatherInformation weatherInformation) {
        if (weatherInformation == null) {
            return 0;
        }
        return getIconImage(weatherInformation.getWeatherIconCode(), isDay(weatherInformation.getSunRise(), weatherInformation.getSunSet()));
    }

    public static int getIconImage(int icon, boolean isDay) {
        int i = 0;
        try {
            String str = TAG;
            LGLog.d(str, "getIconImage icon : " + icon + " / " + isDay);
            int iIconToDayNightCode = iconToDayNightCode(icon, isDay);
            StringBuilder sb = new StringBuilder();
            sb.append("getIconImage icon no : ");
            sb.append(iIconToDayNightCode);
            LGLog.d(str, sb.toString());
            i = iIconToDayNightCode - 1;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException unused) {
        }
        LGLog.d(TAG, "getIconImage : " + i);
        return i;
    }

    private class WeatherTypeBackground {
        public static final int CLEAR_DAY = 0;
        public static final int CLEAR_NIGHT = 1;
        public static final int CLOUDY_DAY = 2;
        public static final int CLOUDY_NIGHT = 3;
        public static final int RAINY_DAY = 8;
        public static final int RAINY_NIGHT = 9;
        public static final int SNOWY_DAY = 6;
        public static final int SNOWY_NIGHT = 7;
        public static final int THUNDER_DAY = 4;
        public static final int THUNDER_NIGHT = 5;

        private WeatherTypeBackground() {
        }
    }

    public void refresh() {
        this.mOffsetHour = 0;
        this.mOffsetMin = 0;
    }

    public static boolean isDay(String sunRise, String sunSet) {
        int i;
        int i2;
        LGLog.d(TAG, "sunRise : " + sunRise + " , sunSet : " + sunSet);
        int i3 = 7;
        int i4 = 19;
        try {
            if (sunRise.length() >= 5) {
                i3 = Integer.parseInt(sunRise.substring(0, 2));
                i = Integer.parseInt(sunRise.substring(3, 5));
            } else {
                i = 0;
            }
        } catch (NumberFormatException unused) {
            i = 0;
        }
        try {
        } catch (NumberFormatException unused2) {
            LGLog.e(TAG, "NumberFormatExcetion : sunRise = " + sunRise + ", sunSet = " + sunSet);
        }
        if (sunSet.length() >= 5) {
            i4 = Integer.parseInt(sunSet.substring(0, 2));
            i2 = Integer.parseInt(sunSet.substring(3, 5));
        } else {
            i2 = 0;
        }
        int hour = getHour();
        int minute = getMinute();
        if (hour <= i3 || hour >= i4) {
            if (hour < i3 || hour > i4) {
                return false;
            }
            if (hour == i3) {
                if (minute < i) {
                    return false;
                }
            } else if (hour != i4) {
                LGLog.d(TAG, "isDay calculator has a hole..");
            } else if (minute >= i2) {
                return false;
            }
        }
        return true;
    }

    public static int getHour() {
        return GregorianCalendar.getInstance(TimeZone.getDefault()).get(11);
    }

    public static int getMinute() {
        return GregorianCalendar.getInstance(TimeZone.getDefault()).get(12);
    }
}
