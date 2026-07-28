package com.lge.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsSearch extends LinearLayout implements SearchView.OnQueryTextListener, View.OnTouchListener, View.OnFocusChangeListener {
    private static final String MENU_SEARCH_KEYWORD = "searchkeyword";
    private static final String MENU_SEARCH_PREFERENCE = "allappssearch.appsearchkeyword";
    private ImageView mExitButton;
    private SearchView mSearchInput;
    private IAllAppsSearchListener mSearchListener;

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent event) {
        return true;
    }

    public AllAppsSearch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        SearchView searchView = (SearchView) findViewById(R.id.all_apps_search_edittext);
        this.mSearchInput = searchView;
        int identifier = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        if (identifier != 0) {
            ((EditText) this.mSearchInput.findViewById(identifier)).setPrivateImeOptions("com.lge.android.editmode.noContent");
        }
        this.mSearchInput.setOnQueryTextListener(this);
        this.mSearchInput.setOnQueryTextFocusChangeListener(this);
        this.mSearchInput.onActionViewExpanded();
        this.mSearchInput.clearFocus();
        this.mSearchInput.setOnKeyListener(new View.OnKeyListener() { // from class: com.lge.launcher3.allapps.AllAppsSearch.1
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 6 || AllAppsSearch.this.mSearchInput.getQuery() == null) {
                    return false;
                }
                AllAppsSearch allAppsSearch = AllAppsSearch.this;
                return allAppsSearch.isAllWhiteSpace(allAppsSearch.mSearchInput.getQuery().toString());
            }
        });
        ImageView imageView = (ImageView) this.mSearchInput.findViewById(34472166);
        this.mExitButton = imageView;
        imageView.setVisibility(0);
        ImageView imageView2 = this.mExitButton;
        if (imageView2 != null) {
            imageView2.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.allapps.AllAppsSearch.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    AllAppsSearch.this.hideAppsSearchBar();
                }
            });
        }
    }

    public void setOnSearchListener(IAllAppsSearchListener listener) {
        this.mSearchListener = listener;
    }

    public void showAppsSearchBar(String keyword) {
        setVisibility(0);
        this.mSearchInput.requestFocus();
        this.mSearchInput.setQuery(keyword, true);
        this.mSearchListener.prepareSearchViewShow();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (super.dispatchKeyEvent(event)) {
            return true;
        }
        View focusedChild = this.mSearchInput.getFocusedChild();
        if (focusedChild == null || event.getAction() != 0 || !(focusedChild instanceof EditText) || event.getKeyCode() != 21) {
            return false;
        }
        this.mExitButton.requestFocus();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAllWhiteSpace(String searchword) {
        if (searchword == null) {
            return false;
        }
        int i = 0;
        for (int i2 = 0; i2 < searchword.length(); i2++) {
            if (searchword.charAt(i2) != ' ') {
                return false;
            }
            i++;
        }
        return i == searchword.length();
    }

    public void hideAppsSearchBar() {
        if (getVisibility() == 0) {
            this.mSearchInput.setQuery("", false);
            this.mSearchInput.clearFocus();
            setVisibility(4);
            this.mSearchListener.prepareSearchViewHide();
        }
    }

    public void saveSearchKeyword() {
        if (getVisibility() == 0) {
            this.mContext.getSharedPreferences(MENU_SEARCH_PREFERENCE, 0).edit().putString(MENU_SEARCH_KEYWORD, this.mSearchInput.getQuery().toString()).commit();
        }
    }

    public String restoreSearchKeyword() {
        return this.mContext.getSharedPreferences(MENU_SEARCH_PREFERENCE, 0).getString(MENU_SEARCH_KEYWORD, "");
    }

    public void requestFocusForSearch() {
        if (getVisibility() == 0) {
            this.mSearchInput.requestFocus();
        }
    }

    public void setFocus(boolean hasFocus) {
        SearchView searchView = this.mSearchInput;
        if (searchView != null) {
            searchView.onWindowFocusChanged(hasFocus);
        }
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextSubmit(String query) {
        clearFocus();
        return false;
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextChange(String newText) {
        if (getVisibility() != 0) {
            return false;
        }
        this.mSearchListener.onSearch(newText);
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void clearFocus() {
        this.mSearchInput.clearFocus();
    }

    public boolean onBackPressed() {
        if (getVisibility() != 0) {
            return false;
        }
        hideAppsSearchBar();
        return true;
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View view, boolean hasFocus) {
        InputMethodManager inputMethodManager;
        if (!hasFocus || (inputMethodManager = (InputMethodManager) this.mContext.getApplicationContext().getSystemService("input_method")) == null) {
            return;
        }
        inputMethodManager.showSoftInput(view.findFocus(), 1);
    }
}
