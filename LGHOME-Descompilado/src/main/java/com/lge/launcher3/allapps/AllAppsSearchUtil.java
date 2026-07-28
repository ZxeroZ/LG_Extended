package com.lge.launcher3.allapps;

import android.os.AsyncTask;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.AppInfo;
import com.lge.launcher3.util.LGChosungUtils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsSearchUtil {
    private final ISearchCallback mSearchCallback;
    private final ArrayList<AllAppsItemInfo> mSearchedItems = new ArrayList<>();
    private final Collator sCollator = Collator.getInstance();
    private String mSearchWord = "";
    private SearchAppsAsyncTask mSearchAppsAsyncTask = null;
    private boolean mSearchState = false;

    public interface ISearchCallback {
        ArrayList<AllAppsItemInfo> getAllAppsItemInfoList();

        AppInfo getAppInfo(ShortcutInfo shortcutInfo);

        void searchResult(boolean keyvalue);
    }

    public AllAppsSearchUtil(ISearchCallback searchCallback) {
        this.mSearchCallback = searchCallback;
    }

    public void setSearchKeyword(String key) {
        this.mSearchWord = key;
    }

    public boolean searchForKeyWord(String key) {
        boolean z = false;
        if (key == null) {
            return false;
        }
        clearSearchResult();
        if (key.length() > 0) {
            z = true;
            for (AllAppsItemInfo allAppsItemInfo : this.mSearchCallback.getAllAppsItemInfoList()) {
                if (allAppsItemInfo != null) {
                    if (allAppsItemInfo.itemType == 0) {
                        addAppInfoToSearchResult(allAppsItemInfo, key);
                    } else {
                        addFolderInfoToSearchResult(allAppsItemInfo, key);
                    }
                }
            }
            Collections.sort(this.mSearchedItems, new Comparator<AllAppsItemInfo>() { // from class: com.lge.launcher3.allapps.AllAppsSearchUtil.1
                /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
                @Override // java.util.Comparator
                public int compare(AllAppsItemInfo a, AllAppsItemInfo b) {
                    if (a == null || a.title == null || b == null || b.title == null) {
                        return 0;
                    }
                    String string = a.title.toString();
                    String string2 = b.title.toString();
                    if (string == null || string2 == null) {
                        return 0;
                    }
                    return AllAppsSearchUtil.this.sCollator.compare(string, string2);
                }
            });
        }
        return z;
    }

    private void addAppInfoToSearchResult(AllAppsItemInfo info, String key) {
        String string;
        int searchedKeyIndex;
        if (info == null || info.title == null || (searchedKeyIndex = getSearchedKeyIndex((string = info.title.toString()), key)) < 0) {
            return;
        }
        AllAppsItemInfo allAppsItemInfo = new AllAppsItemInfo(info);
        allAppsItemInfo.isSearched = true;
        allAppsItemInfo.searchPrefix = searchedKeyIndex > 0 ? string.substring(0, searchedKeyIndex) : "";
        allAppsItemInfo.searchBody = string.substring(searchedKeyIndex, key.length() + searchedKeyIndex);
        allAppsItemInfo.searchPostfix = key.length() + searchedKeyIndex < string.length() ? string.substring(searchedKeyIndex + key.length(), string.length()) : "";
        allAppsItemInfo.itemView = info.itemView;
        info.itemView.setTag(allAppsItemInfo);
        this.mSearchedItems.add(allAppsItemInfo);
    }

    private void addFolderInfoToSearchResult(AllAppsItemInfo info, String key) {
        String string;
        int searchedKeyIndex;
        AppInfo appInfo;
        AllAppsFolderInfo allAppsFolderInfo = info.mFolderInfo;
        if (allAppsFolderInfo != null) {
            ArrayList arrayList = new ArrayList(allAppsFolderInfo.getContents());
            if (arrayList.size() == 0) {
                return;
            }
            for (int i = 0; i < arrayList.size(); i++) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) arrayList.get(i);
                if (shortcutInfo != null && shortcutInfo.title != null && (searchedKeyIndex = getSearchedKeyIndex((string = shortcutInfo.title.toString()), key)) >= 0 && (appInfo = this.mSearchCallback.getAppInfo(shortcutInfo)) != null) {
                    AllAppsItemInfo allAppsItemInfo = new AllAppsItemInfo(appInfo);
                    allAppsItemInfo.isSearched = true;
                    allAppsItemInfo.searchPrefix = searchedKeyIndex > 0 ? string.substring(0, searchedKeyIndex) : "";
                    allAppsItemInfo.searchBody = string.substring(searchedKeyIndex, key.length() + searchedKeyIndex);
                    allAppsItemInfo.searchPostfix = key.length() + searchedKeyIndex < string.length() ? string.substring(searchedKeyIndex + key.length(), string.length()) : "";
                    this.mSearchedItems.add(allAppsItemInfo);
                }
            }
        }
    }

    public void clearSearchResult() {
        this.mSearchedItems.clear();
    }

    private class SearchAppsAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override // android.os.AsyncTask
        protected void onPreExecute() {
        }

        private SearchAppsAsyncTask() {
        }

        /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(String... args) {
            return Boolean.valueOf(AllAppsSearchUtil.this.searchForKeyWord(args[0]));
        }

        /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean keyvalue) {
            AllAppsSearchUtil.this.mSearchState = keyvalue.booleanValue();
            AllAppsSearchUtil.this.mSearchCallback.searchResult(keyvalue.booleanValue());
        }
    }

    public void onDestroy() {
        SearchAppsAsyncTask searchAppsAsyncTask = this.mSearchAppsAsyncTask;
        if (searchAppsAsyncTask != null) {
            searchAppsAsyncTask.cancel(true);
        }
    }

    public void searchApps(String key) {
        SearchAppsAsyncTask searchAppsAsyncTask = this.mSearchAppsAsyncTask;
        if (searchAppsAsyncTask != null && searchAppsAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mSearchAppsAsyncTask.cancel(true);
        }
        this.mSearchWord = key;
        SearchAppsAsyncTask searchAppsAsyncTask2 = new SearchAppsAsyncTask();
        this.mSearchAppsAsyncTask = searchAppsAsyncTask2;
        searchAppsAsyncTask2.execute(key);
    }

    public boolean isSearchState() {
        return this.mSearchState;
    }

    public boolean searchForKeyWord(ArrayList<AllAppsItemInfo> menuItemInfos) {
        return searchForKeyWord(this.mSearchWord);
    }

    public ArrayList<AllAppsItemInfo> getSearchResult() {
        return this.mSearchedItems;
    }

    public String getSearchWord() {
        return this.mSearchWord;
    }

    public static int getSearchedKeyIndex(String title, String lowerKey) {
        int hangulInitialSound;
        if (title == null) {
            return -1;
        }
        String lowerCase = title.toLowerCase(Locale.getDefault());
        if (lowerCase.contains(lowerKey)) {
            int iIndexOf = lowerCase.indexOf(lowerKey);
            int length = lowerCase.length() - title.length();
            if (length <= 0) {
                return iIndexOf;
            }
            hangulInitialSound = Math.max(iIndexOf - length, 0);
        } else {
            hangulInitialSound = LGChosungUtils.getHangulInitialSound(lowerCase, lowerKey);
            if (hangulInitialSound < 0) {
                return -1;
            }
        }
        return hangulInitialSound;
    }
}
