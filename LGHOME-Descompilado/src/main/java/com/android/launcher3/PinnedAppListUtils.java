package com.android.launcher3;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lge.launcher3.R;
import com.lge.launcher3.screeneffect.ScreenEffectSelectionDialog;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGLog;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class PinnedAppListUtils {
    private static final String CLASS_NAME_TAG = "class_name";
    private static final String PACKAGE_NAME_TAG = "package_name";
    private static final String TAG_APP_ICON = "app_icon";
    private static ContextThemeWrapper sThemeContext;

    public static ContextThemeWrapper getThemeContext(Context context) {
        if (sThemeContext == null) {
            sThemeContext = new ContextThemeWrapper(context, 34210242);
        }
        return sThemeContext;
    }

    public static void deleteItem(Context context, AppEntry appEntry) {
        LGLog.i(ScreenEffectSelectionDialog.TAG, "deleteItem - appEntry = " + appEntry);
        if (appEntry != null) {
            LGLog.i(ScreenEffectSelectionDialog.TAG, "deleteItem - appEntry.getLabel() = " + appEntry.getLabel());
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("pinned_apps", 0);
        String string = sharedPreferences.getString("pinned_apps", null);
        List list = (List) new Gson().fromJson(string, new TypeToken<List<String>>() { // from class: com.android.launcher3.PinnedAppListUtils.1
        }.getType());
        LGLog.i(ScreenEffectSelectionDialog.TAG, "deleteItem - SharedPreferences = " + sharedPreferences);
        LGLog.i(ScreenEffectSelectionDialog.TAG, "deleteItem - data = " + string);
        LGLog.i(ScreenEffectSelectionDialog.TAG, "deleteItem - pinnedApps = " + list);
        if (list == null) {
            LGLog.i(ScreenEffectSelectionDialog.TAG, "deleteItem - pinnedApps is null");
            return;
        }
        list.remove(appEntry.getComponentName().flattenToString());
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        sharedPreferences.edit().putString("pinned_apps", new Gson().toJson(list)).apply();
        editorEdit.apply();
    }

    public static View.OnGenericMotionListener getOnGenericMotionListener(final View v, final int position, final ArrayAdapter<AppEntry> adapter) {
        return new View.OnGenericMotionListener() { // from class: com.android.launcher3.-$$Lambda$PinnedAppListUtils$FeihXINirV_2SYECxFnr0hKqluM
            @Override // android.view.View.OnGenericMotionListener
            public final boolean onGenericMotion(View view, MotionEvent motionEvent) {
                return PinnedAppListUtils.lambda$getOnGenericMotionListener$1(adapter, position, v, view, motionEvent);
            }
        };
    }

    static /* synthetic */ boolean lambda$getOnGenericMotionListener$1(final ArrayAdapter arrayAdapter, final int i, final View view, View view2, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 11 || motionEvent.getActionButton() != 2) {
            return false;
        }
        showMenu(view2, arrayAdapter, R.menu.r_click_menu, 48, new PopupMenu.OnMenuItemClickListener() { // from class: com.android.launcher3.-$$Lambda$PinnedAppListUtils$-fuDWH5P80FmeIPfwgxWgaUgGBI
            @Override // android.widget.PopupMenu.OnMenuItemClickListener
            public final boolean onMenuItemClick(MenuItem menuItem) {
                return PinnedAppListUtils.lambda$getOnGenericMotionListener$0(arrayAdapter, i, view, menuItem);
            }
        });
        return true;
    }

    static /* synthetic */ boolean lambda$getOnGenericMotionListener$0(ArrayAdapter arrayAdapter, int i, View view, MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.delete_app_shortcut) {
            return false;
        }
        AppEntry appEntry = (AppEntry) arrayAdapter.getItem(i);
        arrayAdapter.remove(appEntry);
        deleteItem(view.getContext(), appEntry);
        arrayAdapter.notifyDataSetChanged();
        return true;
    }

    public static void showMenu(View v, ArrayAdapter<AppEntry> adapter, int menuId, int gravity, PopupMenu.OnMenuItemClickListener listener) {
        PopupMenu popupMenu;
        if (v == null || adapter == null) {
            return;
        }
        if (gravity != 0) {
            popupMenu = new PopupMenu(getThemeContext(v.getContext()), v, gravity);
        } else {
            popupMenu = new PopupMenu(getThemeContext(v.getContext()), v);
        }
        popupMenu.setOnMenuItemClickListener(listener);
        popupMenu.getMenuInflater().inflate(menuId, popupMenu.getMenu());
        popupMenu.show();
    }

    public static boolean getFirstRunValueFromPreference(Context context) {
        return SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.SecondrayLauncherKey.ALREADY_SHOWN, true);
    }

    public static void saveFirstRunValueFromPreference(Context context, boolean value) {
        SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.SecondrayLauncherKey.ALREADY_SHOWN, value);
    }

    public static void saveInitialLayoutToPreference(Context context, ArrayList<AppEntry> appEntries) {
        LGLog.i(ScreenEffectSelectionDialog.TAG, "saveInitialLayoutToPreference - appEntries = " + appEntries);
        if (appEntries != null) {
            LGLog.i(ScreenEffectSelectionDialog.TAG, "saveInitialLayoutToPreference - appEntries.size() = " + appEntries.size());
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("pinned_apps", 0);
        List arrayList = (List) new Gson().fromJson(sharedPreferences.getString("pinned_apps", null), new TypeToken<List<String>>() { // from class: com.android.launcher3.PinnedAppListUtils.2
        }.getType());
        if (arrayList == null) {
            arrayList = new ArrayList();
        }
        LGLog.i(ScreenEffectSelectionDialog.TAG, "saveInitialLayoutToPreference - pinnedApps.size() = " + arrayList.size());
        for (AppEntry appEntry : appEntries) {
            String strFlattenToString = appEntry.getComponentName().flattenToString();
            LGLog.i(ScreenEffectSelectionDialog.TAG, "saveInitialLayoutToPreference - newEntry = " + strFlattenToString);
            if (arrayList.contains(strFlattenToString)) {
                LGLog.i(ScreenEffectSelectionDialog.TAG, "saveInitialLayoutToPreference - pinnedApps contains " + strFlattenToString);
                return;
            }
            arrayList.add(appEntry.getComponentName().flattenToString());
        }
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        sharedPreferences.edit().putString("pinned_apps", new Gson().toJson(arrayList)).apply();
        editorEdit.apply();
    }

    public static ArrayList<AppEntry> loadSecondaryLayoutFromXML(Context context) {
        int next;
        ArrayList<AppEntry> arrayList = new ArrayList<>();
        try {
            try {
                XmlResourceParser xml = context.getResources().getXml(R.xml.secondary_layout_default);
                do {
                    next = xml.next();
                    if (next == 2 && TAG_APP_ICON.equals(xml.getName())) {
                        SecondaryIconInfo secondaryIconInfo = new SecondaryIconInfo();
                        while (true) {
                            next = parseIconList(xml, next, secondaryIconInfo);
                            if (next == 3 && TAG_APP_ICON.equals(xml.getName())) {
                                break;
                            }
                        }
                        try {
                            context.getPackageManager().getApplicationInfo(secondaryIconInfo.mPakageName, 0);
                            LGLog.i(ScreenEffectSelectionDialog.TAG, "loadSecondaryLayoutFromXML - new AppEntry.  info.mPakageName = " + secondaryIconInfo.mPakageName + ", info.mClassName = " + secondaryIconInfo.mClassName);
                            arrayList.add(new AppEntry(context, secondaryIconInfo.mPakageName, secondaryIconInfo.mClassName));
                        } catch (PackageManager.NameNotFoundException e) {
                            LGLog.w(ScreenEffectSelectionDialog.TAG, String.format("NameNotFoundException(%s)", e.getMessage()), new int[0]);
                        }
                    }
                } while (next != 1);
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            } catch (XmlPullParserException e3) {
                e3.printStackTrace();
            }
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        return arrayList;
    }

    private static int parseIconList(final XmlPullParser parser, int type, SecondaryIconInfo iconInfo) throws XmlPullParserException, IOException {
        if (type == 2) {
            if ("package_name".equals(parser.getName())) {
                if (parser.next() == 4) {
                    iconInfo.mPakageName = parser.getText();
                }
            } else if (CLASS_NAME_TAG.equals(parser.getName()) && parser.next() == 4) {
                iconInfo.mClassName = parser.getText();
            }
        }
        return parser.next();
    }
}
