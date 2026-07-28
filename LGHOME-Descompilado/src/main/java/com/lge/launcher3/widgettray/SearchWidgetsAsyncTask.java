package com.lge.launcher3.widgettray;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import com.lge.launcher3.allapps.AllAppsSearchUtil;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class SearchWidgetsAsyncTask extends AsyncTask<String, Void, Boolean> {
    private PackageManager mPackageManager;
    ISearchTaskPostExcute mSearchTaskListener;
    private ArrayList<Object> mWidgets;
    private final ArrayList<LGSearchedWidgetsInfo> searchedWidgets = new ArrayList<>();
    private final Collator sCollator = Collator.getInstance();
    private String mSearchWord = "";

    public interface ISearchTaskPostExcute {
        void postExcute(ArrayList<LGSearchedWidgetsInfo> searchedWidgets, Boolean keyValue);
    }

    @Override // android.os.AsyncTask
    protected void onPreExecute() {
    }

    public SearchWidgetsAsyncTask(Context context, ArrayList<Object> widgets, ISearchTaskPostExcute searchTaskListener) {
        this.mWidgets = null;
        this.mPackageManager = null;
        this.mWidgets = new ArrayList<>(widgets);
        this.mPackageManager = context.getPackageManager();
        this.mSearchTaskListener = searchTaskListener;
    }

    /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Boolean doInBackground(String... args) {
        String str = args[0];
        if (str == null) {
            return false;
        }
        this.mSearchWord = str;
        boolean z = str.length() > 0;
        if (z) {
            for (Object obj : this.mWidgets) {
                if (obj instanceof AppWidgetProviderInfo) {
                    AppWidgetProviderInfo appWidgetProviderInfo = (AppWidgetProviderInfo) obj;
                    String str2 = appWidgetProviderInfo.label;
                    int searchedKeyIndex = AllAppsSearchUtil.getSearchedKeyIndex(str2, str);
                    if (searchedKeyIndex != -1) {
                        LGSearchedWidgetsInfo lGSearchedWidgetsInfo = new LGSearchedWidgetsInfo();
                        lGSearchedWidgetsInfo.title = str2;
                        lGSearchedWidgetsInfo.appWidgetProvideInfo = appWidgetProviderInfo;
                        addSearchedWidget(lGSearchedWidgetsInfo, searchedKeyIndex, str);
                    }
                } else if (obj instanceof ResolveInfo) {
                    ResolveInfo resolveInfo = (ResolveInfo) obj;
                    String string = resolveInfo.loadLabel(this.mPackageManager).toString();
                    int searchedKeyIndex2 = AllAppsSearchUtil.getSearchedKeyIndex(string, str);
                    if (searchedKeyIndex2 != -1) {
                        LGSearchedWidgetsInfo lGSearchedWidgetsInfo2 = new LGSearchedWidgetsInfo();
                        lGSearchedWidgetsInfo2.resolveInfo = resolveInfo;
                        lGSearchedWidgetsInfo2.title = string;
                        addSearchedWidget(lGSearchedWidgetsInfo2, searchedKeyIndex2, str);
                    }
                }
            }
            Collections.sort(this.searchedWidgets, new Comparator<LGSearchedWidgetsInfo>() { // from class: com.lge.launcher3.widgettray.SearchWidgetsAsyncTask.1
                /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
                @Override // java.util.Comparator
                public int compare(LGSearchedWidgetsInfo a, LGSearchedWidgetsInfo b) {
                    if (a == null || a.title == null || b == null || b.title == null) {
                        return 0;
                    }
                    String str3 = a.title;
                    String str4 = b.title;
                    if (str3 == null || str4 == null) {
                        return 0;
                    }
                    return SearchWidgetsAsyncTask.this.sCollator.compare(str3, str4);
                }
            });
        }
        return Boolean.valueOf(z);
    }

    /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Boolean keyvalue) {
        ISearchTaskPostExcute iSearchTaskPostExcute = this.mSearchTaskListener;
        if (iSearchTaskPostExcute != null) {
            iSearchTaskPostExcute.postExcute(this.searchedWidgets, keyvalue);
        }
    }

    public ArrayList<LGSearchedWidgetsInfo> getSearchedWidgets() {
        return this.searchedWidgets;
    }

    private void addSearchedWidget(LGSearchedWidgetsInfo info, int index, String key) {
        String str = info.title;
        info.searchPrefix = index > 0 ? str.substring(0, index) : "";
        info.searchBody = str.substring(index, key.length() + index);
        info.searchPostfix = key.length() + index < str.length() ? str.substring(index + key.length(), str.length()) : "";
        this.searchedWidgets.add(info);
    }

    public String getSearchWord() {
        return this.mSearchWord;
    }

    public void setSearchWord(String key) {
        this.mSearchWord = key;
    }
}
